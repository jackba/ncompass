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
        int whichURI = parser.match(uri);
        int whichType = whichURI >> 1;
        long rowId = -1;
        
        switch (whichType)
        {
            case 0:
                insertOrUpdateLocation(uri, values);
                break;
            case 1:
                rowId = conn.insert("Lists", "_empty", values);
                break;
            case 2:
                rowId = conn.insert("Intents", "_empty", values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        
        if (rowId > 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
            return CONTENT_URI.addId(rowId);
        }
        else
        {
            throw new SQLException("Failed to insert row into " + uri);
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
        boolean hasAlt = values.containsKey(Places.ALT);

        if ( !(hasLat && hasLon && hasAlt) )
        {
            Location l = location.getCurrentLocation();
            
            if (!hasLat) values.put(Places.LAT, l.getLatitude());
            if (!hasLon) values.put(Places.LON, l.getLongitude());
            if (!hasLon) values.put(Places.ALT, l.getAltitude());
        }
        
        Long updated = Long.valueOf(System.currentTimeMillis());
        values.put(Places.CREATED, updated);
        values.put("date", updated);

        return values;
    }
    
    
    private long insertOrUpdateLocation(ContentURI uri, ContentValues values)
    {
        ensureCompleteValues(values);
        long placeId = getPlace(values);
        long listId = getList(values);
        long placeList = -1;
        
        String[] args = new String[]{String.valueOf(listId), String.valueOf(placeId)};
        String sql =    " SELECT c._id AS _id, c.place AS place_id " + 
                        " FROM PlaceLists c INNER JOIN Lists l ON c.list = l._id " +
                        " WHERE l._id=? AND ( l.is_sequence OR c.place=? ) " +
                        " ORDER BY c._id DESC " + 
                        " LIMIT 1 ";
        Cursor c = conn.query(sql, args);
        
        if (c.first() && c.getLong(1) == placeId)
        {
            placeList = c.getLong(0);
        }
        else
        {
            values.put("place", placeId);
            values.put("list", listId);
            placeList = conn.insert("PlaceLists", "_empty", values);
            
            // TODO truncate list to appropriate length
        }
        
        return placeList;
    }
    
    
    private long getPlace(ContentValues values)
    {
        String[] args = {values.getAsString(Places.LAT), values.getAsString(Places.LON), values.getAsString(Places.ALT)};
        Cursor c = conn.query("SELECT _id FROM Places WHERE lat=? AND lon=? AND alt=?", args);

        if (c.first())
        {
            return c.getLong(0);
        }
        else
        {
            return conn.insert("Places", "_empty", values);            
        }   
    }

    
    private long getList(ContentValues values)
    {
        String[] args = {values.getAsString(Lists.NAME)};
        Cursor c = conn.query("SELECT _id FROM Lists WHERE name=?", args);

        if (c.first())
        {
            return c.getLong(0);
        }
        else
        {
            return conn.insert("Lists", "_empty", values);            
        }   
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
