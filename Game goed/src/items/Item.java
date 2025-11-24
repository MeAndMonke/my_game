package items;

import org.json.JSONArray;

public class Item {
    private String id, name, description, texture, rarity;
    private double weight, value;
    private JSONArray recipe;

    public Item(String id, String name, String description, String texture, double weight,
                double value, String rarity, JSONArray recipe) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.texture = texture;
        this.weight = weight;
        this.value = value;
        this.rarity = rarity;
        this.recipe = recipe;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getTexture() { return texture; }
    public double getWeight() { return weight; }
    public double getValue() { return value; }
    public String getRarity() { return rarity; }
    public JSONArray getRecipe() { return recipe; }
}
