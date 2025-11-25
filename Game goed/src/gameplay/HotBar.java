package gameplay;

import ui.UIManager;
import core.App;
import core.InputHandler;
import ui.UIItemSlot;

import java.util.List;
import java.awt.event.KeyEvent;

public class HotBar {

    private UIManager uiManager;

    private int screenWidth = App.getWindowWidth();
    private int screenHeight = App.getWindowHeight();

    private int itemSlotSize = 50;
    private int padding = 5;

    private InputHandler inputHandler = App.getInputHandler();

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

    public void setItemInSlot(int slotIndex, items.Item item) {
        if (slotIndex >= 0 && slotIndex < itemSlots.size()) {
            itemSlots.get(slotIndex).setItem(item);
        }
    }

    public void equipSlot(int slotIndex) {
        for (int i = 0; i < itemSlots.size(); i++) {
            itemSlots.get(i).setEquipped(i == slotIndex);
        }
    }
}
