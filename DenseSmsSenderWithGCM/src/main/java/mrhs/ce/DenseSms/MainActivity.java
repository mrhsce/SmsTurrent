package mrhs.ce.DenseSms;



import static gcm.Config.SENDER_ID;


import gcm.Config;
import gcm.GCMForceUpdateActivity;
import gcm.ServerUtilities;

import java.util.ArrayList;

import com.google.android.gcm.GCMRegistrar;

import mrhs.ce.DenseSms.R;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity {
    
	final int EDIT=1;
	final int MAKE=0;
   
    Button send ;
    EditText messageText;
    Spinner phoneNumsArraySpinner;
    TextView phoneCountLabel;
    TextView messageCountLabel;
    
    // Asyntask
 	AsyncTask<Void, Void, Void> mRegisterTask;
 	
    //tmp
    
    Button pickContactButton;
    Button manualGroupMakier;
    Button editButton;
    
    DatabaseHandler db;
    SdCardHandler sdHandler;
    
    String selectedGroup="";
	
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("Entered on create");
        // For stopping the user from using outdated applications
        Integer versionCode =0;
        try {
			versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Integer updatedVersion = getApplicationContext().getSharedPreferences("version", Context.MODE_PRIVATE)
        		.getInt("latest_version", 0);
        //log("Shared preferences is: "+updatedVersion);
        //log("\nVersion is: "+versionCode+"\n");
       
        if(updatedVersion>versionCode){
        	// Break the usual code flow in case the program is outdated
        	startActivity(new Intent(this,GCMForceUpdateActivity.class));
        	finish();
        }
          
        setContentView(R.layout.activity_main);         
        send=(Button)findViewById(R.id.sendButton);
        editButton=(Button)findViewById(R.id.buttonEdit);
        phoneCountLabel=(TextView)findViewById(R.id.phoneCountLabel);
        messageCountLabel=(TextView)findViewById(R.id.messageCountLabel);
        messageText=(EditText)findViewById(R.id.messageText);        
        log("All items are initiated oncreate");
        
        db=new DatabaseHandler(this);
        db.open();
        sdHandler=new SdCardHandler(db, this);  
        sdHandler.execute();									// In this part all the files in the directory 
        log("Sdcard has been checked for adding new contacts");	//are checked and inserted into the database        
        settingUpTheSpinner();
        
        pickContactButton=(Button)findViewById(R.id.buttonAddUsingContacts);
        pickContactButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, Phone.CONTENT_URI);
//			    startActivityForResult(contactPickerIntent, 1001);
				
				Intent intent=new Intent(MainActivity.this,ContactPickerMulti.class);
				startActivityForResult(intent, 0);
			}
		});
        
        manualGroupMakier=(Button)findViewById(R.id.buttonAddManually);
        manualGroupMakier.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, Phone.CONTENT_URI);
//			    startActivityForResult(contactPickerIntent, 1001);
				
				Intent intent=new Intent(MainActivity.this,GroupEditorActivity.class);
				intent.putExtra("mode", MAKE);
				startActivityForResult(intent, 1);
			}
		});
        
        messageText.addTextChangedListener(new TextWatcher() {
			
					
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				setMessageCount();
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
		});
        
        send.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				log("The message text is : "+messageText.getText().toString());
				sendSMS();
			}
		});    
        
        editButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(MainActivity.this,GroupEditorActivity.class);
				intent.putExtra("groupName", selectedGroup);
				intent.putStringArrayListExtra("names", db.getNameList(selectedGroup));
				intent.putStringArrayListExtra("phones", db.getPhoneList(selectedGroup));
				//log("Name list size is : "+Integer.toString(db.getNameList(selectedGroup).size()));
				intent.putExtra("mode", EDIT);
				startActivityForResult(intent, 1);
			}
		});
        
        setUpGCM();		
    } 
    	
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// TODO Auto-generated method stub
    	super.onActivityResult(requestCode, resultCode, data);
    	switch(requestCode){
    	case(0):{    		
	    	if(resultCode==Activity.RESULT_OK){
	    		dbPumping(data.getExtras().getString("groupName"),data.getStringArrayListExtra("names"), data.getStringArrayListExtra("phones"));
	    	}
	    	break;
    	}
    	case(1):{
    		if(resultCode==Activity.RESULT_OK){ 
    			if(data.getIntExtra("mode", 0)==EDIT)
    				db.delete(data.getExtras().getString("exgroupName"));  
    			if(!data.getExtras().getString("groupName").equals(""))
    				dbPumping(data.getExtras().getString("groupName"),data.getStringArrayListExtra("names"), data.getStringArrayListExtra("phones"));
    			else
    				settingUpTheSpinner();
    		}
	    	break;
    	}
    	}	    	    	
    }
    
	private void sendSMS(){   		
        String text=messageText.getText().toString();
        ArrayList<String> addrList=db.getPhoneList(selectedGroup);
        if(!text.equals("") && addrList.size()>0){
	    	Intent intent=new Intent(MainActivity.this,PostMessageActivity.class);
	    	Bundle b=new Bundle();
	    	b.putString("message text", text);
	    	b.putInt("messageCount", setMessageCount());
	    	b.putInt("phoneCount", setPhoneCount());
	    	b.putStringArrayList("phones", addrList);
	    	b.putStringArrayList("names", db.getNameList(selectedGroup));
	    	intent.putExtras(b);
	    	startActivity(intent);
        }
    }
        
    
    private void settingUpTheSpinner(){
    	phoneNumsArraySpinner=(Spinner)findViewById(R.id.phoneNumSpinner);
        log("The number of the added groups are "+Integer.toString(db.getGroupList().size()));
    	ArrayAdapter<String> adaptor=new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_item,db.getGroupList());
        adaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        phoneNumsArraySpinner.setAdapter(adaptor);
        phoneNumsArraySpinner.setOnItemSelectedListener(new spinnerListener());
        if(db.isFilled()){
        	setPhoneCount();
        	editButton.setEnabled(true);
        }        	
        else{
        	phoneCountLabel.setText("0\nشماره");
        	editButton.setEnabled(false);
        }
        
    }
       
    
    class spinnerListener implements  OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> parent, View arg1, int pos,
				long id) {
			// TODO Auto-generated method stub
			log("Item "+Integer.toString(pos)+" is selected");
			setPhoneCount();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
    	
    }
    public Integer setPhoneCount(){
    	int pos=phoneNumsArraySpinner.getSelectedItemPosition();
    	selectedGroup = phoneNumsArraySpinner.getItemAtPosition(pos).toString();
    	int count= db.getPhoneList(selectedGroup).size();
    	phoneCountLabel.setText(Integer.toString(count)+"\nشماره");
    	log(Integer.toString(count)+" is the number of the phone numbers");
    	return count;
    }
    
    public Integer setMessageCount(){
    	String editableText=messageText.getText().toString();
     	int num=SmsManager.getDefault().divideMessage(editableText).size();
     	messageCountLabel.setText(Integer.toString(num)+"\nپیام");
     	return num;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void dbPumping(String groupName,ArrayList<String> namesList,ArrayList<String> phonesList){
    	if(namesList.size()==phonesList.size()){
    		for (int i=0;i<namesList.size();i++){
    			db.insert(groupName, phonesList.get(i), namesList.get(i)); // here should be revised
    		}
    	}
    	 settingUpTheSpinner();
    }
    
    public void setUpGCM(){
    	//Remove it*********************************************************************************
        //GCMRegistrar.setRegisteredOnServer(this, false);
                
        // GCM Related parts
		if (!isConnectingToInternet()) {
					
					// stop executing code by return
					return;
		}
		// Make sure the device has the proper dependencies.
		//GCMRegistrar.checkDevice(this);
		
		
		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest(this);

		// Get GCM registration id
		final String regId = GCMRegistrar.getRegistrationId(this);

		// Check if regid already presents
		if (regId.equals("")) {
			// Registration is not present, register now with GCM			
			GCMRegistrar.register(this, SENDER_ID);
		} else {
			// Device is already registered on GCM
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				// Skips registration.				
				//Toast.makeText(getApplicationContext(), "Already registered with GCM", Toast.LENGTH_LONG).show();
			} else {
				// Try to register again, but not in the UI thread.
				// It's also necessary to cancel the thread onDestroy(),
				// hence the use of AsyncTask instead of a raw thread.
				final Context context = this;
				mRegisterTask = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						// Register on our server
						// On server creates a new user
						ServerUtilities.register(context, Config.APP_NAME, regId);
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						mRegisterTask = null;
					}

				};
						mRegisterTask.execute(null, null, null);
			}
		}
    }
    
    public boolean isConnectingToInternet(){
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
    
    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
	   db.close();
	   log("going to be destroyed");
	   GCMRegistrar.onDestroy(this);
	   super.onDestroy();
	}
    
    private void log(String text){
    	Log.d("Main Activity", text);
    }
}
    
    

