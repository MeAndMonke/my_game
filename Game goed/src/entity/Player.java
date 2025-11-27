package entity;

import org.joml.Vector3f;

import core.InputHandler;
import gameplay.HotBar;
import gameplay.Inventory;
import physics.CollisionHandler;
import renderer.Shader;

import static org.lwjgl.glfw.GLFW.*;

import ui.UIManager;
import java.awt.event.KeyEvent;

import items.Stack;


import core.App;

public class Player extends Entity {

    private InputHandler inputHandler;
    private UIManager uiManager = new UIManager();
    private HotBar hotBar;
    private Inventory inventory;

    public Player(Vector3f position, Shader shader) {
        super(position, shader, "res/models/configs/player.json");
        this.inputHandler = App.getInputHandler();
        this.hotBar = new HotBar(uiManager);
        hotBar.loadHotbar();

        this.inventory = new Inventory(this);
        uiManager.addInventory(inventory);

        Stack stick = new Stack("stick", 12);
        Stack stick2 = new Stack("stick", 8);
        inventory.addItem(stick2);
        hotBar.setItemInSlot(0, stick);
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

        if (inputHandler.isKeyPressed(GLFW_KEY_E)) inventory.toggleInventory();
        inventory.update(deltaTime);

        if (inputHandler.isKeyPressed(GLFW_KEY_SPACE)) {
            // break thingie infront of player
        }

        // hotbar input
        for (int i = 0; i < hotBar.itemSlots.size(); i++) {
            if (inputHandler.isKeyDown(KeyEvent.VK_1 + i)) {
                hotBar.equipSlot(i);
            }
        }

        if (direction.length() > 0) {
            direction.normalize().mul(speed * deltaTime);
            Vector3f newPos = new Vector3f(getPosition()).add(direction);

            collisionBox.setPosition(newPos);

            boolean collision = CollisionHandler.checkCollision(collisionBox, 
                App.getWorldObjectsCollisionBoxes()
            );

            if (!collision) {
                setPosition(newPos);
            } else {
                collisionBox.setPosition(getPosition());
            }

            setRotation(getRotation(direction));
        }
    }

    public HotBar getHotBar() {
        return hotBar;
    }

    public void renderUI() {
        uiManager.render();
        inventory.render();
    }
}
