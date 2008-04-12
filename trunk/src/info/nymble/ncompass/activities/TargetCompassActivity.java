package info.nymble.ncompass.activities;

import info.nymble.ncompass.LocationListener;
import info.nymble.ncompass.LocationTracker;
import info.nymble.ncompass.R;
import info.nymble.ncompass.view.AudioStatus;
import info.nymble.ncompass.view.Format;
import info.nymble.ncompass.view.TargetCompass;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.TextView;

public class TargetCompassActivity extends Activity
{
	private final int SELECT_COLOR = 1;
	
	private final Handler handler = new Handler();
	
	private LocationListener listener;
	private LocationTracker tracker;
	private TargetCompass compass;
	
	private TextView title;
	private TextView distance;
	private TextView bearing;
	private TextView target;
	private TextView speed;
	private TextView eta;

	
	SharedPreferences preferences;
	private int color;
	
    @Override
    public void onCreate(Bundle icicle)
    {
		super.onCreate(icicle);
		setContentView(R.layout.target_compass);
		
		long time = logTime(System.currentTimeMillis(), "start");
		preferences = this.getPreferences(0);	time = logTime(time, "1");
		compass = (TargetCompass) findViewById(R.id.compass);  time = logTime(time, "2");
		
		title = (TextView) findViewById(R.id.place_title);        
		distance = (TextView) findViewById(R.id.place_distance);
		bearing = (TextView) findViewById(R.id.place_bearing);
		target = (TextView) findViewById(R.id.place_target);
		speed = (TextView) findViewById(R.id.place_speed);
		eta = (TextView) findViewById(R.id.place_eta);
		
		
		
		setColor();	time = logTime(time, "3");
		setNeedle();	time = logTime(time, "4");
		loadLocationInfo();	time = logTime(time, "6");
		
//        testCompass(compass);
		
		
		handler.post( new Runnable(){
			public void run()
			{        		
				setupTracking();
			}
		});
    }
    
    
    private void setupTracking()
    {
		tracker = new LocationTracker(this);   
		setTarget(getIntent());	
		setupLocationTracking();
    }
    
    
    
	private long logTime(long startTime, String m)
	{
		long endTime = System.currentTimeMillis();
		
		Log.w("Log Time", m + " elapsed=" + (endTime - startTime));
		
		return endTime;
	}

    

    private void setColor()
    {
    	try
    	{    		
    		setColor(preferences.getInt("color", 0xFFF99F00), false);
    	}
    	catch (Exception e)
    	{
    		Log.w(null, "error occured while setting color " + e.getMessage());
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
    	this.color = color;
        compass.setColor(color);
        title.setTextColor(color);
        distance.setTextColor(color);
        bearing.setTextColor(color);
        target.setTextColor(color);
        speed.setTextColor(color);
        eta.setTextColor(color);
    }
    
    
    private void setNeedle()
    {
    	try
    	{    		
    		setNeedle(preferences.getInt("needle", TargetCompass.BLACK), false);
    	}
    	catch (Exception e)
    	{
    		Log.w(null, "error occured while setting needle " + e.getMessage());
    	}
    }
    
    
    private void setNeedle(int needleId, boolean save)
    {
    	if (save)
    	{
    		SharedPreferences.Editor editor = preferences.edit();
    		editor.putInt("needle", needleId);
    	    editor.commit();    		
    	}
        compass.setNeedle(needleId);
    }
    
    
    
    
    
    
    private void stashLocationInfo()
    {
    	Location location = compass.getLocation();
    	Location target = compass.getTarget();
    	
   		SharedPreferences.Editor editor = preferences.edit();
   		
   		if (location != null)
   		{   			
   			editor.putString("location.latitude", "" + location.getLatitude());
   			editor.putString("location.longitude", "" + location.getLongitude());
   			editor.putString("location.altitude", "" + location.getAltitude());
   			editor.putFloat("location.bearing", location.getBearing());
   		}
   		
   		if (target != null)
   		{   			
   			editor.putString("target.latitude", "" + target.getLatitude());
   			editor.putString("target.longitude", "" + target.getLongitude());
   			editor.putString("target.altitude", "" + target.getAltitude());
   		}
   		
   		editor.commit();    		
    }
    
    private void loadLocationInfo()
    {   
    	Location location = new Location();
    	
   		location.setLatitude( Double.parseDouble( preferences.getString("location.latitude", "0") ) );
   		location.setLongitude( Double.parseDouble( preferences.getString("location.longitude", "0") ) );
   		location.setAltitude( Double.parseDouble( preferences.getString("location.altitude", "0") ) );
   		location.setBearing( preferences.getFloat("location.bearing", 0.0F) );
   		
   		Location target = new Location();
    	
   		target.setLatitude( Double.parseDouble( preferences.getString("target.latitude", "0") ) );
   		target.setLongitude( Double.parseDouble( preferences.getString("target.longitude", "0") ) );
   		target.setAltitude( Double.parseDouble( preferences.getString("target.altitude", "0") ) );
   		
   		if (location.getLatitude() != 0 || location.getLongitude() != 0 || location.getBearing() != 0)
   			setLocation(location);
   		
   		if ((target.getLatitude() != 0 || target.getLongitude() != 0 || target.getBearing() != 0))
   			setTarget(target);
    }
    
    
    
    
    
    
    
    
    
    
    

	@Override
	protected void onPause() 
	{
		super.onPause();
		stashLocationInfo();
		if (tracker != null) tracker.stop();
	}

	@Override
	protected void onResume() 
	{
		super.onResume();
		if (tracker != null) tracker.start();
		Log.w(null, "Starting again resume");
	}
	
	
	@Override
	protected void onStart() 
	{
		super.onResume();
		if (tracker != null) tracker.start();
		Log.w(null, "Starting again start ");
	}	
	
	
	
	
	@Override
	protected void onDestroy() {
		if (tracker != null){
			tracker.unregisterLocationListener(listener);
		}
		super.onDestroy();
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
        
        menu.add(2, 1, "Silver Needle", new Runnable()
        {
            public void run()
            {
                setNeedle(TargetCompass.SILVER, true);
            }
        }
        );
        
        
        menu.add(3, 1, "White Needle", new Runnable()
        {
            public void run()
            {
            	setNeedle(TargetCompass.WHITE, true);
            }
        }
        );
        
        
        menu.add(4, 1, "Black Needle", new Runnable()
        {
            public void run()
            {
            	setNeedle(TargetCompass.BLACK, true);
            }
        }
        );
        
        
        menu.add(1, 2, "Set Color", new Runnable()
        {
            public void run()
            {
            	
            	
            	Intent i = new Intent(context, InputFieldActivity.class);
                
            	i.putExtra(InputFieldActivity.TITLE, "Select Compass Color");
            	i.putExtra(InputFieldActivity.LABEL, "RGB color value");
            	i.putExtra(InputFieldActivity.DEFAULT, Integer.toHexString((int)(Math.random()*0xFFFFFF)));

            	startSubActivity(i, SELECT_COLOR);
            }
        }
        );
        
        
        
        menu.add(2, 2, "Power Saver", new Runnable()
        {
            public void run()
            {
            	compass.setPowerSaver(!compass.getPowerSaver());
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

	
	private static int parseHex(String hex, int dfault)
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
			final double b = -l.getBearing() % 360;
			final double a = (b + l.bearingTo(t)) % 360;
			
			handler.post(new Runnable(){
				public void run()
				{
					distance.setText(Format.formatDistance(d));
					target.setText("T=" + getDirections(a));
					bearing.setText("N=" + getDirections(b));
					speed.setText(Format.formatSpeed(s));
					eta.setText("eta " + Format.formatTime((int)(d/s)));
				}
			});
		}
	}
	

	
	private String getDirections(double angle)
	{
		int a = (int)angle;
		if (angle < 0) a += 360;
		
		return "" + a + "°";
//		if (a > 180)
//		{
//			return "L" + (360-a) + "°";
//		}
//		else
//		{
//			return "R" + a + "°";
//		}
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
//			long listId = Lists.get(getContentResolver(), "favorites");
//			Places.add(getContentResolver(), t, listId);
		}
	}
	
	
	
	
	private void setLocation(Location l)
	{
		compass.setLocation(l);
		updateDistance();
	}
	
	
	private void setupLocationTracking()
	{    
		listener = new LocationListener()
        {
			public void locationChanged(Location newLocation) 
			{
				setLocation(newLocation);
			}
		};
		
        tracker.registerLocationListener(listener);
        tracker.start();
	}
	
	
	

	
	
	
	
	
	


	private void readOrientation()
	{
		Location l = compass.getLocation();
		Location t = compass.getTarget();
		
		if (l != null && t != null)
		{
			ArrayList<Integer> list = new ArrayList<Integer>();
			double b = -l.getBearing() % 360;
			double a = (b + l.bearingTo(t)) % 360;
			int c = (int)a;
			int d = (int)b;
			
			if (a < 0) c += 360;
			if (b < 0) d += 360;
			
			
			list.add(R.raw.target_is);
			AudioStatus.readNumber(c > 180 ?  360-c : c, list);
			list.add(R.raw.degrees);
			list.add(c > 180 ?  R.raw.left: R.raw.right);
			
			
			list.add(R.raw.north_is);
			AudioStatus.readNumber(d > 180 ?  360-d : d, list);
			list.add(R.raw.degrees);
			list.add(d > 180 ?  R.raw.left: R.raw.right);
			
			
			new AudioStatus.MultifileAudio(AudioStatus.buildMediaArray(list, this)).play();
		}
	}
	
	

	
	
	
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.w("KeyDown", "keyCode=" + keyCode);
		
		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
			compass.press(false);
		}
		
		return super.onKeyDown(keyCode, event);
	}



	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Log.w("KeyUp", "keyCode=" + keyCode);
		
		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
			readOrientation();
		}
		else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
			compass.unpress();
		}
		
		return super.onKeyUp(keyCode, event);
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