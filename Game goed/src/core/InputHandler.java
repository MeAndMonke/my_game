package core;

import static org.lwjgl.glfw.GLFW.*;

import java.util.HashMap;
import java.util.Map;

public class InputHandler {

    private final long window;
    private final Map<Integer, Boolean> keyDown = new HashMap<>();
    private final Map<Integer, Boolean> mouseDown = new HashMap<>();

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
}
