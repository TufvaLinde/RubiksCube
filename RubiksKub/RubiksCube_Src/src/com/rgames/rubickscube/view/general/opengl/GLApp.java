package com.rgames.rubickscube.view.general.opengl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.apache.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.Pbuffer;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.opengl.glu.GLU;

import com.rgames.rubickscube.controller.general.Logging;
import com.rgames.rubickscube.view.general.opengl.image.GLImage;

/**
 * Collection of functions to init and run an OpenGL app using LWJGL.
 * <P>
 * Includes functions to handle:  <BR>
 *        Buffer allocation -- manage IntBuffer, ByteBuffer calls. <BR>
 *        Setup display mode, keyboard, mouse, native cursor <BR>
 *        Run main loop of application <BR>
 *        OpenGL functions -- convert screen/world coords, set modes, lights, etc. <BR>
 *        Utility functions -- load images, convert pixels, getTimeInMillis, etc. <BR>
 * <P>
 * Has a main() function to run as an application, though this class has only
 * minimal placeholder functionality.  It is meant to be subclassed,
 * and the subclass will define more elaborate render() mouseMove() functions, etc.
 * <P>
 * GLApp initializes the LWJGL environment for OpenGL rendering,
 * ie. creates a window, sets the display mode, inits mouse and keyboard,
 * then runs a loop.
 * <P>
 * Uses GLImage to load and hold image pixels.
 * <P>
 * napier a t potatoland d o t org
 */
public class GLApp {
    
    /** 
     * Just for reference. 
     */
    public static final String GLAPP_VERSION = "1.4";
    
    /**
     * The settings.
     */
    protected static GLAppSettings settings = new GLAppSettings();

    /**
     * The display mode.
     */
    protected static GLAppDisplayMode displayMode = new GLAppDisplayMode();
    
    /** 
     * Logger for this class.
     */
    private static final Logger LOG = Logging.getDefault();

    /** App will exit when this key is hit (set to 0 for no key exit). */
    private static int finishedKey = Keyboard.KEY_ESCAPE;
    
    /** App will exit when finished is true (when finishedKey is hit: see mainloop()). */
    private static boolean finished;
    
    /** Cursor position X on screen. */
    private static int cursorX;

    /** Cursor position Y on screen. */
    private static int cursorY;
    
    /** How to tile textures on mesh - vertical */
    private static float tileFactorVert = 1f;

    /** How to tile textures on mesh - horizontal */
    private static float tileFactorHoriz = 1f;
    
    /** For copying screen image to a texture - how large should texture be to hold screen */
    private static int screenTextureSize = 1024; 

    /**
     * NIO Buffers to retrieve OpenGL settings.
     * For memory efficiency and performance, instantiate these once, and reuse.
     * see initBuffers(), getSetingInt(), getModelviewMatrix().
     */
    private static IntBuffer bufferSettingInt;
    
    /** */
    private static IntBuffer bufferViewport;

    /** */
    private static FloatBuffer bufferModelviewMatrix;
    
    /** */
    private static FloatBuffer bufferProjectionMatrix;
    
    /** */
    private static ByteBuffer bufferZdepth;

    /** */
    private static int nextObjectId = 1;
    
    /** Constructor of this class. */
    public GLApp() {
    }
    
    public static final GLAppDisplaySettings getDisplaySettings() {
        return settings.getDisplaySettings();
    }
    
    public static final GLAppDisplayMode getDisplayMode() {
        return displayMode;
    }

    public static final int getNextObjectId() {
        return nextObjectId++; 
    }
    
    /**
     * Runs the application.  Calls init() functions to setup OpenGL,
     * input and display.  Runs the main loop of the application, which handles
     * mouse and keyboard input.
     * <P>
     * Calls:  init() and mainLoop(). <BR>
     * mainLoop() calls:  render(), mouseMove(), mouseDown(), mouseUp(), keyDown(), keyUp()
     */
    public final void run() {
        // load settings from config file (display size, resolution, etc.)
        try {
            settings.loadSettings();
            settings.getDisplaySettings().initFromProperties();
        } catch (final IOException e) {
            LOG.error("Error loading settings!", e);
            System.exit(1);
        }
        
        // init buffers
        initBuffers();
        try {
            // Init Display, Keyboard, Mouse, OpenGL
            init();

            // Main loop
            while (!finished) {
                if (!Display.isVisible()) {  //!!!
                    Thread.sleep(200L);
                } else if (Display.isCloseRequested()) {  //!!!
                    finished = true;
                }
                mainLoop();
                
                Display.update();  //!!!!
            }
        } catch (final InterruptedException e) {
            LOG.error(e);
        }
        
        // prepare to exit
        cleanup();
        System.exit(0);
    }


    /**
     * Called by the run() loop.  Handles animation and input for each frame.
     * <P>
     * Currently only works with Mouse.setGrabbed(true) (see initInput())
     * This means the native cursor is hidden and app has to draw its own.
     */
    public final void mainLoop() {
        final int mouseDX = Mouse.getDX();
        final int mouseDY = Mouse.getDY();
        final int mouseDW = Mouse.getDWheel();
        if (mouseDX != 0 || mouseDY != 0 || mouseDW != 0) {
            cursorX += mouseDX;
            cursorY += mouseDY;
            if (cursorX < 0) {
                cursorX = 0;
            } else if (cursorX > displayMode.getCurrent().getWidth()) {
                cursorX = displayMode.getCurrent().getWidth();
            }
            if (cursorY < 0) {
                cursorY = 0;
            } else if (cursorY > displayMode.getCurrent().getHeight()) {
                cursorY = displayMode.getCurrent().getHeight();
            }
            mouseMove(cursorX, cursorY);
        }
        while (Mouse.next()) {
            if (Mouse.getEventButton() > -1 && Mouse.getEventButtonState()) {
                mouseDown(Mouse.getEventButton(), cursorX, cursorY);
            }
            if (Mouse.getEventButton() > -1 && !Mouse.getEventButtonState()) {
                mouseUp(Mouse.getEventButton(), cursorX, cursorY);
            }
        }
        // Handle key hits
        while (Keyboard.next())  {
            if (Keyboard.getEventKey() == finishedKey) {
                finished = true;
            }
            // pass key event to handler
            if (Keyboard.getEventKeyState()) {
                keyDown(Keyboard.getEventKey());
            } else {
                keyUp(Keyboard.getEventKey());
            }
        }
        
        render();
    }

    /**
     * The three init functions below must be called to get the display, mouse
     * and OpenGL context ready for use. Override init() in a subclass to
     * customize behavior.
     */
    public void init() {
    }

    /**
     *  For memory efficiency and performance, it's best to init NIO buffers
     *  once and reuse, particularly for calls that may be repeated often.
     *  Allocate NIO buffers here, use them in getSetingInt(),
     *  getModelviewMatrix(), etc.
     */
    public final void initBuffers() {
        bufferSettingInt = GLAppUtil.allocInts(16);
        bufferModelviewMatrix = GLAppUtil.allocFloats(16);
        bufferProjectionMatrix = GLAppUtil.allocFloats(16);
        bufferViewport = GLAppUtil.allocInts(16);
        bufferZdepth = GLAppUtil.allocBytes(GLAppUtil.SIZE_FLOAT);
    }

    /**
     * Called by GLApp.run() to initialize OpenGL rendering.
     * @throws java.lang.Exception
     */
    public final void initOpenGL() {
        // Depth tests mess up alpha drawing.  When layering up many transluscent
        // shapes, you have to turn off Depth tests or GL will throw out "hidden"
        // layers, when you actually want to draw the lower layers under the
        // topmost (transluscent) layers.
        GL11.glClearDepth(1.0f); // Depth Buffer Setup
        GL11.glEnable(GL11.GL_DEPTH_TEST); // Enables Depth Testing  // Need this ON if using textures
        GL11.glDepthFunc(GL11.GL_LEQUAL); // The Type Of Depth Testing To Do

        // Needed?
        GL11.glShadeModel(GL11.GL_SMOOTH); // Enable Smooth Shading
        GL11.glClearColor(0f, 0f, 0f, 0f); // Black Background
        GL11.glEnable(GL11.GL_NORMALIZE);
        GL11.glEnable(GL11.GL_CULL_FACE);

        // Perspective quality
        GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);

        // Set the size and shape of the screen area
        //GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
        GL11.glViewport(getDisplaySettings().getViewportX(), getDisplaySettings().getViewportY(), 
                getDisplaySettings().getViewportW(), getDisplaySettings().getViewportH());

        // setup perspective (see setOrtho for 2D)
        setPerspective();

        // select model view for subsequent transforms
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
    }

    /**
     * Called by mainLoop() to render one frame.  Subclass will override this.
     */
    public void render() {
    }

    /**
     * Called by mainLoop() when mouse moves.
     */
    public void mouseMove(final int x, final int y) {
    }

    /**
     * Called by mainLoop() when mouse down.
     */
    public void mouseDown(final int button, final int x, final int y) {
    }

    /**
     * Called by mainLoop() when mouse up.
     */
    public void mouseUp(final int button, final int x, final int y) {
    }

    /**
     * Called by mainLoop() when key down.
     */
    public void keyDown(final int keyCode) {
    }

    /**
     * Called by mainLoop() when key up.
     */
    public void keyUp(final int keyCode) {
    }

    /**
     */
    public void doCleanup() {
    }
    
    /**
     * Run() calls this right before exit.
     */
    public final void cleanup() {
        //GLFont.destroy();
        doCleanup();
        
        Keyboard.destroy();
        Display.destroy();  // will call Mouse.destroy()
    }

    //========================================================================
    // Setup display mode
    //
    // Initialize Display, Mouse, Keyboard.
    // These functions are specific to LWJGL.
    //
    //========================================================================

    /**
     * Initialize the Display mode, viewport size, and open a Window.
     * By default the window is fullscreen, the viewport is the same dimensions
     * as the window.
     */
    public static boolean initDisplay() {
        return displayMode.init();
    }

    /**
     * Initialize the Keyboard and Mouse.
     * <P>
     * Set Mouse.setGrabbed(true).  This hides the native cursor and enables
     * the Mouse.getDX() and Mouse.getDY() functions so we can track mouse
     * motion ourselves.
     *
     * @see mainLoop()  for mouse motion tracking
     * @see drawCursor()  to draw our own cursor on screen
     */
    public static void initInput() {
        try {
            // init keyboard
            Keyboard.create();

            // init mouse: this will hide the native cursor (see drawCursor())
            Mouse.setGrabbed(true);

            // set initial cursor pos to center screen
            cursorX = (int) displayMode.getCurrent().getWidth() / 2;
            cursorY = (int) displayMode.getCurrent().getHeight() / 2;
        } catch (final LWJGLException e) {
            LOG.error(e);
        }
    }


    //========================================================================
    // OpenGL functions
    //
    // Typical OpenGL functionality: get settings, get matrices, convert
    // screen to world coords, define textures and lights.
    //
    //========================================================================

    public static int getSettingInt(final int whichSetting) {
        //IntBuffer ibuffer = allocInts(16);
        bufferSettingInt.clear();
        GL11.glGetInteger(whichSetting, bufferSettingInt);
        
        return bufferSettingInt.get(0);
    }

    public static FloatBuffer getModelviewMatrix() {
        //FloatBuffer modelview = allocFloats(16);
        bufferModelviewMatrix.clear();
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, bufferModelviewMatrix);
        
        return bufferModelviewMatrix;
    }

    public static FloatBuffer getProjectionMatrix() {
        //FloatBuffer projection = allocFloats(16);
        bufferProjectionMatrix.clear();
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, bufferProjectionMatrix);
        
        return bufferProjectionMatrix;
    }

    public static IntBuffer getViewport() {
        //IntBuffer viewport = allocInts(16);
        bufferViewport.clear();
        GL11.glGetInteger(GL11.GL_VIEWPORT, bufferViewport);
        
        return bufferViewport;
    }

    /**
     * Convert a FloatBuffer matrix to a 4x4 float array.
     * @param fb   FloatBuffer containing 16 values of 4x4 matrix
     * @return     2 dimensional float array
     */
    public static float[][] getMatrixAsArray(final FloatBuffer fb) {
        final float[][] fa = new float[4][4];
        fa[0][0] = fb.get();
        fa[0][1] = fb.get();
        fa[0][2] = fb.get();
        fa[0][3] = fb.get();
        fa[1][0] = fb.get();
        fa[1][1] = fb.get();
        fa[1][2] = fb.get();
        fa[1][3] = fb.get();
        fa[2][0] = fb.get();
        fa[2][1] = fb.get();
        fa[2][2] = fb.get();
        fa[2][3] = fb.get();
        fa[3][0] = fb.get();
        fa[3][1] = fb.get();
        fa[3][2] = fb.get();
        fa[3][3] = fb.get();
        
        return fa;
    }

    /**
     * Return the modelview matrix as a 4x4 float array.
     */
    public static float[][] getModelviewMatrixA() {
        return getMatrixAsArray(getModelviewMatrix());
    }

    /**
     * Return the projection matrix as a 4x4 float array.
     */
    public static float[][] getProjectionMatrixA() {
        return getMatrixAsArray(getProjectionMatrix());
    }

    /**
     * Return the Viewport data as array of 4 floats.
     */
    public static int[] getViewportA() {
        final IntBuffer ib = getViewport();
        final int[] ia = new int[4];
        ia[0] = ib.get(0);
        ia[1] = ib.get(1);
        ia[2] = ib.get(2);
        ia[3] = ib.get(3);
        
        return ia;
    }

    /**
     * Return the Z depth of the single pixel at the given screen position.
     */
    public static float getZDepth(final int x, final int y) {
        //ByteBuffer zdepth = allocBytes(SIZE_FLOAT); //ByteBuffer.allocateDirect(1 * SIZE_FLOAT).order(ByteOrder.nativeOrder());
        bufferZdepth.clear();
        GL11.glReadPixels(x, y, 1, 1, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, bufferZdepth);
        
        return (float) (bufferZdepth.getFloat(0));
    }


    /**
     * Find the Z depth of the origin in the projected world view. Used by getWorldCoordsAtScreen()
     * Projection matrix  must be active for this to return correct results (GL.glMatrixMode(GL.GL_PROJECTION)).
     * For some reason I have to chop this to four decimals or I get bizarre
     * results when I use the value in project().
     */
    public static float getZDepthAtOrigin() {
        final float[] resultf = new float[3];
        project(0, 0, 0, resultf);
        
        return ((int) (resultf[2] * 10000F)) / 10000f;  // truncate to 4 decimals
    }

    /**
     * Return screen coordinates for a given point in world space.  The world
     * point xyz is 'projected' into screen coordinates using the current model
     * and projection matrices, and the current viewport settings.
     *
     * @param x         world coordinates
     * @param y
     * @param z
     * @param resultf    the screen coordinate as an array of 3 floats
     */
    public static void project(final float x, final float y, final float z, final float[] resultf) {
        final float[] result = new float[3];
        GLU.gluProject(x, y, z, getModelviewMatrixA(), getProjectionMatrixA(), getViewportA(), result);
        resultf[0] = result[0];
        resultf[1] = result[1];
        resultf[2] = result[2];
    }

    /**
     * Return world coordinates for a given point on the screen.  The screen
     * point xyz is 'un-projected' back into world coordinates using the
     * current model and projection matrices, and the current viewport settings.
     *
     * @param x         screen x position
     * @param y         screen y position
     * @param z         z-buffer depth position
     * @param resultf   the world coordinate as an array of 3 floats
     * @see             getWorldCoordsAtScreen()
     */
    public static void unProject(final float x, final float y, final float z, final float[] resultf) {
        final float[] result = new float[3];  // v.9
        GLU.gluUnProject(x, y, z, getModelviewMatrixA(), getProjectionMatrixA(), getViewportA(), result);
        resultf[0] = result[0];
        resultf[1] = result[1];
        resultf[2] = result[2];
    }

    /**
     * For given screen xy, return the world xyz coords in a float array.  Assume
     * Z position is 0.
     */
    public static float[] getWorldCoordsAtScreen(final int x, final int y) {
        final float z = getZDepthAtOrigin();
        final float[] resultf = new float[3];
        unProject((float) x, (float) y, (float) z, resultf);
        
        return resultf;
    }

    /**
     * For given screen xy and z depth, return the world xyz coords in a float array.
     */
    public static float[] getWorldCoordsAtScreen(final int x, final int y, final float z) {
        final float[] resultf = new float[3];
        unProject((float) x, (float) y, (float) z, resultf);
        
        return resultf;
    }

    /**
     * Allocate a texture (glGenTextures) and return the handle to it.
     */
    public static int allocateTexture() {
        final IntBuffer textureHandle = GLAppUtil.allocInts(1);
        GL11.glGenTextures(textureHandle);
        
        return textureHandle.get(0);
    }

    /**
     * How many times to repeat texture horizontally and vertically.
     */
    public static void setTextureTile(final float horizontalTile, final float verticalTile) {
        tileFactorHoriz = horizontalTile;
        tileFactorVert = verticalTile;
    }

    /**
     * Create a texture from the given image.
     */
    public static int makeTexture(final GLImage textureImg) {
        if (textureImg == null) {
            return 0;
        } else {
            return makeTexture(textureImg.getPixelBuffer(), textureImg.getWidth(), textureImg.getHeight());
        }
    }

    /**
     * Create a texture from the given pixels in RGBA format.  Set the texture
     * to repeat in both directions and use LINEAR for magnification.
     * @return  the texture handle
     */
    public static int makeTexture(final ByteBuffer pixels, final int w, final int h) {
        // get a new empty texture
        final int textureHandle = allocateTexture();
        // 'select' the new texture by it's handle
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureHandle);
        // set texture parameters
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR); //GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR); //GL11.GL_NEAREST);
        // Create the texture from pixels
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, w, h, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
        return textureHandle;
    }

    /**
     * Build Mipmaps for texture (builds different versions of the picture for
     * distances - looks better).
     *
     * @param textureImg  the texture image
     * @return   error code of buildMipMap call
     */
    public static int makeTextureMipMap(final GLImage textureImg) {
        final int ret = GLU.gluBuild2DMipmaps(GL11.GL_TEXTURE_2D, 4, textureImg.getWidth(),
                          textureImg.getHeight(), GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, textureImg.getPixelsRGBA());
        if (ret != 0) {
            LOG.error("GLApp.makeTextureMipMap(): Error occured while building mip map, ret=" + ret);
        }
        
        //Assign the mip map levels and texture info
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_NEAREST);
        GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
        return ret;
    }

    /**
     * Create a texture large enough to hold the screen image.  Use RGBA format
     * to insure colors are copied exactly.  Use GL_NEAREST for magnification
     * to prevent slight blurring of image when screen is drawn back.
     *
     * @see frameSave()
     * @see frameDraw()
     */
    public static int makeTextureForScreen(final int screenSize) {
        // get a texture size big enough to hold screen (512, 1024, 2048 etc.)
        screenTextureSize = GLAppUtil.getPowerOfTwoBiggerThan(screenSize);
        LOG.debug("GLApp.makeTextureForScreen(): made texture for screen with size " + screenTextureSize);
        
        // get a new empty texture
        final int textureHandle = allocateTexture();
        final ByteBuffer pixels = GLAppUtil.allocBytes(screenTextureSize * screenTextureSize * GLAppUtil.SIZE_INT);
        
        // 'select' the new texture by it's handle
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureHandle);
        
        // set texture parameters
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        
        // use GL_NEAREST to prevent blurring during frequent screen restores
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        
        // Create the texture from pixels: use GL_RGBA8 to insure exact color copy
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, screenTextureSize, screenTextureSize, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
        return textureHandle;
    }

    /**
     * Set OpenGL to render in 3D perspective.
     */
    public static void setPerspective() {
        // select projection matrix (controls view on screen)
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GLU.gluPerspective(30f, getDisplaySettings().getAspectRatio(), 1f, 30f);
        GLU.gluLookAt(0f, 0f, 15f, 0f, 0f, 0f, 0f, 1f, 0f);
    }

    /**
     * Set OpenGL to render in flat 2D (no perspective).  This creates a
     * one-to-one relation between screen pixel and rendered pixel, ie. if
     * you draw a 10x10 quad at 100,100 it will appear as a 10x10 pixel
     * square on screen at pixel position 100,100.
     * <P>
     * ABOUT Ortho and Viewport:<BR>
     * --------------------------<BR>
     * Let's say we're drawing in 2D and want to have a cinema proportioned
     * viewport (16x9), and want to bound our 2D rendering into that area ie.
       <PRE>
          ___________1024,576
         |           |
         |  Scene    |      Set the bounds on the scene geometry
         |___________|      to the viewport size and shape
      0,0

          ___________1024,576
         |           |
         |  Ortho    |      Set the projection to cover the same
         |___________|      area as the scene
      0,0

          ___________ 1024,768
         |___________|
         |           |1024,672
         |  Viewport |      Set the viewport to the same shape
     0,96|___________|      as scene and ortho, but centered on
         |___________|      screen.
      0,0
     </PRE>
     *
     */
    public static void setOrtho() {
        // select projection matrix (controls view on screen)
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        
        // set ortho to same size as viewport, positioned at 0,0
        GL11.glOrtho(0, getDisplaySettings().getViewportW(), 0, getDisplaySettings().getViewportH(), -1, 1);
    }

    /**
     * Set OpenGL to render in flat 2D (no perspective) on top of current scene.
     * Preserve current projection and model views, and disable depth testing.
     * Once Ortho is On, glTranslate() will take pixel coords as arguments,
     * with the lower left corner 0,0 and the upper right corner 1024,768 (or
     * whatever your screen size is).
     *
     * @see setOrthoOff()
     */
    public static void setOrthoOn() {
        // prepare to render in 2D
        GL11.glDisable(GL11.GL_DEPTH_TEST);                   // so text stays on top of scene
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();                                  // preserve perspective view
        GL11.glLoadIdentity();                                // clear the perspective matrix
        GL11.glOrtho(getDisplaySettings().getViewportX(), 
                getDisplaySettings().getViewportX() + getDisplaySettings().getViewportW(), 
                getDisplaySettings().getViewportY(), 
                getDisplaySettings().getViewportY() + getDisplaySettings().getViewportH(), -1, 1);  // turn on 2D mode
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();                // Preserve the Modelview Matrix
        GL11.glLoadIdentity();              // clear the Modelview Matrix
    }

    /**
     * Turn 2D mode off.  Return the projection and model views to their
     * preserved state that was saved when setOrthoOn() was called, and
     * enable depth testing.
     *
     * @see setOrthoOn()
     */
    public static void setOrthoOff() {
        // restore the original positions and views
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_DEPTH_TEST);      // turn Depth Testing back on
    }

    /**
     * Set the color of a 'positional' light (a light that has a specific
     * position within the scene).  <BR>
     *
     * Pass in an OpenGL light number (GL11.GL_LIGHT1),
     * the 'Diffuse' and 'Ambient' colors (direct light and reflected light),
     * and the position.<BR>
     *
     * @param glLightHandle
     * @param diffuseLightColor
     * @param ambientLightColor
     * @param position
     */
    public static void setLight(final int glLightHandle,
        final float[] diffuseLightColor, final float[] ambientLightColor, final float[] position) {
        
        final FloatBuffer ltDiffuse = GLAppUtil.allocFloats(diffuseLightColor);
        final FloatBuffer ltAmbient = GLAppUtil.allocFloats(ambientLightColor);
        final FloatBuffer ltPosition = GLAppUtil.allocFloats(position);
        GL11.glLight(glLightHandle, GL11.GL_DIFFUSE, ltDiffuse);   // color of the direct illumination
        GL11.glLight(glLightHandle, GL11.GL_SPECULAR, ltDiffuse);  // color of the highlight
        GL11.glLight(glLightHandle, GL11.GL_AMBIENT, ltAmbient);   // color of the reflected light
        GL11.glLight(glLightHandle, GL11.GL_POSITION, ltPosition);
        GL11.glEnable(glLightHandle);   // Enable the light (GL_LIGHT1 - 7)
        //GL11.glLightf(GLLightHandle, GL11.GL_QUADRATIC_ATTENUATION, .005F);    // how light beam drops off
    }


    public static void setSpotLight(final int glLightHandle,
        final float[] diffuseLightColor, final float[] ambientLightColor,
        final float[] position, final float[] direction, final float cutoffAngle) {
        
        final FloatBuffer ltDirection = GLAppUtil.allocFloats(direction);
        setLight(glLightHandle, diffuseLightColor, ambientLightColor, position);
        GL11.glLightf(glLightHandle, GL11.GL_SPOT_CUTOFF, cutoffAngle);   // width of the beam
        GL11.glLight(glLightHandle, GL11.GL_SPOT_DIRECTION, ltDirection);    // which way it points
        GL11.glLightf(glLightHandle, GL11.GL_CONSTANT_ATTENUATION, 2F);    // how light beam drops off
        //GL11.glLightf(GLLightHandle, GL11.GL_LINEAR_ATTENUATION, .5F);    // how light beam drops off
        //GL11.glLightf(GLLightHandle, GL11.GL_QUADRATIC_ATTENUATION, .5F);    // how light beam drops off
    }

    /**
     * Set the color of the Global Ambient Light.  Affects all objects in
     * scene regardless of their placement.
     */
    public static void setAmbientLight(final float[] ambientLightColor) {
        final FloatBuffer ltAmbient = GLAppUtil.allocFloats(ambientLightColor);
        
        GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, ltAmbient);
    }

    /**
     * Draw an image at the given x,y. If scale values are not 1, then scale
     * the image.  See loadImage().<BR>
     */
    public static void drawImageInt(final GLImage img, final int x, final int y, final float scaleW, final float scaleY) {
        if (img != null) {
            GL11.glRasterPos2i(x, y);
            GL11.glDrawPixels(img.getWidth(), img.getHeight(), GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, img.getPixelBuffer());
            if (scaleW != 1f || scaleY != 1f) {
                GL11.glPixelZoom(scaleW, scaleY);
            }
        }
    }

    /**
     * Draw a cursor image textured onto a quad at cursor position.  The cursor
     * image must be loaded into a texture, then this function can be called
     * after scene is drawn.  Uses glPushAttrib() to preserve the current
     * drawing state.  glPushAttrib() may slow performance down, so in your
     * app you may want to set the states yourself before calling drawCursor()
     * and take the push/pop out of here.
     * <P>
     * See mainLoop() for cursorX cursorY and mouse motion handling.
     * <P>
     * Example:
     * <PRE>
     *    GLImage cursorImg = loadImage("images/cursorCrosshair32.gif");  // cursor image must be 32x32
     *    int cursorCrosshairTxtr = makeTexture(cursorImg);
     *
     *    public void render() {
     *        // render scene
     *        ...
     *        drawCursor(cursorCrosshairTxtr);
     *    }
     * </PRE>
     *
     * @param cursorTextureHandle  handle to texture containing  32x32 cursor image
     */
    public final void drawCursor(final int cursorTextureHandle) {
        setOrthoOn();
        //GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_TEXTURE_BIT | GL11.GL_LIGHTING_BIT); 

        GL11.glDisable(GL11.GL_LIGHTING);    // so cursor will draw as-is
        GL11.glEnable(GL11.GL_BLEND);        // enable transparency
        GL11.glEnable(GL11.GL_TEXTURE_2D);   // so texture image will show
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);  // overlay cursor onto scene
        drawImageQuad(cursorTextureHandle, cursorX - 16, cursorY - 16, 32, 32);  // assumes 32x32 pixels
        
        GL11.glPopAttrib();
        setOrthoOff();
    }


    /**
     * Draw an image at the given x,y. Scale the image to the given w,h.
     * The image must be loaded into the texture pointed to by textureHandle.
     * It will be drawn in 2D on top of the current scene.
     * <BR>
     * @ee loadImage().
     */
    public static void drawImageQuad(final int textureHandle, final int x, final int y, final float w, final float h) {
        // activate the specified texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureHandle);
        // prepare to render in 2D
        setOrthoOn();
        // draw the image textured onto a quad
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0f, 0f);
        GL11.glVertex3f((float) x, (float) y, (float) 0);
        GL11.glTexCoord2f(1f, 0f);
        GL11.glVertex3f((float) x + w, (float) y, (float) 0);
        GL11.glTexCoord2f(1f, 1f);
        GL11.glVertex3f((float) x + w, (float) y + h, (float) 0);
        GL11.glTexCoord2f(0f, 1f);
        GL11.glVertex3f((float) x, (float) y + h, (float) 0);
        GL11.glEnd();
        
        // restore the previous perspective and model views
        setOrthoOff();
    }

    /**
     * Draw an image (loaded into a texture) onto a quad, in 3D model space.
     */
    public static void drawQuad(final int textureHandle, final float x, final float y, final float z, final float w, final float h) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureHandle);
        
        // draw  textured quad
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0f, 0f);
        GL11.glVertex3f(x, y, z);
        GL11.glTexCoord2f(1f, 0f);
        GL11.glVertex3f(x + w, y, z);
        GL11.glTexCoord2f(1f, 1f);
        GL11.glVertex3f(x + w, y + h, z);
        GL11.glTexCoord2f(0f, 1f);
        GL11.glVertex3f(x, y + h, z);
        GL11.glEnd();
    }


    /**
     * Create a native cursor from the given image file.<BR>
     * My cursor motion doesn't work with native cursor since verion .95
     * I'll have to fix someday. I prefer drawing cursor as a quad anyway, so
     * this is low priority.
     *
     * @param imgFilename
     * @return Cursor
     * @see drawCursor()
     */
    public static final Cursor makeNativeCursor(final String imgFilename) {
        // check for hw cursor !!!
        if ((Cursor.getCapabilities() & Cursor.CURSOR_ONE_BIT_TRANSPARENCY) == 0) {
            LOG.error("GLApp.makeNativeCursor(): No hardware cursor support!");
            return null;
        }
        
        // load the image
        final GLImage img = new GLImage(imgFilename);
        final int[] jpixels = img.getImagePixels();      // pixels in Java int format
        final int w = img.getWidth();
        final int h = img.getHeight();
        // check for legal image size  !!!
        if (w < Cursor.getMinCursorSize() || w > Cursor.getMaxCursorSize()
             || h < Cursor.getMinCursorSize() || h > Cursor.getMaxCursorSize()) {
            LOG.error("GLApp.makeNativeCursor(): Cursor image is not correct size, should be " + Cursor.getMinCursorSize() + "-" + Cursor.getMaxCursorSize());
            
            return null;
        }
        
        // correct transparent pixels: if a pixel has partial alpha (anything less
        // than opaque) set entire pixel to 0.  Otherwise transparent area looks funky.
        for (int i = 0; i < jpixels.length; i++) {
            if ((jpixels[i] >> 24 & 0xff) != 0xff) {
                jpixels[i] = 0;
            }
        }
        
        // make the cursor
        final IntBuffer intpixels =  GLAppUtil.allocInts(jpixels);  // convert to IntBuffer
        Cursor cur = null;
        try {
            cur = new Cursor(w, h,        // cursor size
                           w / 2, h / 2,    // hotspot at center
                           1,           // one cursor image will be passed in
                           intpixels,   // image pixels
                           null);       // no delays (timing delays for animated cursors)
        } catch (final LWJGLException e) {
            LOG.error(e);
        }
        
        return cur;
    }


    /**
     * Get pixels from frame buffer.<BR>
     */
    public static GLImage getFramePixels(final int x, final int y, final int w, final int h) {
        final ByteBuffer pixels = GLAppUtil.allocBytes(w * h);
        //GL11.glReadBuffer(GL11.GL_BACK);
        GL11.glReadPixels(x, y, w, h, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
        
        return new GLImage(pixels, w, h);
    }

    /**
     * Get pixels from frame buffer into an existing image.<BR>
     */
    public static void getFramePixels(final int x, final int y, final GLImage img) {
        GL11.glReadPixels(x, y, img.getWidth(), img.getHeight(), GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, img.getPixelBuffer());
    }

    /**
     * Write pixels to frame buffer from an existing image.
     * <BR>
     * Need to switch Projection matrix to ortho (2D) since Raster Position is
     * transformed in space just like a vertex.  Steps: switch to 2D, set the
     * draw position to lower left, draw the image into Back and Front buffers.
     * <BR>
     * Draw into both buffers so they both have same image (otherwise you may
     * get flicker as front and back buffers have different images).  This is
     * an issue only when you're not clearing the screen before drawing each
     * frame, ie. when leaving trails as objects move.
     */
    public static void setFramePixels(final GLImage img) {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        
        GLU.gluOrtho2D(0f, displayMode.getCurrent().getWidth(), 0f, displayMode.getCurrent().getHeight());
        
        GL11.glRasterPos2i(0, 0);
        GL11.glDrawBuffer(GL11.GL_FRONT);
        GL11.glDrawPixels(img.getWidth(), img.getHeight(), GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, img.getPixelBuffer());
        GL11.glDrawBuffer(GL11.GL_BACK);
        GL11.glDrawPixels(img.getWidth(), img.getHeight(), GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, img.getPixelBuffer());
        GL11.glPopMatrix();
    }

    /**
     * Save entire screen image to a texture.  Will copy entire screen even
     * if a viewport is in use.  Texture param must be large enough to hold
     * screen image (see makeTextureForScreen()).
     *
     * @param txtrHandle   texture where screen image will be stored
     * @see frameDraw()
     * @see makeTextureForScreen()
     */
    public static void frameSave(final int txtrHandle) {
        // turn off alpha and color tints
        GL11.glColor4f(1, 1, 1, 1);                
        GL11.glReadBuffer(GL11.GL_BACK);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, txtrHandle);
        
        // Copy screen to texture (whole frame!)
        GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, 
                displayMode.getCurrent().getWidth(), displayMode.getCurrent().getHeight());
    }


    /**
     * Draw the screen-sized image over entire screen area.  The screen image
     * is stored in the given texture at 0,0 (see frameSave()) and has the
     * same dimensions as the current display mode (DM.getWidth(), DM.getHeight()).
     * <P>
     * Reset the viewport and ortho mode to full screen (viewport may be
     * different proportion than screen if custom aspectRatio is set).  Draw the
     * quad the same size as texture so no stretching or compression of image.
     *
     * @param txtrHandle
     */
    public static void frameDraw(final int txtrHandle) {
        // keep it opaque
        GL11.glDisable(GL11.GL_BLEND);
        // set viewport to full screen
        GL11.glViewport(0, 0, displayMode.getCurrent().getWidth(), displayMode.getCurrent().getHeight());
        // set ortho view to full screen and draw quad
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();                      // preserve current projection

            GL11.glLoadIdentity();                // clear the projection matrix
            GL11.glOrtho(0, displayMode.getCurrent().getWidth(), 0, displayMode.getCurrent().getHeight(), -1, 1); // turn on 2D mode full screen
            drawQuad(txtrHandle, 0, 0, 0, screenTextureSize, screenTextureSize); // draw the full screen image

        GL11.glPopMatrix();                       // restore projection view
        // restore viewport to custom aspect ratio
        GL11.glViewport(getDisplaySettings().getViewportX(), getDisplaySettings().getViewportY(), 
                getDisplaySettings().getViewportW(), getDisplaySettings().getViewportH());
    }


    /**
     * Make a blank image of the given size.
     */
    public static GLImage makeImage(final int w, final int h) {
        final ByteBuffer pixels = GLAppUtil.allocBytes(w * h * 4);
        return new GLImage(pixels, w, h);
    }


     //========================================================================
     // PBuffer functions
     //
     // Pbuffers are offscreen buffers that can be rendered into just like
     // the regular framebuffer.  A pbuffer can be larger than the screen,
     // which allows for the creation of higher resolution images.
     //
     //========================================================================

     /**
      * Create a Pbuffer for use as an offscreen buffer, with the given
      * width and height.  Use selectPbuffer() to make the pbuffer the
      * context for all subsequent opengl commands.  Use selectDisplay() to
      * make the Display the context for opengl commands.
      * <P>
      * @param width
      * @param height
      * @return Pbuffer
      * @see selectPbuffer(), selectDisplay()
      */
     public final Pbuffer makePbuffer(final int width, final int height) {
         Pbuffer pbuffer = null;
         try {
             pbuffer = new Pbuffer(width, height,
                                   new PixelFormat(24, //bitsperpixel
                                                   8,  //alpha
                                                   24, //depth
                                                   0,  //stencil
                                                   0), //samples
                                   null,
                                   null);
          } catch (final LWJGLException e) {
             LOG.error("GLApp.makePbuffer(): exception " + e);
         }
         return pbuffer;
     }

     /**
      * Make the pbuffer the current context for opengl commands.  All following
      * gl functions will operate on this buffer instead of the display.
      * <P>
      * NOTE: the Pbuffer may be recreated if it was lost since last used.  It's
      * a good idea to use:
      * <PRE>
      *         pbuff = selectPbuffer(pbuff);
      * </PRE>
      * to hold onto the new Pbuffer reference if Pbuffer was recreated.
      *
      * @param pb  pbuffer to make current
      * @return    Pbuffer
      * @see       selectDisplay(), makePbuffer()
      */
     public final Pbuffer selectPbuffer(final Pbuffer pb) {
         Pbuffer pbuf = pb;
         if (pbuf != null) {
             try {
                 // re-create the buffer if necessary
                 if (pbuf.isBufferLost()) {
                     LOG.warn("GLApp.selectPbuffer(): Buffer contents lost - recreating the pbuffer");
                     
                     final int w = pb.getWidth();
                     final int h = pb.getHeight();
                     pbuf.destroy();
                     pbuf = makePbuffer(w, h);
                 }
                 // select the pbuffer for rendering
                 pbuf.makeCurrent();
             } catch (final LWJGLException e) {
                 LOG.error("GLApp.selectPbuffer(): exception " + e);
             }
         }
         return pbuf;
     }

     /**
      * Make the Display the current context for OpenGL commands.  Subsequent
      * gl functions will operate on the Display.
      *
      * @see selectPbuffer()
      */
     public final void selectDisplay() {
         try {
             Display.makeCurrent();
         } catch (final LWJGLException e) {
             LOG.error("GLApp.selectDisplay(): exception " + e);
         }
     }

     /**
      * Copy the pbuffer contents to a texture.  (Should this use glCopyTexSubImage2D()?
      * Is RGB the fastest format?)
      */
     public static void frameSave(final Pbuffer pbuff, final int textureHandle) {
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureHandle);
         GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, 0, 0, pbuff.getWidth(), pbuff.getHeight(), 0);
     }

     /**
      * Save the contents of the current render buffer to a PNG image.  If the current
      * buffer is the framebuffer then this will work as a screen capture.  Can
      * also be used with the PBuffer class to copy large images or textures that
      * have been rendered into the offscreen pbuffer.
      * <P>
      * WARNING: this function hogs memory!  Call java with more memory
      * (java -Xms360m -Xmx360)
      * <P>
      * @see   selectPbuffer(), selectDisplay()
      */
     public static void screenShot(final int width, final int height, final String saveFilename) {
         // allocate space for RBG pixels
         ByteBuffer framebytes = GLAppUtil.allocBytes(width * height * 3);
         int[] pixels = new int[width * height];
         int bindex;
         // grab a copy of the current frame contents as RGB (has to be UNSIGNED_BYTE or colors come out too dark)
         GL11.glReadPixels(0, 0, width, height, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, framebytes);
         // copy RGB data from ByteBuffer to integer array
         for (int i = 0; i < pixels.length; i++) {
             bindex = i * 3;
             pixels[i] =
                 0xFF000000                                          // A
                 | ((framebytes.get(bindex) & 0x000000FF) << 16)     // R
                 | ((framebytes.get(bindex + 1) & 0x000000FF) << 8)    // G
                 | ((framebytes.get(bindex + 2) & 0x000000FF) << 0);   // B
         }
         // free up this memory
         framebytes = null;
         // flip the pixels vertically (opengl has 0,0 at lower left, java is upper left)
         pixels = GLImage.flipPixels(pixels, width, height);

         try {
             // Create a BufferedImage with the RGB pixels then save as PNG
             final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
             image.setRGB(0, 0, width, height, pixels, 0, width);
             javax.imageio.ImageIO.write(image, "png", new File(saveFilename));
        } catch (final IOException e) {
            LOG.error("GLApp.screenShot(): exception " + e);
        }
     }
}