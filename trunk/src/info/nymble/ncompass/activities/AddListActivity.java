package info.nymble.ncompass.activities;

import info.nymble.ncompass.R;
import info.nymble.ncompass.PlaceBook.Lists;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
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

        
        
    }
    
    
    private void addList()
    {    
    	EditText text = (EditText)this.findViewById(R.id.title);
    	String content = text.getText().toString();
        ContentResolver resolver = this.getContentResolver();
        ContentValues values = new ContentValues();

        values.put("name", content);                
        resolver.insert(Lists.LISTS_URI, values);
    }
    
}
