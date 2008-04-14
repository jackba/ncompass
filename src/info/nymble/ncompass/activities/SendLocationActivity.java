package info.nymble.ncompass.activities;


import info.nymble.ncompass.PlaceBook;
import info.nymble.ncompass.R;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.gsm.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;





public class SendLocationActivity extends Activity
{
	public static final String PARAM_ADDRESS = "Address";
	
	
	EditText to;
	EditText message;
	String address;	// cache of geo: uri
	 
	@Override
	protected void onCreate(Bundle icicle) {
	    super.onCreate(icicle);
	    
	
	    this.setContentView(R.layout.send_location);
	
	    to = (EditText)findViewById(R.id.to);
	    message = (EditText)findViewById(R.id.message);
	    
	    
        Button ok = (Button)findViewById(R.id.ok);
        ok.setOnClickListener(
	        new Button.OnClickListener() {
	            public void onClick(View v) {
	                done();
	            }
	        }
	    );
        
        
        Button cancel = (Button)findViewById(R.id.cancel);
        cancel.setOnClickListener(
	        new Button.OnClickListener() {
	            public void onClick(View v) {
	                cancel();
	            }
	        }
        );
        
        setTitle("Send Location To Contact");
        loadValues();
	}

	
	
	private void loadValues()
	{
		Intent intent = getIntent();
		address = intent.getStringExtra("Address");
    	message.setText("\n\n(" + address + ")");
	}
	

	
	


	
	private void done()
	{
		Bundle bundle = new Bundle();
		
		SmsManager sm = SmsManager.getDefault();
		sm.sendTextMessage(to.getText().toString(), null, message.getText().toString(), null, null, null); 
		logSentLocation(getContentResolver(), address);
		
		setResult(Activity.RESULT_OK, null, bundle);
		finish();
	}

	
	
	
	
	private void logSentLocation(ContentResolver r, String address)
	{
		Location t = new Location();
		long listId = PlaceBook.Lists.get(r, "sent");
		String[] parts = address.substring(4).split(",");
		
		if (listId == -1) 
		{
			Uri uri = PlaceBook.Lists.add(r, "sent", 20, true, true);
			listId = Long.parseLong( uri.getLastPathSegment());
		}
		
		t.setLatitude(Double.parseDouble(parts[0]));
		t.setLongitude(Double.parseDouble(parts[1]));
		t.setLongitude(parts.length > 2 ? Double.parseDouble(parts[2]) : 0);
		PlaceBook.Places.add(r, t, listId);
	}
    
    private void cancel()
    {
        this.setResult(Activity.RESULT_CANCELED);
        this.finish();
    }
}
