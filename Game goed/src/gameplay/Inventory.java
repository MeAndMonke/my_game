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
        // initialize fixed-size inventory
        for (int i = 0; i < slotCount; i++) items.add(null);
        uiInventory = new UIInventory(slotCount, this);
    }

    public void addItem(Stack stack) {
        // try to merge into existing stacks first
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
        // place leftover into first empty slot
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) == null) {
                items.set(i, stack);
                return;
            }
        }
        // inventory full: drop or ignore (currently ignore)
    }

    public Player getPlayer() { return player; }

    public int getSlotCount() { return slotCount; }

    public Stack getSlot(int index) { return items.get(index); }

    public void setSlot(int index, Stack stack) { items.set(index, stack); }

    public void update(float dt) {
        uiInventory.update(dt);
    }


    public void removeItem(Stack stack) {
        items.remove(stack);
    }

    public void toggleInventory() {
        isOpen = !isOpen;
    }

    public void render() {
        if (!isOpen) return;
        uiInventory.render();
    }
}
