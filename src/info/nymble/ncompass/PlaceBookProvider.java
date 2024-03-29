package info.nymble.ncompass;


import info.nymble.ncompass.PlaceBook.Intents;
import info.nymble.ncompass.PlaceBook.Lists;
import info.nymble.ncompass.PlaceBook.Places;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;



public class PlaceBookProvider  extends ContentProvider
{
    public static final Uri CONTENT_URI = PlaceBook.CONTENT_URI;

    private HashMap<Integer, Handler> handlers = new HashMap<Integer, Handler>();
    private UriMatcher parser = null;   
    private SQLiteDatabase conn = null;
    
    private ContentResolver resolver;
    
    public PlaceBookProvider()
    {
        parser = new UriMatcher(UriMatcher.NO_MATCH);
        parser.addURI(CONTENT_URI.getAuthority(), Places.PLACES_PATH, 0);
        parser.addURI(CONTENT_URI.getAuthority(), Places.PLACES_PATH + "/#", 1);
        parser.addURI(CONTENT_URI.getAuthority(), Lists.LISTS_PATH, 2);
        parser.addURI(CONTENT_URI.getAuthority(), Lists.LISTS_PATH + "/#", 3);
        parser.addURI(CONTENT_URI.getAuthority(), Intents.INTENTS_PATH, 4);
        parser.addURI(CONTENT_URI.getAuthority(), Intents.INTENTS_PATH + "/#", 5);
        
        handlers.put(0, new PlacesHandler());
        handlers.put(1, new PlaceHandler());
        handlers.put(2, new ListsHandler());
        handlers.put(3, new ListHandler());
    }
    
    
    
    
    
    
    @Override
    public String getType(Uri uri)
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
    public boolean onCreate()
    {
        Context c = getContext();
        
//        Log.i("Context Info", "" + c.getDataDir());
        Log.i("Context Info", "" + c.getPackageName());
        Log.i("Context Info", "" + c.getPackagePath());

        resolver = c.getContentResolver();
        conn = new PlaceBookDB().openDatabase(c, PlaceBookDB.FILE_NAME, null, PlaceBookDB.VERSION);
        return conn != null;
    }
    
    
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
    	return getHandler(uri).delete(ContentUris.parseId(uri));
    }

    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
    	return getHandler(uri).insert(values);
    }
    
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        return getHandler(uri).update(ContentUris.parseId(uri), values);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        return getHandler(uri).query(uri, projection, selection, selectionArgs, null, null, sortOrder);
    }


    
    
  
    

    
    

    private Handler getHandler(Uri uri)
    {
    	Handler h = handlers.get(parser.match(uri));
    	
    	if (h != null)
    	{
    		return h;
    	}
    	else
    	{    		
    		throw new IllegalArgumentException("Unknown URI " + uri);
    	}
    }
    
    
    
    
    
    
    class Handler 
    {
    	UnsupportedOperationException e = new UnsupportedOperationException();
    	
    	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder){ throw e; }
    	public Uri insert(ContentValues values){ throw e; }
    	public int delete(long id){ throw e; }
    	public int update(long id, ContentValues values){ throw e; }
    }
    
    
    class PlacesHandler extends Handler
    {
    	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder)
    	{ 
    		return conn.query(PlaceBookDB.SQL_PLACES_TABLE, projection, selection, selectionArgs, groupBy, having, sortOrder);
    	}
    	
		public Uri insert(ContentValues values) 
		{
			return Uri.withAppendedPath(Places.PLACES_URI, String.valueOf(insertOrUpdateLocation(values)));
		}

	    
	    private long insertOrUpdateLocation(ContentValues values)
	    {
	    	if (!values.containsKey(Places.LAT)) throw new IllegalArgumentException(Places.LAT + " argument missing");
	    	if (!values.containsKey(Places.LON)) throw new IllegalArgumentException(Places.LON + " argument missing");
	    	if (!values.containsKey(Places.LIST)) throw new IllegalArgumentException(Places.LIST + " argument missing");
	    	if (!values.containsKey(Places.ALT)) values.put(Places.ALT, 0);    	
	    	if (!values.containsKey(Places.INFO)) values.put(Places.INFO, "");    
	    	
	        long placeId = getPlace(values.getAsDouble(Places.LAT), values.getAsDouble(Places.LON), values.getAsDouble(Places.ALT));
	        long listId = values.getAsLong(Places.LIST);
	        long placeList = getPlaceListEntry(listId, placeId, values.getAsString(Places.INFO));

	        resolver.notifyChange(Uri.withAppendedPath(Lists.LISTS_URI, String.valueOf(listId)), null);
	        return placeList;
	    }
	    
	    
	    private long getPlace(double lat, double lon, double alt)
	    {
	        String[] args = new String[]{String.valueOf(lat), String.valueOf(lon), String.valueOf(alt)};
	        Cursor c = conn.rawQuery(PlaceBookDB.SQL_PLACES_SELECT_PLACE, args);
	        long id = -1;
	        
	        if (c.first())
	        {
	            id = c.getLong(0);
	        }
	        else
	        {
	        	ContentValues values = new ContentValues();
	        	
	        	values.put(Places.LAT, String.valueOf(lat));
	        	values.put(Places.LON, String.valueOf(lon));
	        	values.put(Places.ALT, String.valueOf(alt));
	        	values.put(Places.CREATED, String.valueOf(System.currentTimeMillis()));
	        	
	            id = conn.insert("Places", "_empty", values); 
	        }
	        
	        PlaceBookDB.close(c);
	        return id;
	    }
	    
	    
	    private long getPlaceListEntry(long listId, long placeId, String info)
	    {
	    	String[] args = new String[]{String.valueOf(listId), String.valueOf(placeId)};
	        Cursor c = conn.rawQuery(PlaceBookDB.SQL_PLACES_SELECT_ENTRY, args);
	        ContentValues values = new ContentValues();
	        long entryId = -1;
	        
	        values.put("date", System.currentTimeMillis());
	        if (c.first())
	        {
	        	entryId = c.getLong(0);
	        	conn.update("PlaceLists", values, "_id=" + entryId, null);
	        }
	        else
	        {
	        	values.put("place", placeId);
	        	values.put("list", listId);
	        	values.put("info", info);
	        	
	        	entryId = conn.insert("PlaceLists", "_empty", values);    
	            compactPlaceLists(listId);
	        }
	        
	        PlaceBookDB.close(c);
	        return entryId;
	    }
	    
	    
	    private void compactPlaceLists(long listId)
	    {
	    	String[] args = new String[]{String.valueOf(listId)};
	    	Cursor c = conn.rawQuery(PlaceBookDB.SQL_PLACES_COMPACT, args);

	    	if (c.first())
	    	{
	    		args = new String[]{args[0], c.getString(1), c.getString(1), c.getString(0)};
	    		conn.delete("PlaceLists", "list=? AND (date<? OR (date=? AND _id<=?))", args);
				conn.execSQL(PlaceBookDB.SQL_PLACES_CLEANUP);
	    	}
	    	
	    	PlaceBookDB.close(c);
	    }
    }
    
    
    class PlaceHandler extends Handler
    {
		public int delete(long id) 
		{
			long listId = findList(id);
			int affected = conn.delete("PlaceLists", "_id=" + id, PlaceBookDB.EMPTY_ARGS);
			
			if (affected > 0)
			{
				conn.execSQL(PlaceBookDB.SQL_PLACES_CLEANUP);
				resolver.notifyChange(Uri.withAppendedPath(Lists.LISTS_URI, String.valueOf(listId)), null);
			}
			
			return affected;
		}

		public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder)
    	{ 
			return conn.query(PlaceBookDB.SQL_PLACES_TABLE, projection, "_id=" + ContentUris.parseId(uri), null, null, null, null);
		}

		public int update(long id, ContentValues values) 
		{
			Cursor c = query(Uri.withAppendedPath(Places.PLACES_URI, String.valueOf(id)), new String[]{"place"}, null, null, null, null, null);
			long listId = findList(id);
			int affected = 0;
			
			if (c.first())
			{
				affected = conn.update("Places", values, "_id=" + c.getLong(0), null);
				resolver.notifyChange(Uri.withAppendedPath(Lists.LISTS_URI, String.valueOf(listId)), null);
			}
			
			PlaceBookDB.close(c);
			return affected;
		}
		
		private long findList(long placeId)
		{
			Cursor c = conn.rawQuery(PlaceBookDB.SQL_PLACES_LIST, new String[]{String.valueOf(placeId)});
			long id = -1;
			
			if (c.first())
			{
				id = c.getLong(0);
			}
			
			PlaceBookDB.close(c);
			return id;
		}
    }
    
    
    class ListsHandler extends Handler
    {
    	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder)
    	{ 
    		return conn.query(PlaceBookDB.SQL_LISTS_TABLE, projection, selection, selectionArgs, groupBy, having, sortOrder);
    	}
    	
		public Uri insert(ContentValues values) 
		{
			Uri uri = Uri.withAppendedPath(Lists.LISTS_URI, String.valueOf(conn.insert("Lists", "_empty", values)));
			
			resolver.notifyChange(Lists.LISTS_URI, null);
			return uri;
		}
    }
    
    
    class ListHandler extends Handler
    {
		public int delete(long id) 
		{
			int affected = conn.delete("Lists", "_id=" + id, PlaceBookDB.EMPTY_ARGS);
			
			resolver.notifyChange(Lists.LISTS_URI, null);
			return affected;
		}

		public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder)
    	{ 
			return conn.query(PlaceBookDB.SQL_LISTS_TABLE, projection, "_id=" + ContentUris.parseId(uri), null, null, null, null);
		}
    }
}
