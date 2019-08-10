package com.rgames.rubickscube.view.objects.common;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import com.rgames.rubickscube.view.general.opengl.GLAppRenderUtil;

/**
 */
public abstract class AbstractObject3D extends AbstractObject implements IObject3D {

    /**
     */
    private Object3DTransformation m_transformation = new Object3DTransformation();
    
    /**
     */
    private Object3DAnimation m_animation = new Object3DAnimation(this);

    /**
     */
    private Vector3f m_reflection = new Vector3f(1.0f, 1.0f, 1.0f);

    protected AbstractObject3D(final String name) {
        super(name);
    }

    public final Vector3f getInitialScale() {
        return m_transformation.getInitialScale();
    }
    
    public final Vector3f getInitialRotation() {
        return m_transformation.getInitialRotation();
    }
    
    public final Vector3f getInitialPosition() {
        return m_transformation.getInitialPosition();
    }
    
    public final Vector3f getScale() {
        return m_transformation.getScale();
    }

    public final Vector3f getRotation() {
        return m_transformation.getRotation();
    }

    public final Vector3f getPosition() {
        return m_transformation.getPosition();
    }

    public final Object3DTransformation getTransformation() {
        return m_transformation;
    }
    
    public final Object3DAnimation getAnimation() {
        return m_animation;
    }

    public final void setInitialScale(final float x, final float y, final float z) {
        m_transformation.setInitialScale(x, y, z);
    }

    public final void setInitialRotation(final float x, final float y, final float z) {
        m_transformation.setInitialRotation(x, y, z);
    }

    public final void setInitialPosition(final float x, final float y, final float z) {
        m_transformation.setInitialPosition(x, y, z);
    }
    
    public final void setScale(final float x, final float y, final float z) {
        m_transformation.setScale(x, y, z);
    }
    
    public final void setRotation(final float x, final float y, final float z) {
        m_transformation.setRotation(x, y, z);
    }

    public final void setPosition(final float x, final float y, final float z) {
        m_transformation.setPosition(x, y, z);
    }

    public final void setReflection(final float x, final float y, final float z) {
        m_reflection.set(x, y, z);
    }
    
    public final void scale(final float x, final float y, final float z) {
        m_transformation.scale(x, y, z);
    }

    public final void rotate(final float x, final float y, final float z) {
        m_transformation.rotate(x, y, z);
    }

    public final void translate(final float x, final float y, final float z) {
        m_transformation.translate(x, y, z);
    }

    public final void reset() {
        m_transformation.reset();
    }
    
    public final void resetScale() {
        m_transformation.resetScale();
    }

    public final void resetRotation() {
        m_transformation.resetRotationMatrix();
    }
    
    public final void resetTranslation() {
        m_transformation.resetTranslation();
    }

    public final void draw() {
        if (isHidden()) {
            return;
        }

        GL11.glLoadName(getId());

        if (m_animation.isActive()) {
            m_animation.animate();
        }
        
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        
        // relfection...
        if (m_reflection.y < 0) {
            GL11.glFrontFace(GL11.GL_CW);
        } else {
            GL11.glFrontFace(GL11.GL_CCW);
        }
        
        GL11.glScalef(getScale().x, getScale().y, getScale().z);
        GL11.glScalef(m_reflection.x, m_reflection.y, m_reflection.z);
        GL11.glTranslatef(getPosition().x, getPosition().y, getPosition().z);
        GL11.glMultMatrix(getTransformation().getRotationMatrixBuffer());
        
        // set the material
        GL11.glMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT_AND_DIFFUSE, getColor());
        
        drawObject();

        // set the material
        GL11.glMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT_AND_DIFFUSE, GLAppRenderUtil.getDefaultColor());
        
        GL11.glPopMatrix();
    }
    
    protected abstract void drawObject();
}
