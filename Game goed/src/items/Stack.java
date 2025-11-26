package items;

import core.App;

public class Stack {    
    private String itemId;
    private int quantity = 0;
    private int maxStackSize;

    private ItemManager itemManager = App.itemManager;
    

    public Stack(String itemId, int quantity) {
        this.itemId = itemId;
        this.maxStackSize = itemManager.getItemById(itemId).getMaxStackSize();
        this.quantity = quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getMaxStackSize() {
        return maxStackSize;
    }

    public String getItemId() {
        return itemId;
    }

    public Item getItem() {
        return itemManager.getItemById(itemId);
    }
    
}
