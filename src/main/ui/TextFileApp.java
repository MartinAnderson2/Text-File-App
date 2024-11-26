package ui;

import model.*;
import model.exceptions.*;
import persistence.exceptions.InvalidJsonException;
import ui.exceptions.*;

import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

// Represents an application that allows users to add .txt files from their computer to the program and sort and
// browse through them. Structure design based on [TellerApp](https://github.students.cs.ubc.ca/CPSC210/TellerApp)
public class TextFileApp {
    public static final String appName = "Pine";

    private Scanner scanner;

    FileSystem fileSystem;

    // EFFECTS: starts the Text File application
    public TextFileApp() {
        runTextFileApp();
    }

    // MODIFIES: this
    // EFFECTS: initializes the app, welcomes the user, processes user input, and thanks the user for using the 
    // application when closed
    private void runTextFileApp() {
        initialize();

        welcome();

        boolean quit = false;
        while (!quit) {
            displayMainMenuOptions();
            String input = getUserInputTrim();

            if (input.equalsIgnoreCase("q") || input.equalsIgnoreCase("quit")) {
                quit = true;
            } else {
                handleMainMenuInput(input);
            }
        }

        goodbye();

        quit();
    }

    // EFFECTS: displays the current directory and the main menu options (the first level of menu options that are
    // available)
    private void displayMainMenuOptions() {
        System.out.println();
        System.out.println("Current directory is: " + fileSystem.getCurrentFolderName());
        System.out.println("Would you like to:");
        System.out.println("  \"a\": Add a file, folder, or label to the current folder");
        System.out.println("  \"e\": Edit a file, folder, or label in the current directory");
        System.out.println("  \"l\": Load a saved file system from disk");
        System.out.println("  \"n\": Navigate the file system");
        System.out.println("  \"o\": Open a file or folder, open all files with a given label");
        System.out.println("  \"s\": Save current file system to disk");
        System.out.println("  \"q\": Quit the application");
    }

    // MODIFIES: this
    // EFFECTS: calls the menu the user selected
    // if their input was invalid, tell them what they inputted was not an option
    private void handleMainMenuInput(String input) {
        if (input.equalsIgnoreCase("a") || input.equalsIgnoreCase("add")) {
            addMenu();
        } else if (input.equalsIgnoreCase("e") || input.equalsIgnoreCase("edit")) {
            editMenu();
        } else if (input.equalsIgnoreCase("l") || input.equalsIgnoreCase("load")) {
            loadMenu();
        } else if (input.equalsIgnoreCase("n") || input.equalsIgnoreCase("navigate")) {
            navigateMenu();
        } else if (input.equalsIgnoreCase("o") || input.equalsIgnoreCase("open")) {
            openMenu();
        } else if (input.equalsIgnoreCase("s") || input.equalsIgnoreCase("save")) {
            saveMenu();
        } else {
            System.out.println("Your input was not recognized as any of: a, e, l, n, o, s, or q");
        }
    }

    // MODIFIES: this
    // EFFECTS: initializes the application: instantiates fileSystem and instantiates and sets settings for the scanner
    // in order to get user input correctly
    // Additionally, attempts to load previous file system from disk in default location
    private void initialize() {
        try {
            System.out.println("Attempting to load previous file system");
            fileSystem = FileSystem.autoLoad();
            System.out.println("Loading succeeded!");
        } catch (IOException | InvalidJsonException e) {
            System.out.println("Loading previous file system failed. Creating new file system");
            fileSystem = new FileSystem();
        }

        scanner = new Scanner(System.in);

        // Taken from TellerApp (https://github.students.cs.ubc.ca/CPSC210/TellerApp)
        // enables input of strings with spaces
        scanner.useDelimiter("\r?\n|\r");

    }

    // EFFECTS: sends the user a welcome message
    private void welcome() {
        System.out.println();
        System.out.println("Welcome to " + appName + "!");
    }

    // EFFECTS: sends the user a farewell message
    private void goodbye() {
        System.out.println();
        System.out.println("Thank you for using " + appName + "!");
    }

    // EFFECTS: attempts to save current file system to disk in default location
    private void quit() {
        System.out.println();
        try {
            System.out.println("Attempting to save current file system");
            fileSystem.autoSave();
            System.out.println("Saving current file system succeeded!");
        } catch (FileNotFoundException e) {
            System.out.println("Saving current file system failed");
        }
    }


    // Add Menu:

    // MODIFIES: this
    // EFFECTS: until the user decides to go back to the main menu, shows the add menu options and calls the
    // appropriate submenus for their choice
    private void addMenu() {
        while (true) {
            displayAddMenuOptions();

            String input = getUserInputTrim();

            if (input.equalsIgnoreCase("b") || input.equalsIgnoreCase("back")) {
                break;
            } else {
                handleAddMenuInput(input);
            }
        }
    }

    // EFFECTS: displays the (first level of) add menu options
    private void displayAddMenuOptions() {
        System.out.println();
        System.out.println("Would you like to:");
        System.out.println("  \"fi\": Add a file to the current folder");
        System.out.println("  \"fo\": Add a folder to the current folder");
        System.out.println("  \"l\": Create a new label");
        System.out.println("  \"b\": Back to the main menu");
    }

    // MODIFIES: this
    // EFFECTS: calls the submenu the user selected
    // if their input was invalid, tell them what they inputted was not an option
    private void handleAddMenuInput(String input) {
        if (input.equalsIgnoreCase("fi") || input.equalsIgnoreCase("file")) {
            addFileMenu();
        } else if (input.equalsIgnoreCase("fo") || input.equalsIgnoreCase("folder")) {
            addFolderMenu();
        } else if (input.equalsIgnoreCase("l") || input.equalsIgnoreCase("label")) {
            addLabelMenu();
        } else {
            System.out.println("Your input was not recognized as any of: fi, fo, l, or b");
        }
    }

    // MODIFIES: this
    // EFFECTS: allows the user to add a file from their computer to the program by
    // providing a path and optionally giving it a custom name and labels (if any have been made)
    private void addFileMenu() {
        while (true) {
            System.out.println();
            System.out.println("Please enter the .txt file's path or b to go back");
            System.out.println("It should look like " + FileSystem.EXAMPLE_FILE_PATH + " (writing .txt is optional)");

            String input = getUserInputTrim();
            if (input.equalsIgnoreCase("b") || input.equalsIgnoreCase("back")) {
                break;
            }

            String path = (input.endsWith(".txt") ? input : input + ".txt");
            
            if (FileSystem.isFilePathValid(path)) {
                String nameOfFileOnDisk = File.getNameOfFileOnDiskWithoutExtension(path);
                chooseFileNameThenCreateFileAndAddLabels(nameOfFileOnDisk, path);
                break;
            } else {
                System.out.println("The file path you inputted: \"" + path + "\" was invalid");
            }
        }
    }

    // REQUIRES: actualFileName.isBlank() is false and fileSystem.fileWithNameAlreadyExists(actualFileName) is false
    // MODIFIES: this
    // EFFECTS: lets the user choose a custom file name or use the file's actual name, creates the file in fileSystem,
    // and enables the user to add labels as desired, if any exist
    private void chooseFileNameThenCreateFileAndAddLabels(String actualFileName, String path) {
        // Get the user to choose either actualFileName or a custom file name
        String fileName = chooseFileName(actualFileName);
        
        try {
            fileSystem.createFile(fileName, path);
        } catch (NameIsTakenException e) {
            // Not possible via REQUIRES clause
            throw new RequiresClauseNotMetRuntimeException();
        }
        System.out.println("File with name \"" + fileName + "\" added to current folder ("
                + fileSystem.getCurrentFolderName() + ")");

        if (fileSystem.anyLabelsExist()) {
            try {
                addFileLabels(fileName);
            } catch (NoSuchFileFoundException e) {
                System.out.println("No file named " + fileName + " found");
            }
        }
    }

    // EFFECTS: enables the user to create a custom name for the file they are in the process of creating
    // or use the name of the file on their computer
    private String chooseFileName(String actualFileName) {
        if (fileSystem.containsFile(actualFileName)) {
            // Force the user to use a custom name if there already exists a file with the actual file's name in the
            // current folder
            return chooseAndConfirmCustomFileNameNoGoBack();
        } else if (!actualFileName.isBlank()) {
            while (true) {
                System.out.println();
                System.out.println("Would you like to use the actual file's name: \"" + actualFileName
                        + "\" or create a custom name? Please enter \"a\" or \"c\"");

                String input = getUserInputTrim();

                if (input.equalsIgnoreCase("actual") || input.equalsIgnoreCase("a")) {
                    return actualFileName;
                } else if (input.equalsIgnoreCase("custom") || input.equalsIgnoreCase("c")) {
                    try {
                        return chooseAndConfirmCustomFileName();
                    } catch (UserNoLongerWantsCustomNameException e) {
                        // Continue the loop (where they will presumably enter actual, but this lets
                        // them confirm)
                    }
                }
            }
        } else {
            return chooseAndConfirmCustomFileNameNoGoBack();
        }
    }

    // EFFECTS: enables the user to choose the custom name for their file and then confirms it is correct.
    // Throws UserNoLongerWantsCustomNameException if they no longer wish for a custom name
    private String chooseAndConfirmCustomFileName() throws UserNoLongerWantsCustomNameException {
        while (true) {
            String chosenName = chooseCustomFileName();

            if (confirmNameCorrect(chosenName)) {
                return chosenName;
            }
        }
    }

    // EFFECTS: enables the user to choose the custom name for their file and throws
    // UserNoLongerWantsCustomNameException if they no longer wish for a custom name
    private String chooseCustomFileName() throws UserNoLongerWantsCustomNameException {
        while (true) {
            System.out.println();
            System.out.println("Please enter the custom name or b to go back (if you wish "
                        + "to name your file b or B, enter namefileb)");

            String input = getUserInputTrim();

            if (input.equalsIgnoreCase("b") || input.equalsIgnoreCase("back")) {
                throw new UserNoLongerWantsCustomNameException();
            } else if (input.equalsIgnoreCase("namefileb")) {
                try {
                    return nameFileB();
                } catch (UserNoLongerWantsNameBException e) {
                    // Continue the loop
                }
            } else {
                return input;
            }
        }
    }

    // EFFECTS: enables the user to choose the custom name for their file and then confirms it is correct, but, unlike
    // chooseAndConfirmCustomFileName(), does not let them go back to previous menu to choose the file's actual name
    private String chooseAndConfirmCustomFileNameNoGoBack() {
        while (true) {
            String chosenName = chooseCustomFileNameNoGoBack();

            if (confirmNameCorrect(chosenName)) {
                return chosenName;
            }
        }
    }

    // EFFECTS: enables the user to choose the custom name for their file
    // throws UserNoLongerWantsCustomNameException if they no longer wish for a custom name
    private String chooseCustomFileNameNoGoBack() {
        while (true) {
            System.out.println();
            System.out.println("Please enter the custom name");

            String input = getUserInputTrim();

            if (input.isBlank()) {
                System.out.println("Custom file name was not valid");
            } else if (fileSystem.containsFile(input)) {
                tellUserFileWithNameAlreadyExists(input);
            } else {
                return input;
            }
        }
    }

    // EFFECTS: lets the user name their file b or B or namefileb (or any case variants, i.e. NameFileB)
    private String nameFileB() throws UserNoLongerWantsNameBException {
        while (true) {
            System.out.println();
            System.out.println("Please enter the custom name (b, B, namefileb, namefileB, etc.) "
                        + "or prev to go to the previous menu");

            String input = getUserInputTrim();

            if (input.equalsIgnoreCase("p") || input.equalsIgnoreCase("prev") || input.equalsIgnoreCase("previous")) {
                throw new UserNoLongerWantsNameBException();
            } else if (input.equalsIgnoreCase("b") || input.equalsIgnoreCase("namefileb")) {
                if (fileSystem.containsFile(input)) {
                    tellUserFileWithNameAlreadyExists(input);
                } else {
                    return input;
                }
            } else {
                System.out.println("Your input was not recognized as any of: b, B, namefileb "
                        + "(or namefileb with different capitalization)");
            }
        }
    }
    
    // REQUIRES: fileSystem.fileWithNameAlreadyExists(input) is true
    // EFFECTS: tells the user
    private void tellUserFileWithNameAlreadyExists(String input) {
        String fileAlreadyNamedInputWithCorrectCase = fileSystem.getCapitalizationOfFile(input);
        tellUserThisFolder("already contains a file named " + fileAlreadyNamedInputWithCorrectCase);
    }

    // MODIFIES: this
    // EFFECTS: if there is one label, asks the user if they want to label the new file with that label,
    // if there are more asks the user if they would like to add labels and then which labels they would like to add
    // throws NoSuchFileFoundException if fileSystem does not find a File named fileName
    private void addFileLabels(String fileName) throws NoSuchFileFoundException {
        if (fileSystem.exactlyOneLabelExists()) {
            addTheOnlyCreatedLabel(fileName);
        } else {
            chooseLabels(fileName);
        }
    }

    // REQUIRES: fileSystem.exactlyOneLabelExists() is true and fileSystem.fileWithNameAlreadyExists(fileName) is true
    // MODIFIES: this
    // EFFECTS: enables the user to either label the file with the only current label or not label it with any
    private void addTheOnlyCreatedLabel(String fileName) {
        String theOnlyCurrentLabelName;
        try {
            theOnlyCurrentLabelName = fileSystem.getOnlyLabelName();   
            while (true) {
                System.out.println();
                System.out.println("Would you like to label the file \"" + theOnlyCurrentLabelName
                        + "\"? Please enter y or n");
                
                String input = getUserInputTrim();

                if (input.equalsIgnoreCase("n") || input.equalsIgnoreCase("no")) {
                    break;
                } else if (input.equalsIgnoreCase("y") || input.equalsIgnoreCase("yes")) {
                    fileSystem.labelFile(fileName, theOnlyCurrentLabelName);
                    System.out.println(fileName + " is now labelled " + theOnlyCurrentLabelName);
                    break;
                } else {
                    System.out.println("Your input was not recognized as either of: y or n");
                }
            }
        } catch (NoSuchFileFoundException | NoSuchLabelFoundException e) {
            // Shouldn't happen
            throw new RequiresClauseNotMetRuntimeException();
        }
    }

    // MODIFIES: this
    // EFFECTS: asks the user if they would like to add labels.
    // If they would like to, then lets them add as many aspossible or stop when they're done
    // throws NoSuchFileFoundException if fileSystem does not find a File named fileName
    private void chooseLabels(String fileName) throws NoSuchFileFoundException {
        while (true) {
            System.out.println();
            System.out.println("Would you like to add labels to the file? Please enter y or n");

            String input = getUserInputTrim();

            if (input.equalsIgnoreCase("n") || input.equalsIgnoreCase("no")) {
                break;
            } else if (input.equalsIgnoreCase("y") || input.equalsIgnoreCase("yes")) {
                addAsManyLabelsAsDesiredOrPossible(fileName);
                break;
            } else {
                System.out.println("Your input was not recognized as either of: y or n");
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: enables the user to choose the name of a new folder, ensures it is not already in use, and confirms
    // with the user that the name they inputted is correct.
    // Throws UserNoLongerWantsToCreateFolderException if the user decides they longer wish to create a new folder
    private void addFolderMenu() {
        while (true) {
            String chosenName;
            try {
                chosenName = chooseFolderName();
            } catch (UserNoLongerWantsToCreateFolderException e) {
                break;
            }

            if (confirmNameCorrect(chosenName)) {
                try {
                    fileSystem.createFolder(chosenName);
                    System.out.println("Created folder named \"" + chosenName + "\" in current folder ("
                            + fileSystem.getCurrentFolderName() + ")");
                    break;
                } catch (NameIsBlankException e) {
                    System.out.println("Folder name was not valid");
                } catch (NameIsTakenException e) {
                    tellUserThisFolder("already contains a folder named " + e.getCapitalizationOfTakenName());
                }
            }
        }
    }

    // EFFECTS: enables the user to choose the name of their folder: lets them enter the name of the folder or a
    // specific string if they want to name their folder "B".
    // throws UserNoLongerWantsToCreateFolderException if the user decides they no longer wish to create a folder
    private String chooseFolderName() throws UserNoLongerWantsToCreateFolderException {
        while (true) {
            System.out.println();
            System.out.println("Please enter the folder name or b to go back (if you wish "
                    + "to name your folder b or B, enter namefolderb)");

            String input = getUserInputTrim();

            if (input.equalsIgnoreCase("b") || input.equalsIgnoreCase("back")) {
                throw new UserNoLongerWantsToCreateFolderException();
            } else if (input.equalsIgnoreCase("namefolderb")) {
                try {
                    return nameFolderB();
                } catch (UserNoLongerWantsNameBException e) {
                    // Continue the loop so they can name their folder something else
                }   
            } else {
                return input;
            }
        }
    }

    // EFFECTS: lets the user name their folder b or B or namefolderb (or any case variants, i.e. NameFolderB, 
    // nameFOLDerb, etc.)
    // throws UserNoLongerWantsNameBException if the user no longer wishes to name their folder that way
    private String nameFolderB() throws UserNoLongerWantsNameBException {
        while (true) {
            System.out.println();
            System.out.println("Please enter the folder name (b, B, namefolderb, namefolderB, etc.) "
                    + "or prev to go to the previous menu");

            String input = getUserInputTrim();

            if (input.equalsIgnoreCase("p") || input.equalsIgnoreCase("prev") || input.equalsIgnoreCase("previous")) {
                throw new UserNoLongerWantsNameBException();
            } else if (input.equalsIgnoreCase("b") || input.equalsIgnoreCase("namefolderb")) {
                if (fileSystem.containsFolder(input)) {
                    String folderAlreadyNamedInputWithCorrectCase = fileSystem.getCapitalizationOfFolder(input);
                    tellUserThisFolder("already contains a folder named " + folderAlreadyNamedInputWithCorrectCase);
                } else {
                    return input;
                }
            } else {
                System.out.println("Your input was not recognized as any of: b, B, namefolderb "
                        + "(or namefolderb with different capitalization)");
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: allows the user to input a name for a new label to create. If the name is not already in use creates
    // a new label with that name. Catches exception and breaks out of loop if user no longer wants to create a label
    // Catches name validity exceptions and tells the user their name was invalid, then loops
    private void addLabelMenu() {
        while (true) {
            String chosenName;
            try {
                chosenName = chooseLabelName();
            } catch (UserNoLongerWantsToCreateALabelException e) {
                break;
            }

            try {
                if (confirmNameCorrect(chosenName)) {
                    fileSystem.createLabel(chosenName);
                    System.out.println("Created label named \"" + chosenName);
                    break;
                }
            } catch (NameIsBlankException e) {
                System.out.println("Label name was not valid");
            } catch (NameIsTakenException e) {
                System.out.println("A label already exists named  " + e.getCapitalizationOfTakenName());
            }
        }
    }

    // EFFECTS: enables the user to choose the name of their new label and confirms it is not already in use
    // throws UserNoLongerWantsToCreateALabelException if the user decides they no longer wish to create a new label
    private String chooseLabelName() throws UserNoLongerWantsToCreateALabelException {
        while (true) {
            System.out.println();
            System.out.println("Please enter the label name or b to go back (if you wish "
                    + "to name your label b or B, enter namelabelb)");

            String input = getUserInputTrim();

            if (input.equalsIgnoreCase("b") || input.equalsIgnoreCase("back")) {
                throw new UserNoLongerWantsToCreateALabelException();
            } else if (input.equalsIgnoreCase("namelabelb")) {
                try {
                    return nameLabelB();
                } catch (UserNoLongerWantsNameBException e) {
                    // Continue the loop so they can name their label something else
                }
            } else {
                return input;
            }
        }
    }

    // EFFECTS: lets the user name their label b or B or namelabelb (or any case variants, i.e. NameLabelB, namELABelb,
    // etc.). Confirms no folder already exists with chosen name in this directory. Throws
    // UserNoLongerWantsNameBException if the user decides they no longer want to name their label b or namefileb
    private String nameLabelB() throws UserNoLongerWantsNameBException {
        while (true) {
            System.out.println();
            System.out.println("Please enter the label name (b, B, namelabelb, namelabelB, etc.) "
                    + "or prev to go to the previous menu");

            String input = getUserInputTrim();

            if (input.equalsIgnoreCase("p") || input.equalsIgnoreCase("prev") || input.equalsIgnoreCase("previous")) {
                throw new UserNoLongerWantsNameBException();
            } else if (input.equalsIgnoreCase("b") || input.equalsIgnoreCase("namelabelb")) {
                if (fileSystem.labelExists(input)) {
                    String labelAlreadyNamedInputWithCorrectCase = fileSystem.getCapitalizationOfLabel(input);
                    System.out.println("A label already exists named  " + labelAlreadyNamedInputWithCorrectCase);
                } else {
                    return input;
                }
            } else {
                System.out.println("Your input was not recognized as any of: b, B, namelabelb "
                        + "(or namelabelb with different capitalization)");
            }
        }
    }

    // Edit Menu:

    // MODIFIES: this
    // EFFECTS: allows the user to edit Files, Folders, and Labels in the fileSystem by choosing one of htese groups
    // and inputting the name of the one thye wish to edit
    private void editMenu() {
        while (true) {
            displayEditMenuOptions();

            String input = getUserInputTrim();

            if (input.equalsIgnoreCase("b") || input.equalsIgnoreCase("back")) {
                break;
            } else {
                handleEditMenuInput(input);
            }
        }
    }

    // EFFECTS: displays the (first level of) edit menu options
    private void displayEditMenuOptions() {
        System.out.println();
        System.out.println("Would you like to:");
        System.out.println("  \"fi\": Choose a file (in the current folder - "
                + fileSystem.getCurrentFolderName() + ") to edit");
        System.out.println("  \"fo\": Choose a folder (in the current folder - "
                + fileSystem.getCurrentFolderName() + ") to edit");
        System.out.println("  \"l\": Choose a label to edit");
        System.out.println("  \"b\": Back to the main menu");
    }

    // MODIFIES: this
    // EFFECTS: handles the edit menu input and calls the appropriate submenus as needed, so long as the user would
    // actually be able to do anything when using those menus. Tells user if their input wsas invalid
    private void handleEditMenuInput(String input) {
        if (input.equalsIgnoreCase("fi") || input.equalsIgnoreCase("file")) {
            if (fileSystem.getNamesOfSubfiles().size() == 0) {
                tellUserThisFolder("does not contain any files");
            } else {
                editFileMenu();
            }
        } else if (input.equalsIgnoreCase("fo") || input.equalsIgnoreCase("folder")) {
            if (fileSystem.getNamesOfSubfolders().size() == 0) {
                tellUserThisFolder("does not contain any subfolders");
            } else {
                editFolderMenu();
            }
        } else if (input.equalsIgnoreCase("l") || input.equalsIgnoreCase("label")) {
            if (!fileSystem.anyLabelsExist()) {
                System.out.println("You have not created any labels");
            } else {
                editLabelMenu();
            }
        } else {
            System.out.println("Your input was not recognized as any of: fi, fo, l, or b");
        }
    }

    // MODIFIES: this
    // EFFECTS: allows the user to choose a file in the current directory and then open it, change its name, change
    // its labels, or delete it. User can list files in directory or go back
    private void editFileMenu() {
        while (true) {
            System.out.println();
            System.out.println("Please enter the name of the file you would like to edit, l to list the files in "
                    + "this folder (" + fileSystem.getCurrentFolderName() + "), or b to go back");

            String input = getUserInputTrim();

            if (input.equalsIgnoreCase("b") || input.equalsIgnoreCase("back")) {
                break;
                // Returns to the edit menu
            } else if (input.equalsIgnoreCase("l") || input.equalsIgnoreCase("list")) {
                try {
                    listStringsAlphabetically(fileSystem.getNamesOfSubfiles());
                } catch (ListEmptyException e) {
                    System.out.println("There are no files in this folder");
                }
            } else if (fileSystem.containsFile(input)) {
                editFile(input);
                break;
            } else {
                System.out.println("There is no file named \"" + input + "\" in this folder");
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: shows the user the options for editing their file: open, edit name/labels, delete and go back,
    // handles going back and hands off the rest to handleEditFileMenuInput
    // If for some reason fileName isn't found in fileSystem, goes back to previous menu
    private void editFile(String fileName) {
        while (true) {
            displayEditFileMenuOptions(fileName);

            String input = getUserInputTrim();

            if (input.equalsIgnoreCase("b") || input.equalsIgnoreCase("back")) {
                break;
            } else {
                try {
                    handleEditFileMenuInput(input, fileName);
                } catch (CurrentObjectDeletedException e) {
                    break;
                } catch (NoSuchFileFoundException e) {
                    System.out.println("No file named " + fileName + ". Going back to previous menu");
                    break;
                } catch (NameChangedException e) {
                    fileName = e.getNewName();
                }
            }
        }
    }

    // EFFECTS: displays the (first level of) edit file menu options (open, edit, delete, go back)
    private void displayEditFileMenuOptions(String fileName) {
        System.out.println();
        System.out.println(fileName + " is selected. Would you like to:");
        System.out.println("  \"o\": Open the file in your default text editor");
        System.out.println("  \"e\": Edit the file's name or add/remove labels");
        System.out.println("  \"d\": Delete the file");
        System.out.println("  \"b\": Back to the previous menu");
    }

    // MODIFIES: this
    // EFFECTS: calls the appropriate functions for opening, editing, and deleting the file
    // throws CurrentObjectDeletedException if file was deleted
    // throws NoSuchFileFoundException if fileSystem does not find a File named fileName
    private void handleEditFileMenuInput(String input, String fileName)
            throws CurrentObjectDeletedException, NoSuchFileFoundException, NameChangedException {
        if (input.equalsIgnoreCase("o") || input.equalsIgnoreCase("open")) {
            openFile(fileName);
        } else if (input.equalsIgnoreCase("e") || input.equalsIgnoreCase("edit")) {
            editFileNameAndLabelsMenu(fileName);
        } else if (input.equalsIgnoreCase("d") || input.equalsIgnoreCase("delete")) {
            try {
                deleteFile(fileName);
            } catch (NoSuchFileFoundException e) {
                throw new RequiresClauseNotMetRuntimeException();
            }
            throw new CurrentObjectDeletedException();
        } else {
            System.out.println("Your input was not recognized as any of: o, e, d, or b");
        }
    }

    // REQUIRES: fileSystem.fileAlreadyExists(fileName) is true
    // EFFECTS: opens File named fileName in user's default text editor. Once done, tells user file was opened
    // throws runtime exception if requires clause not met (since it is a checked exception)
    // throws NoSuchFileFoundException if fileSystem does not find a File named fileName
    private void openFile(String fileName) throws NoSuchFileFoundException {
        try {
            fileSystem.openFile(fileName);
            System.out.println(fileSystem.getCurrentFolderName() + " opened");
        } catch (FilePathNoLongerValidException e) {
            System.out.println("File at " + fileSystem.getFilePath(fileName) + " no longer exists");
        }
    }

    // REQUIRES: fileSystem.fileWithNameAlreadyExists(fileName) is true
    // MODIFIES: this
    // EFFECTS: shows the user the options to edit their file name, labels, or to go back, handles going back case,
    // and hands off remaining cases to handleEditFileNameAndLabelsMenuInput
    // throws NoSuchFileFoundException if fileSystem does not find a File named fileName
    private void editFileNameAndLabelsMenu(String fileName) throws NoSuchFileFoundException, NameChangedException {
        while (true) {
            displayEditFileNameAndLabelsOptions(fileName);

            String input = getUserInputTrim();

            if (input.equalsIgnoreCase("b") || input.equalsIgnoreCase("back")) {
                break;
            } else {
                handleEditFileNameAndLabelsMenuInput(input, fileName);
            }
        }
    }

    // EFFECTS: displays the option to change the file's name, add a label, remove a
    // label, and remove all labels
    private void displayEditFileNameAndLabelsOptions(String fileName) {
        System.out.println();
        System.out.println(fileName + " is selected. Would you like to:");
        System.out.println("  \"n\": Change the file's name");
        System.out.println("  \"a\": Add a label");
        System.out.println("  \"r\": Remove a label");
        System.out.println("  \"ra\": Remove all labels");
        System.out.println("  \"b\": Back to the edit file menu");
    }

    // MODIFIES: this
    // EFFECTS: calls appropriate functions for changing the name of the file, adding a label to the file
    // removing a label from the file, and removing all labels from the file
    // throws NoSuchFileFoundException if fileSystem does not find a File named fileName
    private void handleEditFileNameAndLabelsMenuInput(String input, String fileName)
            throws NoSuchFileFoundException, NameChangedException {
        if (input.equalsIgnoreCase("n") || input.equalsIgnoreCase("name")) {
            changeFileName(fileName);
        } else if (input.equalsIgnoreCase("a") || input.equalsIgnoreCase("add")) {
            addAsManyLabelsAsDesiredOrPossible(fileName);
        } else if (input.equalsIgnoreCase("r") || input.equalsIgnoreCase("remove")) {
            removeAsManyLabelsAsDesiredOrPossible(fileName);
        } else if (input.equalsIgnoreCase("ra") || input.equalsIgnoreCase("remove all")) {
            confirmThenRemoveAllLabelsFromFileIfAny(fileName);
        } else {
            System.out.println("Your input was not recognized as any of: n, a, r, ra, or b");
        }
    }

    // MODIFIES: this
    // EFFECTS: gets user input: returns if they input b, returns if their input is already the name of a file in this
    // folder, returns if their input is not a valid name, if it is a valid name then changes the file's name to
    // what they inputted
    // throws NoSuchFileFoundException if fileName is not a file in the current directory
    // throws NameChangedException if the file's name was succesfully changed
    private void changeFileName(String fileName) throws NoSuchFileFoundException, NameChangedException {
        try {
            String newName = chooseAndConfirmCustomFileName();
            fileSystem.setFileName(fileName, newName);
            throw new NameChangedException(newName);
        } catch (UserNoLongerWantsCustomNameException e) {
            System.out.println("Name has not been changed and will remain " + fileName);
        } catch (NameIsTakenException e) {
            tellUserFileWithNameAlreadyExists(e.getCapitalizationOfTakenName());
        } catch (NameIsBlankException e) {
            System.out.println("Custom file name was not valid");
        }
    }

    private void confirmThenRemoveAllLabelsFromFileIfAny(String fileName) throws NoSuchFileFoundException {
        if (!fileSystem.anyLabelsExist()) {
            System.out.println("You have not made any labels");
        } else if (fileSystem.getNumLabelsOnFile(fileName) == 0) {
            System.out.println(fileName + " is not labelled with any labels");
        } else {
            confirmRemoveAllLabels(fileName);
        }
    }

    // MODIFIES: this
    // EFFECTS: confirms that the user wishes to remove all labels from File named fileName, if they do,
    // removes all labels from it, otherwise does nothing
    // throws NoSuchFileFoundException if fileSystem does not find a File named fileName
    private void confirmRemoveAllLabels(String fileName) throws NoSuchFileFoundException {
        if (confirmCompleteAction("remove all labels from " + fileName)) {
            fileSystem.removeAllLabels(fileName);
            System.out.println("All labels removed");
        }
    }

    // MODIFIES: this
    // EFFECTS: confirms that the user wishes to delete File named fileName, if they do, deletes it,
    // otherwise does nothing
    // throws NoSuchFileFoundException if fileSystem does not find a File named fileName
    private void deleteFile(String fileName) throws NoSuchFileFoundException {
        if (confirmCompleteAction("delete " + fileName)) {
            fileSystem.deleteFile(fileName);
        }
    }

    // MODIFIES: this
    // EFFECTS: gets the name of the Folder the user wishes to edit, gets input for listing all Folders in the current
    // directory, or lets the user go back. If the user wants the current files listed, lists them, if they want to go
    // back, goes back, if they input a Folder, calls editFolder to handle the Folder editing options
    private void editFolderMenu() {
        while (true) {
            System.out.println();
            System.out.println(
                    "Please enter the name of the subfolder you would like to edit, l to list the folders in "
                            + "this folder (" + fileSystem.getCurrentFolderName() + "), or b to go back");

            String input = getUserInputTrim();

            if (input.equalsIgnoreCase("b") || input.equalsIgnoreCase("back")) {
                break;
                // Returns to the edit menu
            } else if (input.equalsIgnoreCase("l") || input.equalsIgnoreCase("list")) {
                try {
                    listStringsAlphabetically(fileSystem.getNamesOfSubfolders());
                } catch (ListEmptyException e) {
                    System.out.println("This folder doesn't have any subfolders");
                }
            } else if (fileSystem.containsFolder(input)) {
                editFolder(input);
                break;
            } else {
                System.out.println("There is no file named \"" + input + "\" in this folder");
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: shows the user the options for editing the folder that they selected (open, edit, delete, go back).
    // Handles going back to the previous menu and defers any other options to handleEditFolderMenuInput()
    private void editFolder(String folderName) {
        while (true) {
            displayEditFolderMenuOptions(folderName);

            String input = getUserInputTrim();

            if (input.equalsIgnoreCase("b") || input.equalsIgnoreCase("back")) {
                break;
            } else {
                try {
                    handleEditFolderMenuInput(input, folderName);
                } catch (NewFolderOpenedException | CurrentObjectDeletedException e) {
                    break;
                } catch (NoSuchFolderFoundException e) {
                    System.out.println("No folder named " + folderName + ". Going back to previous menu");
                    break;
                }
            }
        }
    }

    // EFFECTS: displays the edit folder menu options (open, edit, delete, go back)
    private void displayEditFolderMenuOptions(String folderName) {
        System.out.println();
        System.out.println(folderName + " is selected. Would you like to:");
        System.out.println("  \"o\": Open the folder");
        System.out.println("  \"e\": Edit the folders's name");
        System.out.println("  \"d\": Delete the folder");
        System.out.println("  \"b\": Back to the main menu");
    }

    // MODIFIES: this
    // EFFECTS: calls appropriate functions for actions the user wishes to complete, depending on their input
    private void handleEditFolderMenuInput(String input, String folderName) throws NewFolderOpenedException,
            CurrentObjectDeletedException, NoSuchFolderFoundException {
        if (input.equalsIgnoreCase("o") || input.equalsIgnoreCase("open")) {
            fileSystem.openFolder(folderName);
            System.out.println(folderName + " opened");
            throw new NewFolderOpenedException();
        } else if (input.equalsIgnoreCase("e") || input.equalsIgnoreCase("edit")) {
            editFolderNameMenu(folderName);
        } else if (input.equalsIgnoreCase("d") || input.equalsIgnoreCase("delete")) {
            deleteFolderMenu(folderName);
        } else {
            System.out.println("Your input was not recognized as any of: o, e, d, or b");
        }
    }

    // MODIFIES: this
    // EFFECTS: gets the new name the user wishes to name the Folder they selected, checks it is valid and that they're
    // not trying to go to the previous menu, and renames the Folder
    // throws NoSuchFolderFoundException if fileSystem does not find a Folder named folderName
    private void editFolderNameMenu(String folderName)
            throws NoSuchFolderFoundException {
        while (true) {
            System.out.println();
            System.out.println("Please enter the new folder name or b to go back (if you wish "
                    + "to rename your folder to b or B, enter namefolderb)");

            String input = getUserInputTrim();

            if (input.equalsIgnoreCase("b") || input.equalsIgnoreCase("back")) {
                break;
            } else if (input.equalsIgnoreCase("namefolderb")) {
                try {
                    if (setFolderName(folderName, nameFolderB())) {
                        break;
                    }
                } catch (UserNoLongerWantsNameBException e) {
                    // Continue the loop so they can rename their folder something else
                }
            } else {
                if (setFolderName(folderName, input)) {
                    break;
                }
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: sets the name of Folder named folderName to newName and returns true or returns false and tells user
    // why this failed if the name is taken or if their input was blank (empty or whitespace)
    // throws NoSuchFolderFoundException if fileSystem does not find a folder named folderName
    private boolean setFolderName(String folderName, String newName) throws NoSuchFolderFoundException {
        try {
            fileSystem.setFolderName(folderName, newName);
            return true;
        } catch (NameIsTakenException e) {
            tellUserThisFolder("already contains a folder named " + e.getCapitalizationOfTakenName());
        } catch (NameIsBlankException e) {
            System.out.println("Folder name was not valid");
        }
        return false;
    }

    // MODIFIES: this
    // EFFECTS: confirms that the user wants to delete Folder named folderName, if they confrim then then deletes it
    // and throws CurrentObjectDeletedException otherwise does nothing
    // throws NoSuchFolderFoundException if fileSystem does not find a folder named folderName
    private void deleteFolderMenu(String folderName) throws CurrentObjectDeletedException, NoSuchFolderFoundException {
        if (confirmCompleteAction("delete " + folderName)) {
            fileSystem.deleteFolder(folderName);
            System.out.println(folderName + " deleted");
            throw new CurrentObjectDeletedException();
        }
    }

    // MODIFIES: this
    // EFFECTS: shows the user the options for editing a label (opening all files labelled with it, renaming it,
    // and deleting it) and to list all labels and go back. Handles listing labels and going back, and hands remaining
    // options off to editLabel()
    private void editLabelMenu() {
        while (true) {
            System.out.println();
            System.out.println("Please enter the name of the label you would like to edit, "
                    + "l to list all created labels or b to go back");

            String input = getUserInputTrim();

            if (input.equalsIgnoreCase("b") || input.equalsIgnoreCase("back")) {
                break;
                // Returns to the edit menu
            } else if (input.equalsIgnoreCase("l") || input.equalsIgnoreCase("list")) {
                try {
                    listStringsAlphabetically(fileSystem.getNamesOfLabels());
                } catch (ListEmptyException e) {
                    System.out.println("You have not created any labels");
                }
            } else if (fileSystem.labelExists(input)) {
                editLabel(input);
                break;
            } else {
                System.out.println("There is no label named \"" + input + "\"");
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: shows the user the options for editing a label (opening all files labelled with it, renaming it,
    // and deleting it) and going back. Handles going back and hands off remaining options to handleEditLabelMenuInput
    private void editLabel(String labelName) {
        while (true) {
            displayEditLabelMenuOptions(labelName);

            String input = getUserInputTrim();

            if (input.equalsIgnoreCase("b") || input.equalsIgnoreCase("back")) {
                break;
            } else {
                try {
                    handleEditLabelMenuInput(input, labelName);
                } catch (NewFolderOpenedException | CurrentObjectDeletedException e) {
                    break;
                } catch (NoSuchLabelFoundException e) {
                    System.out.println("No label with name " + labelName + " was found. Returning to previous menu");
                    break;
                } catch (NameChangedException e) {
                    labelName = e.getNewName();
                }
            }
        }
    }

    // EFFECTS: displays the edit label menu options (opening all files labelled with it, renaming it, and deleting it)
    private void displayEditLabelMenuOptions(String labelName) {
        System.out.println();
        System.out.println(labelName + " is selected. Would you like to:");
        System.out.println("  \"o\": Open a directory with all files labelled with this label");
        System.out.println("  \"e\": Edit the label's name");
        System.out.println("  \"d\": Delete the label");
        System.out.println("  \"b\": Back to the main menu");
    }

    // MODIFIES: this
    // EFFECTS: calls appropriate functions for action user wishes to complete in terms of editing Label named
    // labelName depending on their input
    // throws NewFolderOpenedException if the user decided to open the selected folder in order to go back mulitple
    // menus
    // throws CurrentObjectDeletedException if the user decided to delete the selected folder in order to go back
    // mulitple menus
    // throws NoSuchLabelFoundException if fileSystem does not find a Label named labelName
    private void handleEditLabelMenuInput(String input, String labelName) throws NewFolderOpenedException,
            CurrentObjectDeletedException, NoSuchLabelFoundException, NameChangedException {
        if (input.equalsIgnoreCase("o") || input.equalsIgnoreCase("open")) {
            fileSystem.openLabel(labelName);
            System.out.println("Current directory is every file labelled " + labelName);
            throw new NewFolderOpenedException();
        } else if (input.equalsIgnoreCase("e") || input.equalsIgnoreCase("edit")) {
            editLabelNameMenu(labelName);
        } else if (input.equalsIgnoreCase("d") || input.equalsIgnoreCase("delete")) {
            deleteLabelMenu(labelName);
        } else {
            System.out.println("Your input was not recognized as any of: o, e, d, or b");
        }
    }

    // MODIFIES: this, label
    // EFFECTS: tells the user to enter new label name, checks if they want to go back or name it b or namelabelb and
    // Label named labelName's name to chosen name
    // throws NoSuchLabelFoundException if fileSystem does not find a Label named labelName
    // throws NameChangedException if the name of the label originally named labelName was changed
    private void editLabelNameMenu(String labelName) throws NoSuchLabelFoundException, NameChangedException {
        while (true) {
            printEditLabelNameMenu();

            String input = getUserInputTrim();

            if (input.equalsIgnoreCase("b") || input.equalsIgnoreCase("back")) {
                break;
            } else {
                renameLabelIfNameValid(input, labelName);
            }
        }
    }

    // EFFECTS: if input is "namelabelb", gets new input from the user to determing if they want to name their label
    // "b", "B", or "namelabelb" (and any of its case variants). If they leave that menu without choosing a name then
    // returns false. If the name they chose is already taken, prints that and returns false, otherwise renames it
    // otherwise if input.isBlank() is true, returns false or if there is already a label named input tells the user
    // that and returns false. If neither of those are true then renames it to input
    // throws NoSuchLabelFoundException if there is no label named labelName
    // throws NameChangedException if/when the user succesesfully changes the name of the label
    private void renameLabelIfNameValid(String input, String labelName)
            throws NoSuchLabelFoundException, NameChangedException {
        if (input.equalsIgnoreCase("namelabelb")) {
            try {
                String newLabelName = nameLabelB();
                fileSystem.setLabelName(labelName, newLabelName);
                System.out.println("Label renamed to " + newLabelName);
                updateCurrentFolderNameIfItIsALabelThatWasRenamed(labelName, newLabelName);
                throw new NameChangedException(newLabelName);
            } catch (UserNoLongerWantsNameBException e) {
                // Continue the loop so they can rename the label something else
            } catch (NameIsTakenException e) {
                System.out.println("A label already exists with name " + e.getCapitalizationOfTakenName());
            }
        } else {
            try {
                fileSystem.setLabelName(labelName, input);
                System.out.println("Label renamed to " + input);
                updateCurrentFolderNameIfItIsALabelThatWasRenamed(labelName, input);
                throw new NameChangedException(input);
            } catch (NameIsBlankException e) {
                System.out.println("Label name was not valid");
            } catch (NameIsTakenException e) {
                System.out.println("A label already exists with name " + e.getCapitalizationOfTakenName());
            }
        }
    }

    // EFFECTS: if current folder is a label (determined by checking if it has no parent) named originalName then
    // renames it to newName to match the fact that the label was renamed
    // throws NoSuchLabelFoundException if no label named newName exists
    private void updateCurrentFolderNameIfItIsALabelThatWasRenamed(String originalName, String newName)
            throws NoSuchLabelFoundException {
        if (fileSystem.currentFolderHasParent()) {
            return;
        }

        if (fileSystem.getCurrentFolderName().equals(originalName)) {
            fileSystem.openLabel(newName);
        }
    }

    // EFFECTS: prints out what the edit label name menu options
    private void printEditLabelNameMenu() {
        System.out.println();
        System.out.println("Please enter the new label name or b to go back (if you wish "
                + "to rename your label to b or B, enter namelabelb)");
    }

    // MODIFIES: this
    // EFFECTS: confirms that the user wants to delete label, if they do, deletes it and throws
    // CurrentObjectDeletedException otherwise does nothing 
    // throws NoSuchLabelFoundException if fileSystem does not find a Label named labelName
    private void deleteLabelMenu(String labelName) throws CurrentObjectDeletedException, NoSuchLabelFoundException {
        if (confirmCompleteAction("delete " + labelName)) {
            fileSystem.deleteLabel(labelName);
            System.out.println(labelName + " has been deleted");
            throw new CurrentObjectDeletedException();
        }
    }


    // Load Menu:

    // MODIFIES: this
    // EFFECTS: gives the user the option to load the autosave, load a save in a custom location, or go back
    // makes the given event happen depending on their input
    private void loadMenu() {
        while (true) {
            displayLoadMenuOptions();

            String input = getUserInputTrim();

            if (input.equalsIgnoreCase("b") || input.equalsIgnoreCase("back")) {
                break;
            } else {
                try {
                    handleLoadMenuInput(input);
                } catch (NewFolderOpenedException e) {
                    break;
                }
            }
        }
    }

    // EFFECTS: gives the user the option to load the autosave, load a save in a custom location, or go back
    private void displayLoadMenuOptions() {
        System.out.println();
        System.out.println("Would you like to:");
        System.out.println("  \"a\": Load the autosave (the file system that was automatically saved on last quit)");
        System.out.println("  \"c\": Load a save from a custom location");
        System.out.println("  \"b\": Back to the main menu");
    }

    // MODIFIES: this
    // EFFECTS: calls the submenu the user selected if their input was invalid, tell them that what they inputted was
    // not an option
    // throws NewFolderOpenedException if a new file system was loaded
    private void handleLoadMenuInput(String input) throws NewFolderOpenedException {
        if (input.equalsIgnoreCase("a") || input.equalsIgnoreCase("autosave")) {
            loadAutosave();
        } else if (input.equalsIgnoreCase("c") || input.equalsIgnoreCase("custom")) {
            loadCustomSaveMenu();
        } else {
            System.out.println("Your input was not recognized as any of: a, c, or b");
        }
    }

    // MODIFIES: this
    // EFFECTS: tries to load the autosave. If it was successful, replaces fileSystem with it, tells user, and throws
    // NewFolderOpenedException
    // If it was unsuccessful tells user
    private void loadAutosave() throws NewFolderOpenedException {
        try {
            FileSystem loadedFileSystem = FileSystem.autoLoad();
            this.fileSystem = loadedFileSystem;
            System.out.println("File system successfully loaded from autosave!");
            throw new NewFolderOpenedException();
        } catch (IOException e) {
            System.out.println("There was an issue with the location of the autosave");
        } catch (InvalidJsonException e) {
            System.out.println("The autosave did not represent a valid file system");
        }
    }

    // MODIFIES: this
    // EFFECTS: asks the user for the path of the save they would like to load then attempts to load it
    // throws NewFolderOpenedException if a new file system was loaded
    private void loadCustomSaveMenu() throws NewFolderOpenedException {
        while (true) {
            System.out.println();
            System.out.println("Please enter the path of the save or \"b\" to go back");
            System.out.println("It should look like " + FileSystem.EXAMPLE_SAVE_PATH + " (writing .json is optional)");

            String input = getUserInputTrim();
            if (input.equalsIgnoreCase("b") || input.equalsIgnoreCase("back")) {
                break;
            }

            String path = (input.endsWith(".json") ? input : input + ".json");
            
            loadManualSave(path);
        }
    }

    // MODIFIES: this
    // EFFECTS: attempts to load save at path. Tells the user if it succeeded or failed and some idea of why
    // throws NewFolderOpenedException if a new file system was loaded
    private void loadManualSave(String path) throws NewFolderOpenedException {
        if (!FileSystem.isFilePathValid(path)) {
            System.out.println("The file path you inputted: \"" + path + "\" was invalid");
            return;
        } else {
            try {
                FileSystem loadedFileSystem = FileSystem.manuallyLoad(path);
                this.fileSystem = loadedFileSystem;
                System.out.println("File system successfully loaded from save file at " + path + "!");
                throw new NewFolderOpenedException();
            } catch (IOException e) {
                System.out.println("There was an issue with the location or file type of the save file at " + path);
            } catch (InvalidJsonException e) {
                System.out.println("The save file at " + path + " did not represent a valid file system");
            }
        }
    }


    // Navigate Menu:

    // MODIFIES: this
    // EFFECTS: shows the user the navigate menu options (go: to root folder, up one level, down one level) and the
    // option to go back to the main menu. Handles going back to the main menu and hands off other options to 
    // handleNavigateMenuInput()
    private void navigateMenu() {
        while (true) {
            displayNavigateMenuOptions();

            String input = getUserInputTrim();

            if (input.equalsIgnoreCase("b") || input.equalsIgnoreCase("back")) {
                break;
            } else {
                try {
                    handleNavigateMenuInput(input);
                } catch (NewFolderOpenedException e) {
                    break;
                }
            }
        }
    }

    // EFFECTS: displays the navigate menu options (go: to root folder, up one level, down one level) and going back
    private void displayNavigateMenuOptions() {
        System.out.println();
        System.out.println("You are in folder " + fileSystem.getCurrentFolderName() + ". Would you like to:");
        System.out.println("  \"r\": Jump to the root folder (base folder)");
        try {
            System.out.println("  \"u\": Go up one level of folders to " + fileSystem.getParentFolderName());
        } catch (NoSuchFolderFoundException e) {
            // Continue, just without showing this option
        }
        if (!fileSystem.getNamesOfSubfolders().isEmpty()) {
            System.out.println("  \"o\": Open a folder in this folder (" + fileSystem.getCurrentFolderName() + ")");
        }
        System.out.println("  \"b\": Back to the main menu");
    }

    // MODIFIES: this
    // EFFECTS: calls appropriate functions for actions the user wishes to complete, based on what they inputted
    // throws NoSuchFileFoundException if fileSystem does not find a File named input
    // throws NoSuchFolderFoundException if fileSystem does not find a Folder named input
    private void handleNavigateMenuInput(String input) throws NewFolderOpenedException {
        if (input.equalsIgnoreCase("r") || input.equalsIgnoreCase("root")) {
            fileSystem.openRootFolder();
            System.out.println("root opened");
            throw new NewFolderOpenedException();
        } else if (fileSystem.currentFolderHasParent() && (input.equalsIgnoreCase("u")
                || input.equalsIgnoreCase("up"))) {
            try {
                fileSystem.goUpOneDirectoryLevel();
            } catch (NoSuchFolderFoundException e) {
                // Checked in condition
            }
            System.out.println(fileSystem.getCurrentFolderName() + " opened");
            throw new NewFolderOpenedException();
        } else if (!fileSystem.getNamesOfSubfolders().isEmpty()
                && input.equalsIgnoreCase("o") || input.equalsIgnoreCase("open")) {
            getInputToOpenFolder();
        } else {
            System.out.println("Your input was not recognized as any of: r, "
                    + (fileSystem.currentFolderHasParent() ? "u, " : "") + "o, or b");
        }
    }

    // Open Menu:

    // MODIFIES: this
    // EFFECTS: shows the user the open menu options (open file, folder, label, or go back)
    private void openMenu() {
        while (true) {
            displayOpenMenuOptions();

            String input = getUserInputTrim();

            if (input.equalsIgnoreCase("b") || input.equalsIgnoreCase("back")) {
                break;
            } else {
                try {
                    handleOpenMenuInput(input);
                } catch (NewFolderOpenedException e) {
                    break;
                } catch (NoSuchFileFoundException e) {
                    System.out.println("No file named " + input + " was found. Repeating menu options");
                }  catch (NoSuchFolderFoundException e) {
                    System.out.println("No folder named " + input + " was found. Repeating menu options");
                }
            }
        }
    }

    // EFFECTS: displays the open menu options (open file, folder, label, or go back)
    private void displayOpenMenuOptions() {
        System.out.println();
        System.out.println("You are in folder " + fileSystem.getCurrentFolderName() + ". Would you like to:");
        System.out.println("  \"fi\": Open a file in the current folder");
        System.out.println("  \"fo\": Open a folder in the current folder");
        System.out.println("  \"lf\": Open a directory containing all files labelled with a certain label");
        System.out.println("  \"r\": Open recently-opened files, folders, or recently-viewed labels");
        System.out.println("  \"b\": Back to the main menu");
    }

    // MODIFIES: this
    // EFFECTS: calls functions for submenus depending on what type of object the user is trying to open
    // throws NoSuchFileFoundException if fileSystem does not find a File named input
    // throws NoSuchFolderFoundException if fileSystem does not find a Folder named input
    private void handleOpenMenuInput(String input)
            throws NewFolderOpenedException, NoSuchFileFoundException, NoSuchFolderFoundException {
        if (input.equalsIgnoreCase("fi") || input.equalsIgnoreCase("file")) {
            if (fileSystem.getNamesOfSubfiles().size() == 0) {
                tellUserThisFolder("does not contain any files");
            } else {
                getInputToOpenFile();
            }
        } else if (input.equalsIgnoreCase("fo") || input.equalsIgnoreCase("folder")) {
            if (fileSystem.getNamesOfSubfolders().size() == 0) {
                tellUserThisFolder("does not contain any subfolders");
            } else {
                getInputToOpenFolder();
            }
        } else if (input.equalsIgnoreCase("lf") || input.equalsIgnoreCase("labelled files")) {
            if (!fileSystem.anyLabelsExist()) {
                System.out.println("You have not created any labels");
            } else {
                getInputToOpenLabel();
            }
        } else if (input.equalsIgnoreCase("r") || input.equalsIgnoreCase("recent")) {
            openRecentMenu();
        } else {
            System.out.println("Your input was not recognized as any of: fi, fo, lf, r, or b");
        }
    }

    // EFFECTS: lets the user search for a file in the current directory and opens it if one found, list all
    // subfiles of the currently-opened folder, and go back
    // throws NoSuchFileFoundException if fileSystem does not find a File named input
    private void getInputToOpenFile() throws NoSuchFileFoundException {
        while (true) {
            System.out.println();
            System.out.println("Please enter the name of the file to open, l to list the options, or b to go back");

            String input = getUserInputTrim();

            if (input.equalsIgnoreCase("b") || input.equalsIgnoreCase("back")) {
                break;
            } else if (input.equalsIgnoreCase("l") || input.equalsIgnoreCase("list")) {
                listFilesAlphabeticallyTellUserIfNone();
            } else if (input.isBlank()) {
                System.out.println("File name was not valid");
            } else if (fileSystem.containsFile(input)) {
                try {
                    fileSystem.openFile(input);
                    System.out.println(fileSystem.getCapitalizationOfFile(input) + " opened");
                    break;
                } catch (FilePathNoLongerValidException e) {
                    System.out.println("File was moved or deleted");
                }
            } else {
                tellUserThisFolder("does not contain a file named " + input);
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: gets input from the user so they can decide to open a recently-opened File, Folder, or Label or go back
    // handles going back and hands off the rest to handleOpenRecentMenuInput
    private void openRecentMenu() throws NewFolderOpenedException {
        while (true) {
            displayOpenRecentMenuOptions();

            String input = getUserInputTrim();

            if (input.equalsIgnoreCase("b") || input.equalsIgnoreCase("back")) {
                break;
            } else {
                handleOpenRecentMenuInput(input);
            }
        }
    }

    // EFFECTS: displays the open recent menu options (open recently-opened file, folder or label or go back)
    private void displayOpenRecentMenuOptions() {
        System.out.println();
        System.out.println("  \"fi\": Open a recently-opened file");
        System.out.println("  \"fo\": Open a recently-opened folder");
        System.out.println("  \"lf\": Show all files labelled with a recently-viewed label");
        System.out.println("  \"b\": Back to the open menu");
    }

    // MODIFIES: this
    // EFFECTS: calls the appropriate function such that the user can choose a recently-opened File, Folder, or Label
    private void handleOpenRecentMenuInput(String input) throws NewFolderOpenedException {
        if (input.equalsIgnoreCase("fi") || input.equalsIgnoreCase("file")) {
            getInputToOpenRecentFile();
        } else if (input.equalsIgnoreCase("fo") || input.equalsIgnoreCase("folder")) {
            getInputToOpenRecentFolder();
        } else if (input.equalsIgnoreCase("lf") || input.equalsIgnoreCase("labelled files")) {
            getInputToOpenRecentLabel();
        } else {
            System.out.println("Your input was not recognized as any of: fi, fo, l, or b");
        }
    }

    // EFFECTS: lists the names of the Files that have been opened recently then gets input from the user. If it is b,
    // goes back, otherwise opens file named input, if one has been recently-opened and still exists
    private void getInputToOpenRecentFile() {
        while (true) {
            System.out.println();
            System.out.println("Please input the name of the recently-opened file you would like to open "
                    + "or b to go back");
            try {
                List<String> recentlyOpenedFileNames = fileSystem.getNamesOfRecentlyOpenedFiles();
                System.out.print("Recently-opened files: ");
                listStringsInOrder(recentlyOpenedFileNames);
            } catch (ListEmptyException e) {
                System.out.println("No files have been opened recently. Returning to previous menu.");
                break;
            }

            String input = getUserInputTrim();

            if (input.equalsIgnoreCase("b") || input.equalsIgnoreCase("back")) {
                break;
            } else {
                handleOpenRecentFileInput(input);
            }
        }
    }

    // EFFECTS: opens File named fileName if one has been opened recently. Tells user if their input was invalid or if
    // the file was moved or deleted
    private void handleOpenRecentFileInput(String fileName) {
        try {
            fileSystem.openRecentlyOpenedFile(fileName);
        } catch (NoSuchFileFoundException e) {
            System.out.println("No file named " + fileName + " has been opened recently.");
        } catch (FilePathNoLongerValidException e) {
            System.out.println("File was moved or deleted");
        }
    }

    // MODIFIES: this
    // EFFECTS: lists the names of the Folders that have been opened recently then gets input from the user. If it is b,
    // goes back, otherwise opens Folder named input, if one has been recently-opened
    private void getInputToOpenRecentFolder() throws NewFolderOpenedException {
        while (true) {
            System.out.println();
            System.out.println("Please input the name of the recently-opened folder you would like to open "
                    + "or b to go back");
            try {
                List<String> recentlyOpenedFolderNames = fileSystem.getNamesOfRecentlyOpenedFolders();
                System.out.print("Recently-opened folders: ");
                listStringsInOrder(recentlyOpenedFolderNames);
            } catch (ListEmptyException e) {
                System.out.println("No folders have been opened recently. Returning to previous menu.");
                break;
            }

            String input = getUserInputTrim();

            if (input.equalsIgnoreCase("b") || input.equalsIgnoreCase("back")) {
                break;
            } else {
                handleOpenRecentFolderInput(input);
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: opens Folder named folderName if one has been opened recently. Tells user if their input was invalid
    private void handleOpenRecentFolderInput(String folderName) throws NewFolderOpenedException {
        try {
            fileSystem.openRecentlyOpenedFolder(folderName);
            throw new NewFolderOpenedException();
        } catch (NoSuchFolderFoundException e) {
            System.out.println("No folder named " + folderName + " has been opened recently.");
        }
    }

    // MODIFIES: this
    // EFFECTS: lists the names of the Labels that have been 'opened' recently then gets input from the user. If it is
    // b, goes back, otherwise opens new Folder with all files labelled with Label named input, if one has been opened
    // recently
    private void getInputToOpenRecentLabel() throws NewFolderOpenedException {
        while (true) {
            System.out.println();
            System.out.println("Please input the name of the label for which you have recently viewed files labelled"
                    + " with it or b to go back");
            try {
                List<String> recentlyOpenedLabelNames = fileSystem.getNamesOfRecentlyOpenedLabels();
                System.out.print("Recently-viewed labels: ");
                listStringsInOrder(recentlyOpenedLabelNames);
            } catch (ListEmptyException e) {
                System.out.println("No labels have had files labelled with them viewed recently. "
                        + "Returning to previous menu.");
                break;
            }

            String input = getUserInputTrim();

            if (input.equalsIgnoreCase("b") || input.equalsIgnoreCase("back")) {
                break;
            } else {
                handleOpenRecentLabelInput(input);
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: opens new Folder named labelName containing all Files labelled with Label named labelName, if one has
    // been opened recently. Tells user if their input was invalid
    private void handleOpenRecentLabelInput(String labelName) throws NewFolderOpenedException {
        try {
            fileSystem.openRecentlyOpenedLabel(labelName);
            throw new NewFolderOpenedException();
        } catch (NoSuchLabelFoundException e) {
            System.out.println("No label named " + labelName + " has had files labelled with it viewed recently.");
        }
    }

    // Save Menu:
    // EFFECTS: TODO: Save current state to file
    private void saveMenu() {
        System.out.println("Not implemented...");
    }

    /* 
     *   Methods common to multiple menus:
     */

    // MODIFIES: fileSystem
    // EFFECTS: lets the user search for a folder in the current directory and opens it if one found, list all
    // subfolders of the currently-opened folder, and go back
    private void getInputToOpenFolder() throws NewFolderOpenedException {
        while (true) {
            System.out.println();
            System.out.println("Please enter the name of the folder to open, l to list the options, or b to go back");

            String input = getUserInputTrim();

            if (input.equalsIgnoreCase("b") || input.equalsIgnoreCase("back")) {
                break;
            } else if (input.equalsIgnoreCase("l") || input.equalsIgnoreCase("list")) {
                try {
                    listStringsAlphabetically(fileSystem.getNamesOfSubfolders());
                } catch (ListEmptyException e) {
                    tellUserThisFolder("does not contain any subfolders");
                }
            } else if (input.isBlank()) {
                System.out.println("Folder name was not valid");
            } else {
                openFolder(input);
            }
        }
    }

    // EFFECTS: opens Folder named folderName. If successful, tells the user it was opened and throws
    // NewFolderOpenedException to go back to the correct menu. If unsuccessful, tells the user there is no Folder
    // named folderName
    private void openFolder(String folderName) throws NewFolderOpenedException {
        try {
            fileSystem.openFolder(folderName);
            System.out.println(fileSystem.getCurrentFolderName() + " opened");
            throw new NewFolderOpenedException();
        } catch (NoSuchFolderFoundException e) {
            tellUserThisFolderDoesNotContainFolderNamed(folderName);
        }
    }

    // EFFECTS: tells the user the current Folder does not contain a Folder named input
    private void tellUserThisFolderDoesNotContainFolderNamed(String input) {
        tellUserThisFolder("does not contain a subfolder named " + input);
    }

    // MODIFIES: fileSystem
    // EFFECTS: enables the user to open a brand-new folder (that is not saved) containing all files labelled with the
    // label of their choice, to list all labels they have created, or to go back
    private void getInputToOpenLabel() throws NewFolderOpenedException {
        while (true) {
            System.out.println();
            System.out.println("Please enter the name of the label to open, l to list the options, or b to go back");

            String input = getUserInputTrim();

            if (input.equalsIgnoreCase("b") || input.equalsIgnoreCase("back")) {
                break;
            } else if (input.equalsIgnoreCase("l") || input.equalsIgnoreCase("list")) {
                try {
                    listStringsAlphabetically(fileSystem.getNamesOfLabels());
                } catch (ListEmptyException e) {
                    System.out.println("You have not made any labels");
                }
            } else if (input.isBlank()) {
                System.out.println("Label name was not valid");
            } else {
                openLabel(input);
            }
        }
    }

    // EFFECTS: opens Label named labelName. If successful, tells the user it was opened and throws
    // NewFolderOpenedException to go back to the correct menu. If unsuccessful, tells the user there is no Label
    // named labelName
    private void openLabel(String labelName) throws NewFolderOpenedException {
        try {
            fileSystem.openLabel(labelName);
            System.out.println(fileSystem.getCurrentFolderName() + " opened");
            throw new NewFolderOpenedException();
        } catch (NoSuchLabelFoundException e) {
            System.out.println("There is no label named \"" + labelName + "\"");
        }
    }

    // Confirmations:
    // EFFECTS: returns true if the user confirms that they want to complete action,
    // returns false if they confirm they do not want to
    /**
     * Prints out: "Are you sure you want to {action}? Please enter yes or no"
     */
    private boolean confirmCompleteAction(String action) {
        while (true) {
            System.out.println();
            System.out.println("Are you sure you want to " + action + "? Please enter yes or no");

            String input = getUserInputTrim();

            if (input.equalsIgnoreCase("yes")) {
                return true;
            } else if (input.equalsIgnoreCase("no")) {
                return false;
            } else {
                System.out.println("Your input was not recognized as either of: \"yes\" or \"no\"");
            }
        }
    }

    // EFFECTS: returns true if the user confirms that chosenName is correct,
    // returns false if the user confirms chosenName is incorrect
    /**
     * Prints out: "Is the name $name correct? Please enter y or n"
     */
    private boolean confirmNameCorrect(String name) {
        while (true) {
            System.out.println();
            System.out.println("Is the name \"" + name + "\" correct? Please enter y or n");

            String input = getUserInputTrim();

            if (input.equalsIgnoreCase("y") || input.equalsIgnoreCase("yes")) {
                return true;
            } else if (input.equalsIgnoreCase("n") || input.equalsIgnoreCase("no")) {
                return false;
            } else {
                System.out.println("Your input was not recognized as either of: y or n");
            }
        }
    }

    // Labels (for both add and edit menus):

    // EFFECTS: loops until the user has put every label on File named fileName or no longer wishes to add labels.
    // Enables the user to add any labels they have created to the File named fileName
    // throws NoSuchFileFoundException if fileSystem does not find a File named fileName
    private void addAsManyLabelsAsDesiredOrPossible(String fileName)
            throws NoSuchFileFoundException {
        if (fileSystem.getNumLabelsNotOnFile(fileName) > 1) {
            try {
                chooseLabelsToAdd(fileName);
                addLastRemainingLabel(fileName);
            } catch (UserNoLongWantsToChangeLabelsException e) {
                return;
            } catch (NoSuchLabelFoundException e) {
                System.out.println("No label named that exists");
            }
        } else if (fileSystem.getNumLabelsNotOnFile(fileName) == 1) {
            try {
                addLastRemainingLabel(fileName);
            } catch (UserNoLongWantsToChangeLabelsException e) {
                return;
            } catch (NoSuchLabelFoundException e) {
                System.out.println("No label named that exists");
            }
        } else {
            explainWhyNoLabelsCanBeAdded(fileName);
        }
    }

    // EFFECTS: lets the user label File named fileName with as many labels as they'd like to
    // while there is more than one option
    // throws UserNoLongWantsToChangeLabelsException if the user says no to changing labels
    // throws NoSuchFileFoundException if fileSystem does not find a File named fileName
    // throws NoSuchLabelFoundException if fileSystem does not find a Label named input
    private void chooseLabelsToAdd(String fileName)
            throws UserNoLongWantsToChangeLabelsException, NoSuchFileFoundException, NoSuchLabelFoundException {
        while (fileSystem.getNumLabelsNotOnFile(fileName) > 1) {
            System.out.println();
            System.out.println("Enter the name of the label you would like to add to the file, "
                    + "enter l to list the available labels, or enter b to stop adding labels");

            String input = getUserInputTrim();

            if (input.equalsIgnoreCase("b") || input.equalsIgnoreCase("back")) {
                throw new UserNoLongWantsToChangeLabelsException();
            } else if (input.equalsIgnoreCase("l") || input.equalsIgnoreCase("list")) {
                try {
                    listStringsAlphabetically(fileSystem.getNamesOfLabelsNotOnFile(fileName));
                } catch (ListEmptyException e) {
                    // Not going to happen since it has size > 1
                }
            } else if (fileSystem.labelExists(input)) {
                addLabelToFile(fileName, input);
            } else {
                System.out.println("Your input was not recognized as a label, l, or b");
            }
        }
    }

    // MODIFIES: fileSystem (specifically file named fileName)
    // EFFECTS: adds the Label named labelName to the File named fileName and prints that this was done or
    // if that file is already labelled with that label then prints that fact
    // throws NoSuchFileFoundException if fileSystem does not find a File named fileName
    // throws NoSuchLabelFoundException if fileSystem does not find a Label named labelName
    private void addLabelToFile(String fileName, String labelName)
            throws NoSuchFileFoundException, NoSuchLabelFoundException {
        if (!fileSystem.fileLabelled(fileName, labelName)) {
            fileSystem.labelFile(fileName, labelName);
            System.out.println(fileName + " is now labelled " + labelName);
        } else {
            System.out.println("File is already labelled " + labelName);
        }
    }

    // MODIFIES: fileSystem (specifically File named fileName and Label named labelName)
    // EFFECTS: lets the user choose whether to add the last label to the file or
    // not
    // throws UserNoLongWantsToChangeLabelsException if the user says no to changing labels
    // throws NoSuchFileFoundException if fileSystem does not find a File named fileName
    // throws NoSuchLabelFoundException if fileSystem does not find the appropriate Label
    private void addLastRemainingLabel(String fileName)
            throws UserNoLongWantsToChangeLabelsException, NoSuchFileFoundException, NoSuchLabelFoundException {
        String onlyLabel = fileSystem.getNamesOfLabelsNotOnFile(fileName).get(0);
        while (true) {
            System.out.println();
            System.out.println("Would you like to label the file \"" + onlyLabel
                    + "\"? (This is the only label not on this file) y or n or " + onlyLabel);

            String input = getUserInputTrim();

            if (input.equalsIgnoreCase("n") || input.equalsIgnoreCase("no")) {
                throw new UserNoLongWantsToChangeLabelsException();
            } else if (input.equalsIgnoreCase("y") || input.equalsIgnoreCase("yes")
                    || input.equalsIgnoreCase(onlyLabel)) {
                addLabelToFile(fileName, fileSystem.getNamesOfLabelsNotOnFile(fileName).get(0));
                break;
            } else {
                System.out.println("Your input was not recognized as \"y\" or \"n\"");
            }
        }
    }

    // EFFECTS: if the user has created any labels then tells the user that fileName already has every label
    // if the user has not created any labels then tells them that
    private void explainWhyNoLabelsCanBeAdded(String fileName) {
        if (fileSystem.getNumLabels() > 0) {
            System.out.println(fileName + " already has every label");
        } else {
            System.out.println("You have not made any labels");
        }
    }

    // MODIFIES: fileSystem (specifically File named fileName and Label named labelName)
    // EFFECTS: if there is more than 1 label not on the file, let the user pick which label to remove and
    // loop until they no longer want to remove labels or they have removed all but the last label
    // If there is only one label not on the file, asks the user if they would like to remove it
    // Otherwise tells the user that there are no labels on the file
    // throws NoSuchFileFoundException if fileSystem does not find a File named fileName
    // throws NoSuchLabelFoundException if fileSystem does not find an appropriate Label
    private void removeAsManyLabelsAsDesiredOrPossible(String fileName) throws NoSuchFileFoundException {
        if (fileSystem.getNumLabelsOnFile(fileName) > 1) {
            try {
                chooseLabelsToRemove(fileName);
                removeOnlyLabel(fileName);
            } catch (UserNoLongWantsToChangeLabelsException e) {
                return;
            } catch (NoSuchLabelFoundException e) {
                System.out.println("No label named that exists");
            }
        } else if (fileSystem.getNumLabelsOnFile(fileName) == 1) {
            try {
                removeOnlyLabel(fileName);
            } catch (UserNoLongWantsToChangeLabelsException e) {
                return;
            } catch (NoSuchLabelFoundException e) {
                System.out.println("No label named that exists");
            }
        } else {
            System.out.println(fileName + " already is not labelled with any labels");
        }
    }

    // EFFECTS: while there are 2 or more labels on File named fileName, lets the user remove as many as they'd like
    // to, list the labels on that File, or go back
    // throws UserNoLongWantsToChangeLabelsException if the user says no to changing labels
    // throws NoSuchFileFoundException if fileSystem does not find a File named fileName
    // throws NoSuchLabelFoundException if fileSystem does not find an approriate Label
    private void chooseLabelsToRemove(String fileName)
            throws UserNoLongWantsToChangeLabelsException, NoSuchFileFoundException, NoSuchLabelFoundException {
        while (fileSystem.getNumLabelsOnFile(fileName) > 1) {
            System.out.println();
            System.out.println("Enter the name of the label you would like to remove to the file, "
                    + "enter l to list the labels the file is labelled with, or enter b to stop removing labels");

            String input = getUserInputTrim();

            if (input.equalsIgnoreCase("b") || input.equalsIgnoreCase("back")) {
                throw new UserNoLongWantsToChangeLabelsException();
            } else if (input.equalsIgnoreCase("l") || input.equalsIgnoreCase("list")) {
                try {
                    listStringsAlphabetically(fileSystem.getNamesOfLabelsOnFile(fileName));
                } catch (ListEmptyException e) {
                    // Not going to happen since it has size > 1
                }
            } else if (fileSystem.labelExists(input)) {
                removeLabelFromFile(fileName, input);
            } else {
                System.out.println("Your input was not recognized as a label, l, or b");
            }
        }
    }

    // MODIFIES: fileSystem (specifically File named fileName and Label named labelName)
    // EFFECTS: removes the Label named labelName from the File named fileName and prints that this was done
    // or if the file was already not labelled with that label, tell the user that
    // throws NoSuchFileFoundException if fileSystem does not find a File named fileName
    // throws NoSuchLabelFoundException if fileSystem does not find a Label named labelName
    private void removeLabelFromFile(String fileName, String labelName)
            throws NoSuchFileFoundException, NoSuchLabelFoundException {
        if (fileSystem.fileLabelled(fileName, labelName)) {
            fileSystem.unlabelFile(fileName, labelName);
            System.out.println(fileName + " is no longer labelled " + labelName);
        } else {
            System.out.println("File is already not labelled " + labelName);
        }
    }

    // REQUIRES: File named fileName has exactly 1 Label on it
    // MODIFIES: fileSystem (specifically File named fileName and Label named labelName)
    // EFFECTS: asks the user if they would like to remove the last label from File named fileName
    // if they say no then finish, if they say yes then remove it
    // throws UserNoLongWantsToChangeLabelsException if the user says no to changing labels
    // throws NoSuchFileFoundException if fileSystem does not find a File named fileName
    // throws NoSuchLabelFoundException if fileSystem does not find an appropriate Label
    private void removeOnlyLabel(String fileName)
            throws UserNoLongWantsToChangeLabelsException, NoSuchFileFoundException, NoSuchLabelFoundException {
        String labelName = fileSystem.getNamesOfLabelsOnFile(fileName).get(0);
        while (true) {
            System.out.println();
            System.out.println("Would you like to remove the label " + labelName + " from the file \""
                    + fileName + "\"? (This is the only label on this file) y or n");

            String input = getUserInputTrim();

            if (input.equalsIgnoreCase("n") || input.equalsIgnoreCase("no")) {
                throw new UserNoLongWantsToChangeLabelsException();
            } else if (input.equalsIgnoreCase("y") || input.equalsIgnoreCase("yes")) {
                fileSystem.unlabelFile(fileName, labelName);
                System.out.println(fileName + " is no longer labelled " + labelName);
                break;
            } else {
                System.out.println("Your input was not recognized as a y or n");
            }
        }
    }

    
    /* 
    *  General helpers:
    */

    // User Input:
    // EFFECTS: gets input from the user and trims it
    private String getUserInputTrim() {
        String input = "";
        input += scanner.next();
        input = input.trim();
        return input;
    }

    // EFFECTS: prints "This folder (folderName) + message"
    private void tellUserThisFolder(String message) {
        System.out.println("This folder (" + fileSystem.getCurrentFolderName() + ") " + message);
    }

    // EFFECTS: prints it out separated by commas (with no comma after the last element)
    private void listStringsInOrder(List<String> strings) throws ListEmptyException {
        if (strings.isEmpty()) {
            throw new ListEmptyException();
        }
        
        int indexOfLastString = strings.size() - 1;
        String lastString = strings.get(indexOfLastString);
        strings.remove(indexOfLastString);

        for (String string : strings) {
            System.out.print(string + ", ");
        }
        System.out.println(lastString);
    }

    // EFFECTS: alphabetizes strings, ignoring case, and then prints it out separated by commas
    // (with no comma after the last element)
    private void listStringsAlphabetically(List<String> strings) throws ListEmptyException {
        if (strings.isEmpty()) {
            throw new ListEmptyException();
        }

        // Code taken from Stack Overflow:
        // https://stackoverflow.com/questions/8432581/how-to-sort-a-listobject-alphabetically-using-object-name-field
        strings.sort(Comparator.comparing(String::toLowerCase));

        listStringsInOrder(strings);
    }

    // EFFECTS: lists out all of the files in this folder or a message if there are none
    private void listFilesAlphabeticallyTellUserIfNone() {
        try {
            listStringsAlphabetically(fileSystem.getNamesOfSubfiles());
        } catch (ListEmptyException e) {
            System.out.println("This folder does not contain any files");
        }    
    }
}