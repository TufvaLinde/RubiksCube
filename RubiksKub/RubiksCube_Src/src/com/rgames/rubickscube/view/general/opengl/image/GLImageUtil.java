package com.rgames.rubickscube.view.general.opengl.image;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import com.rgames.rubickscube.view.general.opengl.GLAppUtil;

/**
 */
public final class GLImageUtil {
    
    private GLImageUtil() {
        
    }

    /**
     * Load an image from the given file and return a GLImage object.
     * @param imgFilename
     * @return
     */
    public static GLImage loadImage(final String imgFilename) {
        final GLImage img = new GLImage(imgFilename);
        if (img.isLoaded()) {
            return img;
        }
        
        return null;
    }

    /**
     * Load an image from the given file and return a ByteBuffer containing RGBA pixels.<BR>
     * Can be used to create textures. <BR>
     * @param imgFilename
     * @return
     */
    public static ByteBuffer loadImagePixels(final String imgFilename) {
        final GLImage img = new GLImage(imgFilename);
        return img.getPixelBuffer();
    }

    /**
     * Load an image from the given file and return an IntBuffer containing ARGB pixels.<BR>
     * Can be used to create Native Cursors. <BR>
     * @param imgFilename
     * @return IntBuffer
     */
    public static IntBuffer loadImageInt(final String imgFilename) {
        final GLImage img = new GLImage(imgFilename);
        final int[] jpixels = img.getImagePixels();      // pixels in Java int format
        return GLAppUtil.allocInts(jpixels);  // convert to IntBuffer and return
    } 
}
