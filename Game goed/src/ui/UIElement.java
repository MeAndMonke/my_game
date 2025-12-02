package ui;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;


public abstract class UIElement {
    public float x, y, width, height;

    public abstract void update(float dt);
    public abstract void render();
    public abstract void onClick(double mx, double my);

    public void drawRect(float x, float y, float w, float h, int r, int g, int b, int a) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);


        glColor4f(r / 255f, g / 255f, b / 255f, a / 255f);
        glBegin(GL_QUADS);
            glVertex2f(x, y);
            glVertex2f(x + w, y);
            glVertex2f(x + w, y + h);
            glVertex2f(x, y + h);
        glEnd();
    }


    public void drawText(String text, float x, float y, int fontSize) {
        Font font = new Font(Font.MONOSPACED, Font.BOLD, fontSize);

        BufferedImage tmp = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2dTmp = tmp.createGraphics();
        g2dTmp.setFont(font);
        FontMetrics fm = g2dTmp.getFontMetrics();
        int textWidth = Math.max(1, fm.stringWidth(text));
        int textHeight = Math.max(1, fm.getHeight());
        g2dTmp.dispose();

        BufferedImage img = new BufferedImage(textWidth, textHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setFont(font);
        g2d.setColor(java.awt.Color.WHITE);
        g2d.drawString(text, 0, fm.getAscent());
        g2d.dispose();

        int[] pixels = new int[textWidth * textHeight];
        img.getRGB(0, 0, textWidth, textHeight, pixels, 0, textWidth);
        ByteBuffer buffer = org.lwjgl.BufferUtils.createByteBuffer(textWidth * textHeight * 4);
        for (int i = 0; i < pixels.length; i++) {
            int p = pixels[i];
            buffer.put((byte) ((p >> 16) & 0xFF)); // R
            buffer.put((byte) ((p >> 8) & 0xFF));  // G
            buffer.put((byte) (p & 0xFF));         // B
            buffer.put((byte) ((p >> 24) & 0xFF)); // A
        }
        buffer.flip();

        // Upload texture
        int texId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texId);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, textWidth, textHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        // Draw textured quad
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor3f(1f, 1f, 1f);

        glBegin(GL_QUADS);
            glTexCoord2f(0f, 0f); glVertex2f(x, y);
            glTexCoord2f(1f, 0f); glVertex2f(x + textWidth, y);
            glTexCoord2f(1f, 1f); glVertex2f(x + textWidth, y + textHeight);
            glTexCoord2f(0f, 1f); glVertex2f(x, y + textHeight);
        glEnd();

        glDisable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glBindTexture(GL_TEXTURE_2D, 0);
        glDeleteTextures(texId);
    }

    /**
     * Draw a BufferedImage at specified position and size.
     * @param image The image to draw.
     * @param x The x position.
     * @param y The y position.
     * @param w The width to draw.
     * @param h The height to draw.
     */
    public void drawImage(BufferedImage image, float x, float y, float w, float h) {
        int imgWidth = image.getWidth();
        int imgHeight = image.getHeight();

        int[] pixels = new int[imgWidth * imgHeight];
        image.getRGB(0, 0, imgWidth, imgHeight, pixels, 0, imgWidth);
        ByteBuffer buffer = org.lwjgl.BufferUtils.createByteBuffer(imgWidth * imgHeight * 4);
        for (int i = 0; i < pixels.length; i++) {
            int p = pixels[i];
            buffer.put((byte) ((p >> 16) & 0xFF)); // R
            buffer.put((byte) ((p >> 8) & 0xFF));  // G
            buffer.put((byte) (p & 0xFF));         // B
            buffer.put((byte) ((p >> 24) & 0xFF)); // A
        }
        buffer.flip();

        // Upload texture
        int texId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texId);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, imgWidth, imgHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        // Draw textured quad
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor3f(1f, 1f, 1f);

        // Draw quad
        glBegin(GL_QUADS);
            glTexCoord2f(0f, 0f); glVertex2f(x, y);
            glTexCoord2f(1f, 0f); glVertex2f(x + w, y);
            glTexCoord2f(1f, 1f); glVertex2f(x + w, y + h);
            glTexCoord2f(0f, 1f); glVertex2f(x, y + h);
        glEnd();

        // Cleanup
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glBindTexture(GL_TEXTURE_2D, 0);
        glDeleteTextures(texId);
    }
}
