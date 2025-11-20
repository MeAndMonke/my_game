package entity;

import java.io.FileInputStream;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.json.JSONObject;
import org.json.JSONTokener;

import physics.CollisionBox;
import renderer.Model;
import renderer.Shader;
import renderer.Texture;

public class Entity {
    private Model model;

    private Vector3f position;
    private Vector3f rotation;
    private float scale;
    
    private CollisionBox collisionBox;


    public Entity(Vector3f position, Shader shader, String configPath) {
        this.position = position;
        this.rotation = new Vector3f(0, 100, 0);
        this.scale = 1;
        loadConfigData(configPath, shader);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public CollisionBox getCollisionBox() {
        return collisionBox;
    }

    public void render(Matrix4f view, Matrix4f projection) {
        model.setPosition(position);
        model.setRotation(rotation);
        model.setScale(scale);
        model.render(view, projection);
    }

    private void loadConfigData(String configPath, Shader shader) {
        String collisionPath = null;
        try (FileInputStream fis = new FileInputStream(configPath)) {
            JSONObject json = new JSONObject(new JSONTokener(fis));

            String modelPath = json.getString("modelPath");
            String texturePath = json.getString("texturePath");
            collisionPath = json.getString("collisionPath");

            this.scale = json.getFloat("scale");

            this.model = new Model(modelPath, shader, position, rotation, scale, new Texture(texturePath));
        } 
        catch (Exception e) {
            e.printStackTrace();
        }

        try (FileInputStream fis = new FileInputStream(collisionPath)) {
            JSONObject json = new JSONObject(new JSONTokener(fis));
            float width = json.getFloat("width");
            float height = json.getFloat("height");
            float offsetX = json.getFloat("offsetX");
            float offsetY = json.getFloat("offsetY");

            this.collisionBox = new CollisionBox(position, width, height, offsetX, offsetY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
