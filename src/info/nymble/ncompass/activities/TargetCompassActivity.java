package info.nymble.ncompass.activities;

import info.nymble.ncompass.LocationListener;
import info.nymble.ncompass.LocationTracker;
import info.nymble.ncompass.R;
import info.nymble.ncompass.view.TargetCompass;
import android.app.Activity;
import android.location.Location;
import android.os.Bundle;

public class TargetCompassActivity extends Activity
{
	LocationTracker tracker;
	TargetCompass compass;
	
	
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        setContentView(R.layout.target_compass);
        
        Location target = new Location();
        target.setLatitude(37.447524150941874);
        target.setLongitude(-122.11882744124402);

        compass = (TargetCompass) findViewById(R.id.compass);  
        testCompass(compass);
        
//        compass.setTarget(target);
//        
//        tracker = new LocationTracker(this);        
//        tracker.registerLocationListener(new LocationListener()
//        {
//			public void locationChanged(Location newLocation) 
//			{
//				compass.setLocation(newLocation);
//			}
//		});
//        tracker.start();
    }

	@Override
	protected void onPause() 
	{
		super.onPause();
//		tracker.stop();
	}

	@Override
	protected void onResume() 
	{
		super.onResume();
//		tracker.start();
	}
	
	
	
	
	private void testCompass(final TargetCompass compass)
	{
		new Thread()
		{
			public void run()
			{
				Location[] l = loadLocationArray(4);
				Location[] t = loadLocationArray(4);
				
				for (int i = 0; i < l.length; i++) {
					l[i].setLatitude(37.447524150941874);
					l[i].setLongitude(-122.11882744124402);
					l[i].setBearing(45);
				}
				
				//west
				t[0].setLatitude(37.447524150941874);
				t[0].setLongitude(-123.11882744124402);
				
				// north
				t[1].setLatitude(38.447524150941874);
				t[1].setLongitude(-122.11882744124402);
				
				// east
				t[2].setLatitude(37.447524150941874);
				t[2].setLongitude(-121.11882744124402);
								
				// south
				t[3].setLatitude(36.447524150941874);
				t[3].setLongitude(-122.11882744124402);
				
				for (int j = 0; j < l.length; j++) {					
					compass.setLocation(l[j]);
					compass.setTarget(t[j]);
					try { Thread.sleep(5000); } catch (Exception e){}
				}
			}
		}.start();
	}
	
	private Location[] loadLocationArray(int items)
	{
		Location[] l = new Location[items];
		
		for (int i = 0; i < l.length; i++) {
			l[i] = new Location();
		}
		
		return l;
	}
}