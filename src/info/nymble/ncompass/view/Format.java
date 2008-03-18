package info.nymble.ncompass.view;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Format
{
    private static final DateFormat timestampDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final DateFormat completeDateFormat = DateFormat.getDateTimeInstance();
    private static final DateFormat todayDateFormat = new SimpleDateFormat("h:mm a");
    private static final DateFormat recentDateFormat = new SimpleDateFormat("EEEE ha");
    private static final DateFormat mediumFormat = new SimpleDateFormat("MMMM d (EE a)");
    private static final DateFormat longDateFormat = new SimpleDateFormat("MMM d, yyyy");
    
    
    private static final Calendar compareCalendar = Calendar.getInstance();
    private static final Calendar targetCalendar = Calendar.getInstance();
    private static Date date = new Date();

    
    private static final NumberFormat numberFormat = NumberFormat.getInstance();
    private static int[] chunkSizes = new int[]{1, 12, 63360};
    private static String[] chunkLabels = new String[]{"in", "ft", "mi"};
    private static double m = 39.37007874015748031496062992126;
    private static double b = 0;
    
    

    
    public static String formatDate(long time)
    {
        date.setTime(time);
        return formatDate(date);
    }
    
    public static String formatDate(Date date)
    {
        targetCalendar.setTimeInMillis(date.getTime());
        long currentTime = System.currentTimeMillis();

        System.out.println(timestampDateFormat.format(date));
        System.out.println(timestampDateFormat.format(compareCalendar.getTime()));

        compareCalendar.setTimeInMillis(currentTime);
        if (compareCalendar.before(targetCalendar))
        {
            return completeDateFormat.format(date);
        }
        
        
        compareCalendar.set(Calendar.HOUR, 12);
        compareCalendar.set(Calendar.MINUTE, 0);
        compareCalendar.set(Calendar.SECOND, 0);
        if (compareCalendar.before(targetCalendar))
        {
            return todayDateFormat.format(date);
        }
        
        
        compareCalendar.add(Calendar.WEEK_OF_MONTH, -1);
        compareCalendar.add(Calendar.DAY_OF_MONTH, 1);
        if (compareCalendar.before(targetCalendar))
        {
            return recentDateFormat.format(date);
        }
        
        compareCalendar.setTimeInMillis(currentTime);
        compareCalendar.add(Calendar.YEAR, -1);
        compareCalendar.set(Calendar.HOUR, 12);
        compareCalendar.set(Calendar.MINUTE, 0);
        compareCalendar.set(Calendar.SECOND, 0);
        compareCalendar.add(Calendar.MONTH, 1);
        compareCalendar.set(Calendar.DAY_OF_MONTH, 1);
        if (compareCalendar.before(targetCalendar))
        {
            return mediumFormat.format(date);
        }
        

        return longDateFormat.format(date);
    }
    
//    public static void main(String[] a) throws ParseException
//    {
//        String text = "2007-01-15 23:11:11";
//        Date date = timestampDateFormat.parse(text);
//        
//        text = formatDate(date);
//        System.out.println(text);
//    }
    
    
    
    public static String formatSpeed(double metersPerSecond)
    {
        StringBuffer buffer = new StringBuffer();

        double inches = (m*metersPerSecond + b);
        int chunk = chunkSizes.length - 1;
        double value = (inches/chunkSizes[chunk])/3600;
        
        roundNumber(value, buffer);
        buffer.append(chunkLabels[chunk] + "/hr");
        return buffer.toString();
    }
    
    
    public static String formatDistance(double meters)
    {
        StringBuffer buffer = new StringBuffer();

        double inches = (m*meters + b);
        int chunk = findChunk(inches);
        double value = inches/chunkSizes[chunk];
        
        roundNumber(value, buffer);
        buffer.append(chunkLabels[chunk]);
        return buffer.toString();
    }

    
    private static int findChunk(double inches)
    {
        for (int i = chunkSizes.length - 1; i >= 0; i--)
        {
            if (chunkSizes[i] <= inches) 
            {
                return i;
            }
        }
        return 0;
    }
    
    
    public static String roundNumber(double number)
    {
        StringBuffer buffer = new StringBuffer();

        roundNumber(number, buffer);
        return buffer.toString();
    }
    
    private static void roundNumber(double number, StringBuffer buffer)
    {
        if (number > 10)
        {
            numberFormat.setMaximumFractionDigits(0);
        }
        else if (number > 1)
        {
            numberFormat.setMaximumFractionDigits(1);
        }
        else
        {
            numberFormat.setMaximumFractionDigits(2);
        }
        
        numberFormat.format(number, buffer, null);
    }
}
