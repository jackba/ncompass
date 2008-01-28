package info.nymble.ncompass.activities;

import info.nymble.measure.Stopwatch;
import info.nymble.ncompass.LocationTracker;
import info.nymble.ncompass.PlaceBook;
import info.nymble.ncompass.R;
import info.nymble.ncompass.PlaceBook.Places;
import info.nymble.ncompass.view.Format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.location.Location;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewInflate;
import android.widget.ListAdapter;
import android.widget.TextView;


public class PlaceListAdapter extends ObserverManager implements ListAdapter
{
	Activity activity;
	LocationTracker tracker;

	Map<Long, List> lists = (Map<Long, List>) Collections.synchronizedMap(new HashMap<Long, List>());
	PlaceViewCheckout views;
	Location location;	
	List list;
    

    
    public PlaceListAdapter(Activity a, LocationTracker t)
    {
        this.activity = a;
        this.tracker = t;
        
        views = new PlaceViewCheckout(a.getViewInflate());
    }
    
    
    
    
    public void setList(long listId)
    {
        location = tracker.getCurrentLocation();
        
        synchronized (lists)
        {        	
        	if (list != null) list.checkinViews(views);
        	list = lists.get(listId);
        	if (list == null)
        	{
        		list = new List(listId, activity.getContentResolver());
        		lists.put(listId, list);
        	}
        }
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
    	return ( list == null ? 0 : list.places.size() );
    }

    public Object getItem(int position)
    {
        return getItemId(position);
    }

    public long getItemId(int position)
    {
        return ( list == null ? -1 : list.places.get(position).id );
    }

    public int getNewSelectionForKey(int currentSelection, int keyCode, KeyEvent event)
    {
        return currentSelection;
    }

    public boolean stableIds()
    {
        return true;
    }
    
    public View getView(int position, View convertView, ViewGroup parent)
    {
    	if (list == null) return null;
    	Place p = list.places.get(position);
    	PlaceView view = p.view;
    	
    	Stopwatch.start();
    	if (view == null)
    	{
    		p.view = views.checkout();
    		p.view.dateText.setText(p.getDate());
    		p.view.distanceText.setText(Format.formatDistance(location.distanceTo(p.location)));
    		p.view.titleText.setText(p.title);

    		view = p.view;
    		Stopwatch.stop( "built view for p=" + position + " id=" + p.id);
    	}
    	else
    	{
    		view = p.view;
    		Stopwatch.stop( "retrieved view for p=" + position + " id=" + p.id);
    	}
        
        return view.view;
    }
    
    
    
    
    
       

    
    

    
    


    
    private static class List
    {
    	ArrayList<Place> places = new ArrayList<Place>();
        long listId;
        
        public List(long listId, ContentResolver resolver)
        {
            Cursor c = PlaceBook.Places.query(resolver, listId);
            loadDataFromCursor(c, places);
            c.close();
        }
        
    	
        
        public void checkinViews(PlaceViewCheckout c)
        {
        	for (Iterator<Place> i = places.iterator(); i.hasNext();) 
        	{
				Place place = i.next();
				
				c.checkin(place.view);
				place.view = null;
			}
        }
        
    	
        private void loadDataFromCursor(Cursor c, ArrayList<Place> places)
        {
            Stopwatch.start();
            int id = c.getColumnIndex(Places.ID);
            int lat = c.getColumnIndex(Places.LAT);
            int lon = c.getColumnIndex(Places.LON);
            int alt = c.getColumnIndex(Places.ALT);
            int updated = c.getColumnIndex(Places.UPDATED);
            int title = c.getColumnIndex(Places.TITLE);
            
            for (c.first(); !c.isAfterLast(); c.next())
            {
                Place p = new Place(c.getLong(id), 
                        Double.parseDouble(c.getString(lat)), 
                        Double.parseDouble(c.getString(lon)), 
                        Double.parseDouble(c.getString(alt)));
                
                p.date = c.getLong(updated);
                p.title = c.getString(title);
                
                places.add(p);
            }
            Stopwatch.stop("Loaded place data from cursor");
        }
    }
    
    
    
    
    private static class Place
    {
        long id;
        Location location;
        long date;
        String title;
        String picture;
        
        
        PlaceView view;	// cached
        String dateText; // cached
        
        public Place(long id, double lat, double lon, double alt)
        {
            this.id = id;
            this.location = new Location();
            this.location.setLatitude(lat);
            this.location.setLongitude(lon);
            this.location.setAltitude(alt);
        }
        
        
        public String getDate()
        {
        	if (dateText == null) dateText = Format.formatDate(date);
        	return dateText;
        }
    }
    
    
    private static class PlaceViewCheckout
    {
    	LinkedList<PlaceView> list = new LinkedList<PlaceView>();
    	ViewInflate inflate;
    	
    	PlaceViewCheckout(ViewInflate inflate)
    	{
    		this.inflate = inflate;
    	}
    	
    	public synchronized PlaceView checkout()
    	{
   			if (list.size() > 0)
   			{
   				return list.removeFirst();    		
   			}
   			return new PlaceView(inflate);
    	}
    	
    	public synchronized void checkin(PlaceView view)
    	{
    		if (view != null) list.addFirst(view);
    	}
    }
    
    
    private static class PlaceView
    {
		View view;
		TextView dateText;
		TextView distanceText;
		TextView titleText;
		
		PlaceView(ViewInflate inflate)
		{
			view = inflate.inflate(R.layout.place, null, null);
			dateText = (TextView)view.findViewById(R.id.place_date);
			distanceText = (TextView)view.findViewById(R.id.place_distance);
			titleText = (TextView)view.findViewById(R.id.place_title);
		}
    }
}