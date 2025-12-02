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

    /**
     * Loads the hotbar UI elements.
     */
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

    /**
     * Sets an item stack in the hotbar slot.
     * @param slotIndex Index of the hotbar slot (0-4).
     * @param stack The item stack to set.
     */
    public void setItemInSlot(int slotIndex, Stack stack) {
        if (slotIndex >= 0 && slotIndex < itemSlots.size()) {
            itemSlots.get(slotIndex).setStack(stack);
        }
    }

    /**
     * Gets the item ID in the specified hotbar slot.
     * @param slotIndex Index of the hotbar slot (0-4).
     * @return The item ID in the slot, or null if empty.
     */
    public String getItemInSlot(int slotIndex) {
        if (slotIndex >= 0 && slotIndex < itemSlots.size()) {
            Stack stack = itemSlots.get(slotIndex).getStack();
            if (stack != null) {
                return stack.getItemId();
            }
        }
        return null;
    }

    /**
     * @return The item ID of the currently equipped item or null if none.
     */
    public String getEquippedItemId() {
        for (int i = 0; i < itemSlots.size(); i++) {
            UIItemSlot slot = itemSlots.get(i);
            if (slot.isEquipped() && slot.getStack() != null) {
                return slot.getStack().getItemId();
            }
        }
        return null;
    }

    /**
     * Returns the equipped UIItemSlot or null if none.
     */
    public UIItemSlot getEquippedSlot() {
        for (UIItemSlot slot : itemSlots) {
            if (slot.isEquipped()) return slot;
        }
        return null;
    }

    /**
     * Consume one item from the currently equipped slot. If the stack becomes empty, the slot is cleared.
     * @return true if an item was consumed, false if there was no item.
     */
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

    /**
     * Equips the hotbar slot at the given index.
     * @param slotIndex Index of the hotbar slot (0-4).
     */
    public void equipSlot(int slotIndex) {
        for (int i = 0; i < itemSlots.size(); i++) {
            itemSlots.get(i).setEquipped(i == slotIndex);
        }
    }
}
