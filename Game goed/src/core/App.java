package core;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL;

import entity.Player;
import items.ItemManager;
import renderer.*;

import entity.Object;
import map.MapHandler;


import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.joml.Matrix4f;
import java.util.List;

import static org.lwjgl.opengl.GL20.glUseProgram;
import java.util.ArrayList;


import physics.CollisionBox;
import physics.CollisionHandler;

public class App {
    private static long window;
    private static int width = 1980, height = 1060;
    public static Vector3f cameraPos = new Vector3f(0,0,0);
    private static Vector3f cameraDir = new Vector3f(0,-2,-1);

    public static InputHandler inputHandler;
    private static List<Object> worldObjectList = new ArrayList<>();
    private static MapHandler mapHandler;
    private static Vector3f cameraOffset = new Vector3f(0, 5, 1);
    private static Player player;
    
    public static ItemManager itemManager = new ItemManager("res/items/items.json");

    private static void initWindow() {
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        window = glfwCreateWindow(width, height, "Survival Game", 0, 0);
        if (window == 0) throw new RuntimeException("Failed to create window");

        inputHandler = new InputHandler(window);

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        GL.createCapabilities();

        glEnable(GL_DEPTH_TEST);
        glClearColor(0f, 0.3f, 0f, 1f);
    }

    public static void main(String[] args) {
        initWindow();
        run();
    }

    public static void run() {
        
        String vertexShaderCode = loadShaderSource("res/shaders/default.vert");
        String fragmentShaderCode = loadShaderSource("res/shaders/default.frag");
        Shader shader = new Shader(vertexShaderCode, fragmentShaderCode);
        
        // String lineVert = loadShaderSource("res/shaders/line.vert");
        // String lineFrag = loadShaderSource("res/shaders/line.frag");
        // Shader lineShader = new Shader(lineVert, lineFrag);

        mapHandler = new MapHandler(10, 10, shader);
        player = new Player(new Vector3f(0,0,5), shader);

        long lastTime = System.nanoTime();

        shader.bind();
        shader.setVec3("lightPos", new Vector3f(10, 50, 10));
        shader.setVec3("lightColor", new Vector3f(1, 1, 1));
        shader.setVec3("objectColor", new Vector3f(1, 1, 1));
        shader.unbind();

        Object campfire = new Object("res/models/configs/campfire.json", shader, new Vector3f(2,0,2), new Vector3f(0,0,0));
        addWorldObject(campfire);

        try {
            while (!glfwWindowShouldClose(window)) {

            long now = System.nanoTime();
            float deltaTime = (now - lastTime) / 1_000_000_000.0f;
            lastTime = now;

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // 3D rendering
            player.update(deltaTime);
            cameraPos.set(player.getPosition()).add(cameraOffset);

            Matrix4f viewMatrix = new Matrix4f().lookAt(
                cameraPos,
                new Vector3f(cameraPos).add(cameraDir),
                new Vector3f(0,1,0)
            );

            Matrix4f projectionMatrix = new Matrix4f().perspective(
                (float)Math.toRadians(60.0f),
                (float)width / height,
                0.1f,
                100.0f
            );

                // Render scene: player, map, and all world objects
                player.render(viewMatrix, projectionMatrix);
                mapHandler.render(viewMatrix, projectionMatrix);
                for (Object worldObj : worldObjectList) {
                    if (worldObj == null) continue;
                    worldObj.render(viewMatrix, projectionMatrix);
                }

                // Interaction / collision checks for world objects
                for (Object obj : worldObjectList) {
                    if (obj == null) continue;
                    CollisionBox interactionBox = obj.getInteractionBox();
                    if (interactionBox == null) {
                        obj.setInRange(false);
                        continue;
                    }

                    boolean collision = CollisionHandler.checkCollision(
                        player.getCollisionBox(),
                        List.of(interactionBox)
                    );

                    if (collision) {
                        interactionBox.setColor(new Vector3f(0f,1f,0f));
                        obj.setInRange(true);
                    } else {
                        interactionBox.setColor(new Vector3f(1f,0f,0f));
                        obj.setInRange(false);
                    }
                }

            glDisable(GL_DEPTH_TEST);
            
            // render debug lines for world objects
            // for (Object obj : worldObjectList) {
            //     if (obj == null) continue;

            //     CollisionBox collisionBox = obj.getCollisionBox();
            //     if (collisionBox != null) {
            //         collisionBox.render(lineShader, viewMatrix, projectionMatrix);
            //     }

            //     CollisionBox interactionBox = obj.getInteractionBox();
            //     if (interactionBox != null) {
            //         interactionBox.render(lineShader, viewMatrix, projectionMatrix);
            //     }
            // }

            glEnable(GL_DEPTH_TEST);

            // 2D UI rendering
            glUseProgram(0);
            glDisable(GL_DEPTH_TEST);

            glMatrixMode(GL_PROJECTION);
            glPushMatrix();
            glLoadIdentity();
            glOrtho(0, width, height, 0, -1, 1);

            glMatrixMode(GL_MODELVIEW);
            glPushMatrix();
            glLoadIdentity();

            player.renderUI();

            // restore 3D
            glMatrixMode(GL_MODELVIEW);
            glPopMatrix();

            glMatrixMode(GL_PROJECTION);
            glPopMatrix();

            glEnable(GL_DEPTH_TEST);

                glfwSwapBuffers(window);
                glfwPollEvents();

            }
        } finally {
            cleanup();
        }
    }

    private static void cleanup() {
        // Destroy window and terminate GLFW
        if (window != 0) {
            glfwDestroyWindow(window);
            window = 0;
        }
        glfwTerminate();
    }

    public static InputHandler getInputHandler() {
        return inputHandler;
    }

    public static MapHandler getMapHandler() {
        return mapHandler;
    }

    private static String loadShaderSource(String path) {
        try {
            return new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load shader file: " + path, e);
        }
    }

    public static Matrix4f getViewMatrix() {
        return new Matrix4f().lookAt(
            cameraPos,
            new Vector3f(cameraPos).add(cameraDir),
            new Vector3f(0,1,0)
        );
    }

    public static Matrix4f getProjectionMatrix() {
        return new Matrix4f().perspective(
            (float)Math.toRadians(60.0f),
            (float)width / (float)height,
            0.1f,
            100.0f
        );
    }

    public static int getWindowHeight() {
        return height;
    }

    public static int getWindowWidth() {
        return width;
    }

    public static Player getPlayer() {
        return player;
    }

    public static void addWorldObject(Object obj) {
        if (obj != null) {
            worldObjectList.add(obj);
        }
    }

    public static void removeWorldObject(Object obj) {
        if (obj == null) return;
        worldObjectList.remove(obj);
        if (mapHandler != null) {
            mapHandler.removeObject(obj);
        }
    }


    public static List<Object> getWorldObjects() {
        return worldObjectList;
    }

    public static List<CollisionBox> getWorldObjectsCollisionBoxes() {
        List<CollisionBox> boxes = new ArrayList<>();
        for (Object obj : worldObjectList) {
            if (obj == null) continue;
            CollisionBox cb = obj.getCollisionBox();
            if (cb != null) {
                boxes.add(cb);
            }
        }
        return boxes;
    }

}
