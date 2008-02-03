package info.nymble.ncompass.activities;

import info.nymble.ncompass.LocationTracker;
import info.nymble.ncompass.R;
import info.nymble.ncompass.PlaceBook.Lists;
import info.nymble.ncompass.view.GalleryBackground;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewInflate;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.GalleryAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class PlaceListActivity extends Activity
{
    final static Handler handler = new Handler();

    
    LocationTracker tracker;
    Gallery gallery;
    ListView list;
    TextView loadingText;
    TextView emptyText;
    
    PlaceListAdapter placeListAdapter;
    
    
    
    
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
        

        gallery.setAdapter(new TextListAdapter(this));
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
        final Intent newListIntent = new Intent(this, AddListActivity.class);

        menu.add(1, 1, "New List", new Runnable()
        {
            public void run()
            {
                startSubActivity(newListIntent, 1);
            }
        }
        );
        
        
        menu.add(1, 2, "Delete List", new Runnable()
        {
            public void run()
            {
                removeList();
            }
        }
        );
        
        
        menu.addSeparator(0, 0);
        
        menu.add(2, 1, "Delete Place", new Runnable()
        {
            public void run()
            {
            	removePlace();
            }
        }
        );
        
        
        return true;
    }
    
    
    
    
    
    
    @Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Log.i(null, "key event code=" + keyCode + " event=" + event.toString());
		
		if (keyCode == 21)
		{
			gallery.setSelection(gallery.getSelectedItemIndex() - 1, true);
		}
		else if (keyCode == 22)
		{
			gallery.setSelection(gallery.getSelectedItemIndex() + 1, true);
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
            Log.w("Selection Change", "selection changed in gallery position=" + position + " id=" + id);
            displayLoadingState();

            Thread t = new Thread()
            {
                public void run()
                {
                	pause();
                    placeListAdapter.setList(id);
                    handler.post(new Runnable()
                    {
                        public void run()
                        {
                        	displayLoadedState();
                        }
                    });
                }
                
                public synchronized void pause()
                {
                	try 
                	{
                		wait(300);
                	} 
                	catch (Exception e){}
                }
            };
            t.setName("Change List Loader " + id);
            t.setPriority(t.getPriority() - 1);
            t.start();
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
//        	gallery.requestFocus();
        	Log.i(null, "has no children");
        }
    }
    

    
    
    
    
    
    private static class TextListAdapter extends ObserverManager implements GalleryAdapter
    {
        ArrayList<ContentObserver> contentObservers = new ArrayList<ContentObserver>();
        ArrayList<DataSetObserver> datasetObservers = new ArrayList<DataSetObserver>();
        
        Activity activity;
        ViewInflate inflate;
        long[] ids;
        String[] names;
        
        
        
        public TextListAdapter(Activity a)
        {
            activity = a;
            inflate = a.getViewInflate();
            loadData();
            
            
            a.getContentResolver().registerContentObserver(Lists.LISTS_URI, false, 
            new ContentObserver(handler)
            {
				@Override
				public void onChange(boolean selfChange) 
				{
					super.onChange(selfChange);
					Log.i(null, "Something Changed!");
					loadData();
					onChanged();
				}
            });
        }
        
        
        public int getCount()
        {
            return ids.length;
        }

        public Object getItem(int position)
        {
            return names[position];
        }

        public long getItemId(int position)
        {
            return ids[position];
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
            View v = inflate.inflate(R.layout.list_entry, null, null);
            
            return v;
        }
        
        public float getScale(boolean focused, int offset)
        {
            return offset == 0 ? 1.0F : 0.75F;   
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
