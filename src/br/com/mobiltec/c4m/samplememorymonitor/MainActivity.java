package br.com.mobiltec.c4m.samplememorymonitor;

import br.com.mobiltec.cloud4mobile.android.library.DeviceApi;
import br.com.mobiltec.cloud4mobile.android.library.MonitorsApi;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

	private TextView enrollText;
	private volatile boolean doingEnroll = false;
	private volatile boolean isMonitoring = false;
	private static final String C4M_SAMPLE_TAG = "C4M_SAMPLE";
	private DeviceApi deviceApi;
	MonitorsApi monitorsApi;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		enrollText = (TextView)findViewById(R.id.TextView01);
		deviceApi = new DeviceApi(getApplicationContext());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void doEnroll(final View view){
		if(!doingEnroll){
			enrollText.setText("Enroll Begin");
			new EnrollBackgroundTask().execute("2C5138BF");
		}
	}
	
	private class EnrollBackgroundTask extends AsyncTask<String, Void, Boolean>{
		
		@Override
		protected void onPreExecute() {
			doingEnroll = true;
			super.onPreExecute();
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			try{
				deviceApi.enrollDevice(params[0]);
				Log.d(C4M_SAMPLE_TAG, "Enroll with server ok.");
			} catch(Exception ex){
				Log.e(C4M_SAMPLE_TAG, "Error in enroll.", ex);
				return Boolean.FALSE;
			}
			return Boolean.TRUE;
		}
		
		@Override
		protected void onPostExecute(Boolean isEnrollOk) {
			if(isEnrollOk){
				enrollText.setText("Enroll with server finished.");
			} else {
				enrollText.setText("Failed to enroll with server. Check logcat for details.");
			}
			
			doingEnroll = false;
			startMemoryMonitor();
		}
	}
	
	protected void startMemoryMonitor() {
		if(isMonitoring)
			return;
		
		monitorsApi = new MonitorsApi(this.getApplicationContext());
		try {
			monitorsApi.startMemoryMonitor();
			isMonitoring = true;
		} catch (final Exception ex) {
			Log.e(C4M_SAMPLE_TAG, "Error in start Memory Monitor.", ex);
			isMonitoring = false;
		}
	}
}
