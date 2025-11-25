package entity;

import java.lang.reflect.Array;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import renderer.Model;
import renderer.Shader;

import physics.CollisionBox;

import java.util.List;

public class Object {
    private Model model;

    private Vector3f position;
    private Vector3f rotation;
    private float scale;

    private boolean interactable;
    private String interactableType;
    private String[] toolsRequired;
    private List<Drop> drops;

    private CollisionBox collisionBox;
    private CollisionBox interactionBox;

    public Object(String configPath, Shader shader, Vector3f pos, Vector3f rot) {

        this.position = pos;
        this.rotation = rot;
        this.scale = 1;
        loadConfigData(configPath, shader);
    }

    private void loadConfigData(String configPath, Shader shader) {
        this.model = ConfigLoader.loadModel(configPath, shader, position, rotation);
        this.collisionBox = ConfigLoader.loadCollisionBox(ConfigLoader.getCollisionPath(configPath), position);
        this.interactable = ConfigLoader.getInteractable(configPath);

        if (interactable) {
            this.interactionBox = ConfigLoader.loadInteractionBox(ConfigLoader.getCollisionPath(configPath), position);
        }


        this.interactableType = ConfigLoader.getInteractableType(configPath);
        this.toolsRequired = ConfigLoader.getToolsRequired(configPath);

        this.drops = ConfigLoader.getDrops(configPath);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;

        if (collisionBox != null)
            collisionBox.setPosition(position);

        if (interactionBox != null)
            interactionBox.setPosition(position);
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

    public CollisionBox getInteractionBox() {
        return interactionBox;
    }

    public void render(Matrix4f view, Matrix4f projection) {
        model.setPosition(position);
        model.setRotation(rotation);
        model.setScale(scale);
        model.render(view, projection);
    }

    public void breakObject() {
        
    }
}

