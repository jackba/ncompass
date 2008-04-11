package info.nymble.ncompass.activities;

import java.util.prefs.Preferences;

import info.nymble.ncompass.LocationListener;
import info.nymble.ncompass.LocationTracker;
import info.nymble.ncompass.R;
import info.nymble.ncompass.PlaceBook.Lists;
import info.nymble.ncompass.PlaceBook.Places;
import info.nymble.ncompass.view.Format;
import info.nymble.ncompass.view.TargetCompass;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class TargetCompassActivity extends Activity
{
	private final int SELECT_COLOR = 1;
	
	private final Handler handler = new Handler();
	
	private LocationTracker tracker;
	private TargetCompass compass;
	
	private TextView title;
	private TextView distance;
	private TextView speed;
	private TextView eta;


	SharedPreferences preferences;
	
	
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        setContentView(R.layout.target_compass);

        preferences = this.getPreferences(0);
        tracker = new LocationTracker(this);   
        compass = (TargetCompass) findViewById(R.id.compass);  

        title = (TextView) findViewById(R.id.place_title);        
        distance = (TextView) findViewById(R.id.place_distance);
        speed = (TextView) findViewById(R.id.place_speed);
        eta = (TextView) findViewById(R.id.place_eta);
        
        setColor();
        setTarget(getIntent());

//        testCompass(compass);
        setupLocationTracking();
    }
    

    

    private void setColor()
    {
    	try
    	{    		
    		setColor(preferences.getInt("color", 0xFF066bc9), false);
    	}
    	catch (Exception e)
    	{
    		Log.w(null, "error occured while setting color " + e.getMessage());
    		e.printStackTrace();
    	}
    }
    
    
    private void setColor(int color, boolean save)
    {
    	if (save)
    	{
    		SharedPreferences.Editor editor = preferences.edit();
    		editor.putInt("color", color);
    	    editor.commit();    		
    	}
        compass.setColor(color);
        title.setTextColor(color);
        distance.setTextColor(color);
        speed.setTextColor(color);
        eta.setTextColor(color);
    }
    
    
    
    
    
    
    

	@Override
	protected void onPause() 
	{
		super.onPause();
		tracker.stop();
	}

	@Override
	protected void onResume() 
	{
		super.onResume();
		tracker.start();
		Log.w(null, "Starting again resume");
	}
	
	
	@Override
	protected void onStart() 
	{
		super.onResume();
		tracker.start();
		Log.w(null, "Starting again start ");
	}	
	
	
	
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
		final Context context = this;
		
        menu.add(1, 1, "Target Here", new Runnable()
        {
            public void run()
            {
                setTarget(tracker.getCurrentLocation());
            }
        }
        );
        
        menu.add(2, 1, "Lime", new Runnable()
        {
            public void run()
            {
                setColor(0xffcff377, true);
            }
        }
        );
        
        
        menu.add(3, 1, "Orange", new Runnable()
        {
            public void run()
            {
                setColor(0xfff99f00, true);
            }
        }
        );
        
        
        menu.add(4, 1, "Blue", new Runnable()
        {
            public void run()
            {
                setColor(0xff066bc9, true);
            }
        }
        );
        
        
        
        menu.add(5, 1, "Green", new Runnable()
        {
            public void run()
            {
                setColor(0xff3e8740, true);
            }
        }
        );
        
        
        menu.add(6, 1, "Pearl", new Runnable()
        {
            public void run()
            {
                setColor(0xffecece1, true);
            }
        }
        );
        
        
        
        menu.add(1, 2, "Set Color", new Runnable()
        {
            public void run()
            {
            	Intent i = new Intent(context, InputFieldActivity.class);
                startSubActivity(i, SELECT_COLOR);
            }
        }
        );
        
        
        
        return true;
    }
	
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, String data, Bundle extras) 
	{
		super.onActivityResult(requestCode, resultCode, data, extras);
		
		switch (requestCode)
		{
		case SELECT_COLOR:
			if (resultCode == Activity.RESULT_OK)
			{
				int color = parseHex(data, 0);
				if ((color & 0xFF000000) == 0) color |= 0xFF000000;
				if (color != 0) setColor(color, true);
			}
			else
			{
				Log.i(null, "cancelled request");
			}
			break;
		default:
			Log.i(null, "Requested something, but not sure what it was requestCode=" + requestCode);
		}
	}

	
	public static int parseHex(String hex, int dfault)
	{
		if (hex.startsWith("0x")) hex = hex.substring(2);
		
		try
		{
			long i = Long.parseLong(hex, 16);
			return (int)i;
		}
		catch (NumberFormatException e)
		{
			return dfault;
		}
	}
	



	private void updateDistance()
	{
		Location l = compass.getLocation();
		Location t = compass.getTarget();
		
		if (l != null && t != null)
		{
			final float d = l.distanceTo(t);
			final float s = l.getSpeed();
			
			handler.post(new Runnable(){
				public void run()
				{
					distance.setText(Format.formatDistance(d));
					speed.setText(Format.formatSpeed(s));
					eta.setText("eta " + Format.formatTime((int)(d/s)));
				}
			});
		}
	}
	
	
    private void setTarget(Intent intent)
    {
    	if (intent != null)
    	{
    		Location t = (Location)intent.getExtra("Location");
    		if (t != null)
    		{    			
    			setTarget(t);
    		}
    		else
    		{    			
    			setTarget(tracker.getCurrentLocation());
    		}
    	}
    }
    
	private void setTarget(Location t)
	{
		compass.setTarget(t);
		updateDistance();
		if (t != null)
		{
			long listId = Lists.get(getContentResolver(), "favorites");
			Places.add(getContentResolver(), t, listId);
		}
	}
	
	
	
	
	private void setLocation(Location l)
	{
		compass.setLocation(l);
		updateDistance();
	}
	
	
	private void setupLocationTracking()
	{    
        tracker.registerLocationListener(new LocationListener()
        {
			public void locationChanged(Location newLocation) 
			{
				setLocation(newLocation);
			}
		});
        tracker.start();
	}
	
	
	

	
	
	
	
	
//	private static class Images
//	{
//		private static int i = 0;
//		private static int[] ids = new int[]{R.drawable.gallery_photo_1, 
//				R.drawable.gallery_photo_2, 
//				R.drawable.gallery_photo_3, 
//				R.drawable.gallery_photo_4, 
//				R.drawable.gallery_photo_5, 
//				R.drawable.gallery_photo_6};
//		
//		private static int getNext()
//		{
//			i = (i + 1) % 6;
//			Log.i(null, "changing image to " + ids[i]);
//			return ids[i];
//		}
//	}
	
//	
//	private void testCompass(final TargetCompass compass)
//	{
//		new Thread()
//		{
//			public void run()
//			{
//				Location[] l = loadLocationArray(4);
//				Location[] t = loadLocationArray(4);
//				
//				for (int i = 0; i < l.length; i++) {
//					l[i].setLatitude(37.447524150941874);
//					l[i].setLongitude(-122.11882744124402);
//					l[i].setBearing(i*-90);
//				}
//				
//				//west
//				t[0].setLatitude(37.447524150941874);
//				t[0].setLongitude(-123.11882744124402);
//				
//				// north
//				t[1].setLatitude(38.447524150941874);
//				t[1].setLongitude(-122.11882744124402);
//				
//				// east
//				t[2].setLatitude(37.447524150941874);
//				t[2].setLongitude(-121.11882744124402);
//								
//				// south
//				t[3].setLatitude(36.447524150941874);
//				t[3].setLongitude(-122.11882744124402);
//				
//				for (int j = 0; j < l.length; j++) {					
//					setTarget(t[j]);
//					try { Thread.sleep(500); } catch (Exception e){}
//					setLocation(l[j]);
//					try { Thread.sleep(4000); } catch (Exception e){}
//				}
//			}
//		}.start();
//	}
//	
//	private Location[] loadLocationArray(int items)
//	{
//		Location[] l = new Location[items];
//		
//		for (int i = 0; i < l.length; i++) {
//			l[i] = new Location();
//		}
//		
//		return l;
//	}
}