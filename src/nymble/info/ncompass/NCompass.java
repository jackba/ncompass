package nymble.info.ncompass;

import java.util.List;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class NCompass extends Activity
{
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        
        Location target = new Location();
        target.setLatitude(37.447524150941874);
        target.setLongitude(-122.11882744124402);
        
        setContentView(new Compass(this, target, locationManager));
    }


    
    
    
    public class Compass extends View
    {
        BitmapDrawable ring = (BitmapDrawable)getResources().getDrawable(R.drawable.compass_ring);
        BitmapDrawable needle = (BitmapDrawable)getResources().getDrawable(R.drawable.compass_needle);
        
        Location target = new Location();
        LocationManager locationManager;

        public Compass(Context c, Location target, LocationManager locationManager)
        {
            super(c);
            this.target = target;
            this.locationManager = locationManager;
  

        }



        @Override
        protected void onDraw(Canvas canvas)
        {
            Log.i("redraw", "redrawing compass");
            Location l = getCurrentLocation();
            float distance = l.distanceTo(target);
            float rotation = l.bearingTo(target);
            
            canvas.drawARGB(255, 255, 255, 255);
            
            Paint p = new Paint();
            p.setARGB(255, 0, 0, 0);
            canvas.drawText("d=" + distance, 10F, 10F, p);
            canvas.drawText("o=" + rotation, 10F, 20F, p);

            Rect r = largestCenteredRectangle();
            ring.setBounds(r);
            ring.draw(canvas);
            
            canvas.rotate(rotation, this.getWidth()/2, this.getHeight()/2);
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
}