package com.rgames.rubickscube.view.general.opengl;

import org.apache.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

import com.rgames.rubickscube.controller.general.Logging;

/**
 */
public final class GLAppDisplayMode {

    /** 
     * Logger for this class. 
     */
    private static final Logger LOG = Logging.getDefault();

    /**
     * Default display settings (display settings in default.cfg will override these)
     * initDisplay() uses these to pick a Display and setup viewport shape.
     */
    private DisplayMode m_displayMode;

    /** 
     * Hold the display mode when app first executes. 
     */
    private DisplayMode m_origDisplayMode;

    public GLAppDisplayMode() {
    }
    
    public DisplayMode getCurrent() {
        return m_displayMode;
    }
    
    public DisplayMode getOriginal() {
        return m_origDisplayMode;
    }
    
    public boolean init() {
        m_origDisplayMode = Display.getDisplayMode();
        
        LOG.debug("GLApp.initDisplay(): Current display mode is " + getOriginal());
        
        // Get the settings
        final GLAppDisplaySettings settings = GLApp.getDisplaySettings();

        // Set display mode
        setDisplayMode(settings);
        
        // Create & initialize the Window
        createWindow(settings);
        
        // Set viewport width/height and Aspect ratio.
        if (settings.getAspectRatio() == 0f) {
            // no aspect ratio was set in GLApp.cfg: default to full screen.
            settings.setAspectRatio((float) m_displayMode.getWidth() / (float) m_displayMode.getHeight());
        }
        
        // viewport may not match shape of screen.  Adjust to fit.
        settings.setViewportH(m_displayMode.getHeight());                        // viewport Height
        settings.setViewportW((int) (m_displayMode.getHeight() * settings.getAspectRatio()));  // Width
        if (settings.getViewportW() > m_displayMode.getWidth()) {
            settings.setViewportW(m_displayMode.getWidth());
            settings.setViewportH((int) (m_displayMode.getWidth() * (1 / settings.getAspectRatio())));
        }
        
        // center viewport in screen
        settings.setViewportX((int) ((m_displayMode.getWidth() - settings.getViewportW()) / 2));
        settings.setViewportX((int) ((m_displayMode.getHeight() - settings.getViewportH()) / 2));
        
        return true;
    }
    
    private void setDisplayMode(final GLAppDisplaySettings settings) {
        if (!initDisplayMode(settings)) {
            LOG.error("GLApp.initDisplay(): Can't find a compatible Display Mode!!!");
            System.exit(1);
        } else {
            LOG.debug("GLApp.initDisplay(): Setting display mode to " + m_displayMode + ", fullscreen=" + settings.isFullScreen());
            try {
                Display.setDisplayMode(m_displayMode);
            } catch (final LWJGLException e) {
                LOG.error(e);
                System.exit(1);
            }
        }
    }
    
    private void createWindow(final GLAppDisplaySettings settings) {
        try {
            Display.create(new PixelFormat(0, settings.getBufferBits(), 0));  // set bits per buffer: alpha, depth, stencil
            Display.setTitle(settings.getTitle());
            Display.setFullscreen(settings.isFullScreen());
            Display.setVSyncEnabled(true);
            
            LOG.debug("GLApp.initDisplay(): Created OpenGL window.");
        } catch (final LWJGLException e) {
            LOG.fatal("GLApp.initDisplay(): Failed to create OpenGL window: " + e);
            System.exit(1);
        }
    }
    
    private boolean initDisplayMode(final GLAppDisplaySettings settings) {
        m_displayMode = getDisplayMode(settings.getWidth(), settings.getHeight(), settings.getColorBits(), settings.getFrequency());
        if (m_displayMode == null) {
            m_displayMode = getDisplayMode(1024, 768, 32, 60);
            if (m_displayMode == null) {
                m_displayMode = getDisplayMode(1024, 768, 16, 60);
                if (m_displayMode == null) {
                    m_displayMode = getDisplayMode(m_origDisplayMode.getWidth(), m_origDisplayMode.getHeight(), m_origDisplayMode.getBitsPerPixel(), m_origDisplayMode.getFrequency());
                }
            }
        }
        
        return m_displayMode != null;
    }
    
    /**
     * Retrieve a DisplayMode object with the given params.
     */
    private static DisplayMode getDisplayMode(final int w, final int h, final int colorBits, final int freq) {
        try {
            final DisplayMode[] adisplaymode = Display.getAvailableDisplayModes();
            DisplayMode dm = null;
            for (int j = 0; j < adisplaymode.length; j++) {
                dm = adisplaymode[j];
                if (dm.getWidth() == w && dm.getHeight() == h && dm.getBitsPerPixel() == colorBits 
                        && dm.getFrequency() == freq) {
                    return dm;
                }
            }
        } catch (final LWJGLException e) {
            LOG.error("GLApp.getDisplayMode(): Exception " + e);
        }
        
        return null;
    }

    
}
