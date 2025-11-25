package entity;

import org.joml.Vector3f;

import core.InputHandler;
import gameplay.HotBar;
import physics.CollisionHandler;
import renderer.Shader;

import static org.lwjgl.glfw.GLFW.*;
import org.joml.Matrix4f;

import ui.UIManager;
import java.awt.event.KeyEvent;


import core.App;

public class Player extends Entity {

    private InputHandler inputHandler;
    private UIManager uiManager = new UIManager();
    private HotBar hotBar;

    public Player(Vector3f position, Shader shader) {
        super(position, shader, "res/models/configs/player.json");
        this.inputHandler = App.getInputHandler();
        this.hotBar = new HotBar(uiManager);
        hotBar.loadHotbar();
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

        // movement input
        if (inputHandler.isKeyDown(GLFW_KEY_W)) direction.z -= 1;
        if (inputHandler.isKeyDown(GLFW_KEY_S)) direction.z += 1;
        if (inputHandler.isKeyDown(GLFW_KEY_A)) direction.x -= 1;
        if (inputHandler.isKeyDown(GLFW_KEY_D)) direction.x += 1;

        // hotbar input
        // check if number keys 1-5 are pressed to equip corresponding slot
        for (int i = 0; i < hotBar.itemSlots.size(); i++) {
            if (inputHandler.isKeyDown(KeyEvent.VK_1 + i)) {
                hotBar.equipSlot(i);
            }
        }

        if (direction.length() > 0) {
            direction.normalize().mul(speed * deltaTime);
            Vector3f newPos = new Vector3f(getPosition()).add(direction);

            collisionBox.setPosition(newPos);

            boolean collision = CollisionHandler.checkCollision(collisionBox, App.getWorldObjectsCollisionBoxes());

            if (!collision) {
                setPosition(newPos);
            } else {
                collisionBox.setPosition(getPosition());
            }

            setRotation(getRotation(direction));
        }

    }

    public void renderUI() {
        uiManager.render();
    }

}
