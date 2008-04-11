package info.nymble.ncompass;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.util.Log;


/**
 * Helper class defining the PlaceBookProvider's managed
 * types and gives helper methods for external parties
 * to call to make changes or access the database.
 * 
 * @author Andrew Evenson
 *
 */
public final class PlaceBook
{
    static final Uri CONTENT_URI = Uri.parse("content://info.nymble.ncompass.placebook");
    static final String MIME_DIRECTORY = "vnd.info.nymble.cursor.dir";
    static final String MIME_ITEM = "vnd.info.nymble.cursor.item";
    static final String MIME_BASE = "/ncompass.placebook.";
    
    
    
    public static class Place
    {
    	long id;
    	long created;
    	long updated;
    	Location location;
    	String title;
    	String info;
    	Uri picture;
    	Uri contact;
    }
    
    public static final class Places
    {   
        public static final String PLACES_PATH = "places";
        public static final Uri PLACES_URI = Uri.withAppendedPath(CONTENT_URI, PLACES_PATH);
        
        
        public static final String ID = "_id";
        public static final String LAT = "lat";
        public static final String LON = "lon";
        public static final String ALT = "alt";
        public static final String CREATED = "created";
        
        public static final String TITLE = "title";
        public static final String PICTURE = "picture";
        public static final String CONTACT = "contact";
        
        public static final String LIST = "list";        
        public static final String UPDATED = "updated";
        public static final String INFO = "info";
        
        private static final String DEFAULT_ORDER = "list ASC, updated DESC";
        
        
        public static Uri add(ContentResolver resolver, Location location, long listId)
        {
        	return add(resolver, location, listId, null);
        }
        
        public static Uri add(ContentResolver resolver, Location location, long listId, String info)
        {
			ContentValues values = new ContentValues();
			
			values.put(Places.LAT, location.getLatitude());
			values.put(Places.LON, location.getLongitude());
			if (location.hasAltitude()) values.put(Places.ALT, location.getAltitude());
			if (info != null) values.put(Places.INFO, info);
			values.put(Places.LIST, listId);
			
			return resolver.insert(Places.PLACES_URI, values);
        }

        public static void delete(ContentResolver resolver, long id)
        {
        	resolver.delete(Uri.withAppendedPath(PLACES_URI, String.valueOf(id)), null, null);
        }

        public static void update(ContentResolver resolver, long id, String title, Uri picture, Uri contact)
        {
        	ContentValues values = new ContentValues();
			
			if (title != null) values.put(Places.TITLE, title);
			if (picture != null) values.put(Places.PICTURE, picture.toString());
			if (contact != null) values.put(Places.CONTACT, contact.toString());

			resolver.update(Uri.withAppendedPath(PLACES_URI, String.valueOf(id)), values, null, null);
        }

        public static Cursor get(ContentResolver resolver, long id)
        {
        	return resolver.query(Uri.withAppendedPath(PLACES_URI, String.valueOf(id)), null, null, null, null);
        }
        
        public static Cursor query(ContentResolver resolver, long listId)
        {
        	String list = LIST + "=" + listId;
        	
        	return resolver.query(Places.PLACES_URI, null, list, null, DEFAULT_ORDER);        	
        }
        
        
        
        
        public static Place getPlace(ContentResolver resolver, long id)
        {
        	Cursor c = resolver.query(Uri.withAppendedPath(PLACES_URI, String.valueOf(id)), null, null, null, null);
        	List<Place> l = loadPlacesFromCursor(c);
        	
        	if (l.size()  > 0) return l.get(0);
        	return null;
        }
        
        public static List<Place> getPlaces(ContentResolver resolver, long listId)
        {
        	String list = LIST + "=" + listId;
        	Cursor c = resolver.query(Places.PLACES_URI, null, list, null, DEFAULT_ORDER);
        	List<Place> l = loadPlacesFromCursor(c);

        	return l;
        }
        
        
        
        
        private static List<Place> loadPlacesFromCursor(Cursor c)
        {
        	ArrayList<Place> list = new ArrayList<Place>();
        	int title = c.getColumnIndex( Places.TITLE );
        	int lat = c.getColumnIndex( Places.LAT );
        	int lon = c.getColumnIndex( Places.LON );
        	int alt = c.getColumnIndex( Places.ALT );
        	int idColumn = c.getColumnIndex( Places.ID );
        	int created = c.getColumnIndex( Places.CREATED );
        	int updated = c.getColumnIndex( Places.UPDATED );
        	int picture = c.getColumnIndex( Places.PICTURE );
        	int contact = c.getColumnIndex( Places.CONTACT );
        	
        	while (c.next())
        	{
        		Place p = new Place();
        		Location l = new Location();
        		
        		l.setAltitude(Double.parseDouble( c.getString(alt) ));
        		l.setLatitude(Double.parseDouble( c.getString(lat) ));
        		l.setLongitude(Double.parseDouble( c.getString(lon) ));
        		p.location = l;
        		p.id = Long.parseLong( c.getString(idColumn) );
        		p.title = c.getString(title);
        		p.info = c.getString(title);
        		p.created = Long.parseLong( c.getString(created) );
        		p.updated = Long.parseLong( c.getString(updated) );
        		p.picture = Uri.parse( c.getString(picture) );
        		p.contact = Uri.parse( c.getString(contact) );
        		
        		list.add(p);
        		Log.w(null, "found target " + l.toString() + " entitled " + c.getString(title));
        	}
        	
        	return list;
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * Organization units for places. Each list contains a number
     * (0 to CAPACTITY) of place entries. If a place is not defined
     * in one of the lists, it is not defined at all. 
     * 
     * If the list IS_SYSTEM, then an option to remove the list 
     * will not be presented to the user (not user managed). If the
     * list IS_SEQUENCE, then places twice inserted in succession
     * will be collapsed as a single entry and the most recent 
     * created time will display. However, placing another entry
     * and then re-entering the original location will each produce 
     * new entries. Thus, for two places a, b and an insert sequence 
     * 'aababb' at times '012345', the resulting list will return 
     * 'abab' with times '1235'. 
     * 
     * If the list not IS_SEQUENCE, then it will be treated as a set,
     * making each entry unique. A twice inserted place will always
     * result in that place having only an updated time.
     * 
     * The list will be maintained to be no longer than 
     * the CAPACITY. A cleanup occurs after each 
     * insert bringing the list into compliance with the
     * CAPACITY parameter. This cleanup removes the oldest
     * item in the list.
     */
    public static final class Lists
    {        
        public static final String LISTS_PATH = "lists";
        public static final Uri LISTS_URI = Uri.withAppendedPath(CONTENT_URI, LISTS_PATH);
        
        public static final String ID = "_id";
        public static final String NAME = "name";
        public static final String CAPACITY = "capacity";
        public static final String IS_SEQUENCE = "is_sequence";
        public static final String IS_SYSTEM = "is_system";
        
        
        
        
      
        
        public static Uri add(ContentResolver resolver, String name)
        {
        	return add(resolver, name, 4, false, false);
        }
        
        public static Uri add(ContentResolver resolver, String name, int capacity, boolean isSequence, boolean isSystem)
        {
			ContentValues values = new ContentValues();
			
			values.put(Lists.NAME, name);
			values.put(Lists.CAPACITY, capacity);
			values.put(Lists.IS_SEQUENCE, isSequence);
			values.put(Lists.IS_SYSTEM, isSystem);
			
			return resolver.insert(Lists.LISTS_URI, values);
        }

        
        public static void delete(ContentResolver resolver, long id)
        {
        	resolver.delete(Uri.withAppendedPath(LISTS_URI, String.valueOf(id)), null, null);
        }     
        
        
        public static Cursor get(ContentResolver resolver, long id)
        {
        	return resolver.query(Uri.withAppendedPath(LISTS_URI, String.valueOf(id)), null, null, null, null);
        }
        
        public static long get(ContentResolver resolver, String name)
        {
        	Cursor c = resolver.query(LISTS_URI, null, "name=?", new String[]{name}, null);
        	
        	if (c.first())
        	{
        		return c.getLong(c.getColumnIndex(Lists.ID));
        	}
        	
        	return -1;
        }
        
        
        public static Cursor query(ContentResolver resolver)
        {
        	return resolver.query(LISTS_URI, null, null, null, null);
        }
    }
    
    
    /**
     * Defines those actions/intents that should be displayed anytime
     * a place is being displayed. Putting an entry in this list will 
     * cause all windows which display a place to add a menu option for
     * the entry. When the menu option is clicked, the application will 
     * raise the intent supplied in BRADCAST_INTENT and put extra 
     * information describing the place.
     */
    public static final class Intents
    {        
        public static final String INTENTS_PATH = "intents";
        public static final Uri INTENTS_URI = Uri.withAppendedPath(CONTENT_URI, INTENTS_PATH);
        
        
        public static final String ID = "_id";
        public static final String MENU_NAME = "menu_name";
        public static final String BROADCAST_STRING = "broadcast_string";
    }
}
