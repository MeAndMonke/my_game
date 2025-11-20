package physics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class CollisionBox {
    private Vector3f position; // center position
    private float width, height;
    private float offsetX, offsetY;

    // GL objects for drawing a rectangle as a line loop (created once)
    private int vao = 0;
    private int vboPos = 0;
    private int vboNormal = 0;
    private int vboTex = 0;
    private int ebo = 0;
    private boolean initialized = false;

    public CollisionBox(Vector3f position, float width, float height, float offsetX, float offsetY) {
        this.position = new Vector3f(position);
        this.width = width;
        this.height = height;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public Vector3f getPosition() {
        return new Vector3f(position.x + offsetX, position.y, position.z + offsetY);
    }

    public void setPosition(Vector3f position) {
        this.position = new Vector3f(position);
    }

    public float getWidth() { return width; }
    public float getHeight() { return height; }

    public boolean intersects(CollisionBox other) {
        Vector3f thisPos = getPosition();
        Vector3f otherPos = other.getPosition();

        boolean xOverlap = Math.abs(thisPos.x - otherPos.x) < (this.width / 2 + other.width / 2);
        boolean zOverlap = Math.abs(thisPos.z - otherPos.z) < (this.height / 2 + other.height / 2);

        return xOverlap && zOverlap;
    }

    private void ensureInitialized() {
        if (initialized) return;

        float w = width / 2f;
        float d = height / 2f;

        float[] vertices = new float[] {
            -w, 0f, -d,
             w, 0f, -d,
             w, 0f,  d,
            -w, 0f,  d
        };

        float[] normals = new float[] {
            0f,1f,0f,
            0f,1f,0f,
            0f,1f,0f,
            0f,1f,0f
        };

        float[] texcoords = new float[] {
            0f,0f,
            1f,0f,
            1f,1f,
            0f,1f
        };

        int[] indices = new int[] {0,1,2,3};

        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        vboPos = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboPos);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);

        vboNormal = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboNormal);
        glBufferData(GL_ARRAY_BUFFER, normals, GL_STATIC_DRAW);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);

        vboTex = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboTex);
        glBufferData(GL_ARRAY_BUFFER, texcoords, GL_STATIC_DRAW);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);

        ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glBindVertexArray(0);

        initialized = true;
    }

    // modern debug render using provided shader and matrices
    public void render(renderer.Shader shader, Matrix4f view, Matrix4f projection) {
        ensureInitialized();

        Matrix4f model = new Matrix4f().translation(getPosition());

        shader.bind();
        shader.setMat4("model", model);
        shader.setMat4("view", view);
        shader.setMat4("projection", projection);
        // color is provided by the shader used to draw the box (e.g. a simple line shader)

        glBindVertexArray(vao);
        glDrawElements(GL_LINE_LOOP, 4, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);

        shader.unbind();
    }
}
