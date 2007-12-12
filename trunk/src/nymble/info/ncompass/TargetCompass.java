package nymble.info.ncompass;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
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
 * If there is no current speed, time remaining to target 
 * cannot be determined
 * 
 * If there is no target, the compass will not display 
 * distance to target, altitude change to target, 
 * or target bearing. 
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
    private LocationManager locationManager;

    
    
    public TargetCompass(Context c, LocationManager locationManager)
    {
        super(c);
        this.locationManager = locationManager;
    }
    
    public TargetCompass(Context c, Location target, LocationManager locationManager)
    {
        super(c);
        this.target = target;
        this.locationManager = locationManager;
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
        Location l = getCurrentLocation();
        float distance = l.distanceTo(target);
        float rotation = l.bearingTo(target);
        
        //canvas.drawARGB(255, 0, 0, 0);
        
        Paint p = new Paint();
        p.setARGB(255, 255, 255, 255);
        canvas.drawText("d=" + distance, 10F, 10F, p);
        canvas.drawText("o=" + rotation, 10F, 25F, p);
        canvas.drawText("b=" + l.getBearing(), 10F, 40F, p);
        
        Rect r = largestCenteredRectangle();
        ring.setBounds(r);
        ring.draw(canvas);
        
        canvas.rotate(l.getBearing(), this.getWidth()/2, this.getHeight()/2);
        nwse.setBounds(largestCenteredRectangle());
        nwse.draw(canvas);
                    
        canvas.rotate(rotation - l.getBearing(), this.getWidth()/2, this.getHeight()/2);
        needle.setBounds(largestCenteredRectangle());
        needle.draw(canvas);
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
        
        return r;
    }
    
    
    
    
    public Location getCurrentLocation()
    {
        List<LocationProvider> list = locationManager.getProviders();

        for (int i = 0; i < list.size(); i++)
        {
            LocationProvider p = list.get(i);
            Location l = locationManager.getCurrentLocation(p.getName());

            logProvider(p);
            logLocation(l);
            return l;
        }

        return null;
    }

    private void logProvider(LocationProvider p)
    {
        Log.i("NCompass Logger", "name=" + p.getName());
        Log.i("NCompass Logger", "getPowerRequirement=" + p.getPowerRequirement());
        Log.i("NCompass Logger", "getPowerRequirement=" + p.getPowerRequirement());
        Log.i("NCompass Logger", "hasMonetaryCost=" + p.hasMonetaryCost());
        Log.i("NCompass Logger", "requiresCell=" + p.requiresCell());
        Log.i("NCompass Logger", "requiresNetwork=" + p.requiresNetwork());
        Log.i("NCompass Logger", "requiresSatellite=" + p.requiresSatellite());
        Log.i("NCompass Logger", "supportsAltitude=" + p.supportsAltitude());
        Log.i("NCompass Logger", "supportsBearing=" + p.supportsBearing());
        Log.i("NCompass Logger", "supportsSpeed=" + p.supportsSpeed());
    }

    private void logLocation(Location l)
    {
        Log.i("NCompass Location Logger", "getLatitude=" + l.getLatitude());
        Log.i("NCompass Location Logger", "getLongitude=" + l.getLongitude());
        Log.i("NCompass Location Logger", "getBearing=" + l.getBearing());
    }
    
    
    
    

    @Override
    public boolean onMotionEvent(MotionEvent event)
    {
        this.invalidate();
        return true;
    }
}
