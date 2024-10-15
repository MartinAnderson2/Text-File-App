package ui;

import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
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
            displayMainMenuCommands();
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
    private void displayMainMenuCommands() {
        System.out.println();
        System.out.println("Would you like to:");
        System.out.println("  \"a\": Add a file, folder, or label to the current folder");
        System.out.println("  \"e\": Edit a file, folder, or label in the current directory");
        System.out.println("  \"l\": List the files and folders, just files, or just folders in the current directory");
        System.out.println("  \"n\": Navigate the file system");
        System.out.println("  \"o\": Open a file or folder, open all files with a given label");
        System.out.println("  \"s\": Search for file or folder");
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
        } else if (input.equals("s") || input.equals("search")) {
            searchMenu();
        } else {
            System.out.println("Your input was not recognized as any of: a, e, l, n, o, s, or q");
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
        boolean back = false;
        while (!back) {
            displayAddMenuCommands();

            String input = getUserInputTrimToLower();

            if (input.equals("b") || input.equals("back")) {
                back = true;
            } else {
                handleAddMenuInput(input);
            }
        }
    }

    // EFFECTS: displays the (first level of) add menu commands
    private void displayAddMenuCommands() {
        System.out.println();
        System.out.println("Would you like to:");
        System.out.println("  \"file\": Add a file to the current folder");
        System.out.println("  \"folder\": Add a folder to the current folder");
        System.out.println("  \"label\": Create a new label");
        System.out.println("  \"b\": Back to the main menu");
    }

    // MODIFIES: this
    // EFFECTS: handles the add menu input and calls the appropriate submenus as needed
    private void handleAddMenuInput(String input) {
        if (input.equals("file")) {
            addFileMenu();
        } else if (input.equals("folder")) {
            addFolderMenu();
        } else if (input.equals("label")) {
            addLabelMenu();
        } else {
            System.out.println("Your input was not recognized as any of: file, folder, label, or b");
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
            } else if (confirmCorrectFilePathAdded(path)) {
                System.out.println("Great!");
                
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

    // EFFECTS: gets the user to confirm that the path they entered is correct.
    // If they are happy with the path, returns true otherwise returns false
    private boolean confirmCorrectFilePathAdded(String path) {
        java.io.File file = new java.io.File(path);

        return file.exists();
    }

    // EFFECTS: enables the user to create a custom name for their newly added file
    //          or use the name of the file on their computer
    private String chooseFileName(String filePath) {
        String fileName = getCharactersAfterLastBackslash(filePath);
        fileName = removeDotTXT(fileName);

        if (currentFolderContainsFileWithName(fileName)) {
            return chooseCustomFileNameNoGoBack();
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

    // EFFECTS: returns true if currentFolder contains a file named fileName otherwise returns false
    private boolean currentFolderContainsFileWithName(String fileName) {
        return currentFolder.getFile(fileName) != null;
    }

    // EFFECTS: enables the user to choose the custom name for their file and then confirms it is correct.
    // Throws UserNoLongerWantsCustomNameException if they no longer wish for a custom name
    private String chooseAndConfirmCustomFileName() throws UserNoLongerWantsCustomNameException {
        while (true) {
            String chosenName = chooseCustomFileName();

            if (confirmCustomFileName(chosenName)) {
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
            } else if (currentFolderContainsFileWithName(input)) {
                String fileAlreadyNamedInputWithCase = currentFolder.getFile(input).getName();
                System.out.println(currentFolder.getName() + " (current folder) already contains a file named "
                + fileAlreadyNamedInputWithCase);
            } else {
                return input;
            }
        }
    }

    // EFFECTS: enables the user to choose the custom name for their file and then confirms it is correct.
    private String chooseAndConfirmCustomFileNameNoGoBack() {
        while (true) {
            String chosenName = chooseCustomFileNameNoGoBack();

            if (confirmCustomFileName(chosenName)) {
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
            } else if (currentFolderContainsFileWithName(input)) {
                String fileAlreadyNamedInputWithCase = currentFolder.getFile(input).getName();
                System.out.println(currentFolder.getName() + " (current folder) already contains a file named "
                + fileAlreadyNamedInputWithCase);
            } else {
                return input;
            }
        }
    }

    // EFFECTS: returns true if the user confirms that chosenName is correct, returns false if the user confirms chosenName is incorrect
    private boolean confirmCustomFileName(String chosenName) {
        while(true) {
            System.out.println();
            System.out.println("Is the name \"" + chosenName + "\" correct? Please enter y or n");

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
                if (currentFolderContainsFileWithName(input)) {
                    String fileAlreadyNamedInputWithCase = currentFolder.getFile(input).getName();
                    System.out.println(currentFolder.getName() + " (current folder) already contains a file named "
                    + fileAlreadyNamedInputWithCase);
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
    // EFFECTS: enables the user to add any labels to the file they wish to
    private void addFileLabels(model.File file) {
        while(true) {
            System.out.println();
            System.out.println("Would you like to add labels to the file? Please enter y or n");

            String input = getUserInputTrimToLower();

            if (input.equals("n") || input.equals("no")) {
                break;
            } else if (input.equals("y") || input.equals("yes")) {
                chooseLabels(file);
                break;
            } else {
                System.out.println("Your input was not recognized as either of: y or n");
            }
        }
    }

    // MODIFIES: file
    // EFFECTS: lets the user label file
    private void chooseLabels(File file) {
        if (allLabels.size() == 1) {
            addTheOnlyCreatedLabel(file);
        }
        else {
            addAsManyLabelsAsDesiredOrPossible(file);
        }
    }

    // REQUIRES: there is only one label in allLabels
    // MODIFIES: file
    // EFFECTS: enables the user to either label the file with the only current label or not label it
    private void addTheOnlyCreatedLabel(File file) {
        Label theOnlyCurrentLabel = firstLabelFoundInSet(allLabels);
        while(true) {
            System.out.println();
            System.out.println("Would you like to label the file \"" + theOnlyCurrentLabel.getName() + "\"? Please enter y or n");

            String input = getUserInputTrimToLower();

            if (input.equals("n") || input.equals("no")) {
                break;
            } else if (input.equals("y") || input.equals("yes")) {
                file.addLabel(theOnlyCurrentLabel);
                break;
            } else {
                System.out.println("Your input was not recognized as either of: y or n");
            }
        }
    }

    // MODIFIES: file
    // EFFECTS: lets the user label file with as many labels as they'd like to
    private void addAsManyLabelsAsDesiredOrPossible(File file) {
        Set<Label> unusedLabels = new HashSet<Label>(allLabels);
        try {
            chooseLabels(file, unusedLabels);

            Label lastLabel = firstLabelFoundInSet(unusedLabels);
            addLastRemainingLabel(file, lastLabel);
        } catch (UserNoLongWantsToAddLabelsException e) {
            // Stop whenever the user no longer wishes to add labels
        }
    }

    // MODIFIES: file
    // EFFECTS: lets the user label file with as many labels as they'd like to while there is more than one option
    private void chooseLabels(File file, Set<Label> unusedLabels) throws UserNoLongWantsToAddLabelsException {
        while (unusedLabels.size() > 1) {
            System.out.println();
            System.out.println("Enter the name of the label you would like to add to the file," +
             "enter l to list the available labels, or enter b to stop adding labels");

             String input = getUserInputTrimToLower();

             if (input.equals("b") || input.equals("back")) {
                 throw new UserNoLongWantsToAddLabelsException();
             } else if (input.equals("l") || input.equals("list")) {
                 listLabelSetAlphabetically(allLabels);
             } else if (labelExists(input)) {
                Label label = getLabel(input);
                file.addLabel(label);
                System.out.println(file.getName() + " labelled " + label.getName());
             } else {
                 System.out.println("Your input was not recognized as a label, list, or b");
             }
        }
    }

    // MODIFIES: file
    // EFFECTS: lets the user choose whether to add the last label to the file or not
    private void addLastRemainingLabel(File file, Label lastLabel) throws UserNoLongWantsToAddLabelsException {
        while (true) {
            System.out.println();
            System.out.println("Would you like to label the file \"" + lastLabel.getName() + "\"? y or n");

             String input = getUserInputTrimToLower();

             if (input.equals("n") || input.equals("no")) {
                 throw new UserNoLongWantsToAddLabelsException();
             } else if (input.equals("y") || input.equals("yes")) {
                 file.addLabel(lastLabel);
                 System.out.println(file.getName() + " labelled " + lastLabel.getName());
            } else {
                 System.out.println("Your input was not recognized as a y or n");
            }
        }
    }

    // EFFECTS: returns true if currentFolder contains a file named fileName otherwise returns false
    private boolean labelExists(String labelName) {
        return getLabel(labelName) != null;
    }

    // MODIFIES: this
    // EFFECTS: allows the user to create a new folder
    private void addFolderMenu() {
        
    }

    // MODIFIES: this
    // EFFECTS: allows the user to add a file from their computer to the program
    private void addLabelMenu() {
        
    }


    // Edit Menu:

    // MODIFIES: this
    // EFFECTS: handles the edit menu input
    private void editMenu() {
        
    }


    // List Menu:

    // MODIFIES: this
    // EFFECTS: handles the list menu input
    private void listMenu() {

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


    // Search Menu:

    // MODIFIES: this
    // EFFECTS: handles the search menu input
    private void searchMenu() {
        
    }


    // General helper methods:

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

    // EFFECTS: returns the first element found in a set of Labels
    // (if it's a HashSet, this isn't necessarily - and probably isn't - the first object added)
    private Label firstLabelFoundInSet(Set<Label> set) {
        for (Label label : set) {
            return label;
        }

        throw new SetIsEmptyException();
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

    // REQUIRES: labelSet contains at least one element
    // EFFECTS: lists all of the names of the labels in labelSet alphabetically
    private void listLabelSetAlphabetically(Set<Label> labelSet) {
        List<String> labelNameList = new ArrayList<String>();
        for (Label label : labelSet) {
            String labelName = label.getName();
            labelNameList.add(labelName);
        }
        labelNameList.sort(Comparator.comparing(String::toLowerCase));
        
        int indexOfLastLabel = labelNameList.size() - 1;
        String lastLabelName = labelNameList.get(indexOfLastLabel);
        labelNameList.remove(indexOfLastLabel);

        for (String labelName : labelNameList) {
            System.out.print(labelName + ", ");
        }
        System.out.println(lastLabelName);
    }
}