package mrhs.ce.DenseSms;


import java.util.ArrayList;

import gcm.Config;
import gcm.ServerUtilities;
import mrhs.ce.DenseSms.R;
import mrhs.ce.DenseSms.Database.ContactDatabaseHandler;
import mrhs.ce.DenseSms.Database.OperationDatabaseHandler;
import mrhs.ce.DenseSms.MessageLog.MessageLogActivity;
import mrhs.ce.DenseSms.MessageLog.MessageLogMainActivity;
import mrhs.ce.DenseSms.MsgGroupHandler.ContactPickerMulti;
import mrhs.ce.DenseSms.MsgGroupHandler.GroupEditorActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
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

import com.google.android.gcm.GCMRegistrar;

import static gcm.Config.SENDER_ID;

public class MainActivity extends Activity {
    
	final int EDIT=1;
	final int MAKE=0;

    final Boolean LOCAL_SHOW_LOG = true;
    //Used for gcm
    AsyncTask<Void, Void, Void> mRegisterTask;

    Button send ;
    EditText messageText;
    Spinner phoneNumsArraySpinner;
    TextView phoneCountLabel;
    TextView messageCountLabel;
    
    Integer spinnerSelectedItem;
    
    //tmp
    
    Button pickContactButton,manualGroupMakier,editButton,messageLogButton,aboutUsButton;
    
    ContactDatabaseHandler contactDb;
    OperationDatabaseHandler oprDb;
    SdCardHandler sdHandler;
    
    String selectedGroup="";
	
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("Entered on create");         
        
        setContentView(R.layout.activity_main);         
        send=(Button)findViewById(R.id.sendButton);
        editButton=(Button)findViewById(R.id.buttonEdit);
        messageLogButton = (Button)findViewById(R.id.buttonMessageLog);
        aboutUsButton = (Button)findViewById(R.id.buttonAbout);
        phoneCountLabel=(TextView)findViewById(R.id.phoneCountLabel);
        messageCountLabel=(TextView)findViewById(R.id.messageCountLabel);
        messageText=(EditText)findViewById(R.id.messageText);        
        log("All items are initiated oncreate");
        
        contactDb=new ContactDatabaseHandler(this).open();
        oprDb = new OperationDatabaseHandler(this).open();
        
        sdHandler=new SdCardHandler(contactDb, this);  
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
				intent.putStringArrayListExtra("names", contactDb.getNameList(selectedGroup));
				intent.putStringArrayListExtra("phones", contactDb.getPhoneList(selectedGroup));
				//log("Name list size is : "+Integer.toString(db.getNameList(selectedGroup).size()));
				intent.putExtra("mode", EDIT);
				startActivityForResult(intent, 1);
			}
		});
        
        messageLogButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(MainActivity.this,MessageLogMainActivity.class);
				startActivity(intent);
			}
		});
        
        aboutUsButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(MainActivity.this,AboutActivity.class);
				startActivity(intent);
			}
		});

        log("Gcm setup started");
        setUpGCM();
        log("gcm setup finished");
        
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
    				contactDb.delete(data.getExtras().getString("exgroupName"));  
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
        ArrayList<String> addrList=contactDb.getPhoneList(selectedGroup);
        if(!text.equals("") && addrList.size()>0){
        	Integer oprId = oprDb.insertOperation(text);
        	//log("OprId: "+oprId);
        	if(oprId != Commons.OPERATION_INSERT_FAILED){
	        	ArrayList<String> nameList = contactDb.getNameList(selectedGroup);
	        	for(int i=0;i< addrList.size();i++){
	        		if(oprDb.insertStatus(oprId, selectedGroup, addrList.get(i), nameList.get(i),setMessageCount()));
	        		//log(addrList.get(i)+" Has been inserted");
	        	}
//	        	//// for log
//	        	Cursor cursor = oprDb.getAllStatusOfOperation(oprId, true);
//	        	do{
//	        		log("Status Id: "+cursor.getInt(0));
//	        	}while(cursor.moveToNext());
//	        	////
		    	Intent intent=new Intent(this,SendingService.class);
		    	Bundle b=new Bundle();
		    	b.putInt("oprId", oprId);
		    	b.putString("message text", text);
		    	b.putInt("messageCount", setMessageCount());
		    	b.putInt("phoneCount", setPhoneCount());
		    	b.putStringArrayList("phones", addrList);		    	
		    	intent.putExtras(b);		    	
		    	startService(intent);
		    	log("Service started");
		    	
		    	// Starting multiple activities require API 11 		    	
		    	startActivities(new Intent[]{new Intent(MainActivity.this,MessageLogMainActivity.class),
		    			new Intent(MainActivity.this,MessageLogActivity.class).putExtra("oprId", oprId)});
		    	messageText.getText().clear();
        	}
        }
    }
        
    
    private void settingUpTheSpinner(){
    	phoneNumsArraySpinner=(Spinner)findViewById(R.id.phoneNumSpinner);
        log("The number of the added groups are "+Integer.toString(contactDb.getGroupList().size()));
    	ArrayAdapter<String> adaptor=new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_item,contactDb.getGroupList());
        adaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        phoneNumsArraySpinner.setAdapter(adaptor);
        phoneNumsArraySpinner.setOnItemSelectedListener(new spinnerListener());
        if(contactDb.isFilled()){
        	setPhoneCount();
        	editButton.setEnabled(true);
        }        	
        else{
        	phoneCountLabel.setText("0\nشماره");
        	editButton.setEnabled(false);
        }

        spinnerSelectedItem = 0;        
    }
       
    
    class spinnerListener implements  OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> parent, View arg1, int pos,
				long id) {
			// TODO Auto-generated method stub
			log("Item "+Integer.toString(pos)+" is selected");
			spinnerSelectedItem = pos;
			setPhoneCount();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
    	
    }
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	if(spinnerSelectedItem != 0){
	    	phoneNumsArraySpinner.setSelection(spinnerSelectedItem);
	    	setPhoneCount();
    	}
    }
    public Integer setPhoneCount(){
    	int pos=phoneNumsArraySpinner.getSelectedItemPosition();
    	selectedGroup = phoneNumsArraySpinner.getItemAtPosition(pos).toString();
    	int count= contactDb.getPhoneList(selectedGroup).size();
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
    			contactDb.insert(groupName, phonesList.get(i), namesList.get(i)); // here should be revised
    		}
    	}
    	 settingUpTheSpinner();
    }
    
    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
    	contactDb.close();
    	oprDb.close();
	   log("going to be destroyed");	
	   super.onDestroy();
	}

    public void setUpGCM(){
        // GCM Related parts
        if (!isConnectingToInternet()) {

            // stop executing code by return
            log("Is not connected to internet");
            return;
        }
        log("Is connected to internet");

        // Get GCM registration id
        final String regId = GCMRegistrar.getRegistrationId(this);

        // Check if regid already presents
        if (regId.equals("")) {
            log("start registering");
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

    private void log(String message){
        if(Commons.SHOW_LOG && LOCAL_SHOW_LOG)
            Log.d(this.getClass().getSimpleName(), message);
    }
}
    
    

