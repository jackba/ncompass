package info.nymble.ncompass;


import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentProviderDatabaseHelper;
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
import android.text.TextUtils;
import android.util.Log;


/**
 * A list of recent locations of interest. A location
 * is a LAT, LON pair. The recentness of a location is
 * defined by when it was inserted into this provider. 
 * If a given location is inserted twice in succession, 
 * the most recent created time will display. Thus, for
 * two locations a, b and an insert sequence 'aababb'
 * at times '012345', the resulting recent list 
 * will return 'abab' with times '1235'. 
 * 
 * The list will be maintained to be no longer than 
 * the HISTORY_LENGTH. A cleanup occurs after each 
 * insert bringing the list into compliance with the
 * HISTORY_LENGTH parameter. 
 * 
 * @author Andrew Evenson
 *
 */
public class Recent extends ContentProvider
{
    public static final ContentURI CONTENT_URI = ContentURI.create("content://info.nymble.location.Recent/locations");
    
    public static final String ID = "_id";
    public static final String LAT = "lat";
    public static final String LON = "lon";
    public static final String CREATED = "created";
    
    public static final int HISTORY_LENGTH = 5;

    
    private LocationTracker location = null;
    private ContentURIParser parser = null;   
    private SQLiteDatabase conn = null;
    
    
    
    
    public Recent()
    {
        String path = CONTENT_URI.getPath().replaceAll("/", "");
        
        parser = new ContentURIParser(ContentURIParser.NO_MATCH);
        parser.addURI(CONTENT_URI.getAuthority(), path, 1);
        parser.addURI(CONTENT_URI.getAuthority(), path + "/#", 2);
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
               
        
        conn = new DB().openDatabase(c, DB.FILE_NAME, null, DB.VERSION);
        return conn != null;
    }
    

    @Override
    public String getType(ContentURI uri)
    {
        switch (parser.match(uri))
        {
            case 1:
                return "vnd.android.cursor.dir/vnd.info.nymble.location";
            case 2:
                return "vnd.android.cursor.item/vnd.info.nymble.location";
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }
    
    
    
    
    @Override
    public int delete(ContentURI uri, String selection, String[] selectionArgs)
    {
        return 0;
    }


    @Override
    /**
     * Adds a location to the Recent list. 
     * 
     * @param values A ContentValues map containing the
     * location to add. Entries for LAT and LON should 
     * contain data of type double representing the
     * location to be added. 
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

        qb.setTables(DB.TABLE);
        qb.setProjectionMap(DB.COLUMNS);
        if (parser.match(uri) == 2) qb.appendWhere(ID + "=" + uri.getPathSegment(1));
        if (TextUtils.isEmpty(sortOrder)) sortOrder = DB.SORT;

        Cursor c = qb.query(conn, projection, selection, selectionArgs, groupBy,
                having, sortOrder);
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
        boolean hasLat = values.containsKey(LAT);
        boolean hasLon = values.containsKey(LON);

        if ( !(hasLat && hasLon) )
        {
            Location l = location.getCurrentLocation();
            
            if (!hasLat) values.put(LAT, l.getLatitude());
            if (!hasLon) values.put(LON, l.getLongitude());
        }
        
        Long created = Long.valueOf(System.currentTimeMillis());
        values.put(CREATED, created);

        return values;
    }
    
    
    private long insertOrUpdateLocation(ContentURI uri, ContentValues values)
    {
        Cursor c = conn.query(DB.SQL_SELECT_RECENT, DB.EMPTY_ARGS);
        long rowId = 0;

        if (c.first() && c.getDouble(1) == values.getAsDouble(LAT)  && c.getDouble(2) == values.getAsDouble(LON))
        {
            c.updateLong(3, values.getAsLong(CREATED));
            rowId = c.getLong(0);
        }
        else
        {
            rowId = conn.insert(DB.TABLE, "_empty", values);            
        }
        
        getContext().getContentResolver().notifyChange(uri, null);
        cleanupOutdatedRecords();
        
        return rowId;
    }
    
    
    /**
     * Maintains the lists lengths by pruning the list.
     * The list is pruned by eliminating records that 
     * are older than the item positioned at HISTORY_LENGTH
     * in the recent list, when it is ordered by created
     * date.
     */
    private void cleanupOutdatedRecords()
    {
        try
        {            
            conn.execSQL(DB.SQL_DELETE_CLEANUP);
        }
        catch (SQLException e)
        {
            Log.w("Recent Location", "Failed to truncate recent list. List may continue to grow.");
        }
    }
    
    
    
    /**
     * Contains the SQL create and upgrade scripts
     * for the recent locations database table
     */
    private static class DB extends ContentProviderDatabaseHelper 
    {
        static final String FILE_NAME = "locations.db";
        static final int VERSION = 2;
        
        static final String TABLE = "recent";
        static HashMap<String, String> COLUMNS = new HashMap<String, String>();
        static final String SORT = CREATED + " DESC";


        static String[] EMPTY_ARGS = new String[0];
        static String SQL_SELECT_RECENT;
        static String SQL_DELETE_CLEANUP;
        
        
        static
        {
            COLUMNS.put(ID, ID);
            COLUMNS.put(CREATED, CREATED);
            COLUMNS.put(LAT, LAT);
            COLUMNS.put(LON, LON);
            
            SQL_SELECT_RECENT = "SELECT " + ID + ", " + LAT + ", " + LON + ", " + CREATED + 
                                " FROM " + TABLE + 
                                " ORDER BY " + SORT + 
                                " LIMIT 1;";
            
            SQL_DELETE_CLEANUP = "DELETE FROM " + TABLE + 
                                " WHERE " + ID + " < (" +
                                " SELECT MIN(" + ID + ")" +
                                " FROM (" + " SELECT " + ID +
                                " FROM " + TABLE +
                                " ORDER BY " + SORT +
                                " LIMIT " + HISTORY_LENGTH + "))";
            
            Log.i("SQL recent", SQL_SELECT_RECENT);
            Log.i("SQL delete", SQL_DELETE_CLEANUP);
        }
        
        
        
        
        @Override
        public void onCreate(SQLiteDatabase db) 
        {
            db.execSQL("CREATE TABLE " + TABLE + " (" + ID + " INTEGER PRIMARY KEY,"
                    + LAT + " DOUBLE," + LON + " DOUBLE, " + CREATED + " INTEGER" + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("Recent Location Database", "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE);
            onCreate(db);
        }
    }
}
