package com.rgames.rubickscube.view.objects.common;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import com.rgames.rubickscube.view.general.IMouseListener;
import com.rgames.rubickscube.view.general.MouseListener;

/**
 */
public abstract class AbstractObject2D extends AbstractObject implements IObject2D {

    /**
     */
    private Object2DTransformation m_transformation = new Object2DTransformation();
    
    /**
     */
    private IMouseListener m_mouseListener = new MouseListener();
    
    protected AbstractObject2D(final String name) {
        super(name);
    }

    public final boolean mouseOver(final int mouseX, final int mouseY) {
        final int width = (int) getScale().x;
        final int height = (int) getScale().y;
        final int posX = (int) getPosition().x - width / 2;
        final int posY = (int) getPosition().y - height / 2;

        if (mouseX >= posX && mouseX < posX + width) {
            if (mouseY >= posY && mouseY < posY + height) {
                return true;
            }            
        }
        
        return false;
    }

    public final Object2DTransformation getTransformation() {
        return m_transformation;
    }

    public final Vector2f getInitialScale() {
        return m_transformation.getInitialScale();
    }
    
    public final float getInitialRotation() {
        return m_transformation.getInitialRotation();
    }
    
    public final Vector2f getInitialPosition() {
        return m_transformation.getInitialPosition();
    }
    
    public final Vector2f getScale() {
        return m_transformation.getScale();
    }

    public final float getRotation() {
        return m_transformation.getRotation();
    }

    public final Vector2f getPosition() {
        return m_transformation.getPosition();
    }

    public final void setInitialScale(final float x, final float y) {
        m_transformation.setInitialScale(x, y);
    }

    public final void setInitialRotation(final float x) {
        m_transformation.setInitialRotation(x);
    }

    public final void setInitialPosition(final float x, final float y) {
        m_transformation.setInitialPosition(x, y);
    }
    
    public final void setScale(final float x, final float y) {
        m_transformation.setScale(x, y);
    }
    
    public final void setPosition(final float x, final float y) {
        m_transformation.setPosition(x, y);
    }
    
    public final void scale(final float x, final float y) {
        m_transformation.scale(x, y);
    }

    public final void rotate(final float x) {
        m_transformation.rotate(x);
    }

    public final void translate(final float x, final float y) {
        m_transformation.translate(x, y);
    }

    public final void reset() {
        m_transformation.reset();
    }
    
    public final void resetScale() {
        m_transformation.resetScale();
    }

    public final void resetRotation() {
        m_transformation.resetRotation();
    }
    
    public final void resetTranslation() {
        m_transformation.resetTranslation();
    }

    public final void draw() {
        if (isHidden()) {
            return;
        }

        // set name
        GL11.glLoadName(getId());

        // set color
        GL11.glColor4f(getColor().get(), getColor().get(), getColor().get(), getColor().get());
        getColor().rewind();

        // enable blending
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glFrontFace(GL11.GL_CCW);
        
        // Bind the texture for the cube
        if (getTexture(DEFAULT_TEXTURE) > 0) {
            // Enable texturing
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, getTexture(DEFAULT_TEXTURE));
        }
        
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        
        GL11.glTranslatef(getPosition().x - getScale().x / 2, getPosition().y - getScale().y / 2, 0f);
        GL11.glMultMatrix(getTransformation().getRotationMatrixBuffer());

        // draw the object
        drawObject();
        
        GL11.glPopMatrix();
        
        // reset color
        GL11.glColor4f(1f, 1f, 1f, 1f);
    }
    
    public final void addMouseListener(final IMouseListener mouseListener) {
        m_mouseListener = mouseListener;
    }
    
    public final IMouseListener getMouseListener() {
        return m_mouseListener;
    }
    
    protected abstract void drawObject();
}
