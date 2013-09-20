package com.staser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class DiscGolfMainActivity extends Activity  {
	
	private final int ADD_TRACK_DIALOG_ID = 0x00;
	private final int DIALOG_CONFIRM_ID = 0x01;
	
	private AddTrackDialog addTrackDialog;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);    
        
        Log.w("DD", "DiscGolfMainActivity started");
        
        Button startNewGameButton = (Button) findViewById(R.id.main_btn_startnewgame);
        startNewGameButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {        		
        		//startActivity( new Intent( "com.staser.TRACKSLIST" ) );
        		showDialog(DIALOG_CONFIRM_ID);
        		}
        	}
        	);
                
        Button resumeGameButton = (Button) findViewById(R.id.main_btn_resumegame);
        resumeGameButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {        		
        		startActivity( new Intent( "com.staser.GOALINFOACTIVITY" ) );
        		}
        	}
        	);        
         
    }
    
    protected Dialog onCreateDialog(int id) {
    	Dialog dialog;
    	AlertDialog.Builder builder;
        switch(id) {
        case DIALOG_CONFIRM_ID:
        	builder = new AlertDialog.Builder(this);
        	builder.setMessage("Are you sure you want to start a new game?")
        	       .setCancelable(false)
        	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	        	   startActivity( new Intent( "com.staser.TRACKSLIST" ) );
        	           }
        	       })
        	       .setNegativeButton("No", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	                dialog.cancel();
        	           }
        	       });
        	dialog = builder.create();
        	break;
        default:
            dialog = null;
            break;
        }
        return dialog;
    }
    
    /*protected Dialog onCreateDialog(int id) {
    	switch (id) {
    	case ADD_TRACK_DIALOG_ID:
    		if(addTrackDialog == null)
    			addTrackDialog = new AddTrackDialog(this);
    		return addTrackDialog;
    	default:
    		break;
    	}
		return null;
    }

	@Override
	public void onDismiss(DialogInterface dialog) {		
		Log.v("DD", "Dialog dismissed");
		
		if(dialog == addTrackDialog) {	
			Log.v("DD", "Add track dialog result: " + addTrackDialog.getDialogResult());
			
			if(addTrackDialog.getDialogResult() == DiscGolfConstantsGlobal.DIALOG_OK) {
				Log.v("DD", "Track name: " + addTrackDialog.getName());
				Log.v("DD", "Number of tracks: " + addTrackDialog.getNumber());
			}
		}
	}*/
}