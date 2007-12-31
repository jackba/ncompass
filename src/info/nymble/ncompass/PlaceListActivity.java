package info.nymble.ncompass;

import info.nymble.ncompass.PlaceBook.Lists;
import info.nymble.ncompass.PlaceBook.Places;
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
import android.widget.Gallery;
import android.widget.GalleryAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class PlaceListActivity extends Activity
{
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setDefaultKeyMode(SHORTCUT_DEFAULT_KEYS);
        setContentView(R.layout.list_gallery);

        Cursor c = managedQuery(Lists.LISTS_URI, null, null, null);
        Gallery g = (Gallery) findViewById(R.id.list_gallery);

        Log.w("PlaceListActivity", "Cursor contains " + c.count());
        g.setAdapter(new TextListAdapter(c, getViewInflate()));


        ListView list = (ListView) findViewById(R.id.list_contents);
        LocationTracker tracker = new LocationTracker(this);
        String[] columns = new String[]{Places.ID, Places.CREATED, Places.LAT, Places.LON, Places.TITLE};
        
        c = managedQuery(Places.PLACES_URI, columns, null, null); 
        list.setAdapter(new PlaceListAdapter(c, getViewInflate(), tracker));
        
        
        
        
//        LocationTracker tracker = new LocationTracker(this);
        
        
//        c.first();
//        String[] cols = c.getColumnNames();
//        
//        for (int i = 0; i < c.count(); i++)
//        {
//            String values = "";
//            for (int j = 0; j < cols.length; j++)
//            {
//                values += cols[j] + "=" + c.getString(j) + "; ";
//            }
//            
//            Log.i("db printout",  values);
//        }
//        
//        
//        
//        SimpleCursorAdapter adapter = new SimpleCursorAdapter
//        (
//                this, 
//                R.layout.recent_location_entry, 
//                c, 
//                cols, 
//                new int[]{R.id.list_id, R.id.list_title, R.id.list_is_sequence, R.id.list_is_system, R.id.list_capacity}
//        );
//        
//        setListAdapter(adapter);
        
        
        
        
    }

    
    private static class TextListAdapter implements GalleryAdapter
    {
        ViewInflate inflate;
        Cursor cursor;
        int idColumn;
        int nameColumn;
        
        
        public TextListAdapter(Cursor c, ViewInflate i)
        {
            inflate = i;
            cursor = c;
            nameColumn = c.getColumnIndex("name");
            idColumn = c.getColumnIndex("_id");
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
