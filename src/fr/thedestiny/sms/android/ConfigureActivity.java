package fr.thedestiny.sms.android;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * Configure activity
 * @author Sébastien
 */
public class ConfigureActivity extends Activity {

	private SharedPreferences settings;
	
	private TextView serviceUrlInput;
	private TextView serviceHeaderInput;
	private TextView serviceKeyInput;
	private TextView chunkSizeInput;
	
	public ConfigureActivity() {
		super();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Setup activty
		setContentView(R.layout.configure_activity);
		setTitle(getActivityTitle());
		
		// Keep input references
		serviceUrlInput = (TextView) findViewById(R.id.serviceUrl);
		serviceHeaderInput = (TextView) findViewById(R.id.headerTxt);
		serviceKeyInput = (TextView) findViewById(R.id.keyTxt);
		chunkSizeInput = (TextView) findViewById(R.id.chunkSizeTxt);
		((Button) findViewById(R.id.back)).setOnClickListener(backListener);
		
		// Retrieve pref and display stored values
		settings = getSharedPreferences(Constants.PREFERENCE_STORAGE, MODE_PRIVATE);
		
		String serviceUrl = settings.getString(Constants.SERVICE_URL_PROP, "https://");
		String serviceHeader = settings.getString(Constants.SERVICE_HEADER_PROP, "sms-backup-key");
		String serviceKey = settings.getString(Constants.SERVICE_KEY_PROP, "XXX");
		Integer chunkSize = settings.getInt(Constants.CHUNK_SIZE_PROP, 100);
		
		serviceUrlInput.setText(serviceUrl);
		serviceHeaderInput.setText(serviceHeader);
		serviceKeyInput.setText(serviceKey);
		chunkSizeInput.setText(chunkSize.toString());
	}
	
	@Override
	public void onStop() {
		// When activity dismisses, keep preferences
		String serviceUrl = serviceUrlInput.getText().toString();
		String serviceHeader = serviceHeaderInput.getText().toString();
		String serviceKey = serviceKeyInput.getText().toString();
		Integer chunkSize = Integer.valueOf(chunkSizeInput.getText().toString());
		
		Editor editor = settings.edit();
		editor.putString(Constants.SERVICE_URL_PROP, serviceUrl);
		editor.putString(Constants.SERVICE_HEADER_PROP, serviceHeader);
		editor.putString(Constants.SERVICE_KEY_PROP, serviceKey);
		editor.putInt(Constants.CHUNK_SIZE_PROP, chunkSize);

		editor.apply();
		
		super.onStop();
	}
	
	private String getActivityTitle() {
		return 
				new StringBuffer(getTitle())
				.append(" - ")
				.append(getString(R.string.configure))
				.toString();
	}
	
	OnClickListener backListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			finish();
		}
	};
}
