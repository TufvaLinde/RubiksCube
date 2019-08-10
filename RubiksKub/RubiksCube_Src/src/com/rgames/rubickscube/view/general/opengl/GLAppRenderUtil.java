package com.rgames.rubickscube.view.general.opengl;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

/**
 */
public final class GLAppRenderUtil {

    /**
     */
    private static FloatBuffer defaultColor = initColor();
    
    
    private GLAppRenderUtil() {
        
    }

    public static FloatBuffer getDefaultColor() {
        return defaultColor;
    }
    
    public static FloatBuffer initColor() {
        final FloatBuffer color = BufferUtils.createFloatBuffer(4);
        color.put(0, 1.0f);
        color.put(1, 1.0f);
        color.put(2, 1.0f);
        color.put(3, 1.0f);
        
        return color;
    }
    
    /** Render a unit quad, using current color, texture and material. */
    public static void renderQuad2D(final int x, final int y, final int w, final int h) {
        GL11.glBegin(GL11.GL_QUADS);
    
        GL11.glTexCoord2f(0f, 0f);
        GL11.glVertex3f((float) x, (float) y, (float) 0);
        GL11.glTexCoord2f(1f, 0f);
        GL11.glVertex3f((float) x + w, (float) y, (float) 0);
        GL11.glTexCoord2f(1f, 1f);
        GL11.glVertex3f((float) x + w, (float) y + h, (float) 0);
        GL11.glTexCoord2f(0f, 1f);
        GL11.glVertex3f((float) x, (float) y + h, (float) 0);
        
        GL11.glEnd();
    }

    /** Render a unit plane, using current color, texture and material. */
    public static void renderPlane() {
        GL11.glBegin(GL11.GL_QUADS);
        
        // Top Face
        GL11.glNormal3f(0.0f, 1.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f(-1.0f,  0.0f, -1.0f);    // Top Left Of The Texture and Quad
        GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f(-1.0f,  0.0f,  1.0f);    // Bottom Left Of The Texture and Quad
        GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f(1.0f,  0.0f,  1.0f);    // Bottom Right Of The Texture and Quad
        GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f(1.0f,  0.0f, -1.0f);    // Top Right Of The Texture and Quad
    
        GL11.glEnd();
    }

    /** Render a unit cube, using current color, texture and material. 
     * */
    public static void renderCube() {
        GL11.glBegin(GL11.GL_QUADS);
        // Front Face
        GL11.glNormal3f(0.0f, 0.0f, 1.0f);
        GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f(-1.0f, -1.0f,  1.0f);    // Bottom Left Of The Texture and Quad
        GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f(1.0f, -1.0f,  1.0f);    // Bottom Right Of The Texture and Quad
        GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f(1.0f,  1.0f,  1.0f);    // Top Right Of The Texture and Quad
        GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f(-1.0f,  1.0f,  1.0f);    // Top Left Of The Texture and Quad
        // Back Face
        GL11.glNormal3f(0.0f, 0.0f, -1.0f);
        GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f(-1.0f, -1.0f, -1.0f);    // Bottom Right Of The Texture and Quad
        GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f(-1.0f,  1.0f, -1.0f);    // Top Right Of The Texture and Quad
        GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f(1.0f,  1.0f, -1.0f);    // Top Left Of The Texture and Quad
        GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f(1.0f, -1.0f, -1.0f);    // Bottom Left Of The Texture and Quad
        // Top Face
        GL11.glNormal3f(0.0f, 1.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f(-1.0f,  1.0f, -1.0f);    // Top Left Of The Texture and Quad
        GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f(-1.0f,  1.0f,  1.0f);    // Bottom Left Of The Texture and Quad
        GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f(1.0f,  1.0f,  1.0f);    // Bottom Right Of The Texture and Quad
        GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f(1.0f,  1.0f, -1.0f);    // Top Right Of The Texture and Quad
        // Bottom Face
        GL11.glNormal3f(0.0f, -1.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f(-1.0f, -1.0f, -1.0f);    // Top Right Of The Texture and Quad
        GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f(1.0f, -1.0f, -1.0f);    // Top Left Of The Texture and Quad
        GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f(1.0f, -1.0f,  1.0f);    // Bottom Left Of The Texture and Quad
        GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f(-1.0f, -1.0f,  1.0f);    // Bottom Right Of The Texture and Quad
        // Right face
        GL11.glNormal3f(1.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f(1.0f, -1.0f, -1.0f);    // Bottom Right Of The Texture and Quad
        GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f(1.0f,  1.0f, -1.0f);    // Top Right Of The Texture and Quad
        GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f(1.0f,  1.0f,  1.0f);    // Top Left Of The Texture and Quad
        GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f(1.0f, -1.0f,  1.0f);    // Bottom Left Of The Texture and Quad
        // Left Face
        GL11.glNormal3f(-1.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f(-1.0f, -1.0f, -1.0f);    // Bottom Left Of The Texture and Quad
        GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f(-1.0f, -1.0f,  1.0f);    // Bottom Right Of The Texture and Quad
        GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f(-1.0f,  1.0f,  1.0f);    // Top Right Of The Texture and Quad
        GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f(-1.0f,  1.0f, -1.0f);    // Top Left Of The Texture and Quad
        GL11.glEnd();
    }

    /**
     * Render a unit cube, using current color, texture and material. 
     * Assuming the texture map is (2*n) x (2*n / 2).
     * 
     * The sides on the texture should be arranged as follows:
     * +---+---+---+---+
     * | L | F | R | B |
     * +---+---+---+---+
     * | U | D |   |   |
     * +---+---+---+---+
     * */
    public static void renderCubeMapped() {
        GL11.glBegin(GL11.GL_QUADS);
    
        // Front Face
        GL11.glNormal3f(0.0f, 0.0f, 1.0f);
        GL11.glTexCoord2f(0.25f, 0.50f); GL11.glVertex3f(-1.0f, -1.0f,  1.0f);    // Bottom Left Of The Texture and Quad
        GL11.glTexCoord2f(0.50f, 0.50f); GL11.glVertex3f(1.0f, -1.0f,  1.0f);    // Bottom Right Of The Texture and Quad
        GL11.glTexCoord2f(0.50f, 1.00f); GL11.glVertex3f(1.0f,  1.0f,  1.0f);    // Top Right Of The Texture and Quad
        GL11.glTexCoord2f(0.25f, 1.00f); GL11.glVertex3f(-1.0f,  1.0f,  1.0f);    // Top Left Of The Texture and Quad
        
        // Back Face
        GL11.glNormal3f(0.0f, 0.0f, -1.0f);
        GL11.glTexCoord2f(1.00f, 0.50f); GL11.glVertex3f(-1.0f, -1.0f, -1.0f);    // Bottom Right Of The Texture and Quad
        GL11.glTexCoord2f(1.00f, 1.00f); GL11.glVertex3f(-1.0f,  1.0f, -1.0f);    // Top Right Of The Texture and Quad
        GL11.glTexCoord2f(0.75f, 1.00f); GL11.glVertex3f(1.0f,  1.0f, -1.0f);    // Top Left Of The Texture and Quad
        GL11.glTexCoord2f(0.75f, 0.50f); GL11.glVertex3f(1.0f, -1.0f, -1.0f);    // Bottom Left Of The Texture and Quad
        
        // Top Face
        GL11.glNormal3f(0.0f, 1.0f, 0.0f);
        GL11.glTexCoord2f(0.00f, 0.50f); GL11.glVertex3f(-1.0f,  1.0f, -1.0f);    // Top Left Of The Texture and Quad
        GL11.glTexCoord2f(0.00f, 0.00f); GL11.glVertex3f(-1.0f,  1.0f,  1.0f);    // Bottom Left Of The Texture and Quad
        GL11.glTexCoord2f(0.25f, 0.00f); GL11.glVertex3f(1.0f,  1.0f,  1.0f);    // Bottom Right Of The Texture and Quad
        GL11.glTexCoord2f(0.25f, 0.50f); GL11.glVertex3f(1.0f,  1.0f, -1.0f);    // Top Right Of The Texture and Quad
        
        // Bottom Face
        GL11.glNormal3f(0.0f, -1.0f, 0.0f);
        GL11.glTexCoord2f(0.50f, 0.50f); GL11.glVertex3f(-1.0f, -1.0f, -1.0f);    // Top Right Of The Texture and Quad
        GL11.glTexCoord2f(0.25f, 0.50f); GL11.glVertex3f(1.0f, -1.0f, -1.0f);    // Top Left Of The Texture and Quad
        GL11.glTexCoord2f(0.25f, 0.00f); GL11.glVertex3f(1.0f, -1.0f,  1.0f);    // Bottom Left Of The Texture and Quad
        GL11.glTexCoord2f(0.50f, 0.00f); GL11.glVertex3f(-1.0f, -1.0f,  1.0f);    // Bottom Right Of The Texture and Quad
        
        // Right face
        GL11.glNormal3f(1.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(0.75f, 0.50f); GL11.glVertex3f(1.0f, -1.0f, -1.0f);    // Bottom Right Of The Texture and Quad
        GL11.glTexCoord2f(0.75f, 1.00f); GL11.glVertex3f(1.0f,  1.0f, -1.0f);    // Top Right Of The Texture and Quad
        GL11.glTexCoord2f(0.50f, 1.00f); GL11.glVertex3f(1.0f,  1.0f,  1.0f);    // Top Left Of The Texture and Quad
        GL11.glTexCoord2f(0.50f, 0.50f); GL11.glVertex3f(1.0f, -1.0f,  1.0f);    // Bottom Left Of The Texture and Quad
        
        // Left Face
        GL11.glNormal3f(-1.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(0.00f, 0.50f); GL11.glVertex3f(-1.0f, -1.0f, -1.0f);    // Bottom Left Of The Texture and Quad
        GL11.glTexCoord2f(0.25f, 0.50f); GL11.glVertex3f(-1.0f, -1.0f,  1.0f);    // Bottom Right Of The Texture and Quad
        GL11.glTexCoord2f(0.25f, 1.00f); GL11.glVertex3f(-1.0f,  1.0f,  1.0f);    // Top Right Of The Texture and Quad
        GL11.glTexCoord2f(0.00f, 1.00f); GL11.glVertex3f(-1.0f,  1.0f, -1.0f);    // Top Left Of The Texture and Quad
        GL11.glEnd();
    }

    /**
     * Render a unit cube, using current color and texture for each side. 
     */
    public static void renderCubeMapped(final int texUp, final int texDn, final int texLt, 
            final int texRt, final int texFw, final int texBw) {
        
        // Front Face
        GLAppRenderUtil.renderFaceFront(texFw);
        
        // Back Face
        GLAppRenderUtil.renderFaceBack(texBw);
    
        // Top Face
        GLAppRenderUtil.renderFaceTop(texUp);
    
        // Bottom Face
        GLAppRenderUtil.renderFaceBottom(texDn);
    
        // Right face
        GLAppRenderUtil.renderFaceRight(texRt);
    
        // Left Face
        GLAppRenderUtil.renderFaceLeft(texLt);
    }

    public static void renderFaceFront(final int texHandle) {
        GL11.glColor4f(1f, 0f, 0f, 0.5f);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texHandle);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glNormal3f(0.0f, 0.0f, 1.0f);
        GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f(-1.0f, -1.0f,  1.0f);    // Bottom Left Of The Texture and Quad
        GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f(1.0f, -1.0f,  1.0f);    // Bottom Right Of The Texture and Quad
        GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f(1.0f,  1.0f,  1.0f);    // Top Right Of The Texture and Quad
        GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f(-1.0f,  1.0f,  1.0f);    // Top Left Of The Texture and Quad
        GL11.glEnd();
    }

    public static void renderFaceBack(final int texHandle) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texHandle);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glNormal3f(0.0f, 0.0f, -1.0f);
        GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f(-1.0f, -1.0f, -1.0f);    // Bottom Right Of The Texture and Quad
        GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f(-1.0f,  1.0f, -1.0f);    // Top Right Of The Texture and Quad
        GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f(1.0f,  1.0f, -1.0f);    // Top Left Of The Texture and Quad
        GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f(1.0f, -1.0f, -1.0f);    // Bottom Left Of The Texture and Quad
        GL11.glEnd();
    }

    public static void renderFaceTop(final int texHandle) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texHandle);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glNormal3f(0.0f, 1.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f(-1.0f,  1.0f, -1.0f);    // Top Left Of The Texture and Quad
        GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f(-1.0f,  1.0f,  1.0f);    // Bottom Left Of The Texture and Quad
        GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f(1.0f,  1.0f,  1.0f);    // Bottom Right Of The Texture and Quad
        GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f(1.0f,  1.0f, -1.0f);    // Top Right Of The Texture and Quad
        GL11.glEnd();
    }

    public static void renderFaceBottom(final int texHandle) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texHandle);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glNormal3f(0.0f, -1.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f(-1.0f, -1.0f, -1.0f);    // Top Right Of The Texture and Quad
        GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f(1.0f, -1.0f, -1.0f);    // Top Left Of The Texture and Quad
        GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f(1.0f, -1.0f,  1.0f);    // Bottom Left Of The Texture and Quad
        GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f(-1.0f, -1.0f,  1.0f);    // Bottom Right Of The Texture and Quad
        GL11.glEnd();
    }

    public static void renderFaceRight(final int texHandle) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texHandle);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glNormal3f(1.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f(1.0f, -1.0f, -1.0f);    // Bottom Right Of The Texture and Quad
        GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f(1.0f,  1.0f, -1.0f);    // Top Right Of The Texture and Quad
        GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f(1.0f,  1.0f,  1.0f);    // Top Left Of The Texture and Quad
        GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f(1.0f, -1.0f,  1.0f);    // Bottom Left Of The Texture and Quad
        GL11.glEnd();
    }

    public static void renderFaceLeft(final int texHandle) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texHandle);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glNormal3f(-1.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f(-1.0f, -1.0f, -1.0f);    // Bottom Left Of The Texture and Quad
        GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f(-1.0f, -1.0f,  1.0f);    // Bottom Right Of The Texture and Quad
        GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f(-1.0f,  1.0f,  1.0f);    // Top Right Of The Texture and Quad
        GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f(-1.0f,  1.0f, -1.0f);    // Top Left Of The Texture and Quad
        GL11.glEnd();
    }
    
    
}
