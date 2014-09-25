CMPUT 301
Assignment 1
Jonathan Cairo

User Guide:
Create a todo by entering a todo name in the text field and pressing the enter button.

Click on the todo to mark/unmark as done.

You can view the todos and archived by clicking on the relevant tabs.

Hold down on an individual list item to enter selection mode.
Once in this mode the action bar will show the delete and archive/unarchive options.
After one todo is selected you can select additional todos by tapping on them. 

The settings button at the top of the app when pressed displays stats regarding
the number of todos checked/unchecked, archived checked/unchecked and totals.

The settings button also gives you the option to email todos, archived todos, and all
todos.


Class Notes:
The application uses a single activity to manage fragments.
The main activity subclasses a FragmentPagerAdapter which manages 2 fragments.
One fragment is the todo list and the other is the archived todo list. 
Both lists are instances of the same Fragment class ToDoFragment which subclasses FragmentActivity. The displayed list depends on the tab selected by the user.

Each of these fragments uses an ArrayAdapter subclassed as ToDoAdapter to manage a list of Todo objects. The adapter defines how a Todo object is displayed in the listview. 
The ToDoFragment instance manages loading data from the save file and saving to the file
whenever the state of the list is changed. Each instance of the TodoFragment is given 
a unique name in the constructor so it can be identified from the other. For instance
when the todo ListFragment loads data it needs to find the key corresponding to the
todo list and not the archived list so it uses its unique string id set on instance
creation by the main activity to determine
the appropriate key value pair in shared preferences.

The Emailer class takes groups of Todos and converts them into email 
compliant strings then requests the phone open an email app to send the string.

The main activity implements 2 interfaces the ActionBar.TabListener informs the app
what to do when the user clicks on the todo or archived tab. The ToggleToDoItemArchivedState provides a bridge from the todo list to the archived to do list used when a todo is
archived or unarchived and must be switched into the alternate list. This is done
by passing an ArrayList<Todo> to onToDoArchiveStateToggled() which simply uses the Todo
add method on the relevant list.