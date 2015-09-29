package mrhs.ce.DenseSms.MessageLog;

import java.util.ArrayList;

import mrhs.ce.DenseSms.Commons;
import mrhs.ce.DenseSms.R;
import mrhs.ce.DenseSms.Database.OperationDatabaseHandler;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

public class MessageLogActivity extends Activity {

	public Integer oprId;
	public ArrayList<Integer> statusIdList;
	public OperationDatabaseHandler dbHandler;
	LogArrayAdaptor adaptor;
	
	SentReciever reciever;

	TextView messageText,sentCounterText,deliveredCountText,failedCountText;
	ListView mainList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_log);
		messageText = (TextView)findViewById(R.id.messageTextView);
		sentCounterText = (TextView)findViewById(R.id.sentCountLabel);
		deliveredCountText = (TextView)findViewById(R.id.deliveredCountLabel);
		failedCountText = (TextView)findViewById(R.id.failedCountLabel);
		mainList = (ListView)findViewById(R.id.MessageReportListView);		
		
		statusIdList = new ArrayList<Integer>();
		dbHandler = new OperationDatabaseHandler(this).open();
		oprId = getIntent().getExtras().getInt("oprId");
		log("OprId: "+oprId);
		
		Cursor c = dbHandler.getAllStatusOfOperation(oprId);
		do{
			statusIdList.add(c.getInt(0));
		}while(c.moveToNext());
		
		messageText.setText(dbHandler.getOperationText(oprId));
		// getting group name and count
//		Integer count = 0;
//		c =dbHandler.getAllStatusOfOperation(oprId);
//		do{count++;
//		}while(c.moveToNext());
//		c.moveToFirst();
//		String groupName = c.getString(2);
		
		adaptor = new LogArrayAdaptor(this, statusIdList);
		mainList.setAdapter(adaptor);
		
		reciever = new SentReciever();
		registerReceiver(reciever, new IntentFilter("smsReport"));
		update();
	}
	
	public void update(){
		Integer sentCount = 0,deliveredCount = 0,failedCount = 0;
		Cursor c =dbHandler.getAllStatusOfOperation(oprId);
		do{
			if(c.getInt(5) == Commons.MESSAGE_SENT){
				sentCount++;
			}
			if(c.getInt(5) == Commons.MESSAGE_FAILED){
				failedCount++;
			}
			if(c.getInt(5) == Commons.MESSAGE_DELIVERED){
				deliveredCount++;
				sentCount++;
			}
		}while(c.moveToNext());

		sentCounterText.setText(Integer.toString(sentCount)+"\nفرستاده شده");
		deliveredCountText.setText(Integer.toString(deliveredCount)+"\nدلیور شده");
		failedCountText.setText(Integer.toString(failedCount)+"\nناموفق");
		
		adaptor.notifyDataSetChanged();
	}

	class SentReciever extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			//mainList.setAdapter(new MainLogArrayAdaptor(MessageLogMainActivity.this,oprIdList));
			if(intent.getIntExtra("oprId", -1) == oprId)
				update();
		}
	}	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		dbHandler.close();
		unregisterReceiver(reciever);
		super.onDestroy();
	}
	private void log(String text){
		if(Commons.SHOW_LOG)
			Log.d("MessageLogActivity", text);
    }

}
