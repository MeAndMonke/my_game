package gameplay;

import core.App;
import items.Stack;
import ui.UIManager;
import ui.UIItemSlot;

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
            itemSlots.get(slotIndex).setStack(stack);
        }
    }

    public String getItemInSlot(int slotIndex) {
        if (slotIndex >= 0 && slotIndex < itemSlots.size()) {
            Stack stack = itemSlots.get(slotIndex).getStack();
            if (stack != null) {
                return stack.getItemId();
            }
        }
        return null;
    }

    public String getEquippedItemId() {
        for (int i = 0; i < itemSlots.size(); i++) {
            UIItemSlot slot = itemSlots.get(i);
            if (slot.isEquipped() && slot.getStack() != null) {
                return slot.getStack().getItemId();
            }
        }
        return null;
    }

    public UIItemSlot getEquippedSlot() {
        for (UIItemSlot slot : itemSlots) {
            if (slot.isEquipped()) return slot;
        }
        return null;
    }

    public boolean consumeOneFromEquipped() {
        UIItemSlot slot = getEquippedSlot();
        if (slot == null) return false;
        if (slot.getStack() == null) return false;
        int amt = slot.getStack().getAmount();
        if (amt <= 1) {
            slot.setStack(null);
        } else {
            slot.getStack().setQuantity(amt - 1);
        }
        return true;
    }

    public void equipSlot(int slotIndex) {
        for (int i = 0; i < itemSlots.size(); i++) {
            itemSlots.get(i).setEquipped(i == slotIndex);
        }
    }
}
