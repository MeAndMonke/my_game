package entity;

import java.io.FileInputStream;
import java.nio.file.Paths;
import org.joml.Vector3f;
import renderer.Model;
import renderer.Shader;
import renderer.Texture;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONTokener;
import physics.CollisionBox;

import java.util.ArrayList;
import java.util.List;
import items.Stack;


public class ConfigLoader {

    private static final ModelHandler modelHandler = new ModelHandler();

    public static Model loadModel(String configPath, Shader shader, Vector3f position, Vector3f rotation) {
        try {
            JSONObject json = resolveConfig(configPath);

            String modelPath = json.getString("modelPath");
            String texturePath = json.getString("texturePath");
            float scale = json.getFloat("scale");

            // Use ModelHandler cache so mesh/buffers are loaded once and instances reuse them
            return modelHandler.getModel(modelPath, shader, position, rotation, scale, new Texture(texturePath));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static CollisionBox loadCollisionBox(String collisionPath, Vector3f position) {
        try {
            JSONObject json = resolveConfig(collisionPath);

            float width = json.getFloat("width");
            float height = json.getFloat("height");
            float offsetX = json.getFloat("offsetX");
            float offsetY = json.getFloat("offsetY");

            return new CollisionBox(position, width, height, offsetX, offsetY);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getCollisionPath(String configPath) {
        try {
            JSONObject json = resolveConfig(configPath);
            return json.getString("collisionPath");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean getInteractable(String configPath) {
        try {
            JSONObject json = resolveConfig(configPath);
            return json.optBoolean("interactable", false);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getInteractableType(String configPath) {
        try {
            JSONObject json = resolveConfig(configPath);
            return json.optString("interactableType", null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String[] getToolsRequired(String configPath) {
        try {
            JSONObject json = resolveConfig(configPath);
            var jsonArray = json.optJSONArray("toolsRequired");
            if (jsonArray == null) return new String[0];
            String[] tools = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                tools[i] = jsonArray.getString(i);
            }
            return tools;
        } catch (Exception e) {
            e.printStackTrace();
            return new String[0];
        }
    }

    public static CollisionBox loadInteractionBox(String collisionPath, Vector3f position) {
        try {
            JSONObject json = resolveConfig(collisionPath);

            float interactionWidth = json.optFloat("interactionWidth", 0f);
            float width = json.getFloat("width");
            float height = json.getFloat("height");
            float offsetX = json.getFloat("offsetX");
            float offsetY = json.getFloat("offsetY");

            return new CollisionBox(position, width + interactionWidth, height + interactionWidth, offsetX, offsetY);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static float getScale(String configPath) {
        try {
            JSONObject json = resolveConfig(configPath);
            return json.optFloat("scale", 1.0f);
        } catch (Exception e) {
            e.printStackTrace();
            return 1.0f;
        }
    }

    public static List<Stack> getDrops(String configPath) {
        List<Stack> drops = new ArrayList<>();
        try {
            JSONObject json = resolveConfig(configPath);
            // Support two formats for "drops":
            // 1) Object mapping: { "itemId": {"min":1,"max":3,"chance":0.9}, ... }
            // 2) Array list: [ {"itemType":"id","quantity":n}, ... ]
            java.lang.Object dropsNode = json.opt("drops");
            if (dropsNode instanceof JSONObject) {
                JSONObject dropsObj = json.getJSONObject("drops");
                for (String key : dropsObj.keySet()) {
                    JSONObject dropData = dropsObj.getJSONObject(key);
                    int min = dropData.optInt("min", 0);
                    int max = dropData.optInt("max", min);
                    double chance = dropData.optDouble("chance", 1.0);

                    if (Math.random() <= chance) {
                        int qty = min;
                        if (max > min) {
                            qty += (int)(Math.random() * (max - min + 1));
                        }
                        drops.add(new Stack(key, qty));
                    }
                }
            } else if (dropsNode instanceof JSONArray) {
                JSONArray jsonArray = json.getJSONArray("drops");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject dropJson = jsonArray.getJSONObject(i);
                    String itemType = dropJson.getString("itemType");
                    int quantity = dropJson.getInt("quantity");
                    drops.add(new Stack(itemType, quantity));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return drops;
    }

    /**
     * Resolve a config JSON. If `configPath` points to a real file it is loaded directly.
     * Otherwise we try to find an entry in `res/models/model_configs.json` whose `id`
     * matches the filename (without extension) of `configPath`.
     */
    private static JSONObject resolveConfig(String configPath) throws Exception {
        try (FileInputStream fis = new FileInputStream(configPath)) {
            return new JSONObject(new JSONTokener(fis));
        } catch (Exception e) {
            // fallback to model_configs.json lookup by id (filename without extension)
            try (FileInputStream fis = new FileInputStream("res/models/model_configs.json")) {
                JSONArray arr = new JSONArray(new JSONTokener(fis));
                String fileName = Paths.get(configPath).getFileName().toString();
                String id = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    if (id.equals(obj.optString("id"))) return obj;
                }
            }
            throw new java.io.FileNotFoundException(configPath);
        }
    }
}
