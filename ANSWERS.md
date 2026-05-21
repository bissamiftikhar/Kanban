# Assessment Answers


## 1. How to Run

Download the APK from the Releases section on this GitHub page. Install it on any Android device running Android 7 or above. Create an account, start adding tasks. Everything persists automatically.
Note: APK is a debug build for ease of testing.

To run from source you will need Android Studio. Clone the repo,then run on any emulator or device with API 24 or above.



## 2. Stack Choice

I picked Java and XML for Android with Firebase because I have built real projects with this exact stack before including a roommate matching app and a marketplace app. I knew I could deliver something complete and working in the time I had rather than learning something new mid assessment.

Firebase Realtime Database was the right call for persistence here because it works across devices instantly, requires zero server setup, and the free tier is more than enough for this use case. It also made the multi user collaboration feature possible without building a backend.

A worse choice would have been a local SQLite database. It would have worked fine for single user persistence but the collaboration feature would have been impossible without a sync layer on top of it.


## 3. One Real Edge Case

When a user searches for tasks and the filtered result is empty, the app shows an empty state message instead of a blank screen. This is handled in TaskListFragment.java in the checkEmpty method which is called after every filter or load operation. Without this the user would see a completely blank list with no feedback and would not know if the search returned nothing or if something broke.

A second edge case worth mentioning is in CreateTaskDialog.java in the addCollaborator method. When a user types an email to add as a collaborator, the app searches the entire users node in Firebase and checks for a match. If no match is found it shows a toast saying user not found. Without this check a task could be created with a collaborator uid that does not exist in the database which would cause silent failures when loading that task later.


## 4. AI Usage

I used Claude for two things.

First I used it to help structure the XML layout files. I described each screen and it gave me the boilerplate LinearLayout and CardView structure. I then adjusted padding, text sizes, and visibility attributes myself to match what I actually wanted. For example the original dialog layout it gave me had all buttons stacked vertically. I changed the collaborator input row to a horizontal layout so the Add button sits next to the email field.

Second I used it to format and structure this README and ANSWERS.md. The answers and decisions are my own but I used it to organise the structure cleanly.

All Java logic including Firebase queries, fragment communication, adapter setup, and authentication flow was written by me.


## 5. Honest Gap

Three things I would add with another day.

First, there is no People tab. I planned a screen where you can search registered users and view their boards in read only mode. The groundwork is there since every user is saved to the database with their name and email on register. Adding this would mean querying the users node, showing them in a RecyclerView, and opening a read only version of the board when tapped.

Second, there is no collaborator visibility on a task. You can assign collaborators when creating a task but TaskDetailActivity does not show who is assigned. I would add a small list inside that screen pulling names from the collaborators node of each task.

Third, there is no role system. Right now any user can view any other user. The natural next step would be a supervisor role that can view and manage everyone's board, while a regular user can only see people who have added them as a collaborator. This would make the permission model actually meaningful.
