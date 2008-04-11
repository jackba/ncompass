package info.nymble.ncompass;

import info.nymble.measure.Stopwatch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentReceiver;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.util.Log;



/**
 * Maintains your location. Retrieves and orders the provider
 * listing. If a provider becomes unavailable, will attempt 
 * to find another for the same data. 
 */
public class LocationTracker 
{
	private static final String LOCATION_CHANGED_INTENT = LocationTracker.class.getName();
    private static final long ACCEPTABLE_AGE_THRESHOLD = 125;
    
    private Intent intent = new Intent(LOCATION_CHANGED_INTENT);
    private IntentFilter filter = new IntentFilter(LOCATION_CHANGED_INTENT);
    private LocationUpdateReceiver intentReceiver = new LocationUpdateReceiver();
    private ArrayList<LocationListener> listeners = new ArrayList<LocationListener>();
    private boolean listening = false;
    private boolean started = false;
    
    
    private Context context;
    private LocationManager locationManager;
    private LocationProvider locationProvider;
    
    
    private Location currentLocation = new Location();

    
    
    
    
    
    public LocationTracker(final Context c)
    {
        Stopwatch.start();
        
        context = c;
        locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
        
        List<LocationProvider> list = locationManager.getProviders();
        
        for (int i = 0; i < list.size(); i++)
        {
            locationProvider = list.get(i);
        }
        Log.i(null, "Location providers count=" + list.size());

        Stopwatch.stop("find provider");
    }
    

    public Location getCurrentLocation()
    {
        if (isLocationOutdated() && locationProvider != null)
        {
            Stopwatch.start();
            currentLocation = locationManager.getCurrentLocation(locationProvider.getName());
            Stopwatch.stop("get location");
        }
        
        return currentLocation;
    }

    


    
    
    public void start()
    {
    	started = true;
    	if (!listening && listeners.size() > 0 && locationProvider != null)
    	{
    		Log.i(null, "Registering Location Tracking");
    		context.registerReceiver(intentReceiver, filter);
    		locationManager.requestUpdates(locationProvider, ACCEPTABLE_AGE_THRESHOLD, 1, intent);
    		listening = true;
    	}
    }
    
    public void stop()
    {
    	started = false;
    	if (listening)
    	{
    		try
    		{    			
    			Log.i(null, "Unregistering Location Tracking");
    			listening = false;
    			context.unregisterReceiver(intentReceiver);
    			locationManager.removeUpdates(intent);
    		}
    		catch (Exception e)
    		{
    			Log.w("LocationTracker", "Cleanup failure while unregistering m=" + e.getMessage());
    		}
    	}
    }
    
    public void registerLocationListener(LocationListener listener)
    {
    	listeners.add(listener);
    	if (started && !listening) start();
    }
    
    public void unregisterLocationListener(LocationListener listener)
    {
    	listeners.remove(listener);
    	if (listening && listeners.size() <= 0) stop();
    }
    
    
    
    
    
    
    
    private void notifyObservers(Location l)
    {
		for (Iterator<LocationListener> i = listeners.iterator(); i.hasNext();) 
		{
			i.next().locationChanged(l);
		}
    }
    
    
    private class LocationUpdateReceiver extends IntentReceiver 
    {
        @Override
        public void onReceiveIntent(Context context, Intent intent) 
        {
        	updateLocation(intent); 
        } 
    }

	private void updateLocation(Intent intent) 
	{
		if (intent != null)
		{			
			Location l = (Location)intent.getExtra("location");
			
			if (l != null && isLocationOutdated(l))
			{				
				currentLocation = l;
				notifyObservers(l);
			}
		}
	}

    private boolean isLocationOutdated()
    {
    	return isLocationOutdated(null);
    }
    
    private boolean isLocationOutdated(Location newLocation)
    {
    	long time = (newLocation != null ? newLocation.getTime() : System.currentTimeMillis());
    	
    	return currentLocation == null || time - currentLocation.getTime() > ACCEPTABLE_AGE_THRESHOLD;
    }

//    private void logProvider(LocationProvider p)
//    {
//        Log.i("NCompass Logger", "name=" + p.getName());
//        Log.i("NCompass Logger", "getPowerRequirement=" + p.getPowerRequirement());
//        Log.i("NCompass Logger", "getPowerRequirement=" + p.getPowerRequirement());
//        Log.i("NCompass Logger", "hasMonetaryCost=" + p.hasMonetaryCost());
//        Log.i("NCompass Logger", "requiresCell=" + p.requiresCell());
//        Log.i("NCompass Logger", "requiresNetwork=" + p.requiresNetwork());
//        Log.i("NCompass Logger", "requiresSatellite=" + p.requiresSatellite());
//        Log.i("NCompass Logger", "supportsAltitude=" + p.supportsAltitude());
//        Log.i("NCompass Logger", "supportsBearing=" + p.supportsBearing());
//        Log.i("NCompass Logger", "supportsSpeed=" + p.supportsSpeed());
//    }
//
//    private void logLocation(Location l)
//    {
//        Log.i("NCompass Location Logger", "getLatitude=" + l.getLatitude());
//        Log.i("NCompass Location Logger", "getLongitude=" + l.getLongitude());
//        Log.i("NCompass Location Logger", "getBearing=" + l.getBearing());
//        Log.i("NCompass Location Logger", "getAltitude=" + l.getAltitude());
//        Log.i("NCompass Location Logger", "getSpeed=" + l.getSpeed());
//        Log.i("NCompass Location Logger", "getTime=" + l.getTime());
//    }
}
