package ui;

import java.util.ArrayList;
import java.util.List;

import gameplay.Inventory;


public class UIManager {

    private List<UIElement> elements = new ArrayList<>();
    private Inventory inventory;
    private boolean inventoryOpen = false;

    public void add(UIElement e) {
        elements.add(e);
    }

    public void addInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public void toggleInventory() {
        inventoryOpen = !inventoryOpen;
    }

    public void update(float dt) {
        for (UIElement e : elements) {
            e.update(dt);
        }
    }

    public void render() {
        for (UIElement e : elements) {
            e.render();
        }

        if (inventoryOpen && inventory != null) {
            // render inventory ui here
        }
    }

    public void onClick(double mx, double my) {
        for (UIElement e : elements) {
            e.onClick(mx, my);
        }
    }
}
