package ui;

import items.Stack;

public class UIItemSlot extends UIElement {
    private Stack stack;
    private boolean equipped;
    
    public UIItemSlot(float x, float y, float size) {
        this.x = x;
        this.y = y;
        this.width = size;
        this.height = size;
    }

    public void setItem(Stack stack) {
        this.stack = stack;
    }

    public Stack getItem() {
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

        if (stack != null && stack.getItem() != null && stack.getItem().getImage() != null) {
            drawImage(stack.getItem().getImage(), x + 10, y + 10, width - 20, height - 20);
        }
    }

    @Override
    public void onClick(double mx, double my) {
        return;
    }

}
