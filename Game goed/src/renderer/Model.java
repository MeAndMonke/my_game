package renderer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Model {

    private Shader shader;
    private Matrix4f modelMatrix;

    private Vector3f position;
    private Vector3f rotation;
    private float scale;

    private AIMesh mesh;
    private int vao, vbo, vboNormal, ebo;
    private int vertexCount;

    public Model(String path, Shader shader, Vector3f position, Vector3f rotation, float scale) {
        this.shader = shader;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;

        mesh = loadMesh(path);
        vertexCount = mesh.mNumFaces() * 3;

        modelMatrix = computeModelMatrix();

        setupMesh();
    }

    private void setupMesh() {
        float[] vertices = extractVertices(mesh);
        float[] normals  = extractNormals(mesh);
        int[] indices    = extractIndices(mesh);

        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        // positions
        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);

        // normals
        vboNormal = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboNormal);
        glBufferData(GL_ARRAY_BUFFER, normals, GL_STATIC_DRAW);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);

        // indices
        ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glBindVertexArray(0);
    }

    public void render(Matrix4f view, Matrix4f projection) {
        shader.bind();
        shader.setMat4("model", modelMatrix);
        shader.setMat4("view", view);
        shader.setMat4("projection", projection);

        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
        shader.unbind();
    }

    public void setPosition(Vector3f pos) {
        this.position = pos;
        modelMatrix = computeModelMatrix();
    }

    public void setRotation(Vector3f rot) {
        this.rotation = rot;
        modelMatrix = computeModelMatrix();
    }

    public void setScale(float scale) {
        this.scale = scale;
        modelMatrix = computeModelMatrix();
    }

    private Matrix4f computeModelMatrix() {
        return new Matrix4f()
                .translation(position)
                .rotateX((float) Math.toRadians(rotation.x))
                .rotateY((float) Math.toRadians(rotation.y))
                .rotateZ((float) Math.toRadians(rotation.z))
                .scale(scale);
    }

    private float[] extractVertices(AIMesh mesh) {
        float[] vertices = new float[mesh.mNumVertices() * 3];
        for (int i = 0; i < mesh.mNumVertices(); i++) {
            vertices[i * 3]     = mesh.mVertices().get(i).x();
            vertices[i * 3 + 1] = mesh.mVertices().get(i).y();
            vertices[i * 3 + 2] = mesh.mVertices().get(i).z();
        }
        return vertices;
    }

    private float[] extractNormals(AIMesh mesh) {
        float[] normals = new float[mesh.mNumVertices() * 3];
        for (int i = 0; i < mesh.mNumVertices(); i++) {
            normals[i * 3]     = mesh.mNormals().get(i).x();
            normals[i * 3 + 1] = mesh.mNormals().get(i).y();
            normals[i * 3 + 2] = mesh.mNormals().get(i).z();
        }
        return normals;
    }

    private int[] extractIndices(AIMesh mesh) {
        int[] indices = new int[mesh.mNumFaces() * 3];
        for (int i = 0; i < mesh.mNumFaces(); i++) {
            indices[i * 3]     = mesh.mFaces().get(i).mIndices().get(0);
            indices[i * 3 + 1] = mesh.mFaces().get(i).mIndices().get(1);
            indices[i * 3 + 2] = mesh.mFaces().get(i).mIndices().get(2);
        }
        return indices;
    }

    private AIMesh loadMesh(String path) {
        AIScene scene = aiImportFile(path, aiProcess_Triangulate | aiProcess_FlipUVs | aiProcess_GenSmoothNormals);
        if (scene == null) throw new RuntimeException("Failed to load model: " + path);
        return AIMesh.create(scene.mMeshes().get(0));
    }
}
