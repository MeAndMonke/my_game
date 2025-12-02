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
    private Texture texture;


    private Vector3f position;
    private Vector3f rotation;
    private float scale;

    private AIMesh mesh;
    private int vao, vbo, vboNormal, ebo, vboTexCoords;
    private int vertexCount;

    public Model(String path, Shader shader, Vector3f position, Vector3f rotation, float scale, Texture texture) {
        this.shader = shader;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.texture = texture;

        mesh = loadMesh(path);
        vertexCount = mesh.mNumFaces() * 3;

        modelMatrix = computeModelMatrix();

        setupMesh();
    }

    private Model(Shader shader, Vector3f position, Vector3f rotation, float scale, Texture texture,
                  AIMesh mesh, int vao, int vbo, int vboNormal, int ebo, int vboTexCoords, int vertexCount) {
        this.shader = shader;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.texture = texture;

        this.mesh = mesh;
        this.vao = vao;
        this.vbo = vbo;
        this.vboNormal = vboNormal;
        this.ebo = ebo;
        this.vboTexCoords = vboTexCoords;
        this.vertexCount = vertexCount;

        this.modelMatrix = computeModelMatrix();
    }

    /**
     * Setup OpenGL buffers for the mesh.
     */
    private void setupMesh() {
        float[] vertices = extractVertices(mesh);
        float[] normals  = extractNormals(mesh);
        float[] texCoords = extractTexCoords(mesh);
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

        // texture coordinates
        vboTexCoords = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboTexCoords);
        glBufferData(GL_ARRAY_BUFFER, texCoords, GL_STATIC_DRAW);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);

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

        if (texture != null) {
            texture.bind(0);
            shader.setInt("textureSampler", 0);
        }

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

    /**
     * Create a lightweight copy of this model instance.
     * @param position The position of the new instance.
     * @param rotation The rotation of the new instance.
     * @param scale The scale of the new instance.
     * @param texture The texture of the new instance.
     * @param shader The shader of the new instance.
     * @return A new Model instance.
     */
    public Model createInstance(Vector3f position, Vector3f rotation, float scale, Texture texture, Shader shader) {
        Shader useShader = shader != null ? shader : this.shader;
        Texture useTexture = texture != null ? texture : this.texture;
        Vector3f usePos = position != null ? new Vector3f(position) : new Vector3f(this.position);
        Vector3f useRot = rotation != null ? new Vector3f(rotation) : new Vector3f(this.rotation);
        float useScale = scale != 0f ? scale : this.scale;

        return new Model(useShader, usePos, useRot, useScale, useTexture,
                this.mesh, this.vao, this.vbo, this.vboNormal, this.ebo, this.vboTexCoords, this.vertexCount);
    }

    /**
     * Calculate the model matrix based on position, rotation, and scale.
     * @return The Calculated model matrix.
     */
    private Matrix4f computeModelMatrix() {
        return new Matrix4f()
                .translation(position)
                .rotateX((float) Math.toRadians(rotation.x))
                .rotateY((float) Math.toRadians(rotation.y))
                .rotateZ((float) Math.toRadians(rotation.z))
                .scale(scale);
    }

    /**
     * Extract vertex positions from the mesh.
     * @param mesh The AIMesh to extract vertices from.
     * @return An array of vertex positions.
     */
    private float[] extractVertices(AIMesh mesh) {
        float[] vertices = new float[mesh.mNumVertices() * 3];
        for (int i = 0; i < mesh.mNumVertices(); i++) {
            vertices[i * 3]     = mesh.mVertices().get(i).x();
            vertices[i * 3 + 1] = mesh.mVertices().get(i).y();
            vertices[i * 3 + 2] = mesh.mVertices().get(i).z();
        }
        return vertices;
    }

    /**
     * Extract vertex normals from the mesh.
     * @param mesh The AIMesh to extract normals from.
     * @return An array of vertex normals.
     */
    private float[] extractNormals(AIMesh mesh) {
        float[] normals = new float[mesh.mNumVertices() * 3];
        for (int i = 0; i < mesh.mNumVertices(); i++) {
            normals[i * 3]     = mesh.mNormals().get(i).x();
            normals[i * 3 + 1] = mesh.mNormals().get(i).y();
            normals[i * 3 + 2] = mesh.mNormals().get(i).z();
        }
        return normals;
    }

    /**
     * Extract indices from the mesh.
     * @param mesh The AIMesh to extract indices from.
     * @return An array of indices.
     */
    private int[] extractIndices(AIMesh mesh) {
        int[] indices = new int[mesh.mNumFaces() * 3];
        for (int i = 0; i < mesh.mNumFaces(); i++) {
            indices[i * 3]     = mesh.mFaces().get(i).mIndices().get(0);
            indices[i * 3 + 1] = mesh.mFaces().get(i).mIndices().get(1);
            indices[i * 3 + 2] = mesh.mFaces().get(i).mIndices().get(2);
        }
        return indices;
    }

    /**
     * Load a mesh from a file using Assimp.
     * @param path The path to the model file.
     * @return The loaded AIMesh.
     */
    private AIMesh loadMesh(String path) {
        AIScene scene = aiImportFile(path, aiProcess_Triangulate | aiProcess_FlipUVs | aiProcess_GenSmoothNormals);
        if (scene == null) throw new RuntimeException("Failed to load model: " + path);
        return AIMesh.create(scene.mMeshes().get(0));
    }

    /**
     * Extract texture coordinates from the mesh.
     * @param mesh The AIMesh to extract texture coordinates from.
     * @return An array of texture coordinates.
     */
    private float[] extractTexCoords(AIMesh mesh) {
        float[] texCoords = new float[mesh.mNumVertices() * 2];
        for (int i = 0; i < mesh.mNumVertices(); i++) {
            if (mesh.mTextureCoords(0) != null) {
                texCoords[i * 2]     = mesh.mTextureCoords(0).get(i).x();
                texCoords[i * 2 + 1] = 1.0f - mesh.mTextureCoords(0).get(i).y(); // FLIP V
            } else {
                texCoords[i * 2] = 0f;
                texCoords[i * 2 + 1] = 0f;
            }
        }
        return texCoords;
    }

}
