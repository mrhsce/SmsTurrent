package mrhs.ce.DenseSms;

import java.util.ArrayList;

import mrhs.ce.DenseSms.Database.OperationDatabaseHandler;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

public class SendingService extends Service {

	ArrayList<ArrayList<BroadcastReceiver>> sendBroadcastReciever,deliveryBroadcastReciever ;
	
    Integer messageCount,phoneCount,oprId;
    
    ArrayList<String> phoneList;	
	OperationDatabaseHandler db;
	
	Integer waitingTime;
	boolean timerCounting;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		waitingTime = 0;
		timerCounting = false;
		db = new OperationDatabaseHandler(this).open();
		sendBroadcastReciever = new ArrayList<ArrayList<BroadcastReceiver>>();
		deliveryBroadcastReciever = new ArrayList<ArrayList<BroadcastReceiver>>();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub		
		log("The service has started on command");
		phoneList=intent.getStringArrayListExtra("phones");
		
		messageCount=intent.getIntExtra("messageCount", 0);
		phoneCount=intent.getIntExtra("phoneCount", 0);
		oprId = intent.getIntExtra("oprId", -1);				
		
		
		sendMessage(intent.getExtras().getString("message text"),phoneList);
		return Service.START_NOT_STICKY;
	}
	
	private void sendMessage(String text,ArrayList<String> phoneNumbers){
        SmsManager sms = SmsManager.getDefault();
        
        String SENT = "S_"+Integer.toString(oprId)+"*";
        String DELIVERED = "D_"+Integer.toString(oprId)+"*";      
                
        ArrayList<ArrayList<PendingIntent>> sentPI = new ArrayList<ArrayList<PendingIntent>>(); //
        ArrayList<ArrayList<PendingIntent>> deliveredPI = new ArrayList<ArrayList<PendingIntent>>(); //
        
        ArrayList<String> mesgParts= sms.divideMessage(text);
        sendBroadcastReciever.add(new ArrayList<BroadcastReceiver>());
        deliveryBroadcastReciever.add(new ArrayList<BroadcastReceiver>());
                
        for (int i=0 ; i<phoneCount; i++){
        	sentPI.add(new ArrayList<PendingIntent>());
        	deliveredPI.add(new ArrayList<PendingIntent>());        	
        	
        	for(int j=0;j<messageCount;j++){ 
        		
        		sendBroadcastReciever.get(sendBroadcastReciever.size()-1).add(new sentReciever());
            	deliveryBroadcastReciever.get(sendBroadcastReciever.size()-1).add(new deliverReciever());
            	
            	registerReceiver(sendBroadcastReciever.get(sendBroadcastReciever.size()-1).get(i), new IntentFilter(SENT+Integer.toString(i)+"."+Integer.toString(j)));
                registerReceiver(deliveryBroadcastReciever.get(sendBroadcastReciever.size()-1).get(i), new IntentFilter(DELIVERED+Integer.toString(i)+"."+Integer.toString(j)));
	        	sentPI.get(i).add(PendingIntent.getBroadcast(this, 0, new Intent(SENT+Integer.toString(i)+"."+Integer.toString(j)), 0));
	        	deliveredPI.get(i).add(PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED+Integer.toString(i)+"."+Integer.toString(j)),0));
        	}  
        	if(i==0 && waitingTime == 0)
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
				}, Commons.MESSAGE_INTERVAL*messageCount*INi+waitingTime);
        	}
        } 
        waitingTime += Commons.MESSAGE_INTERVAL*messageCount*phoneCount;
        if(!timerCounting){
        	timerCounting = true;
        	timer();
        }
	}	
	
	public void timer(){
		if(waitingTime != 0){
			waitingTime--;
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
				timer();	
				}
			}, 1000);
		}
		else{
			timerCounting = false;
		}
	}
	
	class sentReciever extends BroadcastReceiver{
		@Override
		public void onReceive(Context arg0, Intent intent) {
			Boolean sentMessage = true;
			// TODO Auto-generated method stub
			switch(getResultCode()){
			case Activity.RESULT_OK:				
				log("Sms was sent + id : "+intent.getAction());				
				getRespond(sentMessage , intent.getAction(), true);
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
    		Boolean deliveredMessage = false;
			// TODO Auto-generated method stub
    		switch(getResultCode()){
    			case Activity.RESULT_OK:
    				log("Sms was delivered + id : "+intent.getAction());
    				getRespond(deliveredMessage , intent.getAction(), true);
    				break;
    			case Activity.RESULT_CANCELED:
    				getRespond(deliveredMessage, intent.getAction(), false);
    				log("Message was not delivered");
    				break;
    		}
    	}
    }
	
		
	private void getRespond(Boolean sentOrDelivered,String place,boolean success){
		int position=Integer.parseInt(place.split("\\*")[1].split("\\.")[0]);
		int intentOprId = Integer.parseInt(place.split("_")[1].split("\\*")[0]);		
		
		db.updateStatus(db.getAllStatusOfOperationByNumber(intentOprId, position),
				true, success, sentOrDelivered);
		sendBroadcast(new Intent("smsReport").putExtra("oprId", oprId));
		log("another message has been delivered");			
	}

	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		try{
			for(int i=0;i<sendBroadcastReciever.size();i++){
				for(int j=0;j<sendBroadcastReciever.get(i).size();j++){
					unregisterReceiver(sendBroadcastReciever.get(i).get(j));
		    		unregisterReceiver(deliveryBroadcastReciever.get(i).get(j));
				}
			}
    		log(" both broadcast recievers are unregistered");
    	}catch(Exception e){
    		e.printStackTrace();
    	}
		log("The Sending service has been destroyed");
		super.onDestroy();
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	private void log(String text){
		if(Commons.SHOW_LOG)
			Log.d("Sending service", text);
    }

}
