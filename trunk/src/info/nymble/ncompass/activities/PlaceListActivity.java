package info.nymble.ncompass.activities;

import info.nymble.ncompass.LocationTracker;
import info.nymble.ncompass.PlaceBookDB;
import info.nymble.ncompass.R;
import info.nymble.ncompass.PlaceBook.Lists;
import info.nymble.ncompass.PlaceBook.Places;
import info.nymble.ncompass.view.GalleryBackground;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewInflate;
import android.view.Menu.Item;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.GalleryAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class PlaceListActivity extends Activity
{
	final static int LIST_ADDED = 1001;
	
    final static Handler handler = new Handler();

    
    
    LocationTracker tracker;
    Gallery gallery;
    ListView list;
    TextView loadingText;
    TextView emptyText;
    
    PlaceListAdapter placeListAdapter;
    TextListAdapter galleryAdapter;
    MenuManager menu;
    ListLoader loader = new ListLoader();
    
    private SharedPreferences preferences;
    
    
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        tracker = new LocationTracker(this);
        setDefaultKeyMode(SHORTCUT_DEFAULT_KEYS);
        setContentView(R.layout.list_gallery);
        preferences = getPreferences(0);
        
        gallery = (Gallery) findViewById(R.id.list_gallery);
        list = (ListView) findViewById(R.id.list_contents);
        loadingText = (TextView) findViewById(R.id.list_loading);
        emptyText = (TextView) findViewById(R.id.list_empty);
        
        
        galleryAdapter = new TextListAdapter(this);
        gallery.setAdapter(galleryAdapter);
        gallery.setOnItemSelectedListener(new SelectionChangeListener());
        gallery.setPadding(10, 5, 10, 7);
        gallery.setBackground(new GalleryBackground(100, 0.8F));
        gallery.setFocusable(false);
        
        placeListAdapter =  new PlaceListAdapter(this, tracker);
        list.setAdapter(placeListAdapter);  
        list.setFocusableInTouchMode(true);
        list.setOnItemClickListener(new OnItemClickListener()
        {
			public void onItemClick(AdapterView adapter, View view, int arg2,
					long arg3) {
				if (!view.isSelected())
				{
					view.setSelected(true);
				}
				else
				{					
					targetPlace();
				}
		}});

        
        TextView empty = new TextView(this);
        empty.setText("No places in list");
        list.setEmptyView(empty);
        setInitialList();
    }

    
    @Override
	protected void onStop() {
		setList(gallery.getSelectedItemId());
		super.onStop();
	}


	@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	this.menu = new MenuManager(this, menu);
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
    	gallery.setFocusable(false);
    	this.menu.prepare();
        return true;
    }
    

    @Override
	protected void onActivityResult(int requestCode, int resultCode, String data, Bundle extras) 
    {
		super.onActivityResult(requestCode, resultCode, data, extras);
		
		if (requestCode==LIST_ADDED && resultCode == Activity.RESULT_OK && data != null)
		{
			Uri uri = Uri.parse(data);
			int position = galleryAdapter.findPosition(ContentUris.parseId(uri));
			
			gallery.setSelection(position);
		}
	}


	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		int position = gallery.getSelectedItemPosition();

		gallery.setFocusable(false);
		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && gallery.getSelectedItemPosition() > 0)
		{
			gallery.setSelection(position - 1, true);
		}
		else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && 
				gallery.getSelectedItemPosition() < gallery.getCount() - 1)
		{
			gallery.setSelection(position + 1, true);
		}
		else
		{			
			return super.onKeyUp(keyCode, event);
		}
		return false;
	}


	
	
	
	
	
	
	
	private void removeList()
    {
    	long listId = gallery.getSelectedItemId();

        Log.i("PlaceListActivity", "removing list id=" + listId);
        Lists.delete(getContentResolver(), listId);
    }
    
    
    
    private void removePlace()
    {
    	long placeId = list.getSelectedItemId();

        placeListAdapter.deletePlace(placeId);
        displayLoadedState();
    }
    
    
    private void targetPlace()
    {
    	long placeId = list.getSelectedItemId();
    	Cursor c = Places.get(this.getContentResolver(), placeId);  

    	if ( c.first() )
    	{    		
			Location location = new Location();
			location.setLatitude( Double.parseDouble( c.getString(c.getColumnIndex(Places.LAT)) ));
			location.setLongitude( Double.parseDouble( c.getString(c.getColumnIndex(Places.LON)) ));
			String title = c.getString(c.getColumnIndex(Places.TITLE));
			Intent i = new Intent(Intent.VIEW_ACTION);
			
			
			i.putExtra("Location", location);
			i.putExtra("Title", title);
			
			i.setClass(this, TargetCompassActivity.class);
			
			Log.i("PlaceListActivity", "targeting " + location);
			this.startActivity(i);
    	}
    	
    	PlaceBookDB.close(c);
    }

    
    private void setInitialList()
    {
    	Intent i = getIntent();	
    	long listId = i.getLongExtra("List", -1);
    	int position = -1;
    	
    	if (listId == -1) listId = getList();
    	if (listId != -1) position = galleryAdapter.findPosition(listId);
    	
    	Log.d("PlaceListActivity", "loading listId=" + listId + " at position=" + position);
    	if (position >= 0) gallery.setSelection(position);
    }
    
    private long getList()
    {
    	return preferences.getLong("List", -1);
    }

    private void setList(long listId)
    {
    	if (listId >= 0)
    	{    		
    		SharedPreferences.Editor e = preferences.edit();
    		e.putLong("List", listId);
    		e.commit();
    	}
    }
    
    
    
    
    
    
    
    
    
    
    
    private class SelectionChangeListener implements OnItemSelectedListener
    {
        @SuppressWarnings("unchecked")
        public void onItemSelected(AdapterView parent, View v, int position, final long id)
        {
            displayLoadingState();
            loader.setList(id);
        }

        @SuppressWarnings("unchecked")
        public void onNothingSelected(AdapterView arg0){}
    }
    
    
    
    void displayLoadingState()
    {
        list.setVisibility(View.GONE);
        emptyText.setVisibility(View.GONE);
        loadingText.setVisibility(View.VISIBLE);
    }
    
    void displayLoadedState()
    {
        placeListAdapter.onChanged();
        loadingText.setVisibility(View.GONE);
        emptyText.setVisibility(View.GONE);
        

        if (placeListAdapter.getCount() > 0) 
        {
        	list.setVisibility(View.VISIBLE);
        	list.requestFocus(View.FOCUS_FORWARD);
        }
        else
        {
        	emptyText.setVisibility(View.VISIBLE);
        }
    }
    
    
    
    
    
    
    
    private class MenuManager
    {
    	Menu menu;
    	
    	Item newList;
    	Item deleteList;
    	

    	Item deletePlace;
    	Item targetPlace;


    	
    	
    	MenuManager(final Context context, Menu menu)
    	{
    		this.menu = menu;

            newList = menu.add(1, 1, "New List", new Runnable()
            {
                public void run()
                {
                	Intent newListIntent = new Intent(context, AddListActivity.class);
                    startSubActivity(newListIntent, LIST_ADDED);
                }
            }
            );
            
            
            deleteList = menu.add(1, 2, "Delete List", new Runnable()
            {
                public void run()
                {
                    removeList();
                }
            }
            );
            
            

            deletePlace = menu.add(2, 1, "Delete Place", new Runnable()
        	{
        		public void run()
        		{
        			removePlace();
        		}
        	}
        	);
            

            
            
            targetPlace = menu.add(2, 3, "Target Place", new Runnable()
        	{
        		public void run()
        		{
        			targetPlace();
        		}
        	}
        	);
    	}
    	
    	
    	public void prepare()
    	{
    		boolean showPlaces = placeListAdapter.getCount() > 0;
    		
    		deletePlace.setShown(showPlaces);
    		targetPlace.setShown(showPlaces);
    	}
    	
    }
    
    
    
    
    private class ListLoader
    {
    	private Timer timer = new Timer();
    	private long listId = -1;
    	private Object lock = "listID lock";
    	
    	
    	
    	
    	public void setList(final long id)
    	{
    		synchronized (lock)
    		{    			
    			this.listId = id;
    		}
    		
    		timer.schedule(new TimerTask(){
				public void run() 
				{	
					if (stillValid(id)){
						placeListAdapter.setList(id);
					}
						
					if (stillValid(id)){
						handler.post(new Runnable()
						{
							public void run()
							{
								displayLoadedState();
							}
						});						
					}
						;
				}}, 300);
    	}
    	
    	public boolean stillValid(long id)
    	{
    		synchronized (lock)
    		{
    			return id == this.listId;
    		}
    	}
    }
    
    
    
    
    
    
    private class TextListAdapter extends ObserverManager implements GalleryAdapter
    {
        ArrayList<ContentObserver> contentObservers = new ArrayList<ContentObserver>();
        ArrayList<DataSetObserver> datasetObservers = new ArrayList<DataSetObserver>();
        View measureView;
        
        Activity activity;
        ViewInflate inflate;
        long[] ids;
        String[] names;
        
        
        public TextListAdapter(Activity a)
        {
            activity = a;
            inflate = a.getViewInflate();
            measureView = inflate.inflate(R.layout.list_entry, null, null);
            
            loadData();

            a.getContentResolver().registerContentObserver(Lists.LISTS_URI, false, 
            new ContentObserver(handler)
            {
				@Override
				public void onChange(boolean selfChange) 
				{
					loadData();
					onChanged();
					gallery.setFocusable(false);
				}
            });
        }

        
        
        
        private boolean inRange(int position)
        {
        	return 0 <= position && position < ids.length;
        }
        
        public int getCount()
        {
            return ids.length;
        }

        public Object getItem(int position)
        {
        	if (inRange(position))return names[position];
        	return null;
        }

        
        public int findPosition(long id)
        {
        	for (int i = 0; i < ids.length; i++) 
        	{
				if (ids[i] == id) return i;
			}
        	return -1;
        }
        
        public long getItemId(int position)
        {
        	if (inRange(position)) return ids[position];
        	return -1;
        }

        public int getNewSelectionForKey(int currentSelection, int keyCode, KeyEvent event)
        {
            return currentSelection;
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            if ( convertView == null ) 
            {
                convertView = inflate.inflate(R.layout.list_entry, null, null);
                
                String name = (String)getItem(position);
                TextView titleText = (TextView)convertView.findViewById(R.id.list_title);
                
                titleText.setText(name);
            }

            return convertView;
        }

        public boolean stableIds()
        {
            return true;
        }
        

        public View getDropDownView(int position, View convertView, ViewGroup parent)
        {
            return getView(position, convertView, parent);
        }

        public View getMeasurementView(ViewGroup arg0)
        {
            return measureView;
        }
        
        public float getScale(boolean focused, int offset)
        {
            return offset == 0 ? 1.0F : 0.8F;   
        }
        
        public float getAlpha(boolean focused, int offset)
        {
            return 1.0F; //offset == 0 ? 1.0F : 0.75F;  
        }
        
        
        
        
        

        
        
        
        
        private void loadData()
        {
            Cursor cursor =  Lists.query(activity.getContentResolver());
            
            int nameColumn = cursor.getColumnIndex(Lists.NAME);
            int idColumn = cursor.getColumnIndex(Lists.ID);
            int records = cursor.count();
            
            ids = new long[records];
            names = new String[records];
            
            if (cursor.first())
            {                
                for (int i = 0; i < records; i++)
                {
                    ids[i] = cursor.getLong(idColumn);
                    names[i] = cursor.getString(nameColumn);
                    cursor.next();
                }
            }
            
            PlaceBookDB.close(cursor);
        }
    }
    
    
    
}
