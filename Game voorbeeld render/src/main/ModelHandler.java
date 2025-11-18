package main;

import org.lwjgl.assimp.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.PointerBuffer;


import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

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
        AIScene scene = aiImportFile(path,
                aiProcess_Triangulate | aiProcess_FlipUVs | aiProcess_GenSmoothNormals);
        if (scene == null) throw new RuntimeException("Failed to load model: " + path);

        PointerBuffer meshes = scene.mMeshes();
        if (meshes == null || meshes.capacity() == 0) 
            throw new RuntimeException("No meshes found in model: " + path);

        AIMesh mesh = AIMesh.create(meshes.get(0));

        FloatBuffer verticesBuffer = org.lwjgl.BufferUtils.createFloatBuffer(mesh.mNumVertices() * 8);
        IntBuffer indicesBuffer = org.lwjgl.BufferUtils.createIntBuffer(mesh.mNumFaces() * 3);

        AIVector3D.Buffer texCoords = mesh.mTextureCoords(0);
        for (int i = 0; i < mesh.mNumVertices(); i++) {
            AIVector3D pos = mesh.mVertices().get(i);
            AIVector3D norm = mesh.mNormals().get(i);

            float u = 0, v = 0;
            if (texCoords != null) {
                AIVector3D tex = texCoords.get(i);
                u = tex.x();
                v = tex.y();
            }

            verticesBuffer.put(pos.x()).put(pos.y()).put(pos.z());
            verticesBuffer.put(norm.x()).put(norm.y()).put(norm.z());
            verticesBuffer.put(u).put(v);
        }
        verticesBuffer.flip();

        AIFace.Buffer faces = mesh.mFaces();
        for (int i = 0; i < mesh.mNumFaces(); i++) {
            AIFace face = faces.get(i);
            for (int j = 0; j < face.mNumIndices(); j++) {
                indicesBuffer.put(face.mIndices().get(j));
            }
        }
        indicesBuffer.flip();
        vertexCount = indicesBuffer.limit();

        // VAO, VBO, EBO
        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);

        ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

        // Vertex attributes
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

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

        shader.setMat4("model", model);
        shader.setMat4("view", view);
        shader.setMat4("projection", projection);

        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    public Vector3f getRotation() {
        return new Vector3f(rotation);
    }

    public void cleanup() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
    }
}
