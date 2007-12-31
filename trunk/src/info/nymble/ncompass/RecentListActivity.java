package info.nymble.ncompass;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.ListActivity;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewInflate;
import android.widget.ListAdapter;
import android.widget.TextView;

public class RecentListActivity extends ListActivity
{
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setDefaultKeyMode(SHORTCUT_DEFAULT_KEYS);

        String[] columns = new String[]{Recent.ID, Recent.CREATED, Recent.LAT, Recent.LON};
        Cursor c = managedQuery(Recent.CONTENT_URI, columns, null, null);
        LocationTracker tracker = new LocationTracker(this);
        
        setListAdapter(new PlaceListAdapter(c, getViewInflate(), tracker));
    }
    
    
    
    
    
}
