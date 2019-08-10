package com.rgames.rubickscube.view.objects.common;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

/**
 */
public final class Object3DTransformation {

    /**
     */
    private Vector3f m_initialScale = new Vector3f(1f, 1f, 1f);

    /**
     */
    private Vector3f m_initialRotation = new Vector3f(0f, 0f, 0f);

    /**
     */
    private Vector3f m_initialPosition = new Vector3f(0f, 0f, 0f);
    
    /**
     */
    private Vector3f m_scale = new Vector3f(m_initialScale);
    
    /**
     */
    private Vector3f m_rotation = new Vector3f(m_initialRotation);

    /**
     */
    private Vector3f m_position = new Vector3f(m_initialPosition);
    
    /**
     */
    private FloatBuffer m_rotationMatrixBuffer = BufferUtils.createFloatBuffer(16);
    
    public Object3DTransformation() {
        this(null, null, null);
    }
    
    public Object3DTransformation(final Vector3f initialScale) {
        this(initialScale, null, null);
    }
    
    public Object3DTransformation(final Vector3f initialScale, final Vector3f initialRotation) {
        this(initialScale, initialRotation, null);
    }

    public Object3DTransformation(final Vector3f initialScale, 
            final Vector3f initialRotation, final Vector3f initialPosition) {
        
        if (initialScale != null) {
            m_initialScale.set(initialScale);
        }
        
        if (initialRotation != null) {
            m_initialRotation.set(initialRotation);
        }
        
        if (initialPosition != null) {
            m_initialPosition.set(initialPosition);
        }
        
        initRotationMatrix();
    }
    
    public Vector3f getInitialPosition() {
        return m_position;
    }

    public Vector3f getInitialRotation() {
        return m_rotation;
    }
    
    public Vector3f getInitialScale() {
        return m_scale;
    }

    public Vector3f getPosition() {
        return m_position;
    }

    public Vector3f getRotation() {
        return m_rotation;
    }
    
    public Vector3f getScale() {
        return m_scale;
    }

    public FloatBuffer getRotationMatrixBuffer() {
        return m_rotationMatrixBuffer;
    }

    public void setInitialScale(final float x, final float y, final float z) {
        m_initialScale.set(x, y, z);
        
        setScale(x, y, z);
    }
    
    public void setInitialRotation(final float x, final float y, final float z) {
        m_initialRotation.set(x, y, z);
        
        rotate(x, y, z);
    }

    public void setInitialPosition(final float x, final float y, final float z) {
        m_initialPosition.set(x, y, z);
        
        setPosition(x, y, z);
    }
    
    public void setScale(final float x, final float y, final float z) {
        m_scale.set(x, y, z);
    }
    
    public void setRotation(final float x, final float y, final float z) {
        m_rotation.set(x, y, z);
        
        resetRotationMatrix();
        rotate(x, 1f, 0f, 0f);
        rotate(y, 0f, 1f, 0f);
        rotate(z, 0f, 0f, 1f);
    }

    public void setPosition(final float x, final float y, final float z) {
        m_position.set(x, y, z);
    }
    
    public void scale(final float x, final float y, final float z) {
        m_scale.translate(x, y, z);
    }
    
    public void rotate(final float x, final float y, final float z) {
        m_rotation.translate(x, y, z);
        if (m_rotation.x > 360f) {
            m_rotation.x -= 360f;
        }
        
        if (m_rotation.y > 360f) {
            m_rotation.y -= 360f;
        }
        
        if (m_rotation.z > 360f) {
            m_rotation.z -= 360f;
        }
        
        rotate(x, 1f, 0f, 0f);
        rotate(y, 0f, 1f, 0f);
        rotate(z, 0f, 0f, 1f);
    }

    public void translate(final float x, final float y, final float z) {
        m_position.translate(x, y, z);
    }

    public void reset() {
        resetScale();
        resetRotationMatrix();
        resetTranslation();
        
        m_rotation.set(m_initialRotation.x, m_initialRotation.y, m_initialRotation.z);
    }
    
    public void resetScale() {
        setScale(m_initialScale.x, m_initialScale.y, m_initialScale.z);
    }
    
    public void resetRotationMatrix() {
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
        
        rotate(m_initialRotation.x, m_initialRotation.y, m_initialRotation.z);
    }
    
    public void resetTranslation() {
        setPosition(m_initialPosition.x, m_initialPosition.y, m_initialPosition.z);
    }

    private void initRotationMatrix() {
        m_rotationMatrixBuffer.clear();
        m_rotationMatrixBuffer.put(0, 1f);
        m_rotationMatrixBuffer.put(5, 1f);
        m_rotationMatrixBuffer.put(10, 1f);
        m_rotationMatrixBuffer.put(15, 1f);
    }
    
    private void rotate(final float angle, final float x, final float y, final float z) {
        GL11.glPushMatrix();
        GL11.glLoadIdentity(); 
        
        GL11.glRotatef(angle, x, y, z);
        
        GL11.glMultMatrix(m_rotationMatrixBuffer);
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, m_rotationMatrixBuffer);
        GL11.glPopMatrix();
    }
    
}
