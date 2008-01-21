package info.nymble.measure;

import android.util.Log;


/**
 * Utility class for simple event timing. Each instance carries
 * its own internal timer. Start sets the timer to the currentTimeMillis. 
 * Stop returns the current interval elapsed since the last start time. 
 * Also, a log is printed with information regarding the average, weighted
 * average and current elapsed time. 
 * 
 * @author Andrew Evenson
 *
 */
public class T 
{
	private String tag = T.class.getName();
	private String message = "timed event";
	
	private long time = System.currentTimeMillis();
	private long occurences = 0;
	private long totalTime = 0;
	private long weightedAverage = 0;	// houses a logarithmically trailing average a = a/2 + v/2
	
	public T(){}
	
	public T(String message)
	{
		this.message = message;
	}
	
	
	
	
	
	public void start()
	{
		time = System.currentTimeMillis();
	}
	
	
	public long stop()
	{
		long elapsed = System.currentTimeMillis() - time;
		
		totalTime += elapsed;
		occurences++;
		weightedAverage = weightedAverage/2 + elapsed/2;
		Log.i(tag, message + ": this=" + elapsed + "ms" + " wavg=" + weightedAverage + " avg=" + averageTime() + " n=" + occurences);		
		
		return elapsed;
	}
	
	/**
	 * 
	 * @return
	 */
	public long averageTime()
	{
		return (occurences == 0 ? 0 : totalTime/occurences);
	}
	
	
	public long weightedAverage()
	{
		return weightedAverage;
	}
}
