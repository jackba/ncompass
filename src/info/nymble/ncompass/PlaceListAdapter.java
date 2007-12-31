package info.nymble.ncompass;

import java.text.SimpleDateFormat;
import java.util.Date;

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

        TextView dateText = (TextView)v.findViewById(R.id.place_date);
        TextView distanceText = (TextView)v.findViewById(R.id.place_distance);
        TextView titleText = (TextView)v.findViewById(R.id.place_title);
        
        cursor.moveTo(position);
        
        String[] cols = cursor.getColumnNames();
        String print = "";
        for (int i = 0; i < cols.length; i++)
        {
            print += cols[i] + "=" + cursor.getString(i) + ", ";
        }
        Log.w("Place List", print);
        
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

        Log.i("location", "here.lat=" + here.getLatitude() + " here.lon=" + here.getLongitude());
        Log.i("location", "l.lat=" + l.getLatitude() + " l.lon=" + l.getLongitude());            
        
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
