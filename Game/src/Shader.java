import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import java.nio.FloatBuffer;
import static org.lwjgl.opengl.GL33.*;

public class Shader {

    private int program;

    public Shader(String vertexSrc, String fragmentSrc) {
        int vs = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vs, vertexSrc);
        glCompileShader(vs);
        checkCompile(vs);

        int fs = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fs, fragmentSrc);
        glCompileShader(fs);
        checkCompile(fs);

        program = glCreateProgram();
        glAttachShader(program, vs);
        glAttachShader(program, fs);
        glLinkProgram(program);
        glValidateProgram(program);

        glDeleteShader(vs);
        glDeleteShader(fs);
    }

    private void checkCompile(int shader) {
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE)
            throw new RuntimeException("Shader compile error: " + glGetShaderInfoLog(shader));
    }

    public void bind() { glUseProgram(program); }
    public void unbind() { glUseProgram(0); }

    public void setMatrix4f(String name, Matrix4f matrix) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(16);
            matrix.get(fb);
            int loc = glGetUniformLocation(program, name);
            if (loc != -1) glUniformMatrix4fv(loc, false, fb);
        }
    }
}
