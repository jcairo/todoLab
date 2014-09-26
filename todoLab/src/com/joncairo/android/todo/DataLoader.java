package com.joncairo.android.todo;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

// This class manages saving todo data to and from the shared prefences
// the listfragments instantiate it when they want to load or save.
public class DataLoader {
	Context mContext;
	String mDataFile;
	// This classes use of Gson and shared preferences was taken from the Gson
	// docs, and the google developer website
	// https://sites.google.com/site/gson/gson-user-guide
	// http://developer.android.com/guide/topics/data/data-storage.html#pref
	// (September 25, 2014)
	public void setData(String stringKeyName, ArrayList<Todo> toDosToBeSaved){
        // get the shared prefs object
		Gson gson = new Gson();
		String mSerializedToDos = gson.toJson(toDosToBeSaved);	
		SharedPreferences sp = mContext.getSharedPreferences(mDataFile, 0);
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putString(stringKeyName, mSerializedToDos);
        spEditor.commit();

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
	        mTodos = gson.fromJson(mSerializedString, new TypeToken<List<Todo>>(){}.getType());
		}
		return mTodos;

	}
	
	public String getSavedTextInputData(){
        SharedPreferences sp = mContext.getSharedPreferences(mDataFile, 0);
        String savedText = sp.getString("INPUTTEXT", "NO_DATA_TO_READ");
        if (savedText == "NO_DATA_TO_READ"){
        	return "";
        } else {
        	return savedText;
        }
    }
	
	public void setTextInputData(String inputText){
        // get the shared prefs object	
		SharedPreferences sp = mContext.getSharedPreferences(mDataFile, 0);
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putString("INPUTTEXT", inputText);
        spEditor.commit();
	}
	
	// user needs to specify a data file key name and the context of 
	// the application to create an instance
	public DataLoader(Context context, String keyName){
		mContext = context;
		// this is the main datafile for the application
		mDataFile = keyName;	
	}
}
