package info.nymble.ncompass;

import info.nymble.ncompass.PlaceBook.Places;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.location.Location;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewInflate;
import android.widget.ListAdapter;
import android.widget.TextView;


public class PlaceListAdapter implements ListAdapter
{
    static final String[] columns = new String[]{"_id", "updated", Places.LAT, Places.LON, Places.TITLE, "list_id"};

    Activity activity;
    LocationTracker tracker;
    Cursor cursor;
    ViewInflate inflate;
    
    
    ContentObserver observer;
    
    
    
    public PlaceListAdapter(Activity a, LocationTracker t, long listId)
    {
        Log.w("PlaceListAdapter", "printing list for listId=" + listId);
        this.activity = a;
        this.cursor = a.managedQuery(Places.PLACES_URI, columns, "list_id=" + listId, null);
        this.inflate = a.getViewInflate();
        this.tracker = t;
    }
    
    
    
    
    public void setList(long id)
    {
        this.cursor = activity.managedQuery(Places.PLACES_URI, columns, "list_id=" + id, null);
        
        PlaceBookDB.printCursor(this.cursor, "selected list");
        
        observer.onChange(true);
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
        return cursor.getLong(0);
    }

    public long getItemId(int position)
    {
        cursor.moveTo(position);
        return cursor.getLong(0);
    }

    public int getNewSelectionForKey(int currentSelection, int keyCode, KeyEvent event)
    {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = inflate.inflate(R.layout.place, null, null);

        TextView dateText = (TextView)v.findViewById(R.id.place_date);
        TextView distanceText = (TextView)v.findViewById(R.id.place_distance);
        TextView titleText = (TextView)v.findViewById(R.id.place_title);
        
        cursor.moveTo(position);
                
        dateText.setText(getDate(cursor.getLong(1)));
        distanceText.setText(getDistance(cursor.getString(2), cursor.getString(3)));
        titleText.setText(cursor.getString(4));
        
        return v;
    }
    
    private String getDate(long date)
    {
        SimpleDateFormat f = new SimpleDateFormat();
        Date d = new Date(date);
        
        return f.format(d);
    }
    
    private String getDistance(String lat, String lon)
    {
        Location here = tracker.getCurrentLocation();
        Location l = new Location();
        
        l.setLatitude(Double.parseDouble(lat));
        l.setLongitude(Double.parseDouble(lon));

        double d = l.distanceTo(here);
        
        if (d > 1000)
        {
            return "" + (long)(d/1000) + "Km";
        }
        else
        {
            return "" + (long)(d) + "m";
        }
    }
    
    
    public boolean stableIds()
    {
        return true;
    }

    
    
    
    
    public void registerContentObserver(ContentObserver observer)
    {
        this.observer = observer;
    }

    public void unregisterContentObserver(ContentObserver observer)
    {
    }

    
    public void registerDataSetObserver(DataSetObserver observer)
    {
    }

    
    public void unregisterDataSetObserver(DataSetObserver arg0)
    {
    }
}
