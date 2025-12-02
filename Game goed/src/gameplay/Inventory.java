package gameplay;

import items.Stack;
import ui.UIInventory;

import java.util.List;
import java.util.ArrayList;

import entity.Player;

public class Inventory {
    private List<Stack> items = new ArrayList<>();
    private UIInventory uiInventory;
    private boolean isOpen = false;
    private Player player;
    private final int slotCount = 10;

    public Inventory(Player player) {
        this.player = player;
        for (int i = 0; i < slotCount; i++) items.add(null);
        uiInventory = new UIInventory(slotCount, this);
    }

    public UIInventory getUIInventory() { return uiInventory; }

    public void addItem(Stack stack) {
        for (int i = 0; i < items.size(); i++) {
            Stack s = items.get(i);
            if (s != null && s.getItemId().equals(stack.getItemId())) {
                int space = s.getMaxStackSize() - s.getAmount();
                if (space >= stack.getAmount()) {
                    s.setQuantity(s.getAmount() + stack.getAmount());
                    return;
                } else if (space > 0) {
                    s.setQuantity(s.getAmount() + space);
                    stack.setQuantity(stack.getAmount() - space);
                }
            }
        }
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) == null) {
                items.set(i, stack);
                return;
            }
        }
    }

    public boolean hasItemQuantity(String itemId, int qty) {
        int found = 0;
        for (Stack s : items) {
            if (s == null) continue;
            if (s.getItemId().equals(itemId)) found += s.getAmount();
            if (found >= qty) return true;
        }
        return false;
    }

    public boolean removeItemQuantity(String itemId, int qty) {
        int remaining = qty;
        for (int i = 0; i < items.size(); i++) {
            Stack s = items.get(i);
            if (s == null) continue;
            if (!s.getItemId().equals(itemId)) continue;
            int take = Math.min(s.getAmount(), remaining);
            s.setQuantity(s.getAmount() - take);
            remaining -= take;
            if (s.getAmount() <= 0) items.set(i, null);
            if (remaining <= 0) return true;
        }
        return false;
    }

    public Player getPlayer() { return player; }

    public int getSlotCount() { return slotCount; }

    public Stack getSlot(int index) { return items.get(index); }

    public void setSlot(int index, Stack stack) { items.set(index, stack); }

    public void update(float dt) {
        if (isOpen) {
            uiInventory.update(dt);
        }
    }

    public void removeItem(Stack stack) {
        items.remove(stack);
    }

    public void toggleInventory() {
        isOpen = !isOpen;
    }

    public boolean isOpen() { return isOpen; }

    public void render() {
        if (!isOpen) return;
        uiInventory.render();
    }
}
