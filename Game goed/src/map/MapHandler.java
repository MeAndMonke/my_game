package map;

import entity.Object;
import physics.CollisionBox;
import renderer.Shader;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import core.App;

public class MapHandler {
    private Object[][] mapObjects;

    private Shader shader;

    public MapHandler(int width, int height, Shader shader) {
        this.shader = shader;

        mapObjects = new Object[width][height];
        loadMap(shader);
    }

    private void loadMap(Shader shader) {

        int treeChance = 10;
        int rockChance = 5;

        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                Vector3f rotation = new Vector3f(0, (float)(Math.random() * 360), 0);
                int rand = (int)(Math.random() * 100);
                if (rand > 100 - treeChance) {
                    if (mapObjects[x][y] == null) {
                        mapObjects[x][y] = createObject("res/models/configs/tree.json", new Vector3f(x * 2,0,y * 2), rotation);
                    }
                } else if (rand > 100 - treeChance - rockChance) {
                    if (mapObjects[x][y] == null) {
                        mapObjects[x][y] = createObject("res/models/configs/rock.json", new Vector3f(x * 2,0,y * 2), rotation);
                    }
                }
            }
        }
    }
    private void createRandomTree(int maxX, int maxY) {
        int x = (int)(Math.random() * maxX);
        int y = (int)(Math.random() * maxY);

        Vector3f rotation = new Vector3f(0, (float)(Math.random() * 360), 0);
        if (mapObjects[x][y] == null) {
            mapObjects[x][y] = createObject("res/models/configs/tree.json", new Vector3f(x * 2,0,y * 2), rotation);
        }
    }

    private Object createObject(String configPath, Vector3f position, Vector3f rotation) {
        Object tree =  new Object(configPath, shader, position, rotation);
    
        App.addWorldObject(tree);

        return tree;
    }

    // public CollisionBox getAllColliosBox() {

    // }

    public void render(Matrix4f viewMatrix, Matrix4f projectionMatrix) {
        for (int x = 0; x < mapObjects.length; x++) {
            for (int z = 0; z < mapObjects[0].length; z++) {
                Object obj = mapObjects[x][z];
                if (obj != null) {
                    obj.render(viewMatrix, projectionMatrix);
                }
            }
        }
    }
}
