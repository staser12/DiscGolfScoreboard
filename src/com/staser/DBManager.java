package com.staser;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBManager {
	
	private static final String DATABASE_NAME = "DiscGolfScoreboardDB01";
	
	//all tables
	public static final String ITEM_ROWID = "_id";
	
	//tracks	
	public static final String TRACK_NAME = "track_name";
	public static final String NUM_GOALS = "num_goals";
	
	//players
	public static final String PLAYER_NAME = "player_name";
	
	//games
	public static final String GAME_DATE = "date";
	public static final String TRACK_ID = "track_id";
	
	//scorerecords
	public static final String SCORERECORDS_SCORE = "score";
	public static final String SCORERECORDS_GOALNUM = "goalnum";
	public static final String PLAYER_ID = "player_id";
	public static final String GAME_ID = "game_id";
	
	public static final String ITEM_SCORE = "score";	
	public static final String ITEM_TOTAL = "total";
	public static final String ITEM_TOTAL_MIN = "total_min";
		
	private static final String DATABASE_TABLE_PLAYERS_STR = "players";
	private static final String DATABASE_TABLE_TRACKS_STR = "tracks";
	private static final String DATABASE_TABLE_GAMES_STR = "games";
	private static final String DATABASE_TABLE_SCORERECORDS_STR = "scorerecords";
	private static final String DATABASE_TABLE_GAMEPLAYERS_STR = "gameplayers";
	
	private static final int DATABASE_VERSION = 7;
		
	/*private static final String DATABASE_CREATE =
		"create table gamescoreitems (_id integer primary key autoincrement, "
		+ "goalnum integer not null, playername text not null, score integer not null);";*/
	
	private static final String DATABASE_CREATE_TRACKS =
			"create table tracks (_id integer primary key autoincrement, "
			+ "track_name text not null, num_goals integer not null, unique (track_name));";
	
	private static final String DATABASE_CREATE_PLAYERS =
			"create table players (_id integer primary key autoincrement, "
			+ "player_name text not null, unique (player_name) );";
	
	private static final String DATABASE_CREATE_GAMES =
			"create table games (_id integer primary key autoincrement, "
			+ "date integer not null, track_id integer not null, foreign key(track_id) references tracks(_id));";
	
	private static final String DATABASE_CREATE_SCORERECORDS =
			"create table scorerecords (_id integer primary key autoincrement, "
			+ "score integer not null, goalnum integer not null, player_id integer not null, game_id integer not null, " 
			+ "foreign key(player_id) references players(_id), "
			+ "foreign key(game_id) references games(_id));";
	
	private static final String DATABASE_CREATE_GAMEPLAYERS =
			"create table gameplayers (_id integer primary key autoincrement, "
			+ "game_id integer not null, player_id integer not null, " 
			+ "foreign key(game_id) references games(_id), "
			+ "foreign key(player_id) references players(_id));";
	
	private final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
	
	public Object clone()
			throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException();
	}

	private static DBManager ref;
	
	private DBManager(Context ctx)
	{
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}
	
	public static synchronized DBManager getDBManager(Context ctx)
	{
		if (ref == null)        
			ref = new DBManager(ctx);		
		return ref;
	}
	
	//---opens the database---
	public DBManager open() throws SQLException
	{
		db = DBHelper.getWritableDatabase();
		return this;
	}
	
	//	---closes the database---
	public void close()
	{
		DBHelper.close();
	}
	
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++ tracks table functions ++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public long insertTrack( String trackName, int numGoals )
	{
		ContentValues valuesToInsert = new ContentValues();
		valuesToInsert.put(TRACK_NAME, trackName);
		valuesToInsert.put(NUM_GOALS, numGoals);
		return db.insert(DATABASE_TABLE_TRACKS_STR, null, valuesToInsert);
	}	
	
	//---deletes a particular item---
	public boolean deleteTrack( long rowId )
	{
		return db.delete(DATABASE_TABLE_TRACKS_STR, ITEM_ROWID + "=" + rowId, null) > 0;
	}
	
	public boolean deleteAllTracks( )
	{
		return db.delete(DATABASE_TABLE_TRACKS_STR, null, null) > 0;
	}
	
	//---retrieves all items---
	public Cursor getAllTracks()
	{
		return db.query(DATABASE_TABLE_TRACKS_STR, new String[] {ITEM_ROWID, TRACK_NAME, NUM_GOALS}, null, null, null, null, null);
	}
	
	//---retrieves a particular item---
	public Cursor getTrack(long rowId) throws SQLException
	{
		Cursor mCursor = db.query(true, DATABASE_TABLE_TRACKS_STR, new String[] {ITEM_ROWID,
				TRACK_NAME, NUM_GOALS}, ITEM_ROWID + "=" + rowId, null,	null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	public Cursor getTrackByName(String name) throws SQLException
	{
		Cursor mCursor = db.query(true, DATABASE_TABLE_TRACKS_STR, new String[] {ITEM_ROWID,
				TRACK_NAME, NUM_GOALS}, TRACK_NAME + "='" + name + "'", null,	null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++ players table functions ++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public long insertPlayer( String playerName )
	{
		ContentValues valuesToInsert = new ContentValues();
		valuesToInsert.put(PLAYER_NAME, playerName);
		return db.insert(DATABASE_TABLE_PLAYERS_STR, null, valuesToInsert);
	}	
	
	//---deletes a particular item---
	public boolean deletePlayer( long rowId )
	{
		return db.delete(DATABASE_TABLE_PLAYERS_STR, ITEM_ROWID + "=" + rowId, null) > 0;
	}
	
	public boolean deleteAllPlayers( )
	{
		return db.delete(DATABASE_TABLE_PLAYERS_STR, null, null) > 0;
	}
	
	//---retrieves all items---
	public Cursor getAllPlayers()
	{
		return db.query(DATABASE_TABLE_PLAYERS_STR, new String[] {ITEM_ROWID, PLAYER_NAME}, null, null, null, null, null);
	}
	
	//---retrieves a particular item---
	public Cursor getPlayer(long rowId) throws SQLException
	{
		Cursor mCursor = db.query(true, DATABASE_TABLE_PLAYERS_STR, new String[] {ITEM_ROWID,
				PLAYER_NAME}, ITEM_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	public Cursor getPlayerByName(String name) throws SQLException
	{
		Cursor mCursor = db.query(true, DATABASE_TABLE_PLAYERS_STR, new String[] {ITEM_ROWID,
				PLAYER_NAME}, PLAYER_NAME + "='" + name + "'", null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++ games table functions ++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public long insertGame( long date, int trackId )
	{
		ContentValues valuesToInsert = new ContentValues();
		valuesToInsert.put(GAME_DATE, date);
		valuesToInsert.put(TRACK_ID, trackId);
		return db.insert(DATABASE_TABLE_GAMES_STR, null, valuesToInsert);
	}	
	
	//---deletes a particular item---
	public boolean deleteGame( long rowId )
	{
		return db.delete(DATABASE_TABLE_GAMES_STR, ITEM_ROWID + "=" + rowId, null) > 0;
	}
	
	public boolean deleteAllGames( )
	{
		return db.delete(DATABASE_TABLE_GAMES_STR, null, null) > 0;
	}
	
	//---retrieves all items---
	public Cursor getAllGames()
	{
		return db.query(DATABASE_TABLE_GAMES_STR, new String[] {ITEM_ROWID, GAME_DATE, TRACK_ID}, null, null, null, null, null);
	}
	
	//---retrieves a particular item---
	public Cursor getGame(long rowId) throws SQLException
	{
		Cursor mCursor = db.query(true, DATABASE_TABLE_GAMES_STR, new String[] {ITEM_ROWID, GAME_DATE, TRACK_ID}, 
				ITEM_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++ scorerecords table functions ++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public long insertScorerecord( int score, int gameId, int playerId , int goalNum)
	{
		ContentValues valuesToInsert = new ContentValues();
		valuesToInsert.put(SCORERECORDS_SCORE, score);
		valuesToInsert.put(SCORERECORDS_GOALNUM, goalNum);
		valuesToInsert.put(GAME_ID, gameId);
		valuesToInsert.put(PLAYER_ID, playerId);
		return db.insert(DATABASE_TABLE_SCORERECORDS_STR, null, valuesToInsert);
	}	
	
	//---deletes a particular item---
	public boolean deleteScorerecord( long rowId )
	{
		return db.delete(DATABASE_TABLE_SCORERECORDS_STR, ITEM_ROWID + "=" + rowId, null) > 0;
	}
	
	public boolean deleteAllScorerecords( )
	{
		return db.delete(DATABASE_TABLE_SCORERECORDS_STR, null, null) > 0;
	}
	
	public boolean deleteScorerecordsForGoalGame( int gameId, int goalNum ) {
		String whereString = "game_id=" + gameId + " AND " + "goalnum=" + goalNum;
		return db.delete(DATABASE_TABLE_SCORERECORDS_STR, whereString, null) > 0;		
	}
	
	public boolean deleteScorerecordsForGame( int gameId ) {
		String whereString = "game_id=" + gameId;
		return db.delete(DATABASE_TABLE_SCORERECORDS_STR, whereString, null) > 0;		
	}
	
	//---retrieves all items---
	public Cursor getAllScorerecords()
	{
		return db.query(DATABASE_TABLE_SCORERECORDS_STR, new String[] {ITEM_ROWID, SCORERECORDS_SCORE, SCORERECORDS_GOALNUM, GAME_ID, PLAYER_ID},
				null, null, null, null, null);
	}
	
	//---retrieves a particular item---
	public Cursor getScorerecord(long rowId) throws SQLException
	{
		Cursor mCursor = db.query(true, DATABASE_TABLE_SCORERECORDS_STR, new String[] {ITEM_ROWID, SCORERECORDS_SCORE, SCORERECORDS_GOALNUM, GAME_ID, PLAYER_ID}, 
				ITEM_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++ gameplayers table functions ++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public long insertGameplayer( int gameId, int playerId )
	{
		ContentValues valuesToInsert = new ContentValues();		
		valuesToInsert.put(GAME_ID, gameId);
		valuesToInsert.put(PLAYER_ID, playerId);
		return db.insert(DATABASE_TABLE_GAMEPLAYERS_STR, null, valuesToInsert);
	}	
	
	//---deletes a particular item---
	public boolean deleteGameplayer( long rowId )
	{
		return db.delete(DATABASE_TABLE_GAMEPLAYERS_STR, ITEM_ROWID + "=" + rowId, null) > 0;
	}
	
	public boolean DeleteAllGameplayers( )
	{
		return db.delete(DATABASE_TABLE_GAMEPLAYERS_STR, null, null) > 0;
	}
	
	//---retrieves all items---
	public Cursor getAllGameplayers()
	{
		return db.query(DATABASE_TABLE_GAMEPLAYERS_STR, new String[] {ITEM_ROWID, GAME_ID, PLAYER_ID},
				null, null, null, null, null);
	}
	
	//---retrieves a particular item---
	public Cursor getGameplayer(long rowId) throws SQLException
	{
		Cursor mCursor = db.query(true, DATABASE_TABLE_GAMEPLAYERS_STR, new String[] {ITEM_ROWID, GAME_ID, PLAYER_ID}, 
				ITEM_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	public Cursor getPlayersForGame( int gameId ) {
		Cursor mCursor = db.query(true, DATABASE_TABLE_GAMEPLAYERS_STR, new String[] {ITEM_ROWID, GAME_ID, PLAYER_ID}, 
				GAME_ID + "=" + gameId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	
	//current game summary
	public Cursor getGameScores( int gameId ) {
		String q = "SELECT players._id as _id, player_name, sum(scorerecords.score) as total FROM" +
				" scorerecords INNER JOIN players ON scorerecords.player_id = players._id WHERE" +
				" scorerecords.game_id='" + gameId + "'GROUP BY players._id";
		Cursor mCursor = db.rawQuery(q, null);
		
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		
		return mCursor;
	}   
	
	public Cursor getRecordScoresTrackCurrentPlayers( int gameId, int trackId ) {
		
		String q = "SELECT _id, player_name, min(total) AS total_min" +
				" FROM (SELECT players._id as _id, player_name, game_id, sum(scorerecords.score)" +
				" AS total FROM scorerecords INNER JOIN players ON" +  
				" scorerecords.player_id = players._id INNER JOIN games ON scorerecords.game_id = games._id" +
				" AND games.track_id='" + trackId + "' WHERE (SELECT COUNT(player_id) FROM gameplayers WHERE" +
				" gameplayers.game_id='" + gameId + "' AND gameplayers.player_id = scorerecords.player_id ) > 0" +  
				" GROUP BY player_name, game_id) GROUP BY player_name";
		Cursor mCursor = db.rawQuery(q, null);
		
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		
		return mCursor;	
	}
	
	public int getLastInsertedId( String tableName ) {
		String q = "SELECT _id from " + tableName +  " order by ROWID DESC limit 1;";
		Cursor mCursor = db.rawQuery(q, null );
		
		if (mCursor != null) {
			mCursor.moveToFirst();
			return mCursor.getInt(0);
		}
		
		return -1;
	}
	
	//---retrieves items based on goal number and player id and game id---
	public Cursor getItemsGoalPlayer( int goalNum, int playerId, int gameId ) throws SQLException
	{
		Cursor mCursor = db.query(true, DATABASE_TABLE_SCORERECORDS_STR, new String[] {ITEM_ROWID,
				SCORERECORDS_GOALNUM, PLAYER_ID, ITEM_SCORE}, GAME_ID + "='" + gameId + "' AND " + SCORERECORDS_GOALNUM + "='" + goalNum
				+ "' AND " + PLAYER_ID + "='" + playerId + "'",
					null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	/*public Cursor getItemsSummary() throws SQLException
	{				
		Cursor mCursor = db.query(DATABASE_TABLE, new String[] {ITEM_ROWID, ITEM_PLAYERNAME, "sum(score) as total"}, null, 
				null, ITEM_PLAYERNAME, null, null);
		
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		
		return mCursor;
	}
	
	
	//---retrieves items based on goal number and player name---
	public Cursor getItemsGoalPlayer( int goalNum, String playerName ) throws SQLException
	{
		Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] {ITEM_ROWID,
				ITEM_GOALNUM, ITEM_PLAYERNAME, ITEM_SCORE}, ITEM_PLAYERNAME + "='" + playerName + "' AND " + ITEM_GOALNUM + "=" + goalNum,
					null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	//---updates an item based on rowId---
	public boolean updateItem(int rowId, String playername, int score)
	{
		ContentValues args = new ContentValues();
		args.put(ITEM_PLAYERNAME, playername);
		args.put(ITEM_SCORE, score);
		return db.update(DATABASE_TABLE, args, ITEM_ROWID + "=" + rowId, null) > 0;
	}*/
	
	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		DatabaseHelper(Context context)
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
	
		@Override	
		public void onCreate(SQLiteDatabase db)
		{
			try {
				db.execSQL( DATABASE_CREATE_TRACKS );				
				db.execSQL( DATABASE_CREATE_PLAYERS );
				db.execSQL( DATABASE_CREATE_GAMES );
				db.execSQL( DATABASE_CREATE_SCORERECORDS );
				db.execSQL( DATABASE_CREATE_GAMEPLAYERS );
				db.execSQL( "INSERT INTO tracks (track_name, num_goals) VALUES ('Hervanta', 9);" );
				db.execSQL( "INSERT INTO tracks (track_name, num_goals) VALUES ('Nekala', 9);" );
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion )
		{	
			db.execSQL("DROP TABLE IF EXISTS scorerecords");
			db.execSQL("DROP TABLE IF EXISTS games");
			db.execSQL("DROP TABLE IF EXISTS players");
			db.execSQL("DROP TABLE IF EXISTS tracks");
			db.execSQL("DROP TABLE IF EXISTS gameplayers");
			onCreate( db );
		}		
	}
}
