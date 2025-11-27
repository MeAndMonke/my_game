package entity;

import java.io.FileInputStream;
import org.joml.Vector3f;
import renderer.Model;
import renderer.Shader;
import renderer.Texture;
import org.json.JSONObject;
import org.json.JSONTokener;
import physics.CollisionBox;

import java.util.ArrayList;
import java.util.List;


public class ConfigLoader {

    public static Model loadModel(String configPath, Shader shader, Vector3f position, Vector3f rotation) {
        try (FileInputStream fis = new FileInputStream(configPath)) {
            JSONObject json = new JSONObject(new JSONTokener(fis));

            String modelPath = json.getString("modelPath");
            String texturePath = json.getString("texturePath");
            float scale = json.getFloat("scale");

            return new Model(modelPath, shader, position, rotation, scale, new Texture(texturePath));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static CollisionBox loadCollisionBox(String collisionPath, Vector3f position) {
        try (FileInputStream fis = new FileInputStream(collisionPath)) {
            JSONObject json = new JSONObject(new JSONTokener(fis));

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
        try (FileInputStream fis = new FileInputStream(configPath)) {
            JSONObject json = new JSONObject(new JSONTokener(fis));
            return json.getString("collisionPath");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean getInteractable(String configPath) {
        try {
            FileInputStream fis = new FileInputStream(configPath);
            JSONObject json = new JSONObject(new JSONTokener(fis));
            return json.getBoolean("interactable");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getInteractableType(String configPath) {
        try {
            FileInputStream fis = new FileInputStream(configPath);
            JSONObject json = new JSONObject(new JSONTokener(fis));
            return json.getString("interactableType");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String[] getToolsRequired(String configPath) {
        try {
            FileInputStream fis = new FileInputStream(configPath);
            JSONObject json = new JSONObject(new JSONTokener(fis));
            var jsonArray = json.getJSONArray("toolsRequired");
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
        try (FileInputStream fis = new FileInputStream(collisionPath)) {
            JSONObject json = new JSONObject(new JSONTokener(fis));

            float interactionWidth = json.getFloat("interactionWidth");
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
        try (FileInputStream fis = new FileInputStream(configPath)) {
            JSONObject json = new JSONObject(new JSONTokener(fis));
            return json.getFloat("scale");
        } catch (Exception e) {
            e.printStackTrace();
            return 1.0f;
        }
    }

    public static List<Drop> getDrops(String configPath) {
        List<Drop> drops = new ArrayList<>();
        
        try (FileInputStream fis = new FileInputStream(configPath)) {
            JSONObject json = new JSONObject(new JSONTokener(fis));
            JSONObject dropsObj = json.getJSONObject("drops");

            for (String key : dropsObj.keySet()) {
                JSONObject dropData = dropsObj.getJSONObject(key);

                int min = dropData.getInt("min");
                int max = dropData.getInt("max");
                double chance = dropData.getDouble("chance");

                drops.add(new Drop(key, min, max, chance));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return drops;
    }
}
