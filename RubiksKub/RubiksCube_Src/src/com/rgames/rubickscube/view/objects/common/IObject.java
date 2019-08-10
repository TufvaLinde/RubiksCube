package com.rgames.rubickscube.view.objects.common;

import java.nio.FloatBuffer;

/**
 */
public interface IObject {

    String getName();

    int getId();
    
    FloatBuffer getColor();

    int getTexture(final String materialName);
    
    boolean isHidden();

    void draw();
    
    void hide(final boolean shouldHide);
    
    void setColor(final float r, final float g, final float b, final float a);
    
    void setTransparency(final float transparency);
    
    void addTexture(final int textureHandle);
    
    void addTexture(final String materialName, final int textureHandle);
    
    void reset();

    void resetScale();

    void resetRotation();
    
    void resetTranslation();    
}
