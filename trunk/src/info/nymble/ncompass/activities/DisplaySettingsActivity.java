package info.nymble.ncompass.activities;


import info.nymble.ncompass.R;
import info.nymble.ncompass.view.TargetCompass;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;





public class DisplaySettingsActivity extends Activity
{
	public static final String DISPLAY_MODE="display.mode";
	public static final String COMPASS_COLOR="compass.color";
	
	
	private RadioGroup displays;
	private RadioGroup colors;
	private EditText colorText;
	
	
	 
	@Override
	protected void onCreate(Bundle icicle) {
	    super.onCreate(icicle);
	    
	
	    this.setContentView(R.layout.display_settings);
	    
		displays = (RadioGroup)findViewById(R.id.options);	    
	    colors = (RadioGroup)findViewById(R.id.colors);
		colorText = (EditText)findViewById(R.id.color_text);


		
	    
	    colors.setOnCheckedChangeListener(new OnCheckedChangeListener()
	    {
	    	EditText colorText = (EditText)findViewById(R.id.color_text);
	    	
			public void onCheckedChanged(RadioGroup group, int checkedItem) {
				if (R.id.other == checkedItem)
				{
					colorText.setVisibility(EditText.VISIBLE);
					colorText.requestFocus();				
				}
				else
				{					
					colorText.setVisibility(EditText.GONE);
				}
			}
		});
	    
        setTitle("Compass Display Settings");
        loadValues();

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

	@Override
	protected void onStart() {
		displays.requestFocus();
		super.onStart();
	}







	private void loadValues()
	{
		Intent intent = getIntent();
//    	String color = Integer.toHexString( intent.getIntExtra(COMPASS_COLOR, 0xFFAA3333));
    	int display = intent.getIntExtra(DISPLAY_MODE, -1);
    	
		colors.check( R.id.orange );
		
		switch (display)
		{
		case -1:
			displays.check(R.id.power_saver);
			break;
		case TargetCompass.SILVER: 
			displays.check(R.id.silver_needle);
			break;
		case TargetCompass.BLACK:
			displays.check(R.id.black_needle);
			break;
		case TargetCompass.WHITE:
			displays.check(R.id.white_needle);
			break;
		}
		
		findViewById(R.id.power_saver).requestFocus();
	}
	
	
	
	
	
	private void done()
	{
    	String color = "";
    	int display = -1;
    	
		if (R.id.other == colors.getCheckedRadioButtonId())
		{
			color = colorText.getText().toString();
		}
		else
		{
			RadioButton b = (RadioButton)findViewById(colors.getCheckedRadioButtonId());
			color = Integer.toHexString( b.getTextColors().getDefaultColor() );
		}
		
		switch (displays.getCheckedRadioButtonId())
		{
		case R.id.power_saver:
			display = -1;
			break;
		case R.id.silver_needle:
			display = TargetCompass.SILVER;
			break;
		case R.id.black_needle:
			display = TargetCompass.BLACK;
			break;
		case R.id.white_needle:
			display = TargetCompass.WHITE;
			break;
		}
		
		returnValues(color, display);
	}
	
	
	
    private void returnValues(String color, int display)
    {    
    	Bundle bundle = new Bundle();
		
    	Log.i("CompassDisplayOptions", "color=" + color + " display=" + display);
    	bundle.putString(COMPASS_COLOR, color);
    	bundle.putInt(DISPLAY_MODE, display);
        
    	setResult(Activity.RESULT_OK, null, bundle);
        finish();
    }
    
    private void cancel()
    {
        this.setResult(Activity.RESULT_CANCELED);
        this.finish();
    }
}
