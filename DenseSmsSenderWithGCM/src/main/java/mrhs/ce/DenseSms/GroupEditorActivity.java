package mrhs.ce.DenseSms;

import java.util.ArrayList;

import mrhs.ce.DenseSms.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class GroupEditorActivity extends Activity {

	final int EDIT=1;
	final int MAKE=0;
	
	ImageButton addButton,deleteButton;
	Button addContactButton,resetButton,doneButton;
	ListView listView;
	EditText groupNameEditText;
	
	public ArrayList<String> nameList,initialNameList;
	public ArrayList<String> phoneList,initialPhoneList;
			
	String initialGroupName;
	int mode;
	DatabaseHandler db;
	boolean deleteAll;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_group);

		db=new DatabaseHandler(this).open();
		listView=(ListView)findViewById(R.id.editListView);
		addButton=(ImageButton)findViewById(R.id.editAddButton);
		addContactButton=(Button)findViewById(R.id.editAddContactButton);
		resetButton=(Button)findViewById(R.id.editResetButton);		
		doneButton=(Button)findViewById(R.id.editDoneButton);
		groupNameEditText=(EditText)findViewById(R.id.editGroupName);
		deleteButton=(ImageButton)findViewById(R.id.editDeleteButton);
		
		resetButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(mode==EDIT){
					nameList.clear();
					phoneList.clear();
					for(int i=0;i<initialNameList.size();i++){
						nameList.add(initialNameList.get(i));
						phoneList.add(initialPhoneList.get(i));
					}
					groupNameEditText.setText(initialGroupName);
					deleteAll=false;
					chkDeleteAll();
					setAdaptor(mode);
				}
				if(mode==MAKE){
					Intent output = new Intent();
					setResult(Activity.RESULT_CANCELED, output);
					finish();
				}
			}
		});
		addContactButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(GroupEditorActivity.this,ContactPickerMulti.class);
				intent.putExtra("mode", EDIT);
				startActivityForResult(intent, 0);
			}
		});
		doneButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(groupNameEditText.getText().toString().equals(""))
					Toast.makeText(GroupEditorActivity.this, "لطفا گروه را نامگذاری کنید", Toast.LENGTH_SHORT).show();
				else
					chkList(groupNameEditText.getText().toString());
			}
		});
		addButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
			// TODO Auto-generated method stub
				addField(1);
				}
		});
		
		deleteButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub				
				switch(mode){
				case(EDIT):{
					if(deleteAll){
						log("Group name is going to be deleted");
						Intent intent=new Intent();
						intent.putExtra("groupName", "");
						intent.putExtra("exgroupName", initialGroupName);
						intent.putExtra("mode", mode);
						setResult(Activity.RESULT_OK, intent);
						finish();
					}
					addField(-nameList.size());
					addField(1);
					break;
				}
				case(MAKE):{
					addField(-1);
					
					break;
				}
				}
				
			}
		});
		
		mode=getIntent().getIntExtra("mode", 0);
		if(mode==MAKE)
			resetButton.setText("Cancel");
		else if(mode==EDIT)
			resetButton.setText("Reset");
		switch(mode){
			case(EDIT):{
				log("Entered edit mode");
				nameList=getIntent().getStringArrayListExtra("names");
				phoneList=getIntent().getStringArrayListExtra("phones");
				if(mode==EDIT){	
					initialNameList=new ArrayList<String>();
					initialPhoneList=new ArrayList<String>();
					for(int i=0;i<nameList.size();i++){
						initialNameList.add(nameList.get(i));
						initialPhoneList.add(phoneList.get(i));
					}
				}
				initialGroupName=getIntent().getStringExtra("groupName");			

				groupNameEditText.setText(initialGroupName);
				
				break;
			}
			case(MAKE):{
				log("Entered make mode");
				deleteButton.setImageResource(R.drawable.delete);
				nameList=new ArrayList<String>();
				phoneList=new ArrayList<String>();	
				groupNameEditText.setHint("لطفا گروه را نامگذاری کنید");
				addField(3);
				break;
			}
		}
		setAdaptor(mode);
	}
	
	public void setAdaptor(int mode){
		listView.setAdapter(new GroupEditorArrayAdaptor(this, nameList,mode));
	}
	
	private int addField(int freq){
		if(freq<0 && phoneList.size()>0){
			for(int i=0;i<-freq;i++){
				nameList.remove(nameList.size()-1);
				phoneList.remove(phoneList.size()-1);
			}
		}
		else if(freq>0){
			for(int i=0;i<freq;i++){
				nameList.add("");
				phoneList.add("");
			}
		}
		chkDeleteAll();
		setAdaptor(mode);
		return phoneList.size();
	}
	private int addField(ArrayList<String> names,ArrayList<String> phones){
		for(int i=0;i<phones.size();i++){
			boolean cond=true;
			for(int j=0;j<nameList.size();j++){
				if(nameList.get(j).equals("") && phoneList.get(i).equals("")){
					nameList.set(j, names.get(i));
					phoneList.set(j, phones.get(i));
					cond=false;
					break;
				}
			}
			if(cond){
				nameList.add(names.get(i));
				phoneList.add(phones.get(i));
			}
		}		
		setAdaptor(mode);
		chkDeleteAll();
		return phoneList.size();
	}
		
	private void chkList(String group){
		ArrayList<String> tmpName=new ArrayList<String>();
		ArrayList<String> tmpphone=new ArrayList<String>();
		for(int i=0;i<nameList.size();i++){
			if(/*!nameList.get(i).equals("") && */phoneList.get(i).matches("(\\+98|0)[0-9]{10}")){
				if(nameList.get(i).equals("")){
					tmpName.add("");
				}
				else{
					tmpName.add(nameList.get(i));
				}
				tmpphone.add(phoneList.get(i));
				//log("Item "+tmpphone.get(i));
			}
		}
		if(tmpName.size()>0){			
			if(!group.equals(initialGroupName) && db.groupExists(group)){
				Toast.makeText(GroupEditorActivity.this, "گروه مورد نظر وجود دارد لطفا نام دیگری را وارد کنید",
						Toast.LENGTH_SHORT).show();
			}
			else{
				log(Integer.toString(tmpphone.size())+" contacts has been accepted");
				log("Group name is "+group);
				Intent intent=new Intent();
				intent.putStringArrayListExtra("names", tmpName);
				log("Nameslist size is : "+Integer.toString(tmpName.size()));
				log("Phonelist size is : "+Integer.toString(tmpphone.size()));
				intent.putStringArrayListExtra("phones", tmpphone);
				intent.putExtra("groupName", group);
				intent.putExtra("exgroupName", initialGroupName);
				intent.putExtra("mode", mode);
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
		}
		else
			Toast.makeText(GroupEditorActivity.this, "لطفا مشخصات را به خوبی وارد کنید", Toast.LENGTH_SHORT).show();
	}	
	
	public boolean chkDeleteAll(){
		if((nameList.size()<1 && mode==EDIT) ||(nameList.size()==1 && nameList.get(0).equals("") && 
				phoneList.get(0).equals("") && mode==EDIT)){
			deleteAll=true;
			deleteButton.setImageResource(R.drawable.delete_all);
			log("delete all is true");}
		else{
			deleteAll=false;
			deleteButton.setImageResource(R.drawable.clear);
			log("delete all is false");}
		return deleteAll;
	}	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// TODO Auto-generated method stub
    	super.onActivityResult(requestCode, resultCode, data);
    	switch(requestCode){
    	case(0):{    		
	    	if(resultCode==Activity.RESULT_OK){
	    		addField(data.getStringArrayListExtra("names"), data.getStringArrayListExtra("phones"));
	    	}
	    	break;
    	}
    	}	    	    	
    }
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		db.close();
		super.onDestroy();
	}

	private void log(String text){
    	Log.d("Edit group Activity", text);
    }

}
