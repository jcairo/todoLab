package com.joncairo.android.todo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ToDoFragment extends ListFragment {
	public ArrayList<Todo> mTodos;
	public EditText mNewToDoName;
	public Button mDoIt;
	public ToDoAdapter mAdapter;
	private ListView mListView;
	public ToggleToDoItemArchivedState mToDoArchivedCallback;
	// this is the identifier set in the constructor as to whether the
	// instance is the todolist or the archived todolist
	public String mKeyNameForToDoList;
	public Integer mMenuId;
	public String mDataFileName;
	
	
	// This interface should be implemented in the parent activity
	// it is used to communicate between the todo/archived list fragments
	// so that todos can be archived or unarchived.
	// The interface implemetion was taken from 
	// http://developer.android.com/training/basics/fragments/communicating.html
	// (September 24th, 2014)
	public interface ToggleToDoItemArchivedState{
		public void onToDoArchivedStateToggled(ArrayList<Todo> todos, String listName);
	}
	
	// Ensures that the parent activity has implemeted the ToggleToDoItemArchivedState
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
        try {
            mToDoArchivedCallback = (ToggleToDoItemArchivedState) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ToggleToDoItemArchivedState");
        }		
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // create an empty array of todos for the list
        mTodos = new ArrayList<Todo>();
        
        // make an adapter for the listview and pass it the
        // arraylist of todos.
        mAdapter = new ToDoAdapter(mTodos);
        setListAdapter(mAdapter);
        
        // set the host activity
        // call the dataloader and receive back a list of mTodos
        // which is an arraylist of todos then add them to
        // the fragments arraylist of todo items.
        DataLoader mDataLoader = new DataLoader(getActivity(), mDataFileName);
        ArrayList<Todo> mLoadedTodosToBeAdded = mDataLoader.getData(mKeyNameForToDoList);
        addItemsToList(mLoadedTodosToBeAdded, "");        
        
        // set the contextual menu bar layout todos and archived todos
        // have slightly different contextual action bar menus
        if (mKeyNameForToDoList == "TODOLIST"){
        	mMenuId = R.menu.action_bar_menu;
        } else if (mKeyNameForToDoList == "ARCHIVEDTODOLIST"){
        	mMenuId = R.menu.archived_action_bar_menu;
        }
	}
	
	// set up the list items to activate the contextual action bar when
	// long pressed
	// adapted from http://stackoverflow.com/questions/12485698/using-contextual-action-bar-with-fragments
	// (September 24, 2014)
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// set up the contextual action bar so multiple items in the
		// list of todos can be selected and acted on at the same time.
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                    int position, long id) {
                ((ListView) parent).setItemChecked(position,
                        ((ListView) parent).isItemChecked(position));
                return false;
            }
        });
        
        // Set the listener to see multiple list items selected at the same
        // time and perform actions based on the users selection choice.
        getListView().setMultiChoiceModeListener(new MultiChoiceModeListener() {

            private int nr = 0;
            
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                getActivity().getMenuInflater().inflate(mMenuId,
                        menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                // heres where we handle all the button clicks in the contextual action bar.
                case R.id.delete:
					deleteSelectedItems();				
                	mode.finish();
                    break;        
                case R.id.archive:
                	archiveSelectedItems();
                	mode.finish();
                	break;               
                case R.id.unarchive:
            		archiveSelectedItems();
            		mode.finish();
            		break;           		
                case R.id.archived_delete:
					deleteSelectedItems();				
					mode.finish();
					break;

                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                nr = 0;
            }

            @Override
            public void onItemCheckedStateChanged(ActionMode mode,
                    int position, long id, boolean checked) {
                if (checked) {
                    nr++;
                } else {
                    nr--;
                }
                mode.setTitle(nr + " todos selected!");
            }
        });               
	}
	
	// set the state of the to do when its checkbox is toggled
	public void onListItemClick(ListView listView, View v, int position, long id) {
		Todo t = ((ToDoAdapter)getListAdapter()).getItem(position);
	   	if (t.getDone()){
	   		t.setDone(false);
	   	} else {
	   		t.setDone(true);
    	}
	   	mAdapter.notifyDataSetChanged();
    	// Log.v("TAG", "List item clicked");
    }			
		
	// creates a new instance of this listfragment.
	static ToDoFragment newInstance(int num, String storageKeyNameForToDoList, String dataFileName) {
		ToDoFragment f = new ToDoFragment();
		// set the key name so the todolist references the todos
		// and the archived list refernces the archived list.
		// this variable identifies the list as either the todo list or the archived todo list
		f.mKeyNameForToDoList = storageKeyNameForToDoList;
		// set the datafile name the dataloader should use to lookup store data
		f.mDataFileName = dataFileName;	
		return f;
	}
	
	public void archiveSelectedItems(){
		mListView = getListView();
        SparseBooleanArray chosenItemsPositions = mListView.getCheckedItemPositions();
        // now that we have the positions iterate through them and remove them
        // from the todolist then update the adapter
		// build arraylist of todos to pass to the archive/todo list destination
        ArrayList<Todo> mToDosToBeArchived = new ArrayList<Todo>();
        for (int index = 0; index<chosenItemsPositions.size(); index++){
        	if(chosenItemsPositions.valueAt(index)){
        		// this funky bit adjust for the fact that when we delete
        		// something from the arraylist of todoitems the positions
        		// in the list change. To adjust you just subtract the number
        		// of items already removed from the position your deleting.
        		Todo toDoToBeArchived = mTodos.get(chosenItemsPositions.keyAt(index)-index);
        		mToDosToBeArchived.add(toDoToBeArchived);            
        		//mTodos.remove(chosenItemsPositions.keyAt(index)-index);
        		//adapter.notifyDataSetChanged(); 
        		removeItemFromList(chosenItemsPositions, index);
        	}
        }	

        mToDoArchivedCallback.onToDoArchivedStateToggled(mToDosToBeArchived, this.mKeyNameForToDoList);
	}
	
	public void deleteSelectedItems(){
    	mListView = getListView();
        SparseBooleanArray chosenItemsPositions = mListView.getCheckedItemPositions();
        // now that we have the positions iterate through them and remove them
        // from the todolist then update the adapter
		for (int index = 0; index<chosenItemsPositions.size(); index++){
        	if(chosenItemsPositions.valueAt(index)){
        		// this funky bit adjust for the fact that when we delete
        		// something from the arraylist of todoitems the positions
        		// in the list change. To adjust you just subtract the number
        		// of items already removed from the position your deleting.            
        		//removeItemFromToDoList(chosenItemsPositions.keyAt(index)-index);
        		removeItemFromList(chosenItemsPositions, index);
        	}
        }
	}	

	// create the custom adapter which manages the list of todo's
	// adapted from The Big Nerd Ranch Guide p 189
	class ToDoAdapter extends ArrayAdapter<Todo> {
		public ToDoAdapter(ArrayList<Todo> todo) {
			super(getActivity(), 0, todo);
		}
		
		@Override
	    public View getView(final int position, View convertView, ViewGroup parent) {
	        // If we weren't given a view, inflate one
	        if (convertView == null) {
	            convertView = getActivity().getLayoutInflater()
	                .inflate(R.layout.list_view_layout, null);
	        }
	        // Configure the view for the todo
	        Todo t = getItem(position);
	        TextView titleTextView =
	        		(TextView)convertView.findViewById(R.id.todo_list_item_titleTextView);
	        titleTextView.setText(t.getToDoName());
	        TextView dateTextView =
	        		(TextView)convertView.findViewById(R.id.todo_list_item_dateTextView);
	        dateTextView.setText(t.getDateCreated().toString());
	        CheckBox solvedCheckBox =
	        		(CheckBox)convertView.findViewById(R.id.todo_list_item_doneCheckBox);
	        solvedCheckBox.setChecked(t.getDone());

	        
	        // set up the checkbox check listner
	        View row_layout_view = convertView;
	        final CheckBox checkBox = (CheckBox) row_layout_view.findViewById(R.id.todo_list_item_doneCheckBox);
	        // checkbox.setOnClickListener adapted from 
	        // http://stackoverflow.com/questions/15941635/how-to-add-a-listener-for-checkboxes-in-an-adapter-view-android-arrayadapter
	        checkBox.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					final boolean isChecked = checkBox.isChecked();
					//Log.v(TAG, "checbox clicked inside adapter");
					// update the data model
					mTodos.get(position).setDone(isChecked);
					mAdapter.notifyDataSetChanged();				
				}
			});
	        
	        return convertView;
		}
	}
	
	public void addItemsToList(ArrayList<Todo> toDosToBeAdded, String fromListName){
		// the from listName variable represents which list the todos
		// are coming from, so if they come from archivedlist we setArchive property
		// to be false on each before they are entered into the list and vis a vis when from the 
		// todolist. When something is added from the input field we just pass
		// in an empty string so the archiving doesn't get set as its set to unarchived
		// by default on entry.
		
		// get the index of the last element in the list.
		// I do this so they are added to the destination list in the 
		// same order they were in in the origin list.
		Integer lastItemIndex = toDosToBeAdded.size() - 1;
		
		// for (int i = 0; i < toDosToBeAdded.size(); i++){
		for (int i = lastItemIndex; i >= 0; i--){

			Todo todo = toDosToBeAdded.get(i);
			// if the todo is coming from the todolist we
			// we need to set it as an archived to do.
			// otherwise its come from the archive and
			// needs to be unarchived.
			if (fromListName == "TODOLIST"){
				todo.setArchived(true);
			} else if (fromListName == "ARCHIVEDTODOLIST") { 
				todo.setArchived(false);
			} else {
				// this is a little akward I know
				// if the to do is coming from the input text
				// box setArchive does not need to be set
				// so we pass in an empty string to dodge the above
				// conditions
			}
			mTodos.add(0, todo);
			mAdapter.notifyDataSetChanged();		
		}
		// update the save file
		DataLoader dataLoader = new DataLoader(getActivity(), "TO_DO_DATA");
		dataLoader.setData(mKeyNameForToDoList, mTodos);
	}
	
	public void removeItemFromList(SparseBooleanArray booleanArray, Integer index){
		mTodos.remove(booleanArray.keyAt(index)-index);
		mAdapter.notifyDataSetChanged();
		DataLoader dataLoader = new DataLoader(getActivity(), "TO_DO_DATA");
		dataLoader.setData(mKeyNameForToDoList, mTodos);
	}
	
	public ArrayList<Integer> totalCheckedAndUncheckedItems(){
		ArrayList<Integer> checkedUnchedCountArray = new ArrayList<Integer>();
		Integer totalUnchecked = 0;
		Integer totalChecked = 0;
		for (int i = 0; i<mTodos.size(); i++){
			if(mTodos.get(i).getDone()){
				totalChecked++;
			} else {
				totalUnchecked++;
			}
		}
		checkedUnchedCountArray.add(totalChecked);
		checkedUnchedCountArray.add(totalUnchecked);
		return checkedUnchedCountArray;
	}
	
	public static List<Todo> cloneList(List<Todo> list) {
		ArrayList<Todo> cloneList = new ArrayList<Todo>(list.size());
		for (Todo item: list) {
			cloneList.add(new Todo(item));
		}
		return cloneList;
	}
}
