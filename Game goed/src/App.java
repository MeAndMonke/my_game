import org.joml.Vector3f;
import org.lwjgl.opengl.GL;

import renderer.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.joml.Matrix4f;


public class App {
    private static long window;
    private static int width = 1980, height = 1060;
    public static Vector3f cameraPos = new Vector3f(0,5,1);
    private static Vector3f cameraDir = new Vector3f(0,-2,-1);

    private static void initWindow() {
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        window = glfwCreateWindow(width, height, "Survival Game", 0, 0);
        if (window == 0) throw new RuntimeException("Failed to create window");

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
        Model model = new Model("res/models/model.obj", shader, new Vector3f(0,0,-0.5f), new Vector3f(0,180,0), 1f, new Texture("res/textures/player.jpg"));
        Model tree = new Model("res/models/tree.obj", shader, new Vector3f(2,0,-2f), new Vector3f(0,0,0), 1f, new Texture("res/textures/tree_texture.jpg"));
        
        shader.bind();
        shader.setVec3("lightPos", new Vector3f(10, 10, 10));
        shader.setVec3("lightColor", new Vector3f(1, 1, 1));
        shader.setVec3("objectColor", new Vector3f(1, 1, 1));
        shader.unbind();

        // Main loop
        while (!glfwWindowShouldClose(window)) {

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // Create camera matrices
            Matrix4f viewMatrix = new Matrix4f().lookAt(cameraPos, new Vector3f(cameraPos).add(cameraDir), new Vector3f(0,1,0));
            Matrix4f projectionMatrix = new Matrix4f().perspective((float)Math.toRadians(60.0f), (float)width/height, 0.1f, 100.0f);

            // Render the model
            model.render(viewMatrix, projectionMatrix);
            tree.render(viewMatrix, projectionMatrix);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private static String loadShaderSource(String path) {
        try {
            return new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load shader file: " + path, e);
        }
    }

}
