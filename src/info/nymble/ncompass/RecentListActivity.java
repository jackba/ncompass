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

        String[] columns = new String[]{Places.ID, Places.CREATED, Places.LAT, Places.LON, Places.TITLE};
        Cursor c = managedQuery(Places.PLACES_URI, columns, null, null);
        LocationTracker tracker = new LocationTracker(this);
        
        setListAdapter(new PlaceListAdapter(c, getViewInflate(), tracker));
    }   
}
