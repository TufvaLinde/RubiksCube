package com.rgames.rubickscube.view.objects.common;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

/**
 */
public final class Object2DTransformation {

    /**
     */
    private float m_initialRotation;

    /**
     */
    private float m_rotation;

    /**
     */
    private Vector2f m_initialScale = new Vector2f(1f, 1f);

    /**
     */
    private Vector2f m_scale = new Vector2f(m_initialScale);
    
    /**
     */
    private Vector2f m_initialPosition = new Vector2f(0f, 0f);
    
    /**
     */
    private Vector2f m_position = new Vector2f(m_initialPosition);
    
    /**
     */
    private FloatBuffer m_rotationMatrixBuffer = BufferUtils.createFloatBuffer(16);
    
    public Object2DTransformation() {
        this(null, 0f, null);
    }
    
    public Object2DTransformation(final Vector2f initialScale) {
        this(initialScale, 0f, null);
    }
    
    public Object2DTransformation(final Vector2f initialScale, final float initialRotation) {
        this(initialScale, initialRotation, null);
    }

    public Object2DTransformation(final Vector2f initialScale, 
            final float initialRotation, final Vector2f initialPosition) {
        
        m_initialRotation = initialRotation;
        
        if (initialScale != null) {
            m_initialScale.set(initialScale);
        }
        
        if (initialPosition != null) {
            m_initialPosition.set(initialPosition);
        }
        
        initRotationMatrix();
    }
    
    public Vector2f getInitialPosition() {
        return m_position;
    }

    public float getInitialRotation() {
        return m_rotation;
    }
    
    public Vector2f getInitialScale() {
        return m_scale;
    }

    public Vector2f getPosition() {
        return m_position;
    }

    public float getRotation() {
        return m_rotation;
    }
    
    public Vector2f getScale() {
        return m_scale;
    }

    public FloatBuffer getRotationMatrixBuffer() {
        return m_rotationMatrixBuffer;
    }

    public void setInitialScale(final float x, final float y) {
        m_initialScale.set(x, y);
        
        setScale(x, y);
    }
    
    public void setInitialRotation(final float x) {
        m_initialRotation = x;
        
        rotate(x);
    }

    public void setInitialPosition(final float x, final float y) {
        m_initialPosition.set(x, y);
        
        setPosition(x, y);
    }
    
    public void setScale(final float x, final float y) {
        m_scale.set(x, y);
    }

    public void setPosition(final float x, final float y) {
        m_position.set(x, y);
    }
    
    public void scale(final float x, final float y) {
        m_scale.translate(x, y);
    }
    
    public void rotate(final float x) {
        m_rotation += x;
        if (m_rotation > 360f) {
            m_rotation -= 360f;
        }
        
        rotateMatrix(x);
    }

    public void translate(final float x, final float y) {
        m_position.translate(x, y);
    }

    public void reset() {
        resetScale();
        resetRotation();
        resetTranslation();
    }
    
    public void resetScale() {
        setScale(m_initialScale.x, m_initialScale.y);
    }
    
    public void resetRotation() {
        final Matrix4f mat = new Matrix4f();
        mat.load(m_rotationMatrixBuffer);
        m_rotationMatrixBuffer.rewind();
        
        mat.setIdentity();
        mat.store(m_rotationMatrixBuffer);
        m_rotationMatrixBuffer.rewind();

        GL11.glPushMatrix();
        GL11.glLoadIdentity(); 
        GL11.glMultMatrix(m_rotationMatrixBuffer);
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, m_rotationMatrixBuffer);
        GL11.glPopMatrix();
        
        rotateMatrix(m_initialRotation);
    }
    
    public void resetTranslation() {
        setPosition(m_initialPosition.x, m_initialPosition.y);
    }

    private void initRotationMatrix() {
        m_rotationMatrixBuffer.clear();
        m_rotationMatrixBuffer.put(0, 1f);
        m_rotationMatrixBuffer.put(5, 1f);
        m_rotationMatrixBuffer.put(10, 1f);
        m_rotationMatrixBuffer.put(15, 1f);
    }
    
    private void rotateMatrix(final float angle) {
        GL11.glPushMatrix();
        GL11.glLoadIdentity(); 
        
        GL11.glTranslatef(getScale().x / 2, getScale().y / 2, 0);
        GL11.glRotatef(angle, 0, 0, 1);
        GL11.glTranslatef(-getScale().x / 2, -getScale().y / 2, 0);
        
        GL11.glMultMatrix(m_rotationMatrixBuffer);
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, m_rotationMatrixBuffer);
        GL11.glPopMatrix();
    }
    
}
