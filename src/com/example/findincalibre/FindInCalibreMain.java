package com.example.findincalibre;

import com.example.findincalibre.FindForm;
import com.example.findincalibre.DbAdapter;
import com.example.findincalibre.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.io.File;

public class FindInCalibreMain extends ListActivity
{

    private static final int ACTIVITY_FIND=1;
    private static final int INSERT_ID = Menu.FIRST;
    private DbAdapter mDbHelper;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        
        try {
        	
            mDbHelper = new DbAdapter(this);
            mDbHelper.open();
        	
            fillData();
            registerForContextMenu(getListView());
        }
        catch (SQLException e) {
        	Toast.makeText(this, "no database readable! " + Environment.getExternalStorageDirectory().toString() + "/Calibre Library/metadata.db", Toast.LENGTH_SHORT).show();

        	
        }
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.menu_find);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case INSERT_ID:
                findBook();
                return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }


    private void fillData() {
        // Get all of the rows from the database and create the item list
        Cursor booksCursor = mDbHelper.fetchLatest();
        startManagingCursor(booksCursor);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{DbAdapter.KEY_TITLE, DbAdapter.KEY_AUTHOR};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.bk_title, R.id.bk_author};

        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter books = 
            new SimpleCursorAdapter(this, R.layout.book_row, booksCursor, from, to);
        setListAdapter(books);
    }
    
    private	void findBook() {
    	Intent intent = new Intent(this, FindForm.class);
    	startActivityForResult(intent, ACTIVITY_FIND);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Cursor cur = (Cursor) l.getItemAtPosition(position);
        File file = new File(
        		//Environment.getExternalStorageDirectory(),
        		"/mnt/sdcard/ext_sd/" +
                "Calibre Library"+
                "/"+cur.getString(cur.getColumnIndexOrThrow(DbAdapter.KEY_PATH))+
                "/"+cur.getString(cur.getColumnIndexOrThrow(DbAdapter.KEY_NAME))+
                ".pdf");

        if(!file.exists()) {
        	AlertDialog dialog = new AlertDialog.Builder(this).create();
        	dialog.setMessage("no pdf for this file!");
        	dialog.show();
        } else {
        	Intent intent = new Intent(Intent.ACTION_VIEW);
        	intent.setDataAndType(Uri.fromFile(file), "application/pdf");
        	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            try {
            	startActivity(intent);
            } catch(Exception e) {
            	Toast.makeText(this, "no pdf application available!", Toast.LENGTH_SHORT).show();
            }	
        }
        
        
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        
        Bundle extras = intent.getExtras();
        if(extras.containsKey("filter")) {
//        	AlertDialog dialog = new AlertDialog.Builder(this).create();
//        	dialog.setMessage("filter is: "+extras.getString("filter"));
//        	dialog.show();
        	mDbHelper.set_filter(extras.getString("filter"));
        }
        fillData();
    }
}
