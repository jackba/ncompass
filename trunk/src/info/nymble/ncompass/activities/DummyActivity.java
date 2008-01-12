package info.nymble.ncompass.activities;


import info.nymble.ncompass.R;
import android.app.Activity;
import android.content.Context;
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

        this.setContentView(R.layout.list_gallery);
        
        

	    Gallery g = (Gallery) findViewById(R.id.list_gallery);
	    g.setAdapter(new ImageAdapter(this));
//	    g.setSelectorSkin(getResources().getDrawable(R.drawable.gallery_selected_item));
	    
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
