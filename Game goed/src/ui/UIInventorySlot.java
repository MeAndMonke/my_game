package ui;

import core.App;
import core.InputHandler;
import items.ItemManager;

public class UIInventorySlot extends UIElement {

    public float x, y, size;
    public String itemId;
    public int quantity;

    private boolean hovered;
    private ItemManager itemManager = App.itemManager;
    private InputHandler inputHandler = App.getInputHandler();

    public UIInventorySlot(float x, float y, float size) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.itemId = null;
        this.quantity = 0;
    }

    public void render() {
        if (hovered) {
            drawRect(x - 2, y - 2, size + 4, size + 4, 150, 150, 150, 255);
        } else {
            drawRect(x - 2, y - 2, size + 4, size + 4, 120, 120, 120, 255);
        }
        if (itemId != null) {
            drawImage(
                itemManager.getItemById(itemId).getImage(),
                x + 10,
                y + 10,
                size - 20,
                size - 20
            );
            drawText(quantity + "", x + size - 30, y + size - 30, 15);
        }
    }

    public boolean isHovered() { return hovered; }

    @Override
    public void update(float dt) {
        double mx = inputHandler.getMouseX();
        double my = inputHandler.getMouseY();
        hovered = mx >= x && mx <= x + size &&
            my >= y && my <= y + size;
    }

    @Override
    public void onClick(double mx, double my) {
        if (mx >= x && mx <= x + size && my >= y && my <= y + size) {
            System.out.println("Clicked on inventory slot with item: " + itemId);
        }
    }
    
}
