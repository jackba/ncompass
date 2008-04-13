package info.nymble.ncompass.activities;


import info.nymble.ncompass.R;
import android.app.Activity;
import android.content.Intent;
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
    	message.setText("\n\n( " + intent.getStringExtra("Address") + " )");
	}
	

	
	private void done()
	{
		Bundle bundle = new Bundle();
		
		SmsManager sm = SmsManager.getDefault();
		sm.sendTextMessage(to.getText().toString(), null, message.getText().toString(), null, null, null); 
		
		setResult(Activity.RESULT_OK, null, bundle);
		finish();
	}
	

    
    private void cancel()
    {
        this.setResult(Activity.RESULT_CANCELED);
        this.finish();
    }
}
