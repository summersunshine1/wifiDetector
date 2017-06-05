package com.example.wifidetector;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.ActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class MainActivity extends Activity {

	Button start_Button,stop_Button;
	Spinner action_spinner,build_spinner,loc_spinner;
	ArrayAdapter<CharSequence> actionAdapter,buildAdapter,locaAdapter;
	SQLiteDatabase sqLiteDatabase;
	int action_num=-1,build_num=-1,loc_num=-1;
	
	gpsService gpsservice;
    ServiceConnection serviceConnection;
    Long firsttime=System.currentTimeMillis();
    
    boolean start = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		serviceConnection=new ServiceConnection() {
			@Override
				public void onServiceDisconnected(ComponentName name) {
					// TODO Auto-generated method stub
					gpsservice=null;
				}			
				@Override
				public void onServiceConnected(ComponentName name, IBinder service) {
					// TODO Auto-generated method stub
					gpsservice=((gpsService.gpsBinder)service).getService();
					Log.e("service","start service");
					if(gpsservice == null)
					{
						Log.e("service","null");
					}
					
				}
		};
		Intent serviceIntent=new Intent(MainActivity.this,gpsService.class);
		bindService(serviceIntent,serviceConnection,Context.BIND_AUTO_CREATE);
        start_Button=(Button)findViewById(R.id.start_Button);
		stop_Button=(Button)findViewById(R.id.stop_Button);
		
		actionAdapter=ArrayAdapter.createFromResource(this, R.array.action, android.R.layout.simple_spinner_item);
		actionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
		action_spinner=(Spinner)findViewById(R.id.action_spinner);
		action_spinner.setAdapter(actionAdapter);
		buildAdapter=ArrayAdapter.createFromResource(this, R.array.build_num, android.R.layout.simple_spinner_item);
		buildAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
		build_spinner=(Spinner)findViewById(R.id.build_spinner);
		build_spinner.setAdapter(buildAdapter);
		locaAdapter=ArrayAdapter.createFromResource(this, R.array.loc_num, android.R.layout.simple_spinner_item);
		locaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
		loc_spinner=(Spinner)findViewById(R.id.loc_spinner);
		loc_spinner.setAdapter(locaAdapter);
		action_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				action_num=position+1;
				Log.e("action", action_num+"");
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				action_num=-1;
			}
	    	
		});
		
		build_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				build_num=position+1;
				Log.e("build", build_num+"");
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub	
				build_num=-1;
			}
	    	
		});
		loc_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				loc_num=position+1;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub			
				loc_num=-1;
			}
	    	
		});

		start_Button.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(start == false)
				{
				start = true;
				
				storeThread thread = new storeThread();
				thread.start();	
				}
			}
		});	
		
		stop_Button.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				start = false;
			}
		});
		
		 
	}
	
	class storeThread extends Thread
	{
		public void run()
		{
			while(start)
			{ 
			  Sqlite locdbHelper = new Sqlite(MainActivity.this,"info.db");
			  DatabaseManager.initializeInstance(locdbHelper);
			  sqLiteDatabase=DatabaseManager.getInstance().openDatabase();
			  String mac;
			  int rssi; 
			   List<ScanResult> results = gpsservice.getResult();
			   String insertString=("INSERT INTO loc(time,BSSID,RSSI,action,build,loc)"+ "VALUES (?,?,?,?,?,?)");				
			   Date date=new Date(System.currentTimeMillis());
			   SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 		   String time=formatter.format(date);
        	   for(int i=0;i<results.size();i++)
        	  {
        		 mac=results.get(i).BSSID;
        		 rssi=results.get(i).level;
        		 if(action_num==-1 || loc_num==-1 || build_num==-1)
        		 {
        			 continue;
        		 }
        		 sqLiteDatabase.execSQL(insertString,new Object[]{time,mac,rssi,action_num,build_num,loc_num});
        		 Log.i("insert","insert");
        		
        	   }
        	   DatabaseManager.getInstance().closeDatabase();
		       try {
		    	   Thread.sleep(20*1000);
			} catch (Exception e) {
				// TODO: handle exception
			}
		  }
		}
	}
	
	
	public void onBackPressed() {
		if (System.currentTimeMillis() - firsttime < 3000) {
			gpsservice.closeSelf();
			finish();
			System.exit(0);
			
		} else {
			firsttime = System.currentTimeMillis();
			Toast.makeText(this, R.string.press_again_backrun, Toast.LENGTH_SHORT).show();
			
		}
	}
	public void onDestroy() {
	    super.onDestroy();

	    if (serviceConnection != null) {
	        unbindService(serviceConnection);
	    }
	}
}
