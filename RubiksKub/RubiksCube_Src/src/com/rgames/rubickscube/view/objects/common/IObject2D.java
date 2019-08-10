package com.rgames.rubickscube.view.objects.common;

import org.lwjgl.util.vector.Vector2f;

import com.rgames.rubickscube.view.general.IMouseListener;

/**
 * Interface for a generic three dimensional object.
 */
public interface IObject2D extends IObject {

    Object2DTransformation getTransformation();
    
    Vector2f getScale();

    Vector2f getInitialScale();

    float getRotation();

    float getInitialRotation();
    
    Vector2f getPosition();
    
    Vector2f getInitialPosition();

    IMouseListener getMouseListener();
    
    boolean mouseOver(final int mouseX, final int mouseY);
    
    void scale(final float x, final float y);

    void rotate(final float x);

    void translate(final float x, final float y);
    
    void setInitialScale(final float x, final float y);
    
    void setInitialRotation(final float x);
    
    void setInitialPosition(final float x, final float y);
    
    void setScale(final float x, final float y);

    void setPosition(final float x, final float y);
    
    void addMouseListener(final IMouseListener mouseListener);
    
}
