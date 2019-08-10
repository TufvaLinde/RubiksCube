package com.rgames.rubickscube.view.objects.objects2d;

import org.lwjgl.opengl.GL11;

import com.rgames.rubickscube.view.general.opengl.GLAppRenderUtil;
import com.rgames.rubickscube.view.objects.common.AbstractObject2D;

/**
 */
public final class Image2D extends AbstractObject2D {

    public Image2D() {
        super("RotateArrow2D");
    }
    
    protected void drawObject() {
        GL11.glLoadName(getId());
        
        GLAppRenderUtil.renderQuad2D(0, 0, (int) getScale().x, (int) getScale().y);
        //GLAppUtil.renderQuad2D((int) getPosition().x, (int) getPosition().y, 
        //        (int) getScale().x, (int) getScale().y);
    }

}
