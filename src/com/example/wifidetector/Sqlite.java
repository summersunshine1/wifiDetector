package com.example.wifidetector;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class Sqlite extends SQLiteOpenHelper{

	public Sqlite(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	public Sqlite(Context context,String name)
	{
		this(context, name, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Log.e("2","2");
		db.execSQL("CREATE TABLE loc(_id INTEGER PRIMARY KEY AUTOINCREMENT ,time TEXT,BSSID TEXT,RSSI TEXT,action Interger,build Interger,loc Interger)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}


}
