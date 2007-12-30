package info.nymble.ncompass;

import android.net.ContentURI;


public final class PlaceBook
{
    public static final ContentURI CONTENT_URI = ContentURI.create("content://info.nymble.ncompass.placebook");
    public static final String MIME_DIRECTORY = "vnd.info.nymble.cursor.dir";
    public static final String MIME_ITEM = "vnd.info.nymble.cursor.item";
    public static final String MIME_BASE = "/ncompass.placebook.";
    
    
    
    
    
    public static final class Places
    {   
        public static final String PLACES_PATH = "places";
        public static final ContentURI PLACES_URI = CONTENT_URI.addPath(PLACES_PATH);
        
        
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
        public static final ContentURI LISTS_URI = CONTENT_URI.addPath(LISTS_PATH);
        
        public static final String ID = "_id";
        public static final String NAME = "name";
        public static final String CAPACITY = "capacity";
        public static final String IS_SEQUENCE = "is_sequence";
        public static final String IS_SYSTEM = "is_system";
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
        public static final ContentURI INTENTS_URI = CONTENT_URI.addPath(INTENTS_PATH);
        
        
        public static final String ID = "_id";
        public static final String MENU_NAME = "menu_name";
        public static final String BROADCAST_STRING = "broadcast_string";
    }
}
