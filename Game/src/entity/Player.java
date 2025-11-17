package entity;

import item.Item;
import java.util.List;
import main.Shader;
import org.joml.Vector3f;

public class Player extends Entity {
    private int food;
    private int water;
    
    private int stamina;

    private List<Item> inventory;

    public Player(Shader shader) {
        super("res/models/model.obj", 100, new Vector3f(0,0,0), new Vector3f(80,0,0), shader);
        this.food = 100;
        this.water = 100;
        
        this.stamina = 20;
    }

    public int getFood() {
        return food;
    }

    public void setFood(int food) {
        this.food = food;
    }

    public int getWater() {
        return water;
    }

    public void setWater(int water) {
        this.water = water;
    }

    public int getStamina() {
        return stamina;
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
    }

    public List<Item> getInventory() {
        return inventory;
    }

    public void setInventory(List<Item> inventory) {
        this.inventory = inventory;
    }

    /**
     * Adds an item to the player's inventory.
     * @param itemId The ID of the item to add.
     * @return true if the item was added, false if inventory is full.
     */
    public boolean addToInventory(int itemId) {
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).id == 0) {
                inventory.get(i).id = itemId;
                return true;
            }
        }
        return false;
    }

    /**
     * Removes an item from the player's inventory.
     * @param itemId The ID of the item to remove.
     * @return true if the item was removed, false if not found.
     */
    public boolean removeFromInventory(int itemId) {
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).id == itemId) {
                inventory.get(i).id = 0;
                inventory.get(i).amount = 0;
                return true;
            }
        }
        return false; // Item not found
    }

}
