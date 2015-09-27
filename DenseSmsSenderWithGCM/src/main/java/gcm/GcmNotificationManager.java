package gcm;

import mrhs.ce.DenseSms.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class GcmNotificationManager {
	
	Context mContext;
	Intent mIntent;
	NotificationManager notificationMgr;	
	Integer versionCode;
	
	public GcmNotificationManager(Context context, Intent intent) throws
		NullPointerException,IndexOutOfBoundsException,NumberFormatException {
		// TODO Auto-generated constructor stub		
		mContext=context;
		mIntent=intent;
		notificationMgr =(NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		try {
			versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
			log("The version code is: "+versionCode);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String messageType=mIntent.getExtras().getString("type");
		log("Message type: "+messageType);
		if(messageType.equals("report") || messageType.equals("buy") ||
				messageType.equals("comment") || messageType.equals("update")){
			if(messageType.equals("report")) ReportMsgHandler();
			if(messageType.equals("buy")) BuyMsgHandler();
			if(messageType.equals("comment")) CommentMsgHandler();
			if(messageType.equals("update")) UpdateMsgHandler();
		}
		else
			PlainMsgHandler();
	}
	
	@SuppressWarnings("deprecation")
	public void PlainMsgHandler()throws NullPointerException,IndexOutOfBoundsException,NumberFormatException{
		String	message = mIntent.getExtras().getString("message");
		log("Type=plain&"+"&message="+message);
		
		Notification noti = new Notification(R.drawable.ic_launcher, message, java.lang.System.currentTimeMillis());
		noti.setLatestEventInfo(mContext, mContext.getString(R.string.app_name), message, null);
        noti.flags |= Notification.FLAG_AUTO_CANCEL;
        noti.defaults |= Notification.DEFAULT_ALL;
        
        notificationMgr.notify(Config.PLAIN_MESSAGE_ID, noti);        
		
	}
	
	@SuppressWarnings("deprecation")
	public void ReportMsgHandler()throws NullPointerException,IndexOutOfBoundsException,NumberFormatException{
		String title =  mIntent.getExtras().getString("title");		
		Integer behaviour= Integer.parseInt(mIntent.getExtras().getString("behaviour"));
		String messageTitle =  mIntent.getExtras().getString("message").split("/",2)[0];
		String messageText="";
		if( mIntent.getExtras().getString("message").split("/").length>1)
			messageText = mIntent.getExtras().getString("message").split("/")[1];		
		log("Type=report&Behaviout="+Integer.toString(behaviour)+"&title="+title+
				"&messageTitle="+messageTitle+"&messageText="+messageText);
		
		Notification noti = new Notification(R.drawable.ic_launcher, title, java.lang.System.currentTimeMillis());
		Intent notificationIntent = new Intent(mContext, GcmTransparentDialogHandler.class);
		notificationIntent.putExtra("type", "report").putExtra("messageTitle", messageTitle)
		.putExtra("messageText", messageText);
        
        PendingIntent intent = PendingIntent.getActivity(mContext, 0, notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
        noti.setLatestEventInfo(mContext, title, messageTitle, intent);
        if(behaviour==Config.NOTIFICATION_NON_REMOVABLE)
        	noti.flags |= Notification.FLAG_NO_CLEAR;
        if(behaviour==Config.NOTIFICATION_REMOVABLE)
        	noti.flags |= Notification.FLAG_AUTO_CANCEL;
        noti.defaults |= Notification.DEFAULT_ALL;
        
        notificationMgr.notify(Config.REPORT_MESSAGE_ID, noti);
	}
	
	@SuppressWarnings("deprecation")
	public void BuyMsgHandler()throws NullPointerException,IndexOutOfBoundsException,NumberFormatException{
		String title = mIntent.getExtras().getString("title");
		
		Integer behaviour= Integer.parseInt(mIntent.getExtras().getString("behaviour"));
		String messageTitle =  mIntent.getExtras().getString("message").split("/",2)[0];		
		String messageText="";
		if( mIntent.getExtras().getString("message").split("/").length>1)
			messageText = mIntent.getExtras().getString("message").split("/")[1];		
		String bazarPackage = (mIntent.getExtras().getString("package"));		
		log("Type=buy&Behaviout="+Integer.toString(behaviour)+"&title="+title+
				"&package="+bazarPackage+"&messageTitle="+messageTitle+"&messageText="+messageText);
		
		Notification noti = new Notification(R.drawable.ic_launcher, title, java.lang.System.currentTimeMillis());
		Intent notificationIntent = new Intent(mContext, GcmTransparentDialogHandler.class);
		notificationIntent.putExtra("type", "buy").putExtra("messageTitle", messageTitle)
		.putExtra("messageText", messageText).putExtra("package", bazarPackage);
        
        PendingIntent intent = PendingIntent.getActivity(mContext, 0, notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
        noti.setLatestEventInfo(mContext, title, messageTitle, intent);
        if(behaviour==Config.NOTIFICATION_NON_REMOVABLE)
        	noti.flags |= Notification.FLAG_NO_CLEAR;
        if(behaviour==Config.NOTIFICATION_REMOVABLE)
        	noti.flags |= Notification.FLAG_AUTO_CANCEL;
        noti.defaults |= Notification.DEFAULT_ALL;
        
        notificationMgr.notify(Config.BUY_MESSAGE_ID, noti);
			
	}
	
	@SuppressWarnings("deprecation")
	public void CommentMsgHandler()
			throws NullPointerException,IndexOutOfBoundsException,NumberFormatException{
		Integer behaviour= Integer.parseInt(mIntent.getExtras().getString("behaviour"));		
		String message = mIntent.getExtras().getString("message");		
		log("Type=comment&Behaviout="+Integer.toString(behaviour)+"&message="+message);
		
		Notification noti = new Notification(R.drawable.ic_launcher,
				mContext.getString(R.string.app_name), java.lang.System.currentTimeMillis());
		Intent notificationIntent = new Intent(mContext, GcmTransparentDialogHandler.class);
		notificationIntent.putExtra("type", "comment");
        
        PendingIntent intent = PendingIntent.getActivity(mContext, 0, notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
        noti.setLatestEventInfo(mContext, mContext.getString(R.string.app_name), message, intent);
        if(behaviour==Config.NOTIFICATION_NON_REMOVABLE)
        	noti.flags |= Notification.FLAG_NO_CLEAR;
        if(behaviour==Config.NOTIFICATION_REMOVABLE)
        	noti.flags |= Notification.FLAG_AUTO_CANCEL;
        
        noti.defaults |= Notification.DEFAULT_ALL;
        
        notificationMgr.notify(Config.COMMENT_MESSAGE_ID, noti); 
        
	}
	
	@SuppressWarnings("deprecation")
	public void UpdateMsgHandler() throws
		NullPointerException,IndexOutOfBoundsException,NumberFormatException{		
		Integer behaviour= Integer.parseInt(mIntent.getExtras().getString("behaviour"));		
		String message = mIntent.getExtras().getString("message");		
		Integer version =  Integer.parseInt(mIntent.getExtras().getString("version"));		
		Integer updateType= Integer.parseInt(mIntent.getExtras().getString("updateType"));

		log("Type=update&Behaviout="+Integer.toString(behaviour)+
				"&updateType="+Integer.toString(updateType)+"&message="+
					message+"&version="+Integer.toString(version));
		log("vesrion is "+version+" version code is "+versionCode);
		if(version>versionCode){
			Notification noti = new Notification(R.drawable.ic_launcher,
					mContext.getString(R.string.app_name), java.lang.System.currentTimeMillis());
			Intent notificationIntent = new Intent(mContext, GcmTransparentDialogHandler.class);
			notificationIntent.putExtra("type", "update");
	        
	        PendingIntent intent = PendingIntent.getActivity(mContext, 0, notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
	        noti.setLatestEventInfo(mContext, mContext.getString(R.string.app_name), message, intent);
	        if(behaviour==Config.NOTIFICATION_NON_REMOVABLE)
	        	noti.flags |= Notification.FLAG_NO_CLEAR;
	        if(behaviour==Config.NOTIFICATION_REMOVABLE)
	        	noti.flags |= Notification.FLAG_AUTO_CANCEL;
	        	
	        noti.defaults |= Notification.DEFAULT_ALL;
	        
	        notificationMgr.notify(Config.UPDATE_MESSAGE_ID, noti); 
			if(updateType==Config.UPDATE_CRITICAL){
				SharedPreferences pref = mContext.getApplicationContext().getSharedPreferences("version", Context.MODE_PRIVATE);
				//log("The previous version is "+pref.getInt("latest_version", 0));
				Editor editor = pref.edit();				
				editor.putInt("latest_version",version );
				editor.commit();
			}
		}
	}
	
	
	private static void log(String message){
		Log.d("GCM_notificationManager",message);
	}
	

}
