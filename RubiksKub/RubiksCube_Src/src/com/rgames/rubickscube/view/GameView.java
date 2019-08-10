package com.rgames.rubickscube.view;
/**
 * Create a simple 3D scene using functions in GLApp.  Sets up
 * a scene with an object, textures and a light.  Responds to mouse motion
 * and click.
 * <P>
 * GLApp initializes the LWJGL environment for OpenGL rendering,
 * ie. creates a window, sets the display mode, inits mouse and keyboard,
 * then runs a loop that calls render().
 * <P>
 * napier at potatoland dot org
 */

import java.nio.IntBuffer;

import org.apache.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.glu.GLU;

import com.rgames.rubickscube.RubicksCubeGame;
import com.rgames.rubickscube.controller.general.Logging;
import com.rgames.rubickscube.model.GameModel;
import com.rgames.rubickscube.view.general.FPSCounter;
import com.rgames.rubickscube.view.general.opengl.GLApp;
import com.rgames.rubickscube.view.general.opengl.GLAppUtil;
import com.rgames.rubickscube.view.general.opengl.font.GLFont;
import com.rgames.rubickscube.view.general.opengl.font.GLFontConfiguration;
import com.rgames.rubickscube.view.general.opengl.font.GLFontPrinter;
import com.rgames.rubickscube.view.general.opengl.image.GLImage;
import com.rgames.rubickscube.view.general.opengl.image.GLImageUtil;

/**
 */
public final class GameView extends GLApp {

    /**
     */
    private static final Logger LOG = Logging.getDefault();

    /**
     * FPS Counter 
     */
    private static FPSCounter fps = new FPSCounter();

    /**
     */
    private static boolean showFps = true;

    /**
     */
    private static GLFont baseFont;
    
    /**
     * All the 3d objects
     */
    private GameScene2D m_scene2d;

    /**
     * All the 3d objects
     */
    private GameScene3D m_scene3d;

    /**
     */
    private int m_cursorTextureHandle;
    
    /**
     */
    private float[] m_faBlack = { 0f, 0f, 0f, 1.0f };
    
    /**
     */
    private float[] m_faLightBlue = { 0.8f, 0.8f, .9f, 1f };
    
    /**
     */
    private float[] m_faWhite = { 2.0f, 2.0f, 2.0f, 1.0f };

    /**
     *  Light position: if last value is 0, then this describes light direction.  If 1, then light position.
     */
    private float[] m_lightPosition = { 0f, 3f, 3f, 1.0f };
    
    public GameView(final GameModel model) {
        m_scene2d = new GameScene2D();
        m_scene3d = new GameScene3D(); 
    }
    
    public GameScene2D getScene2D() {
        return m_scene2d;
    }
    
    public GameScene3D getScene3D() {
        return m_scene3d;
    }

    /**
     * Initialize the scene.  Called by GLApp.run()
     */
    public void init() {
        LOG.debug("Initializing renderer...");
        
        // initialize Window, Keyboard, Mouse, OpenGL environment
        initDisplay();
        initInput();
        initOpenGL();

        // setup perspective
        setPerspective();

        // create a light (diffuse light, ambient light, position)
        setLight(GL11.GL_LIGHT1, m_faWhite, m_faLightBlue, m_lightPosition);

        // no overall scene lighting
        setAmbientLight(m_faBlack);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        
        // enable lighting and texture rendering
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        // select model view for subsequent transforms
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();

        // prepare character set for text rendering
        final GLFontConfiguration fontConf = new GLFontConfiguration("data/images/font_tahoma.png");
        fontConf.setLetterSpacing(-7);
        fontConf.setLineSpacing(5);
        fontConf.setSize(1f);
        baseFont = new GLFont(fontConf);
        baseFont.init();
        
        // init scenes
        m_scene2d.init();
        m_scene3d.init();
        
        // load a cursor image and make it a texture
        final GLImage textureImg = GLImageUtil.loadImage("data/images/cursorPointer32.gif");
        m_cursorTextureHandle = makeTexture(textureImg);
        
        // initialize FPS counter
        fps.init();
    }

    public void doCleanup() {
        baseFont.destroy();
    }
    
    /**
     * Set the camera position, field of view, depth.
     */
    public static void setPerspective() {
        // select projection matrix (controls view on screen)
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        
        setCamera();
    }

    public static void setCamera() {
        // fovy, aspect, zNear, zFar
        GLU.gluPerspective(30f, getDisplaySettings().getAspectRatio(), 1f, 500f);

        // look at
        GLU.gluLookAt(0f, 10f, 20f, 0f, 0f, 0f, 0f, 1f, 0f);
    }

    public static int getViewportX() {
        return getDisplaySettings().getViewportX();
    }
   
    public static int getViewportY() {
        return getDisplaySettings().getViewportY();
    }

    public static int getViewportW() {
        return getDisplaySettings().getViewportW();
    }

    public static int getViewportH() {
        return getDisplaySettings().getViewportH();
    }

    public static GLFont getBaseFont() {
        return baseFont;
    }
    
    /**
     * Render one frame.  Called by GLApp.run().
     */
    public void render() {
        m_scene3d.render(false);
        m_scene2d.render(false);
        
        // render some text relative to viewport edges
        if (showFps) {
            GLFontPrinter.print(baseFont, getViewportX(), getViewportH() - 20, 
                    GLFontPrinter.HORIZONTAL_ALIGN_BEGINNING, "FPS " + fps);
        }

        GLFontPrinter.print(baseFont, getViewportW(), getViewportY() + 3, 
                GLFontPrinter.HORIZONTAL_ALIGN_END, RubicksCubeGame.getVersion());
        
        // turn lighting back on
        GL11.glEnable(GL11.GL_LIGHTING);
        
        // Draw a cursor
        drawCursor(m_cursorTextureHandle);
        
        // Calculate fps
        fps.calculate();
    }

    public void mouseMove(final int x, final int y) {
        m_scene2d.mouseMove(x, y);
        m_scene3d.mouseMove(x, y);
    }

    public void mouseDown(final int button, final int x, final int y) {
        m_scene2d.mouseDown(button, x, y);
        m_scene3d.mouseDown(button, x, y);
    }

    public void mouseUp(final int button, final int x, final int y) {
        m_scene2d.mouseUp(button, x, y);
        m_scene3d.mouseUp(button, x, y);
    }

    public void keyDown(final int keyCode) {
        m_scene2d.keyDown(keyCode);
        m_scene3d.keyDown(keyCode);
    }

    public void keyUp(final int keyCode) {
        m_scene2d.keyUp(keyCode);
        m_scene3d.keyUp(keyCode);
    }
    
    public static void toggleFps() {
        showFps = !showFps;
    }
    
    /** 
     * Attempt at OpenGL Picking :).
     * @see http://gpwiki.org/index.php/OpenGL:Tutorials:Picking
     */
    public int glSelect(final int x, final int y) {
        //final IntBuffer buf = BufferUtils.createIntBuffer(64);
        final IntBuffer buf = BufferUtils.createIntBuffer(64);
        final IntBuffer view = BufferUtils.createIntBuffer(16);
        int hits;
        
        GL11.glSelectBuffer(buf);
        
        // This retrieve info about the viewport
        GL11.glGetInteger(GL11.GL_VIEWPORT, view);
        view.rewind();
        
        GL11.glRenderMode(GL11.GL_SELECT);
        
        // Clearing the name's stack. This stack contains all the info about the objects.
        GL11.glInitNames();
        
        // Now fill the stack with one element (or glLoadName will generate an error)
        GL11.glPushName(0);
        
        // Now modify the vieving volume, restricting selection area around the cursor
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        
        // Restrict the draw to an area around the cursor
        GLU.gluPickMatrix(x, y, 1.0f, 1.0f, GLAppUtil.intBuffToArray(view));

        // Set up camera
        setCamera();
        
        // Draw the objects onto the screen
        m_scene3d.render(true);
        
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();

        // get number of objects drawed in that area and return to render mode
        hits = GL11.glRenderMode(GL11.GL_RENDER);
        
        return GLAppUtil.getLastNameFromPickList(hits, buf);
    }
}