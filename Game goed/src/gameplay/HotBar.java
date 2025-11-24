package gameplay;

import ui.UIManager;
import core.App;
import ui.UIItemSlot;

public class HotBar {

    private UIManager uiManager;

    private int screenWidth = App.getWindowWidth();
    private int screenHeight = App.getWindowHeight();

    private int itemSlotSize = 100;
    private int padding = 10;

    public HotBar(UIManager uiManager) {
        this.uiManager = uiManager;
    }

    public void loadHotbar() {
        for (int i = 0; i < 5; i++) {
            float x = (screenWidth / 2) - ((5 * itemSlotSize + 4 * padding) / 2) + i * (itemSlotSize + padding);
            float y = screenHeight - (20 + itemSlotSize);
            UIItemSlot itemSlot = new UIItemSlot(x, y, itemSlotSize);
            uiManager.add(itemSlot);
        }
    }
}
