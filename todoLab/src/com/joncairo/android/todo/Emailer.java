package com.joncairo.android.todo;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.util.SparseBooleanArray;

// This class manages the generation and sending of emails based
// on either an arraylist of Todo objects or a sparesboolean array
// of Todo objects.
public class Emailer {
	private Context mContext;	
	// This class takes different data structures which contain 
	// todos and creates and send emails based on them.
	public Emailer(Context context){
		mContext = context;
	}
	
	public void emailArrayList(ArrayList<Todo> todos, String emailSubject){
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
		sendEmail(formattedEmailString, emailSubject);
	}
	
	private void sendEmail(String emailString, String emailSubject){
		// Method should receive a preformatted email string
		// that is ready to be sent.
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/html");
		intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
		intent.putExtra(Intent.EXTRA_TEXT, emailString);
		mContext.startActivity(Intent.createChooser(intent, "Send Email"));
	}
	
	private void sendEmail(String emailString){
		// Method should receive a preformatted email string
		// that is ready to be sent.
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/html");
		intent.putExtra(Intent.EXTRA_SUBJECT, "Todos List");
		intent.putExtra(Intent.EXTRA_TEXT, emailString);
		mContext.startActivity(Intent.createChooser(intent, "Send Email"));
	}

}
