package com.example.wifidetector;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

public class gpsService extends Service{
	 WifiManager wifiManager;
	 List<ScanResult> listResults;
     String []mac=new String[20]; 
	 String []SSID=new String[20];
	 int [] RSSI=new int[20];
	 ContentValues contentValues;
	 LocationManager locationManager;
	 Location loc;
	 double lat;
	 double lon;
	 SQLiteDatabase gpsDatabase;
	 WiFilocator wiFilocator;
	 IBinder binder=new gpsBinder();	
	 boolean wifirun=true;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return binder;
	}
	public void onCreate()
	{   
		wiFilocator=new WiFilocator();
		wiFilocator.startlocate();
	}
	class WiFilocator
	{
		public void startlocate()
		{
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					while(wifirun)
					{ 
				      getinfo();
				      try {
						Thread.sleep(20*1000);
					} catch (Exception e) {
						// TODO: handle exception
					}
			}			
			}}).start();
		}		
		public void getinfo()
		{
		wifiManager=(WifiManager)getSystemService(Context.WIFI_SERVICE); 
		wifiManager.startScan();
		listResults=wifiManager.getScanResults();        
		if(listResults==null||!wifiManager.isWifiEnabled())
	     {
	    	 new Thread()
	    	 {
	    		 public void run()
	    		 {   wifiManager.setWifiEnabled(true);
	    			 for(int i=0;i<10;i++)
	    			 {
	    			 listResults=wifiManager.getScanResults();
	    			 if(listResults!=null)
	    			 {
	    				 break;
	    			 }
	    			 try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    			 }
	    			 }
	    	 }.start();
	     }
		}
//	 if(listResults!=null)
//	         {
//	        	   Log.e("3","3");
//	        	   gpsSqlite dbHelper = new gpsSqlite(gpsService.this,"gps.db");
//				   gpsDatabaseManager.initializeInstance(dbHelper);
//				   gpsDatabase=gpsDatabaseManager.getInstance().openDatabase();
//				   String insertString=("INSERT INTO gpsrecord(time,BSSID,SSID,RSSI)"+ "VALUES (?,?,?,?)");				
//				   Date date=new Date(System.currentTimeMillis());
//		 		   SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		 		   String time=formatter.format(date);
//	        	   for(int i=0;i<listResults.size();i++)
//	        	  {
//	        		 mac[i]=listResults.get(i).BSSID;
//	        		 SSID[i]=listResults.get(i).SSID;
//	        		 RSSI[i]=listResults.get(i).level;
//	        		 gpsDatabase.execSQL(insertString,new Object[]{time,mac[i],SSID[i],RSSI[i]});
//	        		
//	        	   }
//	        	   gpsDatabaseManager.getInstance().closeDatabase();	        	 
//	         }         
//		}

	}
	 public class gpsBinder extends Binder
	{
		public gpsService getService()
		{
			return gpsService.this;
		}
	}
    public void closeSelf()
    {   wifirun=false;
    	stopSelf();
    }
    public List<ScanResult> getResult()
    {
    	return listResults;
    }
}
