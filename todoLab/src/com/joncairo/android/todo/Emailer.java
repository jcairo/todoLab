package com.joncairo.android.todo;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.SparseBooleanArray;

public class Emailer {
	private Context mContext;
	
	// This class should take different data structures which contain 
	// todos and create and send emails based on them.
	public Emailer(Context context){
		mContext = context;
	}
	
	public void emailSparseBooleanArray(SparseBooleanArray todos, ArrayList<Todo> todosArray){
		// Method should receive an SparseBooleanArray of todos to be
		// emailed and create the formatted String ready to 
		// be sent
		String formattedEmailString = "";
		for (int index = 0; index<todos.size(); index++){
        	if(todos.valueAt(index)){
        		// this funky bit adjust for the fact that when we delete
        		// something from the arraylist of todoitems the positions
        		// in the list change. To adjust you just subtract the number
        		// of items already removed from the position your deleting.            
        		Todo todo = todosArray.get(todos.keyAt(index));
        		formattedEmailString += todo.toStringEmailFormat() + '\n';
        	}
		}
   		Log.v("EMAILSTRING", formattedEmailString);
		sendEmail(formattedEmailString);
	}
	
	public void emailArrayList(ArrayList<Todo> todos){
		// Method should receive an arraylist of todos to be
		// emailed and create the formatted String ready to 
		// be sent
		String formattedEmailString = "";
		for (int index = 0; index<todos.size(); index++){
        	// this funky bit adjust for the fact that when we delete
        	// something from the arraylist of todoitems the positions
       		// in the list change. To adjust you just subtract the number
       		// of items already removed from the position your deleting.            
       		Todo todo = todos.get(index);
       		formattedEmailString += todo.toStringEmailFormat() + '\n';
		}
   		Log.v("EMAILSTRING", formattedEmailString);
		sendEmail(formattedEmailString);
	}
	
	private void sendEmail(String emailString){
		// Method should receive a preformatted email string
		// that is ready to be sent.
		Intent intent = new Intent(Intent.ACTION_SENDTO);
		intent.setType("text/html");
		intent.putExtra(Intent.EXTRA_SUBJECT, "Todos List");
		intent.putExtra(Intent.EXTRA_TEXT, emailString);
		mContext.startActivity(Intent.createChooser(intent, "Send Email"));
	}

}
