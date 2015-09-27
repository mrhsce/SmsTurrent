package mrhs.ce.DenseSms;

import mrhs.ce.DenseSms.R;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PostMessageArrayAdaptor extends ArrayAdapter<String> {
	
	PostMessageActivity context;
	public PostMessageArrayAdaptor(PostMessageActivity ctx){
		super(ctx,R.layout.message_report_item,ctx.phoneList);
		context=ctx;
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
		if(context.nameList.get(position).equals(""))
			nameLabel.setText(context.phoneList.get(position));
		else
			nameLabel.setText(context.nameList.get(position));
		
		if(context.sentList.get(position)==1){
			sentLabel.setText("ارسال شد");
		}else if(context.sentList.get(position)==-1)
			sentLabel.setText("ارسال نشد");
		else if(context.sentList.get(position)==0)
			sentLabel.setText("صبر کنید");
		
		if(context.deliveredList.get(position)==1){
			deliveredLabel.setText("Delivered");
		}else if(context.deliveredList.get(position)==-1)
			deliveredLabel.setText("Failed");
		else if(context.deliveredList.get(position)==0)
			deliveredLabel.setText("صبر کنید");
		
		log("All values are set");	
		
		return convertView;
	}
	
	private void log(String text){
    	Log.d("PostMessage Array Adaptor", text);
    }
}
