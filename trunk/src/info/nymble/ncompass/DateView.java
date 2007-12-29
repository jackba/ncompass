package info.nymble.ncompass;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import android.content.Context;
import android.text.method.InputMethod;
import android.text.method.MovementMethod;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class DateView extends TextView
{

    public DateView(Context context, AttributeSet attrs, Map inflateParams, int defStyle)
    {
        super(context, attrs, inflateParams, defStyle);
        // TODO Auto-generated constructor stub
    }

    public DateView(Context context, AttributeSet attrs, Map inflateParams)
    {
        super(context, attrs, inflateParams);
        // TODO Auto-generated constructor stub
    }

    public DateView(Context context, MovementMethod movement, InputMethod input, TransformationMethod transformation)
    {
        super(context, movement, input, transformation);
        // TODO Auto-generated constructor stub
    }

    public DateView(Context context, MovementMethod movement, InputMethod input)
    {
        super(context, movement, input);
        // TODO Auto-generated constructor stub
    }

    public DateView(Context context)
    {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public DateView(Context context, AttributeSet attrs, Map inflateParams, MovementMethod movement, InputMethod input, TransformationMethod transformation,
            int defStyle)
    {
        super(context, attrs, inflateParams, movement, input, transformation, defStyle);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void setText(CharSequence text, BufferType type)
    {
        try
        {
            
            Log.i("DateText", "Setting the value to " + text);
            
            if (text != null)
            {
                if (1==2) throw new IOException();
                String s = text.toString();
                Log.i("ARE", s);
                long time = Long.parseLong(s);
                SimpleDateFormat format = new SimpleDateFormat();
                text = format.format(new Date(time));
            }
            super.setText(text, type);
        }
        catch (IOException e)
        {
            Log.w("DateText", "Exception "  + e.getMessage());
            
        }
    }
}
