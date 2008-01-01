package info.nymble.ncompass;

import info.nymble.measure.Stopwatch;

import java.util.List;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;



/**
 * Maintains your location. Retrieves and orders the provider
 * listing. If a provider becomes unavailable, will attempt 
 * to find another for the same data. 
 */
public class LocationTracker
{
    static final long ACCEPTABLE_AGE_THRESHOLD = 500;
    
    LocationManager locationManager;
    LocationProvider locationProvider;
    
    Location currentLocation = null;
    long lastCheckedTime = 0;
    
    
    
    public LocationTracker(Context c)
    {
        Stopwatch.start();
        locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
        
        List<LocationProvider> list = locationManager.getProviders();
        
        for (int i = 0; i < list.size(); i++)
        {
            locationProvider = list.get(i);
        }
        
        Stopwatch.stop("find provider");
    }
    

    public Location getCurrentLocation()
    {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastCheckedTime < ACCEPTABLE_AGE_THRESHOLD && locationProvider != null)
        {
            Stopwatch.start();

            currentLocation = locationManager.getCurrentLocation(locationProvider.getName());
            lastCheckedTime = currentTime;
            
            Stopwatch.stop("get location");
        }
        
        return currentLocation;
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
