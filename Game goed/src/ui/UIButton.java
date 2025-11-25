package ui;

import core.App;
import core.InputHandler;

import java.awt.Color;

public class UIButton extends UIElement {
    private String text;
    private boolean hovered;
    private InputHandler inputHandler = App.getInputHandler();

    public UIButton(float x, float y, float w, float h, String text) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.text = text;
    }

    @Override
    public void update(float dt) {
        double mx = inputHandler.getMouseX();
        double my = inputHandler.getMouseY();
        hovered = mx >= x && mx <= x + width &&
                my >= y && my <= y + height;
    }

    @Override
    public void render() {
        if (hovered) {
            drawRect(x, y, width, height, 90, 90, 90, 255);
        } else {       
            drawRect(x, y, width, height, 64, 64, 64, 255);
        }

        drawText(text, x + 10, y + 10);
    }

    @Override
    public void onClick(double mx, double my) {
        if (hovered) {
            System.out.println("button clicked");
        }
    }
}
