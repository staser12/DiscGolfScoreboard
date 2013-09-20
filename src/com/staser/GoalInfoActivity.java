package com.staser;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class GoalInfoActivity extends Activity implements OnGestureListener {
	
	private final int DIALOG_CONFIRM_ID = 0x01;

	private ViewFlipper mFlipper;
	private GestureDetector mGestureScanner;
	private ListView mFirstList;
	private ListView mSecondList;
	private int currView = 0;	
	private List<List<GoalListItem>> mListAllViews;
	private List<GoalInfoListAdapter> mListViewAdapters;
	private List<Integer> mCurrentGamePlayerIds; 
	private DBManager mDb;
	private int mCurrentGameId;
	private int mCurrentGameGoalsNum;
	//private int mCurrentTrackId;
	
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.goalinfo_mainview);
    	
    	SharedPreferences discGolfPrefs = getSharedPreferences("discGolfPrefs", MODE_WORLD_READABLE);    	
    	mCurrentGameId = discGolfPrefs.getInt(DiscGolfConstantsGlobal.CURRENT_GAME_ID, 1);
    	//mCurrentTrackId = discGolfPrefs.getInt(DiscGolfConstantsGlobal.CURRENT_TRACK_ID, 1);
    	mCurrentGameGoalsNum = discGolfPrefs.getInt(DiscGolfConstantsGlobal.CURRENT_GAME_GOALSNUM, 9);
    	
    	mDb = DBManager.getDBManager(this);    	
    	mListAllViews = new ArrayList<List<GoalListItem>>();
    	
    	populatePlayersForTheCurrentGame();
    	
    	populateAllListsData();
    	
    	mListViewAdapters = new ArrayList<GoalInfoListAdapter>();
    	initializeAllListViewAdapters();   	
    	
    	mFlipper = (ViewFlipper) findViewById(R.id.flipper);    
    	
    	setTitle("Goal #1");
    	
    	Log.w("DD", "GoalInfoActivity.onCreate");
    	
    	mGestureScanner = new GestureDetector( this );
    	
    	//initialize listview
    	mFirstList = (ListView) findViewById(R.id.goalinfo_listview_first);    	
    	mFirstList.setClickable(true);   	
    	mFirstList.setAdapter(mListViewAdapters.get(currView));
        
        //initialize listview
    	mSecondList = (ListView) findViewById(R.id.goalinfo_listview_second);    	
    	mSecondList.setClickable(true);   	
    	mSecondList.setAdapter(mListViewAdapters.get(currView+1));
    }
    
    // FROM GestureDetector
    @Override
    public boolean onTouchEvent(MotionEvent me)
    {
    	return mGestureScanner.onTouchEvent(me);
    }

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		
		View prevFlipperView = mFlipper.getCurrentView();
		
		if(velocityX > 0)
		{		
			updatePrevViewAdapter( prevFlipperView.getId() );
			mFlipper.setInAnimation(inFromLeftAnimation());
			mFlipper.setOutAnimation(outToRightAnimation());			
			mFlipper.showPrevious();   
		}
		else
		{
			updateNextViewAdapter( prevFlipperView.getId() );
			mFlipper.setInAnimation(inFromRightAnimation());
			mFlipper.setOutAnimation(outToLeftAnimation());
			mFlipper.showNext();   
		}		
		
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {	
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}
    
    
    // ANIMATIONS
    private Animation inFromRightAnimation() {
		Animation inFromRight = new TranslateAnimation(
			Animation.RELATIVE_TO_PARENT,  +1.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
			Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
			);
		inFromRight.setDuration(500);
		inFromRight.setInterpolator(new AccelerateInterpolator());
		return inFromRight;
	}
    
    private Animation outToLeftAnimation() {
    	Animation outtoLeft = new TranslateAnimation(
	    	Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  -1.0f,
	    	Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
	    	);
    	outtoLeft.setDuration(500);
    	outtoLeft.setInterpolator(new AccelerateInterpolator());
    	return outtoLeft;
    }
    
    private Animation inFromLeftAnimation() {
    	Animation inFromLeft = new TranslateAnimation(
			Animation.RELATIVE_TO_PARENT,  -1.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
			Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
			);
    	inFromLeft.setDuration(500);
    	inFromLeft.setInterpolator(new AccelerateInterpolator());
    	return inFromLeft;
    }
    
    private Animation outToRightAnimation() {
    	Animation outtoRight = new TranslateAnimation(
    		Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  +1.0f,
    		Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
    		);
    	outtoRight.setDuration(500);
    	outtoRight.setInterpolator(new AccelerateInterpolator());
    	return outtoRight;
    }
    
    private void populateAllListsData() {    	
    	for( int i = 0; i < mCurrentGameGoalsNum; i++) {
    		List<GoalListItem> goalListItems = getDataForGoal( i );        	
        	mListAllViews.add( goalListItems );
    	}    	
    }
    
    private void initializeAllListViewAdapters() {    	
    	for( int i = 0; i < mCurrentGameGoalsNum; i++ ) {
    		GoalInfoListAdapter adapter = new GoalInfoListAdapter(this, mListAllViews.get(i));
    		mListViewAdapters.add( adapter );
    	}
    }
    
    private void updateNextViewAdapter( int prevFlipperViewId ) {
    	saveGoalDataCurrentView(currView);
    	
    	if( currView == mCurrentGameGoalsNum - 1 ) {
    		currView = -1;
    	}
    	
    	switch( prevFlipperViewId ) {   	
	    	case R.id.first:	    		
	    		mSecondList.setAdapter(mListViewAdapters.get(++currView));
	    		break;	    		
	    	case R.id.second:
	    		mFirstList.setAdapter(mListViewAdapters.get(++currView));
	    		break;
	    	default:
	    		break;
    	}
    	
    	setTitle("Goal #" + (currView + 1));
    }
    
    private void updatePrevViewAdapter( int prevFlipperViewId ) {
    	saveGoalDataCurrentView(currView);
    	
    	if( currView == 0 ) {
    		currView = mCurrentGameGoalsNum;
    	}
    	
    	switch( prevFlipperViewId ) {   	
	    	case R.id.first:	    		
	    		mSecondList.setAdapter(mListViewAdapters.get(--currView));
	    		break;	    		
	    	case R.id.second:
	    		mFirstList.setAdapter(mListViewAdapters.get(--currView));		
	    		break;
	    	default:
	    		break;
    	}
    	
    	setTitle("Goal #" + (currView + 1));
    }
    
    // get data for the current goal (players + scores)
    // set default score if no score is found in the DB
    private List<GoalListItem> getDataForGoal( int goalNum ) {
    	List<GoalListItem> goalListItems = new ArrayList<GoalListItem>();    	
    	   	    	
    	mDb.open();  	
    	for(int i = 0; i < mCurrentGamePlayerIds.size(); i++) {    		
    		int currPlayerId = mCurrentGamePlayerIds.get(i);
    		Cursor playerNameCur = mDb.getPlayer(currPlayerId);
    		Cursor tmp = mDb.getItemsGoalPlayer(goalNum, currPlayerId, mCurrentGameId);
    		
    		if( playerNameCur.moveToFirst() && tmp.moveToFirst())    		
        		goalListItems.add(new GoalListItem(playerNameCur.getString(1), tmp.getInt(3)));    		
    		else if(!tmp.moveToFirst())
    	    	goalListItems.add(new GoalListItem(playerNameCur.getString(1), GoalListItem.DEFAULT_SCORE));
    		
    	}    	
    	mDb.close();   	
    	
		return goalListItems;    	
    }   
    
    // save goal records for the current goal/game
    private void saveGoalDataCurrentView( int viewIndex ) {    	   	
    	mDb.open();    	
    	mDb.deleteScorerecordsForGoalGame(mCurrentGameId, viewIndex);
       	
    	if(viewIndex < mListViewAdapters.size()) {
    		List<GoalListItem> goalList = mListViewAdapters.get(viewIndex).getList();   		
    		
    		for(int j = 0; j < goalList.size(); j++) {    					
    			Cursor playerCur = mDb.getPlayerByName(goalList.get(j).getPlayerName());
    			int playerId = -1;
    			
    			if(playerCur.moveToFirst()) {
    				playerId = playerCur.getInt(0);
    			}
    			
    			if(playerId != -1)
    				mDb.insertScorerecord(goalList.get(j).getPlayerScore(), mCurrentGameId, playerId, viewIndex);
    		}
    	}
    	
    	mDb.close();
    }
    
    
    protected void onDestroy () {
    	//this.saveGoalData();
    	saveGoalDataCurrentView(currView);
    	super.onDestroy();    	
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
    	menu.add(0, 0, 0, R.string.goal_info_listitem_mnu_reset);    	
    	menu.add(0, 1, 1, R.string.goal_info_listitem_mnu_summary);
    	menu.add(0, 2, 2, R.string.goal_info_listitem_mnu_trackrecords);
    }
    
    private boolean MenuChoice(MenuItem item)
    {
    	switch( item.getItemId() ) {	
    	case 0:    		   
    		showDialog(DIALOG_CONFIRM_ID);
    		return true;   
    	case 1:
    		saveGoalDataCurrentView(currView);
    		startActivity( new Intent( "com.staser.GOALSUMMARYACTIVITY" ) );
    		return true;
    	case 2:    		
    		saveGoalDataCurrentView(currView);
    		startActivity( new Intent( "com.staser.TRACKRECORDSCURRENTPLAYERSACTIVITY" ) );
    		return true;
    	default:
    		return super.onOptionsItemSelected(item);
    	}		    
    }
    
    private void resetDataAdapters() {
    	for(int i = 0; i < mListViewAdapters.size(); i++) {
    		List<GoalListItem> goalList = mListViewAdapters.get(i).getList();
    		    		
    		for(int j = 0; j < goalList.size(); j++) {
    			goalList.get(j).setPlayerScore(GoalListItem.DEFAULT_SCORE);		
    		}
    		
    		//notifyChanged will be called automatically for other adaptors when view is changed
    		if(i == currView)
    			mListViewAdapters.get(i).notifyDataSetChanged();
    	}
    }
    
    private void populatePlayersForTheCurrentGame() {
    	
    	DBManager dbManager = DBManager.getDBManager(this);
    	dbManager.open();
    	Cursor cur = dbManager.getPlayersForGame(mCurrentGameId);
    	
    	mCurrentGamePlayerIds = new ArrayList<Integer>(3);
    	
    	if(cur.moveToFirst()) {
    		do {
    			mCurrentGamePlayerIds.add(cur.getInt(2));
    		}
    		while(cur.moveToNext());
    	}
    	
    	dbManager.close();
    	
    }
    
    private void resetAllCurrentScores() {
    	mDb.open();
		mDb.deleteScorerecordsForGame(mCurrentGameId);
		resetDataAdapters();
		mDb.close(); 
    }
    
    protected Dialog onCreateDialog(int id) {
    	Dialog dialog;
    	AlertDialog.Builder builder;
        switch(id) {
        case DIALOG_CONFIRM_ID:
        	builder = new AlertDialog.Builder(this);
        	builder.setMessage("Are you sure you want to reset the scores?")
        	       .setCancelable(false)
        	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	        	   resetAllCurrentScores();
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
}


