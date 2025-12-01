package entity;

import renderer.Model;
import renderer.Shader;
import renderer.Texture;
import org.joml.Vector3f;

import java.util.Map;
import java.util.HashMap;

public class ModelHandler {
    private static final Map<String, Model> prototypes = new HashMap<>();

    /**
     * Get a model instance for the given `path`. The model mesh/VBOs/VAO are loaded once per path
     * and reused for subsequent instances. Each returned Model has its own transform/shader/texture.
     *
     * @param path      path to model file (Assimp-supported)
     * @param shader    shader to use for this instance (if null, prototype's shader is used)
     * @param position  initial position for the instance
     * @param rotation  initial rotation for the instance
     * @param scale     initial scale for the instance
     * @param texture   texture for the instance (may be null)
     * @return a lightweight Model instance (shares GPU buffers)
     */
    public Model getModel(String path, Shader shader, Vector3f position, Vector3f rotation, float scale, Texture texture) {
        if (!prototypes.containsKey(path)) {
            // create prototype; it will upload mesh/buffers once
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
