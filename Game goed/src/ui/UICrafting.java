package ui;

import gameplay.CraftingManager;
import gameplay.CraftingManager.Recipe;
import gameplay.Inventory;
import entity.Player;
import core.App;
import static org.lwjgl.glfw.GLFW.*;
import items.Item;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ArrayList;

public class UICrafting extends UIElement {

    private CraftingManager craftingManager;
    private Inventory inventory;
    private Player player;
    private UIManager uiManager;
    private List<Recipe> recipes = new ArrayList<>();

    private float xOffset = 20;
    private float yOffset = 50;
    private float buttonHeight = 64;
    private float padding = 10;

    private float scrollY = 0f;
    private boolean lastMouseDown = false;
    private double lastMouseY = 0;
    private boolean dragging = false;
    private int hoveredIndex = -1;

    public UICrafting(CraftingManager craftingManager, Inventory inventory, Player player, UIManager uiManager) {
        this.craftingManager = craftingManager;
        this.inventory = inventory;
        this.player = player;
        this.uiManager = uiManager;
        if (craftingManager != null) recipes = craftingManager.getRecipes();
    }

    @Override
    public void update(float dt) {
        if (!uiManager.isCraftingOpen()) return;

        double mx = App.getInputHandler().getMouseX();
        double my = App.getInputHandler().getMouseY();

        // handle scrolling and hovering
        boolean mouseDown = App.getInputHandler().isMouseDown(GLFW_MOUSE_BUTTON_LEFT);
        if (mouseDown && !lastMouseDown) {
            lastMouseY = my;
            dragging = true;
        } else if (!mouseDown && lastMouseDown) {
            if (dragging) {
                double dy = my - lastMouseY;
                if (Math.abs(dy) < 5) {
                    // if another UI element consumed the click
                    if (uiManager.isClickConsumed()) {
                        uiManager.clearClickConsumed();
                    } else {
                        onClick(mx, my);
                    }
                }
            }
            dragging = false;
        }

        // continue dragging
        if (mouseDown && dragging) {
            double dy = my - lastMouseY;
            scrollY -= dy;
            lastMouseY = my;
        }

        // clamp scrollY
        float contentHeight = recipes.size() * (buttonHeight + padding);
        if (scrollY < 0) scrollY = 0;
        float panelHeight = App.getWindowHeight();
        float innerHeight = panelHeight - yOffset * 2;
        float maxScroll = Math.max(0, contentHeight - innerHeight);
        if (scrollY > maxScroll) scrollY = maxScroll;

        hoveredIndex = -1;
        for (int i = 0; i < recipes.size(); i++) {
            float rx = xOffset;
            float ry = yOffset + i * (buttonHeight + padding) - scrollY;
            if (mx >= rx && mx <= rx + (500f - xOffset * 2) && my >= ry && my <= ry + buttonHeight) {
                hoveredIndex = i;
                break;
            }
        }

        lastMouseDown = mouseDown;
    }

    @Override
    public void render() {
        float panelWidth = 500f;
        float panelHeight = App.getWindowHeight();
        drawRect(0, 0, panelWidth, panelHeight, new Color4(80, 80, 80, 200));

        float btnW = panelWidth - xOffset * 2;

        // draw recipe buttons
        for (int i = 0; i < recipes.size(); i++) {
            Recipe r = recipes.get(i);
            float rx = xOffset;
            float ry = yOffset + i * (buttonHeight + padding) - scrollY;

            if (ry + buttonHeight < 0 || ry > panelHeight) continue;

            // hover effect
            boolean hover = (i == hoveredIndex);
            boolean canCraft = craftingManager.canCraft(inventory, r, player.getLevel());

            if (hover) {
                drawRect(rx - 2, ry - 2, btnW + 4, buttonHeight + 4, new Color4(200, 200, 200, 80));
            }

            // button background
            if (!canCraft) {
                drawRect(rx, ry, btnW, buttonHeight, new Color4(80, 80, 80, 200));
            } else {
                drawRect(rx, ry, btnW, buttonHeight, new Color4(140, 140, 140, 220));
            }

            // draw item image
            BufferedImage img = null;
            Item item = App.itemManager.getItemById(r.id);
            if (item != null) img = item.getImage();

            float imgSize = buttonHeight - 12;
            float imgX = rx + 6;
            float imgY = ry + 6;
            if (img != null) {
                drawImage(img, imgX, imgY, imgSize, imgSize);
            } else if (r.texture != null) {
                try {
                    java.awt.image.BufferedImage tmp = javax.imageio.ImageIO.read(new java.io.File(r.texture));
                    if (tmp != null) drawImage(tmp, imgX, imgY, imgSize, imgSize);
                } catch (Exception e) {
                }
            }

            // draw name and ingredients
            String name = r.name != null ? r.name : r.id;
            drawText(name, imgX + imgSize + 10, ry + (buttonHeight / 2f) - 10, 20);

            String reqs = "";
            int ci = 0;
            for (java.util.Map.Entry<String,Integer> en : r.ingredients.entrySet()) {
                if (ci > 0) reqs += ", ";
                reqs += en.getValue() + "x " + en.getKey();
                ci++;
                if (ci > 2) break;
            }
            drawText(reqs, imgX + imgSize + 10, ry + (buttonHeight / 2f) + 10, 14);

            // overlay if cannot craft
            if (!canCraft) {
                drawRect(rx, ry, btnW, buttonHeight, new Color4(0, 0, 0, 120));
            }
        }
    }

    @Override
    public void onClick(double mx, double my) {
        if (!uiManager.isCraftingOpen()) return;

        // check recipe buttons
        for (int i = 0; i < recipes.size(); i++) {
            Recipe r = recipes.get(i);
            float rx = xOffset;
            float ry = yOffset + i * (buttonHeight + padding) - scrollY;
            float btnW = 500f - xOffset * 2;

            // click inside button
            if (mx >= rx && mx <= rx + btnW && my >= ry && my <= ry + buttonHeight) {
                UICraftItem detailElem = uiManager.getCraftItemElement();
                if (detailElem == null) {
                    detailElem = new UICraftItem(craftingManager, inventory, player, uiManager);
                    uiManager.add(detailElem);
                }
                detailElem.open(r);
                break;
            }
        }
    }
}
