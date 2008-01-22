package info.nymble.ncompass.activities;

import info.nymble.ncompass.LocationTracker;
import info.nymble.ncompass.R;
import info.nymble.ncompass.PlaceBook.Lists;
import info.nymble.ncompass.PlaceBook.Places;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
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
    final Handler handler = new Handler();

    
    LocationTracker tracker = new LocationTracker(this);
    Gallery gallery;
    ListView list;
    TextView loading;
    
    
    
    PlaceListAdapter placeListAdapter;
    
    
    
    
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setDefaultKeyMode(SHORTCUT_DEFAULT_KEYS);
        setContentView(R.layout.list_gallery);

        gallery = (Gallery) findViewById(R.id.list_gallery);
        list = (ListView) findViewById(R.id.list_contents);
        loading = (TextView) findViewById(R.id.list_loading);


        gallery.setAdapter(new TextListAdapter(this));
        gallery.setOnItemSelectedListener(new SelectionChangeListener(this));

        placeListAdapter =  new PlaceListAdapter(this, tracker);
        list.setAdapter(placeListAdapter);  
        
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
    
    
    private void removeList()
    {
    	String listId = String.valueOf(gallery.getSelectedItemId());
        ContentResolver resolver = this.getContentResolver();
        
        Log.i(null, "removing list id=" + listId);
        resolver.delete(Lists.LISTS_URI, "list=?", new String[]{listId});
    }
    
    
    
    private void removePlace()
    {
    	long placeId = list.getSelectedItemId();
        ContentResolver resolver = this.getContentResolver();
        
        Log.i(null, "deleting place id=" + placeId);
        resolver.delete(Places.PLACES_URI.addId(placeId), null, null);
        updateUI();
    }
    
    
    
    
    
    
    
    
    
    private class SelectionChangeListener implements OnItemSelectedListener
    {
        Activity activity;
        
        SelectionChangeListener(Activity a)
        {
            activity = a;
        }

        @SuppressWarnings("unchecked")
        public void onItemSelected(AdapterView parent, View v, int position, final long id)
        {
//            Stopwatch.start();
//            Stopwatch.stop("Finished selection");
            
            Log.w("Selection Change", "selection changed in gallery position=" + position + " id=" + id);
            list.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);
            
            
            
            new Thread()
            {
                
                public void run()
                {
                    placeListAdapter.setList(id);
                    handler.post(new Runnable()
                    {
                        public void run()
                        {
                            updateUI();
                        }
                    });
                }
            }.start();
        }

        @SuppressWarnings("unchecked")
        public void onNothingSelected(AdapterView arg0)
        {
            // TODO Auto-generated method stub
            
        }
    }
    
    
    
    private void updateUI()
    {
        placeListAdapter.onChanged();
        loading.setVisibility(View.GONE);
        list.setVisibility(View.VISIBLE);
        list.takeFocus(View.FOCUS_FORWARD);
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
            Cursor cursor =  activity.managedQuery(Lists.LISTS_URI, null, null, null);
            
            int nameColumn = cursor.getColumnIndex("name");
            int idColumn = cursor.getColumnIndex("_id");
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
