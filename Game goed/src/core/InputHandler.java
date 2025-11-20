package core;

import static org.lwjgl.glfw.GLFW.*;

import java.util.HashMap;
import java.util.Map;

public class InputHandler {

    private final Map<Integer, Boolean> keyDown = new HashMap<>();
    private final Map<Integer, Boolean> mouseDown = new HashMap<>();

    private long window;

    public InputHandler(long window) {

        this.window = window;

        glfwSetKeyCallback(window, (win, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) {
                keyDown.put(key, true);
            } else if (action == GLFW_RELEASE) {
                keyDown.put(key, false);
            }
        });

        glfwSetMouseButtonCallback(window, (win, button, action, mods) -> {
            if (action == GLFW_PRESS) {
                mouseDown.put(button, true);
            } else if (action == GLFW_RELEASE) {
                mouseDown.put(button, false);
            }
        });
    }

    public boolean isKeyDown(int key) {
        return keyDown.getOrDefault(key, false);
    }

    public boolean isMouseDown(int button) {
        return mouseDown.getOrDefault(button, false);
    }

    public double getMouseX() {
        double[] x = new double[1];
        glfwGetCursorPos(window, x, null);
        return x[0];
    }

    public double getMouseY() {
        double[] y = new double[1];
        glfwGetCursorPos(window, null, y);
        return y[0];
    }
}
