package info.nymble.ncompass;

import info.nymble.measure.Stopwatch;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


/**
 * Presents a [target] compass display to the user.
 * This view displays target oriented information 
 * including.
 * 
 * 1. A compass background
 * 2. A NWSE directional bearing
 * 3. A pointer indicating the direction to the target
 * 4. The distance to the target
 * 5. The time it would take to reach the target at the current 
 *    speed traveling in the shortest direction
 * 
 * If there is no known bearing, direction to target and NWSE 
 * cannot be displayed. 
 * 
 * If there is no currently known location, distance to target 
 * cannot be determined
 * 
 * If there is no current speed or distance to target, time 
 * remaining to target cannot be determined
 * 
 * If there is no target, the compass will not display 
 * distance to target, altitude change to target, 
 * or target bearing. 
 * 
 * 
 * The compass uses internal facilities to maintain its
 * knowledge of current location data. The 
 * 
 * 
 * 
 * @author Andrew Evenson
 *
 */
public class TargetCompass extends View
{
    private BitmapDrawable ring = (BitmapDrawable)getResources().getDrawable(R.drawable.compass_ring);
    private BitmapDrawable nwse = (BitmapDrawable)getResources().getDrawable(R.drawable.compass_nwse);
    private BitmapDrawable needle = (BitmapDrawable)getResources().getDrawable(R.drawable.compass_needle);
    
    private Location target = null;
    private LocationTracker tracker = null;


    
    
    public TargetCompass(Context c)
    {
        super(c);

        tracker = new LocationTracker(c);
    }
    
    public TargetCompass(Context c, Location target)
    {
        this(c);
        this.target = target;
    }



    
    public void setTarget(Location target)
    {
        this.target = target;
        this.invalidate();
    }
    
    
    
    @Override
    protected void onDraw(Canvas canvas)
    {
        Log.i("redraw", "redrawing compass");
        Location l = tracker.getCurrentLocation();
        
        Stopwatch.start();
        float northR = -l.getBearing();
        float distance = l.distanceTo(target);
        float targetR = northR + l.bearingTo(target);
        Stopwatch.stop("calculate distance");
        
        Stopwatch.start();
        Paint p = new Paint();
        p.setARGB(255, 255, 255, 255);
        canvas.drawText("d=" + distance, 10F, 10F, p);
        canvas.drawText("o=" + targetR, 10F, 25F, p);
        canvas.drawText("b=" + l.getBearing(), 10F, 40F, p);
        Stopwatch.stop("paint text and background");
        
        Stopwatch.start();
        Rect r = largestCenteredRectangle();
        Stopwatch.stop("determine center mass");
        
        Stopwatch.start();
        ring.setBounds(r);
        ring.draw(canvas);
        Stopwatch.stop("draw compass back");
        
        Stopwatch.start();
        canvas.rotate(northR, this.getWidth()/2, this.getHeight()/2);
        nwse.setBounds(largestCenteredRectangle());
        nwse.draw(canvas);
        Stopwatch.stop("draw nwse");           
        
        Stopwatch.start();
        canvas.rotate(targetR - l.getBearing(), this.getWidth()/2, this.getHeight()/2);
        needle.setBounds(largestCenteredRectangle());
        needle.draw(canvas);
        Stopwatch.stop("draw needle");
    }
    
    
    
    
    private Rect largestCenteredRectangle()
    {
        int h = this.getHeight();
        int w = this.getWidth();
        Rect r = new Rect(0,0,w,h);
        
        if (h > w)
        {
            r.top = (int)Math.floor((h-w)/2);
            r.bottom = r.top + w;
        }
        else
        {
            r.left = (int)Math.floor((w-h)/2);
            r.right = r.left + h;
        }
        
        int scale = (int)((r.right - r.left)*.1);
        
        r.left += scale;
        r.right -= scale;
        r.top += scale;
        r.bottom -= scale;
        
        
        return r;
    }
    
    
    
    @Override
    public boolean onMotionEvent(MotionEvent event)
    {
        mContext.getContentResolver().insert(Recent.CONTENT_URI, null);
        this.invalidate();
        return true;
    }
}
