import org.lwjgl.assimp.*;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.assimp.Assimp.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.joml.Vector3f;
import org.joml.Matrix4f;
import java.lang.Math;

public class ModelHandler {

    private int vao, vbo, ebo;
    private int vertexCount;
    private Shader shader;

    private Vector3f position = new Vector3f();
    private Vector3f rotation = new Vector3f();
    private Vector3f scale = new Vector3f(1,1,1);

    public ModelHandler(String path, Shader shader) {
        this.shader = shader;
        loadModel(path);
    }

    private void loadModel(String path) {
        AIScene scene = aiImportFile(path, aiProcess_Triangulate | aiProcess_FlipUVs | aiProcess_GenSmoothNormals);
        if (scene == null) throw new RuntimeException("Failed to load model: " + path);

        AIMesh mesh = AIMesh.create(scene.mMeshes().get(0));

        FloatBuffer verticesBuffer = FloatBuffer.allocate(mesh.mNumVertices() * 8);
        for (int i = 0; i < mesh.mNumVertices(); i++) {
            AIVector3D pos = mesh.mVertices().get(i);
            AIVector3D norm = mesh.mNormals().get(i);
            AIVector3D tex = mesh.mTextureCoords(0) != null ? mesh.mTextureCoords(0).get(i) : AIVector3D.create();
            verticesBuffer.put(pos.x()).put(pos.y()).put(pos.z());
            verticesBuffer.put(norm.x()).put(norm.y()).put(norm.z());
            verticesBuffer.put(tex.x()).put(tex.y());
        }
        verticesBuffer.flip();

        IntBuffer indicesBuffer = IntBuffer.allocate(mesh.mNumFaces() * 3);
        AIFace.Buffer faces = mesh.mFaces();
        for (int i = 0; i < mesh.mNumFaces(); i++) {
            AIFace face = faces.get(i);
            indicesBuffer.put(face.mIndices());
        }
        indicesBuffer.flip();
        vertexCount = indicesBuffer.limit();

        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);

        ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

        // Vertex positions
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        // Normals
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);
        // Texture coords
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * Float.BYTES, 6 * Float.BYTES);
        glEnableVertexAttribArray(2);

        glBindVertexArray(0);
    }

    public void setPosition(float x, float y, float z) { position.set(x, y, z); }
    public void setRotation(float x, float y, float z) { rotation.set(x, y, z); }
    public void setScale(float x, float y, float z) { scale.set(x, y, z); }

    public void render(Matrix4f view, Matrix4f projection) {
        shader.bind();

        Matrix4f model = new Matrix4f()
            .translate(position)
            .rotateXYZ((float)Math.toRadians(rotation.x),
                       (float)Math.toRadians(rotation.y),
                       (float)Math.toRadians(rotation.z))
            .scale(scale);

        shader.setMatrix4f("model", model);
        shader.setMatrix4f("view", view);
        shader.setMatrix4f("projection", projection);

        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    public void cleanup() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
    }
}
