package ui;

import java.util.ArrayList;
import java.util.List;

import core.App;
import gameplay.HotBar;
import items.Stack;

import entity.Player;

public class UIInventory extends UIElement {

    private List<Stack> stacks = new ArrayList<>();

    private int slots;

    private int slotSize = 75;
    private int padding = 15;

    private int grid = 5;

    private int xOffset = 30;
    private int yOffset = 50;

    private Player player;
    private HotBar hotbar;

    public UIInventory(int slotAmount, Player player) {
        this.slots = slotAmount;
        this.player = player;
        this.hotbar = player.getHotBar();
        addItem(new Stack("stick", 3));
    }

    public void addItem(Stack stack) {
        stacks.add(stack);
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
        drawRect(0, 0, 500, App.getWindowHeight(), 100, 100, 100, 150);

        for (int i = 0; i < slots; i++) {
            int col = i % grid;
            int row = i / grid;

            float x = (col * (slotSize + padding)) + xOffset;
            float y = (row * (slotSize + padding)) + yOffset;

            drawRect(x, y, slotSize, slotSize, 150, 150, 150, 200);

            if (stacks != null && i < stacks.size()) {
                Stack stack = stacks.get(i);
                if (stack != null && stack.getItem() != null && stack.getItem().getImage() != null) {
                    drawImage(stack.getItem().getImage(), x + 5, y + 5, slotSize - 10, slotSize - 10);
                    drawText(stack.getAmount() + "", x + slotSize - 30, y + slotSize - 30, 25);
                }
            }
        }

        for (int i = 0; i < hotbar.itemSlots.size(); i++) {
            UIItemSlot slot = hotbar.itemSlots.get(i);
            slot.getStack();

            float x = (i * (slotSize + padding)) + xOffset;
            float y = App.getWindowHeight() - (yOffset + slotSize) + 10;

            drawRect(x, y, slotSize, slotSize, 150, 150, 150, 200);

            if (slot.getStack() != null && slot.getStack().getItem() != null && slot.getStack().getItem().getImage() != null) {
                drawImage(slot.getStack().getItem().getImage(), x + 5, y + 5, slotSize - 10, slotSize - 10);
                drawText(slot.getStack().getAmount() + "", x + slotSize - 30, y + slotSize - 30, 25);
            }
        }
    }    
}
