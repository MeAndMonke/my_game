import org.joml.Vector3f;
import org.lwjgl.opengl.GL;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class App {
    private static long window;
    private static int width = 800, height = 600;
    public static Vector3f cameraPos = new Vector3f(0,0,0);
    private static Vector3f cameraDir = new Vector3f(0,0,-1);

    private static void initWindow() {
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        window = glfwCreateWindow(width, height, "LWJGL Game", 0, 0);
        if (window == 0) throw new RuntimeException("Failed to create window");

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        GL.createCapabilities();

        glEnable(GL_DEPTH_TEST);
        glClearColor(1f, 0f, 0f, 1f);
    }

    public static void main(String[] args) {
        initWindow();
        run();
    }

    public static void run() {
        while (!glfwWindowShouldClose(window)) {

            // game loop logic goes here

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }
    
}
