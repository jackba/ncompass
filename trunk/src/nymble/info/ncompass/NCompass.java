package nymble.info.ncompass;

import java.util.List;

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
import android.view.MotionEvent;
import android.view.View;

public class NCompass extends Activity
{

    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        List<LocationProvider> list = locationManager.getProviders();

        
        
        for (int i = 0; i < list.size(); i++)
        {
            LocationProvider p = list.get(i);
            Location l = locationManager.getCurrentLocation(p.getName());

            
            logProvider(p);
            logLocation(l);

            // locationManager.requestUpdates(p, 0, 0, null);
        }

        // setContentView(R.layout.main);
        setContentView(new Compass(this));
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

    
    
    
    public class Compass extends View
    {
        private final Paint mPaint;


        public Compass(Context c)
        {
            super(c);
           
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setARGB(127, 255, 255, 255);
        }

//        @Override
//        protected void onSizeChanged(int w, int h, int oldw, int oldh)
//        {
//        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            Log.i("cHeight", "" + canvas.getBitmapHeight());
            Log.i("cWidth", "" + canvas.getBitmapWidth());
            
            canvas.drawPaint(mPaint);
            BitmapDrawable ring = (BitmapDrawable)getResources().getDrawable(R.drawable.compass_ring);
            BitmapDrawable needle = (BitmapDrawable)getResources().getDrawable(R.drawable.compass_needle);
            
            ring.setBounds(largestCenteredRectangle());
            ring.draw(canvas);
            
            canvas.rotate(40.0F);
            needle.setBounds(largestCenteredRectangle());
            needle.draw(canvas);
            
            
//            canvas.drawBitmap(b, 0, 0, b.getPaint());
        }
        
        public Rect largestCenteredRectangle()
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
        
        

        @Override
        public boolean onMotionEvent(MotionEvent event)
        {
//            int action = event.getAction();
//            mCurDown = action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE;
//            mCurX = (int) event.getX();
//            mCurY = (int) event.getY();
            return true;
        }
    }
}