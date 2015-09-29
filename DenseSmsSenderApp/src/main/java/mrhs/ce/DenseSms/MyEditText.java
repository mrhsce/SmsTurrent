package mrhs.ce.DenseSms;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

public class MyEditText extends EditText {

	ArrayList<TextWatcher> list;
	public MyEditText(Context ctx)
    {
        super(ctx);
    }

    public MyEditText(Context ctx, AttributeSet attrs)
    {
        super(ctx, attrs);
    }

    public MyEditText(Context ctx, AttributeSet attrs, int defStyle)
    {       
        super(ctx, attrs, defStyle);
    }
	
	@Override
	public void addTextChangedListener(TextWatcher watcher) {
		// TODO Auto-generated method stub
		if(list==null){
			list=new ArrayList<TextWatcher>();
			list.add(null);
		}
		list.set(0, watcher);
		super.addTextChangedListener(watcher);
	}
	public void removeTextWatcher(){
		removeTextChangedListener(list.get(0));
		list.set(0, null);
	}

}
