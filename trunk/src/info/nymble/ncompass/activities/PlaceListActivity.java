package info.nymble.ncompass.activities;

import info.nymble.ncompass.LocationTracker;
import info.nymble.ncompass.R;
import info.nymble.ncompass.PlaceBook.Lists;
import info.nymble.ncompass.view.GalleryBackground;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.ContentURI;
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
    
    
    
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        tracker = new LocationTracker(this);
        setDefaultKeyMode(SHORTCUT_DEFAULT_KEYS);
        setContentView(R.layout.list_gallery);

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
        
        TextView empty = new TextView(this);
        empty.setText("No places in list");
        list.setEmptyView(empty);
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
		
		if (requestCode==LIST_ADDED)
		{
			try 
			{
				ContentURI uri = new ContentURI(data);
				int position = galleryAdapter.findPosition(uri.getPathLeafId());
				
				gallery.setSelection(position);
			} catch (URISyntaxException e) {}
		}
		
		
	}


	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		int position = gallery.getSelectedItemIndex();
		Log.i(null, "key event code=" + keyCode + " event=" + event.toString() + " pos=" + position);
		Log.i(null, "gallery focus=" + gallery.isFocusable() + " windowFocus=" + gallery.hasWindowFocus());
		
		gallery.setFocusable(false);
		if (keyCode == 21)
		{
			gallery.setSelection(position - 1, true);
		}
		else if (keyCode == 22)
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

        Log.i(null, "removing list id=" + listId);
        Lists.delete(getContentResolver(), listId);
    }
    
    
    
    private void removePlace()
    {
    	long placeId = list.getSelectedItemId();

        placeListAdapter.deletePlace(placeId);
        displayLoadedState();
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
        	list.takeFocus(View.FOCUS_FORWARD);
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
    	
    	Item placeSeparator;
    	Item deletePlace;
    	
    	
    	
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
            
            
     	
            placeSeparator = menu.addSeparator(2, 0);
            	
            
            deletePlace = menu.add(2, 1, "Delete Place", new Runnable()
        	{
        		public void run()
        		{
        			removePlace();
        		}
        	}
        	);
    		
    	}
    	
    	
    	public void prepare()
    	{
    		boolean showPlaces = placeListAdapter.getCount() > 0;
    		
    		placeSeparator.setShown(showPlaces);
    		deletePlace.setShown(showPlaces);
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
        }
    }
    
    
    
}
