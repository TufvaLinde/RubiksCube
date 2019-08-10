package com.rgames.rubickscube.view;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.rgames.rubickscube.RubicksCubeGame;
import com.rgames.rubickscube.model.GameModel;
import com.rgames.rubickscube.model.cube.RubicksCube;
import com.rgames.rubickscube.model.util.MathUtil;
import com.rgames.rubickscube.view.general.MouseDragEvent;
import com.rgames.rubickscube.view.general.opengl.image.GLImageUtil;
import com.rgames.rubickscube.view.objects.common.AbstractGameScene;
import com.rgames.rubickscube.view.objects.common.IObject3D;
import com.rgames.rubickscube.view.objects.common.Object3DAnimation;
import com.rgames.rubickscube.view.objects.objects3d.GroundPlane3D;
import com.rgames.rubickscube.view.objects.objects3d.RubicksCube3D;

/**
 */
public final class GameScene3D extends AbstractGameScene {

    /**
     */
    private final GameModel m_game = RubicksCubeGame.getInstance().getModel();
    
    /**
     */
    private long m_lastSelectionTime;

    /**
     */
    private int m_selectedObject = -1;
    
    /**
     * Detect mouse dragging
     */
    private MouseDragEvent m_mouseDragEvent = new MouseDragEvent();

    /**
     */
    private IObject3D m_groundPlane;

    /**
     */
    private IObject3D m_rubicksCube3D;
    
    public GameScene3D() {
    }
    
    public IObject3D getCube3D() {
        return m_rubicksCube3D; 
    }
    
    public void render(final boolean simple) {
        // select model view for subsequent transforms
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();

        // clear depth buffer and color
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        if (!simple) {
            // render ground plane
            m_groundPlane.draw();
    
            // rotate, when game over
            if (m_game.hasEnded()) {
                m_rubicksCube3D.rotate(0, 1, 0);
            }
        }
        
        // render cube
        m_rubicksCube3D.draw();
    }

    public void reset() {
        m_rubicksCube3D.reset();
    }

    public void mouseMove(final int x, final int y) {
        if (m_game.hasEnded() || m_game.isPaused()) {
            return;
        }
        
        if (m_mouseDragEvent.canDrag(x, y)) {
            // calculate the angles to rotate mapped to the mouse drag
            final float rotateX = 180.0f / ((float) GameView.getViewportH() / (m_mouseDragEvent.getLastY() - y));
            final float rotateY = 180.0f / ((float) GameView.getViewportW() / (m_mouseDragEvent.getLastX() - x));
            
            // rotate the cube
            m_rubicksCube3D.rotate(rotateX, -rotateY, 0);
            
            // remember the mouse drag
            m_mouseDragEvent.dragTo(x, y);            
        } else {
            selectCubeLevelFromMouse(x, y);
        }
    }
    
    public void mouseDown(final int button, final int x, final int y) {
        if (m_game.hasEnded() || m_game.isPaused()) {
            return;
        }
        
        if (button == 2) {
            m_mouseDragEvent.dragStart(x, y);
        } else  {
            if (MathUtil.right(m_selectedObject, 4) == m_rubicksCube3D.getId()) {
                m_game.start();
                m_game.getCube().rotate(button == 0);
                m_game.updateState();
                
                RubicksCubeGame.getInstance().getView().getScene2D().showMessages();
            } 
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
        //if (!m_game.isPaused() && keyCode != Keyboard.KEY_P) {
            controlView(keyCode);
        //}
    
        if (!(m_game.isPaused() || m_game.hasEnded())) {
            controlGame(keyCode);
        }
    }

    private void controlView(final int keyCode) {
        switch (keyCode) {
            case Keyboard.KEY_P:
                m_game.pause();
                break;
                
            case Keyboard.KEY_R:
                reset();
                break;
    
            case Keyboard.KEY_UP:
                m_rubicksCube3D.getAnimation().rotate(500, Object3DAnimation.FUNCTION_CURVE1, -90, 0, 0);
                break;
            
            case Keyboard.KEY_DOWN:
                m_rubicksCube3D.getAnimation().rotate(500, Object3DAnimation.FUNCTION_CURVE1, 90, 0, 0);
                break;
            
            case Keyboard.KEY_LEFT:
                m_rubicksCube3D.getAnimation().rotate(500, Object3DAnimation.FUNCTION_CURVE1, 0, -90, 0);
                break;
            
            case Keyboard.KEY_RIGHT:
                m_rubicksCube3D.getAnimation().rotate(500, Object3DAnimation.FUNCTION_CURVE1, 0, 90, 0);
                break;
        }
    }
    
    private void controlGame(final int keyCode) {
        final RubicksCube cube = m_game.getCube();
        switch (keyCode) {
            case Keyboard.KEY_Z:
                cube.toggleAxis();
                break;
    
            case Keyboard.KEY_X:
                cube.toggleLevel();
                break;
    
            case Keyboard.KEY_S:
                reset();
                cube.scramble();
                break;
        }
    }
    
    private void selectCubeLevelFromMouse(final int x, final int y) {
        if (m_game.hasEnded()) {
            return;
        }

        if (System.currentTimeMillis() - m_lastSelectionTime > 100) {
            // TODO optimize?
            m_lastSelectionTime = System.currentTimeMillis();
            m_selectedObject = RubicksCubeGame.getInstance().getView().glSelect(x, y);
            if (MathUtil.right(m_selectedObject, 4) == m_rubicksCube3D.getId()) {
                final int floor = MathUtil.right(m_selectedObject, 3); 
                final int row = MathUtil.right(m_selectedObject, 2);
                final int column = MathUtil.right(m_selectedObject, 1);
                
                final RubicksCube cube = m_game.getCube();
                switch (cube.getCurrentAxis()) {
                    case X:
                        cube.setCurrentLevel(row);
                        break;
                    case Y:
                        cube.setCurrentLevel(floor);
                        break;
                    case Z:
                        cube.setCurrentLevel(column);
                        break;
                }
            }
        }
    }
    
    protected void createScene() {
        // Create ground plane
        createGroundPlane();
        
        // Create rubicks cube
        createRubicksCube();
    }

    private void createGroundPlane() {
        m_groundPlane = new GroundPlane3D();
        m_groundPlane.setInitialScale(15f, 1f, 15f);
        m_groundPlane.setInitialPosition(0f, -3f, 0f);
        m_groundPlane.addTexture(GameView.makeTexture(GLImageUtil.loadImage("data/images/ground_plane.jpg")));
    }
    
    private void createRubicksCube() {
        m_rubicksCube3D = new RubicksCube3D();
        m_rubicksCube3D.setInitialScale(0.5f, 0.5f, 0.5f);
        m_rubicksCube3D.setInitialRotation(0f, 45f, 0f);
        m_rubicksCube3D.addTexture("xx", GameView.makeTexture(GLImageUtil.loadImage("data/images/cube_xx.png")));
        m_rubicksCube3D.addTexture("up", GameView.makeTexture(GLImageUtil.loadImage("data/images/cube_up.png")));
        m_rubicksCube3D.addTexture("dn", GameView.makeTexture(GLImageUtil.loadImage("data/images/cube_dn.png")));
        m_rubicksCube3D.addTexture("lt", GameView.makeTexture(GLImageUtil.loadImage("data/images/cube_lt.png")));
        m_rubicksCube3D.addTexture("rt", GameView.makeTexture(GLImageUtil.loadImage("data/images/cube_rt.png")));
        m_rubicksCube3D.addTexture("ft", GameView.makeTexture(GLImageUtil.loadImage("data/images/cube_ft.png")));
        m_rubicksCube3D.addTexture("bk", GameView.makeTexture(GLImageUtil.loadImage("data/images/cube_bk.png")));
    }
}
