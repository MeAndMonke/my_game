package ui;

import gameplay.CraftingManager;
import gameplay.CraftingManager.Recipe;
import gameplay.Inventory;
import entity.Player;
import core.App;
import items.Item;

import java.awt.image.BufferedImage;

public class UICraftItem extends UIElement {

    private CraftingManager craftingManager;
    private Inventory inventory;
    private Player player;
    private UIManager uiManager;

    private Recipe recipe = null;
    private boolean open = false;

    private float panelX = 20;
    private float panelY = 220;
    private float panelW = 460;
    private float panelH = 300;

    public UICraftItem(CraftingManager cm, Inventory inv, Player player, UIManager uiManager) {
        this.craftingManager = cm;
        this.inventory = inv;
        this.player = player;
        this.uiManager = uiManager;
    }

    public void open(Recipe r) {
        this.recipe = r;
        this.open = true;
    }

    public void close() {
        this.recipe = null;
        this.open = false;
    }

    public boolean isOpen() { return open; }

    @Override
    public void update(float dt) {
        if (!open) return;
        // nothing for now
    }

    @Override
    public void render() {
        if (!open || recipe == null) return;

        drawRect(panelX, panelY, panelW, panelH, new Color4(60, 60, 60, 230));

        // item image
        Item item = App.itemManager.getItemById(recipe.id);
        BufferedImage img = item != null ? item.getImage() : null;
        float imgSize = 96;
        float imgX = panelX + 12;
        float imgY = panelY + 12;
        if (img != null) drawImage(img, imgX, imgY, imgSize, imgSize);

        // name and description
        String name = recipe.name != null ? recipe.name : recipe.id;
        drawText(name, imgX + imgSize + 12, imgY + 20, 24);

        String desc = "";
        if (item != null) desc = item.getDescription();
        drawText(desc, imgX + imgSize + 12, imgY + 50, 14);

        // ingredients list
        float ingX = panelX + 12;
        float ingY = imgY + imgSize + 12;
        drawText("Ingredients:", ingX, ingY, 18);
        ingY += 22;
        for (java.util.Map.Entry<String,Integer> e : recipe.ingredients.entrySet()) {
            String line = e.getValue() + " x " + e.getKey();
            drawText(line, ingX + 8, ingY, 16);
            ingY += 20;
        }

        // craft button
        float btnW = 120;
        float btnH = 36;
        float btnX = panelX + panelW - btnW - 12;
        float btnY = panelY + panelH - btnH - 12;

        boolean canCraft = craftingManager.canCraft(inventory, recipe, player.getLevel());
        if (canCraft) drawRect(btnX, btnY, btnW, btnH, new Color4(100, 180, 100, 220));
        else drawRect(btnX, btnY, btnW, btnH, new Color4(120, 120, 120, 200));

        drawText("Craft", btnX + 30, btnY + 6, 18);

        // close button (small X)
        float xSize = 32;
        float xX = panelX + panelW - xSize - 8;
        float xY = panelY + 8;
        drawRect(xX, xY, xSize, xSize, new Color4(200, 80, 80, 220));
        drawText("X", xX + 12, xY + 6, 14);
    }

    @Override
    public void onClick(double mx, double my) {
        if (!open || recipe == null) return;

        // close button
        float xSize = 32;
        float xX = panelX + panelW - xSize - 8;
        float xY = panelY + 8;
        if (mx >= xX && mx <= xX + xSize && my >= xY && my <= xY + xSize) {
            close();
            uiManager.consumeClick();
            return;
        }

        // craft button
        float btnW = 120;
        float btnH = 36;
        float btnX = panelX + panelW - btnW - 12;
        float btnY = panelY + panelH - btnH - 12;
        if (mx >= btnX && mx <= btnX + btnW && my >= btnY && my <= btnY + btnH) {
            if (craftingManager.canCraft(inventory, recipe, player.getLevel())) {
                craftingManager.craft(inventory, recipe);
                // after crafting, close detail view
                close();
                uiManager.consumeClick();
            } else {
                // maybe flash or show message later
            }
        }
    }
}
