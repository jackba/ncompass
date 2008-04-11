package info.nymble.ncompass.activities;

import info.nymble.ncompass.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class InputFieldActivity extends Activity
{

    
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        this.setContentView(R.layout.input_field);
        
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
    }
    
    
    private void done()
    {    
    	EditText text = (EditText)this.findViewById(R.id.content);
    	String content = text.getText().toString();

        this.setResult(Activity.RESULT_OK, content);
        this.finish();
    }
    
    private void cancel()
    {    
        this.setResult(Activity.RESULT_CANCELED);
        this.finish();
    }
}
