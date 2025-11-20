package entity;

import java.io.FileInputStream;
import org.joml.Vector3f;
import renderer.Model;
import renderer.Shader;
import renderer.Texture;
import org.json.JSONObject;
import org.json.JSONTokener;
import physics.CollisionBox;


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
}
