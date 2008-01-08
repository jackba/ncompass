package info.nymble.ncompass;

import java.util.ArrayList;
import java.util.Iterator;

import android.database.ContentObserver;
import android.database.DataSetObserver;

public class ObserverManager
{
    ArrayList<ContentObserver> contentObservers = new ArrayList<ContentObserver>();
    ArrayList<DataSetObserver> datasetObservers = new ArrayList<DataSetObserver>();
    
    
    
    public void onChanged()
    {
//        for (Iterator<ContentObserver> i = contentObservers.iterator(); i.hasNext();)
//        {
//            i.next().onChange(false);
//        }
        
        for (Iterator<DataSetObserver> i = datasetObservers.iterator(); i.hasNext();)
        {
            i.next().onChanged();
        }
    }
    
    
    public void registerContentObserver(ContentObserver o)
    {
        contentObservers.add(o);
    }

    public void registerDataSetObserver(DataSetObserver o)
    {
        datasetObservers.add(o);
    }


    public void unregisterContentObserver(ContentObserver o)
    {
        contentObservers.remove(o);
    }

    public void unregisterDataSetObserver(DataSetObserver o)
    {
        datasetObservers.remove(o);
    }
}
