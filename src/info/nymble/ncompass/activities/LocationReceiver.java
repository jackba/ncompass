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
        	String title = message.getPseudoSubject();
        	String body = message.getMessageBody();
        	Matcher matcher = pattern.matcher(body);
        	
            while (matcher.find()) 
            {
            	try
            	{            		
            		String[] address = matcher.group().substring(4).split(",");
            		Location location = new Location();
            		
            		location.setLatitude(Double.parseDouble(address[0]));
            		location.setLongitude(Double.parseDouble(address[1]));
            		
            		logLocation(location, context, title, from);
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
		if (listId == -1) listId = PlaceBook.Lists.get(r, "received");
    	Uri place = PlaceBook.Places.add(r, l, listId);
    	long placeId = Long.valueOf( place.getLastPathSegment() );
    	
    	PlaceBook.Places.update(r, placeId, title, null, contact);
    	
    	Toast.makeText(context, "location received " + l.toString() + " from " + contact, Toast.LENGTH_SHORT).show();
	}
}
