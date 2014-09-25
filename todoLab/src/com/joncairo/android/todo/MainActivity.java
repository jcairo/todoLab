package com.joncairo.android.todo;

import java.util.ArrayList;
import java.util.Locale;

import android.content.ClipData.Item;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
//import android.support.v7.app.ActionBar;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
//import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.joncairo.android.todo.ToDoFragment.ToggleToDoItemArchivedState;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener, ToggleToDoItemArchivedState {
	
	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	Button mDoIt;
	EditText mNewToDoName;
	ToDoFragment mtoDoFragment;
	ToDoFragment mArchivedToDoFragment;
	String mToDoListId = "TODOLIST";
	String mArchivedToDoListId = "ARCHIVEDTODOLIST";
	// name of the file shared preferences will access for loading saved data.
	String mDataFileName = "TO_DO_DATA";

	// Communication method built to communicate between
	// the todo and archived fragments and the activity itself.
	// it is called when the archived state is toggled and the todo
	// needs to be passed from todo list to archived or vis a vis.
	public void onToDoArchivedStateToggled(ArrayList<Todo> todos, String listName){
		// here we need to check which list is sending the todos
		// to be archived/unarchived and use that info to add them to the appropriate list.
		// if we are adding to the archive the message is coming from the 
		// "TODOLIST" fragment, if its coming the "ARCHIVEDTODOLIST then we add it
		// to the TODOLIST
		if (listName == mToDoListId){
			mArchivedToDoFragment.addItemsToList(todos, listName);
		} else if (listName == mArchivedToDoListId) {
			mtoDoFragment.addItemsToList(todos, listName);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// set up the main activity view to be populated with fragments.
		setContentView(R.layout.activity_main);

		// Set up the action bar for tabs
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter to manage tab switching
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		
		// Wire up the to do text entry field
		final EditText mNewToDoName = (EditText)findViewById(R.id.todo_text);					
		// Wire up the to do enter button
		Button mDoIt = (Button)findViewById(R.id.enter_button);
		mDoIt.setOnClickListener( new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
			ArrayList<Todo> mToDoToBeAdded = new ArrayList<Todo>();
			// then add this as a new to do to the to do array				
			String newToDoText = (String)mNewToDoName.getText().toString();
			Log.v("ButtonTextCheck", newToDoText);
			// Create a new todo instance 
				Todo newTodo = new Todo(newToDoText);
				mToDoToBeAdded.add(newTodo);
				// append it to the todolist array
				mtoDoFragment.addItemsToList(mToDoToBeAdded, "");	
			}
		}); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		Log.v("Menu check", "Settings pressed");
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		// get the checked/unchecked counts and number of archived/unarchived items
		MenuItem totalTodos = menu.findItem(R.id.total_todos);
		totalTodos.setTitle("Total Todos: " + Integer.toString(mtoDoFragment.mTodos.size()));
		ArrayList<Integer> checkedAndUncheckedTotals = mtoDoFragment.totalCheckedAndUncheckedItems();
		MenuItem totalTodosUnchecked = menu.findItem(R.id.total_unchecked_todos);
		MenuItem totalTodosChecked = menu.findItem(R.id.total_checked_todos);
		totalTodosChecked.setTitle("Todos Checked: " + Integer.toString(checkedAndUncheckedTotals.get(0)));
		totalTodosUnchecked.setTitle("Todos Unchecked: " + Integer.toString(checkedAndUncheckedTotals.get(1)));
		
		MenuItem totalArchivedTodos = menu.findItem(R.id.total_archived);
		totalArchivedTodos.setTitle("Total Archived: " + Integer.toString(mArchivedToDoFragment.mTodos.size()));
		ArrayList<Integer> checkedAndUncheckedArchivedTotals = mArchivedToDoFragment.totalCheckedAndUncheckedItems();
		MenuItem totalArchivedTodosChecked = menu.findItem(R.id.total_checked_archived);
		MenuItem totalArchivedTodosUnchecked = menu.findItem(R.id.total_unchecked_archived);
		totalArchivedTodosChecked.setTitle("Archived Checked: " + Integer.toString(checkedAndUncheckedArchivedTotals.get(0)));
		totalArchivedTodosUnchecked.setTitle("Archived Unchecked: " + Integer.toString(checkedAndUncheckedArchivedTotals.get(1)));	
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		// create the emailer
		Emailer emailer = new Emailer(this);
		switch (item.getItemId()) {
			case R.id.email_all:
				// get a list of all todos and send the email
				ArrayList<Todo> todosArrayAggregator = (ArrayList<Todo>) 
							mtoDoFragment.cloneList(mtoDoFragment.mTodos);
				ArrayList<Todo> todosArrayArchivedToBeAdded = (ArrayList<Todo>) 
							mArchivedToDoFragment.cloneList(mArchivedToDoFragment.mTodos);
				
				// concatenate the arraylists
				todosArrayAggregator.addAll(todosArrayArchivedToBeAdded);
				//mtoDoFragment.emailSelectedItems(todosArrayAggregator);
				emailer.emailArrayList(todosArrayAggregator);
				return true;
			case R.id.email_all_todos:
				ArrayList<Todo> todosArray = mtoDoFragment.mTodos;
				emailer.emailArrayList(todosArray);			
				// get a list of all todos and email
				return true;
			case R.id.email_all_archived:
				ArrayList<Todo> todosArrayArchived = mArchivedToDoFragment.mTodos;
				emailer.emailArrayList(todosArrayArchived);	
				return true;
	        default:
	            return super.onOptionsItemSelected(item);
		}
	}

	// returns the fragment based on the specified integer index
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the listfragment for the given page.
			// Each page is referred to based on an integer index related to the tab
			// selected
			if (position == 0) {
				mtoDoFragment = ToDoFragment.newInstance(position, mToDoListId, mDataFileName);
				// Log.v("tag of fragment", mtoDoFragment.getTag());
				return mtoDoFragment;			
			} else {
				mArchivedToDoFragment = ToDoFragment.newInstance(position, mArchivedToDoListId, mDataFileName);
				return mArchivedToDoFragment;	
			}		
		}

		@Override
		public int getCount() {
			// Show 2 total pages.
			return 2;
		}

		@Override
		// set the tab display names
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			}
			return null;
		}
	}

	@Override
	public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
		mViewPager.setCurrentItem(tab.getPosition());		
	}

	@Override
	public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
	}
}
