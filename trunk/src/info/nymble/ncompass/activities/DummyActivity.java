package info.nymble.ncompass.activities;


import info.nymble.ncompass.PlaceBookDB;
import info.nymble.ncompass.R;
import info.nymble.ncompass.PlaceBook.Lists;
import info.nymble.ncompass.PlaceBook.Places;
import android.app.Activity;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SpinnerAdapter;

public class DummyActivity extends Activity
{
	 ImageView i1;
	 ImageView i2;
	 
	 
@Override
protected void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    
//    testGallery(this);
//    testList(this);

    
//    i1 = new ImageView(this);
//    i1.setImageResource(R.drawable.gallery_photo_1);
//    
//    i2 = new ImageView(this);
//    i2.setImageResource(R.drawable.gallery_photo_2);
//    
//    this.setContentView(i1);
//    
//    
    
    this.setContentView(R.layout.dummy);
    
//    Rect r = new Rect(0,0,250,250);
//    BitmapDrawable nwse = (BitmapDrawable) getResources().getDrawable(R.drawable.compass_nwse);
//    BitmapDrawable nwse_p  = buildScaledDrawable(nwse, r);
//    nwse.setBounds(r);
//    
//    
//    Bitmap b = Bitmap.createBitmap(r.right, r.bottom, true);
//    Canvas c = new Canvas(b);
//    
//    Bitmap b1 = Bitmap.createBitmap(r.right, r.bottom, true);
//    Canvas c1 = new Canvas(b1);
//    
//    
//    long time = System.currentTimeMillis();
//    
//    time = System.currentTimeMillis();
//    for (int i = 0; i < 360; i++) {
//    	c.rotate(1);
//    	nwse.draw(c);
//    }
//    time = logTime(time, "regular");
//    
//    for (int i = 0; i < 360; i++) {
//    	c1.rotate(1);
//    	nwse_p.draw(c1);
//    }
//    time = logTime(time, "regular");
//    
//    
//    
//    
//
//    i1 = new ImageView(this);
//    i1.setImageBitmap(b);
//    this.setContentView(i1);
//    
    
    
    
    
    
    
    
    try {
    	MediaPlayer mp = MediaPlayer.create(this, R.raw.mph);
//		mp.prepare();
		mp.start();
	} catch (Exception e) {
		Log.e(null, "bit of a problem " + e.getMessage(), e);
	}
}


private BitmapDrawable buildScaledDrawable(BitmapDrawable original, Rect bounds)
{
	Bitmap b = Bitmap.createBitmap(bounds.width(), bounds.height(), true);
	Canvas c = new Canvas(b);
	
	original.setBounds(0, 0, bounds.width(), bounds.height());
	original.draw(c);
	BitmapDrawable scaled = new BitmapDrawable(b);
	scaled.setBounds(bounds);
	
	return scaled;
}





private long logTime(long startTime, String m)
{
	long endTime = System.currentTimeMillis();
	
	Log.w("Log Time", m + " elapsed=" + (endTime - startTime));
	
	return endTime;
}



@Override
public boolean onKeyUp(int keyCode, KeyEvent event) {
	Log.i(null, "onKeyUp event occurred");
	
	;
	animate(true);
	
	return super.onKeyUp(keyCode, event);
}


void animate(boolean out)
{
	float start = (out ? 0 : 90);
	float end = 90 - start;
	
    final Rotate3dAnimation rotation =
        new Rotate3dAnimation(start, end, i1.getWidth()/2.0f, i1.getHeight()/2.0f, i1.getWidth(), true);
	rotation.setDuration(500);
	rotation.setFillAfter(true);
	rotation.setInterpolator(new AccelerateInterpolator());
	if (out) rotation.setAnimationListener(new DisplayNextView());
	
	i1.startAnimation(rotation);	
}





private final class DisplayNextView implements Animation.AnimationListener {

    public void onAnimationStart() {
    }

    public void onAnimationEnd() {
    	ImageView i3 = i1;
    	i1 = i2;
    	i2 = i3;
    	setContentView(i1);
    	
    	animate(false);
    }

    public void onAnimationRepeat() {
    }
}






static void testList(Context c)
{
    ListView l = new ListView(c);
    TestAdapter a = new TestAdapter();
    
    l.setAdapter(a);
    l.setFocusable(true);
    
    Log.i(null, "list.isFocusable=" + l.isFocusable());
    
    a.change(new long[]{271828182, 314159265});
    
    Log.i(null, "list.isFocusable=" + l.isFocusable());
}

static void testGallery(Context c)
{
    Gallery g = new Gallery(c);
    TestAdapter a = new TestAdapter();
    
    g.setAdapter(a);
    g.setFocusable(false);
    
    Log.i(null, "gallery.isFocusable=" + g.isFocusable());
    
    a.change(new long[]{271828182, 314159265});
    
    Log.i(null, "gallery.isFocusable=" + g.isFocusable());
}













static class TestAdapter implements SpinnerAdapter, ListAdapter
{
	ContentObserver cObserver;
	DataSetObserver dObserver;        	
	long[] values = new long[]{271828182};

	
	public void change(long[] values)
	{
		this.values = values;
		if (cObserver != null)
		{
			Log.i(null, "sending change notification to changeObserver " + cObserver.getClass());
			cObserver.onChange(false);			
		}
		if (dObserver != null)
		{			
			Log.i(null, "sending change notification to dataObserver " + dObserver.getClass());
			dObserver.onChanged();
		}
		if (dObserver == null && cObserver == null)
		{			
			Log.i(null, "both observer references are null");
		}
	}
	
	
	public View getDropDownView(int position, View convertView,
			ViewGroup parent) {
		return null;
	}

	public View getMeasurementView(ViewGroup arg0) {
		return null;
	}

	public int getCount() {
		return values.length;
	}

	public Object getItem(int position) {
		return values[position];
	}

	public long getItemId(int position) {
		return values[position];
	}

	public int getNewSelectionForKey(int currentSelection, int keyCode,
			KeyEvent event) {
		return currentSelection;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}

	public void registerContentObserver(ContentObserver observer) {
		cObserver = observer;
	}

	public void registerDataSetObserver(DataSetObserver observer) {
		dObserver = observer;
	}

	public boolean stableIds() {
		return true;
	}

	public void unregisterContentObserver(ContentObserver observer) {
	}

	public void unregisterDataSetObserver(DataSetObserver observer) {
	}


	public boolean areAllItemsSelectable() {
		return true;
	}


	public boolean isSelectable(int arg0) {
		return true;
	}
}
    
    
    
    
    
    
    
    public void g()
    {
    	
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

    
//    private void f()
//    {
//        this.setContentView(R.layout.recent_location_entry);
//
//        ImageView i = (ImageView) findViewById(R.id.image_id);
//	    i.setImageResource(R.drawable.icon_compass);
//	    
//	    
//	    Display d = this.getWindowManager().getDefaultDisplay();
//	    Log.w(null, "screen dimensions h=" + d.getHeight() + " w=" + d.getWidth() + " orient=" + d.getOrientation());    	
//    }
    
    
    
	
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




class Rotate3dAnimation extends Animation {
    private final float mFromDegrees;
    private final float mToDegrees;
    private final float mCenterX;
    private final float mCenterY;
    private final float mDepthZ;
    private final boolean mReverse;
    private Camera mCamera;

    /**
     * Creates a new 3D rotation on the Y axis. The rotation is defined by its
     * start angle and its end angle. Both angles are in degrees. The rotation
     * is performed around a center point on the 2D space, definied by a pair
     * of X and Y coordinates, called centerX and centerY. When the animation
     * starts, a translation on the Z axis (depth) is performed. The length
     * of the translation can be specified, as well as whether the translation
     * should be reversed in time.
     *
     * @param fromDegrees the start angle of the 3D rotation
     * @param toDegrees the end angle of the 3D rotation
     * @param centerX the X center of the 3D rotation
     * @param centerY the Y center of the 3D rotation
     * @param reverse true if the translation should be reversed, false otherwise
     */
    public Rotate3dAnimation(float fromDegrees, float toDegrees,
            float centerX, float centerY, float depthZ, boolean reverse) {
        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;
        mCenterX = centerX;
        mCenterY = centerY;
        mDepthZ = depthZ;
        mReverse = reverse;
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mCamera = new Camera();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        final float fromDegrees = mFromDegrees;
        float degrees = fromDegrees + ((mToDegrees - fromDegrees) * interpolatedTime);

        final float centerX = mCenterX;
        final float centerY = mCenterY;
        final Camera camera = mCamera;

        final Matrix matrix = t.getMatrix();

        camera.save();
        if (mReverse) {
            camera.translate(0.0f, 0.0f, mDepthZ * interpolatedTime);
        } else {
            camera.translate(0.0f, 0.0f, mDepthZ * (1.0f - interpolatedTime));
        }
        camera.rotateY(degrees);
        camera.getMatrix(matrix);
        camera.restore();

        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);
    }
}
