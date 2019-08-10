package com.rgames.rubickscube.model.util;

import java.util.Calendar;

/**
 */
public final class TimeUtil {

    /**
     */
    public static final int MILLIS_IN_SECOND = 1000;
    
    /**
     */
    public static final int MILLIS_IN_MINUTE = MILLIS_IN_SECOND * 60;
    
    /**
     */
    public static final int MILLIS_IN_HOUR = MILLIS_IN_MINUTE * 60;
    
    private TimeUtil() {
        // utility class
    }
    
    public static String millisToTime(final long millis, final String sHours, 
            final String sMinutes, final String sSeconds) {
        final StringBuffer sb = new StringBuffer();
        final int[] time = millisToTime(millis);
        
        if (time[0] > 0) {
            sb.append(time[0]).append(sHours);
        }

        if (time[1] > 0) {
            sb.append(time[1]).append(sMinutes);
        }

        final float seconds = MathUtil.round((float) time[2] + (float) time[3] / MILLIS_IN_SECOND, 1); 
        if (time[2] > 0 || time[3] > 0) {
            sb.append(seconds).append(sSeconds);
        }
        
        return sb.toString();
    }
    
    public static String millisToTimeFormatted(final long millis) {
        if (millis < 0) {
            return "--:--:--.-";
        }
        
        final int[] time = millisToTime(millis);

        final StringBuffer sb = new StringBuffer();
        sb.append(leadingZero(time[0])).append(":");
        sb.append(leadingZero(time[1])).append(":");

        final float seconds = MathUtil.round((float) time[2] + (float) time[3] / MILLIS_IN_SECOND, 1); 
        sb.append(leadingZero(seconds));
        
        return sb.toString();
    }
    
    public static int[] millisToTime(final long millis) {
        long time = millis;
        
        int iHours = 0;        
        while (time >= MILLIS_IN_HOUR) {
            time -= MILLIS_IN_HOUR;
            iHours++;
        }
        
        int iMinutes = 0;
        while (time >= MILLIS_IN_MINUTE) {
            time -= MILLIS_IN_MINUTE;
            iMinutes++;
        }

        int iSeconds = 0;
        while (time >= MILLIS_IN_SECOND) {
            time -= MILLIS_IN_SECOND;
            iSeconds++;
        }
        
        return new int[] {iHours, iMinutes, iSeconds, (int) time};
    }
    
    /**
     * make a time stamp for filename.
     * @return
     */
    public static String makeTimestamp() {
        final Calendar now = Calendar.getInstance();
        final int year = now.get(Calendar.YEAR);
        final int month = now.get(Calendar.MONTH) + 1;
        final int day = now.get(Calendar.DAY_OF_MONTH);
        final int hours = now.get(Calendar.HOUR_OF_DAY);
        final int minutes = now.get(Calendar.MINUTE);
        final int seconds = now.get(Calendar.SECOND);
        
        final StringBuffer dateTime = new StringBuffer();
        dateTime.append(year);
        dateTime.append(leadingZero(month));
        dateTime.append(leadingZero(day));
        dateTime.append("-");
        dateTime.append(leadingZero(hours));
        dateTime.append(leadingZero(minutes));
        dateTime.append(leadingZero(seconds));
        
        return dateTime.toString();
    }
    
    private static String leadingZero(final int i) {
        return (i < 10 ? "0" : "") + i;
    }

    private static String leadingZero(final float f) {
        return (f < 10 ? "0" : "") + f;
    }
}
