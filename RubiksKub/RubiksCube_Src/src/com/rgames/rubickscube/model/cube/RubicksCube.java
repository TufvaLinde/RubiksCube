package com.rgames.rubickscube.model.cube;

import com.rgames.rubickscube.model.util.MathUtil;

/**
 * Rubick's Cube consists of 26 individual cubes.
 * 
 * Each cube in the rubick's cube may have 1 .. 3 colored sides.
 */
public final class RubicksCube {

    /** 
     * The sides. 
     */
    public enum Sides { NONE, UP, DOWN, LEFT, RIGHT, FRONT, BACK };

    /**
     * Axes.
     */
    public enum Axis { X, Y, Z };

    /** 
     * By default, the Rubick's Cube is a 3x3 union of cubes. 
     */
    public static final int SIZE = 3;
    
    /**
     * Number of times to randomly rotate (scramble). 
     */
    private static final int SCRAMBLE_AMOUNT = 25;
    
    /** 
     * The cubes. 
     */
    private RubicksCubePart[][][] m_cube = new RubicksCubePart[SIZE][SIZE][SIZE];

    /**
     * The current axis to rotate around.
     * 
     */
    private Axis m_currentAxis = Axis.X;
    
    /**
     */
    private int m_currentLevel;
    
    /**
     */
    private int m_totalMoves;

    public RubicksCube() {
        initialize();
    }
    
    public RubicksCubePart[][][] getCube() {
        return m_cube;
    }
    
    /**
     * Scrambles the cube.
     */
    public void scramble() {
        final int currentLevel = m_currentLevel;
        final Axis currentAxis = m_currentAxis;
        
        for (int i = 0; i < SCRAMBLE_AMOUNT; i++) {
            m_currentLevel = MathUtil.random(0, SIZE);
            m_currentAxis = Axis.values()[MathUtil.random(0, SIZE)];
            
            rotate(MathUtil.random(0, 2) == 0);
        }
        
        m_currentLevel = currentLevel;
        m_currentAxis = currentAxis;
        m_totalMoves = 0;
    }
    
    /**
     * Resets the rotations, Rubick's Cube is in its 'solved' state.
     */
    public void reset() {
        m_totalMoves = 0;

        init1stFloor();
        init2ndFloor();
        init3rdFloor();
    }
    
    /**
     * Will rotate the current axis and current level 90 degrees counter clockwise.
     */
    public void rotate(final boolean clockWise) {
        m_totalMoves++;
        switch (m_currentAxis) {
            case X:
                rotateX(m_currentLevel, clockWise);
                break;
            case Y:
                rotateY(m_currentLevel, clockWise);
                break;
            case Z:
                rotateZ(m_currentLevel, clockWise);
                break;
        }
    }
   
    /**
     * Check if the cube is in its 'solved' state.
     * Each side must have only one type of colors.
     */
    public boolean isSolved() {
        final Sides[][] sides = new Sides[][] {
            // up
            {
                m_cube[2][0][0].up(), m_cube[2][1][0].up(), m_cube[2][2][0].up(),
                m_cube[2][0][1].up(), m_cube[2][1][1].up(), m_cube[2][2][1].up(),
                m_cube[2][0][2].up(), m_cube[2][1][2].up(), m_cube[2][2][2].up()
            },
            // down
            {
                m_cube[0][0][0].dn(), m_cube[0][1][0].dn(), m_cube[0][2][0].dn(),
                m_cube[0][0][1].dn(), m_cube[0][1][1].dn(), m_cube[0][2][1].dn(),
                m_cube[0][0][2].dn(), m_cube[0][1][2].dn(), m_cube[0][2][2].dn()
            },
            // left
            {
                m_cube[0][0][0].lt(), m_cube[0][0][1].lt(), m_cube[0][0][2].lt(),
                m_cube[1][0][0].lt(), m_cube[1][0][1].lt(), m_cube[1][0][2].lt(),
                m_cube[2][0][0].lt(), m_cube[2][0][1].lt(), m_cube[2][0][2].lt()
            },
            // right
            {
                m_cube[0][2][0].rt(), m_cube[0][2][1].rt(), m_cube[0][2][2].rt(),
                m_cube[1][2][0].rt(), m_cube[1][2][1].rt(), m_cube[1][2][2].rt(),
                m_cube[2][2][0].rt(), m_cube[2][2][1].rt(), m_cube[2][2][2].rt()
            },   
            // front
            {
                m_cube[0][0][0].ft(), m_cube[0][1][0].ft(), m_cube[0][2][0].ft(),
                m_cube[1][0][0].ft(), m_cube[1][1][0].ft(), m_cube[1][2][0].ft(),
                m_cube[2][0][0].ft(), m_cube[2][1][0].ft(), m_cube[2][2][0].ft()
            },
            // back
            {
                m_cube[0][0][2].bk(), m_cube[0][1][2].bk(), m_cube[0][2][2].bk(),
                m_cube[1][0][2].bk(), m_cube[1][1][2].bk(), m_cube[1][2][2].bk(),
                m_cube[2][0][2].bk(), m_cube[2][1][2].bk(), m_cube[2][2][2].bk()
            }   
        };
        
        if (!RubicksCubePart.areSidesEqual(sides)) {
            return false;
        }
        
        return true;
    }

    /**
     * @return the current axis to rotate by
     */
    public Axis getCurrentAxis() {
        return m_currentAxis;
    }
    
    /**
     * Sets the current axis to rotate by.
     * @param axis the axis to set
     */
    public void setCurrentAxis(final Axis axis) {
        m_currentAxis = axis;
    }
    
    /**
     * Sets the next axis to rotate by.
     */
    public void toggleAxis() {
        int ordinal = m_currentAxis.ordinal();
        if (ordinal == Axis.values().length - 1) {
            ordinal = 0;
        } else {
            ordinal++;
        }
        
        setCurrentAxis(Axis.values()[ordinal]);
    }
    
    /**
     * @return the current level
     */
    public int getCurrentLevel() {
        return m_currentLevel;
    }
    
    /**
     * Sets the current level.
     * @param level the level
     */
    public void setCurrentLevel(final int level) {
        if (level > -1 && level < SIZE) {
            m_currentLevel = level;
        }
    }

    /**
     * Toggles between levels.
     */
    public void toggleLevel() {
        if (m_currentLevel == SIZE - 1) {
            m_currentLevel = 0;
        } else {
            m_currentLevel++;
        }
    }

    /**
     * @return the total moves (rotations)
     */
    public int getTotalMoves() {
        return m_totalMoves;
    }
    
    // rotate about X-axis
    private void rotateX(final int col, final boolean clockWise) {
        final RubicksCubePart[] cornerParts = {
                m_cube[0][0][col], m_cube[0][2][col],
                m_cube[2][2][col], m_cube[2][0][col]
        };
        rotate(clockWise ? cornerParts : reverse(cornerParts), Axis.X, clockWise);
        
        final RubicksCubePart[] middleParts = {
                m_cube[0][1][col], m_cube[1][2][col],
                m_cube[2][1][col], m_cube[1][0][col]
        };
        rotate(clockWise ? middleParts : reverse(middleParts), Axis.X, clockWise);
    }
    
    // rotate about Y-axis
    private void rotateY(final int floor, final boolean clockWise) {
        final RubicksCubePart[] cornerParts = {
                m_cube[floor][0][0], m_cube[floor][2][0],
                m_cube[floor][2][2], m_cube[floor][0][2]
        };
        rotate(clockWise ? cornerParts : reverse(cornerParts), Axis.Y, clockWise);
        
        final RubicksCubePart[] middleParts = {
                m_cube[floor][1][0], m_cube[floor][2][1],
                m_cube[floor][1][2], m_cube[floor][0][1]
        };
        rotate(clockWise ? middleParts : reverse(middleParts), Axis.Y, clockWise);
    }
    
    // rotate about Z-axis
    private void rotateZ(final int row, final boolean clockWise) {
        final RubicksCubePart[] cornerParts = {
                m_cube[0][row][0], m_cube[2][row][0],
                m_cube[2][row][2], m_cube[0][row][2]
        };
        rotate(clockWise ? cornerParts : reverse(cornerParts), Axis.Z, clockWise);
        
        final RubicksCubePart[] middleParts = {
                m_cube[1][row][0], m_cube[2][row][1],
                m_cube[1][row][2], m_cube[0][row][1]
        };
        rotate(clockWise ? middleParts : reverse(middleParts), Axis.Z, clockWise);
    }
    
    private void rotate(final RubicksCubePart[] parts, final Axis axis, final boolean clockWise) {
        final RubicksCubePart temp = new RubicksCubePart(parts[0]);
        RubicksCubePart fromPart;
        for (int i = 0; i < parts.length; i++) {
            if (i < parts.length - 1) {
                fromPart = parts[i + 1];
            } else {
                fromPart = temp;
            }
            
            switch (axis) {
                case X:
                    parts[i].rotX(fromPart, clockWise);
                    break;
                case Y:
                    parts[i].rotY(fromPart, clockWise);
                    break;
                case Z:
                    parts[i].rotZ(fromPart, clockWise);
                    break;
            }
            
        }
    }
    
    private void initialize() {
        init1stFloor();
        init2ndFloor();
        init3rdFloor();
    }
    
    private void init1stFloor() {
        // RubicksCubePart(up, dn, lt, rt, ft, bk);
        
        // bottom, left, back
        m_cube[0][0][0] = new RubicksCubePart(Sides.NONE, Sides.DOWN, Sides.LEFT, Sides.NONE, Sides.NONE, Sides.BACK);
        
        // bottom, left
        m_cube[0][0][1] = new RubicksCubePart(Sides.NONE, Sides.DOWN, Sides.LEFT, Sides.NONE, Sides.NONE, Sides.NONE);

        // bottom, left, front
        m_cube[0][0][2] = new RubicksCubePart(Sides.NONE, Sides.DOWN, Sides.LEFT, Sides.NONE, Sides.FRONT, Sides.NONE);

        // bottom, back
        m_cube[0][1][0] = new RubicksCubePart(Sides.NONE, Sides.DOWN, Sides.NONE, Sides.NONE, Sides.NONE, Sides.BACK);
        
        // bottom
        m_cube[0][1][1] = new RubicksCubePart(Sides.NONE, Sides.DOWN, Sides.NONE, Sides.NONE, Sides.NONE, Sides.NONE);
        
        // bottom, front
        m_cube[0][1][2] = new RubicksCubePart(Sides.NONE, Sides.DOWN, Sides.NONE, Sides.NONE, Sides.FRONT, Sides.NONE);
        
        // bottom, back, right
        m_cube[0][2][0] = new RubicksCubePart(Sides.NONE, Sides.DOWN, Sides.NONE, Sides.RIGHT, Sides.NONE, Sides.BACK);
        
        // bottom, right
        m_cube[0][2][1] = new RubicksCubePart(Sides.NONE, Sides.DOWN, Sides.NONE, Sides.RIGHT, Sides.NONE, Sides.NONE);

        // bottom, front, right
        m_cube[0][2][2] = new RubicksCubePart(Sides.NONE, Sides.DOWN, Sides.NONE, Sides.RIGHT, Sides.FRONT, Sides.NONE);
    }

    private void init2ndFloor() {
        // RubicksCubePart(up, dn, lt, rt, ft, bk);
        
        // back, left
        m_cube[1][0][0] = new RubicksCubePart(Sides.NONE, Sides.NONE, Sides.LEFT, Sides.NONE, Sides.NONE, Sides.BACK);

        // left
        m_cube[1][0][1] = new RubicksCubePart(Sides.NONE, Sides.NONE, Sides.LEFT, Sides.NONE, Sides.NONE, Sides.NONE);

        // front, left
        m_cube[1][0][2] = new RubicksCubePart(Sides.NONE, Sides.NONE, Sides.LEFT, Sides.NONE, Sides.FRONT, Sides.NONE);

        // back
        m_cube[1][1][0] = new RubicksCubePart(Sides.NONE, Sides.NONE, Sides.NONE, Sides.NONE, Sides.NONE, Sides.BACK);

        // in the center, no cube!
        m_cube[1][1][1] = null;

        // front
        m_cube[1][1][2] = new RubicksCubePart(Sides.NONE, Sides.NONE, Sides.NONE, Sides.NONE, Sides.FRONT, Sides.NONE);
        
        // back, right
        m_cube[1][2][0] = new RubicksCubePart(Sides.NONE, Sides.NONE, Sides.NONE, Sides.RIGHT, Sides.NONE, Sides.BACK);

        // right
        m_cube[1][2][1] = new RubicksCubePart(Sides.NONE, Sides.NONE, Sides.NONE, Sides.RIGHT, Sides.NONE, Sides.NONE);

        // front, right
        m_cube[1][2][2] = new RubicksCubePart(Sides.NONE, Sides.NONE, Sides.NONE, Sides.RIGHT, Sides.FRONT, Sides.NONE);
    }

    private void init3rdFloor() {
        // RubicksCubePart(up, dn, lt, rt, ft, bk);
     
        // top, back, left
        m_cube[2][0][0] = new RubicksCubePart(Sides.UP, Sides.NONE, Sides.LEFT, Sides.NONE, Sides.NONE, Sides.BACK);
        
        // top, left
        m_cube[2][0][1] = new RubicksCubePart(Sides.UP, Sides.NONE, Sides.LEFT, Sides.NONE, Sides.NONE, Sides.NONE);

        // top, front, left
        m_cube[2][0][2] = new RubicksCubePart(Sides.UP, Sides.NONE, Sides.LEFT, Sides.NONE, Sides.FRONT, Sides.NONE);

        // top, back
        m_cube[2][1][0] = new RubicksCubePart(Sides.UP, Sides.NONE, Sides.NONE, Sides.NONE, Sides.NONE, Sides.BACK);

        // top
        m_cube[2][1][1] = new RubicksCubePart(Sides.UP, Sides.NONE, Sides.NONE, Sides.NONE, Sides.NONE, Sides.NONE);

        // top, front
        m_cube[2][1][2] = new RubicksCubePart(Sides.UP, Sides.NONE, Sides.NONE, Sides.NONE, Sides.FRONT, Sides.NONE);

        // top, back, right
        m_cube[2][2][0] = new RubicksCubePart(Sides.UP, Sides.NONE, Sides.NONE, Sides.RIGHT, Sides.NONE, Sides.BACK);
        
        // top, right
        m_cube[2][2][1] = new RubicksCubePart(Sides.UP, Sides.NONE, Sides.NONE, Sides.RIGHT, Sides.NONE, Sides.NONE);

        // top, front, right
        m_cube[2][2][2] = new RubicksCubePart(Sides.UP, Sides.NONE, Sides.NONE, Sides.RIGHT, Sides.FRONT, Sides.NONE);
    }
    
    private static RubicksCubePart[] reverse(final RubicksCubePart[] parts) {
        final RubicksCubePart[] reversedParts = new RubicksCubePart[parts.length];
        for (int i = parts.length; i > 0; i--) {
            reversedParts[parts.length - i] = parts[i - 1];
        }
        
        return reversedParts;
    }
}
