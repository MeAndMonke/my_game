package renderer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;

public class Shader {

    private final int programId;
    private final int vertexShaderId;
    private final int fragmentShaderId;
    private final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    public Shader(String vertexCode, String fragmentCode) {
        vertexShaderId = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShaderId, vertexCode);
        glCompileShader(vertexShaderId);
        checkCompileErrors(vertexShaderId, "VERTEX");

        fragmentShaderId = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShaderId, fragmentCode);
        glCompileShader(fragmentShaderId);
        checkCompileErrors(fragmentShaderId, "FRAGMENT");

        programId = glCreateProgram();
        glAttachShader(programId, vertexShaderId);
        glAttachShader(programId, fragmentShaderId);
        glLinkProgram(programId);
        checkCompileErrors(programId, "PROGRAM");

        glDeleteShader(vertexShaderId);
        glDeleteShader(fragmentShaderId);
    }

    public void setVec3(String name, Vector3f vec) {
        int loc = glGetUniformLocation(programId, name);
        glUniform3f(loc, vec.x, vec.y, vec.z);
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void setMat4(String name, Matrix4f matrix) {
        glUniformMatrix4fv(glGetUniformLocation(programId, name), false, matrix.get(matrixBuffer));
    }

    public void setVec3(String name, float x, float y, float z) {
        glUniform3f(glGetUniformLocation(programId, name), x, y, z);
    }

    private void checkCompileErrors(int id, String type) {
        int success;
        if (type.equals("PROGRAM")) {
            success = glGetProgrami(id, GL_LINK_STATUS);
            if (success == 0) System.err.println("ERROR::PROGRAM_LINKING_ERROR\n" + glGetProgramInfoLog(id));
        } else {
            success = glGetShaderi(id, GL_COMPILE_STATUS);
            if (success == 0) System.err.println("ERROR::SHADER_COMPILATION_ERROR of type: " + type + "\n" + glGetShaderInfoLog(id));
        }
    }
}
