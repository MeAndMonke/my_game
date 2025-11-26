package gameplay;

import items.Stack;
import ui.UIInventory;

import java.util.List;
import java.util.ArrayList;

public class Inventory {
    private List<Stack> items = new ArrayList<>();
    private UIInventory uiInventory = new UIInventory(10);

    private boolean isOpen = false;

    public Inventory() {
    }

    public void addItem(Stack stack) {
        if (!items.contains(stack)) {
            items.add(stack);
            return;
        }

        Stack existingStack = items.get(items.indexOf(stack));

        int max = existingStack.getMaxStackSize();
        int current = existingStack.getQuantity();
        int incoming = stack.getQuantity();

        int total = current + incoming;

        if (total <= max) {
            existingStack.setQuantity(total);
        } else {
            existingStack.setQuantity(max);
            int leftover = total - max;

            Stack newStack = new Stack(stack.getItemId(), leftover);

            addItem(newStack);
        }
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
