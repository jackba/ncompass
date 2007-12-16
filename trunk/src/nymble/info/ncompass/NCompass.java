package nymble.info.ncompass;

import nymble.info.measure.Stopwatch;
import android.app.Activity;
import android.location.Location;
import android.os.Bundle;

public class NCompass extends Activity
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