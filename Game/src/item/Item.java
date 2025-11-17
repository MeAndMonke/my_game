package item;

public class Item {
    public int id = 0;
    public String name;
    public String description;
    public int amount;
    public int maxStackSize;

    public Item(int id, String name, String description, int maxStackSize) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.amount = 0;
        this.maxStackSize = maxStackSize;
    }
}
