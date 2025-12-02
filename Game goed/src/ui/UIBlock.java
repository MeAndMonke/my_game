package ui;



public class UIBlock extends UIElement {

    public float x, y, width, height;
    public int r, g, b, a;

    public UIBlock(float x, float y, float width, float height, int r, int g, int b, int a) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public void render() {
        drawRect(x, y, width, height,  new Color4(r, g, b, a));
    }

    @Override
    public void update(float dt) {
        return;
    }

    @Override
    public void onClick(double mx, double my) {
        return;
    }
}
