package com.rgames.rubickscube.view.general.opengl.font;

import org.apache.log4j.Logger;
import org.lwjgl.opengl.GL11;

import com.rgames.rubickscube.controller.general.Logging;
import com.rgames.rubickscube.view.general.opengl.GLApp;

/**
 */
public final class GLFontPrinter {

    /**
     */
    public static final int HORIZONTAL_ALIGN_BEGINNING = 0x001;
    
    /**
     */
    public static final int HORIZONTAL_ALIGN_CENTER = 0x002;

    /**
     */
    public static final int HORIZONTAL_ALIGN_END = 0x003;

    /** 
     * Logger for this class.
     */
    private static final Logger LOG = Logging.getDefault();

    /**
     */
    private final GLFont m_font;
    
    /**
     */
    private int m_startX;
    
    /**
     */
    private int m_startY;

    /**
     */
    private int m_currentY;
    
    /**
     */
    private int m_hAlign = HORIZONTAL_ALIGN_BEGINNING;

    public GLFontPrinter(final GLFont font) {
        m_font = font;
    }
 
    public GLFont getFont() {
        return m_font;
    }
    
    public void setDrawingArea(final int x, final int y) {
        m_startX = x;
        m_startY = y;
        
        done();
    }
    
    public void setHorizontalAlign(final int align) {
        m_hAlign = align;
    }
    
    public void done() {
        m_currentY = m_startY;
    }

    public void print() {
        print("");
    }
    
    public void print(final String msg) {
        print(m_font, m_startX, m_currentY, m_hAlign, msg);
        
        m_currentY -= m_font.getConfiguration().getLineHeight() + m_font.getConfiguration().getLineSpacing();
    }
    
    public static void print(final GLFont font, final int x, final int y, 
            final int hAlign, final String msg) {
        final int set = 0; // TODO ?
        final int offset = font.getFontListBase() - 32 + (128 * set);
        if (font.getFontListBase() == -1 || font.getTextureHandle() == -1) {
            LOG.error("Character set has not been created yet!");
            return;
        }
        
        if (msg != null) {
            // enable the charset texture
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, font.getTextureHandle());
            
            // prepare to render in 2D
            GLApp.setOrthoOn();
            
            // Position The Text (in pixels coords)
            GL11.glTranslatef(x, y, 0);        

            // Fix aligning
            GL11.glTranslatef(getHorizonalAlign(hAlign, msg.length(), font), 0, 0);
            
            // Position The Text (in pixels coords)
            GL11.glScalef(font.getConfiguration().getSize(), font.getConfiguration().getSize(), font.getConfiguration().getSize());        

            // draw the text
            for (int i = 0; i < msg.length(); i++) {
                GL11.glCallList(offset + msg.charAt(i));
            }

            // restore the original positions and views
            GLApp.setOrthoOff();
        }
    }
    
    private static int getHorizonalAlign(final int hAlign, final int textLength, final GLFont font) {
        final GLFontConfiguration fc = font.getConfiguration(); 
        final int fontWidth = fc.getFontWidth() + fc.getLetterSpacing();
        final int textWidth = (int) (fontWidth * fc.getSize() * textLength) + fontWidth;
        
        switch (hAlign) {
            case HORIZONTAL_ALIGN_CENTER:
                return -textWidth / 2;
            case HORIZONTAL_ALIGN_END:
                return -textWidth;
            default:
                return 0;
        }
    }
}
