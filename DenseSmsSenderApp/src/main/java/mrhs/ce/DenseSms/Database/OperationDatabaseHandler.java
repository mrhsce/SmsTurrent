package mrhs.ce.DenseSms.Database;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import mrhs.ce.DenseSms.Commons;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class OperationDatabaseHandler {
	private static final String DATABASE_NAME = "denseSMS";
	private static final String OPERATION_TABLE_NAME = "operations";
	private static final String STATUS_TABLE_NAME = "status";
	
	private DbHelper dbHelper;
	private  SQLiteDatabase db;
	
	// Necessary functions (DDL)
	public OperationDatabaseHandler(Context ctx){
			dbHelper=new DbHelper(ctx);
	}
		
	public OperationDatabaseHandler open() throws SQLException{
			db=dbHelper.getWritableDatabase();
			return this;
	}
		
	public void close(){
			dbHelper.close();
	}
	
		
	public Integer insertOperation(String message){ // if successful returns id else returns fail code
		ContentValues values=new ContentValues();
		values.put("msgtxt", message);	
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date date = new Date();			        
		values.put("created_at",dateFormat.format(date));
		try{
			if(db.insert(OPERATION_TABLE_NAME, null, values)>0){	
				Cursor cursor=db.query(OPERATION_TABLE_NAME, new String[]{"oprId"}, null, null, null, null, "oprId desc");
				cursor.moveToFirst();
				return cursor.getInt(0);
			}
				else
					return Commons.OPERATION_INSERT_FAILED;
			}catch(Exception e){
			e.printStackTrace();
			log("Error inserting values to the operation databse");
			return Commons.OPERATION_INSERT_FAILED;
		}
	}
	
	public boolean insertStatus(Integer oprId,String groupName,String phoneNum,String name,Integer msgCount){
		ContentValues values=new ContentValues();
		values.put("oprId",oprId);
		values.put("groupName",groupName);
		values.put("phoneNum", phoneNum);
		values.put("status",Commons.MESSAGE_PENDING);
		values.put("acceptance",Commons.RESPONSE_NOT_ANSWERED);
		values.put("msgCount",msgCount);
		values.put("sentCount", 0);
		values.put("deliveredCount", 0);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date date = new Date();
			        
		values.put("stat_at",dateFormat.format(date));
		if(name!=null)
			values.put("name", name);
		try{
			return db.insert(STATUS_TABLE_NAME, null, values)>0;
		}catch(Exception e){
			e.printStackTrace();
			log("Error inserting values to the status databse");
			return false;
		}
	}
	
	public Cursor getAllOperations(){
		Cursor cursor=db.query(OPERATION_TABLE_NAME, new String[]{"oprId","msgtxt","created_at"}, null, null, null, null, "created_at desc");
		if(cursor != null){
			cursor.moveToFirst();
			return cursor;
		}return null;
	}
	
	public String getOperationText(Integer oprId){
		Cursor cursor=db.query(OPERATION_TABLE_NAME, new String[]{"msgtxt"}, "oprId = "+Integer.toString(oprId), null, null, null, null);
		if(cursor != null){
			if(cursor.moveToFirst())
				return cursor.getString(0);
			else
				return null;
		}return null;
	}
	
	// for the response 
	//if both success and sentOrDelivered are true --> Response accepted
	//if success is true but sentorDelivered is false --> response rejected
	//if success is false --> Response invalid
	public boolean updateStatus(Integer id,boolean messageOrRespond,boolean success,boolean sentOrDelivered){
		ContentValues values=new ContentValues();
		if(messageOrRespond){
			Cursor c = getAllDetailsOfStatus(id);
			if(success && c.getInt(5) != Commons.MESSAGE_FAILED){
				if(sentOrDelivered){
					//When the message has been sent
					if(c.getInt(2) == c.getInt(3)+1 )
						values.put("status",Commons.MESSAGE_SENT);
					else
						values.put("sentCount", c.getInt(3)+1);					
				}
				else{
					// When the message has been delivered
					if(c.getInt(2) == c.getInt(4)+1)
						values.put("status",Commons.MESSAGE_DELIVERED);
					else
						values.put("deliveredCount", c.getInt(4)+1);	
				}
			}
			else						
				values.put("status",Commons.MESSAGE_FAILED);			
		}
		else{
			// Response to the message should be implemented here
			if(success)
				if(sentOrDelivered)
					values.put("acceptance",Commons.RESPONSE_ACCEPTED);
				else
					values.put("acceptance",Commons.RESPONSE_REJECTED);
			else
				values.put("acceptance",Commons.RESPONSE_INVALID);
		}
		// Getting the current time and formating it
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date date = new Date();
			        
		values.put("stat_at",dateFormat.format(date));
		return db.update(STATUS_TABLE_NAME, values, "id = "+Integer.toString(id), null)>0;
		
	}
	
	public Cursor getAllStatusOfOperation(Integer oprId){
		Cursor cursor;
		cursor=db.query(STATUS_TABLE_NAME, new String[]{"id","oprId","groupName","phoneNum","name","status","acceptance","stat_at"}, "oprId = "+Integer.toString(oprId), null, null, null, "id asc");
		if(cursor != null){
			cursor.moveToFirst();
			return cursor;
		}return null;		
	}
	
	public Integer getAllStatusOfOperationByNumber(Integer oprId,Integer num){
		Cursor cursor;
		cursor=db.query(STATUS_TABLE_NAME, new String[]{"id","oprId"}, "oprId = "+Integer.toString(oprId), null, null, null, "id asc");
		if(cursor.moveToPosition(num)){
			return cursor.getInt(0);
		}return null;		
	}		 
	
	public Cursor getAllDetailsOfStatus(Integer id){
		Cursor cursor;
		cursor=db.query(STATUS_TABLE_NAME, new String[]{"id",
				"oprId","msgCount","sentCount","deliveredCount","status","acceptance","groupName","phoneNum",
				"name","stat_at"}, "id = "+
				Integer.toString(id), null, null, null, null);
		if(cursor != null){
			cursor.moveToFirst();
			return cursor;
		}return null;		
	}
	
	public boolean deleteOperation(Integer oprId){
		// Delete the operation as well as all the related status
		Cursor cursor=getAllStatusOfOperation(oprId);
		do{
			db.delete(OPERATION_TABLE_NAME, "id = "+Integer.toString(cursor.getInt(0)), null);
		}while(cursor.moveToNext());
		return db.delete(OPERATION_TABLE_NAME, "oprId = "+oprId, null)>0;
	} 	
	
	private static class DbHelper extends SQLiteOpenHelper{
		
		public DbHelper(Context context){
			super(context, DATABASE_NAME, null, Commons.DATABASE_VERSION);
			log("The database has been initialized");
		}
	
		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub	
		}
	
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS "+OPERATION_TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS "+STATUS_TABLE_NAME);
			onCreate(db);
		}	
		
	}
	private static void log(String message){
		if(Commons.SHOW_LOG)
			Log.d("operationsDbHelper",message);
	}
}
