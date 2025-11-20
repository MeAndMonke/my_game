package entity;

import org.joml.Vector3f;

import core.InputHandler;
import renderer.Shader;

import static org.lwjgl.glfw.GLFW.*;

import core.App;

public class Player extends Entity {

    private InputHandler inputHandler;

    public Player(Vector3f position, Shader shader) {
        super(position, shader, "res/models/configs/player.json");
        this.inputHandler = App.getInputHandler();
    }

    public Vector3f getRotation(Vector3f direction) {
        if (direction.lengthSquared() == 0) return new Vector3f(0, 0, 0); // No movement

        // atan2 returns angle in radians, convert to degrees
        float yaw = (float) Math.toDegrees(Math.atan2(direction.x, direction.z));
        return new Vector3f(0, yaw, 0);
    }

    public void update(float deltaTime) {
        float speed = 5.0f;
        Vector3f direction = new Vector3f();

        if (inputHandler.isKeyDown(GLFW_KEY_W)) direction.z -= 1;
        if (inputHandler.isKeyDown(GLFW_KEY_S)) direction.z += 1;
        if (inputHandler.isKeyDown(GLFW_KEY_A)) direction.x -= 1;
        if (inputHandler.isKeyDown(GLFW_KEY_D)) direction.x += 1;

        if (direction.length() > 0) {
            direction.normalize().mul(speed * deltaTime);
            setPosition(getPosition().add(direction));

            // Calculate rotation based on movement
            Vector3f newRotation = getRotation(direction);
            setRotation(newRotation);
        }
    }

}
