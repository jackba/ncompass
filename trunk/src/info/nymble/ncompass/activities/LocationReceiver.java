package info.nymble.ncompass.activities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import info.nymble.ncompass.PlaceBook;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentReceiver;
import android.location.Location;
import android.net.Uri;
import android.provider.Telephony;
import android.telephony.gsm.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class LocationReceiver extends IntentReceiver
{
	Pattern pattern = Pattern.compile("geo:-?[0-9]+(\\.[0-9]+)*,-?[0-9]+(\\.[0-9]+)*");
	private long listId = -1;
	
	
	
	
	
	@Override
	public void onReceiveIntent(Context context, Intent intent) 
	{
		context.getContentResolver();
        SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);

        for (int i = 0; i < messages.length; i++) {
        	SmsMessage message = messages[i];
        	String from = message.getOriginatingAddress();
        	String title = "from: " + from;
        	String body = message.getMessageBody();
        	Matcher matcher = pattern.matcher(body);
        	
            while (matcher.find()) 
            {
            	try
            	{
            		String address = matcher.group();
            		String[] parts = address.substring(4).split(",");
            		Location location = new Location();
            		Intent compassIntent = new Intent(context, TargetCompassActivity.class);
            		
            		location.setLatitude(Double.parseDouble(parts[0]));
            		location.setLongitude(Double.parseDouble(parts[1]));
            		
            		logLocation(location, context, title, from);
            		compassIntent.putExtra(TargetCompassActivity.PARAM_LOCATION, location);
            		compassIntent.putExtra(TargetCompassActivity.PARAM_TITLE, title);
            		compassIntent.addLaunchFlags(Intent.NEW_TASK_LAUNCH);
            		compassIntent.addLaunchFlags(Intent.MULTIPLE_TASK_LAUNCH);
            		
            		context.startActivity(compassIntent);
            		Toast.makeText(context, "location received from " + from, Toast.LENGTH_SHORT).show();
            	}
            	catch (Exception e)
            	{
            		Log.e("LocationReceiver", "Unable to log received location " + e.getMessage());
            	}
            }
        }
	}
	
	
	
	private void logLocation(Location l, Context context, String title, String contact)
	{
		ContentResolver r = context.getContentResolver();
		setListId(r);
		Uri place = PlaceBook.Places.add(r, l, listId);
    	long placeId = Long.valueOf( place.getLastPathSegment() );

    	PlaceBook.Places.update(r, placeId, title, null, contact);
	}
	
	
	
	private void setListId(ContentResolver r)
	{
		if (listId == -1)
		{
			listId = PlaceBook.Lists.get(r, "received");
		}
		if (listId == -1) 
		{
			Uri uri = PlaceBook.Lists.add(r, "received", 20, true, true);
			listId = Long.parseLong( uri.getLastPathSegment());
		}
	}
	
}
