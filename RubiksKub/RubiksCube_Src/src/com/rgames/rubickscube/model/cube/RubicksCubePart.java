package com.rgames.rubickscube.model.cube;

/**
 * Each cube in the rubick's cube may have 1 .. 3 colored sides. 
 */
public final class RubicksCubePart {

    /** 
     * Up. 
     */
    private RubicksCube.Sides m_up;
    
    /** 
     * Down. 
     */
    private RubicksCube.Sides m_dn;

    /** 
     * Left. 
     */
    private RubicksCube.Sides m_lt;

    /** 
     * Right. 
     */
    private RubicksCube.Sides m_rt;

    /** 
     * Front. 
     */
    private RubicksCube.Sides m_ft;

    /** 
     * Back. 
     */
    private RubicksCube.Sides m_bk;
    
    public RubicksCubePart(final RubicksCubePart part) {
        set(part);
    }
    
    public RubicksCubePart(final RubicksCube.Sides up, final RubicksCube.Sides dn, final RubicksCube.Sides lt,
            final RubicksCube.Sides rt, final RubicksCube.Sides ft, final RubicksCube.Sides bk) {
        m_up = up;
        m_dn = dn;
        m_lt = lt;
        m_rt = rt;
        m_ft = ft;
        m_bk = bk;
    }
    
    public void set(final RubicksCubePart part) {
        m_up = part.up();
        m_dn = part.dn();
        m_lt = part.lt();
        m_rt = part.rt();
        m_ft = part.ft();
        m_bk = part.bk();
    }
    
    /**
     * Rotate 90 degrees clockwise on X-axis.
     * @param part
     */
    public void rotX(final RubicksCubePart part, final boolean clockWise) {
        if (numSides() == part.numSides()) {
            ft(part.ft()); // front
            bk(part.bk()); // back
            if (clockWise) {
                dn(part.rt()); // right to down
                lt(part.dn()); // down to left
                up(part.lt()); // left to up
                rt(part.up()); // up to right
            } else {
                dn(part.lt()); // left to down
                lt(part.up()); // up to left
                up(part.rt()); // right to up
                rt(part.dn()); // down to right
            }
        }
    }
    
    /**
     * Rotate 90 degrees clockwise on Y-axis.
     * @param part
     */
    public void rotY(final RubicksCubePart part, final boolean clockWise) {
        if (numSides() == part.numSides()) {
            up(part.up()); // up
            dn(part.dn()); // down
            if (clockWise) {
                lt(part.bk()); // back to left
                ft(part.lt()); // left to front
                rt(part.ft()); // front to right
                bk(part.rt()); // right to back
            } else {
                lt(part.ft()); // front to left
                ft(part.rt()); // right to front
                rt(part.bk()); // back to right
                bk(part.lt()); // left to back
            }
        }
    }
    
    /**
     * Rotate 90 degrees clockwise on X-axis.
     * @param part
     */
    public void rotZ(final RubicksCubePart part, final boolean clockWise) {
        if (numSides() == part.numSides()) {
            lt(part.lt()); // left
            rt(part.rt()); // right
            if (clockWise) {
                bk(part.up()); // up to back
                dn(part.bk()); // back to down
                ft(part.dn()); // down to front
                up(part.ft()); // front to up
            } else {
                bk(part.dn()); // down to back
                dn(part.ft()); // front to down
                ft(part.up()); // up to front
                up(part.bk()); // back to up
            }
        }
    }
    
    public int numSides() {
        int i = 0;
        
        i += isColored(up());
        i += isColored(dn());
        i += isColored(lt());
        i += isColored(rt());
        i += isColored(ft());
        i += isColored(bk());
        
        return i;
    }
    
    public RubicksCube.Sides up() {
        return m_up;
    }

    public void up(final RubicksCube.Sides side) {
        if (canAssign(m_up, side)) {
            m_up = side;
        }
    }

    public RubicksCube.Sides dn() {
        return m_dn;
    }

    public void dn(final RubicksCube.Sides side) {
        if (canAssign(m_dn, side)) {
            m_dn = side;
        }
    }
    
    public RubicksCube.Sides lt() {
        return m_lt;
    }

    public void lt(final RubicksCube.Sides side) {
        if (canAssign(m_lt, side)) {
            m_lt = side;
        }
    }

    public RubicksCube.Sides rt() {
        return m_rt;
    }

    public void rt(final RubicksCube.Sides side) {
        if (canAssign(m_rt, side)) {
            m_rt = side;
        }
    }
    
    public RubicksCube.Sides ft() {
        return m_ft;
    }

    public void ft(final RubicksCube.Sides side) {
        if (canAssign(m_ft, side)) {
            m_ft = side;
        }
    }
    
    public RubicksCube.Sides bk() {
        return m_bk;
    }
    
    public void bk(final RubicksCube.Sides side) {
        if (canAssign(m_bk, side)) {
            m_bk = side;
        }
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer("CubePart[ ");
        
        if (isColored(up()) == 1) {
            sb.append("up ");
        }

        if (isColored(dn()) == 1) {
            sb.append("down ");
        }
        
        if (isColored(lt()) == 1) {
            sb.append("left ");
        }

        if (isColored(rt()) == 1) {
            sb.append("right ");
        }
        
        if (isColored(ft()) == 1) {
            sb.append("front ");
        }

        if (isColored(bk()) == 1) {
            sb.append("back ");
        }
        
        sb.append("]");
        
        return sb.toString();
    }
    
    private int isColored(final RubicksCube.Sides side) {
        return side != RubicksCube.Sides.NONE ? 1 : 0;
    }
    
    private boolean canAssign(final RubicksCube.Sides dst, final RubicksCube.Sides src) {
        return isColored(dst) == 1 && isColored(src) == 1;
    }
    
    public static boolean areSidesEqual(final RubicksCube.Sides[][] sides) {
        for (int i = 0; i < sides.length - 1; i++) {
            for (int j = 0; j < sides[i].length - 1; j++) {
                if (sides[i][j] != sides[i][j + 1]) {
                    return false;
                }
            }
        }
        
        return true;
    }
}
