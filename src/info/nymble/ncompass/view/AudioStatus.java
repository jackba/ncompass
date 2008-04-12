package info.nymble.ncompass.view;

import info.nymble.ncompass.R;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

public class AudioStatus {
	static HashMap<Integer, MediaPlayer> players = new HashMap<Integer, MediaPlayer>();
	static int[] numbers = new int[]{R.raw.zero, R.raw.one, R.raw.two, R.raw.three, R.raw.four, 
									R.raw.five, R.raw.six, R.raw.seven, R.raw.eight, R.raw.nine};
	
	
	
	
	
	public synchronized static MediaPlayer[] buildMediaArray(List<Integer> resources, Context context)
	{
		MediaPlayer[] mp = new MediaPlayer[resources.size()];
		
		int i = 0;
		for (Iterator<Integer> iterator = resources.iterator(); iterator.hasNext();i++) {
			int key = iterator.next();
			mp[i] = MediaPlayer.create(context, key);
			
//			if (players.containsKey(key))
//			{
//				mp[i] = players.get(key);
//			}
//			else
//			{				
//				players.put(key, mp[i]);
//			}
		}
		
		return mp;
	}
	
	/**
	 * Reads a series of resource ids on to the end of 'resources' which represent
	 * the sequence of sounds needed to say the supplied number
	 * @param number
	 * @param resources
	 */
	public static void readNumber(double number, List<Integer> resources)
	{
		LinkedList<Integer> list = new LinkedList<Integer>();
		int i = (int)Math.abs(number);	// can only read positive, non decimal numbers
		
		while (i > 0)
		{
			int r = i %10;
			
			i = i / 10;
			list.addFirst(numbers[r]);
		}

		while (list.size() > 0) {
			resources.add( list.removeFirst() );
		}
	}
	
	
	
	public static class MultifileAudio implements OnCompletionListener
	{
		private MediaPlayer[] files;
		private int position;
		
		public MultifileAudio(MediaPlayer[] files)
		{
			this.files = files;
			this.position = 0;
		}
		
		
		public void play()
		{
			try
			{				
				if (position < files.length)
				{
					Log.w("File Position", "i=" + position + " p=" + files[position].getCurrentPosition());
					files[position].setOnCompletionListener(this);
					files[position].start();
				}
			}
			catch (Exception e)
			{
				Log.e("Multifile", "Bad News " + e.getMessage());
			}
		}
		
		
		public void onCompletion(MediaPlayer mp)
		{
			try
			{
				mp.stop();
				mp.release();
				position++;
				play();
			}
			catch (Exception e)
			{
				Log.e("Multifile", "Really Bad News " + e.getMessage());
			}
		}
	}
}
