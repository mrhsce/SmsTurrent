package mrhs.ce.DenseSms.MsgGroupHandler;

import java.util.ArrayList;

import mrhs.ce.DenseSms.R;
import mrhs.ce.DenseSms.R.id;
import mrhs.ce.DenseSms.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.Contacts;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ContactPickerMulti extends Activity {

	Button cancelButton;
	
	Button doneButton;
	ListView listView;
	TextView textView;
	EditText groupName;
	ArrayList<String> phoneList;
	ArrayList<String> nameList;
	public ArrayList<Boolean> checkedList;
	
	final int EDIT=1;
	final int MAKE=0;
	int mode;	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.contacts_multi);
		
		mode=getIntent().getIntExtra("mode", 0);
		log("mode is "+Integer.toString(mode));
		
		cancelButton = (Button) findViewById(R.id.cancelButton);
		doneButton = (Button) findViewById(R.id.doneButton);
		listView = (ListView) findViewById(R.id.list);
		textView = (TextView) findViewById(R.id.selectedTextview);
		groupName=(EditText)findViewById(R.id.editTextGroupName);
		
		phoneList = new ArrayList<String>();
		nameList = new ArrayList<String>();
		checkedList = new ArrayList<Boolean>();
		log("All attributes initialized");
		
		getContacts(nameList, phoneList, checkedList);
		log("Contacts recieved");

		
		listView.setAdapter(new ContactsArrayAdaptor(this, nameList,
				phoneList));
		listView.setDividerHeight(2);
		listView.setOnItemClickListener(new ContactClickListener());

		cancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent output = new Intent();
				setResult(Activity.RESULT_CANCELED, output);
				finish();
			}
		});

		doneButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(getSelected()>0)
					if(!groupName.getText().toString().equals("") || mode==EDIT){
						Intent output = new Intent();
						output.putStringArrayListExtra("names", getNamesList());
						output.putStringArrayListExtra("phones", getPhonesList());
						output.putExtra("groupName", groupName.getText().toString());
						setResult(Activity.RESULT_OK, output);
						finish();
					}
					else
						Toast.makeText(ContactPickerMulti.this, "لطفا گروه را نامگذاری کنید", Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(ContactPickerMulti.this, "لطفا حداقل یک شماره را انتخاب کنید", Toast.LENGTH_SHORT).show();
			}
		});
		

	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		listView.requestFocus();
	}

	private ArrayList<String> getNamesList() {
		ArrayList<String> list = new ArrayList<String>();

		for (int i = 0; i < checkedList.size(); i++) {
			if (checkedList.get(i))
				list.add(nameList.get(i));
		}
		return list;
	}

	private ArrayList<String> getPhonesList() {
		ArrayList<String> list = new ArrayList<String>();

		for (int i = 0; i < checkedList.size(); i++) {
			if (checkedList.get(i))
				list.add(phoneList.get(i));
		}
		return list;
	}

	
	private void getContacts(ArrayList<String> nameL, ArrayList<String> phoneL,
			ArrayList<Boolean> chkL) {
		// Run query
		Uri uri = Phone.CONTENT_URI;
		String[] projection = new String[] { Contacts.DISPLAY_NAME,Phone.NUMBER};
		String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + " = '"
				+ ("1") + "' and "+Phone.TYPE+" = "+CommonDataKinds.Phone.TYPE_MOBILE;
		String[] selectionArgs = null;
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
				+ " COLLATE LOCALIZED ASC";
		Cursor cursor = getContentResolver().query(uri, projection, selection,
				selectionArgs, sortOrder);
		if (cursor.moveToFirst()) {
			do {
				nameL.add(cursor.getString(0));
				phoneL.add(cursor.getString(1));
				chkL.add(false);
			} while (cursor.moveToNext());
		}
		cursor.close();

	}
	private class ContactClickListener implements OnItemClickListener{
		@Override
			public void onItemClick(AdapterView<?> adapter, View view, int pos,
					long id) {
				// TODO Auto-generated method stub
			log("on item click triggered");
				CheckBox cb = (CheckBox) view.findViewById(R.id.chkBox);
				cb.performClick();
	
				if (cb.isChecked()) {
					checkedList.set(pos, true);
				} else {
					checkedList.set(pos, false);
				}
				textView.setText("Selected: "+Integer.toString(getSelected()));
		}
	}
	
	private int getSelected(){
		int counter=0;
		for(boolean i: checkedList)
			if(i)
				counter++;
		if(counter==0){
			cancelButton.setVisibility(View.GONE);
			doneButton.setVisibility(View.GONE);
			groupName.setVisibility(View.GONE);
			textView.setVisibility(View.GONE);
		}
		else{			
			cancelButton.setVisibility(View.VISIBLE);
			doneButton.setVisibility(View.VISIBLE);
			if(mode!=EDIT)			
				groupName.setVisibility(View.VISIBLE);
			textView.setVisibility(View.VISIBLE);
		}
		return counter;
	}

	private static void log(String message) {		
		Log.d("ContactPickerMulti", message);
	}

}