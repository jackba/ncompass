package info.nymble.ncompass.activities;

import info.nymble.ncompass.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class InputFieldActivity extends Activity
{
	public static final String TITLE = "title";
	public static final String LABEL = "label";
	public static final String DEFAULT = "default";
    
	
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        String title = this.getIntent().getStringExtra(TITLE);
        String label = this.getIntent().getStringExtra(LABEL);
        String dfault = this.getIntent().getStringExtra(DEFAULT);
        
        this.setContentView(R.layout.input_field);
        this.setTitle(title);
        

        
        TextView labelView = (TextView)findViewById(R.id.label);
        labelView.setText(label);
        
        EditText inputView = (EditText)findViewById(R.id.content);
        inputView.setText(dfault);
        
        
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
