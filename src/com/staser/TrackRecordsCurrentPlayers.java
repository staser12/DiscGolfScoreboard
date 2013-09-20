package com.staser;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

public class TrackRecordsCurrentPlayers extends ListActivity {
	
	private DBManager mDb;
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.goalsummaryview);
    	
    	mDb = DBManager.getDBManager(this);
    	mDb.open();
    	
    	SharedPreferences discGolfPrefs = getSharedPreferences("discGolfPrefs", MODE_WORLD_READABLE);    	
    	int currentGameId = discGolfPrefs.getInt(DiscGolfConstantsGlobal.CURRENT_GAME_ID, 1);    	
    	int currentGameTrackId = discGolfPrefs.getInt(DiscGolfConstantsGlobal.CURRENT_TRACK_ID, 1);
    	Cursor c = mDb.getRecordScoresTrackCurrentPlayers(currentGameId, currentGameTrackId);
    	
    	String[] columns = new String[] {
    			DBManager.PLAYER_NAME,
    			DBManager.ITEM_TOTAL_MIN};
    	int[] views = new int[] {R.id.goalsummary_playername, R.id.goalsummary_playerscore};
    	    	
    	SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.goalsummaryview, c, columns, views);
    	this.setListAdapter(adapter);
    	
    	mDb.close();
    }
}