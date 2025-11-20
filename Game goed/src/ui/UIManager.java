package ui;

import java.util.ArrayList;
import java.util.List;


public class UIManager {

    private List<UIElement> elements = new ArrayList<>();

    public void add(UIElement e) {
        elements.add(e);
    }

    public void update(float dt) {
        for (UIElement e : elements) {
            e.update(dt);
        }
    }

    public void render() {
        for (UIElement e : elements) {
            e.render();
        }
    }

    public void onClick(double mx, double my) {
        for (UIElement e : elements) {
            e.onClick(mx, my);
        }
    }
}
