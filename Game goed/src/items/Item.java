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

    private BufferedImage imageCache = null;

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

        try {
            imageCache = ImageIO.read(new File(texture)); // texture is the path from JSON
        } catch (IOException e) {
            e.printStackTrace();
            imageCache = null; // fallback if the image failed to load
        }

    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getTexture() { return texture; }
    public double getWeight() { return weight; }
    public double getValue() { return value; }
    public String getRarity() { return rarity; }
    public JSONArray getRecipe() { return recipe; }
    public BufferedImage getImage() { return imageCache; }
}
