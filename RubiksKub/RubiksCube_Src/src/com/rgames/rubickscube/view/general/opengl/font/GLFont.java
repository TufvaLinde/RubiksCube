package com.rgames.rubickscube.view.general.opengl.font;

import org.lwjgl.opengl.GL11;

import com.rgames.rubickscube.view.general.opengl.GLApp;
import com.rgames.rubickscube.view.general.opengl.image.GLImage;
import com.rgames.rubickscube.view.general.opengl.image.GLImageUtil;

/**
 * Build the character set display list from the given texture.  Creates
 * one quad for each character, with one letter textured onto each quad.
 * Assumes the texture is a 256x256 image containing every
 * character of the charset arranged in a 16x16 grid.  Each character
 * is FONT_W x FONT_H pixels.  Call destroyFont() to release the display list memory.
 *
 * Should be in ORTHO (2D) mode to render text (see setOrtho()).
 *
 * Special thanks to NeHe and Giuseppe D'Agata for the "2D Texture Font"
 * tutorial (http://nehe.gamedev.net).
 */
public final class GLFont {

    /**
     */
    private final GLFontConfiguration m_conf;
    
    /** 
     * Base Display List For The character set. 
     */
    private int m_fontListBase = -1;         
    
    /** 
     * Texture handle for character set image. 
     */
    private int m_fontTextureHandle = -1;     
    
    public GLFont(final GLFontConfiguration conf) {
        m_conf = conf;
        
        build();
    }

    public GLFontConfiguration getConfiguration() {
        return m_conf;
    }
    
    public int getFontListBase() {
        return m_fontListBase;
    }
    
    public int getTextureHandle() {
        return m_fontTextureHandle;
    }

    /**
     * Tell OpenGL how to draw the font. 
     */
    public void init() {
        // Enable alpha transparency (so text will have transparent background)
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
        // Enable texturing, since each character is a texture drawn on a quad
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
    
    /**
     * Clean up the allocated display lists for the character set.
     */
    public void destroy() {
        if (m_fontListBase != -1) {
            GL11.glDeleteLists(m_fontListBase, 256);
            
            m_fontListBase = -1;
        }
    }

    /**
     * Build a character set from the given texture image.
     *
     * @param charSetImage   texture image containing 256 characters in a 16x16 grid
     * @param fontWidth      how many pixels to allow per character on screen
     *
     * @see       destroyFont()
     */
    private void build() {
        // make texture from image
        final GLImage textureImg = GLImageUtil.loadImage(m_conf.getFontSet());
        m_fontTextureHandle = GLApp.makeTexture(textureImg);
        
        // build character set as call list of 256 textured quads
        build(m_fontTextureHandle);
    }

    /**
      * Build the character set display list from the given texture.  Creates
      * one quad for each character, with one letter textured onto each quad.
      * Assumes the texture is a 256x256 image containing every
      * character of the charset arranged in a 16x16 grid.  Each character
      * is 16x16 pixels.  Call destroyFont() to release the display list memory.
      *
      * Should be in ORTHO (2D) mode to render text (see setOrtho()).
      *
      * Special thanks to NeHe and Giuseppe D'Agata for the "2D Texture Font"
      * tutorial (http://nehe.gamedev.net).
      *
      * @param charSetImage   texture image containing 256 characters in a 16x16 grid
      * @param fontWidth      how many pixels to allow per character on screen
      *
      * @see       destroyFont()
      */
    private void build(final int fontTxtrHandle) {
        final float factor = 1f / 16f;
        float cx;
        float cy;
        
        // Creating 256 Display Lists
        m_fontListBase = GL11.glGenLists(256); 
        for (int i = 0; i < 256; i++) {
            // X Texture Coord Of Character (0 - 1.0)
            cx = (float) (i % 16) / 16f;              
            // Y Texture Coord Of Character (0 - 1.0)
            cy = (float) (i / 16) / 16f;              
            
            // Start Building A List
            GL11.glNewList(m_fontListBase + i, GL11.GL_COMPILE); 
            GL11.glBegin(GL11.GL_QUADS);
            
            // Texture Coord (Bottom Left)
            GL11.glTexCoord2f(cx, 1 - cy - factor); 
            GL11.glVertex2i(0, 0);
            
            // Texture Coord (Bottom Right)
            GL11.glTexCoord2f(cx + factor, 1 - cy - factor); 
            GL11.glVertex2i(16, 0);
            
            // Texture Coord (Top Right)
            GL11.glTexCoord2f(cx + factor, 1 - cy); 
            GL11.glVertex2i(16, 16);
            
            // Texture Coord (Top Left)
            GL11.glTexCoord2f(cx, 1 - cy);             
            GL11.glVertex2i(0, 16);
            
            // Done Building Our Quad (Character)            
            GL11.glEnd();                              
            
            // Move To The Right Of The Character
            GL11.glTranslatef(m_conf.getFontWidth() + m_conf.getLetterSpacing(), 0, 0);
            
            // Done Building The Display List            
            GL11.glEndList();                          
        }
    }
}
