package items;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.json.JSONArray;

public class Item {
    private String id, name, description, texture, rarity;
    private double weight, value;
    private JSONArray recipe;
    private boolean placeable = false;
    private String modelId = null;

    private BufferedImage imageCache = null;
    private int maxStackSize;

    public Item(String id, String name, Integer maxStackSize, String description, String texture, double weight, double value, String rarity, JSONArray recipe, boolean placeable, String modelId) {
        this.id = id;
        this.name = name;
        this.maxStackSize = maxStackSize;
        this.description = description;
        this.texture = texture;
        this.weight = weight;
        this.value = value;
        this.rarity = rarity;
        this.recipe = recipe;
        this.placeable = placeable;
        this.modelId = modelId;

        try {
            imageCache = ImageIO.read(new File(texture));
        } catch (IOException e) {
            e.printStackTrace();
            imageCache = null;
        }

    }

    public boolean isPlaceable() { return placeable; }
    public String getModelId() { return modelId; }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getTexture() { return texture; }
    public double getWeight() { return weight; }
    public double getValue() { return value; }
    public String getRarity() { return rarity; }
    public JSONArray getRecipe() { return recipe; }
    public BufferedImage getImage() { return imageCache; }
    public int getMaxStackSize() { return maxStackSize; }
}
