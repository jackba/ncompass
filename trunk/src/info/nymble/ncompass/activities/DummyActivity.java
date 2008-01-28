package info.nymble.ncompass.activities;


import info.nymble.ncompass.PlaceBookDB;
import info.nymble.ncompass.R;
import info.nymble.ncompass.PlaceBook.Lists;
import info.nymble.ncompass.PlaceBook.Places;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class DummyActivity extends Activity
{

    
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        Location l = new Location();
        l.setLatitude(192.34254857);
        l.setLongitude(-47.3427534295);
        l.setAltitude(237);
        
        Location l2 = new Location();
        l2.setLatitude(192.34254857);
        l2.setLongitude(-47.3427534295);
        l2.setAltitude(0);
        
        
        Location l3 = new Location();
        l3.setLatitude(193.34254857);
        l3.setLongitude(-48.3427534295);
        l3.setAltitude(0);
        
        
        
//        Places.update(getContentResolver(), 39, "Best place on earth", null, null);
//        Places.add(getContentResolver(), l3, 4);
        

//        Places.delete(getContentResolver(), 33);
        
        Cursor c =  Lists.query(getContentResolver());
        PlaceBookDB.printCursor(c);
        
        c = Places.query(getContentResolver(), 1);
        PlaceBookDB.printCursor(c);
        
        c = Places.query(getContentResolver(), 2);
        PlaceBookDB.printCursor(c);
        
        c = Places.query(getContentResolver(), 3);
        PlaceBookDB.printCursor(c);
        
        c = Places.query(getContentResolver(), 4);
        PlaceBookDB.printCursor(c);
	}

    
    private void f()
    {
        this.setContentView(R.layout.recent_location_entry);

        ImageView i = (ImageView) findViewById(R.id.image_id);
	    i.setImageResource(R.drawable.icon_compass);
	    
	    
	    Display d = this.getWindowManager().getDefaultDisplay();
	    Log.w(null, "screen dimensions h=" + d.getHeight() + " w=" + d.getWidth() + " orient=" + d.getOrientation());    	
    }
    
    
    
	
	public class ImageAdapter extends BaseAdapter {
	    public ImageAdapter(Context c) {
	        mContext = c;
	    }
	
	    public int getCount() {
	        return mImageIds.length;
	    }
	
	    public Object getItem(int position) {
	        return position;
	    }
	
	    public long getItemId(int position) {
	        return position;
	    }
	
	    public View getView(int position, View convertView, ViewGroup parent) {
	        ImageView i = new ImageView(mContext);
	
	        i.setImageResource(mImageIds[position]);
	        i.setScaleType(ImageView.ScaleType.FIT_XY);
	        i.setLayoutParams(new Gallery.LayoutParams(100, 65));
	        return i;
	    }
	
	    public float getAlpha(boolean focused, int offset) {
	        return Math.max(0, 1.0f - (0.2f * Math.abs(offset)));
	    }
	
	    public float getScale(boolean focused, int offset) {
	        return Math.max(0, 1.0f - (0.2f * Math.abs(offset)));
	    }
	
	    private Context mContext;
	
	    private Integer[] mImageIds = {
	            R.drawable.gallery_photo_1,
	            R.drawable.gallery_photo_2
	    };
	}
	    
	    
	    
    
    
    
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add(1, 1, "Hello", new Runnable()
        {
            public void run()
            {
                //
            }
        }
        );
        
        return true;
    }
}
