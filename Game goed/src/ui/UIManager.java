package ui;

import java.util.ArrayList;
import java.util.List;

import gameplay.Inventory;
import core.App;
import static org.lwjgl.glfw.GLFW.*;


public class UIManager {

    private List<UIElement> elements = new ArrayList<>();
    private Inventory inventory;
    private boolean inventoryOpen = false;
    private boolean craftingOpen = false;
    private boolean lastMouseDown = false;
    private boolean clickConsumed = false;

    public void add(UIElement e) {
        elements.add(e);
    }

    public void addInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public void toggleInventory() {
        inventoryOpen = !inventoryOpen;
        if (inventoryOpen) {
            craftingOpen = false;
            if (inventory != null && !inventory.isOpen()) inventory.toggleInventory();
            UICraftItem ci = getCraftItemElement();
            if (ci != null && ci.isOpen()) ci.close();
        } else {
            if (inventory != null && inventory.isOpen()) inventory.toggleInventory();
        }
    }

    public void toggleCrafting() {
        craftingOpen = !craftingOpen;
        if (craftingOpen) {
            inventoryOpen = false;
            if (inventory != null && inventory.isOpen()) inventory.toggleInventory();
        } else {
            UICraftItem ci = getCraftItemElement();
            if (ci != null && ci.isOpen()) ci.close();
        }
    }


    public boolean isCraftingOpen() { return craftingOpen; }

    public void toggleCraftingMode() {
        toggleCrafting();
    }

    public void update(float dt) {
        for (UIElement e : elements) {
            if (e instanceof UICrafting && !craftingOpen) continue;
            e.update(dt);
        }

        boolean mouseDown = App.getInputHandler().isMouseDown(GLFW_MOUSE_BUTTON_LEFT);
        if (mouseDown && !lastMouseDown) {
            double mx = App.getInputHandler().getMouseX();
            double my = App.getInputHandler().getMouseY();
            onClick(mx, my);
        }
        lastMouseDown = mouseDown;
    }

    public void render() {
        for (UIElement e : elements) {
            if (e instanceof UICrafting && !craftingOpen) continue;
            e.render();
        }

        if (inventoryOpen && inventory != null) {
        }
    }

    public void onClick(double mx, double my) {
        for (UIElement e : elements) {
            if (e instanceof UICrafting && !craftingOpen) continue;
            e.onClick(mx, my);
        }
    }

    public void consumeClick() { clickConsumed = true; }
    public boolean isClickConsumed() { return clickConsumed; }
    public void clearClickConsumed() { clickConsumed = false; }

    public UICraftItem getCraftItemElement() {
        for (UIElement e : elements) {
            if (e instanceof UICraftItem) return (UICraftItem)e;
        }
        return null;
    }
}
