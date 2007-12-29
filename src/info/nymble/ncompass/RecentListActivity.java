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
    
    
    
    
    
    private static class PlaceListAdapter implements ListAdapter
    {
        LocationTracker tracker;
        Cursor cursor;
        ViewInflate inflate;
        
        public PlaceListAdapter(Cursor c, ViewInflate i, LocationTracker t)
        {
            this.cursor = c;
            this.inflate = i;
            this.tracker = t;
        }
        

        
        
        
        public boolean areAllItemsSelectable()
        {
            return true;
        }

        public boolean isSelectable(int arg0)
        {
            return true;
        }

        public int getCount()
        {
            return cursor.count();
        }

        public Object getItem(int position)
        {
            cursor.moveTo(position);
            return cursor.getString(cursor.getColumnIndex(Recent.CREATED));
        }

        public long getItemId(int position)
        {
            cursor.moveTo(position);
            return cursor.getLong(cursor.getColumnIndex(Recent.ID));
        }

        public int getNewSelectionForKey(int currentSelection, int keyCode, KeyEvent event)
        {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            View v = inflate.inflate(R.layout.place, null, null);

            TextView dateText = (TextView)v.findViewById(R.id.date);
            TextView distanceText = (TextView)v.findViewById(R.id.distance);
            
            cursor.moveTo(position);
            
            dateText.setText(getDate(cursor.getLong(1)));
            distanceText.setText(getDistance(cursor.getDouble(2), cursor.getDouble(3)));

            return v;
        }
        
        private String getDate(long date)
        {
            SimpleDateFormat f = new SimpleDateFormat();
            Date d = new Date(date);
            
            return f.format(d);
        }
        
        private String getDistance(double lat, double lon)
        {
            Location here = tracker.getCurrentLocation();
            Location l = new Location();
            
            Log.i("location", "here.lat=" + here.getLatitude() + " here.lon=" + here.getLongitude());
            Log.i("location", "l.lat=" + l.getLatitude() + " l.lon=" + l.getLongitude());            
            
            l.setLatitude(lon);
            l.setLongitude(lat);
            
            return "" + l.distanceTo(here);
        }
        
        

        public void registerContentObserver(ContentObserver observer)
        {
        }

        public void registerDataSetObserver(DataSetObserver observer)
        {
        }

        public boolean stableIds()
        {
            return true;
        }

        public void unregisterContentObserver(ContentObserver observer)
        {
        }

        public void unregisterDataSetObserver(DataSetObserver arg0)
        {
        }
    }
}
