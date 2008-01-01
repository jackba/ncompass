package info.nymble.ncompass;

import info.nymble.ncompass.PlaceBook.Places;
import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;

public class RecentListActivity extends ListActivity
{
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setDefaultKeyMode(SHORTCUT_DEFAULT_KEYS);

        LocationTracker tracker = new LocationTracker(this);
        setListAdapter(new PlaceListAdapter(this, tracker, 1));
    }   
}
