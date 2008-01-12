package info.nymble.ncompass.view;
import java.util.Map;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;




public class SelectionExpandingLayout extends LinearLayout
{
    public SelectionExpandingLayout(Context context)
    {
        super(context);
    }
    
    
    @SuppressWarnings("unchecked")
    public SelectionExpandingLayout(Context context, AttributeSet attrs, Map inflateParams)
    {
        super(context, attrs, inflateParams);
    }
    
    
    public void setSelected(boolean selected)
    {
        super.setSelected(selected);
        this.getChildAt(1).setVisibility(selected ? View.VISIBLE : View.GONE);
        
        this.requestLayout();
    }
}
