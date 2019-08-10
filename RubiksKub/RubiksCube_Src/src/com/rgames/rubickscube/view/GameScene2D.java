package com.rgames.rubickscube.view;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.rgames.rubickscube.RubicksCubeGame;
import com.rgames.rubickscube.model.GameModel;
import com.rgames.rubickscube.model.cube.RubicksCube;
import com.rgames.rubickscube.model.util.TimeUtil;
import com.rgames.rubickscube.view.general.MouseDragEvent;
import com.rgames.rubickscube.view.general.MouseListener;
import com.rgames.rubickscube.view.general.opengl.GLApp;
import com.rgames.rubickscube.view.general.opengl.font.GLFontPrinter;
import com.rgames.rubickscube.view.general.opengl.image.GLImage;
import com.rgames.rubickscube.view.general.opengl.image.GLImageUtil;
import com.rgames.rubickscube.view.objects.common.AbstractGameScene;
import com.rgames.rubickscube.view.objects.common.IObject2D;
import com.rgames.rubickscube.view.objects.common.Object3DAnimation;
import com.rgames.rubickscube.view.objects.common.GameScene2DObjects;
import com.rgames.rubickscube.view.objects.objects2d.Image2D;

/**
 */
public final class GameScene2D extends AbstractGameScene {

    /**
     */
    private final GameModel m_game = RubicksCubeGame.getInstance().getModel();
    
    /**
     */
    private long m_timeCreated;
    
    /**
     */
    private boolean m_showHelp;
    
    /**
     * Detect mouse dragging
     */
    private MouseDragEvent m_mouseDragEvent = new MouseDragEvent();

    /**
     */
    private GameScene2DObjects m_scene2dObjects = new GameScene2DObjects();
    
    /**
     */
    private IObject2D m_helpTextBackground;

    /**
     */
    private IObject2D m_cubeSolvedMsgBg;

    /**
     */
    private IObject2D m_cubeSolvedMsg;

    /**
     */
    private IObject2D m_rotateArrowLt;

    /**
     */
    private IObject2D m_rotateArrowRt;

    /**
     */
    private IObject2D m_rotateArrowUp;

    /**
     */
    private IObject2D m_rotateArrowDn;

    /**
     */
    private GLFontPrinter m_gameInfoTextPrinter;

    /**
     */
    private GLFontPrinter m_helpTextPrinter;
    
    public GameScene2D() {
    }
    
    public void render(final boolean simple) {
        GL11.glDisable(GL11.GL_LIGHTING);
        GLApp.setOrthoOn();

        // draw 2d objects
        m_scene2dObjects.draw();

        // show help
        showHelp();

        // show game info
        showGameInfo();
        
        GLApp.setOrthoOff();
    }
    
    public void mouseMove(final int x, final int y) {
        if (m_game.hasEnded() || m_game.isPaused()) {
            return;
        }
        
        if (m_mouseDragEvent.canDrag(x, y)) {
            // remember the mouse drag
            m_mouseDragEvent.dragTo(x, y);            
        } else {
            m_scene2dObjects.notifyMouseMoved(x, y);
        }
    }
    
    public void mouseDown(final int button, final int x, final int y) {
        if (m_game.hasEnded() || m_game.isPaused()) {
            return;
        }
        
        if (button == 2) {
            m_mouseDragEvent.dragStart(x, y);
        } else if (button == 0) { 
            m_scene2dObjects.notifyMouseDown(x, y);
        }
    }
    
    public void mouseUp(final int button, final int x, final int y) {
        if (m_game.hasEnded() || m_game.isPaused()) {
            return;
        }

        m_mouseDragEvent.dragEnd();
    }

    public void keyUp(final int keyCode) {
        if (m_game.isPaused()) {
            return;
        }        
    }
    
    public void keyDown(final int keyCode) {
        switch (keyCode) {
            case Keyboard.KEY_F1:
                m_showHelp = !m_showHelp;
                break;

            case Keyboard.KEY_F:
                GameView.toggleFps();
                break;

            case Keyboard.KEY_R:
                reset();
                break;
        
            default:
                controlView(keyCode);
                controlGame(keyCode);
        }
    }

    public void reset() {
        m_game.reset();
        showMessages();
    }

    public void showMessages() {
        m_cubeSolvedMsgBg.hide(!m_game.hasEnded());
        m_cubeSolvedMsg.hide(!m_game.hasEnded());
    }
    
    protected void createScene() {
        // create rotate arows (2d)
        createRotationArrows();
        
        // create cube solved image
        createCubeSolvedImage();
        
        // create help text
        createHelpText();
        
        // create game info text printer
        createGameInfoTextPrinter();
    }
    
    private void controlView(final int keyCode) {
        if (m_game.isPaused() && keyCode != Keyboard.KEY_P) {
            return;
        }
        
        switch (keyCode) {
            case Keyboard.KEY_R:
                reset();
                break;
        }
    }
    
    private void controlGame(final int keyCode) {
        if (m_game.hasEnded() || m_game.isPaused()) {
            return;
        }
        
        final RubicksCube cube = m_game.getCube();
        switch (keyCode) {
            case Keyboard.KEY_Z:
                cube.toggleAxis();
                break;
    
            case Keyboard.KEY_S:
                reset();
                cube.scramble();
                break;
        }
    }
    
    private void createRotationArrows() {
        final int rotationDuration = 500;
        final int rotationMethod = Object3DAnimation.FUNCTION_CURVE1;
        
        final GLImage textureImg = GLImageUtil.loadImage("data/images/arrowl.png");
        final int texHandle = GameView.makeTexture(textureImg);
        
        final float transparencyRest = 0.6f;
        final float transparencyHot = 1f;
        
        createArrowLt(texHandle, rotationDuration, rotationMethod, transparencyRest, transparencyHot);
        createArrowRt(texHandle, rotationDuration, rotationMethod, transparencyRest, transparencyHot);
        createArrowUp(texHandle, rotationDuration, rotationMethod, transparencyRest, transparencyHot);
        createArrowDn(texHandle, rotationDuration, rotationMethod, transparencyRest, transparencyHot);
    }
    
    // left arrow
    private void createArrowLt(final int texHandle, final int rotationDuration, final int rotationMethod,
            final float transparencyRest, final float transparencyHot) {
        m_rotateArrowLt = new Image2D();
        m_rotateArrowLt.setInitialScale(128, 128);
        m_rotateArrowLt.setInitialRotation(0);
        m_rotateArrowLt.setInitialPosition(200f, 300f);
        m_rotateArrowLt.addTexture(texHandle);
        m_rotateArrowLt.setTransparency(transparencyRest);
        m_rotateArrowLt.addMouseListener(new MouseListener() {
            public void mouseDown(final int mouseX, final int mouseY) {
                if (m_rotateArrowLt.mouseOver(mouseX, mouseY)) {
                    RubicksCubeGame.getInstance().getView().getScene3D().getCube3D().getAnimation().rotate(rotationDuration, rotationMethod, 0, 90, 0);
                }
            }

            public void mouseMoved(final int mouseX, final int mouseY) {
                if (m_rotateArrowLt.mouseOver(mouseX, mouseY)) {
                    m_rotateArrowLt.setTransparency(transparencyHot);
                } else {
                    m_rotateArrowLt.setTransparency(transparencyRest);
                }
            }
        });
        
        m_scene2dObjects.add(m_rotateArrowLt);
    }
    
    // right arrow
    private void createArrowRt(final int texHandle, final int rotationDuration, final int rotationMethod,
            final float transparencyRest, final float transparencyHot) {
        m_rotateArrowRt = new Image2D();
        m_rotateArrowRt.setInitialScale(128, 128);
        m_rotateArrowRt.setInitialRotation(180);
        m_rotateArrowRt.setInitialPosition(GameView.getViewportW() - 200f, 300);
        m_rotateArrowRt.addTexture(texHandle);
        m_rotateArrowRt.setTransparency(transparencyRest);
        m_rotateArrowRt.addMouseListener(new MouseListener() {
            public void mouseDown(final int mouseX, final int mouseY) {
                if (m_rotateArrowRt.mouseOver(mouseX, mouseY)) {
                    RubicksCubeGame.getInstance().getView().getScene3D().getCube3D().getAnimation().rotate(rotationDuration, rotationMethod, 0, -90, 0);
                }
            }

            public void mouseMoved(final int mouseX, final int mouseY) {
                if (m_rotateArrowRt.mouseOver(mouseX, mouseY)) {
                    m_rotateArrowRt.setTransparency(transparencyHot);
                } else {
                    m_rotateArrowRt.setTransparency(transparencyRest);
                }
            }
        });
        
        m_scene2dObjects.add(m_rotateArrowRt);
    }
    
    // up arrow
    private void createArrowUp(final int texHandle, final int rotationDuration, final int rotationMethod,
            final float transparencyRest, final float transparencyHot) {
        m_rotateArrowUp = new Image2D();
        m_rotateArrowUp.setInitialScale(128, 128);
        m_rotateArrowUp.setInitialRotation(-90);
        m_rotateArrowUp.setInitialPosition(GameView.getViewportW() / 2, GameView.getViewportH() - 100);
        m_rotateArrowUp.addTexture(texHandle);
        m_rotateArrowUp.setTransparency(transparencyRest);
        m_rotateArrowUp.addMouseListener(new MouseListener() {
            public void mouseDown(final int mouseX, final int mouseY) {
                if (m_rotateArrowUp.mouseOver(mouseX, mouseY)) {
                    RubicksCubeGame.getInstance().getView().getScene3D().getCube3D().getAnimation().rotate(rotationDuration, rotationMethod, 90, 0, 0);
                }
            }

            public void mouseMoved(final int mouseX, final int mouseY) {
                if (m_rotateArrowUp.mouseOver(mouseX, mouseY)) {
                    m_rotateArrowUp.setTransparency(transparencyHot);
                } else {
                    m_rotateArrowUp.setTransparency(transparencyRest);
                }
            }
        });
        
        m_scene2dObjects.add(m_rotateArrowUp);
    }
    
    // down arrow
    private void createArrowDn(final int texHandle, final int rotationDuration, final int rotationMethod,
            final float transparencyRest, final float transparencyHot) {
        m_rotateArrowDn = new Image2D();
        m_rotateArrowDn.setInitialScale(128, 128);
        m_rotateArrowDn.setInitialRotation(90);
        m_rotateArrowDn.setInitialPosition(GameView.getViewportW() / 2, 100);
        m_rotateArrowDn.addTexture(texHandle);
        m_rotateArrowDn.setTransparency(transparencyRest);
        m_rotateArrowDn.addMouseListener(new MouseListener() {
            public void mouseDown(final int mouseX, final int mouseY) {
                if (m_rotateArrowDn.mouseOver(mouseX, mouseY)) {
                    RubicksCubeGame.getInstance().getView().getScene3D().getCube3D().getAnimation().rotate(rotationDuration, rotationMethod, -90, 0, 0);
                }
            }

            public void mouseMoved(final int mouseX, final int mouseY) {
                if (m_rotateArrowDn.mouseOver(mouseX, mouseY)) {
                    m_rotateArrowDn.setTransparency(transparencyHot);
                } else {
                    m_rotateArrowDn.setTransparency(transparencyRest);
                }
            }
        });
        
        m_scene2dObjects.add(m_rotateArrowDn);
    }
    
    private void createHelpText() {
        final GLImage textureImg = GLImageUtil.loadImage("data/images/helptextbg.png");
        final int texHandle = GameView.makeTexture(textureImg);
        
        m_helpTextBackground = new Image2D();
        m_helpTextBackground.setInitialScale(GameView.getViewportW() - 200, GameView.getViewportH() - 200);
        m_helpTextBackground.setInitialPosition(GameView.getViewportW() / 2, GameView.getViewportH() / 2);
        m_helpTextBackground.setColor(0.5f, 0.5f, 0.5f, 0.85f);
        m_helpTextBackground.addTexture(texHandle);
    }
    
    private void createCubeSolvedImage() {
        GLImage textureImg = GLImageUtil.loadImage("data/images/helptextbg.png");
        
        m_cubeSolvedMsgBg = new Image2D();
        m_cubeSolvedMsgBg.setInitialScale(GameView.getViewportW(), 128);
        m_cubeSolvedMsgBg.setInitialPosition(GameView.getViewportW() / 2, GameView.getViewportH() / 2);
        m_cubeSolvedMsgBg.setColor(1f, 1f, 1f, 0.75f);
        m_cubeSolvedMsgBg.addTexture(GameView.makeTexture(textureImg));
        m_cubeSolvedMsgBg.hide(!m_game.hasEnded());
        m_scene2dObjects.add(m_cubeSolvedMsgBg);
        
        textureImg = GLImageUtil.loadImage("data/images/cube_solved.png");
        m_cubeSolvedMsg = new Image2D();
        m_cubeSolvedMsg.setInitialScale(256, 128);
        m_cubeSolvedMsg.setInitialPosition(GameView.getViewportW() / 2, GameView.getViewportH() / 2 + 20);
        m_cubeSolvedMsg.addTexture(GameView.makeTexture(textureImg));
        m_cubeSolvedMsg.hide(!m_game.hasEnded());
        m_scene2dObjects.add(m_cubeSolvedMsg);
    }
    
    private void createGameInfoTextPrinter() {
        m_gameInfoTextPrinter = new GLFontPrinter(GameView.getBaseFont());
        m_gameInfoTextPrinter.setDrawingArea(GameView.getViewportW(), GameView.getViewportH() - 20);
        m_gameInfoTextPrinter.setHorizontalAlign(GLFontPrinter.HORIZONTAL_ALIGN_END);
    }
    
    private void showHelp() {
        if (!m_showHelp) {
            return;
        }
        
        m_helpTextBackground.draw();
        if (m_helpTextPrinter == null) {
            final int startX = 10 + (int) (m_helpTextBackground.getPosition().x - (m_helpTextBackground.getScale().x / 2));
            final int startY = (int) (m_helpTextBackground.getPosition().y + (m_helpTextBackground.getScale().y / 2));
            
            m_helpTextPrinter = new GLFontPrinter(GameView.getBaseFont());
            m_helpTextPrinter.setDrawingArea(startX, startY - 30);
        }
        
        final GLFontPrinter f = m_helpTextPrinter;
        f.print("Rubick's Cube 3D");
        f.print("Created by Siim Annuk");
        f.print();
        f.print("   Controls:");
        f.print("   F1 - Show this help");
        f.print("   LEFT MOUSE BUTTON - Rotate selected level clockwise");
        f.print("   RIGHT MOUSE BUTTON - Rotate selected level counter clockwise");
        f.print("   MIDDLE MOUSE BUTTON + DRAG - Rotate cube");
        f.print("   ARROW UP - Rotate cube up");
        f.print("   ARROW DOWN - Rotate cube down");
        f.print("   ARROW LEFT - Rotate cube left");
        f.print("   ARROW RIGHT - Rotate cube right");
        f.print("   S - Scramble cube");
        f.print("   R - Reset cube");
        f.print("   Z - Toggle rotation axis");
        f.print("   F - Toggle FPS");
        f.print("   P - Toggle pause");
        f.print("   ESC - Quit game");
        f.done();
    }

    private void showGameInfo() {
        m_gameInfoTextPrinter.print("Time: " + TimeUtil.millisToTimeFormatted(m_game.getPlayTime()));
        m_gameInfoTextPrinter.print("Total moves: " + m_game.getCube().getTotalMoves());
        m_gameInfoTextPrinter.done();

        if (System.currentTimeMillis() - m_timeCreated < 3000) {
            GLFontPrinter.print(GameView.getBaseFont(), GameView.getViewportX(), 3, 
                    GLFontPrinter.HORIZONTAL_ALIGN_BEGINNING, "Press F1 for help...");
        }
        
        if (m_game.hasEnded()) {
            GLFontPrinter.print(GameView.getBaseFont(), 
                    GameView.getViewportW() / 2, GameView.getViewportH() / 2 - 20, 
                    GLFontPrinter.HORIZONTAL_ALIGN_CENTER, m_game.getCube().getTotalMoves() 
                    + " moves in " + TimeUtil.millisToTime(m_game.getPlayTime(), " hours ", " minutes ", " seconds"));
            GLFontPrinter.print(GameView.getBaseFont(), 
                    GameView.getViewportW() / 2, GameView.getViewportH() / 2 - 50, 
                    GLFontPrinter.HORIZONTAL_ALIGN_CENTER, "Press 'R' to play again...");
        } 
        
        if (m_game.isPaused()) {
            GLFontPrinter.print(GameView.getBaseFont(), 
                    GameView.getViewportW() / 2, GameView.getViewportH() - 20, 
                    GLFontPrinter.HORIZONTAL_ALIGN_CENTER, "PAUSED"); 
        }
    }
}
