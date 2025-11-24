package ui;

import items.Item;

public class UIItemSlot extends UIElement {
    private Item item;
    
    public UIItemSlot(float x, float y, float size) {
        this.x = x;
        this.y = y;
        this.width = size;
        this.height = size;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    @Override
    public void update(float dt) {
        return;
    }

    @Override
    public void render() {
        drawRect(x, y, width, height, java.awt.Color.LIGHT_GRAY);
        if (item != null) {
            drawText(item.getName(), x + 10, y + 20);
        }
    }

    @Override
    public void onClick(double mx, double my) {
        return;
    }

}
