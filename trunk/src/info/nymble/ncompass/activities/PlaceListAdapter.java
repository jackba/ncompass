package info.nymble.ncompass.activities;

import info.nymble.measure.Stopwatch;
import info.nymble.ncompass.LocationTracker;
import info.nymble.ncompass.R;
import info.nymble.ncompass.PlaceBook.Places;
import info.nymble.ncompass.view.Format;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.location.Location;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewInflate;
import android.widget.ListAdapter;
import android.widget.TextView;


public class PlaceListAdapter extends ObserverManager implements ListAdapter
{
    static final String[] columns = new String[]{"_id", Places.LAT, Places.LON, Places.ALT, "updated", Places.TITLE, "list_id"};


    ArrayList<Place> places = new ArrayList<Place>();
    
    Activity activity;
    LocationTracker tracker;
    ViewInflate inflate;
    int listId;
    

    
    public PlaceListAdapter(Activity a, LocationTracker t)
    {
        this.activity = a;
        this.inflate = a.getViewInflate();
        this.tracker = t;
    }
    
    
    
    
    public void setList(final long id)
    {
        Cursor c = activity.managedQuery(Places.PLACES_URI, columns, "list_id=" + id, null);
        
        places.clear();
        loadDataFromCursor(c, places);
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
        return places.size();
    }

    public Object getItem(int position)
    {
        return getItemId(position); //places.get(position);
    }

    public long getItemId(int position)
    {
        return places.get(position).id;
    }

    public int getNewSelectionForKey(int currentSelection, int keyCode, KeyEvent event)
    {
        Log.w(null, "getNewSelectionForKey:  currentSelection=" + currentSelection + " keyCode=" + keyCode);
        return currentSelection;
    }

    public boolean stableIds()
    {
        return true;
    }
    
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Log.w(null, "getView:  position=" + position);
        if (convertView == null )
        {
        	Place p = places.get(position);
        	
        	if (p.view == null)
        	{        		
        		Log.w(null, "rebuilding view");
        		convertView = inflate.inflate(R.layout.place, null, null);
        		
        		TextView dateText = (TextView)convertView.findViewById(R.id.place_date);
        		TextView distanceText = (TextView)convertView.findViewById(R.id.place_distance);
        		TextView titleText = (TextView)convertView.findViewById(R.id.place_title);

        		dateText.setText(p.date);
        		distanceText.setText(getDistance(p.location));
        		titleText.setText(p.title);
        		
        		p.view = convertView;
        	}
        	else
        	{
        		convertView = p.view;
        	}
        }
        
        return convertView;
    }
    
    
    
    
    
       
    private void loadDataFromCursor(Cursor c, ArrayList<Place> places)
    {
        Stopwatch.start();
        for (c.first(); !c.isAfterLast(); c.next())
        {
            Place p = new Place(c.getLong(0), 
                    Double.parseDouble(c.getString(1)), 
                    Double.parseDouble(c.getString(2)), 
                    Double.parseDouble(c.getString(3)));
            
            p.date = Format.formatDate(c.getLong(4));
            p.title = c.getString(5);
            
            places.add(p);
        }
        Stopwatch.stop("Loaded place data from cursor");
    }
    
    
    private String getDistance(Location l)
    {
        Location here = tracker.getCurrentLocation();

        return Format.formatDistance(l.distanceTo(here));
    }
    
    


    
    
    
    private static class Place
    {
        long id;
        Location location;
        String date;
        String title;
        String picture;
        View view;
        
        public Place(long id, double lat, double lon, double alt)
        {
            this.id = id;
            this.location = new Location();
            this.location.setLatitude(lat);
            this.location.setLongitude(lon);
            this.location.setAltitude(alt);
        }

    }

}
