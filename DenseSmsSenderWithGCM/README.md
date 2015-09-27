Dense-sms-sender-for-android
============================

This application automatically send the determined text to the phone numbers inside a text file

**********************************************

Things to do ->

	First phase:
	- ✔ disable screen rotation
	- ✔ check to see if there exist any sd card
	- ✔ if sdcard exists make a directory inside the sdcard else inside the internal memory called "شماره های مخاطبان"
	- ✔ The program checks the directory and make a combo box filled with the names of the text files inside the directory
	- ✔ when any text file is selected the count of the phone numbers inside it is shown in a small label adjacent to it
	- ✔ when the text of the message editText is changed the number of the messages is shown alike phone numbers
	- ✔ Based on the size of the message the application shows the number of sent and delivered messages
	- ✔ when the send button is pressed the edit text becomes empty and the text of the previous message will be shown in a new activity
	and in it should be two small numbers indicating the count of sent and delivered	
	- ✔ Take care for when the message is null or no phone number is chosen 
	
	Second phase:	
	- ✔ Use branched weights for the gui
	- ✔ take care of the rotation
	- ✔ also add the database for storing numbers and groups
	- ✔ design a mechanism for creating groups from contacts and inserting (number,name) tuple
	- ✔ a mechanism for editing the groups and deleting them
	- ✔ Show exactly for which phone numbers message has been send and also for which has been delivered
	
	Third phase:	
	- ✔ In the manual maker take care of the focus(returning to its place) and also not to focus edit text
	- ✔ see if the broadcast recievers and intents stop when closing the activity they are created and if it is so think about
	 		and do sth that in all condition closed and open the program continues looking for sent , delivery , incoming
	  			messages(use application or service if needed)	
	- ✔ when editing a group and returning the edited group should be selected 
	- ✔ Adding two database one for message details and the other for the numbers and the status
	- ✔ In the beginning and after the send button is pressed the message log list is shown here only brief information like 
			the number of send and recieved and failed messages ,message text and date and time is dispalyed for more 
				information each of these should be clicked
	- ✔ add button for accessing the message log from the main menu
	- ✔ show detail about the success of a message (it means you don't have to watch and wait in the post message activity
	- ✔ Design message sent history
	- ✔ Solve the problem of sending several operatoin failing in the service add some delay to the next series 
			and reduce it every second 
	
	Fourth phase:
	- Design the date converter class for converting the date from Gregorian to Jalali
	- Add search feature to the contact list maker
	- before sending there should be a check button that when this check button is ticked after sending the messages listens 
		for the specific respond from the audience(like 0 or 1) and then base on that adds another status to the 
		numbers(accepted or rejected or not answered) and this will be added to the filters- this message should also 
		be allowed to enter the phone default messaging application
	- the detailed message status page should have spinner for filtering the different status numbers like all,sent,delivered,
		failed,answered,not answered and etc	
	- when long clicking each operation there should be a dialog for {view,delete,copy message,forward to the group}
		*If the group is deleted toast about it ** for forwarding close the current activity and putExtra the data
	- when long pressing the message text there should be a dialog to copy the text inside it
	- when long clicking each item in the message log array there should be a dialog to show the latest response date time
	- an option in the main activity menu to force stop the service by user
	- a mechanism that the service stops itself after being informed about the delivery report of all messages or 
		finishing timeout after the last received report
	- Add sim card choice to the main activity for when the device has two sim card(this option should be disabled
		when the device has only one)	
	- optimizing the service
	- make notification for the last series of sent messages and how to change it without informing the user
		 of a new notification	and an option to deactivate it
			

	
	- A mechanism for sending message to failed contacts
	to see the results you can just close the app but still the detail are saved in the database)	
	- as long as the number of the sent and delivered messages hasn't reached the maximum there should be a small animation
	running for each
	- work on the graphic and use branched weighting and scroll bars for the views
	- add attribute source to the database to differentiate groups automatically created and manuals
	in order to add duplicate sd based groups
	- add mechanism for exporting the groups
