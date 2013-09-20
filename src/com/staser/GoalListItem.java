package com.staser;

public class GoalListItem {
	
	public static final int DEFAULT_SCORE = 3;
	
	private String mPlayerName = "";
	//default value is 3
	private int mPlayerScore = DEFAULT_SCORE;
	
	private static final int MIN_SCORE = 0;
	private static final int MAX_SCORE = 100;
	
	public GoalListItem( String playerName, int playerScore ) {	
		mPlayerName = playerName;
		mPlayerScore = playerScore;
	}
	
	public String getPlayerName() {
		return mPlayerName;		
	}
	
	public int getPlayerScore() {
		return mPlayerScore;		
	}
	
	public void setPlayerName( String playerName ) {
		mPlayerName = playerName;	
	}
	
	public void setPlayerScore( int playerScore ) {
			
		mPlayerScore = playerScore;
	}
	
	public void incrementScore() {
		if( mPlayerScore + 1 <= MAX_SCORE ) {		
			mPlayerScore++;
		}
	}
	
	public void decrementScore() {
		if( mPlayerScore - 1 >= MIN_SCORE ) {
			mPlayerScore--;
		}
	}
	
}