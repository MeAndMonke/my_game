
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class App {
	
	private static long window;
    private static int width = 800, height = 600;
    private static Vector3f cameraPos = new Vector3f(0,0,0);
    private static Vector3f cameraDir = new Vector3f(0,0,-1);

    public static void main(String[] args) {
        initWindow();
        run();
    }

    private static void initWindow() {
        GLFW.glfwInit();
        window = GLFW.glfwCreateWindow(width, height, "LWJGL Game", 0, 0);
        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    private static void run() {
        // 1. Create shader
        String vertexShader = """
			#version 330 core
			layout(location = 0) in vec3 aPos;
			layout(location = 1) in vec3 aNormal;
			layout(location = 2) in vec2 aUV;

			uniform mat4 model;
			uniform mat4 view;
			uniform mat4 projection;

			void main() {
				gl_Position = projection * view * model * vec4(aPos, 1.0);
			}
		""";

        String fragmentShader = """
			#version 330 core
			out vec4 FragColor;

			void main() {
				FragColor = vec4(1.0,1.0,1.0,1.0);
			}
		""";

        Shader shader = new Shader(vertexShader, fragmentShader);

        // 2. Load model
        ModelHandler model = new ModelHandler("res/models/model.obj", shader);
        model.setPosition(0,0,-5);
        model.setScale(1,1,1);

        // 3. Render loop
        while (!GLFW.glfwWindowShouldClose(window)) {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            Matrix4f projection = new Matrix4f().perspective((float)Math.toRadians(70f),
                    width/(float)height, 0.01f, 1000f);
            Matrix4f view = new Matrix4f().lookAt(cameraPos,
                    new Vector3f(cameraPos).add(cameraDir), new Vector3f(0,1,0));

            model.render(view, projection);

            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }

        model.cleanup();
        GLFW.glfwTerminate();
    }
}