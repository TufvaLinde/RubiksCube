package com.rgames.rubickscube.view.general.opengl.font;

/**
 */
public final class GLFontConfiguration {

    /**
     */
    private final String m_fontSet;

    /**
     * Width of each font in the font texture.
     */
    private final int m_fontWidth;
    
    /**
     * Height of each font in the font texture.
     */
    private final int m_fontHeight;

    /**
     * The size of the font.
     */
    private float m_fontSize = 1.0f;
    
    /**
     * Letter spacing.
     */
    private int m_letterSpacing;

    /**
     * Line Spacing.
     */
    private int m_lineSpacing;

    public GLFontConfiguration(final String fontSet) {
        this(fontSet, 16, 16);
    }
    
    public GLFontConfiguration(final String fontSet, final int fontWidth, final int fontHeight) {
        m_fontSet = fontSet;
        
        m_fontWidth = fontWidth;
        m_fontHeight = fontHeight;
    }

    public String getFontSet() {
        return m_fontSet;
    }
    
    public int getFontWidth() {
        return m_fontWidth; 
    }
    
    public int getFontHeight() {
        return m_fontHeight; 
    }

    public float getSize() {
        return m_fontSize;
    }
    
    public void setSize(final float size) {
        m_fontSize = size; 
    }
    
    public int getLetterSpacing() {
        return m_letterSpacing;
    }
    
    public void setLetterSpacing(final int spacing) {
        m_letterSpacing = spacing; 
    }
    
    public int getLineSpacing() {
        return m_lineSpacing;
    }
    
    public void setLineSpacing(final int spacing) {
        m_lineSpacing = spacing; 
    }
    
    public int getLineHeight() {
        return (int) (m_fontHeight * getSize()); 
    }

}
