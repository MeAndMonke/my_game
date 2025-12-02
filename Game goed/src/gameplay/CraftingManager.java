package gameplay;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CraftingManager {

    public static class Recipe {
        public final String id;
        public final String name;
        public final String texture;
        public final Map<String, Integer> ingredients = new HashMap<>();
        public final int levelRequired;

        public Recipe(String id, String name, String texture, int levelRequired) {
            this.id = id;
            this.name = name;
            this.texture = texture;
            this.levelRequired = levelRequired;
        }
    }

    private final List<Recipe> recipes = new ArrayList<>();

    public CraftingManager(String itemsJsonPath) {
        loadRecipes(itemsJsonPath);
    }

    private void loadRecipes(String path) {
        try (FileInputStream fis = new FileInputStream(path)) {
            JSONArray arr = new JSONArray(new JSONTokener(fis));
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                if (obj.isNull("recipe")) continue;
                String id = obj.getString("id");
                String name = obj.optString("name", id);
                String texture = obj.optString("texture", null);
                int levelReq = obj.optInt("level_required", 0);

                Recipe r = new Recipe(id, name, texture, levelReq);
                JSONObject rec = obj.getJSONObject("recipe");
                for (String key : rec.keySet()) {
                    int qty = rec.getInt(key);
                    r.ingredients.put(key, qty);
                }
                recipes.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Recipe> getRecipes() { return recipes; }

    public boolean canCraft(Inventory inv, Recipe r, int playerLevel) {
        if (playerLevel < r.levelRequired) return false;
        for (Map.Entry<String,Integer> e : r.ingredients.entrySet()) {
            String itemId = e.getKey();
            int need = e.getValue();
            if (!inv.hasItemQuantity(itemId, need)) return false;
        }
        return true;
    }

    public boolean craft(Inventory inv, Recipe r) {
        for (Map.Entry<String,Integer> e : r.ingredients.entrySet()) {
            if (!inv.removeItemQuantity(e.getKey(), e.getValue())) {
                return false;
            }
        }
        inv.addItem(new items.Stack(r.id, 1));
        return true;
    }
}
