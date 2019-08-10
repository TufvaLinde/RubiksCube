package com.rgames.rubickscube.view.general.opengl;

import java.io.IOException;
import java.util.Properties;

/**
 */
public final class GLAppSettings {

    /** 
     * Application settings file. 
     */
    public static final String SETTINGS_FILE = "config.ini";

    /**
     * Display settings
     */
    private GLAppDisplaySettings m_displaySettings;

    /** 
     * holds settings (see loadSettings()). 
     */
    private Properties m_settings;   //
    
    public GLAppSettings() {
        m_displaySettings = new GLAppDisplaySettings(this);
    }
    
    public Properties getSettings() {
        return m_settings;
    }

    public GLAppDisplaySettings getDisplaySettings() {
        return m_displaySettings;
    }

    public void loadSettings() throws IOException {
        loadSettings(SETTINGS_FILE);
    }
    
    /**
     * Load configuration settings from optional properties file.
     * @param configFilename
     */
    public void loadSettings(final String configFilename) throws IOException {
        m_settings = new Properties();
        m_settings.load(GLAppUtil.getInputStream(configFilename));
    }
}
