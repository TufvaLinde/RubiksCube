package com.rgames.rubickscube.view.general.opengl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.apache.log4j.Logger;

import com.rgames.rubickscube.controller.general.Logging;

/**
 */
public final class GLAppUtil {

    /** 
     * Used when allocating native buffers for data types. 
     */
    public static final int SIZE_DOUBLE = 8;
    
    /** 
     * Used when allocating native buffers for data types. 
     */
    public static final int SIZE_FLOAT = 4;
    
    /** 
     * Used when allocating native buffers for data types. 
     */
    public static final int SIZE_INT = 4;
    
    /** 
     * Used when allocating native buffers for data types. 
     */
    public static final int SIZE_BYTE = 1;

    /** */
    private static final Logger LOG = Logging.getDefault();

    private GLAppUtil() {
        // utility class
    }
    
    /**
     * Open a file InputStream and trap errors.
     * @param filenameOrURL
     * @return
     */
    public static InputStream getInputStream(final String filename) {
        InputStream in = null;
        try {
            in = new FileInputStream(filename);
        } catch (final IOException ioe) {
            LOG.error("GLApp.getInputStream (" + filename + "): " + ioe);
            if (in != null) {
                try {
                    in.close();
                    in = null;
                } catch (final IOException e) {
                    // ingored?
                }
            }
        }
        
        if (in == null) {
            // Couldn't open file: try looking in jar.
            // NOTE: this will look only in the folder that this class is in.
            //URL u = getClass().getResource(filename);   // for non-static class
            final URL u = GLApp.class.getResource(filename);
            if (u != null) {
                try {
                    in = u.openStream();
                } catch (final IOException e) {
                    LOG.error("GLApp.getInputStream (" + filename + "): Can't load from jar: " + e);
                }
            }
        }
        
        return in;
    }
    
    /**
     * Find a power of two big enough to hold the given dimension.  Ie.
     * to make a texture big enough to hold a screen image for an 800x600
     * screen getPowerOfTwoBiggerThan(800) will return 1024.
     * <P>
     * @see makeTextureForScreen()
     * @param dimension
     * @return a power of two bigger than the given dimension
     */
    public static int getPowerOfTwoBiggerThan(final int dimension) {
        for (int exp = 1; exp <= 32; exp++) {
            if (dimension <= Math.pow(2, exp)) {
                return (int) Math.pow(2, exp);
            }
        }
        return 0;
    }
    
    /* ========================================================================
     * Buffer allocation functions
     *
     * These functions create and populate the native buffers used by LWJGL.
     * ======================================================================== */

    public static ByteBuffer allocBytes(final int howmany) {
        return ByteBuffer.allocateDirect(howmany * SIZE_BYTE).order(ByteOrder.nativeOrder());
    }

    public static IntBuffer allocInts(final int howmany) {
        return ByteBuffer.allocateDirect(howmany * SIZE_INT).order(ByteOrder.nativeOrder()).asIntBuffer();
    }

    public static FloatBuffer allocFloats(final int howmany) {
        return ByteBuffer.allocateDirect(howmany * SIZE_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
    }

    public static DoubleBuffer allocDoubles(final int howmany) {
        return ByteBuffer.allocateDirect(howmany * SIZE_DOUBLE).order(ByteOrder.nativeOrder()).asDoubleBuffer();
    }

    public static ByteBuffer allocBytes(final byte[] bytearray) {
        final ByteBuffer bb = ByteBuffer.allocateDirect(bytearray.length * SIZE_BYTE).order(ByteOrder.nativeOrder());
        bb.put(bytearray).flip();
        return bb;
    }

    public static IntBuffer allocInts(final int[] intarray) {
        final IntBuffer ib = ByteBuffer.allocateDirect(intarray.length * SIZE_FLOAT).order(ByteOrder.nativeOrder()).asIntBuffer();
        ib.put(intarray).flip();
        return ib;
    }

    public static FloatBuffer allocFloats(final float[] floatarray) {
        final FloatBuffer fb = ByteBuffer.allocateDirect(floatarray.length * SIZE_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        fb.put(floatarray).flip();
        return fb;
    }
    
    public static int getLastNameFromPickList(final int hits, final IntBuffer buf) {
        int name = 0;
        for (int i = 0; i < hits; i++) {
            name = buf.get(i * 4 + 3);
            /*
            System.out.println(" #:" + buf.get(i * 4));
            System.out.println(" minZ:" + buf.get(i * 4 + 1));
            System.out.println(" maxZ:" + buf.get(i * 4 + 2));
            System.out.println(" name:" + buf.get(i * 4 + 3));*/
        }
        
        return name;
    }
    
    public static int[] intBuffToArray(final IntBuffer intBuff) {
        final int[] array = new int[intBuff.capacity()];
        int i = 0;
        while (i < array.length) {
            array[i++] = intBuff.get();
        }
        
        intBuff.rewind();
        return array;
    }
}
