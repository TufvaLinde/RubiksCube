package com.rgames.rubickscube.view.general.opengl.image;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.log4j.Logger;

import com.rgames.rubickscube.controller.general.Logging;

/**
 * Loads an Image from file, stores pixels as ARGB int array, and RGBA ByteBuffer.
 * An alternate constructor creates a GLImage from a ByteBuffer containing
 * pixel data.
 * <P>
 * Static functions are included to load, flip and convert pixel arrays.
 * <P>
 * napier at potatoland dot org
 */
public class GLImage {
    
    /**
     */
    public static final int SIZE_BYTE = 1;
    
    /**
     */
    private static final Logger LOG = Logging.getDefault();
    
    /**
     */
    private int m_h;
    
    /**
     */
    private int m_w;
    
    /**
     * store bytes in GL_RGBA format
     */
    private ByteBuffer m_pixelBuffer;
    
    /**
     */
    private int[] m_pixels;
    
    /**
     */
    private Image m_image;

    public GLImage() {
    }

    /**
     * Load pixels from an image file.
     * @param imgName
     */
    public GLImage(final String imgName) {
        loadImage(imgName);
    }

    /**
     * Store pixels passed in a ByteBuffer.
     * @param pixels
     * @param w
     * @param h
     */
    public GLImage(final ByteBuffer pixels, final int w, final int h) {
        this.m_pixelBuffer = pixels;
        this.m_pixels = null;
        this.m_image = null;  // image is not loaded from file
        this.m_h = h;
        this.m_w = w;
    }

    /**
     * @return true if image has been loaded successfully.
     */
    public final boolean isLoaded() {
        return m_image != null;
    }

    /**
     * Flip the image pixels vertically.
     */
    public final void flipPixels() {
        m_pixels = GLImage.flipPixels(m_pixels, m_w, m_h);
    }

    /**
     * Scale the image and regenerate pixel array
     */
    /*
    public void resize(float scaleW, float scaleH)
    {
        if (image != null) {
            image = image.getScaledInstance(
                //(int)((float)w*scaleW),
                //(int)((float)h*scaleH),
                600, -1,
                Image.SCALE_SMOOTH);
            if (image != null) {
                w = image.getWidth(null);
                h = image.getHeight(null);
                pixels = getImagePixels(); // pixels in default Java ARGB format
                pixelBuffer = convertImagePixels(pixels, w, h, true); // convert to RGBA bytes
            }
        }
    }
    */

    /**
     * Load an image file and hold its width/height.
     * @param imgName
     */
    public final void loadImage(final String imgName) {
        final Image tmpi = loadImageFromFile(imgName);
        if (tmpi != null) {
            m_w = tmpi.getWidth(null);
            m_h = tmpi.getHeight(null);
            m_image = tmpi;
            
            // pixels in default Java ARGB format
            m_pixels = getImagePixels();  
            
            // convert to RGBA bytes
            m_pixelBuffer = convertImagePixels(m_pixels, m_w, m_h, true);  

            LOG.info("GLImage: loaded " + imgName + ", width=" + m_w + " height=" + m_h);
        } else {
            LOG.error("GLImage: failed to load image " + imgName);
            
            m_image = null;
            m_pixels = null;
            m_pixelBuffer = null;
            m_h = 0;
            m_w = 0;
        }
    }

    /**
     * Return the image pixels in default Java int ARGB format.
     * @return
     */
    public final int[] getImagePixels() {
        if (m_pixels == null && m_image != null) {
            m_pixels = new int[m_w * m_h];
            final PixelGrabber pg = new PixelGrabber(m_image, 0, 0, m_w, m_h, m_pixels, 0, m_w);
            try {
                pg.grabPixels();
            } catch (final InterruptedException e) {
                LOG.error("Pixel Grabbing interrupted!");
                return null;
            }
        }
        
        return m_pixels;
    }

    /**
     * return int array containing pixels in ARGB format (default Java byte order).
     */
    public final int[] getPixelsARGB() {
        return m_pixels;
    }

    /**
     * return ByteBuffer containing pixels in RGBA format (commmonly used in OpenGL).
     */
    public final ByteBuffer getPixelsRGBA() {
        return m_pixelBuffer;
    }

    //========================================================================
    //
    // Utility functions to prepare pixels for use in OpenGL
    //
    //========================================================================

    /**
     * Flip an array of pixels vertically.
     * @param imgPixels
     * @param imgw
     * @param imgh
     * @return int[]
     */
    public static int[] flipPixels(final int[] imgPixels, final int imgw, final int imgh) {
        int[] flippedPixels = null;
        if (imgPixels != null) {
            flippedPixels = new int[imgw * imgh];
            for (int y = 0; y < imgh; y++) {
                for (int x = 0; x < imgw; x++) {
                    flippedPixels[ ((imgh - y - 1) * imgw) + x] = imgPixels[ (y * imgw) + x];
                }
            }
        }
        
        return flippedPixels;
    }

    /**
     * Convert ARGB pixels to a ByteBuffer containing RGBA pixels.<BR>
     * Can be drawn in ORTHO mode using:<BR>
     *         GL.glDrawPixels(imgW, imgH, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, byteBuffer); <BR>
     * If flipVertically is true, pixels will be flipped vertically (for OpenGL coord system).
     * @param imgFilename
     * @return ByteBuffer
     */
    public static ByteBuffer convertImagePixels(final int[] jpixels, final int imgw, final int imgh, final boolean flipVertically) {
        int[] flippedPixels = {};
        
        // will hold pixels as RGBA bytes
        byte[] bytes;     
        if (flipVertically) {
            // flip Y axis
            flippedPixels = flipPixels(jpixels, imgw, imgh); 
        }
        bytes = convertARGBtoRGBA(flippedPixels);
        
        // convert to ByteBuffer and return
        return allocBytes(bytes);  
    }

    /**
     * Convert pixels from java default ARGB int format to byte array in RGBA format.
     * @param jpixels
     * @return
     */
    public static byte[] convertARGBtoRGBA(final int[] jpixels) {
        // will hold pixels as RGBA bytes
        final byte[] bytes = new byte[jpixels.length * 4];  
        
        int p;
        int r;
        int g;
        int b;
        int a;
        
        int j = 0;
        for (int i = 0; i < jpixels.length; i++) {
            //final int outPixel = 0x00000000;
            p = jpixels[i];
            
            // get pixel bytes in ARGB order
            a = (p >> 24) & 0xFF;  
            r = (p >> 16) & 0xFF;
            g = (p >> 8) & 0xFF;
            b = (p >> 0) & 0xFF;
            
            // fill in bytes in RGBA order
            bytes[j + 0] = (byte) r;
            bytes[j + 1] = (byte) g;
            bytes[j + 2] = (byte) b;
            bytes[j + 3] = (byte) a;
            j += 4;
        }
        return bytes;
    }

    //========================================================================
    // Utility functions to load file into byte array
    // and create Image from bytes.
    //========================================================================

    /**
     * Same function as in GLApp.java.  Allocates a ByteBuffer to hold the given
     * array of bytes.
     *
     * @param bytearray
     * @return  ByteBuffer containing the contents of the byte array
     */
    public static ByteBuffer allocBytes(final byte[] bytearray) {
        final ByteBuffer bb = ByteBuffer.allocateDirect(bytearray.length * SIZE_BYTE).order(ByteOrder.nativeOrder());
        bb.put(bytearray).flip();
        return bb;
    }

    /**
     * Load an image from file.  Avoids the flaky MediaTracker/ImageObserver headache.
     * Assumes that the file can be loaded quickly from the local filesystem, so
     * does not need to wait in a thread.  If it can't find the file in the
     * filesystem, will try loading from jar file.  If not found will return
     * null.
     * <P>
     * @param imgName
     */
    public static Image loadImageFromFile(final String imgName) {
        final byte[] imageBytes = getBytesFromFile(imgName);
        Image tmpi = null;

        if (imageBytes == null) {
            // couldn't read image from file: try JAR file
            // URL url = getClass().getResource(filename);   // for non-static class
            tmpi = getImageFromJar(imgName);
        } else {
            tmpi = createImageFromBytes(imageBytes);
        }
        
        return tmpi;
    }

    private static Image getImageFromJar(final String imgName) {
        Image tmpi = null;
        
        final URL url = GLImage.class.getResource(imgName);
        if (url != null) {
            // found image in jar: load it
            tmpi = Toolkit.getDefaultToolkit().createImage(url);
            // Wait up to two seconds to load image
            final int wait = 200;
            while (tmpi.getHeight(null) < 0 && wait > 0) {
                try {
                    Thread.sleep(10);
                } catch (final InterruptedException e) {
                    LOG.error(e);
                }
            }
        }
        
        return tmpi;
    }
    
    private static Image createImageFromBytes(final byte[] imageBytes) {
        int numTries = 20;
        
        final Image tmpi = Toolkit.getDefaultToolkit().createImage(imageBytes, 0, imageBytes.length);
        
        while (tmpi.getWidth(null) < 0 && numTries-- > 0) {
            try { 
                Thread.sleep(100); 
            } catch (final InterruptedException e) {
                LOG.error(e);
            }
        }
        
        while (tmpi.getHeight(null) < 0 && numTries-- > 0) {
            try { 
                Thread.sleep(100); 
            } catch (final InterruptedException e) {
                LOG.error(e);
            }
        }
        
        return tmpi;
    }
    
    public static Image loadImageFromFileORG(final String imgName) {
        final byte[] imageBytes = getBytesFromFile(imgName);
        
        return createImageFromBytes(imageBytes);
        
        /*Image tmpi = null;
        int numTries = 20;
        if (imageBytes != null) {
            tmpi = Toolkit.getDefaultToolkit().createImage(imageBytes, 0, imageBytes.length);
            while (tmpi.getWidth(null) < 0 && numTries-- > 0) {
                try { Thread.sleep(100); }
                catch( final InterruptedException e ) {System.out.println(e);}
            }
            while (tmpi.getHeight(null) < 0 && numTries-- > 0) {
                try { Thread.sleep(100); }
                catch( final InterruptedException e ) {System.out.println(e);}
            }
        }
        return tmpi;*/
    }

    /**
     * Given name of file, return entire file as a byte array.
     * @param filename
     * @return
     */
    public static byte[] getBytesFromFile(final String filename) {
        final File f = new File(filename);
        byte[] bytes = null;
        if (f.exists()) {
            try {
                bytes = getBytesFromFile(f);
            } catch (final IOException e) {
                LOG.error(e);
            }
        }

        return bytes;
    }

    /**
     * Given File object, returns the contents of the file as a byte array.
     */
    public static byte[] getBytesFromFile(final File file) throws IOException {
        byte[] bytes = null;
        if (file != null) {
            final InputStream is = new FileInputStream(file);
            final long length = file.length();
            
            // Can't create an array using a long type.
            // Before converting to an int type, check
            // to ensure that file is not larger than Integer.MAX_VALUE.
            if (length > Integer.MAX_VALUE) {
                LOG.error("getBytesFromFile() error: File " + file.getName() + " is too large");
            } else {
                
                // Create the byte array to hold the data
                bytes = new byte[ (int) length];
                int offset = 0;
                int numRead = 0;
                
                // Read in the bytes
                numRead = is.read(bytes, offset, bytes.length - offset);
                while (offset < bytes.length && numRead >= 0) {
                    offset += numRead;
                    
                    numRead = is.read(bytes, offset, bytes.length - offset);
                }
                
                // Ensure all the bytes have been read in
                if (offset < bytes.length) {
                    throw new IOException("getBytesFromFile() error: Could not completely read file " + file.getName());
                }
            }
            
            // Close the input stream and return bytes
            is.close();
        }
        return bytes;
    }

    public final ByteBuffer getPixelBuffer() {
        return m_pixelBuffer;
    }

    public final int getHeight() {
        return m_h;
    }

    public final int getWidth() {
        return m_w;
    }

}