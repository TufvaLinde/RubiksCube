package com.rgames.rubickscube.view.objects.common;

import java.util.ArrayList;
import java.util.List;


/**
 */
public final class GameScene2DObjects {

    /**
     */
    private final List<IObject2D> m_objects = new ArrayList<IObject2D>();
    
    public GameScene2DObjects() {
        
    }
    
    public void add(final IObject2D object) {
        m_objects.add(object);
    }
    
    public List<IObject2D> getObjects() {
        return m_objects;
    }
    
    public void draw() {
        for (IObject2D object : m_objects) {
            object.draw();
        }
    }
    
    public void notifyMouseMoved(final int mouseX, final int mouseY) {
        for (IObject2D object : m_objects) {
            object.getMouseListener().mouseMoved(mouseX, mouseY);
        }
    }

    public void notifyMouseDown(final int mouseX, final int mouseY) {
        for (IObject2D object : m_objects) {
            object.getMouseListener().mouseDown(mouseX, mouseY);
        }
    }

    public void notifyMouseUp(final int mouseX, final int mouseY) {
        for (IObject2D object : m_objects) {
            object.getMouseListener().mouseUp(mouseX, mouseY);
        }
    }
}
