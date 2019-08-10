package com.rgames.rubickscube.view.general;

import com.rgames.rubickscube.model.util.MathUtil;

/**
 */
public final class FPSCounter {

    /**
     */
    private static final int MILLISECONDS_PER_COUNT = 1000; // frames per second 
    
    /**
     */
    private long m_startTime;
    
    /**
     */
    private long m_framesRendered;
    
    /**
     */
    private float m_fps;
    
    public FPSCounter() {
        
    }
    
    public void init() {
        m_startTime = System.currentTimeMillis();
    }
    
    public void calculate() {
        if (m_startTime == 0) {
            return;
        }
        
        m_framesRendered++;
        
        final long now = System.currentTimeMillis();
        m_fps = m_framesRendered / ((now - m_startTime) / (float) MILLISECONDS_PER_COUNT);  
    }
    
    public float getFps() {
        return m_fps;
    }
    
    public String toString() {
        return Float.toString(MathUtil.round(m_fps, 2)); 
    }
}
