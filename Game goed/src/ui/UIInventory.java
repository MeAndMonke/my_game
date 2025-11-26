package ui;

import java.util.List;

import core.App;
import items.ItemManager;
import items.Stack;

public class UIInventory extends UIElement {

    private ItemManager itemManager = App.itemManager;
    private List<Stack> stacks;

    private int slots;

    private int slotSize = 100;
    private int padding = 15;

    private int gridX = 5;
    private int gridY = 10;

    public UIInventory(int slotAmount) {
        this.slots = slotAmount;
    }

    @Override
    public void update(float dt) {
        return;
    }

    @Override
    public void onClick(double mx, double my) {
        return;
    }

    @Override
    public void render() {
        drawRect(0, 0, 700, App.getWindowHeight(), 100, 100, 100, 150);

        for (int i = 0; i < slots; i++) {
            int col = i % gridX;
            int row = i / gridX;

            float x = col * (slotSize + padding);
            float y = row * (slotSize + padding);

            drawRect(x, y, slotSize, slotSize, 150, 150, 150, 200);

            if (stacks != null && i < stacks.size()) {
                Stack stack = stacks.get(i);
                if (stack != null && stack.getItem() != null && stack.getItem().getImage() != null) {
                    drawImage(stack.getItem().getImage(), x + 5, y + 5, slotSize - 10, slotSize - 10);
                }
            }
        }
    }    
}
