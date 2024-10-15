package ui;

import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.io.IOException;
import java.awt.Desktop;
import model.*;
import ui.exceptions.*;

// Represents the application that allows users to add .txt files from their computer to the program and sort and
// browse through them. Structure design based on "TellerApp"
public class TextFileApp {
    private Scanner scanner;

    private Folder rootFolder;
    private Folder currentFolder;

    private Set<Label> allLabels;

    // EFFECTS: starts the Text File application
    public TextFileApp() {
        runTextFileApp();
    }

    // MODIFIES: this
    // EFFECTS: processes user input
    private void runTextFileApp() {
        initialize();

        welcome();

        boolean quit = false;
        while (!quit) {
            displayMainMenuOptions();
            String input = scanner.next();
            input = input.trim();
            input = input.toLowerCase();

            if (input.equals("q") || input.equals("quit")) {
                quit = true;
            } else {
                handleMainMenuInput(input);
            }
        }

        goodbye();
    }

    // EFFECTS: displays the first level of menu options that are available
    private void displayMainMenuOptions() {
        System.out.println();
        System.out.println("Would you like to:");
        System.out.println("  \"a\": Add a file, folder, or label to the current folder");
        System.out.println("  \"e\": Edit a file, folder, or label in the current directory");
        System.out.println("  \"l\": List the files and folders, just files, or just folders in the current directory");
        System.out.println("  \"n\": Navigate the file system");
        System.out.println("  \"o\": Open a file or folder, open all files with a given label");
        System.out.println("  \"q\": Quit the application");
    }

    // MODIFIES: this
    // EFFECTS: handles the main menu input and calls the appropriate submenus as needed
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
    // EFFECTS: initializes the application: instantiates rootFolders
    private void initialize() {
        scanner = new Scanner(System.in);
        // From TellerApp — enables input of strings with spaces
        scanner.useDelimiter("\r?\n|\r"); 

        rootFolder = new Folder("root");
        currentFolder = rootFolder;

        allLabels = new HashSet<Label>();
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

    // MODIFIES: this
    // EFFECTS: handles the add menu input
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

    // MODIFIES: this
    // EFFECTS: handles the add menu input and calls the appropriate submenus as needed
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

    // MODIFIES: this
    // EFFECTS: allows the user to add a file from their computer to the program by providing a path
    // and optionally giving it a custom name and labels
    private void addFileMenu() {
        boolean fileCreated = false;
        while (!fileCreated) {
            System.out.println();
            System.out.println("Please enter the .txt file's path (it should look like " +
             "C:\\Users\\User\\Documents\\Note Name.txt (writing .txt is optional)) or b to go back");
            String path = getUserInputTrim();
            path = addDotTXTIfMissing(path);
            String pathLowerCase = path.toLowerCase();

            if (pathLowerCase.equals("b") || pathLowerCase.equals("back")) {
                break;
                // Returns to the add menu
            } else if (isFilePathValid(path)) {                
                String name = chooseFileName(path);
                model.File newFile = currentFolder.addFile(name, path);
                System.out.println("File with name \"" + newFile.getName() + "\" added to this folder (" + currentFolder.getName() + ")");

                if (!allLabels.isEmpty()) {
                    addFileLabels(newFile);
                }

                fileCreated = true;
            } else {
                System.out.println("The file path you inputted: \"" + path + "\" was incorrect");
            }
        }
    }

    // EFFECTS: enables the user to create a custom name for their newly added file
    //          or use the name of the file on their computer
    private String chooseFileName(String filePath) {
        String fileName = getCharactersAfterLastBackslash(filePath);
        fileName = removeDotTXT(fileName);

        if (currentFolderContainsFileNamed(fileName)) {
            return chooseAndConfirmCustomFileNameNoGoBack();
        } else if (!fileName.isEmpty()) {
            while (true) {
                System.out.println();
                System.out.println("Would you like to use the actual file's name: \"" + fileName +
                "\" or create a custom name? Please enter actual or custom");

                String input = getUserInputTrimToLower();

                if (input.equals("actual") || input.equals("a")) {
                    return fileName;
                } else if (input.equals("custom") || input.equals("c")) {
                    try {
                        return chooseAndConfirmCustomFileName();
                    } catch (UserNoLongerWantsCustomNameException e) {
                        // Continue the loop (where they will presumably enter actual, but this lets them confirm)
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
            System.out.println("Please enter the custom name or b to go back (if you wish " +
            "to name your file b or B, enter namefileb)");

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
            } else if (currentFolderContainsFileNamed(input)) {
                String fileAlreadyNamedInputWithCorrectCase = currentFolder.getFile(input).getName();
                System.out.println(currentFolder.getName() + " (current folder) already contains a file named "
                + fileAlreadyNamedInputWithCorrectCase);
            } else {
                return input;
            }
        }
    }

    // EFFECTS: enables the user to choose the custom name for their file and then confirms it is correct.
    private String chooseAndConfirmCustomFileNameNoGoBack() {
        while (true) {
            String chosenName = chooseCustomFileNameNoGoBack();

            if (confirmNameCorrect(chosenName)) {
                return chosenName;
            }
        }
    }

    // EFFECTS: enables the user to choose the custom name for their file and throws
    // UserNoLongerWantsCustomNameException if they no longer wish for a custom name
    private String chooseCustomFileNameNoGoBack() {
        while (true) {
            System.out.println();
            System.out.println("Please enter the custom name");

            String input = getUserInputTrim();

            if (input.isEmpty()) {
                System.out.println("Custom file name was not valid");
            } else if (currentFolderContainsFileNamed(input)) {
                String fileAlreadyNamedInputWithCorrectCase = currentFolder.getFile(input).getName();
                System.out.println(currentFolder.getName() + " (current folder) already contains a file named "
                + fileAlreadyNamedInputWithCorrectCase);
            } else {
                return input;
            }
        }
    }

    // EFFECTS: lets the user name their file b or B or namefileb (or any case variants, i.e. NameFileB)
    private String nameFileB() throws UserNoLongerWantsNameBException {
        while (true) {
            System.out.println();
            System.out.println("Please enter the custom name (b, B, namefileb, namefileB, etc.) " +
            "or prev to go to the previous menu");

            String input = getUserInputTrim();
            String inputLowerCase = input.toLowerCase();

            if (inputLowerCase.equals("p") || inputLowerCase.equals("prev") || inputLowerCase.equals("previous")) {
                throw new UserNoLongerWantsNameBException();
            } else if (inputLowerCase.equals("b") || inputLowerCase.equals("namefileb")) {
                if (currentFolderContainsFileNamed(input)) {
                    String fileAlreadyNamedInputWithCorrectCase = currentFolder.getFile(input).getName();
                    System.out.println(currentFolder.getName() + " (current folder) already contains a file named "
                    + fileAlreadyNamedInputWithCorrectCase);
                } else {
                    return input;
                }
            }
            else {
                System.out.println("Your input was not recognized as any of: b, B, namefileb " +
                "(or namefileb with different capitalization)");
            }
        }
    }

    // MODIFES: file
    // EFFECTS: if there is one label, asks the user if they want to label the new file with that label,
    //  if there are more asks the user if they would like to add labels and then which labels they would like to add
    private void addFileLabels(model.File file) {
        if (allLabels.size() == 1) {
            addTheOnlyCreatedLabel(file);
        }
        else {
            chooseLabels(file);
        }
    }

    // MODIFIES: file
    // EFFECTS: asks the user if they would like to add labels and then lets them do so if they do
    private void chooseLabels(model.File file) {
        while(true) {
            System.out.println();
            System.out.println("Would you like to add labels to the file? Please enter y or n");

            String input = getUserInputTrimToLower();

            if (input.equals("n") || input.equals("no")) {
                break;
            } else if (input.equals("y") || input.equals("yes")) {
                addAsManyLabelsAsDesiredOrPossible(file);
                break;
            } else {
                System.out.println("Your input was not recognized as either of: y or n");
            }
        }
    }

    // REQUIRES: there is only one label in allLabels
    // MODIFIES: file
    // EFFECTS: enables the user to either label the file with the only current label or not label it
    private void addTheOnlyCreatedLabel(model.File file) {
        Label theOnlyCurrentLabel = firstLabelFoundInSet(allLabels);
        while(true) {
            System.out.println();
            System.out.println("Would you like to label the file \"" + theOnlyCurrentLabel.getName() + "\"? Please enter y or n");

            String input = getUserInputTrimToLower();

            if (input.equals("n") || input.equals("no")) {
                break;
            } else if (input.equals("y") || input.equals("yes")) {
                file.addLabel(theOnlyCurrentLabel);
                System.out.println(file.getName() + " is now labelled " + theOnlyCurrentLabel.getName());
                break;
            } else {
                System.out.println("Your input was not recognized as either of: y or n");
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: allows the user to create a new folder
    private void addFolderMenu() {
        while (true) {
            String chosenName;
            try {
                chosenName = chooseFolderName();
            } catch (UserNoLongerWantsToCreateFolderException e) {
                break;
            }

            if (confirmNameCorrect(chosenName)) {
                Folder newFolder = currentFolder.makeSubfolder(chosenName);
                System.out.println("Created folder named \"" + newFolder.getName() + "\" in current folder (" + 
                currentFolder.getName() + ")");
                break;
            }
        }
    }
    
    // EFFECTS: enables the user to choose the name of their folder
    // throws UserNoLongerWantsToCreateFolderException if the user decides they no longer wish to create a folder
    private String chooseFolderName() throws UserNoLongerWantsToCreateFolderException {
        while (true) {
            System.out.println();
            System.out.println("Please enter the folder name or b to go back (if you wish " +
            "to name your folder b or B, enter namefolderb)");

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
            } else if (currentFolderContainsFolderNamed(input)) {
                String folderAlreadyNamedInputWithCorrectCase = currentFolder.getSubfolder(input).getName();
                System.out.println(currentFolder.getName() + " (current folder) already contains a folder named "
                + folderAlreadyNamedInputWithCorrectCase);
            } else {
                return input;
            }
        }
    }

    // EFFECTS: lets the user name their folder b or B or namefolderb (or any case variants, i.e. NameFolderB)
    private String nameFolderB() throws UserNoLongerWantsNameBException {
        while (true) {
            System.out.println();
            System.out.println("Please enter the folder name (b, B, namefolderb, namefolderB, etc.) " +
            "or prev to go to the previous menu");

            String input = getUserInputTrim();
            String inputLowerCase = input.toLowerCase();

            if (inputLowerCase.equals("p") || inputLowerCase.equals("prev") || inputLowerCase.equals("previous")) {
                throw new UserNoLongerWantsNameBException();
            } else if (inputLowerCase.equals("b") || inputLowerCase.equals("namefolderb")) {
                if (currentFolderContainsFolderNamed(input)) {
                    String folderAlreadyNamedInputWithCorrectCase = currentFolder.getSubfolder(input).getName();
                    System.out.println(currentFolder.getName() + " (current folder) already contains a folder named "
                    + folderAlreadyNamedInputWithCorrectCase);
                } else {
                    return input;
                }
            }
            else {
                System.out.println("Your input was not recognized as any of: b, B, namefolderb " +
                "(or namefolderb with different capitalization)");
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: allows the user to create a new label
    private void addLabelMenu() {
        while (true) {
            String chosenName;
            try {
                chosenName = chooseLabelName();
            } catch (UserNoLongerWantsToCreateALabelException e) {
                break;
            }

            if (confirmNameCorrect(chosenName)) {
                Label newLabel = new Label(chosenName);
                allLabels.add(newLabel);
                System.out.println("Created label named \"" + newLabel.getName());
                break;
            }
        }
    }

    // EFFECTS: enables the user to choose the name of their new label
    // throws UserNoLongerWantsToCreateALabelException if the user decides they no longer wish to create a label
    private String chooseLabelName() throws UserNoLongerWantsToCreateALabelException {
        while (true) {
            System.out.println();
            System.out.println("Please enter the label name or b to go back (if you wish " +
            "to name your label b or B, enter namelabelb)");

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
            } else if (labelExists(input)) {
                String labelAlreadyNamedInputWithCorrectCase = getLabel(input).getName();
                System.out.println("A label already exists named  " + labelAlreadyNamedInputWithCorrectCase);
            } else {
                return input;
            }
        }
    }

    // EFFECTS: lets the user name their label b or B or namelabelb (or any case variants, i.e. NameLabelB)
    private String nameLabelB() throws UserNoLongerWantsNameBException {
        while (true) {
            System.out.println();
            System.out.println("Please enter the label name (b, B, namelabelb, namelabelB, etc.) " +
            "or prev to go to the previous menu");

            String input = getUserInputTrim();
            String inputLowerCase = input.toLowerCase();

            if (inputLowerCase.equals("p") || inputLowerCase.equals("prev") || inputLowerCase.equals("previous")) {
                throw new UserNoLongerWantsNameBException();
            } else if (inputLowerCase.equals("b") || inputLowerCase.equals("namelabelb")) {
                if (labelExists(input)) {
                    String labelAlreadyNamedInputWithCorrectCase = getLabel(input).getName();
                    System.out.println("A label already exists named  " + labelAlreadyNamedInputWithCorrectCase);
                } else {
                    return input;
                }
            }
            else {
                System.out.println("Your input was not recognized as any of: b, B, namelabelb " +
                "(or namelabelb with different capitalization)");
            }
        }
    }


    // Edit Menu:

    // MODIFIES: this
    // EFFECTS: handles the edit menu input
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
        System.out.println("  \"fi\": Choose a file (in the current folder - " +
                            currentFolder.getName() + ") to edit");
        System.out.println("  \"fo\": Choose a folder (in the current folder - " +
                            currentFolder.getName() + ") to edit");
        System.out.println("  \"l\": Choose a label to edit");
        System.out.println("  \"b\": Back to the main menu");
    }

    // MODIFIES: this
    // EFFECTS: handles the edit menu input and calls the appropriate submenus as needed
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

    // MODIFIES: this
    // EFFECTS: allows the user to edit a file: open it, change its name, changes its labels, or delete it
    private void editFileMenu() {
        while (true) {
            System.out.println();
            System.out.println("Please enter the name of the file you would like to edit, l to list the files in " +
             "this folder (" + currentFolder.getName() + "), or b to go back");
             
            String input = getUserInputTrim();
            String inputLowerCase = input.toLowerCase();

            if (inputLowerCase.equals("b") || inputLowerCase.equals("back")) {
                break;
                // Returns to the edit menu
            } else if (inputLowerCase.equals("l") || inputLowerCase.equals("list")) {
                try {
                    listFilesAlphabetically(currentFolder.containedFiles());
                } catch (SetIsEmptyException e) {
                    System.out.println("There are no files in this folder");
                }
            } else if (currentFolderContainsFileNamed(input)) {
                editFile(currentFolder.getFile(input));
                break;
            } else {
                System.out.println("There is no file named \"" + input + "\" in this folder");
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: enables the user to open file, change its name, changes its labels, and delete it
    private void editFile(model.File file) {
        while (true) {
            displayEditFileMenuOptions(file.getName());

            String input = getUserInputTrimToLower();

            if (input.equals("b") || input.equals("back")) {
                break;
            } else {
                try {
                    handleEditFileMenuInput(input, file);
                } catch (CurrentObjectDeletedException e) {
                    break;
                }
            }
        }
    }

    // EFFECTS: displays the (first level of) edit file menu options
    private void displayEditFileMenuOptions(String fileName) {
        System.out.println();
        System.out.println(fileName + " is selected. Would you like to:");
        System.out.println("  \"o\": Open the file in your default text editor");
        System.out.println("  \"e\": Edit the file's name or add/remove labels");
        System.out.println("  \"d\": Delete the file");
        System.out.println("  \"b\": Back to the main menu");
    }
    
    // MODIFIES: this
    // EFFECTS: handles the edit file menu input and calls the appropriate functions as needed
    //          throws FileDeletedException if file's path is no longer valid
    private void handleEditFileMenuInput(String input, model.File file) throws CurrentObjectDeletedException {
        if (input.equals("o") || input.equals("open")) {
            try {
                openFile(file);
            } catch (FilePathNoLongerValidException e) {
                System.out.println("File at " + file.getFilePath() + " no longer exists");
            }
        } else if (input.equals("e") || input.equals("edit")) {
            editFileNameAndLabelsMenu(file);
        } else if (input.equals("d") || input.equals("delete")) {
            deleteFile(file);
            throw new CurrentObjectDeletedException();
        } else {
            System.out.println("Your input was not recognized as any of: o, e, d, or b");
        }
    }

    // MODIFIES: this, file
    // EFFECTS: allows the user to change file's name and add/remove labels from it
    private void editFileNameAndLabelsMenu(model.File file) {
        while (true) {
            displayEditFileNameAndLabelsOptions(file.getName());

            String input = getUserInputTrimToLower();

            if (input.equals("b") || input.equals("back")) {
                break;
            } else {
                handleEditFileNameAndLabelsMenuInput(input, file);
            }
        }
    }

    // EFFECTS: displays the option to change the file's name, add a label, remove a label, and remove all labels
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
    // EFFECTS: calls the methods enabling the user to change file's name and add/remove labels from it
    private void handleEditFileNameAndLabelsMenuInput(String input, model.File file) {
        if (input.equals("n") || input.equals("name")) {
            try {
                file.setName(chooseAndConfirmCustomFileName());
            } catch (UserNoLongerWantsCustomNameException e) {
                System.out.println("Name has not been changed and will remain " + file.getName());
            }
        } else if (input.equals("a") || input.equals("add")) {
            addAsManyLabelsAsDesiredOrPossible(file);
        } else if (input.equals("r") || input.equals("remove")) {
            removeAsManyLabelsAsDesiredOrPossible(file);
        } else if (input.equals("ra") || input.equals("remove all")) {
            confirmRemoveAllLables(file);
        } else {
            System.out.println("Your input was not recognized as any of: n, a, r, ra, or b");
        }
    }

    // MODIFIES: file, allLabels
    // EFFECTS: removes all labels from file
    private void confirmRemoveAllLables(model.File file) {
        if (confirmCompleteAction("remove all labels from " + file.getName())) {
            removeAllLabels(file);
            System.out.println("All labels removed");
        }
    }

    // MODIFIES: this, file
    // EFFECTS: confirms that the user wants to delete file and then deletes this folder's reference to it as well
    // as label's reference to it
    private void deleteFile(model.File file) {
        if (confirmCompleteAction("delete " + file.getName())) {
            currentFolder.removeFile(file);
            removeAllLabels(file);
        }
    }

    // MODIFIES: this
    // EFFECTS: allows the user to edit a folder: to open it, change its name, or delete it
    private void editFolderMenu() {
        while (true) {
            System.out.println();
            System.out.println("Please enter the name of the subfolder you would like to edit, l to list the folders in " +
             "this folder (" + currentFolder.getName() + "), or b to go back");
             
            String input = getUserInputTrim();
            String inputLowerCase = input.toLowerCase();

            if (inputLowerCase.equals("b") || inputLowerCase.equals("back")) {
                break;
                // Returns to the edit menu
            } else if (inputLowerCase.equals("l") || inputLowerCase.equals("list")) {
                try {
                    listFoldersAlphabetically(currentFolder.containedFolders());
                } catch (SetIsEmptyException e) {
                    System.out.println("This folder doesn't have any subfolders");
                }
            } else if (currentFolderContainsFolderNamed(input)) {
                editFolder(currentFolder.getSubfolder(input));
                break;
            } else {
                System.out.println("There is no file named \"" + input + "\" in this folder");
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: enables the user to open folder, change its name, and delete it
    private void editFolder(Folder folder) {
        while (true) {
            displayEditFolderMenuOptions(folder.getName());

            String input = getUserInputTrimToLower();

            if (input.equals("b") || input.equals("back")) {
                break;
            } else {
                try {
                    handleEditFolderMenuInput(input, folder);
                } catch (NewFolderOpenedException | CurrentObjectDeletedException e) {
                    break;
                }
            }
        }
    }

    // EFFECTS: displays the edit folder menu options
    private void displayEditFolderMenuOptions(String folderName) {
        System.out.println();
        System.out.println(folderName + " is selected. Would you like to:");
        System.out.println("  \"o\": Open the folder");
        System.out.println("  \"e\": Edit the folders's name");
        System.out.println("  \"d\": Delete the folder");
        System.out.println("  \"b\": Back to the main menu");
    }
    
    // MODIFIES: this
    // EFFECTS: handles the edit folder menu input and calls the appropriate functions as needed
    private void handleEditFolderMenuInput(String input, Folder folder) throws NewFolderOpenedException, 
    CurrentObjectDeletedException {
        if (input.equals("o") || input.equals("open")) {
            openFolder(folder);
            System.out.println(folder.getName() + " opened");
            throw new NewFolderOpenedException();
        } else if (input.equals("e") || input.equals("edit")) {
            editFolderNameMenu(folder);
        } else if (input.equals("d") || input.equals("delete")) {
            deleteFolderMenu(folder);
        } else {
            System.out.println("Your input was not recognized as any of: o, e, d, or b");
        }
    }

    // MODIFIES: this, folder
    // EFFECTS: allows the user to change folder's name
    private void editFolderNameMenu(Folder folder) {
        while (true) {
            System.out.println();
            System.out.println("Please enter the new folder name or b to go back (if you wish " +
            "to rename your folder to b or B, enter namefolderb)");

            String input = getUserInputTrim();
            String inputLowerCase = input.toLowerCase();

            if (inputLowerCase.equals("b") || inputLowerCase.equals("back")) {
                break;
            } else if (inputLowerCase.equals("namefolderb")) {
                try {
                    folder.setName(nameFolderB());
                    break;
                } catch (UserNoLongerWantsNameBException e) {
                    // Continue the loop so they can rename their folder something else
                }
            } else if (input.isEmpty()) {
                System.out.println("Folder name was not valid");
            } else if (currentFolderContainsFolderNamed(input)) {
                String folderAlreadyNamedInputWithCorrectCase = currentFolder.getSubfolder(input).getName();
                System.out.println(currentFolder.getName() + " (current folder) already contains a folder named "
                + folderAlreadyNamedInputWithCorrectCase);
            } else {
                folder.setName(input);
                break;
            }
        }
    }

    // MODIFIES: this, file
    // EFFECTS: confirms that the user wants to delete file and then deletes this folder's reference to it as well
    // as label's reference to it
    private void deleteFolderMenu(Folder folder) throws CurrentObjectDeletedException {
        if (confirmCompleteAction("delete " + folder.getName())) {
            currentFolder.removeSubfolder(folder);
            System.out.println(folder.getName() + " deleted");
            throw new CurrentObjectDeletedException();
        }
    }

    // MODIFIES: this
    // EFFECTS: allows the user to edit a label: they can open it, change its name, or delete it
    private void editLabelMenu() {
        while (true) {
            System.out.println();
            System.out.println("Please enter the name of the label you would like to edit, " + 
            "l to list all created labels or b to go back");
             
            String input = getUserInputTrim();
            String inputLowerCase = input.toLowerCase();

            if (inputLowerCase.equals("b") || inputLowerCase.equals("back")) {
                break;
                // Returns to the edit menu
            } else if (inputLowerCase.equals("l") || inputLowerCase.equals("list")) {
                try {
                    listLabelsAlphabetically(allLabels);
                } catch (SetIsEmptyException e) {
                    System.out.println("You have not created any labels");
                }
            } else if (labelExists(input)) {
                editLabel(getLabel(input));
                break;
            } else {
                System.out.println("There is no label named \"" + input + "\"");
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: enables the user to open label, change its name, and delete it
    private void editLabel(Label label) {
        while (true) {
            displayEditLabelMenuOptions(label.getName());

            String input = getUserInputTrimToLower();

            if (input.equals("b") || input.equals("back")) {
                break;
            } else {
                try {
                    handleEditLabelMenuInput(input, label);
                } catch (NewFolderOpenedException | CurrentObjectDeletedException e) {
                    break;
                }
            }
        }
    }

    // EFFECTS: displays the edit label menu options
    private void displayEditLabelMenuOptions(String labelName) {
        System.out.println();
        System.out.println(labelName + " is selected. Would you like to:");
        System.out.println("  \"o\": Open a directory with all files labelled with this label");
        System.out.println("  \"e\": Edit the label's name");
        System.out.println("  \"d\": Delete the label");
        System.out.println("  \"b\": Back to the main menu");
    }
    
    // MODIFIES: this
    // EFFECTS: handles the edit label menu input and calls the appropriate functions as needed
    private void handleEditLabelMenuInput(String input, Label label) throws NewFolderOpenedException, 
    CurrentObjectDeletedException {
        if (input.equals("o") || input.equals("open")) {
            openLabel(label);
            System.out.println("Current directory is every file labelled " + label.getName());
            throw new NewFolderOpenedException();
        } else if (input.equals("e") || input.equals("edit")) {
            editLabelNameMenu(label);
        } else if (input.equals("d") || input.equals("delete")) {
            deleteLabelMenu(label);
        } else {
            System.out.println("Your input was not recognized as any of: o, e, d, or b");
        }
    }

    // MODIFIES: this, label
    // EFFECTS: allows the user to change label's name
    private void editLabelNameMenu(Label label) {
        while (true) {
            System.out.println();
            System.out.println("Please enter the new label name or b to go back (if you wish " +
            "to rename your label to b or B, enter namelabelb)");

            String input = getUserInputTrim();
            String inputLowerCase = input.toLowerCase();

            if (inputLowerCase.equals("b") || inputLowerCase.equals("back")) {
                break;
            } else if (inputLowerCase.equals("namelabelb")) {
                try {
                    label.setName(nameLabelB());
                    break;
                } catch (UserNoLongerWantsNameBException e) {
                    // Continue the loop so they can rename the label something else
                }
            } else if (input.isEmpty()) {
                System.out.println("Label name was not valid");
            } else if (labelExists(input)) {
                String labelAlreadyNamedInputWithCorrectCase = getLabel(input).getName();
                System.out.println("A label already exists with name " + labelAlreadyNamedInputWithCorrectCase);
            } else {
                label.setName(input);
                break;
            }
        }
    }

    // MODIFIES: this, label
    // EFFECTS: confirms that the user wants to delete label and then deletes this folder's reference to it
    // and removes it from any files
    private void deleteLabelMenu(Label label) throws CurrentObjectDeletedException {
        if (confirmCompleteAction("delete " + label.getName())) {
            for (File file : label.getLabelledFiles()) {
                label.unlabelFile(file);
            }
            allLabels.remove(label);
            System.out.println(label.getName() + " deleted");
            throw new CurrentObjectDeletedException();
        }
    }


    // List Menu:

    // MODIFIES: this
    // EFFECTS: handles the list menu input
    private void listMenu() {
        while (true) {
            displayListMenuOptions();

            String input = getUserInputTrimToLower();

            if (input.equals("b") || input.equals("back")) {
                break;
            } else {
                handleListMenuInput(input);
            }
        }
    }

    // EFFECTS: displays the list menu options
    private void displayListMenuOptions() {
        System.out.println();
        System.out.println("You are in folder " + currentFolder.getName() + ". Would you like to:");
        System.out.println("  \"fi\": List the files in the current folder");
        System.out.println("  \"fo\": List the folders in the current folder");
        System.out.println("  \"f\": List both the files and the folders in the current folder");
        System.out.println("  \"b\": Back to the main menu");
    }

    // MODIFIES: this
    // EFFECTS: handles the edit menu input and calls the appropriate submenus as needed
    private void handleListMenuInput(String input) {
        if (input.equals("fi") || input.equals("file")) {
            try {
                listFilesAlphabetically(currentFolder.containedFiles());
            } catch (SetIsEmptyException e) {
                System.out.println("This folder does not contain any files");
            }
        } else if (input.equals("fo") || input.equals("folder")) {
            try {
                listFoldersAlphabetically(currentFolder.containedFolders());
            } catch (SetIsEmptyException e) {
                System.out.println("This folder does not contain any subfolders");
            }
        } else if (input.equals("f") || input.equals("files and folders")) {
            try {
                System.out.print("Files: ");
                listFilesAlphabetically(currentFolder.containedFiles());
            } catch (SetIsEmptyException e) {
                System.out.println("This folder does not contain any files");
            }
            try {
                System.out.print("Folders: ");
                listFoldersAlphabetically(currentFolder.containedFolders());
            } catch (SetIsEmptyException e) {
                System.out.println("This folder does not contain any subfolders");
            }
        } else {
            System.out.println("Your input was not recognized as any of: fi, fo, f, or b");
        }
    }


    // Navigate Menu:

    // MODIFIES: this
    // EFFECTS: handles the navigate menu input
    private void navigateMenu() {
        
    }


    // Open Menu:

    // MODIFIES: this
    // EFFECTS: handles the "open" menu input
    private void openMenu() {

    }


    // General helper methods:

    // Files:
    // EFFECTS: checks if path leads to a file on the user's computer
    private boolean isFilePathValid(String path) {
        java.io.File file = new java.io.File(path);

        return file.exists();
    }

    // EFFECTS: opens file if it still exists
    private void openFile(model.File file) throws FilePathNoLongerValidException {
        if (!isFilePathValid(file.getFilePath())) {
            throw new FilePathNoLongerValidException();
        }

        try {
            Desktop.getDesktop().open(new java.io.File(file.getFilePath()));
        } catch (IOException e) {
            System.out.println("File failed to open. Please contact the developer if this issue persists");
        }
    }

    // EFFECTS: opens folder
    private void openFolder(Folder folder) {
        currentFolder = folder;
    }

    // EFFECTS: creates a new folder with every File labelled file and sets currentFolder to that new folder
    private void openLabel(Label label) {
        Folder labelFolder = new Folder(label.getName());
        currentFolder = labelFolder;
    }

    // Confirmations:
    // EFFECTS: returns true if the user confirms that they want to complete action,
    // returns false if they confirm they do not want to
    /**
     * Prints out: "Are you sure you want to $action? Please enter yes or no"
     */
    private boolean confirmCompleteAction(String action) {
        while(true) {
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
        while(true) {
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


    // Cleaning up file paths:
    // EFFECTS: returns every character after the final backslash in string
    private String getCharactersAfterLastBackslash(String string) {
        String charsAfterMostRecentBackslash = "";
        int numberOfCharacters = string.length();

        for (int i = 0; i < numberOfCharacters; i++) {
            char character = string.charAt(i);

            if (character == '\\') {
                charsAfterMostRecentBackslash = "";
            } else {
                charsAfterMostRecentBackslash += character;
            }
        }

        return charsAfterMostRecentBackslash;
    }

    // EFFECTS: adds .txt to string if it does not end in .txt
    private String addDotTXTIfMissing(String string) {
        if (string.endsWith(".txt")) {
            return string;
        } else {
            return string + ".txt";
        }
    }

    // REQUIRES: string ends in .txt
    // EFFECTS: removes .txt from the end of string
    private String removeDotTXT(String string) {
        // Ensure the requires clause is met
        assert string.endsWith(".txt");

        int indexOfDot = string.length() - 4;

        return string.substring(0, indexOfDot);
    }


    // Labels:
    // EFFECTS: returns the first element found in a set of Labels
    // (if it's a HashSet, this isn't necessarily - and probably isn't - the first object added)
    private Label firstLabelFoundInSet(Set<Label> set) throws SetIsEmptyAndShouldNotBeException {
        for (Label label : set) {
            return label;
        }

        throw new SetIsEmptyAndShouldNotBeException();
    }

    // EFFECTS: returns label with given name or null if not found
    public Label getLabel(String name) {
        for (Label label : allLabels) {
            if (label.isNamed(name)) {
                return label;
            }
        }
        return null;
    }

    // EFFECTS: returns true if there exists a label named labelName otherwise returns false
    private boolean labelExists(String labelName) {
        return getLabel(labelName) != null;
    }

    // EFFECTS: returns all of the label on a file
    private Set<Label> getLabelsOnFile(model.File file) {
        Set<Label> labelsOnFile = new HashSet<Label>();
        for (Label label : allLabels) {
            if (file.isLabelled(label)) {
                labelsOnFile.add(label);
            }
        }
        return labelsOnFile;
    }

    // EFFECTS: returns every labels not on a file
    private Set<Label> getLabelsNotOnFile(model.File file) {
        Set<Label> unusedLabels = new HashSet<Label>(allLabels);
        for (Label label : allLabels) {
            if (file.isLabelled(label)) {
                unusedLabels.remove(label);
            }
        }
        return unusedLabels;
    }
    
    // MODIFIES: file
    // EFFECTS: removes every label on file
    private void removeAllLabels(model.File file) {
        for (Label label : getLabelsOnFile(file)) {
            label.unlabelFile(file);
        }
    }

    // MODIFIES: file
    // EFFECTS: lets the user label file with as many labels as they'd like to
    private void addAsManyLabelsAsDesiredOrPossible(model.File file) {
        Set<Label> unusedLabels = getLabelsNotOnFile(file);

        if (unusedLabels.size() > 1) {
            try {
                chooseLabelsToAdd(file, unusedLabels);
                Label lastLabel = firstLabelFoundInSet(unusedLabels);
                addLastRemainingLabel(file, lastLabel);
            } catch (UserNoLongWantsToChangeLabelsException e) {
                return;
            }
        } else if (unusedLabels.size() == 1) {
            Label lastLabel = firstLabelFoundInSet(unusedLabels);
            try {
                addLastRemainingLabel(file, lastLabel);
            } catch (UserNoLongWantsToChangeLabelsException e) {
                return;
            }
        } else {
            System.out.println(file.getName() + " already has every label");
        }
    }

    // MODIFIES: file
    // EFFECTS: lets the user label file with as many labels as they'd like to while there is more than one option
    private void chooseLabelsToAdd(model.File file, Set<Label> unusedLabels) throws UserNoLongWantsToChangeLabelsException {
        while (unusedLabels.size() > 1) {
            System.out.println();
            System.out.println("Enter the name of the label you would like to add to the file, " +
             "enter l to list the available labels, or enter b to stop adding labels");

             String input = getUserInputTrimToLower();

             if (input.equals("b") || input.equals("back")) {
                throw new UserNoLongWantsToChangeLabelsException();
            } else if (input.equals("l") || input.equals("list")) {
                try {
                    listLabelsAlphabetically(unusedLabels);
                } catch (SetIsEmptyException e) {
                    // Not going to happen since it has size > 1
                }
            } else if (labelExists(input)) {
                Label label = getLabel(input);
                if (unusedLabels.contains(label)) {
                    file.addLabel(label);
                    System.out.println(file.getName() + " is now labelled " + label.getName());
                    unusedLabels.remove(label);
                } else {
                    System.out.println("File is already labelled " + label.getName());
                }
            } else {
                System.out.println("Your input was not recognized as a label, l, or b");
            }
        }
    }

    // MODIFIES: file
    // EFFECTS: lets the user choose whether to add the last label to the file or not
    private void addLastRemainingLabel(model.File file, Label lastLabel) throws UserNoLongWantsToChangeLabelsException {
        while (true) {
            System.out.println();
            System.out.println("Would you like to label the file \"" + lastLabel.getName() + 
            "\"? (This is the only label not on this file) y or n");

             String input = getUserInputTrimToLower();

             if (input.equals("n") || input.equals("no")) {
                 throw new UserNoLongWantsToChangeLabelsException();
             } else if (input.equals("y") || input.equals("yes")) {
                 file.addLabel(lastLabel);
                 System.out.println(file.getName() + " is now labelled " + lastLabel.getName());
                 break;
            } else {
                 System.out.println("Your input was not recognized as a y or n");
            }
        }
    }

    // MODIFIES: file
    // EFFECTS: lets the user remove any labels on file
    private void removeAsManyLabelsAsDesiredOrPossible(model.File file) {
        Set<Label> labelsOnFile = getLabelsOnFile(file);

        if (labelsOnFile.size() > 1) {
            try {
                chooseLabelsToRemove(file, labelsOnFile);
                Label lastLabel = firstLabelFoundInSet(labelsOnFile);
                removeOnlyLabel(file, lastLabel);
            } catch (UserNoLongWantsToChangeLabelsException e) {
                return;
            }
        } else if (labelsOnFile.size() == 1) {
            Label lastLabel = firstLabelFoundInSet(labelsOnFile);
            try {
                removeOnlyLabel(file, lastLabel);
            } catch (UserNoLongWantsToChangeLabelsException e) {
                return;
            }
        } else {
            System.out.println(file.getName() + " already is not labelled with any labels");
        }
    }

    // MODIFIES: file
    // EFFECTS: lets the user remove as many labels as they'd like to while there is more than one option
    private void chooseLabelsToRemove(model.File file, Set<Label> labelsOnFile) throws UserNoLongWantsToChangeLabelsException {
        while (labelsOnFile.size() > 1) {
            System.out.println();
            System.out.println("Enter the name of the label you would like to remove to the file, " +
             "enter l to list the labels the file is labelled with, or enter b to stop removing labels");

             String input = getUserInputTrimToLower();

             if (input.equals("b") || input.equals("back")) {
                throw new UserNoLongWantsToChangeLabelsException();
            } else if (input.equals("l") || input.equals("list")) {
                try {
                    listLabelsAlphabetically(labelsOnFile);
                } catch (SetIsEmptyException e) {
                    // Not going to happen since it has size > 1
                }
            } else if (labelExists(input)) {
                Label label = getLabel(input);
                if (labelsOnFile.contains(label)) {
                    label.unlabelFile(file);
                    System.out.println(file.getName() + " is no longer labelled " + label.getName());
                    labelsOnFile.remove(label);
                } else {
                    System.out.println("File is already not labelled " + label.getName());
                }
            } else {
                System.out.println("Your input was not recognized as a label, l, or b");
            }
        }
    }

    // MODIFIES: file
    // EFFECTS: lets the user choose whether to add the last label to the file or not
    private void removeOnlyLabel(model.File file, Label lastLabel) throws UserNoLongWantsToChangeLabelsException {
        while (true) {
            System.out.println();
            System.out.println("Would you like to remove the label " + lastLabel.getName() + " from the file \""
             + lastLabel.getName() + "\"? (This is the only label on this file) y or n");

             String input = getUserInputTrimToLower();

             if (input.equals("n") || input.equals("no")) {
                 throw new UserNoLongWantsToChangeLabelsException();
             } else if (input.equals("y") || input.equals("yes")) {
                 lastLabel.unlabelFile(file);
                 System.out.println(file.getName() + " is now labelled " + lastLabel.getName());
                 break;
            } else {
                 System.out.println("Your input was not recognized as a y or n");
            }
        }
    }


    // currentFolder Contents:
    // EFFECTS: returns true if currentFolder contains a file named fileName otherwise returns false
    private boolean currentFolderContainsFileNamed(String fileName) {
        return currentFolder.getFile(fileName) != null;
    }

    // EFFECTS: returns true if currentFolder contains a folder named foldername otherwise returns false
    private boolean currentFolderContainsFolderNamed(String folderName) {
        return currentFolder.getSubfolder(folderName) != null;
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


    // Listing alphabetically:
    // EFFECTS: alphabetizes (ignoring case) nameList and then prints it out separated by commas
    private void listStringListAlphabetically(List<String> nameList) {
        nameList.sort(Comparator.comparing(String::toLowerCase));
        
        int indexOfLastLabel = nameList.size() - 1;
        String lastLabelName = nameList.get(indexOfLastLabel);
        nameList.remove(indexOfLastLabel);

        for (String labelName : nameList) {
            System.out.print(labelName + ", ");
        }
        System.out.println(lastLabelName);
    }

    // EFFECTS: lists the names of all of the files in fileSet
    private void listFilesAlphabetically(Set<model.File> fileSet) throws SetIsEmptyException {
        if (fileSet.isEmpty()) {
            throw new SetIsEmptyException();
        }

        List<String> fileNameList = new ArrayList<String>();
        for (model.File file : fileSet) {
            String fileName = file.getName();
            fileNameList.add(fileName);
        }

        listStringListAlphabetically(fileNameList);
    }

    // EFFECTS: lists the names of all of the files in folderSet
    private void listFoldersAlphabetically(Set<Folder> folderSet) throws SetIsEmptyException {
        if (folderSet.isEmpty()) {
            throw new SetIsEmptyException();
        }

        List<String> folderNameList = new ArrayList<String>();
        for (Folder folder : folderSet) {
            String folderName = folder.getName();
            folderNameList.add(folderName);
        }

        listStringListAlphabetically(folderNameList);
    }

    // EFFECTS: lists all of the names of the labels in labelSet alphabetically
    private void listLabelsAlphabetically(Set<Label> labelSet) throws SetIsEmptyException {
        if (labelSet.isEmpty()) {
            throw new SetIsEmptyException();
        }

        List<String> labelNameList = new ArrayList<String>();
        for (Label label : labelSet) {
            String labelName = label.getName();
            labelNameList.add(labelName);
        }

        listStringListAlphabetically(labelNameList);
    }
}