package info.nymble.ncompass;

import android.app.Activity;
import android.os.Bundle;

public class PlaceActivity extends Activity
{
    @Override
    protected void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        setContentView(R.layout.recent_location_entry);
    }
}
