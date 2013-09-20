package com.staser;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class PlayersList extends ListActivity {
	
	private static final int ADD_PLAYER_DIALOG_ID = 0x00;
	private static final int ADD_PLAYER_EMPTY_USERNAME_DIALOG_ID = 0x01;
	private static final int CONTEXTMENU_DELETEITEM = 0x10;	
	
	private List<String> mPlayers = null;
		
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.playerslist);
        
        mPlayers = getPlayersFromTheDB();
                
        ListView lstView = getListView();
        
        lstView.setChoiceMode( ListView.CHOICE_MODE_MULTIPLE );    
        lstView.setOnCreateContextMenuListener( this );
        
        updateList();
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
    	menu.add(0, 1, 1, R.string.players_a_mnuitem_2);
    }
    private boolean MenuChoice(MenuItem item)
    {
    	switch( item.getItemId() ) {	
    	case 0:
    		showDialog( ADD_PLAYER_DIALOG_ID );   		
    		return true;
    	case 1:
    		startGame();   	
    		finish();
    		return true;
    	default:
    		return super.onOptionsItemSelected(item);
    	}		    
    }
    
    protected Dialog onCreateDialog(int id) {
    	switch (id) {
    	case ADD_PLAYER_DIALOG_ID:    		
    		LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
    		View layout = inflater.inflate(R.layout.addplayerdialog,(ViewGroup) findViewById(R.id.add_player_d_layout_root)); 		
    		
	    	return new AlertDialog.Builder(this)	    	
	    	.setTitle(R.string.add_player_d_title)
	    	.setPositiveButton(R.string.common_ok, new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int whichButton)
			    	{			    	
			    	
				    	String name = ((EditText) ((AlertDialog) dialog).findViewById(R.id.add_player_d_et_name)).getText().toString();
				    				    	
				    	if( !name.equals("")) {
				    		mPlayers.add(name);
				    		
				    		DBManager dbManager = DBManager.getDBManager(PlayersList.this);
				    		dbManager.open();
				    		dbManager.insertPlayer(name);
				    		dbManager.close();
				    		updateList();
				    	}
				    	else {
				    		showDialog(ADD_PLAYER_EMPTY_USERNAME_DIALOG_ID);
				    	}  	
			    	}
			    	})
	    	.setNegativeButton(R.string.common_cancel, new DialogInterface.OnClickListener() {
		    	public void onClick(DialogInterface dialog, int whichButton)
		    	{		    	
		    	}
	    		})
	    	.setView(layout)
	    	.create();
    	case ADD_PLAYER_EMPTY_USERNAME_DIALOG_ID: 
    		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
    		// set title
    		alertDialogBuilder.setTitle("@string/players_empty_name_alert_title");
     
    		// set dialog message
    		return alertDialogBuilder
    			.setMessage("@string/players_empty_name_alert_content")
    			.setCancelable(false)
    			.setIcon(android.R.drawable.ic_dialog_alert)
    			.setPositiveButton("@string/common_ok", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog,int id) {
    						
    					}
    				}).create();
    	}
    	return null;
    }
    
    public void onListItemClick(
    		ListView parent, View v, int position, long id)
	{
    	parent.setItemChecked(position, parent.isItemChecked(position));		
	}
    
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
    	AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
    	menu.setHeaderTitle((String)mPlayers.get(info.position));
    	// forbid to remove players for now (need to handle lots of situations where player is used in the other tables)
    	//menu.add(Menu.NONE, CONTEXTMENU_DELETEITEM, 0, R.string.delete_player_d_title);            
    }
    
    public boolean onContextItemSelected(MenuItem item)
    {
    	return ContextMenuChoice( item );
    }
    
    private boolean ContextMenuChoice( MenuItem item )
    {
    	AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
    	
    	switch( item.getItemId() ) {	
    	case CONTEXTMENU_DELETEITEM:    		
    		mPlayers.remove(info.position);    		
    		updateList();
    		return true;    		
    	default:
    		return super.onContextItemSelected(item);
    	}		    
    }
    
    private void updateList() {   	        
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, mPlayers));       
    }
    
    private List<String> getPlayersFromTheDB() {    	
    	List<String> playersList = new ArrayList<String>(3);
    	DBManager dbManager = DBManager.getDBManager(this);
    	
    	dbManager.open();    	
    	Cursor cur = dbManager.getAllPlayers();
    	
    	if (cur.moveToFirst())
    	{
	    	do {
	    		playersList.add(cur.getString(1));
	    	} 
	    	while (cur.moveToNext());
    	}
    	dbManager.close();
    	
    	return playersList;
    }
    
    public void startGame() {
    	ListView lstView = getListView();
    	
    	DBManager dbManager = DBManager.getDBManager(this);
    	dbManager.open();
    	int newGameId = dbManager.getLastInsertedId("games");
    	
    	//save current game id to SharedPrefs
		SharedPreferences discGolfPrefs = getSharedPreferences("discGolfPrefs", MODE_WORLD_READABLE);
        SharedPreferences.Editor prefsEditor = discGolfPrefs.edit();
        prefsEditor.putInt(DiscGolfConstantsGlobal.CURRENT_GAME_ID, newGameId);        
        prefsEditor.commit();
    	
    	SparseBooleanArray selItems = lstView.getCheckedItemPositions();
    	    	
    	for(int i = 0; i < selItems.size(); i++) {
    		if(selItems.valueAt(i) == true) {
    			String addedPlayer = (String)lstView.getAdapter().getItem(selItems.keyAt(i));
    			Cursor cur = dbManager.getPlayerByName(addedPlayer);
    			
    			if(cur.moveToFirst()) {
    				dbManager.insertGameplayer(newGameId, cur.getInt(0));
    			}
    		}
    	}
    	dbManager.close();
    	
    	startActivity( new Intent( "com.staser.GOALINFOACTIVITY" ) );    	
    }
}

