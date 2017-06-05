package com.example.wifidetector;
import java.util.concurrent.atomic.AtomicInteger;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
/*used to manage many threads that use the same database*/
public class DatabaseManager {   
	private AtomicInteger mOpenCounter = new AtomicInteger();    
	private static DatabaseManager instance;   
	private static Sqlite mDatabaseHelper;   
	private SQLiteDatabase mDatabase;    
	public static synchronized void initializeInstance(Sqlite helper) 
	{       
		if (instance == null) 
		{            
			instance = new DatabaseManager();           
			mDatabaseHelper = helper;       
		} 
		
	}     
	public static synchronized DatabaseManager getInstance() 
	{   
		if (instance == null) 
		{            
			 throw new IllegalStateException(DatabaseManager.class.getSimpleName() +  " is not initialized, call initializeInstance(..) method first.");      
		}        
	    return instance;    
	}     
	public synchronized SQLiteDatabase openDatabase() 
	{        
		if(mOpenCounter.incrementAndGet() == 1)
		{            // Opening new database           
			mDatabase = mDatabaseHelper.getWritableDatabase();        
		}        
		return mDatabase;    
	}    
	public synchronized void closeDatabase() 
	{       
		     if(mOpenCounter.decrementAndGet() == 0) 
	         {            // Closing database           
		      mDatabase.close();        
		     }   
	}
}
		    
