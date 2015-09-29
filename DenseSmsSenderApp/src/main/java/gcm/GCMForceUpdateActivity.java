package gcm;

import mrhs.ce.DenseSms.R;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class GCMForceUpdateActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gcm_activity_force_update);
		Button updateButton = (Button)findViewById(R.id.updateButton);
		updateButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(isOnline()){
					try {
		                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("bazaar://details?id=" + getPackageName())));
		            } catch (android.content.ActivityNotFoundException anfe) {
		                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://cafebazaar.ir/app/?id="+getPackageName())));
		            }
				}
			}
		});
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gcm_force_update, menu);
		return true;
	}

}
