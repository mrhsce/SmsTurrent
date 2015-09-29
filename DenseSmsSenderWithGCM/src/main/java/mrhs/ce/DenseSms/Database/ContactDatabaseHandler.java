package mrhs.ce.DenseSms.Database;
import java.util.ArrayList;

import mrhs.ce.DenseSms.Commons;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class ContactDatabaseHandler {	
	private static final String TABLE_NAME = "contactGroups";
	private static final String OPERATION_TABLE_NAME = "operations";
	private static final String STATUS_TABLE_NAME = "status";
	
	private DbHelper dbHelper;
	private  SQLiteDatabase db;
	
	// Necessary functions (DDL)
	public ContactDatabaseHandler(Context ctx){
		dbHelper=new DbHelper(ctx);
	}
	
	public ContactDatabaseHandler open() throws SQLException{
		db=dbHelper.getWritableDatabase();
		return this;
	}
	
	public void close(){
		dbHelper.close();
	}
	
	// DML functions
	
	public boolean insert(String groupName,String phoneNum,String name){
		
		ContentValues values=new ContentValues();
		values.put("groupName", groupName);
		values.put("phoneNum", phoneNum);
		if(name!=null)
			values.put("name", name);
		try{
			return db.insert(TABLE_NAME, null, values)>0;
		}catch(Exception e){
			e.printStackTrace();
			log("Error inserting values to the databse");
			return false;
		}
	}
	
	public boolean delete(String groupName,String phoneNum){
		return db.delete(TABLE_NAME, "groupName = '"+groupName+"' and phoneNum= '"+phoneNum+"'", null)>0;
	}
	public boolean delete(String groupName){
		return db.delete(TABLE_NAME, "groupName = '"+groupName+"'", null)>0;
	}	
	
	public String getName(String groupName,String phoneNum){
		Cursor cursor=db.query(true, TABLE_NAME,
				new String[]{"phoneNum" ,"name"}, "groupName = '"+groupName+"' and phoneNum= '"+phoneNum+"'", null, null, null, " name desc", null);
		if(cursor != null){
			cursor.moveToFirst();
			return cursor.getString(0);
		}return null;
	}
	
	public Cursor getAllPhoneNums(){
		Cursor cursor=db.query(true, TABLE_NAME,
				new String[]{"phoneNum","name"}, null, null, null, null, "name desc", null);
		
		cursor.moveToFirst();
		return cursor;
	}	
	
	public Cursor getAllGroups(){
		Cursor cursor=db.query(true, TABLE_NAME,
				new String[]{"groupName"}, null, null, null, null, "groupName desc", null);
		cursor.moveToFirst();
		return cursor;
	}
	
	public boolean isFilled(){
		Cursor cursor=db.query(true, TABLE_NAME,
				new String[]{"groupName","phoneNum"}, null, null, null, null, "name desc", null);
		return cursor.moveToFirst();
	}
	
	public boolean groupExists(String groupName){
		Cursor cursor=db.query(true, TABLE_NAME,
				new String[]{"phoneNum"}, "groupName = '"+groupName+"'", null, null, null, " name desc", null);		
		return cursor.moveToFirst();
	}
	
	public ArrayList<String> getGroupList(){
		ArrayList<String> list=new ArrayList<String>();
		Cursor cursor=db.query(true, TABLE_NAME,
				new String[]{"groupName"}, null, null, null, null, "groupName desc", null);
		if(cursor.moveToFirst()){			
			do{
				list.add(cursor.getString(0));
			}while(cursor.moveToNext());
		}
			
		return list;
	}
	public ArrayList<String> getPhoneList(String groupName){
		ArrayList<String> list=new ArrayList<String>();
		Cursor cursor=db.query(true, TABLE_NAME,
				new String[]{"phoneNum" ,"name"}, "groupName = '"+
						groupName+"'", null, null, null, " name desc", null);
		if(cursor.moveToFirst()){			
			do{
				list.add(cursor.getString(0));
			}while(cursor.moveToNext());
		}
			
		return list;
	}
	
	public ArrayList<String> getNameList(String groupName){
		ArrayList<String> list=new ArrayList<String>();
		Cursor cursor=db.query(true, TABLE_NAME,
				new String[]{"phoneNum" ,"name"}, "groupName = '"+
						groupName+"'", null, null, null, " name desc", null);
		if(cursor.moveToFirst()){			
			do{
				list.add(cursor.getString(1));
			}while(cursor.moveToNext());
		}
			
		return list;
	}

	private static class DbHelper extends SQLiteOpenHelper{
					
		public DbHelper(Context context){
			super(context, Commons.DATABASE_NAME, null, Commons.DATABASE_VERSION);
			log("The database has been initialized");
		}
	
		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			String CREATE_DATABASE="CREATE TABLE "+TABLE_NAME+ 
					" ( groupName text,phoneNum varchar(13), name text, primary key(groupName,phoneNum))";
			String OPERATION_CREATE_DATABASE="CREATE TABLE IF NOT EXISTS "+OPERATION_TABLE_NAME+ 
					" ( oprId INTEGER PRIMARY KEY," +
						"msgtxt  text NOT NULL," +
							"created_at DATETIME NOT NULL)";
			String STATUS_CREATE_DATABASE="CREATE TABLE IF NOT EXISTS "+STATUS_TABLE_NAME+
											"(id INTEGER PRIMARY KEY," +
											"oprId INTEGER NOT NULL," +
											"groupName varchar(40) NOT NULL," +
											"name varchar(30)," +
											"phoneNum varchar(13) NOT NULL," +
											"status int(1) NOT NULL," +
											"acceptance int(1)," +
											"msgCount int(2) NOT NULL," +
											"sentCount int(2) NOT NULL," +
											"deliveredCount int(2) NOT NULL,"+
											"stat_at DATETIME NOT NULL," +
											"foreign key(oprId) REFERENCES " +OPERATION_TABLE_NAME+
											"(oprId))";
					
			db.execSQL(OPERATION_CREATE_DATABASE);
			db.execSQL(STATUS_CREATE_DATABASE);
			db.execSQL(CREATE_DATABASE);
			log("All three Databse schematics has been craeted");
		}
	
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
			onCreate(db);
		}	
		
	}
	private static void log(String message){
		if(Commons.SHOW_LOG)
			Log.d("ContactDbHelper",message);
	}
}
