package test;

import com.rgames.rubickscube.model.util.MathUtil;
import com.rgames.rubickscube.model.util.TimeUtil;

import junit.framework.TestCase;

/**
 */
public final class TestMath extends TestCase {

    public void test() {
        //mathsTest1();
        //randomTest();
        //getDigits();
        timeToString();
    }
    
    private void timeToString() {
        System.out.println(TimeUtil.millisToTime(60 * 60005, " hours ", " minutes ", " seconds "));
    }
    
    private void getDigits() {
        getDigit(123, 1);
        getDigit(2123, 3);
        getDigit(52076, 4);
        getDigit(8776, 4);
        getDigit(635, 4);
        getDigit(635, 0);
    }
    
    private void getDigit(final int number, final int nth) {
        System.out.println(nth + ". digit from right in " + number + " is " + MathUtil.right(number, nth));
    }
    
    private void randomTest() {
        for (int i = 0; i < 100; i++) {
            System.out.println(MathUtil.random(0, 3));
        }
    }
    
    /**
     */
    private void mathsTest1() {
        final float maxX = 500f;
        final float maxY = 90f;
        final float stepY = maxX / maxY;
        
        final float halfMaxX = maxX / 2;
        
        float prevY = 0;
        float y = 0;
        float ay = 0;
        float ySum = 0;
        for (int i = 0; i <= maxX; i++) {
            final float x = (float) i; 
            
            y = MathUtil.functionCurve1(x, maxX) * 90f;
            //y = stepY;
            
            ay = y - prevY;
            
            //ySum += ay;
            
            System.out.println("x = " + x + "; ay = " + ay + ", y = " + y + ", prevY = " + prevY);

            prevY = y;
        }
        
        System.out.println("Sum Y = " + ySum);
        
    }
}
