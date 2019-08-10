package com.rgames.rubickscube.controller.general;

import org.apache.log4j.Logger;

/**
 */
public final class Logging {

    /**
     */
    private static final Logger LOGGER = getLogger();
    
    private Logging() {
        // empty
    }
    
    public static Logger getDefault() {
        return LOGGER;
    }
    
    private static Logger getLogger() {
        return Logger.getLogger(Logging.class);
    }
}
