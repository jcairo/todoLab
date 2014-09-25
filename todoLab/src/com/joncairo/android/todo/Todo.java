package com.joncairo.android.todo;

// This class represents a Todo, An arraylist of these objects makes
// up the todo list and a seperate arraylist makes up the archived to
// do list.
import java.util.Date;
import java.util.UUID;

public class Todo {
	private String mToDoName;
	private Date mDateCreated;
	private Boolean mDone;
	private Boolean mArchived;
	
	// To-do getters and setters
	public String getToDoName() {
		return mToDoName;
	}
	public void setToDoName(String toDoName) {
		mToDoName = toDoName;
	}
	public Boolean getDone() {
		return mDone;
	}
	public void setDone(Boolean done) {
		mDone = done;
	}
	public Boolean getArchived() {
		return mArchived;
	}
	public void setArchived(Boolean archived) {
		mArchived = archived;
	}

	public Date getDateCreated() {
		return mDateCreated;
	}
	
	// To-do Constructor
	public Todo(String todoname) {
		mDateCreated = new Date();
		mDone = false;
		mArchived = false;
		mToDoName = todoname;
	}
	
	public String toString() {
		return mToDoName;
	}
	
	// This method formats the todo info in an email friendly manner
	public String toStringEmailFormat(){
		String toDoEmailable = this.getToDoName();
		if (this.getDone()){
			toDoEmailable += " [x]";
		} else {
			toDoEmailable += " [ ]";
		}
		if (this.getArchived()){
			toDoEmailable += " Archived";
		}
		return toDoEmailable;
	}
	
	// The following clone method is from
	// http://stackoverflow.com/questions/715650/how-to-clone-arraylist-and-also-clone-its-contents
	public Todo(Todo todo){
		mToDoName = todo.getToDoName();
		mDateCreated = todo.getDateCreated();
		mDone = todo.getDone();
		mArchived = todo.getArchived();
	}
	
}
