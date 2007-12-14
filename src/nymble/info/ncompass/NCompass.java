package nymble.info.ncompass;

import android.app.Activity;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

public class NCompass extends Activity
{
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        
        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        
        Location target = new Location();
        target.setLatitude(37.447524150941874);
        target.setLongitude(-122.11882744124402);
        
        setContentView(new TargetCompass(this, target, locationManager));
    }
}