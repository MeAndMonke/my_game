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

    public static final ModelHandler modelHandler = new ModelHandler();

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

    // New: load collision directly from the model config (embedded collision object)
    public static CollisionBox loadCollisionBoxFromConfig(String configPath, Vector3f position) {
        try {
            JSONObject json = resolveConfig(configPath);

            JSONObject coll = json.optJSONObject("collision");
            if (coll != null) {
                float width = coll.optFloat("width", 1.0f);
                float height = coll.optFloat("height", 1.0f);
                float offsetX = coll.optFloat("offsetX", 0f);
                float offsetY = coll.optFloat("offsetY", 0f);
                return new CollisionBox(position, width, height, offsetX, offsetY);
            }

            // Fallback: if the model config references an external collision path, use it
            String collisionPath = json.optString("collisionPath", null);
            if (collisionPath != null && !collisionPath.isEmpty()) {
                return loadCollisionBox(collisionPath, position);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

    // New: load interaction box from the model config's embedded collision object
    public static CollisionBox loadInteractionBoxFromConfig(String configPath, Vector3f position) {
        try {
            JSONObject json = resolveConfig(configPath);

            JSONObject coll = json.optJSONObject("collision");
            if (coll != null) {
                float interactionWidth = coll.optFloat("interactionWidth", 0f);
                float width = coll.optFloat("width", 1.0f);
                float height = coll.optFloat("height", 1.0f);
                float offsetX = coll.optFloat("offsetX", 0f);
                float offsetY = coll.optFloat("offsetY", 0f);
                return new CollisionBox(position, width + interactionWidth, height + interactionWidth, offsetX, offsetY);
            }

            // Fallback to external collision path if present
            String collisionPath = json.optString("collisionPath", null);
            if (collisionPath != null && !collisionPath.isEmpty()) {
                return loadInteractionBox(collisionPath, position);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

    private static JSONObject resolveConfig(String configPath) throws Exception {
        try (FileInputStream fis = new FileInputStream(configPath)) {
            return new JSONObject(new JSONTokener(fis));
        } catch (Exception e) {
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
