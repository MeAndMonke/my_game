package items;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class ItemManager {

    private List<Item> items;

    public ItemManager(String configPath) {
        items = new ArrayList<>();
        loadItemData(configPath);
    }

    private void loadItemData(String configPath) {
        try (FileInputStream fis = new FileInputStream(configPath)) {
            JSONTokener tokener = new JSONTokener(fis);
            JSONArray array = new JSONArray(tokener);

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                Item item = new Item(
                    obj.getString("id"),
                    obj.getString("name"),
                    obj.getInt("maxStackSize"),
                    obj.getString("description"),
                    obj.getString("texture"),
                    obj.getDouble("weight"),
                    obj.getDouble("value"),
                    obj.getString("rarity"),
                    obj.optJSONArray("recipe")
                );
                items.add(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Item> getItems() {
        return items;
    }

    public Item getItemById(String id) {
        for (Item item : items) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }
}
