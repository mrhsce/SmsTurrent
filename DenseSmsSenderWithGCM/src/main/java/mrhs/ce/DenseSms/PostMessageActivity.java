package mrhs.ce.DenseSms;


import java.util.ArrayList;

import mrhs.ce.DenseSms.R;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

public class PostMessageActivity extends Activity {

	ArrayList<BroadcastReceiver> sendBroadcastReciever ;
	ArrayList<BroadcastReceiver> deliveryBroadcastReciever ;
    
    Integer messageCount;
    Integer phoneCount;
    
    public ArrayList<String> phoneList,nameList;
	public ArrayList<Integer> sentList,deliveredList ;
	
	ArrayList<ArrayList<Boolean>> sendCondition,deliveredCondition;	
    
    TextView messageTextView,sentTextView,deliveredTextView;
    ListView listView;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_on_message_sent);
		messageTextView=(TextView)findViewById(R.id.messageTextView);
		sentTextView=(TextView)findViewById(R.id.sentCountLabel);
		deliveredTextView=(TextView)findViewById(R.id.deliveredCountLabel);
		listView=(ListView)findViewById(R.id.MessageReportListView);
		
		messageTextView.setText(getIntent().getExtras().getString("message text"));
		
		phoneList=getIntent().getStringArrayListExtra("phones");
		nameList=getIntent().getStringArrayListExtra("names");
		
		messageCount=getIntent().getIntExtra("messageCount", 0);
		phoneCount=getIntent().getIntExtra("phoneCount", 0);
				
		sentList=new ArrayList<Integer>();
		deliveredList=new ArrayList<Integer>();
		sendCondition=new ArrayList<ArrayList<Boolean>>();
		deliveredCondition=new ArrayList<ArrayList<Boolean>>();
		for(String i:phoneList){
			sentList.add(0);
			deliveredList.add(0);
			sendCondition.add(new ArrayList<Boolean>());
			deliveredCondition.add(new ArrayList<Boolean>());
		}
		listView.setAdapter(new PostMessageArrayAdaptor(this));
		sendMessage(getIntent().getExtras().getString("message text"),phoneList);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		try{
			for(int i=0;i<messageCount*phoneCount;i++){
	    		unregisterReceiver(sendBroadcastReciever.get(i));
	    		unregisterReceiver(deliveryBroadcastReciever.get(i));
			}
    		log(" both broadcast recievers are unregistered");
    	}catch(Exception e){
    		e.printStackTrace();
    	}
		super.onDestroy();
	}
	
	private void sendMessage(String text,ArrayList<String> phoneNumbers){
        SmsManager sms = SmsManager.getDefault();
        
        String SENT = "S_";
        String DELIVERED = "D_";      
                
        ArrayList<ArrayList<PendingIntent>> sentPI = new ArrayList<ArrayList<PendingIntent>>(); //
        ArrayList<ArrayList<PendingIntent>> deliveredPI = new ArrayList<ArrayList<PendingIntent>>(); //
        
        ArrayList<String> mesgParts= sms.divideMessage(text);
        sendBroadcastReciever=new ArrayList<BroadcastReceiver>();
        deliveryBroadcastReciever=new ArrayList<BroadcastReceiver>();
                
        for (int i=0 ; i<phoneCount; i++){
        	sentPI.add(new ArrayList<PendingIntent>());
        	deliveredPI.add(new ArrayList<PendingIntent>());        	
        	
        	for(int j=0;j<messageCount;j++){ 
        		
        		sendBroadcastReciever.add(new sentReciever());
            	deliveryBroadcastReciever.add(new deliverReciever());
            	
            	registerReceiver(sendBroadcastReciever.get(i), new IntentFilter(SENT+Integer.toString(i)+"."+Integer.toString(j)));
                registerReceiver(deliveryBroadcastReciever.get(i), new IntentFilter(DELIVERED+Integer.toString(i)+"."+Integer.toString(j)));
	        	sentPI.get(i).add(PendingIntent.getBroadcast(this, 0, new Intent(SENT+Integer.toString(i)+"."+Integer.toString(j)), 0));
	        	deliveredPI.get(i).add(PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED+Integer.toString(i)+"."+Integer.toString(j)),0));
        	}  
        	if(i==0)
        	sms.sendMultipartTextMessage(phoneNumbers.get(i), null, mesgParts, sentPI.get(i), deliveredPI.get(i));
        	else{
        		final SmsManager INsms=sms;
        		final ArrayList<String> INphoneNumbers=phoneNumbers;
        		final int INi=i;
        		final ArrayList<ArrayList<PendingIntent>> INsentPI=sentPI;
        		final ArrayList<ArrayList<PendingIntent>> INdeliveredPI=deliveredPI;
        		final ArrayList<String> INmesgParts=mesgParts;
        		new Handler().postDelayed(new Runnable() {					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						INsms.sendMultipartTextMessage(INphoneNumbers.get(INi), null, INmesgParts, INsentPI.get(INi), INdeliveredPI.get(INi));
					}
				}, 1000*3*messageCount*INi);
        	}
        }       
	}	
	
	
	class sentReciever extends BroadcastReceiver{
		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			switch(getResultCode()){
			case Activity.RESULT_OK:				
				log("Sms was sent + id : "+intent.getAction());				
				getRespond(sentMessage, intent.getAction(), true);
				break;
			case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
				log("Generic failure");
				getRespond(sentMessage, intent.getAction(), false);
				break;
			case SmsManager.RESULT_ERROR_NO_SERVICE:
				log("No service");
				getRespond(sentMessage, intent.getAction(), false);
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
            	getRespond(sentMessage, intent.getAction(), false);
            	log("Null PDU");
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
            	getRespond(sentMessage, intent.getAction(), false);
            	log("Radio off");
                break;	
			}
		}
	}
	
	class deliverReciever extends BroadcastReceiver{
    	@Override
    	public void onReceive(Context arg0, Intent intent) {
    		// TODO Auto-generated method stub
    		switch(getResultCode()){
    			case Activity.RESULT_OK:
    				log("Sms was delivered + id : "+intent.getAction());
    				getRespond(deliveredMessage, intent.getAction(), true);
    				break;
    			case Activity.RESULT_CANCELED:
    				getRespond(deliveredMessage, intent.getAction(), false);
    				log("Message was not delivered");
    				break;
    		}
    	}
    }
	
	final int deliveredMessage=10;
	final int sentMessage=11;
	
	private void getRespond(int type,String place,boolean success){
		int position=Integer.parseInt(place.split("_")[1].split("\\.")[0]);
		//log("The beginning position : "+Integer.toString(position));
		//log("Size of sent condition"+Integer.toString(sendCondition.size()));
		//log("Size of sent list"+Integer.toString(sentList.size()));
		if(type==deliveredMessage){
			if(success){
				deliveredCondition.get(position).add(true);
				//log("true added to the deliveredCondition");
			}
			else{
				deliveredCondition.get(position).add(false);
				//log("false added to the deliveredCondition");
			}
			if(deliveredCondition.get(position).size()==messageCount){
				//log("messageCount has reached");
				boolean cond=true;
				for(int i=0;i<deliveredCondition.get(position).size();i++){
					if(!deliveredCondition.get(position).get(i)){
						cond=false;
						break;
					}
				}
				if(cond){
					deliveredList.set(position, 1);
					Integer successCount=Integer.parseInt(deliveredTextView.getText().toString().split("\n")[0])+1;
					deliveredTextView.setText(Integer.toString(successCount)+"\nبه مقصد رسید");
					//log("another message has been delivered");
				}
				else{
					deliveredList.set(position, -1);
					//log("another message has been failed");
				}
			}
		}
		if(type==sentMessage){
			if(success){
				sendCondition.get(position).add(true);
				//log("true added to the sentCondition");
			}
			else{
				sendCondition.get(position).add(false);
				//log("false added to the sentCondition");
			}			
			if(sendCondition.get(position).size()==messageCount){
				//log("messageCount has reached");
				boolean cond=true;
				for(int i=0;i<sendCondition.get(position).size();i++){
					if(!sendCondition.get(position).get(i)){
						cond=false;
						break;
					}
				}
				if(cond){
					sentList.set(position, 1);
					Integer successCount=Integer.parseInt(sentTextView.getText().toString().split("\n")[0])+1;
					sentTextView.setText(Integer.toString(successCount)+"\nفرستاده شد");
					//log("another message has been sent");
				}
				else{
					sentList.set(position, -1);
					//log("another message has not been sent");
				}
			}
		}
		//log("The end of the getRespond");
		listView.setAdapter(new PostMessageArrayAdaptor(this));
	}
	
	private void log(String text){
    	Log.d("Post message activity", text);
    }

}
