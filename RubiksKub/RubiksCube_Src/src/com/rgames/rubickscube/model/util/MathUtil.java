package com.rgames.rubickscube.model.util;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

/**
 */
public final class MathUtil {

    /**
     */
    public static final float PIOVER180 = 0.0174532925f; 
    
    private MathUtil() {
        // utility class
    }
    
    public static float deg2rad(final float deg) {
        return (float) (deg * Math.PI / 180.0f);
    }
    
    public static float rad2deg(final float rad) {
        return (float) (rad * 180.0f / Math.PI);
    }
    
    public static Quaternion rotate(final double heading, final double attitude, final double bank) {
        // Assuming the angles are in radians.
        final double c1 = Math.cos(heading / 2);
        final double s1 = Math.sin(heading / 2);
        final double c2 = Math.cos(attitude / 2);
        final double s2 = Math.sin(attitude / 2);
        final double c3 = Math.cos(bank / 2);
        final double s3 = Math.sin(bank / 2);
        final double c1c2 = c1 * c2;
        final double s1s2 = s1 * s2;
        
        final double w = c1c2 * c3 - s1s2 * s3;
        final double x = c1c2 * s3 + s1s2 * c3;
        final double y = s1 * c2 * c3 + c1 * s2 * s3;
        final double z = c1 * s2 * c3 - s1 * c2 * s3;
        
        return new Quaternion((float) x, (float) y, (float) z, (float) w);
    }

    public static Quaternion eulerToQuat(final float pitch, final float yaw, final float roll) {
        final float p = pitch * PIOVER180 / 2.0f;
        final float y = yaw * PIOVER180 / 2.0f;
        final float r = roll * PIOVER180 / 2.0f;

        final float sinp = (float) Math.sin(p);
        final float siny = (float) Math.sin(y);
        final float sinr = (float) Math.sin(r);
        final float cosp = (float) Math.cos(p);
        final float cosy = (float) Math.cos(y);
        final float cosr = (float) Math.cos(r);
     
        final float qx = sinr * cosp * cosy - cosr * sinp * siny;
        final float qy = cosr * sinp * cosy + sinr * cosp * siny;
        final float qz = cosr * cosp * siny - sinr * sinp * cosy;
        final float qw = cosr * cosp * cosy + sinr * sinp * siny;
     
        final Quaternion q = new Quaternion(qx, qy, qz, qw);
        q.normalise();
        
        return q;
    }
    
    public static float round(final float aFloat, final int decimals) {
        return (float) Math.round(aFloat * (int) Math.pow(10, decimals)) / (int) Math.pow(10, decimals);
    }
    
    public static boolean isGreaterOrEqual(final Vector3f v1, final Vector3f v2) {
        return Math.abs(v1.x) >= Math.abs(v2.x) 
            && Math.abs(v1.y) >= Math.abs(v2.y) 
            && Math.abs(v1.z) >= Math.abs(v2.z);
    }
    
    public static float functionLinear(final float x, final float maxX) {
        return x / maxX;
    }
    
    public static float functionCurve1(final float x, final float maxX) {
        final float halfMaxX = maxX / 2;

        float y;
        if (x <= halfMaxX) {
            y = 1 - (float) Math.sqrt(1 - Math.pow(x / halfMaxX, 2));
        } else {
            y = 1 + (float) Math.sqrt(1 - Math.pow((maxX - x) / halfMaxX, 2));
        }
        
        return Math.abs(y / 2);
    }

    public static int random(final int min, final int max) {
        return (int) (Math.random() * (max - min)) + min; 
    }
    
    /**
     * @param number
     * @param digit
     * @return returns the nth digit in the number 
     */
    public static int right(final int number, final int digit) {
        final int pow = (int) Math.pow(10, digit - 1);
        if (digit > 0 && number >= pow) {
            final int num = (int) (number / pow);
            if (num < 10) {
                return num;
            } else {
                return num - (num / 10 * 10);
            }
        }
        
        return -1;
    }
}
