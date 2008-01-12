package info.nymble.ncompass.activities;

import info.nymble.measure.Stopwatch;
import info.nymble.ncompass.view.TargetCompass;
import android.app.Activity;
import android.location.Location;
import android.os.Bundle;

public class TargetCompassActivity extends Activity
{
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        
        Location target = new Location();
        target.setLatitude(37.447524150941874);
        target.setLongitude(-122.11882744124402);

        Stopwatch.start();
        setContentView(new TargetCompass(this, target));
        Stopwatch.stop("load content pane");
    }
}