package ui;

import model.*;
import ui.exceptions.*;

import java.util.Scanner;
import java.util.Comparator;
import java.util.List;

// Represents an application that allows users to add .txt files from their computer to the program and sort and
// browse through them. Structure design based on [TellerApp](https://github.students.cs.ubc.ca/CPSC210/TellerApp)
public class TextFileApp {
    private Scanner scanner;

    FileSystem fileSystem;

    // EFFECTS: starts the Text File application
    public TextFileApp() {
        runTextFileApp();
    }

    // EFFECTS: initializes the app, welcomes the user, processes user input, and thanks the user for using the 
    // application when closed
    private void runTextFileApp() {
        initialize();

        welcome();

        boolean quit = false;
        while (!quit) {
            displayMainMenuOptions();
            String input = getUserInputTrimToLower();

            if (input.equals("q") || input.equals("quit")) {
                quit = true;
            } else {
                handleMainMenuInput(input);
            }
        }

        goodbye();
    }

    // EFFECTS: displays the current directory and the main menu options (the first level of menu options that are
    // available)
    private void displayMainMenuOptions() {
        System.out.println();
        System.out.println("Current directory is: " + fileSystem.getCurrentFolderName());
        System.out.println("Would you like to:");
        System.out.println("  \"a\": Add a file, folder, or label to the current folder");
        System.out.println("  \"e\": Edit a file, folder, or label in the current directory");
        System.out.println("  \"l\": List the files and folders in the current directory");
        System.out.println("  \"n\": Navigate the file system");
        System.out.println("  \"o\": Open a file or folder, open all files with a given label");
        System.out.println("  \"q\": Quit the application");
    }

    // EFFECTS: calls the menu the user selected
    // if their input was invalid, tell them what they inputted was not an option
    private void handleMainMenuInput(String input) {
        if (input.equals("a") || input.equals("add")) {
            addMenu();
        } else if (input.equals("e") || input.equals("edit")) {
            editMenu();
        } else if (input.equals("l") || input.equals("list")) {
            listMenu();
        } else if (input.equals("n") || input.equals("navigate")) {
            navigateMenu();
        } else if (input.equals("o") || input.equals("open")) {
            openMenu();
        } else {
            System.out.println("Your input was not recognized as any of: a, e, l, n, o, or q");
        }
    }

    // MODIFIES: this
    // EFFECTS: initializes the application: instantiates fileSystem and instantiates and sets settings for the scanner
    // in order to get user input correctly
    private void initialize() {
        fileSystem = new FileSystem();

        scanner = new Scanner(System.in);

        // Taken from TellerApp (https://github.students.cs.ubc.ca/CPSC210/TellerApp)
        // enables input of strings with spaces
        scanner.useDelimiter("\r?\n|\r");

    }

    // EFFECTS: sends the user a welcome message
    private void welcome() {
        System.out.println();
        System.out.println("Welcome to the Text File application!");
    }

    // EFFECTS: sends the user a farewell message
    private void goodbye() {
        System.out.println("Thank you for using the Text File application!");
    }


    // Add Menu:

    // EFFECTS: until the user decides to go back to the main menu, shows the add menu options and calls the
    // appropriate submenus for their choice
    private void addMenu() {
        while (true) {
            displayAddMenuOptions();

            String input = getUserInputTrimToLower();

            if (input.equals("b") || input.equals("back")) {
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

    // EFFECTS: calls the submenu the user selected
    // if their input was invalid, tell them what they inputted was not an option
    private void handleAddMenuInput(String input) {
        if (input.equals("fi") || input.equals("file")) {
            addFileMenu();
        } else if (input.equals("fo") || input.equals("folder")) {
            addFolderMenu();
        } else if (input.equals("l") || input.equals("label")) {
            addLabelMenu();
        } else {
            System.out.println("Your input was not recognized as any of: fi, fo, l, or b");
        }
    }

    // EFFECTS: allows the user to add a file from their computer to the program by
    // providing a path and optionally giving it a custom name and labels (if any have been made)
    private void addFileMenu() {
        boolean fileCreated = false;
        while (!fileCreated) {
            System.out.println();
            System.out.println("Please enter the .txt file's path or b to go back");
            System.out.println("It should look like " + FileSystem.EXAMPLE_FILE_PATH + " (writing .txt is optional)");
            String path = getUserInputTrim();
            String inputLowerCase = path.toLowerCase();
            path = (path.endsWith(".txt") ? path : path + ".txt");

            if (inputLowerCase.equals("b") || inputLowerCase.equals("back")) {
                break;
                // Returns to the add menu
            } else if (FileSystem.isFilePathValid(path)) {
                String nameOfFileOnDisk = File.getNameOfFileOnDiskWithoutExtension(path);

                chooseFileNameThenCreateFileAndAddLabels(nameOfFileOnDisk, path);

                fileCreated = true;
            } else {
                System.out.println("The file path you inputted: \"" + path + "\" was incorrect");
            }
        }
    }

    // EFFECTS: lets the user choose a custom file name or use the file's actual name, creates the file in fileSystem,
    // and enables the user to add labels as desired, if any exist
    private void chooseFileNameThenCreateFileAndAddLabels(String actualFileName, String path) {
        // Get the user to choose either actualFileName or a custom file name
        String fileName = chooseFileName(actualFileName);
        
        fileSystem.createFile(fileName, path);
        System.out.println("File with name \"" + fileName + "\" added to current folder ("
                + fileSystem.getCurrentFolderName() + ")");

        if (fileSystem.anyLabelsExist()) {
            addFileLabels(fileName);
        }
    }

    // EFFECTS: enables the user to create a custom name for their newly added file
    // or use the name of the file on their computer
    private String chooseFileName(String actualFileName) {
        if (fileSystem.fileWithNameAlreadyExists(actualFileName)) {
            // Force the user to use a custom name if there already exists a file with the actual file's name in the
            // current folder
            return chooseAndConfirmCustomFileNameNoGoBack();
        } else if (!actualFileName.isEmpty()) {
            while (true) {
                System.out.println();
                System.out.println("Would you like to use the actual file's name: \"" + actualFileName
                        + "\" or create a custom name? Please enter actual or custom");

                String input = getUserInputTrimToLower();

                if (input.equals("actual") || input.equals("a")) {
                    return actualFileName;
                } else if (input.equals("custom") || input.equals("c")) {
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
            String inputLowerCase = input.toLowerCase();

            if (inputLowerCase.equals("b") || inputLowerCase.equals("back")) {
                throw new UserNoLongerWantsCustomNameException();
            } else if (inputLowerCase.equals("namefileb")) {
                try {
                    return nameFileB();
                } catch (UserNoLongerWantsNameBException e) {
                    // Continue the loop
                }
            } else if (input.isEmpty()) {
                System.out.println("Custom file name was not valid");
            } else if (fileSystem.fileWithNameAlreadyExists(input)) {
                String fileAlreadyNamedInputWithCorrectCase = fileSystem.getCapitalizationOfFile(input);
                System.out.println(fileSystem.getCurrentFolderName() + " (current folder) already contains "
                        + "a file named " + fileAlreadyNamedInputWithCorrectCase);
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

            if (input.isEmpty()) {
                System.out.println("Custom file name was not valid");
            } else if (fileSystem.fileWithNameAlreadyExists(input)) {
                String fileAlreadyNamedInputWithCorrectCase = fileSystem.getCapitalizationOfFile(input);
                System.out.println(fileSystem.getCurrentFolderName() + " (current folder) already contains "
                        + "a file named " + fileAlreadyNamedInputWithCorrectCase);
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
            String inputLowerCase = input.toLowerCase();

            if (inputLowerCase.equals("p") || inputLowerCase.equals("prev") || inputLowerCase.equals("previous")) {
                throw new UserNoLongerWantsNameBException();
            } else if (inputLowerCase.equals("b") || inputLowerCase.equals("namefileb")) {
                if (fileSystem.fileWithNameAlreadyExists(input)) {
                    String fileAlreadyNamedInputWithCorrectCase = fileSystem.getCapitalizationOfFile(input);
                    System.out.println(fileSystem.getCurrentFolderName() + " (current folder) already contains "
                            + "a file named " + fileAlreadyNamedInputWithCorrectCase);
                } else {
                    return input;
                }
            } else {
                System.out.println("Your input was not recognized as any of: b, B, namefileb "
                        + "(or namefileb with different capitalization)");
            }
        }
    }

    // EFFECTS: if there is one label, asks the user if they want to label the new file with that label,
    // if there are more asks the user if they would like to add labels and then which labels they would like to add
    private void addFileLabels(String fileName) {
        if (fileSystem.exactlyOneLabelExists()) {
            addTheOnlyCreatedLabel(fileName);
        } else {
            chooseLabels(fileName);
        }
    }

    // REQUIRES: there is only one label in allLabels
    // MODIFIES: fileSystem (specifically file named fileName)
    // EFFECTS: enables the user to either label the file with the only current label or not label it with any
    private void addTheOnlyCreatedLabel(String fileName) {
        String theOnlyCurrentLabelName = fileSystem.getOnlyLabelName();
        while (true) {
            System.out.println();
            System.out.println(
                    "Would you like to label the file \"" + theOnlyCurrentLabelName + "\"? Please enter y or n");

            String input = getUserInputTrimToLower();

            if (input.equals("n") || input.equals("no")) {
                break;
            } else if (input.equals("y") || input.equals("yes")) {
                fileSystem.labelFileWithTheOnlyLabel(fileName);
                System.out.println(fileName + " is now labelled " + theOnlyCurrentLabelName);
                break;
            } else {
                System.out.println("Your input was not recognized as either of: y or n");
            }
        }
    }

    // EFFECTS: asks the user if they would like to add labels.
    // If they would like to, then lets them add as many aspossible or stop when they're done
    private void chooseLabels(String fileName) {
        while (true) {
            System.out.println();
            System.out.println("Would you like to add labels to the file? Please enter y or n");

            String input = getUserInputTrimToLower();

            if (input.equals("n") || input.equals("no")) {
                break;
            } else if (input.equals("y") || input.equals("yes")) {
                addAsManyLabelsAsDesiredOrPossible(fileName);
                break;
            } else {
                System.out.println("Your input was not recognized as either of: y or n");
            }
        }
    }

    // MODIFIES: fileSystem
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
                fileSystem.createFolder(chosenName);
                System.out.println("Created folder named \"" + chosenName + "\" in current folder ("
                        + fileSystem.getCurrentFolderName() + ")");
                break;
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
            String inputLowerCase = input.toLowerCase();

            if (inputLowerCase.equals("b") || inputLowerCase.equals("back")) {
                throw new UserNoLongerWantsToCreateFolderException();
            } else if (inputLowerCase.equals("namefolderb")) {
                try {
                    return nameFolderB();
                } catch (UserNoLongerWantsNameBException e) {
                    // Continue the loop so they can name their folder something else
                }
            } else if (input.isEmpty()) {
                System.out.println("Folder name was not valid");
            } else if (fileSystem.folderWithNameAlreadyExists(input)) {
                String folderAlreadyNamedInputWithCorrectCase = fileSystem.getCapitalizationOfFolder(input);
                System.out.println(fileSystem.getCurrentFolderName() + " (current folder) already contains "
                        + "a folder named " + folderAlreadyNamedInputWithCorrectCase);
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
            String inputLowerCase = input.toLowerCase();

            if (inputLowerCase.equals("p") || inputLowerCase.equals("prev") || inputLowerCase.equals("previous")) {
                throw new UserNoLongerWantsNameBException();
            } else if (inputLowerCase.equals("b") || inputLowerCase.equals("namefolderb")) {
                if (fileSystem.folderWithNameAlreadyExists(input)) {
                    String folderAlreadyNamedInputWithCorrectCase = fileSystem.getCapitalizationOfFolder(input);
                    System.out.println(fileSystem.getCurrentFolderName() + " (current folder) already contains "
                            + "a folder named " + folderAlreadyNamedInputWithCorrectCase);
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
    private void addLabelMenu() {
        while (true) {
            String chosenName;
            try {
                chosenName = chooseLabelName();
            } catch (UserNoLongerWantsToCreateALabelException e) {
                break;
            }

            if (confirmNameCorrect(chosenName)) {
                fileSystem.createLabel(chosenName);
                System.out.println("Created label named \"" + chosenName);
                break;
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
            String inputLowerCase = input.toLowerCase();

            if (inputLowerCase.equals("b") || inputLowerCase.equals("back")) {
                throw new UserNoLongerWantsToCreateALabelException();
            } else if (inputLowerCase.equals("namelabelb")) {
                try {
                    return nameLabelB();
                } catch (UserNoLongerWantsNameBException e) {
                    // Continue the loop so they can name their label something else
                }
            } else if (input.isEmpty()) {
                System.out.println("Label name was not valid");
            } else if (fileSystem.labelExists(input)) {
                String labelAlreadyNamedInputWithCorrectCase = fileSystem.getCapitalizationOfLabel(input);
                System.out.println("A label already exists named  " + labelAlreadyNamedInputWithCorrectCase);
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
            String inputLowerCase = input.toLowerCase();

            if (inputLowerCase.equals("p") || inputLowerCase.equals("prev") || inputLowerCase.equals("previous")) {
                throw new UserNoLongerWantsNameBException();
            } else if (inputLowerCase.equals("b") || inputLowerCase.equals("namelabelb")) {
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

    // EFFECTS: allows the user to edit Files, Folders, and Labels in the fileSystem by choosing one of htese groups
    // and inputting the name of the one thye wish to edit
    private void editMenu() {
        while (true) {
            displayEditMenuOptions();

            String input = getUserInputTrimToLower();

            if (input.equals("b") || input.equals("back")) {
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

    // EFFECTS: handles the edit menu input and calls the appropriate submenus as
    // needed. Tells user if their input wsas invalid
    private void handleEditMenuInput(String input) {
        if (input.equals("fi") || input.equals("file")) {
            editFileMenu();
        } else if (input.equals("fo") || input.equals("folder")) {
            editFolderMenu();
        } else if (input.equals("l") || input.equals("label")) {
            editLabelMenu();
        } else {
            System.out.println("Your input was not recognized as any of: fi, fo, l, or b");
        }
    }

    // EFFECTS: allows the user to choose a file in the current directory and then open it, change its name, change
    // its labels, or delete it. User can list files in directory or go back
    private void editFileMenu() {
        while (true) {
            System.out.println();
            System.out.println("Please enter the name of the file you would like to edit, l to list the files in "
                    + "this folder (" + fileSystem.getCurrentFolderName() + "), or b to go back");

            String input = getUserInputTrim();
            String inputLowerCase = input.toLowerCase();

            if (inputLowerCase.equals("b") || inputLowerCase.equals("back")) {
                break;
                // Returns to the edit menu
            } else if (inputLowerCase.equals("l") || inputLowerCase.equals("list")) {
                try {
                    listStringsAlphabetically(fileSystem.getNamesOfAllSubfiles());
                } catch (ListEmptyException e) {
                    System.out.println("There are no files in this folder");
                }
            } else if (fileSystem.fileWithNameAlreadyExists(input)) {
                editFile(input);
                break;
            } else {
                System.out.println("There is no file named \"" + input + "\" in this folder");
            }
        }
    }

    // EFFECTS: enables the user to open file, change its name, changes its labels,
    // and delete it TODO
    private void editFile(String fileName) {
        while (true) {
            displayEditFileMenuOptions(fileName);

            String input = getUserInputTrimToLower();

            if (input.equals("b") || input.equals("back")) {
                break;
            } else {
                try {
                    handleEditFileMenuInput(input, fileName);
                } catch (CurrentObjectDeletedException e) {
                    break;
                }
            }
        }
    }

    // EFFECTS: displays the (first level of) edit file menu options TODO
    private void displayEditFileMenuOptions(String fileName) {
        System.out.println();
        System.out.println(fileName + " is selected. Would you like to:");
        System.out.println("  \"o\": Open the file in your default text editor");
        System.out.println("  \"e\": Edit the file's name or add/remove labels");
        System.out.println("  \"d\": Delete the file");
        System.out.println("  \"b\": Back to the main menu");
    }

    // MODIFIES: this
    // EFFECTS: handles the edit file menu input and calls the appropriate functions
    // as needed TODO
    // throws CurrentObjectDeletedException if file was deleted
    private void handleEditFileMenuInput(String input, String fileName) throws CurrentObjectDeletedException {
        if (input.equals("o") || input.equals("open")) {
            try {
                fileSystem.openFile(fileName);
            } catch (FilePathNoLongerValidException e) {
                System.out.println("File at " + fileSystem.getFilePath(fileName) + " no longer exists");
            }
        } else if (input.equals("e") || input.equals("edit")) {
            editFileNameAndLabelsMenu(fileName);
        } else if (input.equals("d") || input.equals("delete")) {
            deleteFile(fileName);
            throw new CurrentObjectDeletedException();
        } else {
            System.out.println("Your input was not recognized as any of: o, e, d, or b");
        }
    }

    // MODIFIES: this, file
    // EFFECTS: allows the user to change file's name and add/remove labels from it TODO
    private void editFileNameAndLabelsMenu(String fileName) {
        while (true) {
            displayEditFileNameAndLabelsOptions(fileName);

            String input = getUserInputTrimToLower();

            if (input.equals("b") || input.equals("back")) {
                break;
            } else {
                handleEditFileNameAndLabelsMenuInput(input, fileName);
            }
        }
    }

    // EFFECTS: displays the option to change the file's name, add a label, remove a
    // label, and remove all labels TODO
    private void displayEditFileNameAndLabelsOptions(String fileName) {
        System.out.println();
        System.out.println(fileName + " is selected. Would you like to:");
        System.out.println("  \"n\": Change the file's name");
        System.out.println("  \"a\": Add a label");
        System.out.println("  \"r\": Remove a label");
        System.out.println("  \"ra\": Remove all labels");
        System.out.println("  \"b\": Back to the edit file menu");
    }

    // MODIFIES: this, file
    // EFFECTS: calls the methods enabling the user to change file's name and
    // add/remove labels from it TODO
    private void handleEditFileNameAndLabelsMenuInput(String input, String fileName) {
        if (input.equals("n") || input.equals("name")) {
            try {
                fileSystem.setFileName(fileName, chooseAndConfirmCustomFileName());
            } catch (UserNoLongerWantsCustomNameException e) {
                System.out.println("Name has not been changed and will remain " + fileName);
            }
        } else if (input.equals("a") || input.equals("add")) {
            addAsManyLabelsAsDesiredOrPossible(fileName);
        } else if (input.equals("r") || input.equals("remove")) {
            removeAsManyLabelsAsDesiredOrPossible(fileName);
        } else if (input.equals("ra") || input.equals("remove all")) {
            confirmRemoveAllLables(fileName);
        } else {
            System.out.println("Your input was not recognized as any of: n, a, r, ra, or b");
        }
    }

    // MODIFIES: file, allLabels
    // EFFECTS: removes all labels from file TODO
    private void confirmRemoveAllLables(String fileName) {
        if (confirmCompleteAction("remove all labels from " + fileName)) {
            fileSystem.removeAllLabels(fileName);
            System.out.println("All labels removed");
        }
    }

    // MODIFIES: this, file
    // EFFECTS: confirms that the user wants to delete file and then deletes this
    // folder's reference to it as well
    // as label's reference to it TODO
    private void deleteFile(String fileName) {
        if (confirmCompleteAction("delete " + fileName)) {
            fileSystem.deleteFile(fileName);
        }
    }

    // MODIFIES: this
    // EFFECTS: allows the user to edit a folder: to open it, change its name, or
    // delete it TODO
    private void editFolderMenu() {
        while (true) {
            System.out.println();
            System.out.println(
                    "Please enter the name of the subfolder you would like to edit, l to list the folders in "
                            + "this folder (" + fileSystem.getCurrentFolderName() + "), or b to go back");

            String input = getUserInputTrim();
            String inputLowerCase = input.toLowerCase();

            if (inputLowerCase.equals("b") || inputLowerCase.equals("back")) {
                break;
                // Returns to the edit menu
            } else if (inputLowerCase.equals("l") || inputLowerCase.equals("list")) {
                try {
                    listStringsAlphabetically(fileSystem.getNamesOfAllSubfolders());
                } catch (ListEmptyException e) {
                    System.out.println("This folder doesn't have any subfolders");
                }
            } else if (fileSystem.folderWithNameAlreadyExists(input)) {
                editFolder(input);
                break;
            } else {
                System.out.println("There is no file named \"" + input + "\" in this folder");
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: enables the user to open folder, change its name, and delete it TODO
    private void editFolder(String folderName) {
        while (true) {
            displayEditFolderMenuOptions(folderName);

            String input = getUserInputTrimToLower();

            if (input.equals("b") || input.equals("back")) {
                break;
            } else {
                try {
                    handleEditFolderMenuInput(input, folderName);
                } catch (NewFolderOpenedException | CurrentObjectDeletedException e) {
                    break;
                }
            }
        }
    }

    // EFFECTS: displays the edit folder menu options TODO
    private void displayEditFolderMenuOptions(String folderName) {
        System.out.println();
        System.out.println(folderName + " is selected. Would you like to:");
        System.out.println("  \"o\": Open the folder");
        System.out.println("  \"e\": Edit the folders's name");
        System.out.println("  \"d\": Delete the folder");
        System.out.println("  \"b\": Back to the main menu");
    }

    // MODIFIES: this
    // EFFECTS: handles the edit folder menu input and calls the appropriate
    // functions as needed TODO
    private void handleEditFolderMenuInput(String input, String folderName) throws NewFolderOpenedException,
            CurrentObjectDeletedException {
        if (input.equals("o") || input.equals("open")) {
            fileSystem.openFolder(folderName);
            System.out.println(folderName + " opened");
            throw new NewFolderOpenedException();
        } else if (input.equals("e") || input.equals("edit")) {
            editFolderNameMenu(folderName);
        } else if (input.equals("d") || input.equals("delete")) {
            deleteFolderMenu(folderName);
        } else {
            System.out.println("Your input was not recognized as any of: o, e, d, or b");
        }
    }

    // MODIFIES: this, folder
    // EFFECTS: allows the user to change folder's name TODO
    private void editFolderNameMenu(String folderName) {
        while (true) {
            System.out.println();
            System.out.println("Please enter the new folder name or b to go back (if you wish "
                    + "to rename your folder to b or B, enter namefolderb)");

            String input = getUserInputTrim();
            String inputLowerCase = input.toLowerCase();

            if (inputLowerCase.equals("b") || inputLowerCase.equals("back")) {
                break;
            } else if (inputLowerCase.equals("namefolderb")) {
                try {
                    fileSystem.setFolderName(folderName, nameFolderB());
                    break;
                } catch (UserNoLongerWantsNameBException e) {
                    // Continue the loop so they can rename their folder something else
                }
            } else if (input.isEmpty()) {
                System.out.println("Folder name was not valid");
            } else if (fileSystem.folderWithNameAlreadyExists(input)) {
                tellUserCurrentFolderContainsFileNamed(input);
            } else {
                fileSystem.setFolderName(folderName, input);
                break;
            }
        }
    }

    // EFFECTS: tells the user that the name that they entered is in use TODO
    private void tellUserCurrentFolderContainsFileNamed(String input) {
        System.out.println(fileSystem.getCurrentFolderName() + " (current folder) already contains a folder named "
                + fileSystem.getCapitalizationOfFolder(input));
    }

    // MODIFIES: this, file
    // EFFECTS: confirms that the user wants to delete file and then deletes this
    // folder's reference to it as well
    // as label's reference to it TODO
    private void deleteFolderMenu(String folderName) throws CurrentObjectDeletedException {
        if (confirmCompleteAction("delete " + folderName)) {
            fileSystem.deleteFolder(folderName);
            System.out.println(folderName + " deleted");
            throw new CurrentObjectDeletedException();
        }
    }

    // MODIFIES: this
    // EFFECTS: allows the user to edit a label: they can open it, change its name,
    // or delete it TODO
    private void editLabelMenu() {
        while (true) {
            System.out.println();
            System.out.println("Please enter the name of the label you would like to edit, "
                    + "l to list all created labels or b to go back");

            String input = getUserInputTrim();
            String inputLowerCase = input.toLowerCase();

            if (inputLowerCase.equals("b") || inputLowerCase.equals("back")) {
                break;
                // Returns to the edit menu
            } else if (inputLowerCase.equals("l") || inputLowerCase.equals("list")) {
                try {
                    listStringsAlphabetically(fileSystem.getNamesOfAllLabels());
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
    // EFFECTS: enables the user to open label, change its name, and delete it TODO
    private void editLabel(String labelName) {
        while (true) {
            displayEditLabelMenuOptions(labelName);

            String input = getUserInputTrimToLower();

            if (input.equals("b") || input.equals("back")) {
                break;
            } else {
                try {
                    handleEditLabelMenuInput(input, labelName);
                } catch (NewFolderOpenedException | CurrentObjectDeletedException e) {
                    break;
                }
            }
        }
    }

    // EFFECTS: displays the edit label menu options TODO
    private void displayEditLabelMenuOptions(String labelName) {
        System.out.println();
        System.out.println(labelName + " is selected. Would you like to:");
        System.out.println("  \"o\": Open a directory with all files labelled with this label");
        System.out.println("  \"e\": Edit the label's name");
        System.out.println("  \"d\": Delete the label");
        System.out.println("  \"b\": Back to the main menu");
    }

    // MODIFIES: this
    // EFFECTS: handles the edit label menu input and calls the appropriate
    // functions as needed TODO
    private void handleEditLabelMenuInput(String input, String labelName) throws NewFolderOpenedException,
            CurrentObjectDeletedException {
        if (input.equals("o") || input.equals("open")) {
            fileSystem.openLabel(labelName);
            System.out.println("Current directory is every file labelled " + labelName);
            throw new NewFolderOpenedException();
        } else if (input.equals("e") || input.equals("edit")) {
            editLabelNameMenu(labelName);
        } else if (input.equals("d") || input.equals("delete")) {
            deleteLabelMenu(labelName);
        } else {
            System.out.println("Your input was not recognized as any of: o, e, d, or b");
        }
    }

    // MODIFIES: this, label
    // EFFECTS: allows the user to change label's name TODO
    private void editLabelNameMenu(String labelName) {
        while (true) {
            System.out.println();
            System.out.println("Please enter the new label name or b to go back (if you wish "
                    + "to rename your label to b or B, enter namelabelb)");

            String input = getUserInputTrim();
            String inputLowerCase = input.toLowerCase();

            if (inputLowerCase.equals("b") || inputLowerCase.equals("back")) {
                break;
            } else if (inputLowerCase.equals("namelabelb")) {
                try {
                    fileSystem.setLabelName(labelName, nameLabelB());
                    break;
                } catch (UserNoLongerWantsNameBException e) {
                    // Continue the loop so they can rename the label something else
                }
            } else if (input.isEmpty()) {
                System.out.println("Label name was not valid");
            } else if (fileSystem.labelExists(input)) {
                tellUserLabelAlreadyExistsWithName(input);
            } else {
                fileSystem.setLabelName(labelName, input);
                break;
            }
        }
    }

    // EFFECTS: thells the user that there is already a label named input TODO
    private void tellUserLabelAlreadyExistsWithName(String input) {
        System.out.println("A label already exists with name " + fileSystem.getCapitalizationOfLabel(input));
    }

    // MODIFIES: this, label
    // EFFECTS: confirms that the user wants to delete label and then deletes this
    // folder's reference to it
    // and removes it from any files TODO
    private void deleteLabelMenu(String labelName) throws CurrentObjectDeletedException {
        if (confirmCompleteAction("delete " + labelName)) {
            fileSystem.deleteLabel(labelName);
            throw new CurrentObjectDeletedException();
        }
    }


    // List Menu:

    // MODIFIES: this
    // EFFECTS: handles the list menu input TODO
    private void listMenu() {
        System.out.print("Files: ");
        listFilesAlphabeticallyTellUserIfNone();
        System.out.print("Folders: ");
        listFoldersAlphabeticallyTellUserIfNone();
    }


    // Navigate Menu:

    // MODIFIES: this
    // EFFECTS: handles the navigate menu input TODO
    private void navigateMenu() {
        while (true) {
            displayNavigateMenuOptions();

            String input = getUserInputTrimToLower();

            if (input.equals("b") || input.equals("back")) {
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

    // EFFECTS: displays the navigate menu options TODO
    private void displayNavigateMenuOptions() {
        System.out.println();
        System.out.println("You are in folder " + fileSystem.getCurrentFolderName() + ". Would you like to:");
        System.out.println("  \"r\": Jump to the root folder (base folder)");
        if (fileSystem.currentFolderHasParent()) {
            System.out.println("  \"u\": Go up one level of folders to " + fileSystem.getParentFolderName());
        }
        System.out.println("  \"o\": Open a folder in this folder (" + fileSystem.getCurrentFolderName() + ")");
        System.out.println("  \"b\": Back to the main menu");
    }

    // MODIFIES: this
    // EFFECTS: handles the navigate menu input and implements the menu TODO
    private void handleNavigateMenuInput(String input) throws NewFolderOpenedException {
        if (fileSystem.currentFolderHasParent()) {
            handleNavigateMenuInputHasParent(input);
        } else {
            handleNavigateMenuInputNoParent(input);
        }
    }

    // MODIFIES: this
    // EFFECTS: handles the navigate menu input and implements the menu TODO
    private void handleNavigateMenuInputHasParent(String input) throws NewFolderOpenedException {
        if (input.equals("r") || input.equals("root")) {
            fileSystem.openRootFolder();
            System.out.println("root opened");
            throw new NewFolderOpenedException();
        } else if (fileSystem.currentFolderHasParent() && (input.equals("u") || input.equals("up"))) {
            fileSystem.goUpOneDirectoryLevel();
            System.out.println(fileSystem.getCurrentFolderName() + " opened");
            throw new NewFolderOpenedException();
        } else if (input.equals("o") || input.equals("open")) {
            getInputToOpenFolder();
        } else {
            System.out.println("Your input was not recognized as any of: r, u, o, or b");
        }
    }

    // MODIFIES: this
    // EFFECTS: handles the navigate menu input and implements the menu TODO
    private void handleNavigateMenuInputNoParent(String input) throws NewFolderOpenedException {
        if (input.equals("r") || input.equals("root")) {
            fileSystem.openRootFolder();
            System.out.println("root opened");
            throw new NewFolderOpenedException();
        } else if (input.equals("o") || input.equals("open")) {
            getInputToOpenFolder();
        } else {
            System.out.println("Your input was not recognized as any of: r, o, or b");
        }
    }

    // Open Menu:

    // MODIFIES: this
    // EFFECTS: handles the open menu input TODO
    private void openMenu() {
        while (true) {
            displayOpenMenuOptions();

            String input = getUserInputTrimToLower();

            if (input.equals("b") || input.equals("back")) {
                break;
            } else {
                try {
                    handleOpenMenuInput(input);
                } catch (NewFolderOpenedException e) {
                    break;
                }
            }
        }
    }

    // EFFECTS: displays the open menu options TODO
    private void displayOpenMenuOptions() {
        System.out.println();
        System.out.println("You are in folder " + fileSystem.getCurrentFolderName() + ". Would you like to:");
        System.out.println("  \"fi\": Open a file in the current folder");
        System.out.println("  \"fo\": Open a folder in the current folder");
        System.out.println("  \"lf\": Open a directory containing all files labelled with a certain label");
        // System.out.println("  \"r\": Open recently-opened files");
        System.out.println("  \"b\": Back to the main menu");
    }

    // MODIFIES: this
    // EFFECTS: handles the open menu input and implements the menu TODO
    private void handleOpenMenuInput(String input) throws NewFolderOpenedException {
        if (input.equals("fi") || input.equals("file")) {
            getInputToOpenFile();
        } else if (input.equals("fo") || input.equals("folder")) {
            getInputToOpenFolder();
            throw new NewFolderOpenedException();
        } else if (input.equals("lf") || input.equals("labelled files")) {
            getInputToOpenLabel();
        } else if (input.equals("r") || input.equals("recent")) {
            // TODO: Implement opening recently-opened files
            System.out.println("Your input was not recognized as any of: fi, fo, lf, or b");
        } else {
            System.out.println("Your input was not recognized as any of: fi, fo, lf, or b");
        }
    }

    /* 
     *   Methods common to multiple menus:
     */

    // EFFECTS: lets the user search for a file in the current directory and opens it if one found, list all
    // subfiles of the currently-opened folder, and go back
    private void getInputToOpenFile() {
        while (true) {
            System.out.println();
            System.out.println("Please enter the name of the file to open, l to list the options, or b to go back");

            String input = getUserInputTrim();

            if (input.equalsIgnoreCase("b") || input.equalsIgnoreCase("back")) {
                break;
            } else if (input.equalsIgnoreCase("l") || input.equalsIgnoreCase("list")) {
                listFilesAlphabeticallyTellUserIfNone();
            } else if (input.isEmpty()) {
                System.out.println("Files name was not valid");
            } else if (fileSystem.fileWithNameAlreadyExists(input)) {
                try {
                    fileSystem.openFile(input);
                    break;
                } catch (FilePathNoLongerValidException e) {
                    System.out.println("File was moved or deleted");
                }
            } else {
                System.out.println("This folder (" + fileSystem.getCurrentFolderName()
                        + ") does not contain a file named " + input);
            }
        }
    }

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
                    listStringsAlphabetically(fileSystem.getNamesOfAllSubfolders());
                } catch (ListEmptyException e) {
                    System.out.println(fileSystem.getCurrentFolderName()
                                + " (current folder) does not contain any subfolders");
                }
            } else if (input.isEmpty()) {
                System.out.println("Folder name was not valid");
            } else if (fileSystem.folderWithNameAlreadyExists(input)) {
                fileSystem.openFolder(input);
                System.out.println(fileSystem.getCurrentFolderName() + " opened");
                throw new NewFolderOpenedException();
            } else {
                tellUserThisFolderDoesNotContainFolderNamed(input);
            }
        }
    }

    // EFFECTS: tells the user the current Folder does not contain a Folder named input
    private void tellUserThisFolderDoesNotContainFolderNamed(String input) {
        System.out.println("This folder (" + fileSystem.getCurrentFolderName()
                + ") does not contain a subfolder named " + input);
    }

    // MODIFIES: fileSystem
    // EFFECTS: enables the user to open a read-only folder containing all files labelled with the label of their
    // choice, to list all labels they have created, or to go back
    private void getInputToOpenLabel() throws NewFolderOpenedException {
        while (true) {
            System.out.println();
            System.out.println("Please enter the name of the label to open, l to list the options, or b to go back");

            String input = getUserInputTrim();
            String inputLowerCase = input.toLowerCase();

            if (inputLowerCase.equals("b") || inputLowerCase.equals("back")) {
                break;
            } else if (inputLowerCase.equals("l") || inputLowerCase.equals("list")) {
                try {
                    listStringsAlphabetically(fileSystem.getNamesOfAllLabels());
                } catch (ListEmptyException e) {
                    System.out.println("You have not made any labels");
                }
            } else if (input.isEmpty()) {
                System.out.println("Label name was not valid");
            } else if (fileSystem.labelExists(input)) {
                fileSystem.openLabel(input);
                System.out.println(fileSystem.getCurrentFolderName() + " opened");
                throw new NewFolderOpenedException();
            } else {
                System.out.println("There is no label named \"" + input + "\"");
            }
        }
    }

    // Confirmations:
    // EFFECTS: returns true if the user confirms that they want to complete action,
    // returns false if they confirm they do not want to
    /**
     * Prints out: "Are you sure you want to $action? Please enter yes or no"
     */
    private boolean confirmCompleteAction(String action) {
        while (true) {
            System.out.println();
            System.out.println("Are you sure you want to " + action + "? Please enter yes or no");

            String input = getUserInputTrimToLower();

            if (input.equals("yes")) {
                return true;
            } else if (input.equals("no")) {
                return false;
            } else {
                System.out.println("Your input was not recognized as either of: yes or no");
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

            String input = getUserInputTrimToLower();

            if (input.equals("y") || input.equals("yes")) {
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                return false;
            } else {
                System.out.println("Your input was not recognized as either of: y or n");
            }
        }
    }

    // Labels:

    // EFFECTS: loops until the user has put every label on File named fileName or no longer wishes to add labels.
    // Enables the user to add any labels they have created to the File named fileName
    private void addAsManyLabelsAsDesiredOrPossible(String fileName) {
        if (fileSystem.getNumLabelsNotOnFile(fileName) > 1) {
            try {
                chooseLabelsToAdd(fileName);
                addLastRemainingLabel(fileName);
            } catch (UserNoLongWantsToChangeLabelsException e) {
                return;
            }
        } else if (fileSystem.getNumLabelsNotOnFile(fileName) == 1) {
            try {
                addLastRemainingLabel(fileName);
            } catch (UserNoLongWantsToChangeLabelsException e) {
                return;
            }
        } else {
            System.out.println(fileName + " already has every label");
        }
    }

    // EFFECTS: lets the user label File named fileName with as many labels as they'd like to
    // while there is more than one option
    private void chooseLabelsToAdd(String fileName)
            throws UserNoLongWantsToChangeLabelsException {
        while (fileSystem.getNumLabelsNotOnFile(fileName) > 1) {
            System.out.println();
            System.out.println("Enter the name of the label you would like to add to the file, "
                    + "enter l to list the available labels, or enter b to stop adding labels");

            String input = getUserInputTrimToLower();

            if (input.equals("b") || input.equals("back")) {
                throw new UserNoLongWantsToChangeLabelsException();
            } else if (input.equals("l") || input.equals("list")) {
                try {
                    listStringsAlphabetically(fileSystem.getNamesOfAllLabelsNotOnFile(fileName));
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
    private void addLabelToFile(String fileName, String labelName) {
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
    private void addLastRemainingLabel(String fileName) throws UserNoLongWantsToChangeLabelsException {
        while (true) {
            System.out.println();
            System.out.println("Would you like to label the file \""
                    + fileSystem.getNamesOfAllLabelsNotOnFile(fileName).get(0)
                    + "\"? (This is the only label not on this file) y or n");

            String input = getUserInputTrimToLower();

            if (input.equals("n") || input.equals("no")) {
                throw new UserNoLongWantsToChangeLabelsException();
            } else if (input.equals("y") || input.equals("yes")) {
                addLabelToFile(fileName, fileSystem.getNamesOfAllLabelsNotOnFile(fileName).get(0));
                break;
            } else {
                System.out.println("Your input was not recognized as a y or n");
            }
        }
    }

    // MODIFIES: fileSystem (specifically File named fileName and Label named labelName)
    // EFFECTS: if there is more than 1 label not on the file, let the user pick which label to remove and
    // loop until they no longer want to remove labels or they have removed all but the last label
    // If there is only one label not on the file, asks the user if they would like to remove it
    // Otherwise tells the user that there are no labels on the file
    private void removeAsManyLabelsAsDesiredOrPossible(String fileName) {
        if (fileSystem.getNumLabelsOnFile(fileName) > 1) {
            try {
                chooseLabelsToRemove(fileName);
                removeOnlyLabel(fileName);
            } catch (UserNoLongWantsToChangeLabelsException e) {
                return;
            }
        } else if (fileSystem.getNumLabelsOnFile(fileName) == 1) {
            try {
                removeOnlyLabel(fileName);
            } catch (UserNoLongWantsToChangeLabelsException e) {
                return;
            }
        } else {
            System.out.println(fileName + " already is not labelled with any labels");
        }
    }

    // EFFECTS: while there are 2 or more labels on File named fileName, lets the user remove as many as they'd like
    // to, list the labels on that File, or go back
    private void chooseLabelsToRemove(String fileName) throws UserNoLongWantsToChangeLabelsException {
        while (fileSystem.getNumLabelsOnFile(fileName) > 1) {
            System.out.println();
            System.out.println("Enter the name of the label you would like to remove to the file, "
                    + "enter l to list the labels the file is labelled with, or enter b to stop removing labels");

            String input = getUserInputTrimToLower();

            if (input.equals("b") || input.equals("back")) {
                throw new UserNoLongWantsToChangeLabelsException();
            } else if (input.equals("l") || input.equals("list")) {
                try {
                    listStringsAlphabetically(fileSystem.getNamesOfAllLabelsOnFile(fileName));
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
    private void removeLabelFromFile(String fileName, String labelName) {
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
    private void removeOnlyLabel(String fileName) throws UserNoLongWantsToChangeLabelsException {
        String labelName = fileSystem.getNamesOfAllLabelsOnFile(fileName).get(0);
        while (true) {
            System.out.println();
            System.out.println("Would you like to remove the label " + labelName + " from the file \""
                    + fileName + "\"? (This is the only label on this file) y or n");

            String input = getUserInputTrimToLower();

            if (input.equals("n") || input.equals("no")) {
                throw new UserNoLongWantsToChangeLabelsException();
            } else if (input.equals("y") || input.equals("yes")) {
                fileSystem.unlabelFile(fileName, labelName);
                System.out.println(fileName + " is no longer labelled " + labelName);
                break;
            } else {
                System.out.println("Your input was not recognized as a y or n");
            }
        }
    }

    // User Input:
    // EFFECTS: gets input from the user and trims it
    private String getUserInputTrim() {
        String input = "";
        input += scanner.next();
        input = input.trim();
        return input;
    }

    // EFFECTS: gets input from the user and trims it and turns it to lowercase
    private String getUserInputTrimToLower() {
        String input = "";
        input += scanner.next();
        input = input.trim();
        input = input.toLowerCase();
        return input;
    }

    /* 
     *  Listing alphabetically:
     */

    // EFFECTS: alphabetizes strings, ignoring case, and then prints it out separated by commas
    // (with no comma after the last element)
    private void listStringsAlphabetically(List<String> strings) throws ListEmptyException {
        if (strings.isEmpty()) {
            throw new ListEmptyException();
        }

        // Code taken from [Stack Overflow]
        // https://stackoverflow.com/questions/8432581/how-to-sort-a-listobject-alphabetically-using-object-name-field
        strings.sort(Comparator.comparing(String::toLowerCase));

        int indexOfLastLabel = strings.size() - 1;
        String lastLabelName = strings.get(indexOfLastLabel);
        strings.remove(indexOfLastLabel);

        for (String labelName : strings) {
            System.out.print(labelName + ", ");
        }
        System.out.println(lastLabelName);
    }

    // EFFECTS: lists out all of the files in this folder or a message if there are none
    private void listFilesAlphabeticallyTellUserIfNone() {
        try {
            listStringsAlphabetically(fileSystem.getNamesOfAllSubfiles());
        } catch (ListEmptyException e) {
            System.out.println("This folder does not contain any files");
        }    
    }

    // EFFECTS: lists out all of the folders in this folder or a message if there are none
    private void listFoldersAlphabeticallyTellUserIfNone() {
        try {
            listStringsAlphabetically(fileSystem.getNamesOfAllSubfolders());
        } catch (ListEmptyException e) {
            System.out.println("This folder does not contain any subfolders");
        }  
    }
}