package mrhs.ce.DenseSms.MessageLog;

import java.util.ArrayList;

import mrhs.ce.DenseSms.Commons;

import mrhs.ce.DenseSms.R;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MainLogArrayAdaptor extends ArrayAdapter<Integer> {
	MessageLogMainActivity context;	
	ArrayList<Integer> oprIdList;
	public MainLogArrayAdaptor(MessageLogMainActivity context, ArrayList<Integer> oprIdList) {		
		super(context,R.layout.activity_main_log_item,oprIdList); 
		Log.i("arrayAdaptor","Has started");
		this.context = context;
		this.oprIdList=oprIdList;		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if(convertView==null){
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.activity_main_log_item, parent, false);
		}			
		
		Integer sentCount = 0,deliveredCount = 0,failedCount = 0,count = 0;
		Cursor c = context.dbHandler.getAllStatusOfOperation(oprIdList.get(position));
		do{
			count++;
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
		c.moveToFirst();
		String groupName = c.getString(2);
		
		TextView sentView = (TextView) convertView.findViewById(R.id.labelSent);
		TextView deliveredView = (TextView) convertView.findViewById(R.id.lebelDelivered);
		TextView failedView   =(TextView) convertView.findViewById(R.id.labelFailed);
		TextView dateView   =(TextView) convertView.findViewById(R.id.labelDate);
		TextView groupNameView = (TextView) convertView.findViewById(R.id.labelGroupName);
		TextView countView   =(TextView) convertView.findViewById(R.id.labelCount);
		TextView messageView   =(TextView) convertView.findViewById(R.id.labelMessageText);
		
		sentView.setText("فرستاده شده:  "+Integer.toString(sentCount));
		deliveredView.setText("دریافت شده:  "+Integer.toString(deliveredCount));
		failedView.setText("ناموفق:  "+Integer.toString(failedCount));
		groupNameView.setText(groupName);
		countView.setText("تعداد:  "+Integer.toString(count));
		dateView.setText(context.oprDateList.get(position));
		messageView.setText(context.oprMsgList.get(position));
		
		
		
		return convertView;
	}
	
	private void log(String text){
    	Log.d("MainLogArrayAdaptor", text);
    }
}
