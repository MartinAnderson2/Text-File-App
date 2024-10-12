package ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import model.*;

// Represents the application that allows users to add .txt files from their computer to the program and sort and
// browse through them. Structure design based on "TellerApp"
public class TextFileApp {
    private Scanner scanner;
    List<Folder> rootFolders;

    // EFFECTS: starts the Text File application
    public TextFileApp() {
        runTextFileApp();
    }

    // MODIFIES: this
    // EFFECTS: processes user input
    private void runTextFileApp() {
        initialize();

        welcome();

        while (true) {
            displayCommands();
            String input = scanner.next().toLowerCase();
        }
    }
    
    // MODIFIES: this
    // EFFECTS: initializes the application: instantiates rootFolders
    private void initialize() {
        rootFolders = new ArrayList<Folder>();
    }

    // EFFECTS: displays the first level of menu options that are available
    private void displayCommands() {

    }
    
    // EFFECTS: sends the user a welcome message
    private void welcome() {
        System.out.println("Welcome to the Text File application!");
    }
}
