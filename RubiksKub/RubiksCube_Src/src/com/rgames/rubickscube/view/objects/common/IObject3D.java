package com.rgames.rubickscube.view.objects.common;

import org.lwjgl.util.vector.Vector3f;

/**
 * Interface for a generic three dimensional object.
 */
public interface IObject3D extends IObject {

    Object3DTransformation getTransformation();
    
    Object3DAnimation getAnimation();

    Vector3f getScale();

    Vector3f getInitialScale();

    Vector3f getRotation();

    Vector3f getInitialRotation();
    
    Vector3f getPosition();
    
    Vector3f getInitialPosition();
    
    void scale(final float x, final float y, final float z);

    void rotate(final float x, final float y, final float z);

    void translate(final float x, final float y, final float z);
    
    void setInitialScale(final float x, final float y, final float z);
    
    void setInitialRotation(final float x, final float y, final float z);
    
    void setInitialPosition(final float x, final float y, final float z);
    
    void setScale(final float x, final float y, final float z);

    void setRotation(final float x, final float y, final float z);

    void setPosition(final float x, final float y, final float z);
    
    void setReflection(final float x, final float y, final float z);
}
