package info.nymble.ncompass;

import info.nymble.ncompass.PlaceBook.Lists;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Bundle;
import android.widget.Button;

public class AddListActivity extends Activity
{

    
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        this.setContentView(R.layout.list_new);
        
        Button button = (Button)findViewById(R.id.ok);

//        final ContentResolver resolver = this.getContentResolver();
//        ContentValues values = new ContentValues();
//        values.put("name", "Jod's Long Named List");                
//        resolver.insert(Lists.LISTS_URI, values);
        
    }
    
    
    
}
