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
import java.util.List;

import core.App;

public class Player extends Entity {

    private InputHandler inputHandler;
    private UIManager uiManager = new UIManager();
    private HotBar hotBar;
    private Inventory inventory;
    private int level = 0;

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    private String equippedItem = null;
    
    private List<Object> worldObjects = App.getWorldObjects();

    public Player(Vector3f position, Shader shader) {
        super(position, shader, "res/models/configs/player.json");
        this.inputHandler = App.getInputHandler();
        this.hotBar = new HotBar(uiManager);
        hotBar.loadHotbar();

        this.inventory = new Inventory(this);
        uiManager.addInventory(inventory);
        // create craft ui and craft item ui
        gameplay.CraftingManager craftingManager = new gameplay.CraftingManager("res/items/items.json");
        uiManager.add(new ui.UICrafting(craftingManager, inventory, this, uiManager));
        uiManager.add(new ui.UICraftItem(craftingManager, inventory, this, uiManager));

        Stack axe = new Stack("basic.axe", 1);
        Stack pickaxe = new Stack("basic.pickaxe", 1);
        Stack stick2 = new Stack("stick", 8);

        inventory.addItem(stick2);
        hotBar.setItemInSlot(0, axe);
        hotBar.setItemInSlot(1, pickaxe);
    }

    public Vector3f getRotation(Vector3f direction) {
        if (direction.lengthSquared() == 0) return new Vector3f(0, 0, 0);

        // atan2 returns angle in radians, convert to degrees
        float yaw = (float) Math.toDegrees(Math.atan2(direction.x, direction.z));
        return new Vector3f(0, yaw, 0);
    }

    public void update(float deltaTime) {
        float speed = 5.0f;
        Vector3f direction = new Vector3f();
        
        if (inputHandler.isKeyPressed(GLFW_KEY_C)) {
            uiManager.toggleCrafting();
        }

        // movement input
        if (inputHandler.isKeyDown(GLFW_KEY_W)) direction.z -= 1;
        if (inputHandler.isKeyDown(GLFW_KEY_S)) direction.z += 1;
        if (inputHandler.isKeyDown(GLFW_KEY_A)) direction.x -= 1;
        if (inputHandler.isKeyDown(GLFW_KEY_D)) direction.x += 1;

        if (inputHandler.isKeyPressed(GLFW_KEY_E)) uiManager.toggleInventory();
        inventory.update(deltaTime);
        uiManager.update(deltaTime);

        if (inputHandler.isKeyPressed(GLFW_KEY_SPACE)) breakObjectInFront();

        // hotbar input
        for (int i = 0; i < hotBar.itemSlots.size(); i++) {
            if (inputHandler.isKeyDown(KeyEvent.VK_1 + i)) {
                hotBar.equipSlot(i);
                equippedItem = hotBar.getItemInSlot(i);
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

    public void breakObjectInFront() {
        Object toRemove = null;
        for (Object obj : worldObjects) {
            if (obj == null) continue;

            String[] required = obj.getToolsRequired();
            if (required != null && required.length > 0) {
                // if player has no equipped tool, can't break
                if (equippedItem == null) continue;
                boolean ok = false;
                for (String r : required) {
                    if (r != null && r.equals(equippedItem)) { ok = true; break; }
                }
                if (!ok) continue;
            }

            if (obj.inRange()) {
                List<Stack> drops = obj.breakObject();
                for (Stack drop : drops) {
                    inventory.addItem(drop);
                }
                toRemove = obj;
                break;
            }
        }

        if (toRemove != null) {
            // remove from global world and map
            core.App.removeWorldObject(toRemove);
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
