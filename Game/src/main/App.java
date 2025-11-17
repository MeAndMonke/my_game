package main;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import entity.Player;

public class App {

    private static long window;
    private static int width = 800, height = 600;
    public static Vector3f cameraPos = new Vector3f(0,0,0);
    private static Vector3f cameraDir = new Vector3f(0,0,-1);

    private static Player player;

    public static void main(String[] args) {
        initWindow();
        run();
    }

    private static void initWindow() {
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        window = glfwCreateWindow(width, height, "LWJGL Game", 0, 0);
        if (window == 0) throw new RuntimeException("Failed to create window");

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1); // Enable vsync
        GL.createCapabilities();

        glEnable(GL_DEPTH_TEST);
        glClearColor(0f, 0f, 0f, 1f);
    }

    private static void run() {

        String vertexShader = """
            #version 330 core
            layout(location = 0) in vec3 aPos;
            layout(location = 1) in vec3 aNormal;
            layout(location = 2) in vec2 aUV;

            uniform mat4 model;
            uniform mat4 view;
            uniform mat4 projection;

            out vec3 FragPos;
            out vec3 Normal;

            void main() {
                FragPos = vec3(model * vec4(aPos, 1.0));
                Normal = mat3(transpose(inverse(model))) * aNormal;
                gl_Position = projection * view * vec4(FragPos, 1.0);
            }
        """;

        String fragmentShader = """
            #version 330 core
            out vec4 FragColor;

            in vec3 FragPos;
            in vec3 Normal;

            uniform vec3 lightPos;
            uniform vec3 lightColor;
            uniform vec3 objectColor;

            void main() {
                // simple diffuse lighting
                vec3 norm = normalize(Normal);
                vec3 lightDir = normalize(lightPos - FragPos);
                float diff = max(dot(norm, lightDir), 0.0);
                vec3 diffuse = diff * lightColor;
                
                vec3 result = diffuse * objectColor;
                FragColor = vec4(result, 1.0);
            }
        """;
        
        Shader shader = new Shader(vertexShader, fragmentShader);

        player = new Player(shader);

        // render loop
        int fps = 0;
        double fpsTimer = glfwGetTime();

        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            double currentTime = glfwGetTime();

            fps++;
            if (currentTime - fpsTimer >= 1.0) {
                System.out.println("FPS: " + fps);
                fps = 0;
                fpsTimer += 1.0;
            }

            Matrix4f projection = new Matrix4f().perspective((float)Math.toRadians(70f), width/(float)height, 0.01f, 1000f);
            Matrix4f view = new Matrix4f().lookAt(cameraPos, new Vector3f(cameraPos).add(cameraDir), new Vector3f(0,1,0));
            Vector3f rotation = player.getRotation();
            rotation.y += 10;
            player.setRotation(rotation);
            player.render(view, projection);


            glfwSwapBuffers(window);
            glfwPollEvents();
        }
        glfwTerminate();
    }
}
