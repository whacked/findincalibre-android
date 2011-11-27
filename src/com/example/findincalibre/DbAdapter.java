package com.example.findincalibre;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

/**
 * Simple notes database access helper class. Defines the basic CRUD operations
 * for the notepad example, and gives the ability to list all notes as well as
 * retrieve or modify a specific note.
 * 
 * This has been improved from the first version of this tutorial through the
 * addition of better error handling and also using returning a Cursor instead
 * of using a collection of inner classes (which is less scalable and not
 * recommended).
 */
public class DbAdapter {
	
//    public static final String KEY_TITLE = "title";
//    public static final String KEY_BODY = "body";
//    public static final String KEY_ROWID = "_id";
    

    public static final String DATABASE_TABLE = "books";
    public static final String KEY_TITLE = "title";
    public static final String KEY_NAME = "name";
    public static final String KEY_PATH = "path";
    public static final String KEY_AUTHOR = "author_sort";
    public static final String KEY_ROWID = "_id";

    public static final String SQL_PRE = "SELECT " +
      "b.id as _id, b.author_sort as author_sort, b.path as path, b.title as title, " +
      "d.name, d.format, b.pubdate" +
      " FROM data AS d " +
      "LEFT OUTER JOIN books AS b ON d.book = b.id WHERE d.format == 'PDF' ";
    public static String SQL_FILTER = "";
    public static final String SQL_POST = "ORDER BY b.id DESC LIMIT 100";

    private static final String TAG = "DbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    //private static final String DATABASE_NAME = Environment.getExternalStorageDirectory().toString() + "/Calibre Library/metadata.db";
    private static final String DATABASE_NAME = "/mnt/sdcard/ext_sd" + "/Calibre Library/metadata.db";
    private static final int DATABASE_VERSION = 3;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public DbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public DbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public Cursor fetchLatest() {
        return mDb.rawQuery(SQL_PRE + SQL_FILTER + SQL_POST, null);
    }
    
    public void set_filter(String filter) {
    	SQL_FILTER = filter;
    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchNote(long rowId) throws SQLException {

        Cursor mCursor =

            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                    KEY_TITLE, KEY_AUTHOR}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

}