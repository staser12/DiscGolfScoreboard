package com.staser;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class TracksList extends ListActivity {
	
	/*String[] mTracks = {
    		"Hervanta",
    		"Nekala",
    		"Epilä"
    		};*/
	
	List<String> mTracks = null;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        
        mTracks = getTracksFromTheDB();
        
        ListView lstView = getListView();
        lstView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        
        setListAdapter(new ArrayAdapter<String>(this,
        		android.R.layout.simple_list_item_single_choice, mTracks));       
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	CreateMenu(menu);
    	return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	return MenuChoice(item);
    }
    
    private void CreateMenu(Menu menu)
    {
    	menu.add(0, 0, 0, R.string.track_a_mnuitem_1);    	
    }
    private boolean MenuChoice(MenuItem item)
    {
    	switch( item.getItemId() ) {	
    	case 0:
    		return true;    		
    	default:
    		return super.onOptionsItemSelected(item);
    	}		    
    }
    
    private List<String> getTracksFromTheDB() {    	
    	List<String> tracksList = new ArrayList<String>(3);
    	DBManager dbManager = DBManager.getDBManager(this);
    	dbManager.open();
    	
    	Cursor cur = dbManager.getAllTracks();
    	
    	if (cur.moveToFirst())
    	{
	    	do {
	    		tracksList.add(cur.getString(1));
	    	} 
	    	while (cur.moveToNext());
    	}
    	
    	dbManager.close();    	
    	return tracksList;
    }
    
    public void onListItemClick(
    		ListView parent, View v, int position, long id)
	{
    	parent.setItemChecked(position, parent.isItemChecked(position));
		Toast.makeText(this, "You have selected " + mTracks.get(position), Toast.LENGTH_SHORT).show();
				
		DBManager dbManager = DBManager.getDBManager(this);
		dbManager.open();		
		
		Cursor cur = dbManager.getTrackByName(mTracks.get(position));
		
		if (cur.moveToFirst())
    	{
			//add new game
			dbManager.insertGame(System.currentTimeMillis(), cur.getInt(0));
			
			//save goals number to the shared prefs
			//save current game id to SharedPrefs
			SharedPreferences discGolfPrefs = getSharedPreferences("discGolfPrefs", MODE_WORLD_READABLE);
	        SharedPreferences.Editor prefsEditor = discGolfPrefs.edit();
	        //Toast.makeText(this, "Goals num " + cur.getInt(2), Toast.LENGTH_SHORT).show();
	        prefsEditor.putInt(DiscGolfConstantsGlobal.CURRENT_GAME_GOALSNUM, cur.getInt(2));
	        
	        //save track id to SharedPrefs
	        prefsEditor.putInt(DiscGolfConstantsGlobal.CURRENT_TRACK_ID, cur.getInt(0));
	        	        
	        prefsEditor.commit();
    	}	
		
		dbManager.close();
		startActivity( new Intent( "com.staser.PLAYERSLIST" ) );
		finish();
	}
}
