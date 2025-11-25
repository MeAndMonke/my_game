package entity;

public class Drop {
    public String itemId;
    public int min;
    public int max;
    public double chance;

    public Drop(String itemId, int min, int max, double chance) {
        this.itemId = itemId;
        this.min = min;
        this.max = max;
        this.chance = chance;
    }
}