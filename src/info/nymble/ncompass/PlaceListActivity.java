package info.nymble.ncompass;

import info.nymble.ncompass.PlaceBook.Lists;
import android.app.Activity;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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
    LocationTracker tracker = new LocationTracker(this);
    Gallery g;
    ListView list;
    
    PlaceListAdapter placeListAdapter;
    
    
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setDefaultKeyMode(SHORTCUT_DEFAULT_KEYS);
        setContentView(R.layout.list_gallery);

        g = (Gallery) findViewById(R.id.list_gallery);
        list = (ListView) findViewById(R.id.list_contents);


        g.setAdapter(new TextListAdapter(this));
        g.setOnItemSelectedListener(new SelectionChangeListener(this));

        placeListAdapter =  new PlaceListAdapter(this, tracker, 1);
        list.setAdapter(placeListAdapter);
    }

    
    
    private class SelectionChangeListener implements OnItemSelectedListener
    {
        Activity activity;
        
        SelectionChangeListener(Activity a)
        {
            activity = a;
        }

        public void onItemSelected(AdapterView parent, View v, int position, final long id)
        {
            Log.w("Selection Change", "selection changed in gallery position=" + position + " id=" + id);
            placeListAdapter.setList(id);
        }

        public void onNothingSelected(AdapterView arg0)
        {
            // TODO Auto-generated method stub
            
        }
    }
    
    
    
    
    
    
    
    private static class TextListAdapter implements GalleryAdapter
    {
        ViewInflate inflate;
        Cursor cursor;
        int idColumn;
        int nameColumn;
        
        
        public TextListAdapter(Activity a)
        {
            cursor =  a.managedQuery(Lists.LISTS_URI, null, null, null);
            inflate = a.getViewInflate();
            nameColumn = cursor.getColumnIndex("name");
            idColumn = cursor.getColumnIndex("_id");
        }
        
        
        public int getCount()
        {
            return cursor.count();
        }

        public Object getItem(int position)
        {
            cursor.moveTo(position);
            return cursor.getString(nameColumn);
        }

        public long getItemId(int position)
        {
            cursor.moveTo(position);
            return cursor.getLong(idColumn);
        }

        public int getNewSelectionForKey(int currentSelection, int keyCode, KeyEvent event)
        {
            return currentSelection;
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            String name = (String)getItem(position);
            View v = inflate.inflate(R.layout.list_entry, null, null);
            TextView titleText = (TextView)v.findViewById(R.id.list_title);

            titleText.setText(name);
                       
            return v;
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
            return offset == 0 ? 1.0F : 0.75F;  
        }
        
        
        
        
        
        public void registerContentObserver(ContentObserver arg0)
        {
        }

        public void registerDataSetObserver(DataSetObserver arg0)
        {
        }


        public void unregisterContentObserver(ContentObserver observer)
        {
        }

        public void unregisterDataSetObserver(DataSetObserver arg0)
        {
            
        }
    }
}
