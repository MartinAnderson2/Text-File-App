# My Personal Project


## Purpose

The application will enable the user to:
- add `.txt` files from anywhere on their computer  
- place each note in a folder (which can be hierarchical, say: School > Science > Ecology)  
- browse these notes based on their folder (so, in the past example, looking at all notes in the Ecology folder)  
- find desired notes by looking at recently-opened notes  
- open the notes in their default text editor  

It will be used by those who wish to be able to easily find notes about various topics whenever they need them. It will also be handy for those who have a lot of notes but difficulty finding the correct ones when they need them.  
This project is of interest to me since I will personally find it helpful for keeping track of and finding important and handy information. Additionally, I am interested in learning more about how to handle files via code and how to create categories of arbitrary depth.  


## User Stories

- As a user, I want to be able to add a note and group it with other notes in a folder.  
- As a user, I want to be able to view all of the notes I have added to a given folder.  
- As a user, I want to be able to view all of the folders I have created.  
- As a user, I want to be able to choose a note from a list of recently-opened notes.  
- As a user, I want to be able to look through the titles of my notes to find the one I desire.  
- As a user, I want to be able to delete and re-add a note I have previously added.  
- As a user, I want to be able to open my note after finding the desired one.  
- As a user, I want to be given the option to save my file system when I quit.  
- As a user, I want to be given the option to reload that save and resume where I left off when I start the application.  


## Instructions for End User

- In order to add a file to the currently-opened folder, press the "Add" button in the top left. Additional options will appear. Press the "Add File" button. You will then be prompted to enter the name of the file and its path. The name must not already be in use by another file in the folder and the path must lead to a .txt file on your computer. If you get an error then you can simply try again with correct input.  
- You are able to add as many files as you would like to the current folder by following the same procedure (as long as you don't run out of file names).  
- In order to open a file in your computer's default text editor, press the "Open" button in the top left. Additional options will appear. Press the "Open File" button. You will then be prompted to enter the name of the file you would like to open. If the name you inputted does not match any file (these are the rectangular boxes with a yellow background with text inside) in the currently-opened folder then an error message will appear. Simply try again with the name of a file that is in the currently-opened folder. Once you've inputted the name of a file that is present in the currently-opened folder, if it references a file that was deleted or moved then an error message will appear. You are able to try opening a different file, but that one will not be possible to open until the file is moved back. If all went well and the currently-opened folder contains a file with the name you inputted and that file was not moved, it will open in your default .txt file editor.  
- In order to open a folder such that you may view its subfiles and subfolders, press the "Open" button in the top left. Additional options will appear. Press the "Open Folder" button. You will then be prompted to enter the name of the folder you would like to open. If there is no folder named what you inputted in the currently-opened folder then an error message will appear. You are able to try again by repeating the steps. If there is a folder named what you inputted then it will be opened and its contents will be displayed. Any folders it directly contains will be represented by blue boxes with text inside (the text is their name). Any files it directly contains will be represented by yellow boxes with text inside (the text is also their name).  
- On the left side of the screen, you will find the visual component: an icon of a tree. This is to make the application more interesting and represent it.  
- In order to save the state of the application, press the "Save" button in the top left. An additional option named "Save File System" will appear. Press it. Assuming there are no issues with administrator privileges, the current file system will be saved to a file at a default location within the project folder.  
- In order to load a previous state of the application, press the "Load" button in the top left. An additional option named "Load File System" will appear. Press it. Assuming there are no issues with administrator privileges and the save file is correct, the current file system will become the saved one.

## Phase 4: Task 2
```
Event Log:
Wed Nov 27 22:13:10 PST 2024
New FileSystem created

Wed Nov 27 22:13:32 PST 2024
File named "Test File" created

Wed Nov 27 22:13:40 PST 2024
Folder named "Test folder!?!" created

Wed Nov 27 22:13:44 PST 2024
Folder named "Test folder!?!" opened

Wed Nov 27 22:14:09 PST 2024
File named "New file but references same file on disk" created

Wed Nov 27 22:14:21 PST 2024
File named "New file but references same file on disk" with path on this computer "data/test/file for testing.txt" opened
```

## Phase 4: Task 3
To improve my design, I might implement the composite pattern. This would involve creating a new class that is a supertype of Folder and File, and then updating Folder to make use of this type. Specifically, instead of storing a list of Folders and a list of Files, I would make it store a list of that supertype. This would make my program easier to understand since it would follow a common design pattern. It would also eliminate some duplication, but, at the same time, it would make certain operations less efficient. For instance, when opening a file or a folder, the user will already have inputted which of the two they would like to open (because a file and a folder can share the same name), so searching a list of both rather than a list of just the type you know you need would generally take longer.