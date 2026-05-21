# KanFlow

KanFlow is a collaborative task management app for Android. You can create tasks, move them across three stages (Todo, In Progress, Done), assign collaborators,Collaborators are added by emails and they can also view task,change status and edit task.
I built this for the Dev Weekends Fellowship assessment. The idea came from wanting something more useful than a plain todo list where tasks actually involve other people.

## Note for Testers

I have included my google-services.json file in the repo so the APK and source both work out of the box without any Firebase setup. This is intentionally left in for ease of testing. In a production app this file would be excluded via .gitignore.

## Quick Install (Recommended)

Download the APK from the Releases section on the right side of this GitHub page. Enable "Install from unknown sources" on your Android device and install it. No setup needed.
Note: APK is a debug build for ease of testing.


## Run from Source

You will need Android Studio installed on your machine.

1. Clone this repo
2. Open the project in Android Studio
3. Run the app on an emulator or a physical Android device (API 24 or above)

The google-services.json file is already included in the repo so no Firebase setup is needed.

## Features

- Email and password authentication
- Three stage Kanban board: Todo, In Progress, Done
- Create tasks with a title and description
- Assign collaborators to tasks by their email
- Collaborators can move task status, only the owner can delete
- Search tasks by title inside each tab
- Tasks persist across sessions via Firebase Realtime Database


## Stack

- Java and XML for Android
- Firebase Authentication for user accounts
- Firebase Realtime Database for persistent storage


## AI Usage

I used AI in two places. First for structuring the XML layout files since writing boilerplate layout code is repetitive and time consuming. I gave it the screen I had in mind and adjusted the output to match what I actually wanted. Second for formatting and structuring this README. The content and decisions are mine but I used AI to help organise it cleanly. All Java logic was written by me.
