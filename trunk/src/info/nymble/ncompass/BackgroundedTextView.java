package info.nymble.ncompass;

import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.method.InputMethod;
import android.text.method.MovementMethod;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.widget.TextView;

public class BackgroundedTextView extends TextView
{    
    private Drawable backgroundImage = null;
    
    
    @SuppressWarnings("unchecked")
    public BackgroundedTextView(Context context, AttributeSet attrs, Map inflateParams, int defStyle)
    {
        super(context, attrs, inflateParams, defStyle);
    }

    @SuppressWarnings("unchecked")
    public BackgroundedTextView(Context context, AttributeSet attrs, Map inflateParams, MovementMethod movement, InputMethod input,
            TransformationMethod transformation, int defStyle)
    {
        super(context, attrs, inflateParams, movement, input, transformation, defStyle);
    }

    @SuppressWarnings("unchecked")
    public BackgroundedTextView(Context context, AttributeSet attrs, Map inflateParams)
    {
        super(context, attrs, inflateParams);
    }

    public BackgroundedTextView(Context context, MovementMethod movement, InputMethod input, TransformationMethod transformation)
    {
        super(context, movement, input, transformation);
    }

    public BackgroundedTextView(Context context, MovementMethod movement, InputMethod input)
    {
        super(context, movement, input);
    }

    public BackgroundedTextView(Context context)
    {
        super(context);
    }
    

    @Override
    public void setSelected(boolean selected)
    {
        super.setSelected(selected);
        boolean wasSelected = this.isSelected();
        
        if (selected ^ wasSelected)
        {
            if (!selected)
            {
                backgroundImage = getBackground();
                setBackground(null);
            }
            else
            {
                this.setBackground(backgroundImage);
                backgroundImage = null;
            }
            this.forceLayout();
        }
    }
    
    
}
