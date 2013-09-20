package com.staser;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

class AddTrackDialog extends Dialog {
	
	private int mDialogResult = DiscGolfConstantsGlobal.DIALOG_CANCEL;
	
	private String mTrackName = "";
	private	int mGoalsNum = 0;

	public AddTrackDialog(Context context) {
		super(context);
		
		setContentView(R.layout.addtrackdialog);
		setTitle(R.string.add_track_d_title);
		//is cancelled with "back"-key
		setCancelable(true);	
		
		setOnDismissListener((OnDismissListener) context);
		
		Button okButton = (Button) findViewById(R.id.add_new_track_d_btn_ok);
		Button cancelButton = (Button) findViewById(R.id.add_new_track_d_btn_cancel);
		
		okButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		mDialogResult = DiscGolfConstantsGlobal.DIALOG_OK;
        		saveValues();
        		dismiss();
        		}
			} );
		
		cancelButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		mDialogResult = DiscGolfConstantsGlobal.DIALOG_CANCEL;
        		dismiss();
        		}
			} );
	}
	
	private void saveValues() {
		EditText trackNameEt = (EditText)findViewById(R.id.add_new_track_d_et_name);
		EditText trackNumberEt = (EditText)findViewById(R.id.add_new_track_d_et_number);
		
		if(trackNameEt.getText().toString() != "")
			mGoalsNum = Integer.parseInt(trackNumberEt.getText().toString());
		if(trackNumberEt.getText().toString() != "")
			mTrackName = trackNameEt.getText().toString();
	}
	
	public int getDialogResult() {
		return mDialogResult;
	}	
	
	public String getName() {
		return mTrackName;		
	}
	
	public int getNumber() {
		return mGoalsNum;		
	}
}