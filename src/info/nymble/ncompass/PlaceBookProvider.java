package info.nymble.ncompass;


import info.nymble.ncompass.PlaceBook.Intents;
import info.nymble.ncompass.PlaceBook.Lists;
import info.nymble.ncompass.PlaceBook.Places;
import android.content.ContentProvider;
import android.content.ContentProviderInfo;
import android.content.ContentURIParser;
import android.content.ContentValues;
import android.content.Context;
import android.content.QueryBuilder;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.ContentURI;
import android.util.Log;



public class PlaceBookProvider  extends ContentProvider
{
    public static final ContentURI CONTENT_URI = PlaceBook.CONTENT_URI;

    private LocationTracker location = null;
    private ContentURIParser parser = null;   
    private SQLiteDatabase conn = null;
    
    
    
    
    public PlaceBookProvider()
    {
        parser = new ContentURIParser(ContentURIParser.NO_MATCH);
        parser.addURI(CONTENT_URI.getAuthority(), Places.PLACES_PATH, 0);
        parser.addURI(CONTENT_URI.getAuthority(), Places.PLACES_PATH + "/#", 1);
        parser.addURI(CONTENT_URI.getAuthority(), Lists.LISTS_PATH, 2);
        parser.addURI(CONTENT_URI.getAuthority(), Lists.LISTS_PATH + "/#", 3);
        parser.addURI(CONTENT_URI.getAuthority(), Intents.INTENTS_PATH, 4);
        parser.addURI(CONTENT_URI.getAuthority(), Intents.INTENTS_PATH + "/#", 5);
    }
    
    
    @Override
    public String getType(ContentURI uri)
    {
        switch (parser.match(uri))
        {
            case 0:
                return PlaceBook.MIME_DIRECTORY + PlaceBook.MIME_BASE + Places.PLACES_PATH;
            case 1:
                return PlaceBook.MIME_ITEM + PlaceBook.MIME_BASE + Places.PLACES_PATH;
            case 2:
                return PlaceBook.MIME_DIRECTORY + PlaceBook.MIME_BASE + Lists.LISTS_PATH;
            case 3:
                return PlaceBook.MIME_ITEM + PlaceBook.MIME_BASE + Lists.LISTS_PATH;
            case 4:
                return PlaceBook.MIME_DIRECTORY + PlaceBook.MIME_BASE + Intents.INTENTS_PATH;
            case 5:
                return PlaceBook.MIME_ITEM + PlaceBook.MIME_BASE + Intents.INTENTS_PATH;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }
    
    
    
    @Override
    public void attachInfo(Context context, ContentProviderInfo info)
    {
        super.attachInfo(context, info);
        location = new LocationTracker(context);
    }
    
    
    @Override
    public boolean onCreate()
    {
        Context c = getContext();
        
        Log.i("Context Info", "" + c.getDataDir());
        Log.i("Context Info", "" + c.getPackageName());
        Log.i("Context Info", "" + c.getPackagePath());

        conn = new PlaceBookDB().openDatabase(c, PlaceBookDB.FILE_NAME, null, PlaceBookDB.VERSION);
        return conn != null;
    }
    


    
    
    
    
    @Override
    public int delete(ContentURI uri, String selection, String[] selectionArgs)
    {
        return 0;
    }


    @Override
    /**
     *  
     */
    public ContentURI insert(ContentURI uri, ContentValues values)
    {
        if (parser.match(uri) != 1)
        {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        else
        {
            values = ensureCompleteValues(values);
            long rowID = insertOrUpdateLocation(uri, values);
            
            if (rowID > 0)
            {
                ContentURI inserted = CONTENT_URI.addId(rowID);
                return inserted;
            }
            else
            {
                throw new SQLException("Failed to insert row into " + uri);
            }
        }
    }

    
    @Override
    public int update(ContentURI uri, ContentValues values, String selection, String[] selectionArgs)
    {
        return 0;
    }

    @Override
    public Cursor query(ContentURI uri, String[] projection, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder)
    {
        QueryBuilder qb = new QueryBuilder();
        int whichURI = parser.match(uri);
        boolean isDirectory = (whichURI % 2 == 0);
        int whichType = whichURI >> 1;

        switch (whichType)
        {
            case 0:
                qb.setTables(PlaceBookDB.SQL_SELECT_PLACES);
                break;
            case 1:
                qb.setTables(PlaceBookDB.SQL_SELECT_LISTS);
                break;
            case 2:
                qb.setTables(PlaceBookDB.SQL_SELECT_INTENTS);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        
        if (!isDirectory) qb.appendWhere("_id=" + uri.getPathSegment(1));

        Cursor c = qb.query(conn, projection, selection, selectionArgs, groupBy, having, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        
        return c;
    }

    
    
    
    /**
     * Makes sure that the content values supplied 
     * contain both a LAT and LON value. If either 
     * is missing, the current lat or lon will be
     * supplied in its place. Also adds a timestamp
     * for date created.
     * 
     * @param values The ContentValues map to be checked
     * @return a reference to a complete ContentValues map
     */
    private ContentValues ensureCompleteValues(ContentValues values)
    {
        if (values == null) values = new ContentValues();
        boolean hasLat = values.containsKey(Places.LAT);
        boolean hasLon = values.containsKey(Places.LON);

        if ( !(hasLat && hasLon) )
        {
            Location l = location.getCurrentLocation();
            
            if (!hasLat) values.put(Places.LAT, l.getLatitude());
            if (!hasLon) values.put(Places.LON, l.getLongitude());
        }
        
        Long created = Long.valueOf(System.currentTimeMillis());
        values.put(Places.CREATED, created);

        return values;
    }
    
    
    private long insertOrUpdateLocation(ContentURI uri, ContentValues values)
    {
//        Cursor c = conn.query(DB.SQL_SELECT_RECENT, DB.EMPTY_ARGS);
        long rowId = 0;
//
//        if (c.first() && c.getDouble(1) == values.getAsDouble(LAT)  && c.getDouble(2) == values.getAsDouble(LON))
//        {
//            c.updateLong(3, values.getAsLong(CREATED));
//            rowId = c.getLong(0);
//        }
//        else
//        {
//            rowId = conn.insert(DB.TABLE, "_empty", values);            
//        }
//        
//        getContext().getContentResolver().notifyChange(uri, null);
//        cleanupOutdatedRecords();
//        
        return rowId;
    }
    
    
    /**
     * Maintains the lists lengths by pruning the list.
     * The list is pruned by eliminating records that 
     * are older than the item positioned at HISTORY_LENGTH
     * in the recent list, when it is ordered by created
     * date.
     */
//    private void cleanupOutdatedRecords()
//    {
//        try
//        {            
//            conn.execSQL("SELECT 1");
//        }
//        catch (SQLException e)
//        {
//            Log.w("Recent Location", "Failed to truncate recent list. List may continue to grow.");
//        }
//    }
    
    
}
