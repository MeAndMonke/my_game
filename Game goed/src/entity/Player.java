package entity;

import org.joml.Vector3f;

import core.InputHandler;
import gameplay.HotBar;
import gameplay.Inventory;
import physics.CollisionHandler;
import renderer.Shader;

import gameplay.BuildManager;

import static org.lwjgl.glfw.GLFW.*;

import ui.UIManager;
import items.Item;
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
    private Shader shader;
    private BuildManager buildManager = null;
    
    private List<Object> worldObjects = App.getWorldObjects();

    public Player(Vector3f position, Shader shader) {
        super(position, shader, "res/models/configs/player.json");
        this.shader = shader;
        this.inputHandler = App.getInputHandler();
        this.hotBar = new HotBar(uiManager);
        hotBar.loadHotbar();

        this.inventory = new Inventory(this);
        uiManager.addInventory(inventory);

        gameplay.CraftingManager craftingManager = new gameplay.CraftingManager("res/items/items.json");
        uiManager.add(new ui.UICrafting(craftingManager, inventory, this, uiManager));
        uiManager.add(new ui.UICraftItem(craftingManager, inventory, this, uiManager));

        Stack axe = new Stack("basic.axe", 1);
        Stack pickaxe = new Stack("basic.pickaxe", 1);
        Stack stick2 = new Stack("stick", 8);

        inventory.addItem(stick2);
        hotBar.setItemInSlot(0, axe);
        hotBar.setItemInSlot(1, pickaxe);

        // initialize buildManager to null; will be created when holding a placeable item
    }

    /**
     * @param direction direction vector
     * @return rotation in degrees
     */
    public Vector3f getRotation(Vector3f direction) {
        if (direction.lengthSquared() == 0) return new Vector3f(0, 0, 0);

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

        // placement or breaking
        if (inputHandler.isKeyPressed(GLFW_KEY_SPACE)) {
            items.Stack held = null;
            if (inventory != null && inventory.getUIInventory() != null) {
                held = inventory.getUIInventory().getHeldStack();
            }

            if (held != null && held.getItem() != null && held.getItem().isPlaceable()) {
                String modelId = held.getItem().getModelId();
                if (modelId != null) {
                    String configPath = "res/models/configs/" + modelId + ".json";
                    if (buildManager == null) buildManager = new BuildManager(configPath, shader);
                    Vector3f placePos = computePlacementPosition();
                    buildManager.placeModel(placePos);
                    held.setQuantity(held.getAmount() - 1);
                    if (held.getAmount() <= 0) inventory.getUIInventory().setHeldStack(null);
                    return;
                }
            }

            String equipped = hotBar.getEquippedItemId();
            if (equipped != null) {
                items.Item it = core.App.itemManager.getItemById(equipped);
                if (it != null && it.isPlaceable()) {
                    String modelId = it.getModelId();
                    if (modelId != null) {
                        String configPath = "res/models/configs/" + modelId + ".json";
                        if (buildManager == null) buildManager = new BuildManager(configPath, shader);
                        Vector3f placePos = computePlacementPosition();
                        buildManager.placeModel(placePos);
                        hotBar.consumeOneFromEquipped();
                        return;
                    }
                }
            }

            breakObjectInFront();
        }

        // hotbar input
        for (int i = 0; i < hotBar.itemSlots.size(); i++) {
            if (inputHandler.isKeyDown(KeyEvent.VK_1 + i)) {
                hotBar.equipSlot(i);
                equippedItem = hotBar.getItemInSlot(i);
            }
        }

        // apply movement
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

    /**
     * Breaks object near player.
     */
    public void breakObjectInFront() {
        Object toRemove = null;
        for (Object obj : worldObjects) {
            if (obj == null) continue;

            String[] required = obj.getToolsRequired();
            if (required != null && required.length > 0) {
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
            core.App.removeWorldObject(toRemove);
        }
    }

    public HotBar getHotBar() {
        return hotBar;
    }

    private Vector3f computePlacementPosition() {
        float yaw = getRotation().y;
        float rad = (float)Math.toRadians(yaw);
        Vector3f forward = new Vector3f((float)Math.sin(rad), 0f, (float)Math.cos(rad));
        Vector3f pos = new Vector3f(getPosition()).add(new Vector3f(forward).mul(2f));
        pos.y = 0f;
        return pos;
    }

    @Override
    public void render(org.joml.Matrix4f view, org.joml.Matrix4f projection) {
        super.render(view, projection);

        // preview equipped hotbar item
        String equipped = hotBar.getEquippedItemId();
        if (equipped == null) { return; }
        Item it = core.App.itemManager.getItemById(equipped);
        if (it == null) { return; }
        if (it.isPlaceable()) {
            String modelId = it.getModelId();
            if (modelId == null) { return; }
            if (buildManager == null) buildManager = new BuildManager("res/models/configs/" + modelId + ".json", shader);
            
            Vector3f placePos = computePlacementPosition();
            buildManager.renderPreview(placePos, view, projection);
        }
    }

    public void renderUI() {
        uiManager.render();
        inventory.render();
    }
}
