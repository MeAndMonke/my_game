package gameplay;

import entity.Object;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import renderer.Shader;

import core.App;

public class BuildManager {

    private String configPath;
    private Shader shader;
    private Object previewObject;

    public BuildManager(String configPath, Shader shader) {
        this.configPath = configPath;
        this.shader = shader;
        this.previewObject = new Object(configPath, shader, new Vector3f(0,0,0), new Vector3f(0,0,0));
    }

    public void renderPreview(Vector3f pos, Matrix4f view, Matrix4f projection) {
        if (previewObject == null) return;
        previewObject.setPosition(new Vector3f(pos.x, pos.y, pos.z));
        previewObject.render(view, projection);
    }

    public void placeModel(Vector3f pos) {
        if (configPath == null || shader == null) return;
        Object placed = new Object(configPath, shader, new Vector3f(pos.x, pos.y, pos.z), new Vector3f(0,0,0));
        App.addWorldObject(placed);
    }
}
