package nymble.info.measure;

import android.util.Log;

public class Stopwatch
{
    private static long time;
    
    public static void start()
    {
        time = System.currentTimeMillis();
    }
    
    public static long stop()
    {
        return stop("");
    }
    
    /**
     * @param message the string title of the log message produced
     * @return time elapsed since start
     */
    public static long stop(String message)
    {
        long elapsed = System.currentTimeMillis() - time;
        
        Log.i("Stopwatch: " + message, "" + elapsed + "ms");
        return elapsed;
    }
}
