package info.nymble.ncompass.activities;

import info.nymble.ncompass.LocationListener;
import info.nymble.ncompass.LocationTracker;
import info.nymble.ncompass.PlaceBook;
import info.nymble.ncompass.R;
import info.nymble.ncompass.PlaceBook.Places;
import info.nymble.ncompass.view.AudioStatus;
import info.nymble.ncompass.view.Format;
import info.nymble.ncompass.view.TargetCompass;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Rect;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.widget.TextView;

public class TargetCompassActivity extends Activity
{
	private final int SELECT_COLOR = 1;
	private final int TITLE_TARGET = 2;
	private final int DISPLAY_SETTINGS = 3;
	
	
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

	private SharedPreferences preferences;
	private long listId;
	
	
	
	private Rect compassCoords = null;
	private Rect titleCoords = null;
	private boolean compassDown = false;
	private boolean titleDown = false;
	
    
    /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++
     * Lifetime Event Handlers
     +++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

    @Override
    public void onCreate(Bundle icicle)
    {
		super.onCreate(icicle);
		setContentView(R.layout.target_compass);
		
		preferences = this.getPreferences(0);
		compass = (TargetCompass) findViewById(R.id.compass);
		title = (TextView) findViewById(R.id.place_title);        
		distance = (TextView) findViewById(R.id.place_distance);
		bearing = (TextView) findViewById(R.id.place_bearing);
		target = (TextView) findViewById(R.id.place_target);
		speed = (TextView) findViewById(R.id.place_speed);
		eta = (TextView) findViewById(R.id.place_eta);
		listId = PlaceBook.Lists.get(getContentResolver(), "targeted");
		
		setColor();
		setNeedle();
		loadLocationInfo();
		setPowerSaver();

		handler.post( new Runnable(){
			public void run()
			{
				setupTracking();
			}
		});
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
	

	
	
	
    
    
    
    /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++
     * Event Handlers
     +++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
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
			targetHere();
		}
		
		return super.onKeyUp(keyCode, event);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (compassCoords == null){
			compassCoords = new Rect();
			compass.getGlobalVisibleRect(compassCoords);
			
			titleCoords = new Rect();
			title.getGlobalVisibleRect(titleCoords);
		}
		int x = (int)event.getX();
		int y = (int)event.getY();
		
		if (MotionEvent.ACTION_DOWN == event.getAction())
		{
			if (compassCoords.contains(x, y))
			{				
				compass.press(false);
				compassDown = true;
			}
			else if (titleCoords.contains(x, y))
			{
				title.setBackgroundColor(title.getCurrentTextColor() & 0x8FFFFFFF);
				titleDown = true;
			}
		}
		else if (MotionEvent.ACTION_UP == event.getAction())
		{
			if (compassDown)
			{				
				compassDown = false;
				compass.unpress();
				targetHere();
			}
			else if (titleDown)
			{
				titleDown = false;
				title.setBackgroundColor(0x00000000);
				targetTitle();
			}
		}
		else if (compassDown && !compassCoords.contains(x, y))
		{
			compass.unpress();
			compassDown = false;
		}
		else if (titleDown && !titleCoords.contains(x, y))
		{
			title.setBackgroundColor(0x00000000);
			titleDown = false;
		}

		return super.onTouchEvent(event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, String data, Bundle extras) 
	{
		super.onActivityResult(requestCode, resultCode, data, extras);
		
		if (resultCode == Activity.RESULT_OK)
		{
			switch (requestCode)
			{
			case SELECT_COLOR:
				setColor(data, true);
				break;
			case TITLE_TARGET:
				logTargetTitle(data);
				break;
			case DISPLAY_SETTINGS:
				setColor(extras.getString(DisplaySettingsActivity.COMPASS_COLOR), true);
				int display = extras.getInt(DisplaySettingsActivity.DISPLAY_MODE);
				
				if (display == -1)
				{
					setPowerSaver(true, true);
				}
				else
				{
					setPowerSaver(false, true);
					setNeedle(display, true);
				}
				break;
			default:
				Log.i(null, "Requested something, but not sure what it was requestCode=" + requestCode);
			}
		}
		else
		{
			Log.i(null, "cancelled request");
		}
	}

	
	@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
		final Context context = this;
		
        menu.add(1, 1, "Set Target Title", new Runnable()
        {
            public void run()
            {
                targetTitle();
            }
        }
        );
        
        menu.add(1, 2, "Target Current Location", new Runnable()
        {
            public void run()
            {
                targetHere();
            }
        }
        );
        
        
        
        
        
        menu.add(2, 1, "Send Target", new Runnable()
        {
            public void run()
            {
         		Location t = compass.getTarget();
        		String uri = "geo:" + t.getLatitude() + "," + t.getLongitude();
        		Intent i = new Intent(context, SendLocationActivity.class);
        	
        		i.putExtra(SendLocationActivity.PARAM_ADDRESS, uri);
        		
        		Log.i(null, "loading map at uri=" + uri);
        		startActivity(i);
            }
        }
        );
        
        
        menu.add(2, 1, "Map Target", new Runnable()
        {
            public void run()
            {
            	Location t = compass.getTarget();
        		Uri uri = Uri.parse("geo:" + t.getLatitude() + "," + t.getLongitude());
        		Intent i = new Intent(Intent.VIEW_ACTION, uri);
        	
        		Log.i(null, "loading location of uri=" + uri);
        		startActivity(i);
            }
        }
        );
        
        
        menu.add(2, 3, "Show Target List", new Runnable()
        {
            public void run()
            {
            	Intent i = new Intent(context, PlaceListActivity.class);

            	i.putExtra("List", listId);

            	startActivity(i);
            }
        }
        );
        


        menu.add(3, 1, "Display Settings", new Runnable()
        {
            public void run()
            {
            	Intent i = new Intent(context, DisplaySettingsActivity.class);
                int display = (getPowerSaver() ? -1 : getNeedle()); 

            	i.putExtra(DisplaySettingsActivity.DISPLAY_MODE, display);
            	i.putExtra(DisplaySettingsActivity.COMPASS_COLOR, compass.getColor());

            	startSubActivity(i, DISPLAY_SETTINGS);
            }
        }
        );
        
        return true;
    }
	
	
	


    
    
    
    /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++
     * Set display settings and persist to preferences
     +++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
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
    
    
    private void setColor(String c, boolean save)
    {
    	int color = parseHex(c, 0);
		if ((color & 0xFF000000) == 0) color |= 0xFF000000;
		if (color != 0) setColor(color, true);
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
        bearing.setTextColor(color);
        target.setTextColor(color);
        speed.setTextColor(color);
        eta.setTextColor(color);
    }
    
    private int getNeedle()
    {
    	return preferences.getInt("needle", TargetCompass.BLACK);
    }
    
    private void setNeedle()
    {
    	try
    	{    		
    		setNeedle(getNeedle(), false);
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
   			editor.putString("target.title", "" + title.getText());
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
   			setTarget(target, preferences.getString("target.title", ""));

    }
    
    
    private void setPowerSaver()
    {
    	try
    	{    		
    		setPowerSaver(getPowerSaver(), false);
    	}
    	catch (Exception e)
    	{
    		Log.w(null, "error occured while setting powersaver " + e.getMessage());
    	}
    }
    
    private void setPowerSaver(boolean on, boolean save)
    {
    	if (save)
    	{
    		SharedPreferences.Editor editor = preferences.edit();
    		editor.putBoolean("powersaver", on);
    	    editor.commit();    		
    	}
        compass.setPowerSaver(on);
    }
    
    private boolean getPowerSaver()
    {
    	return preferences.getBoolean("powersaver", false);
    }
    
    
    
    
    
    
    


	
	
	
	


	

    /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++
     * Hidden Functionality
     +++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
    
    /**
     * Updates all the display elements (compass and text views)
     * to display the target and location.
     */
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
					target.setText("T=" + Format.formatAngle(a));
					bearing.setText("N=" + Format.formatAngle(b));
					speed.setText(Format.formatSpeed(s));
					eta.setText("eta " + Format.formatTime((int)(d/s)));
				}
			});
		}
	}

	
	/**
	 * Uses an intent to set the target. If no
	 * target is supplied on the intent, the
	 * current location is used as a target.
	 * 
	 * @param intent
	 */
    private void setTarget(Intent intent)
    {
    	if (intent != null)
    	{
    		Location target = (Location)intent.getExtra("Location");
    		String titleText = intent.getStringExtra("Title");
    		
    		if (target != null)
    		{    			
    			setTarget(target, titleText);
    		}
    	}
    }
    
    
    private void setAndLogTarget(Location t, String titleText)
    {
    	setTarget(t, titleText);
    	ContentResolver resolver = this.getContentResolver();    	
    	PlaceBook.Places.add(resolver, t, listId);
    }
    
    private void logTargetTitle(String titleText)
    {
    	Location t = compass.getTarget();
    	setTarget(t, titleText);
    	ContentResolver resolver = this.getContentResolver();    	
    	Uri place = PlaceBook.Places.add(resolver, t, listId);
    	long placeId = Long.parseLong(place.getLastPathSegment());
    	PlaceBook.Places.update(resolver, placeId, titleText, null, null);
    }
    
    
    
    
    
    
    /**
     * Updates the compass to point at a new target
     * @param t the location to point at
     */
	private void setTarget(Location t, String titleText)
	{
		compass.setTarget(t);
		
		if (t == null)
		{
			title.setText("<no target selected>");
		}
		if (titleText == null)
		{
			title.setText("<untitled target>");
		}
		else 
		{
			title.setText(titleText);
		}
		
		updateDistance();
	}
	
	
	private void targetHere()
	{
		if (tracker != null) setAndLogTarget(tracker.getCurrentLocation(), null);
	}
	
	
	private void targetTitle()
	{
		Intent i = new Intent(this, InputFieldActivity.class);

    	i.putExtra(InputFieldActivity.TITLE, "Title Your Target");
    	i.putExtra(InputFieldActivity.LABEL, "Provide a title for your new target location");

    	startSubActivity(i, TITLE_TARGET);
	}
	
	
	
	
	
	/**
	 * Updates the activity to a new location.
	 * @param l the location to use as current
	 */
	private void setLocation(Location l)
	{
		compass.setLocation(l);
		updateDistance();
	}
	
	
    /**
     * Background routine for loading location information
     * May take several seconds to accomplish. 
     */
    private void setupTracking()
    {
		tracker = new LocationTracker(this);   
		setTarget(getIntent());	
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
	
    
    /**
     * Reads a String representation of a hex number into a 
     * int value
     * @param hex the string representation of a hex number
     * @param dfault the default value to return, if hex 
     * cannot be interpreted 
     * @return an int representing hex if possible, otherwise dfault
     */
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
	

	/**
	 * Plays an audio stream that tells the user to current
	 * NSEW bearing and bearing to target.
	 */
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
}