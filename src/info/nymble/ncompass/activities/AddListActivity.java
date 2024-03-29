package info.nymble.ncompass.activities;

import info.nymble.ncompass.R;
import info.nymble.ncompass.PlaceBook.Lists;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddListActivity extends Activity
{

    
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        this.setContentView(R.layout.list_new);
        
        Button button = (Button)findViewById(R.id.ok);
        
        button.setOnClickListener(
	        new Button.OnClickListener() {
	            public void onClick(View v) {
	                addList();
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
    
    
    private void addList()
    {    
    	EditText text = (EditText)this.findViewById(R.id.title);
    	String content = text.getText().toString();

        Uri uri = Lists.add(this.getContentResolver(), content);
        this.setResult(Activity.RESULT_OK, uri.toString());
        this.finish();
    }
    
    
    private void cancel()
    {    
        this.setResult(Activity.RESULT_CANCELED, null);
        this.finish();
    }
}
