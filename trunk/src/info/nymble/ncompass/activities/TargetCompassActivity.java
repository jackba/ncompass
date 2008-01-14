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
        compass.setTarget(target);
        
        tracker = new LocationTracker(this);        
        tracker.registerLocationListener(new LocationListener()
        {
			public void locationChanged(Location newLocation) 
			{
				compass.setLocation(newLocation);
			}
		});
        tracker.start();
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
	}
}