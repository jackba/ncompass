package info.nymble.ncompass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentProviderDatabaseHelper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class PlaceBookDB extends ContentProviderDatabaseHelper
{
        static final String FILE_NAME = "placebook.db";
        static final int VERSION = 20;
        
        static final String[] EMPTY_ARGS = new String[0];
        
        
        static String SQL_PLACES_TABLE;
        static String SQL_LISTS_TABLE;
        static String SQL_INTENTS_TABLE;
        
        
        
        static String SQL_PLACES_SELECT_PLACE;
        static String SQL_PLACES_SELECT_ENTRY;
        static String SQL_PLACES_COMPACT;
        static String SQL_PLACES_CLEANUP;
        
        static
        {
            SQL_LISTS_TABLE = "Lists" ;
            SQL_INTENTS_TABLE = "Intents" ;
            
            
            
            
            SQL_PLACES_TABLE = "(SELECT c.list AS list, c.place AS place, p.lat AS lat, " +
                " p.lon as lon, p.alt as alt, c.date AS updated, c._id AS _id, p.title as title, p.created as created " + 
                " FROM Places p INNER JOIN PlaceLists c ON p._id=c.place ORDER BY c.list ASC, c.date DESC) AS t1";

            SQL_PLACES_SELECT_PLACE = 	"SELECT _id FROM Places WHERE lat=? AND lon=? AND alt=?";
            
            SQL_PLACES_SELECT_ENTRY = 	"SELECT c1._id AS _id " + 
							            " FROM 	PlaceLists c1 " + 
							            "			INNER JOIN " + 
							            "		Lists l " +
							            "			ON c1.list = l._id " +
							            "			LEFT JOIN " +
							            "		(SELECT c2.list as list, Max(c2.date) as date FROM PlaceLists c2 GROUP BY c2.list) c3 " +
							            "			ON c1.list = c3.list " +
							            " WHERE l._id=? AND c1.place=? AND (NOT l.is_sequence OR c1.date = c3.date)";
            
            SQL_PLACES_COMPACT  = 	"SELECT c1._id, c1.date " +
            						"FROM PlaceLists c1 " +
									"WHERE 	c1.list=? " +
									"  AND (SELECT MAX(capacity) " +
									"		FROM Lists " +
									"		WHERE _id=c1.list) <= " +
									"		(SELECT COUNT(*) " +
									"		FROM PlaceLists c2 " +
									" 		WHERE c2.list=c1.list " +
									"		  AND (c2.date > c1.date " + 
									" 		   OR (c2.date = c1.date AND c2._id > c1._id))) " +
									"ORDER BY c1.date DESC, c1._id DESC ";
            
            SQL_PLACES_CLEANUP = "DELETE FROM Places WHERE _id NOT IN (SELECT place FROM PlaceLists);";
        }

        
        @Override
        public void onCreate(SQLiteDatabase db) 
        {
            String sql = "CREATE TABLE IF NOT EXISTS Places ( " + 
            " _id INTEGER PRIMARY KEY AUTOINCREMENT, lat TEXT NOT NULL, " +
            " lon TEXT NOT NULL, alt TEXT NOT NULL DEFAULT 0, created INTEGER DEFAULT CURRENT_TIMESTAMP, " +
            " title VARCHAR(255), pic VARCHAR(255), contact VARCHAR(255), UNIQUE (lat, lon, alt) );";
            db.execSQL(sql);
            
            sql = "CREATE TABLE IF NOT EXISTS Lists (" +
                " _id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(255) NOT NULL UNIQUE, " +
                " is_sequence BIT DEFAULT 0, is_system BIT DEFAULT 0, " +
                " capacity INTEGER DEFAULT 100  CHECK (capacity >= 0));";
            db.execSQL(sql);
            
            sql = "CREATE TABLE IF NOT EXISTS PlaceLists (" +
            " _id INTEGER PRIMARY KEY AUTOINCREMENT, place INTEGER NOT NULL, list INTEGER NOT NULL, " + 
            " date INTEGER DEFAULT CURRENT_TIMESTAMP, info VARCHAR(255) );";
            db.execSQL(sql);
            
            sql = "CREATE TABLE IF NOT EXISTS Intents (" +
            " _id INTEGER PRIMARY KEY AUTOINCREMENT, menu_name VARCHAR(255) NOT NULL UNIQUE, " +
            " broadcast_string VARCHAR(255) NOT NULL );";
            db.execSQL(sql);
            
            
            
            sql = "INSERT INTO Lists (name, is_sequence, is_system, capacity) VALUES (?,?,?,?)";
            SQLiteStatement e = db.compileStatement(sql);
            
            e.bindString(1, "favorites"); e.bindLong(2, 0); e.bindLong(3, 0); e.bindLong(4, 100); e.execute();
            e.bindString(1, "sent"); e.bindLong(2, 1); e.bindLong(3, 1); e.bindLong(4, 5); e.execute();
            e.bindString(1, "targeted"); e.bindLong(2, 1); e.bindLong(3, 1); e.bindLong(4, 10); e.execute();
            e.bindString(1, "received"); e.bindLong(2, 1); e.bindLong(3, 1); e.bindLong(4, 7); e.execute();
        
            
            sql = "INSERT INTO Places (lat, lon, alt, title) VALUES (?,?,?,?)";
            e = db.compileStatement(sql);
            
            e.bindDouble(1, 41.128415); e.bindDouble(2, 13.863802); e.bindDouble(3, 37.0); e.bindString(4, "italian beach house"); e.execute();
            e.bindDouble(1, 82.255779); e.bindDouble(2, -72.421875); e.bindDouble(3, 0); e.bindString(4, "north pole"); e.execute();
            e.bindDouble(1, 39.924713); e.bindDouble(2, 116.379333); e.bindDouble(3, 0); e.bindString(4, "Beijing"); e.execute();
            e.bindDouble(1, -0.097160); e.bindDouble(2, 34.743662); e.bindDouble(3, 0); e.bindString(4, "kisumu on lake victoria"); e.execute();
            e.bindDouble(1, 55.922265); e.bindDouble(2, -3.177248); e.bindDouble(3, 0); e.bindString(4, "Edinburgh, U"); e.execute();
            e.bindDouble(1, 37.827769); e.bindDouble(2, -122.481862); e.bindDouble(3, 0); e.bindString(4, "Golden Gate Bridge"); e.execute();

            sql = "INSERT INTO PlaceLists (place, list, date) VALUES (1, 1, " + getTimeStamp("2008-01-01 08:55:06") + ") ";
            db.execSQL(sql);
            sql = "INSERT INTO PlaceLists (place, list, date) VALUES (4, 1, " + getTimeStamp("2007-06-06 11:45:06") + ") ";
            db.execSQL(sql);
            sql = "INSERT INTO PlaceLists (place, list, date) VALUES (5, 1, " + getTimeStamp("2007-06-03 19:15:45") + ") ";
            db.execSQL(sql);
            sql = "INSERT INTO PlaceLists (place, list, date) VALUES (6, 1, " + getTimeStamp("2007-06-02 19:42:55") + ") ";
            db.execSQL(sql);
            sql = "INSERT INTO PlaceLists (place, list, date) VALUES (1, 2, " + getTimeStamp("2007-12-24 09:16:32") + ") ";
            db.execSQL(sql);
            sql = "INSERT INTO PlaceLists (place, list, date) VALUES (3, 2, " + getTimeStamp("2007-07-04 04:41:19") + ") ";
            db.execSQL(sql);
            sql = "INSERT INTO PlaceLists (place, list, date) VALUES (2, 2, " + getTimeStamp("2006-03-15 13:19:39") + ") ";
            db.execSQL(sql);
            sql = "INSERT INTO PlaceLists (place, list, date) VALUES (1, 2, " + getTimeStamp("2006-02-20 14:22:20") + ") ";
            db.execSQL(sql);
            sql = "INSERT INTO PlaceLists (place, list, date) VALUES (4, 3, " + getTimeStamp("2007-06-06 11:45:06") + ") ";
            db.execSQL(sql);
            sql = "INSERT INTO PlaceLists (place, list, date) VALUES (5, 3, " + getTimeStamp("2007-06-03 19:15:45") + ") ";
            db.execSQL(sql);
            sql = "INSERT INTO PlaceLists (place, list, date) VALUES (6, 3, " + getTimeStamp("2007-05-28 20:20:28") + ") ";
            db.execSQL(sql);
            sql = "INSERT INTO PlaceLists (place, list, date) VALUES (4, 3, " + getTimeStamp("2007-05-27 11:45:06") + ") ";
            db.execSQL(sql);
            sql = "INSERT INTO PlaceLists (place, list, date) VALUES (1, 3, " + getTimeStamp("2007-04-06 11:45:06") + ") ";
            db.execSQL(sql);
            sql = "INSERT INTO PlaceLists (place, list, date) VALUES (5, 3, " + getTimeStamp("2007-01-01 08:55:06") + ") ";   
            db.execSQL(sql);
            sql = "INSERT INTO PlaceLists (place, list, date) VALUES (4, 3, " + getTimeStamp("2006-06-06 11:45:06") + ") ";
            db.execSQL(sql);
            sql = "INSERT INTO PlaceLists (place, list, date) VALUES (5, 3, " + getTimeStamp("2006-06-03 19:15:45") + ") ";
            db.execSQL(sql);
            sql = "INSERT INTO PlaceLists (place, list, date) VALUES (6, 3, " + getTimeStamp("2006-05-28 20:20:28") + ") ";
            db.execSQL(sql);
            sql = "INSERT INTO PlaceLists (place, list, date) VALUES (4, 3, " + getTimeStamp("2006-05-27 11:45:06") + ") ";
            db.execSQL(sql);
            sql = "INSERT INTO PlaceLists (place, list, date) VALUES (1, 3, " + getTimeStamp("2006-04-06 11:45:06") + ") ";
            db.execSQL(sql);
            sql = "INSERT INTO PlaceLists (place, list, date) VALUES (5, 3, " + getTimeStamp("2006-01-01 08:55:06") + ") ";   
            db.execSQL(sql);
            
            
            printCursor(db.query("SELECT * FROM Places", EMPTY_ARGS), "Places Table");
            printCursor(db.query("SELECT * FROM Lists", EMPTY_ARGS), "Lists Table");
            printCursor(db.query("SELECT * FROM PlaceLists", EMPTY_ARGS), "PlaceLists Table");
        }

        
        
        
        
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("Recent Location Database", "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            
            db.execSQL("DROP TABLE IF EXISTS Places");
            db.execSQL("DROP TABLE IF EXISTS Lists");
            db.execSQL("DROP TABLE IF EXISTS PlaceLists");
            db.execSQL("DROP TABLE IF EXISTS Intents");
            
            onCreate(db);
        }
        
        
        
        
        
        
        
        

        private static long getTimeStamp(String date)
        {
            try
            {
                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date d = f.parse(date);
                
                return d.getTime();
            } catch (ParseException e)
            {
                return 0;
            }
        }
        
        
        public static void printCursor(Cursor cursor)
        {
            printCursor(cursor, "");
        }
        
        
        public static void printCursor(Cursor cursor, String message)
        {
            Log.w("PlaceBookDB", "printing cursor with " + cursor.count() + " records: " + message);
            String[] cols = cursor.getColumnNames();
            StringBuffer buffer = new StringBuffer();

            for (cursor.first(); !cursor.isAfterLast(); cursor.next())
            {
                for (int k = 0; k < cols.length; k++)
                {
                    buffer.append(cols[k]);
                    buffer.append("=");
                    buffer.append(cursor.getString(k));
                    buffer.append(", ");
                }
                
                Log.w(null, buffer.toString());
                buffer.setLength(0);
            }
        }
    
}
