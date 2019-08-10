package com.rgames.rubickscube.model;

import com.rgames.rubickscube.model.cube.RubicksCube;

/**
 */
public final class GameModel {

    /**
     */
    private RubicksCube m_cube = new RubicksCube();
    
    /**
     */
    private long m_gameStartTime = -1;

    /**
     */
    private long m_gamePlayTime = -1;

    /**
     */
    private long m_gameEndTime = -1;

    /**
     */
    private boolean m_gameWin;
    
    /**
     */
    private boolean m_gamePaused;

    public GameModel() {
        // initially scramble the cube
        m_cube.scramble();
    }
    
    public RubicksCube getCube() {
        return m_cube;
    }
    
    public long getStartTime() {
        return m_gameStartTime;
    }
    
    public long getEndTime() {
        return m_gameEndTime;
    }

    public long getPlayTime() {
        if (isPaused()) {
            return m_gamePlayTime;
        }
        
        if (m_gameEndTime < 0) {
            if (hasStarted()) {
                m_gamePlayTime = System.currentTimeMillis() - m_gameStartTime;
                
                return m_gamePlayTime;
            } else {
                return m_gameStartTime;
            }
        }
        
        return m_gameEndTime - m_gameStartTime;
    }
    
    public void start() {
        if (!hasStarted()) {
            m_gameStartTime = System.currentTimeMillis();
        }
    }
    
    public void pause() {
        if (!hasStarted()) {
            return;
        }
        
        m_gamePaused = !isPaused();
        
        if (!isPaused()) {
            m_gameStartTime += System.currentTimeMillis() - (m_gamePlayTime + m_gameStartTime);
        }
    }
    
    public void reset() {
        m_gameWin = false;
        m_gamePaused = false;
        
        m_gameStartTime = -1;
        m_gamePlayTime = -1;
        m_gameEndTime = -1;
        
        m_cube.reset();
    }
    
    public boolean hasStarted() {
        return m_gameStartTime > -1;
    }
    
    public boolean hasEnded() {
        return m_gameWin;
    }
    
    public boolean isPaused() {
        return m_gamePaused;
    }
    
    public void updateState() {
        if (hasStarted()) {
            m_gameWin = m_cube.isSolved();
            
            if (hasEnded()) {
                m_gameEndTime = System.currentTimeMillis();
            }
        }
    }
}
