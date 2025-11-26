package gameplay;

import ui.UIManager;
import core.App;
import ui.UIItemSlot;

import items.Stack;

import java.util.List;

public class HotBar {

    private UIManager uiManager;

    private int screenWidth = App.getWindowWidth();
    private int screenHeight = App.getWindowHeight();

    private int itemSlotSize = 50;
    private int padding = 5;


    public HotBar(UIManager uiManager) {
        this.uiManager = uiManager;
    }

    public List<UIItemSlot> itemSlots = new java.util.ArrayList<>();

    public void loadHotbar() {
        for (int i = 0; i < 5; i++) {
            float x = (screenWidth / 2) - ((5 * itemSlotSize + 4 * padding) / 2) + i * (itemSlotSize + padding);
            float y = screenHeight - (20 + itemSlotSize);
            UIItemSlot itemSlot = new UIItemSlot(x, y, itemSlotSize);
            itemSlots.add(itemSlot);
            uiManager.add(itemSlot);
        }
        equipSlot(0);
    }

    public void setItemInSlot(int slotIndex, Stack stack) {
        if (slotIndex >= 0 && slotIndex < itemSlots.size()) {
            itemSlots.get(slotIndex).setItem(stack);
        }
    }

    public void equipSlot(int slotIndex) {
        for (int i = 0; i < itemSlots.size(); i++) {
            itemSlots.get(i).setEquipped(i == slotIndex);
        }
    }
}
