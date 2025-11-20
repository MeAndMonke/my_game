package entity;

import java.io.FileInputStream;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import renderer.Model;
import renderer.Shader;
import renderer.Texture;

import org.json.JSONObject;
import org.json.JSONTokener;


public class Object {
    private Model model;

    private Vector3f position;
    private Vector3f rotation;
    private float scale;


    public Object(String configPath, Shader shader, Vector3f pos, Vector3f rot) {

        this.position = pos;
        this.rotation = rot;
        this.scale = 1;
        loadConfigData(configPath, shader);
    }

    private void loadConfigData(String configPath, Shader shader) {
        try (FileInputStream fis = new FileInputStream(configPath)) {
            JSONObject json = new JSONObject(new JSONTokener(fis));

            String modelPath = json.getString("modelPath");
            String texturePath = json.getString("texturePath");

            this.scale = json.getFloat("scale");

            this.model = new Model(modelPath, shader, position, rotation, scale, new Texture(texturePath));
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
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

    public void render(Matrix4f view, Matrix4f projection) {
        model.setPosition(position);
        model.setRotation(rotation);
        model.setScale(scale);
        model.render(view, projection);
    }
}
