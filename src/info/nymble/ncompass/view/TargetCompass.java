package info.nymble.ncompass.view;

import info.nymble.measure.Stopwatch;
import info.nymble.ncompass.LocationTracker;
import info.nymble.ncompass.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;


/**
 * Presents a [target] compass display to the user.
 * This view displays target oriented information 
 * including.
 * 
 * 1. A NWSE directional bearing  (
 * 		! depends on location.getBearing only
 * 2. A pointer indicating the direction to the target
 * 		! depends on location.getBearing, .getLat, .getLon and target.getLat, .getLon
 * 
 * 
 * @author Andrew Evenson
 *
 */
public class TargetCompass extends View
{
    private final BitmapDrawable nwse = (BitmapDrawable)getResources().getDrawable(R.drawable.compass_nwse);
    private final BitmapDrawable needle = (BitmapDrawable)getResources().getDrawable(R.drawable.compass_needle);
    private final String no_bearing_message = "Current Bearing Unknown";
    private final String no_target_message = "No Target Set";
    private final Paint error_message_paint = buildErrorPaint();
    
    // variables for containing and rotating the compass images
    private Rect bounds = new Rect();	// the square bounding rectangle into which we draw the compass
    private int height;					// the screen height
    private int width;					// the screen width
    private float cx;					// center of the screen on the horizontal axis
    private float cy;					// center of the screen on the vertical axis
    
    
    private Location location = null;
    private Location target = null;
    private LocationTracker tracker = null;


    
    
    
    
    
    public TargetCompass(Context c)
    {
        super(c);
        tracker = new LocationTracker(c);
//        this.location = tracker.getCurrentLocation();
    }
    
    public TargetCompass(Context c, Location target)
    {
        this(c);
//        this.target = target;
    }


    
    /**
     * Alters the location that is being pointed at.
     * The target must not be null and should include 
     * a latitude and longitude. 
     * 
     * @param t the location to use as 'point to there'
     */
    public void setTarget(Location t)
    {
    	if (t != null)
    	{
    		if (target == null || t.getLatitude() != target.getLatitude() || t.getLongitude() != target.getLongitude())
    		{    			
    			target = t;
    			postInvalidate();
    		}
    	}
    }
    
    /**
     * @see setTarget(Location t)
     * 
     * convenience method to construct a Location for the caller
     * 
     * @param latitude
     * @param longitude
     */
    public void setTarget(double latitude, double longitude)
    {
    	Location t = new Location();
    	
    	t.setLatitude(latitude);
    	t.setLongitude(longitude);
    	
    	setTarget(t);
    }
    
    /**
     * @return the target this compass is pointing at. Null if no target has been set
     */
    public Location getTarget()
    {
    	return target;
    }
    
    
    
    
    /**
     * Alters the location that is being displayed 
     * as the reference point for this compass. If
     * the supplied parameter is no different from 
     * the current location, the view will not be 
     * affected (no work will be done). If the new
     * location is different, the location will be 
     * set and the view will be invalidated and drawn 
     * on the UI thread's next pass.
     * 
     * @param l the location to represent as 'from here'
     */
    public void setLocation(Location l)
    {
    	if (l != null)
    	{
    		if (location == null || l.getLatitude() != location.getLatitude() || l.getLongitude() != location.getLongitude())
    		{    			
    			location = l;
    			postInvalidate();
    		}
    	}
    }
    
    
    /**
     * @see setLocation(Location l)
     * 
     * convenience method to construct a Location for the caller
     * 
     * @param latitude
     * @param longitude
     * @param bearing
     */
    public void setLocation(double latitude, double longitude, float bearing)
    {
    	Location l = new Location();
    	
    	l.setLatitude(latitude);
    	l.setLongitude(longitude);
    	l.setBearing(bearing);
    	
    	setLocation(l);
    }
    
    /**
     * @return the location representing the current location 
     * from which the compass points toward the target. Null
     * if no location has been set.
     */
    public Location getLocation()
    {
    	return location;
    }
    
    
    
    
    
    
    
    @Override
    /**
     * The main routine of the class, draws the compass
     * images to the screen.
     */
    protected void onDraw(Canvas canvas)
    {
    	Stopwatch.start();
    	setDimensions();

    	if (location != null && location.hasBearing())
    	{
    		canvas.save();
    		canvas.rotate(-location.getBearing(), cx, cy);
    		nwse.draw(canvas);
    		canvas.restore();
    		
    		if (target != null)
    		{    			
    			canvas.rotate(location.bearingTo(target), cx, cy);
    			needle.draw(canvas);
    		}
    		else
    		{
    	    	canvas.drawText(no_target_message, cx, cy, error_message_paint);
    		}
    	}
    	else
    	{
        	canvas.drawText(no_bearing_message, cx, cy, error_message_paint);

    	}
    	
        Stopwatch.stop("redrawing compass");
    }
    
    

    
    
    
    
    /**
     * Manufactures a paint object that is suited to 
     * drawing error messages on the compass window.
     * @return the constructed paint object
     */
    private Paint buildErrorPaint()
    {
    	Paint p = new Paint();
    	p.setARGB(255, 255, 0, 0);
    	p.setTextAlign(Paint.Align.CENTER);
    	p.setTextSize(14);
    	p.setTypeface(Typeface.create("Georgia", Typeface.BOLD));
    	p.setAntiAlias(true);
    	
    	return p;
    }
    
    /**
     * Makes sure that the drawing parameters for the class
     * are correctly set to the current screen dimensions. The
     * height and width of the screen are used to set these 
     * values. They are only corrected if the mMeasuredHeight
     * or mMeasuredWidth of the view are changed.
     */
    private void setDimensions()
    {
        int h = mMeasuredHeight;
        int w = mMeasuredWidth;
        
        if (h != height || w != width)
        {
        	width = w;
        	height = h;
        	cx = w/2;
        	cy = h/2;
        	setSquareBounds(h, w, bounds);
            
            nwse.setBounds(bounds);
            needle.setBounds(bounds);
        }
    }

    /**
     * calculates the largest square that is smaller than h X w
     * 
     * @param h max height of the square
     * @param w max width of the square
     * 
     * @return a square that fits the dimensions
     */
    private void setSquareBounds(int h, int w, Rect r)
    {
        if (h > w)
        {
        	r.left = 0;
        	r.top = (int)Math.floor((h-w)/2);
        	r.right = w;
        	r.bottom = r.top + w;
        }
        else
        {
        	r.left = (int)Math.floor((w-h)/2);
        	r.top = 0;
        	r.right = r.left + h;
        	r.bottom = 0;
        }
    }
    
    
    
    @Override
    public boolean onMotionEvent(MotionEvent event)
    {
//        mContext.getContentResolver().insert(Recent.CONTENT_URI, null);
//        mContext.startActivity(new Intent(Intent.VIEW_ACTION, Recent.CONTENT_URI));
    	
    	if (event.getAction() == MotionEvent.ACTION_UP) 
    	{
    		Location target = new Location();
    		target.setLatitude(37.447524150941874);
    		target.setLongitude(-122.11882744124402);
    		
			setLocation(tracker.getCurrentLocation());
//			setTarget(target);
		}
    	
    	
    	
        return true;
    }
}
