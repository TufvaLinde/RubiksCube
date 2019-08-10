package com.rgames.rubickscube.view.objects.objects3d;

import org.lwjgl.opengl.GL11;

import com.rgames.rubickscube.view.general.opengl.GLAppRenderUtil;
import com.rgames.rubickscube.view.objects.common.AbstractObject3D;

/**
 */
public final class GroundPlane3D extends AbstractObject3D {

    public GroundPlane3D() {
        super("GroundPlane");
    }
    
    protected void drawObject() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, getTexture(DEFAULT_TEXTURE));

        GLAppRenderUtil.renderPlane();
    }

}
