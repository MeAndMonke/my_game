package entity;

import renderer.Model;
import renderer.Shader;
import renderer.Texture;
import org.joml.Vector3f;

import java.util.Map;
import java.util.HashMap;

public class ModelHandler {
    private static final Map<String, Model> prototypes = new HashMap<>();

    public Model getModel(String path, Shader shader, Vector3f position, Vector3f rotation, float scale, Texture texture) {
        if (!prototypes.containsKey(path)) {
            Model prototype = new Model(path, shader, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 1.0f, texture);
            prototypes.put(path, prototype);
        }

        Model proto = prototypes.get(path);
        return proto.createInstance(position, rotation, scale, texture, shader);
    }

    public void clearCache() {
        prototypes.clear();
    }
}
