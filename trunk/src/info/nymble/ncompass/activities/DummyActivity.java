package info.nymble.ncompass.activities;


import info.nymble.ncompass.R;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RadioGroup;

public class DummyActivity extends Activity
{
	
	
	
	 
	@Override
	protected void onCreate(Bundle icicle) {
	    super.onCreate(icicle);
	    
	
	    this.setContentView(R.layout.dummy);

	    
	    RadioGroup g = (RadioGroup)findViewById(R.id.radios);
	    g.check(R.id.silver_needle);

//	    findViewById(R.id.compass_color);
	}


//	private void playMedia()
//	{
//	    try {
//			MediaPlayer mp = MediaPlayer.create(this, R.raw.mph);
//			// mp.prepare();
//			mp.start();
//		} catch (Exception e) {
//			Log.e(null, "bit of a problem " + e.getMessage(), e);
//		}
//	}


}
