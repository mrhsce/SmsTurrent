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

public class LogArrayAdaptor extends ArrayAdapter<Integer> {
	MessageLogActivity context;	
	ArrayList<String> nameList,phoneList;
	ArrayList<Integer> statusList,acceptanceList;
	
	public LogArrayAdaptor(MessageLogActivity ctx, ArrayList<Integer> list){
		super(ctx,R.layout.activity_log_item,list);
		context=ctx;
		nameList = new ArrayList<String>();
		phoneList = new ArrayList<String>();
		statusList = new ArrayList<Integer>();
		acceptanceList = new ArrayList<Integer>();
		
		Cursor c = context.dbHandler.getAllStatusOfOperation(context.oprId);
		do{
			phoneList.add(c.getString(3));
			nameList.add(c.getString(4));
			statusList.add(c.getInt(5));
			acceptanceList.add(c.getInt(6));
		}while(c.moveToNext());
	}
	
	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		super.notifyDataSetChanged();
		nameList = new ArrayList<String>();
		phoneList = new ArrayList<String>();
		statusList = new ArrayList<Integer>();
		acceptanceList = new ArrayList<Integer>();
		
		Cursor c = context.dbHandler.getAllStatusOfOperation(context.oprId);
		do{
			phoneList.add(c.getString(3));
			nameList.add(c.getString(4));
			statusList.add(c.getInt(5));
			acceptanceList.add(c.getInt(6));
		}while(c.moveToNext());		
		
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if(convertView==null){
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.message_report_item, parent, false);
		}		
		
		TextView numberLabel,nameLabel,sentLabel,deliveredLabel;
		
		numberLabel=(TextView)convertView.findViewById(R.id.numberLabel1);
		nameLabel=(TextView)convertView.findViewById(R.id.NameLabel1);
		sentLabel=(TextView)convertView.findViewById(R.id.sentLabel1);
		deliveredLabel=(TextView)convertView.findViewById(R.id.deliveredLabel1);
		log("All views are found");
		
		
		numberLabel.setText(Integer.toString(position+1));
		if(nameList.get(position).equals(""))
			nameLabel.setText(phoneList.get(position));
		else
			nameLabel.setText(nameList.get(position));
		
		sentLabel.setText("صبر کنید");
		if(statusList.get(position)==Commons.MESSAGE_SENT || statusList.get(position)==Commons.MESSAGE_DELIVERED){
			sentLabel.setText("ارسال شد");
		}else if(statusList.get(position)==Commons.MESSAGE_FAILED)
			sentLabel.setText("ارسال نشد");					
		
		deliveredLabel.setText("صبر کنید");
		if(statusList.get(position)==Commons.MESSAGE_DELIVERED){
			deliveredLabel.setText("Delivered");
		}else if(statusList.get(position)==Commons.MESSAGE_FAILED)
			deliveredLabel.setText("Failed");			
		
		log("All values are set");	
		
		return convertView;
	}
	
	private void log(String text){
    	Log.d("PostMessage Array Adaptor", text);
    }
}
