package jlr.ProcessManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ListActivity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainActivity extends ListActivity {
	private PackageManager packageManager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SimpleAdapter adapter = new SimpleAdapter(this,getData(),R.layout.process_info,
				new String[]{"app_name","process_name","process_mem"},
				new int[]{R.id.app_name,R.id.process_name,R.id.process_mem});
		setListAdapter(adapter);
	}

	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> processList = new ArrayList<Map<String, Object>>();
		packageManager = getPackageManager();
		
		ActivityManager activityManager=(ActivityManager)getSystemService(ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningProcesses=activityManager.getRunningAppProcesses();
		
		Log.e("Process number:",""+runningProcesses.size());
		if(runningProcesses !=null && runningProcesses.size()>0){
			for(RunningAppProcessInfo process : runningProcesses){
				Map<String, Object> map = new HashMap<String, Object>();
				ApplicationInfo applicationInfo = null;
				int[] pidMem=new int[] {process.pid};
				Debug.MemoryInfo[] memInfo=activityManager.getProcessMemoryInfo(pidMem);
				String memOccupy=memInfo[0].dalvikPss+"kB";
				try{
				applicationInfo= packageManager.getApplicationInfo(process.processName, PackageManager.GET_META_DATA);
				map.put("app_name", packageManager.getApplicationLabel(applicationInfo) );
				map.put("process_name",process.processName);
				map.put("process_mem", memOccupy);
				processList.add(map);
				}
				catch(NameNotFoundException e){
					e.printStackTrace();
				}
			}
		}else{
			Toast.makeText(getApplicationContext(), "No application is running!", Toast.LENGTH_LONG).show();
		}
		return processList;
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
}