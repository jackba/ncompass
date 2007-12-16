package info.nymble.ncompass;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

public class RecentListActivity extends ListActivity
{
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setDefaultKeyMode(SHORTCUT_DEFAULT_KEYS);

       
        String[] columns = new String[]{Recent.ID, Recent.CREATED};
        Cursor c = managedQuery(Recent.CONTENT_URI, columns, null, null);
        

        ListAdapter adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1, c,
                new String[] {Recent.CREATED}, new int[] {android.R.id.text1});
        setListAdapter(adapter);
    }
}
