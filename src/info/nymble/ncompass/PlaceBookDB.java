package info.nymble.ncompass;

import android.content.ContentProviderDatabaseHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class PlaceBookDB extends ContentProviderDatabaseHelper
{
        static final String FILE_NAME = "placebook.db";
        static final int VERSION = 11;
        
        static final String[] EMPTY_ARGS = new String[0];
        
        
        static String SQL_SELECT_PLACES;
        static String SQL_SELECT_LISTS;
        static String SQL_SELECT_INTENTS;
        
        static String SQL_DELETE_CLEANUP;
        
        
        static
        {
            SQL_SELECT_LISTS = "Lists" ;
            SQL_SELECT_INTENTS = "Intents" ;
            
            SQL_SELECT_PLACES = "Places";
            
            
            
            SQL_DELETE_CLEANUP = "DELETE FROM PlaceList " + 
            " WHERE _id < (" +
            " SELECT MIN(_id)" +
            " FROM (" + " SELECT place " +
            " FROM PlaceLists c " +
            " INNER JOIN Lists l " +
            " ON c.list = l._id " +
            " WHERE l._id=? " +
            " ORDER BY c.date " +
            " LIMIT l.capactity ))";
        }
        
        
        
        
        @Override
        public void onCreate(SQLiteDatabase db) 
        {
            String sql = "CREATE TABLE IF NOT EXISTS Places ( " + 
            " _id INTEGER PRIMARY KEY AUTOINCREMENT, lat TEXT NOT NULL, " +
            " lon TEXT NOT NULL, alt TEXT NOT NULL DEFAULT 0, created TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            " title VARCHAR(255), pic VARCHAR(255), contact VARCHAR(255), UNIQUE (lat, lon, alt) );";
            db.execSQL(sql);
            
            sql = "CREATE TABLE IF NOT EXISTS Lists (" +
                " _id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(255) NOT NULL UNIQUE, " +
                " is_sequence BIT DEFAULT 0, is_system BIT DEFAULT 0, " +
                " capacity INTEGER DEFAULT 100  CHECK (capacity >= 0));";
            db.execSQL(sql);
            
            sql = "CREATE TABLE IF NOT EXISTS PlaceLists (" +
            " place INTEGER NOT NULL, list INTEGER NOT NULL, date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            " info VARCHAR(255), PRIMARY KEY (place, list) );";
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
    
}
