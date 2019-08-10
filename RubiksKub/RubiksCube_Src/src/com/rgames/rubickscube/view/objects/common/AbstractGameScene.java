package com.rgames.rubickscube.view.objects.common;

/**
 */
public abstract class AbstractGameScene implements IGameScene {

    /**
     */
    private long m_timeCreated;

    protected AbstractGameScene() {
        
    }
    
    public final long getTimeCreated() {
        return m_timeCreated;
    }
    
    public final void init() {
        createScene();
        
        m_timeCreated = System.currentTimeMillis();
    }
    
    protected abstract void createScene();
}
