package ui;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

import core.App;
import gameplay.HotBar;
import items.Stack;


public class UIInventory extends UIElement {

    private List<Stack> stacks = new ArrayList<>();

    private int slots;

    private int slotSize = 75;
    private int padding = 15;

    private int grid = 5;

    private int xOffset = 30;
    private int yOffset = 50;

    private gameplay.Inventory inventory;
    private HotBar hotbar;

    private UIInventorySlot[] inventorySlots;

    private items.Stack heldStack = null;
    private boolean lastMouseDown = false;
    private boolean lastRightDown = false;

    public UIInventory(int slotAmount, gameplay.Inventory inventory) {
        this.slots = slotAmount;
        this.inventory = inventory;
        this.hotbar = inventory.getPlayer().getHotBar();

        for (int i = 0; i < slots; i++) stacks.add(inventory.getSlot(i));

        inventorySlots = new UIInventorySlot[slots];
        for (int i = 0; i < slots; i++) {
            inventorySlots[i] = new UIInventorySlot(
                (i % grid) * (slotSize + padding) + xOffset,
                (i / grid) * (slotSize + padding) + yOffset,
                slotSize
            );
        }
    }

    public void addItem(Stack stack) {
        for (int i = 0; i < stacks.size(); i++) {
            if (stacks.get(i) == null) {
                stacks.set(i, stack);
                if (inventory != null) inventory.setSlot(i, stack);
                return;
            }
        }
        if (stacks.size() < slots) stacks.add(stack);
        if (inventory != null) inventory.setSlot(stacks.size() - 1, stack);
    }

    @Override
    public void update(float dt) {
        if (inventory != null) {
            for (int i = 0; i < slots; i++) {
                Stack invStack = inventory.getSlot(i);
                if (i < stacks.size()) {
                    stacks.set(i, invStack);
                } else {
                    stacks.add(invStack);
                }
            }
        }

        for (int i = 0; i < slots; i++) {
            UIInventorySlot slot = inventorySlots[i];
            slot.update(dt);
        }

        boolean mouseDown = App.getInputHandler().isMouseDown(GLFW_MOUSE_BUTTON_LEFT);
        if (mouseDown && !lastMouseDown) {
            for (int i = 0; i < slots; i++) {
                UIInventorySlot slot = inventorySlots[i];
                if (slot.isHovered()) {
                    Stack slotStack = stacks.get(i);
                    if (heldStack == null) {
                        if (slotStack != null) {
                            heldStack = slotStack;
                            stacks.set(i, null);
                            if (inventory != null) inventory.setSlot(i, null);
                        }
                    } else {
                        if (slotStack == null) {
                            stacks.set(i, heldStack);
                            if (inventory != null) inventory.setSlot(i, heldStack);
                            heldStack = null;
                        } else if (slotStack.getItemId().equals(heldStack.getItemId())) {
                            int space = slotStack.getMaxStackSize() - slotStack.getAmount();
                            if (space >= heldStack.getAmount()) {
                                slotStack.setQuantity(slotStack.getAmount() + heldStack.getAmount());
                                if (inventory != null) inventory.setSlot(i, slotStack);
                                heldStack = null;
                            } else if (space > 0) {
                                slotStack.setQuantity(slotStack.getAmount() + space);
                                if (inventory != null) inventory.setSlot(i, slotStack);
                                heldStack.setQuantity(heldStack.getAmount() - space);
                            } else {
                                stacks.set(i, heldStack);
                                if (inventory != null) inventory.setSlot(i, heldStack);
                                heldStack = slotStack;
                            }
                        } else {
                            stacks.set(i, heldStack);
                            if (inventory != null) inventory.setSlot(i, heldStack);
                            heldStack = slotStack;
                        }
                    }
                    break;
                }
            }
            
            double mx = App.getInputHandler().getMouseX();
            double my = App.getInputHandler().getMouseY();
            for (int i = 0; i < hotbar.itemSlots.size(); i++) {
                ui.UIItemSlot hs = hotbar.itemSlots.get(i);
                float hx = (i * (slotSize + padding)) + xOffset;
                float hy = App.getWindowHeight() - (yOffset + slotSize) + 10;
                boolean hover = mx >= hx && mx <= hx + slotSize && my >= hy && my <= hy + slotSize;
                if (hover) {
                    Stack hot = hs.getStack();
                    if (heldStack == null) {
                        if (hot != null) {
                            heldStack = hot;
                            hs.setStack(null);
                        }
                    } else {
                        if (hot == null) {
                            hs.setStack(heldStack);
                            heldStack = null;
                        } else if (hot.getItemId().equals(heldStack.getItemId())) {
                            int space = hot.getMaxStackSize() - hot.getAmount();
                            if (space >= heldStack.getAmount()) {
                                hot.setQuantity(hot.getAmount() + heldStack.getAmount());
                                if (inventory != null) {
                                    // update hotbar slot in inventory if needed
                                }
                                heldStack = null;
                            } else if (space > 0) {
                                hot.setQuantity(hot.getAmount() + space);
                                heldStack.setQuantity(heldStack.getAmount() - space);
                            } else {
                                hs.setStack(heldStack);
                                heldStack = hot;
                            }
                        } else {
                            hs.setStack(heldStack);
                            heldStack = hot;
                        }
                    }
                    break;
                }
            }
        }
        lastMouseDown = mouseDown;

        boolean rightDown = App.getInputHandler().isMouseDown(GLFW_MOUSE_BUTTON_RIGHT);
        if (rightDown && !lastRightDown) {
            for (int i = 0; i < slots; i++) {
                UIInventorySlot slot = inventorySlots[i];
                if (slot.isHovered()) {
                    Stack slotStack = stacks.get(i);
                    if (heldStack == null) {
                        if (slotStack != null) {
                            int take = (slotStack.getAmount() + 1) / 2;
                            heldStack = new Stack(slotStack.getItemId(), take);
                            slotStack.setQuantity(slotStack.getAmount() - take);
                            if (slotStack.getAmount() <= 0) {
                                stacks.set(i, null);
                                if (inventory != null) inventory.setSlot(i, null);
                            } else {
                                if (inventory != null) inventory.setSlot(i, slotStack);
                            }
                        }
                    } else {
                        if (slotStack == null) {
                            stacks.set(i, new Stack(heldStack.getItemId(), 1));
                            if (inventory != null) inventory.setSlot(i, stacks.get(i));
                            heldStack.setQuantity(heldStack.getAmount() - 1);
                            if (heldStack.getAmount() <= 0) heldStack = null;
                        } else if (slotStack.getItemId().equals(heldStack.getItemId())) {
                            if (slotStack.getAmount() < slotStack.getMaxStackSize()) {
                                slotStack.setQuantity(slotStack.getAmount() + 1);
                                if (inventory != null) inventory.setSlot(i, slotStack);
                                heldStack.setQuantity(heldStack.getAmount() - 1);
                                if (heldStack.getAmount() <= 0) heldStack = null;
                            }
                        }
                    }
                    break;
                }
            }

            double mx = App.getInputHandler().getMouseX();
            double my = App.getInputHandler().getMouseY();
            for (int i = 0; i < hotbar.itemSlots.size(); i++) {
                ui.UIItemSlot hs = hotbar.itemSlots.get(i);
                float hx = (i * (slotSize + padding)) + xOffset;
                float hy = App.getWindowHeight() - (yOffset + slotSize) + 10;
                boolean hover = mx >= hx && mx <= hx + slotSize && my >= hy && my <= hy + slotSize;
                if (!hover) continue;

                Stack hot = hs.getStack();
                if (heldStack == null) {
                    if (hot != null) {
                        int take = (hot.getAmount() + 1) / 2;
                        heldStack = new Stack(hot.getItemId(), take);
                        hot.setQuantity(hot.getAmount() - take);
                        if (hot.getAmount() <= 0) {
                            hs.setStack(null);
                        } else {
                            hs.setStack(hot);
                        }
                    }
                } else {
                    if (hot == null) {
                        hs.setStack(new Stack(heldStack.getItemId(), 1));
                        heldStack.setQuantity(heldStack.getAmount() - 1);
                        if (heldStack.getAmount() <= 0) heldStack = null;
                    } else if (hot.getItemId().equals(heldStack.getItemId())) {
                        if (hot.getAmount() < hot.getMaxStackSize()) {
                            hot.setQuantity(hot.getAmount() + 1);
                            hs.setStack(hot);
                            heldStack.setQuantity(heldStack.getAmount() - 1);
                            if (heldStack.getAmount() <= 0) heldStack = null;
                        }
                    }
                }
                break;
            }
        }
        lastRightDown = rightDown;
    }

    @Override
    public void onClick(double mx, double my) {
        return;
    }

    @Override
    public void render() {
        drawRect(0, 0, 500, App.getWindowHeight(), 100, 100, 100, 150);

        for (int i = 0; i < slots; i++) {
            UIInventorySlot slot = inventorySlots[i];
            Stack s = stacks.get(i);
            if (s != null) {
                slot.itemId = s.getItemId();
                slot.quantity = s.getAmount();
            } else {
                slot.itemId = null;
                slot.quantity = 0;
            }
            slot.render();
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

        if (heldStack != null && heldStack.getItem() != null && heldStack.getItem().getImage() != null) {
            double mx = App.getInputHandler().getMouseX();
            double my = App.getInputHandler().getMouseY();
            drawImage(heldStack.getItem().getImage(), (float)mx - slotSize/2, (float)my - slotSize/2, slotSize, slotSize);
            drawText(heldStack.getAmount() + "", (float)mx + slotSize/2 - 20, (float)my + slotSize/2 - 20, 15);
        }
    }    
}
