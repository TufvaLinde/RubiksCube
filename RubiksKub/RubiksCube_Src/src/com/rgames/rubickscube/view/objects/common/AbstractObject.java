package com.rgames.rubickscube.view.objects.common;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import com.rgames.rubickscube.view.general.opengl.GLApp;
import com.rgames.rubickscube.view.general.opengl.GLAppRenderUtil;

/**
 */
public abstract class AbstractObject implements IObject {

    /**
     */
    public static final String DEFAULT_TEXTURE = "default";
    
    /**
     */
    private final int m_id;
    
    /**
     */
    private final String m_name;
    
    /**
     */
    private boolean m_isHidden;
    
    /**
     */
    private Map<String, Integer> m_textures = new HashMap<String, Integer>();
    
    /**
     */
    private FloatBuffer m_color = GLAppRenderUtil.initColor();
    
    protected AbstractObject(final String name) {
        m_id = GLApp.getNextObjectId();
        m_name = name;
    }
    
    public final int getId() {
        return m_id;
    }

    public final String getName() {
        return m_name;
    }
    
    public final void hide(final boolean shouldHide) {
        m_isHidden = shouldHide;
    }

    public final boolean isHidden() {
        return m_isHidden;
    }

    public final FloatBuffer getColor() {
        return m_color;
    }

    public final void setColor(final float r, final float g, final float b, final float a) {
        m_color.put(0, r);
        m_color.put(1, g);
        m_color.put(2, b);
        m_color.put(3, a);
    }
    
    public final int getTexture(final String materialName) {
        final Integer textureHandle = m_textures.get(materialName);
        
        return textureHandle != null ? textureHandle : 0;
    }
    
    public final void addTexture(final int textureHandle) {
        addTexture(DEFAULT_TEXTURE, textureHandle);
    }

    public final void addTexture(final String materialName, final int textureHandle) {
        if (!m_textures.containsKey(materialName)) {
            m_textures.put(materialName, textureHandle);
        }
    }
    
    public final void setTransparency(final float transparency) {
        m_color.put(3, transparency);
    }
    
}
