package ui;

import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.awt.Color;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.GL_QUADS;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBEasyFont;


public abstract class UIElement {
    public float x, y, width, height;

    public abstract void update(float dt);
    public abstract void render();
    public abstract void onClick(double mx, double my);

    private static final ByteBuffer charBuffer = BufferUtils.createByteBuffer(99999);


    public void drawRect(float x, float y, float w, float h, Color color) {
        glColor3f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);

        glBegin(GL_QUADS);
            glVertex2f(x,     y);
            glVertex2f(x + w, y);
            glVertex2f(x + w, y + h);
            glVertex2f(x,     y + h);
        glEnd();
    }

    public void drawText(String text, float x, float y) {
        // stb_easy_font returns how many quads (characters) it wrote
        int quads = STBEasyFont.stb_easy_font_print(
            x,
            y,
            text,
            null,
            charBuffer
        );

        glColor3f(1f, 1f, 1f); // text color (white)

        glBegin(GL_QUADS);
        for (int i = 0; i < quads * 16; i += 16) {
            glVertex2f(charBuffer.getFloat(i),     charBuffer.getFloat(i + 4));
            glVertex2f(charBuffer.getFloat(i + 8), charBuffer.getFloat(i + 12));
            glVertex2f(charBuffer.getFloat(i + 16), charBuffer.getFloat(i + 20));
            glVertex2f(charBuffer.getFloat(i + 24), charBuffer.getFloat(i + 28));
        }
        glEnd();
    }




}
