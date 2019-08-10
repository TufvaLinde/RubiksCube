package com.rgames.rubickscube.view.objects.objects3d;

import org.lwjgl.opengl.GL11;

import com.rgames.rubickscube.RubicksCubeGame;
import com.rgames.rubickscube.model.cube.RubicksCube;
import com.rgames.rubickscube.model.cube.RubicksCubePart;
import com.rgames.rubickscube.view.general.opengl.GLAppRenderUtil;
import com.rgames.rubickscube.view.objects.common.AbstractObject3D;

/**
 */
public final class RubicksCube3D extends AbstractObject3D {
    
    public RubicksCube3D() {
        super("RubicksCube");
    }

    protected void drawObject() {
        final RubicksCubeGame game = RubicksCubeGame.getInstance();
        final RubicksCube cube = game.getModel().getCube();
        final RubicksCubePart[][][] cubeParts = cube.getCube();
        
        final float spacing = 2.0f;
        int row;
        int floor;
        int column;
        
        RubicksCubePart part;
        
        final RubicksCube.Axis currentAxis = cube.getCurrentAxis();
        final int currentLevel = cube.getCurrentLevel();
        
        // floors
        for (floor = 0; floor < RubicksCube.SIZE; floor++) {
            // rows
            for (row = 0; row < RubicksCube.SIZE; row++) {
                // columns
                for (column = 0; column < RubicksCube.SIZE; column++) {
                    part = cubeParts[floor][column][row];
                    if (part != null) {
                        if (highlightAxis(currentAxis, currentLevel, floor, column, row)) {
                            setColor(1f, 1f, 1f, 1f);
                        } else {
                            setColor(0.5f, 0.5f, 0.5f, 0.75f);
                        }
                        
                        GL11.glMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT_AND_DIFFUSE, getColor());
                        GL11.glLoadName((getId() * 1000) + (floor * 100) + (row * 10) + column);
                        
                        drawCube(part, (column - 1) * spacing, (floor - 1) * spacing, (row - 1) * spacing);
                    }
                }
            }
        }
    }
    
    private void drawCube(final RubicksCubePart part, final float x, final float y, final float z) {
        GL11.glTranslatef(x, y, z);
        
        GLAppRenderUtil.renderCubeMapped(getTex(part.up()), getTex(part.dn()), 
                getTex(part.lt()), getTex(part.rt()), getTex(part.ft()), getTex(part.bk()));
        
        GL11.glTranslatef(-x, -y, -z);
    }
    
    private int getTex(final RubicksCube.Sides side) {
        switch (side) {
            case UP:
                return getTexture("up");
            
            case DOWN:
                return getTexture("dn");
        
            case LEFT:
                return getTexture("lt");
                
            case RIGHT:
                return getTexture("rt");
                
            case FRONT:
                return getTexture("ft");
            
            case BACK:
                return getTexture("bk");
                
            default:
                return getTexture("xx");
            
        }
    }
    
    private boolean highlightAxis(final RubicksCube.Axis currentAxis, final int currentLevel, 
            final int floor, final int column, final int row) {
        
        switch (currentAxis) {
            case X:
                return row == currentLevel;
                
            case Y:
                return floor == currentLevel;
                
            case Z:
                return column == currentLevel;
        }
        
        return false;
    }
}
