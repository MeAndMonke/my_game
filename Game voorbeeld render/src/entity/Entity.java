package entity;

import org.joml.Vector3f;

import main.ModelHandler;
import main.Shader;

import org.joml.Matrix4f;

public class Entity {
    private int health;
    private Vector3f position;
    private Vector3f rotation;
    private ModelHandler model;
    
    public Entity(String modelPath, int health, Vector3f position, Vector3f rotation, Shader shader) {
        this.health = health;
        this.position = position;
        this.rotation = rotation;

        shader.bind();

        shader.setVec3f("lightPos", main.App.cameraPos);
        shader.setVec3f("lightColor", new Vector3f(1f, 1f, 1f));
        shader.setVec3f("objectColor", new Vector3f(1f, 0.5f, 0.5f));

        this.model = new ModelHandler(modelPath, shader);
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
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

    public ModelHandler getModel() {
        return model;
    }

    public void render(Matrix4f view, Matrix4f projection) {

        model.setPosition(0, 0, -5);
        model.setRotation(rotation.x, rotation.y, rotation.z);

        model.render(view, projection);
    }
}
