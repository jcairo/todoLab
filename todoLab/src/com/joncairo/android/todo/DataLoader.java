package com.joncairo.android.todo;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class DataLoader {
	Context mContext;
	String mDataFile;
	
	// set to do list
	// parameter specifies key value in shared prefs
	// reads sharedprefs returns a ToDoList object
	public void setData(String stringKeyName, ArrayList<Todo> toDosToBeSaved){
        // get the shared prefs object
		Gson gson = new Gson();
		String mSerializedToDos = gson.toJson(toDosToBeSaved);	
		SharedPreferences sp = mContext.getSharedPreferences(mDataFile, 0);
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putString(stringKeyName, mSerializedToDos);
        spEditor.commit();
		Log.v("DATA SAVE CHECK", "Saved data");

	}
	// get to do list obeject from sharedprefs
	// parameter specifies key value in shared prefs
	// reads sharedprefs returns a ToDoList object
	public ArrayList<Todo> getData(String stringKeyName){
        SharedPreferences sp = mContext.getSharedPreferences(mDataFile, 0);
        String mSerializedString = sp.getString(stringKeyName, "NO_DATA_TO_READ");
        Gson gson = new Gson();
        // if theres no data pass an empty array list back otherwise pass the read in data.
		ArrayList<Todo> mTodos = new ArrayList<Todo>();
        if (mSerializedString == "NO_DATA_TO_READ"){
			mTodos = new ArrayList<Todo>();
		} else {
			Log.v("DATA LOAD CHECK", "Loaded data");
	        mTodos = gson.fromJson(mSerializedString, new TypeToken<List<Todo>>(){}.getType());
		}
		return mTodos;

	}
	
	// user needs to specify a data file key name and the context of 
	// the application to create an instance
	public DataLoader(Context context, String keyName){
		mContext = context;
		// this is the main datafile for the application
		mDataFile = keyName;
        //Gson mGson = new Gson();
		// for testing purposes create a Todo list object and
		// save it so we have data to work with
		
        //ToDoList mToDoListObject = new ToDoList(mContext);
        //ArrayList<Todo> mToDoListArray = mToDoListObject.getTodos();
        //String mserializedData = mGson.toJson(mToDoListArray);		
	}
}
