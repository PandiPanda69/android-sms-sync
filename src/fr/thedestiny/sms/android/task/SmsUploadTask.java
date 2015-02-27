package fr.thedestiny.sms.android.task;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import fr.thedestiny.sms.android.R;
import fr.thedestiny.sms.android.service.JsonService;
import fr.thedestiny.sms.android.service.SmsService;
import fr.thedestiny.sms.android.task.SmsUploadTask.InputDto;
import fr.thedestiny.sms.android.task.SmsUploadTask.ResultDto;
import fr.thedestiny.sms.android.util.AlertDialogUtil;

public class SmsUploadTask extends AsyncTask<InputDto, Integer, ResultDto> {

	public static class InputDto {
		public final String url;
		public final String header;
		public final String key;
		
		public InputDto(final String url, final String header, final String key) {
			this.url = url;
			this.header = header;
			this.key = key;
		}
	}
	
	public static class ResultDto {
		public final String message;
		public final boolean isError;
		
		public ResultDto(final String message, final boolean isError) {
			this.message = message;
			this.isError = isError;
		}
	}
	
	private Integer chunkSize;
	private Context parentContext; 
	private ProgressDialog progressDialog;
	
	private int messageCount = 0;
	private List<String> jsonChunks = null;
	
	/**
	 * Constructor
	 * @param context Parent activity context
	 * @param chunkSize Size of a chunk
	 */
	public SmsUploadTask(Context context, final Integer chunkSize) {
		super();
		this.parentContext = context;
		this.chunkSize = chunkSize;
		
		setupProgressBar();
	}
	
	@Override
	protected void onPreExecute() {
		progressDialog.show();
		
		try {
			progressDialog.setMessage("Reading messages...");
			List<Map<String, String>> sms = SmsService.INSTANCE.readSms(parentContext);
			
			progressDialog.setMessage("Building chunks...");
			jsonChunks = JsonService.INSTANCE.translateInputIntoChunkedJson(sms, chunkSize);
			
			messageCount = sms.size();
		} catch (IOException e) {
			Log.e(getClass().getName(), "Error while using Jackson.", e);
			AlertDialogUtil.showAlertDialog(parentContext, 
					parentContext.getResources().getString(R.string.error), 
					e.getClass().getName() + " : " + e.getMessage()
			);
			
			jsonChunks = null;
		}
	}
	
	
	@Override
	protected ResultDto doInBackground(InputDto... params) {

		String url = params[0].url;
		String header = params[0].header;
		String key = params[0].key;
		
		if(jsonChunks == null) {
			return new ResultDto("An unexpected error appeared while converting sms.", true);
		}
		
		try {
			int cpt = 0;
			for(String current : jsonChunks) {
				publishProgress(cpt++);
				sendData(url, header, key, current);
			}
			
			return new ResultDto(
					new StringBuffer("Sent ").append(cpt).append(" chunks of ").append(chunkSize).append(" messages to the server (").append(cpt*chunkSize).append(" messages on ").append(messageCount).append(")").toString(),
					false);
		} 
		catch (Throwable e) {
			return new ResultDto(
					new StringBuffer(e.getClass().getName()).append(": ").append(e.getMessage()).toString(),
					true);
		}
	}
	
	@Override
	protected void onPostExecute(final ResultDto result) {
		AlertDialogUtil.showAlertDialog(parentContext, parentContext.getResources().getString(
					result.isError ? R.string.error : R.string.app_name
				), result.message);
		
		progressDialog.dismiss();
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		
		progressDialog.setMessage(new StringBuffer("Sending chunks (").append(values[0]).append("/").append(jsonChunks.size()).append(")"));
	}
	
	private void setupProgressBar() {
		progressDialog = new ProgressDialog(parentContext);
		progressDialog.setCanceledOnTouchOutside(false);
	}
	
	/**
	 * 
	 * @param serviceUrl
	 * @param header
	 * @param key
	 * @param json
	 * @return
	 * @throws IOException
	 */
	private String sendData(final String serviceUrl, final String header, final String key, final String json) throws IOException {
		final URL url = new URL(serviceUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setUseCaches(false);
		connection.setRequestProperty("Content-Type", "application/json");
		if(header != null && !header.isEmpty()) {
			connection.setRequestProperty(header, key);
		}
		
		connection.connect();
		
		DataOutputStream out = new DataOutputStream(connection.getOutputStream());
		out.write(json.getBytes());
		out.flush();
		out.close();
		
		final int responseCode = connection.getResponseCode();
		if(responseCode != 200) {
			throw new IOException(new StringBuffer(responseCode).append(": ").append(connection.getResponseMessage()).toString());
		}
		
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			do {
				line = reader.readLine();
				if(line != null) {
					buffer.append(line);
				}
			} while(line != null);
		}
		finally {
			if(reader != null) {
				reader.close();
			}
		}
		
		Log.i(getClass().getName(), buffer.toString());
		return buffer.toString();
	}
}
