package com.rgames.rubickscube.view.objects.common;

import org.lwjgl.util.vector.Vector3f;

import com.rgames.rubickscube.model.util.MathUtil;

/**
 */
public final class Object3DAnimation {

    /**
     */
    public static final int FUNCTION_LINEAR = 0x001;
    
    /**
     */
    public static final int FUNCTION_CURVE1 = 0x002;

    /**
     */
    private final IObject3D m_object;
    
    /**
     */
    private RotationAnimation m_rotation = new RotationAnimation();
    
    public Object3DAnimation(final IObject3D object) {
        m_object = object;
    }
    
    public void rotate(final int duration, final int function, 
            final float targetX, final float targetY, final float targetZ) {
        if (!m_rotation.isActive()) {
            m_rotation.set(duration, function, targetX, targetY, targetZ);
        }
    }
    
    public void animate() {
        if (m_rotation.isActive()) {
            m_rotation.animate();
        }
    }
    
    public boolean isActive() {
        return m_rotation.isActive();
    }
    
    protected IObject3D getObject() {
        return m_object;
    }
    
    /**
     */
    private final class RotationAnimation {
        
        /**
         */
        private boolean m_isActive;

        /**
         */
        private int m_function;

        /**
         */
        private int m_duration;
        
        /**
         */
        private long m_startTime;
        
        /**
         */
        private Vector3f m_prev = new Vector3f();

        /**
         */
        private Vector3f m_current = new Vector3f();

        /**
         */
        private Vector3f m_target = new Vector3f();
        
        public RotationAnimation() {
        }
        
        public void set(final int duration, final int function, 
                final float targetX, final float targetY, final float targetZ) {
            m_isActive = true;
            m_duration = duration;
            m_function = function;
            m_startTime = System.currentTimeMillis();
            
            m_prev.set(0, 0, 0);
            m_current.set(0, 0, 0);
            m_target.set(targetX, targetY, targetZ);
        }
        
        public void reset() {
            m_isActive = false;
            m_duration = 0;
            m_function = FUNCTION_LINEAR;
            m_startTime = 0;
            
            m_prev.set(0, 0, 0);
            m_current.set(0, 0, 0);
            m_target.set(0, 0, 0);
        }
        
        public void animate() {
            final long currTime = System.currentTimeMillis();
            final long passedTime = currTime - m_startTime;
            if (isFinished(passedTime)) {
                //System.out.println("Finished! current = " + m_current + ", target = " + m_target);
                //getObject().rotate(m_current.x, m_current.y, m_current.z);

                reset();
                return;
            }
            
            final float fx = calculateValue(passedTime);
            
            calculateX(fx);
            getObject().rotate(m_current.x, 0, 0);
            
            calculateY(fx);
            getObject().rotate(0, m_current.y, 0);
            
            calculateZ(fx);
            getObject().rotate(0, 0, m_current.z);

            //getObject().rotate(m_current.x, m_current.y, m_current.z);
        }
        
        public boolean isActive() {
            return m_isActive;
        }
        
        private void calculateX(final float value) {
            if (Math.abs(m_current.x) < Math.abs(m_target.x)) {
                final float fx = value * m_target.x; 
                m_current.x = fx - m_prev.x;  
                m_prev.x = fx; 
                
                if (Math.abs(m_current.x) > Math.abs(m_target.x)) {
                    m_current.x = m_target.x;
                }
            }
        }
        
        private void calculateY(final float value) {
            if (Math.abs(m_current.y) < Math.abs(m_target.y)) {
                final float fx = value * m_target.y; 
                m_current.y = fx - m_prev.y;  
                m_prev.y = fx;
                
                if (Math.abs(m_current.y) > Math.abs(m_target.y)) {
                    m_current.y = m_target.y;
                }
            }
        }
        
        private void calculateZ(final float value) {
            if (Math.abs(m_current.z) < Math.abs(m_target.z)) {
                final float fx = value * m_target.z; 
                m_current.z = fx - m_prev.z;  
                m_prev.z = fx;
                
                if (Math.abs(m_current.z) > Math.abs(m_target.z)) {
                    m_current.z = m_target.z;
                }
            }
        }

        private float calculateValue(final float passedTime) {
            switch (m_function) {
                case FUNCTION_CURVE1:
                    return MathUtil.functionCurve1(passedTime, m_duration);
                default:
                    return MathUtil.functionLinear(passedTime, m_duration);                    
            }
        }
        
        private boolean isFinished(final long passedTime) {
            return (passedTime > m_duration)
                || (MathUtil.isGreaterOrEqual(m_current, m_target));
        }
    }
}
