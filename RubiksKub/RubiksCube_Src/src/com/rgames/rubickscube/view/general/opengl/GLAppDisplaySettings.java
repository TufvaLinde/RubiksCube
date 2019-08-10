package com.rgames.rubickscube.view.general.opengl;

/**
 */
public final class GLAppDisplaySettings {

    /**
     * The settings. 
     */
    private final GLAppSettings m_settings;
    
    /** 
     * title of the display. 
     */
    private String m_title = "GLApp";

    
    /** 
     * width of the display. 
     */
    private int m_width = 1024;
    
    /** 
     * height of the display. 
     */
    private int m_height = 768;
    
    /** 
     * refresh frequency of the display. 
     */
    private int m_frequency = 75;
    
    /** 
     * bit depth of the display. 
     */
    private int m_colorBits = 32;
    
    /** 
     * bits per pixel in the depth buffer. 
     */
    private int m_bufferBits = 24;
    
    /** 
     * full screen or floating window. 
     */
    private boolean m_fullScreen = true;
    
    /** 
     * aspect ratio of Display (will default to displayWidth/displayHeight). 
     */
    private float m_aspectRatio;
    
    /** 
     * viewport position (will default to 0,0). 
     */
    private int m_viewportX; 

    /** 
     * viewport position (will default to 0,0). 
     */
    private int m_viewportY;

    /** 
     * viewport size (will default to screen width, height). 
     */
    private int m_viewportW;
    
    /** 
     * viewport size (will default to screen width, height). 
     */
    private int m_viewportH;
    
    public GLAppDisplaySettings(final GLAppSettings settings) {
        m_settings = settings;
    }
    
    public void initFromProperties() {
        m_title = getPropertyString("displayTitle");
        m_width = getPropertyInt("displayWidth");
        m_height = getPropertyInt("displayHeight");
        m_colorBits = getPropertyInt("displayColorBits");
        m_frequency = getPropertyInt("displayFrequency");
        m_bufferBits = getPropertyInt("depthBufferBits");
        m_aspectRatio = getPropertyFloat("aspectRatio");
        m_fullScreen = getPropertyString("fullScreen").toUpperCase().equals("YES");
    }
    
    private String getPropertyString(final String propertyName) {
        return m_settings.getSettings().getProperty(propertyName) != null ? m_settings.getSettings().getProperty(propertyName) : "";
    }

    private int getPropertyInt(final String propertyName) {
        if (m_settings.getSettings().getProperty(propertyName) != null) {
            try {
                return Integer.parseInt(m_settings.getSettings().getProperty(propertyName));
            } catch (final NumberFormatException e) {
                // ignored
            }
        }
        
        return 0;
    }
    
    private float getPropertyFloat(final String propertyName) {
        if (m_settings.getSettings().getProperty(propertyName) != null) {
            try {
                return Float.parseFloat(m_settings.getSettings().getProperty(propertyName));
            } catch (final NumberFormatException e) {
                // ignored
            }
        }
        
        return 0;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return m_title;
    }

    /**
     * @return the aspectRatio
     */
    public float getAspectRatio() {
        return m_aspectRatio;
    }

    /**
     * @return the aspectRatio
     */
    public void setAspectRatio(final float ratio) {
        m_aspectRatio = ratio;
    }

    /**
     * @return the bufferBits
     */
    public int getBufferBits() {
        return m_bufferBits;
    }

    /**
     * @return the dolorBits
     */
    public int getColorBits() {
        return m_colorBits;
    }

    /**
     * @return the frequency
     */
    public int getFrequency() {
        return m_frequency;
    }

    /**
     * @return the fullScreen
     */
    public boolean isFullScreen() {
        return m_fullScreen;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return m_height;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return m_width;
    }

    /**
     * @return the viewportH
     */
    public int getViewportH() {
        return m_viewportH;
    }

    /**
     * @return the viewportH
     */
    public void setViewportH(final int height) {
        m_viewportH = height;
    }

    /**
     * @return the viewportW
     */
    public int getViewportW() {
        return m_viewportW;
    }

    /**
     * @return the viewportH
     */
    public void setViewportW(final int width) {
        m_viewportW = width;
    }

    /**
     * @return the viewportX
     */
    public int getViewportX() {
        return m_viewportX;
    }

    /**
     * @return the viewportX
     */
    public void setViewportX(final int x) {
        m_viewportX = x;
    }

    /**
     * @return the viewportY
     */
    public int getViewportY() {
        return m_viewportY;
    }

    /**
     * @return the viewportX
     */
    public void setViewportY(final int y) {
        m_viewportY = y;
    }
}
