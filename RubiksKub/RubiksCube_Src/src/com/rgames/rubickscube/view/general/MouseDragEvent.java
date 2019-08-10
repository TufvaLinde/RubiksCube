package com.rgames.rubickscube.view.general;

/**
 */
public final class MouseDragEvent {

    /**
     */
    private boolean m_isDragging;
    
    /**
     */
    private int m_lastX;
    
    /**
     */
    private int m_lastY;

    public MouseDragEvent() {
        
    }

    public void dragEnd() {
        m_isDragging = false;
    }
    
    public void dragStart(final int startX, final int startY) {
        m_isDragging = true;

        dragTo(startX, startY);
    }
    
    public boolean canDrag(final int x, final int y) {
        if (!m_isDragging) {
            return false;
        }
        
        if (x == m_lastX && y == m_lastY) {
            return false;
        }
        
        return true;
    }
    
    public void dragTo(final int x, final int y) {
        m_lastX = x;
        m_lastY = y;
    }
    
    public int getLastX() {
        return m_lastX;
    }

    public int getLastY() {
        return m_lastY;
    }
}
