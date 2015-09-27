package mrhs.ce.DenseSms;

import java.util.ArrayList;

import mrhs.ce.DenseSms.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class ContactsArrayAdaptor extends ArrayAdapter<String> {
	ArrayList<String> nameList;
	ArrayList<String> phoneList;
	ContactPickerMulti context;
	public ContactsArrayAdaptor(ContactPickerMulti context,ArrayList<String> name,ArrayList<String> phone) {
		// TODO Auto-generated constructor stub
		super(context, R.layout.contact_layout, name);
		this.context=context;
		this.nameList=name;
		this.phoneList=phone;
		log("Initialized");
	}	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if(convertView==null){
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.contact_layout, parent, false);
		}
		
		TextView nameView = (TextView) convertView.findViewById(R.id.nameTxtView);
		TextView phoneView = (TextView) convertView.findViewById(R.id.phoneTxtView);
		CheckBox chkBox=(CheckBox) convertView.findViewById(R.id.chkBox);
		
		log("All views are found");
		
		nameView.setText(nameList.get(position));
		phoneView.setText(phoneList.get(position));
		if(context.checkedList.get(position))
			chkBox.setChecked(true);
		else
			chkBox.setChecked(false);
		log("All values are set");	
		
		return convertView;
	}
	
	private void log(String text){
    	Log.d("Contact Array Adaptor", text);
    }

}
