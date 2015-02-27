package fr.thedestiny.sms.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import fr.thedestiny.sms.android.task.SmsUploadTask;
import fr.thedestiny.sms.android.task.SmsUploadTask.InputDto;
import fr.thedestiny.sms.android.util.AlertDialogUtil;

/**
 * Main activity
 * @author Sébastien
 */
public class MainActivity extends Activity {

	public static final int CONFIGURE_ID = Menu.FIRST;
	
	/**
	 * Default constructor
	 */
	public MainActivity() {
		super();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main_activity);
		
		// Register event handlers
		((Button) findViewById(R.id.start)).setOnClickListener(startListener);
		((Button) findViewById(R.id.back)).setOnClickListener(backListener);
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		menu.add(0, CONFIGURE_ID, 0, R.string.configure);
		
		return true;
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case CONFIGURE_ID:
            Intent configure = new Intent(getApplicationContext(), ConfigureActivity.class);
            startActivity(configure);
            return true;
        }
            
        return super.onOptionsItemSelected(item);
    }
	
    /**
     * Back handler : on click on the back button, terminate app.
     */
	OnClickListener backListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			finish();
		}
	};
	
	/**
	 * Start sync handler
	 */
	OnClickListener startListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// Check connection state
			ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
			if(netInfo == null || !netInfo.isConnected()) {
				AlertDialogUtil.showAlertDialog(v.getContext(), R.string.error, R.string.no_network_msg);
				return;
			}
			
			// Retrieve stored prefs
			SharedPreferences pref = getSharedPreferences(Constants.PREFERENCE_STORAGE, MODE_PRIVATE);
			String serviceUrl = pref.getString(Constants.SERVICE_URL_PROP, null);
			String serviceHeader = pref.getString(Constants.SERVICE_HEADER_PROP, null);
			String serviceKey = pref.getString(Constants.SERVICE_KEY_PROP, null);
			Integer chunkSize = pref.getInt(Constants.CHUNK_SIZE_PROP, Integer.MAX_VALUE);
			
			if(serviceUrl == null || serviceUrl.isEmpty()) {
				AlertDialogUtil.showAlertDialog(v.getContext(), R.string.error, R.string.no_service_url);
				return;
			}
			
			// Start sms async upload
			new SmsUploadTask(MainActivity.this, chunkSize).execute(new InputDto(serviceUrl, serviceHeader, serviceKey));
		}
	};
}
