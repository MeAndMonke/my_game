package ui;

import org.w3c.dom.css.RGBColor;

import items.Item;
import items.Stack;

import static org.lwjgl.opengl.GL11.glColor4f;

public class UIItemSlot extends UIElement {
    private Item item;
    private Stack stack;
    private boolean equipped;
    
    public UIItemSlot(float x, float y, float size) {
        this.x = x;
        this.y = y;
        this.width = size;
        this.height = size;
    }

    public void setStack(Stack stack) {
        this.stack = stack;
    }

    public Stack getStack() {
        return stack;
    }

    public boolean isEquipped() {
        return equipped;
    }

    public void setEquipped(boolean equipped) {
        this.equipped = equipped;
    }

    @Override
    public void update(float dt) {
        return;
    }

    @Override
    public void render() {
        if (equipped) {
            drawRect(x - 5, y - 5, width + 10, height + 10, 255, 215, 0, 255);
        }
        drawRect(x, y, width, height, 100, 100, 100, 200);

        // `setItem` stores a Stack in the `stack` field; `item` is unused.
        // Check `stack` (and that it contains a valid Item/image) before drawing.
        if (stack != null && stack.getItem() != null && stack.getItem().getImage() != null) {
            drawImage(stack.getItem().getImage(), x + 10, y + 10, width - 20, height - 20);
            drawText(stack.getAmount() + "", x + width - 20, y + height - 20, 15);
        }
    }

    @Override
    public void onClick(double mx, double my) {
        return;
    }

}
