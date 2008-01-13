package info.nymble.measure;

import java.util.LinkedList;

import android.util.Log;

public class Stopwatch
{
	private static Stack stack = new Stack();

    public static void start()
    {
    	stack.push(System.currentTimeMillis());
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
        long elapsed = System.currentTimeMillis() - stack.pop();
        
        Log.i("Stopwatch: " + message, "" + elapsed + "ms");
        return elapsed;
    }
    
    
    public static class Stack
    {
    	LinkedList<Long> list = new LinkedList<Long>();
    	
    	private void push(long l)
    	{
    		list.addFirst(new Long(l));
    	}
    	
    	private long pop()
    	{
    		Long value = list.getFirst();
    		
    		return value == null ? System.currentTimeMillis() : value.longValue();
    	}
    }
}
