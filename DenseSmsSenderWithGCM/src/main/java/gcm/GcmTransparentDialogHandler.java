package gcm;

import mrhs.ce.DenseSms.R;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class GcmTransparentDialogHandler extends Activity {
	
	String packageName;
	Integer versionCode;
	final int BUYING = 0;
	final int COMMENTING = 1;
	final int UPDATING = 2;
	NotificationManager mgr;
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		setIntent(intent);
		log("New intent initialized");
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gcm_activity_transparent_dialogue_handler);
		packageName = getPackageName();
		try {
			versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
			log("Version is: "+versionCode);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log(packageName);
		mgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);	
		if(getIntent().getExtras().getString("type").equals("report")){
			GcmReportDialog dialog = new GcmReportDialog(this);
			dialog.setCancelable(false);			
			dialog.show();
		}
		if(getIntent().getExtras().getString("type").equals("buy")){
			log("buy message has been recieved");
			GcmBuyDialog dialog = new GcmBuyDialog(this);
			dialog.setCancelable(false);
			dialog.show();
		}
		if(getIntent().getExtras().getString("type").equals("comment")){
			log("comment message has been recieved");
			bazarIntentMaker(packageName, COMMENTING);	    	
	 	}
		if(getIntent().getExtras().getString("type").equals("update")){
			log("update message has been recieved");
			bazarIntentMaker(packageName, UPDATING);			
		}
		
					
	}
	
	public void bazarIntentMaker(String pckg,int type){
		//pckg = "com.faramix.onlineTv";
		if(isOnline()){
			switch (type) {
				case BUYING:
				{
					// Making an intent to bazar for downloading packageName						
		            try {
		                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("bazaar://details?id=" + pckg)));
		                mgr.cancel(Config.BUY_MESSAGE_ID);
				    	finish();
		            } catch (android.content.ActivityNotFoundException anfe) {
		                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://cafebazaar.ir/app/?id="+pckg)));
		                mgr.cancel(Config.BUY_MESSAGE_ID);
				    	finish();
		            }
		            break;
				}
					
				case UPDATING:
				{
					// Making an intent to bazar for downloading packageName						
		            try {
		                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("bazaar://details?id=" + pckg)));
		                mgr.cancel(Config.UPDATE_MESSAGE_ID);
				    	finish();
		            } catch (android.content.ActivityNotFoundException anfe) {
		                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://cafebazaar.ir/app/?id="+pckg)));
		                mgr.cancel(Config.UPDATE_MESSAGE_ID);
				    	finish();
		            }
		            break;
				}
					
				case COMMENTING:
				{
					// Making an intent for commenting in bazar using packageName				
		            try {
		                startActivity(new Intent(Intent.ACTION_EDIT, Uri.parse("bazaar://details?id=" + pckg)));
		                mgr.cancel(Config.COMMENT_MESSAGE_ID);
				    	finish();
		            } catch (android.content.ActivityNotFoundException anfe) {
		                startActivity(new Intent(Intent.ACTION_EDIT, Uri.parse("http://cafebazaar.ir/app/?id="+pckg)));
		                mgr.cancel(Config.COMMENT_MESSAGE_ID);
				    	finish();
		            }
					break;
				}
			}
		}
        else{
              Toast.makeText(getApplicationContext(), "متاسفانه به اینترنت وصل نیستید",   Toast.LENGTH_LONG).show();
              finish();
        }
	}
	
	public boolean isOnline(){
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
          if (connectivity != null)
          {
              NetworkInfo[] info = connectivity.getAllNetworkInfo();
              if (info != null)
                  for (int i = 0; i < info.length; i++)
                      if (info[i].getState() == NetworkInfo.State.CONNECTED)
                      {
                          return true;
                      }
 
          }
          return false;
    }
	
	private static void log(String message){
		Log.d("GCM_transparent_activity",message);
	}
	
	public class GcmReportDialog extends Dialog implements View.OnClickListener {
		
		Activity parent;
		Button yes;
		TextView title,text;
		
		GcmReportDialog(Activity context){
			super(context);
			parent=context;
		}
		
		@Override
		  protected void onCreate(Bundle savedInstanceState) {
		    super.onCreate(savedInstanceState);
		    requestWindowFeature(Window.FEATURE_NO_TITLE);
		    setContentView(R.layout.gcm_report_dialog);
		    yes = (Button) findViewById(R.id.dialogYes);
		    title = (TextView) findViewById(R.id.dialogTitle);
		    text = (TextView) findViewById(R.id.dialogText);
		    title.setText(parent.getIntent().getStringExtra("messageTitle"));
		    text.setText(parent.getIntent().getStringExtra("messageText"));
		    yes.setText("ممنون که گفتی");
		    yes.setOnClickListener(this);

		  }

		  @Override
		  public void onClick(View v) {
		    switch (v.getId()) {
		    case R.id.dialogYes:
		    	mgr.cancel(Config.REPORT_MESSAGE_ID);
		    	dismiss();
		    	parent.finish();
		      break;		    
		    default:
		      break;
		    }
		    //dismiss();
		  }

	}
	
	public class GcmBuyDialog extends Dialog implements View.OnClickListener {
		
		Activity parent;
		Button yes, no;
		TextView title,text;
		
		GcmBuyDialog(Activity context){
			super(context);
			parent=context;
		}
		
		@Override
		  protected void onCreate(Bundle savedInstanceState) {
		    super.onCreate(savedInstanceState);
		    requestWindowFeature(Window.FEATURE_NO_TITLE);
		    setContentView(R.layout.gcm_download_dialog);
		    yes = (Button) findViewById(R.id.dialogYes);
		    no = (Button) findViewById(R.id.dialogNo);
		    title = (TextView) findViewById(R.id.dialogTitle);
		    text = (TextView) findViewById(R.id.dialogText);
		    title.setText(parent.getIntent().getStringExtra("messageTitle"));
		    text.setText(parent.getIntent().getStringExtra("messageText"));
		    yes.setText("باشه ببینیم");
		    no.setText("بعدا");
		    yes.setOnClickListener(this);
		    no.setOnClickListener(this);

		  }

		  @Override
		  public void onClick(View v) {
		    switch (v.getId()) {
		    case R.id.dialogYes:{
		    	String appPackage = parent.getIntent().getStringExtra("package");
		    	bazarIntentMaker(appPackage, BUYING);
		    	
		    	mgr.cancel(Config.BUY_MESSAGE_ID);
		    	dismiss();
		    	parent.finish(); 			      
			    break;
		    }		      
		    case R.id.dialogNo:
		    	dismiss();
		    	parent.finish();		    	
		      break;
		    default:
		      break;
		    }
		    //dismiss();
		  }

	}

}
