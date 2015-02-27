package fr.thedestiny.sms.android.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * Sms service, handling sms operations. <br/>
 * Singleton pattern using enumeration.
 * @author Sébastien
 */
public enum SmsService {

	INSTANCE;
	
	private SmsService() { }

	/**
	 * Read all sms of the phone and return a {@link List} of {@link Map}, mapping all properties.
	 * @param context Any content allowing to query sms URI
	 * @return {@link List} of mapped messages
	 * TODO Build a chunked list here.
	 */
	public List<Map<String, String>> readSms(Context context) {

		List<Map<String, String>> messages = new ArrayList<Map<String,String>>();
		Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms"), null, null, null, null);
		
		try {
			if(!cursor.moveToFirst()) {
				return messages;
			}

			Map<String, String> current;
			do {
				current = new HashMap<String, String>();
				
				for(String col : cursor.getColumnNames()) {
					current.put(col, cursor.getString(cursor.getColumnIndex(col)));
				}
				
				messages.add(current);
			} while(cursor.moveToNext());
			
			return messages;
		}
		finally {
			cursor.close();
		}
	}
}
