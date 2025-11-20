package core;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL;

import entity.Player;
import renderer.*;

import entity.Object;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.joml.Matrix4f;
import java.util.List;
import physics.CollisionBox;

public class App {
    private static long window;
    private static int width = 1980, height = 1060;
    public static Vector3f cameraPos = new Vector3f(0,0,0);
    private static Vector3f cameraDir = new Vector3f(0,-2,-1);

    public static InputHandler inputHandler;

    private static List<CollisionBox> worldObjectsCollisionBoxes;

    private static Vector3f cameraOffset = new Vector3f(0, 5, 1); // X, Y, Z relative to player


    private static void initWindow() {
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        window = glfwCreateWindow(width, height, "Survival Game", 0, 0);
        if (window == 0) throw new RuntimeException("Failed to create window");

        inputHandler = new InputHandler(window);

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        GL.createCapabilities();

        glEnable(GL_DEPTH_TEST);
        glClearColor(0.2f, 0.4f, 0.2f, 1f);
    }

    public static void main(String[] args) {
        initWindow();
        run();
    }

    public static void run() {
        // Load shader and model once
        String vertexShaderCode = loadShaderSource("res/shaders/default.vert");
        String fragmentShaderCode = loadShaderSource("res/shaders/default.frag");
        Shader shader = new Shader(vertexShaderCode, fragmentShaderCode);
        // shader used for drawing simple colored lines (wireframes)
        String lineVert = loadShaderSource("res/shaders/line.vert");
        String lineFrag = loadShaderSource("res/shaders/line.frag");
        Shader lineShader = new Shader(lineVert, lineFrag);
        
        Object tree = new Object("res/models/configs/tree.json", shader, new Vector3f(2,0,-2f), new Vector3f(0,0,0));
        Player player = new Player(new Vector3f(0,0,-0.5f), shader);

        // Only include static/world objects here — don't include the player's own box
        worldObjectsCollisionBoxes = List.of(
            tree.getCollisionBox()
        );

        long lastTime = System.nanoTime();

        shader.bind();
        shader.setVec3("lightPos", new Vector3f(10, 10, 10));
        shader.setVec3("lightColor", new Vector3f(1, 1, 1));
        shader.setVec3("objectColor", new Vector3f(1, 1, 1));
        shader.unbind();

        // NOTE: debug collision rendering will happen each frame in the main loop below

        while (!glfwWindowShouldClose(window)) {

            long now = System.nanoTime();
            float deltaTime = (now - lastTime) / 1_000_000_000.0f;
            lastTime = now;

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // === UPDATE ===
            player.update(deltaTime);

            cameraPos.set(player.getPosition()).add(cameraOffset);

            // === CAMERA MATRICES ===
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

            // === RENDER ===
            player.render(viewMatrix, projectionMatrix);
            tree.render(viewMatrix, projectionMatrix);

            // DEBUG: render collision boxes (wireframes) each frame so they remain visible
            for (CollisionBox cb : worldObjectsCollisionBoxes) {
                cb.render(lineShader, viewMatrix, projectionMatrix);
            }
            // render the player's collision box too (player not included in worldObjectsCollisionBoxes)
            player.getCollisionBox().render(lineShader, viewMatrix, projectionMatrix);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    public static InputHandler getInputHandler() {
        return inputHandler;
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

    public static List<CollisionBox> getWorldObjectsCollisionBoxes() {
        return worldObjectsCollisionBoxes;
    }

}
