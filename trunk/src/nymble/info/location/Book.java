package nymble.info.location;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.ContentURI;

public class Book extends ContentProvider
{
    
    
    
    @Override
    public int delete(ContentURI uri, String selection, String[] selectionArgs)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getType(ContentURI uri)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ContentURI insert(ContentURI uri, ContentValues values)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean onCreate()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Cursor query(ContentURI uri, String[] projection, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int update(ContentURI uri, ContentValues values, String selection, String[] selectionArgs)
    {
        // TODO Auto-generated method stub
        return 0;
    }

}
