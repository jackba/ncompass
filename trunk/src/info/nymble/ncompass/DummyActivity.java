package info.nymble.ncompass;

import android.app.Activity;
import android.os.Bundle;

public class DummyActivity extends Activity
{

    
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        this.setContentView(R.layout.list_new);
    }
}
