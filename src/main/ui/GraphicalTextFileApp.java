package ui;

import model.FileSystem;
import model.exceptions.FilePathNoLongerValidException;
import model.exceptions.NameIsBlankException;
import model.exceptions.NameIsTakenException;
import model.exceptions.NoSuchFileFoundException;
import model.exceptions.NoSuchFolderFoundException;
import persistence.exceptions.InvalidJsonException;

import java.util.Comparator;
import java.util.List;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

// Represents an application that allows users to add .txt files from their computer to the program and browse through
// them. Structure design based on [AlarmSystem](https://github.students.cs.ubc.ca/CPSC210/AlarmSystem)
// This is the graphical version of the application in which the user interacts with a GUI
public class GraphicalTextFileApp extends JFrame {
    private static final int WIDTH = 960;
    private static final int HEIGHT = 600;

    private FileSystem fileSystem;

    private JDesktopPane desktop;
    private JPanel currentFolderPanel;

    // EFFECTS: sets up the main panel and the buttons on it and adds the folders and files
    public GraphicalTextFileApp() {
        try {
            fileSystem = FileSystem.autoLoad();
        } catch (IOException | InvalidJsonException e) {
            fileSystem = new FileSystem();
        }

        desktop = new JDesktopPane();

        setContentPane(desktop);
        setTitle(ConsoleTextFileApp.appName);
        setSize(WIDTH, HEIGHT);
        setLayout(new BorderLayout());

        addLogoInBottomRight();
        addMenu();
        addFoldersAndFiles();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout());
        setVisible(true);
    }

    // MODIFIES: this
    // EFFECTS: adds a bar of buttons to the top of the screen
    private void addMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu addMenu = new JMenu("Add");
        addMenu.setMnemonic('A');
        addMenuItem(addMenu, new AddFileAction());
        addMenuItem(addMenu, new AddFolderAction());
        menuBar.add(addMenu);

        JMenu openMenu = new JMenu("Open");
        openMenu.setMnemonic('O');
        addMenuItem(openMenu, new OpenFileAction());
        addMenuItem(openMenu, new OpenFolderAction());
        addMenuItem(openMenu, new OpenParentFolderAction());
        menuBar.add(openMenu);

        JMenu loadMenu = new JMenu("Load");
        loadMenu.setMnemonic('L');
        addMenuItem(loadMenu, new LoadAction());
        menuBar.add(loadMenu);

        JMenu saveMenu = new JMenu("Save");
        saveMenu.setMnemonic('S');
        addMenuItem(saveMenu, new SaveAction());
        menuBar.add(saveMenu);

        setJMenuBar(menuBar);
    }

    // MODIFIES: this
    // EFFECTS: adds item with event handler action to theMenu
    private void addMenuItem(JMenu theMenu, AbstractAction action) {
        JMenuItem menuItem = new JMenuItem(action);
        menuItem.setMnemonic(menuItem.getText().charAt(0));
        theMenu.add(menuItem);
    }

    // Represents the action that should be taken when the user wants to add a new
    // file to the system
    private class AddFileAction extends AbstractAction {

        AddFileAction() {
            super("Add File");
        }

        // MODIFIES: this
        // EFFECTS: gets the file name and file path from the user. If they are valid
        // then creates a new file with that
        // name and path. If it fails then tells the user why
        @Override
        public void actionPerformed(ActionEvent e) {
            String fileName = JOptionPane.showInputDialog(null,
                    "What would you like to name the file?",
                    "Add File",
                    JOptionPane.QUESTION_MESSAGE);
            String filePath = JOptionPane.showInputDialog(null,
                    "Please enter the path of the file",
                    "Add File",
                    JOptionPane.QUESTION_MESSAGE);

            try {
                fileSystem.createFile(fileName, filePath);
                updateFoldersAndFiles();
            } catch (NameIsTakenException e1) {
                showErrorMessage("Name is taken", "Error Adding");
            } catch (NameIsBlankException e1) {
                showErrorMessage("Name is invalid", "Error Adding");
            }
        }
    }

    // Represents the action that should be taken when the user wants to addd a new
    // folder to the system
    private class AddFolderAction extends AbstractAction {

        AddFolderAction() {
            super("Add Folder");
        }

        // MODIFIES: this
        // EFFECTS: gets the folder name from the user. If it is valid then creates a
        // new folder with that name. If it
        // fails then tells the user why
        @Override
        public void actionPerformed(ActionEvent e) {
            String folderName = JOptionPane.showInputDialog(null,
                    "What would you like to name the folder?",
                    "Add Folder",
                    JOptionPane.QUESTION_MESSAGE);

            try {
                fileSystem.createFolder(folderName);
                updateFoldersAndFiles();
            } catch (NameIsTakenException e1) {
                showErrorMessage("Name is taken", "Error Adding");
            } catch (NameIsBlankException e1) {
                showErrorMessage("Name is invalid", "Error Adding");
            }
        }
    }

    // Represents the action that should be taken when the user wants to open a file
    // in the current folder
    private class OpenFileAction extends AbstractAction {

        OpenFileAction() {
            super("Open File");
        }

        // EFFECTS: gets the file name from the user then opens that file. If it fails
        // then tells the user why
        @Override
        public void actionPerformed(ActionEvent e) {
            String fileName = JOptionPane.showInputDialog(null,
                    "Which file would you like to open?",
                    "Open File",
                    JOptionPane.QUESTION_MESSAGE);

            try {
                fileSystem.openFile(fileName);
            } catch (NoSuchFileFoundException e1) {
                showErrorMessage("There is no file with that name in this folder", "Error Opening");
            } catch (FilePathNoLongerValidException e1) {
                showErrorMessage("There is no file at that location anymore", "Error Opening");
            }
        }
    }

    // Represents the action that should be taken when the user wants to open a
    // folder in the current folder
    private class OpenFolderAction extends AbstractAction {

        OpenFolderAction() {
            super("Open Folder");
        }

        // MODIFIES: this
        // EFFECTS: gets the folder name from the user then opens that folder. If it
        // fails then tells the user why
        @Override
        public void actionPerformed(ActionEvent e) {
            String folderName = JOptionPane.showInputDialog(null,
                    "Which folder would you like to open?",
                    "Open Folder",
                    JOptionPane.QUESTION_MESSAGE);

            try {
                fileSystem.openFolder(folderName);
                updateFoldersAndFiles();
            } catch (NoSuchFolderFoundException e1) {
                showErrorMessage("There is no folder with that name in this folder", "Error Opening");
            }
        }
    }

    // Represents the action that should be taken when the user wants to go up one
    // directory level
    private class OpenParentFolderAction extends AbstractAction {

        OpenParentFolderAction() {
            super("Open Parent Folder");
        }

        // MODIFIES: this
        // EFFECTS: opens the parent folder of the current folder. If this folder is the
        // root folder then tells the
        // user that it doesn't have a parent
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                fileSystem.goUpOneDirectoryLevel();
                updateFoldersAndFiles();
            } catch (NoSuchFolderFoundException e1) {
                showErrorMessage("Current folder does not have a parent", "Error Opening");
            }
        }
    }

    // Represents the action that should be taken when the user wants to go up one
    // directory level
    private class LoadAction extends AbstractAction {

        LoadAction() {
            super("Load File Sytem");
        }

        // MODIFIES: this
        // EFFECTS: attempts to load a file system from the default save location. Tells
        // the user if it failed or if
        // it suceeded
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                fileSystem = FileSystem.autoLoad();
                updateFoldersAndFiles();
                JOptionPane.showMessageDialog(null, "Loading Succeeded!", "Load",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException | InvalidJsonException e1) {
                showErrorMessage("Loading Failed", "Load Error");
            }
        }
    }

    // Represents the action that should be taken when the user wants to go up one
    // directory level
    private class SaveAction extends AbstractAction {

        SaveAction() {
            super("Save File System");
        }

        // EFFECTS: attempts to save this file system to the default save location.
        // Tells the user if it failed or if
        // it suceeded
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                fileSystem.autoSave();
                JOptionPane.showMessageDialog(null, "Saving Succeeded!", "Save",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e1) {
                showErrorMessage("Saving Failed", "Save Error");
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: shows an error popup with title tile and message message
    private void showErrorMessage(String message, String title) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

    // MODIFIES: this
    // EFFECTS: adds the folders and the files to new panels and adds them to the
    // main window
    private void addFoldersAndFiles() {
        currentFolderPanel = new JPanel();

        currentFolderPanel.add(addFoldersToPanel());
        currentFolderPanel.add(addFilesToPanel());

        add(currentFolderPanel);

        revalidate();
        repaint();
    }

    // MODIFIES: this
    // EFFECTS: removes the old folders and files, adds the folders and the files to
    // new panels and adds them to the
    // main window
    private void updateFoldersAndFiles() {
        remove(currentFolderPanel);
        currentFolderPanel = new JPanel();

        currentFolderPanel.add(addFoldersToPanel());
        currentFolderPanel.add(addFilesToPanel());

        add(currentFolderPanel);

        revalidate();
        repaint();
    }

    // EFFECTS: creates a new panel to which all of the names of the folders are
    // added as disabled buttons
    private JPanel addFoldersToPanel() {
        JPanel folderPanel = new JPanel();
        List<String> folderNames = fileSystem.getNamesOfSubfolders();
        sortListAlphabetically(folderNames);

        for (String folderName : folderNames) {
            JButton jbutton = new JButton(folderName);
            jbutton.setBackground(Color.CYAN);
            jbutton.setEnabled(false);
            folderPanel.add(jbutton);
        }
        folderPanel.setVisible(true);

        return folderPanel;
    }

    // EFFECTS: creates a new panel to which all of the names of the files are added
    // as disabled buttons
    private JPanel addFilesToPanel() {
        JPanel filePanel = new JPanel();
        List<String> fileNames = fileSystem.getNamesOfSubfiles();
        sortListAlphabetically(fileNames);

        for (String fileName : fileNames) {
            JButton jbutton = new JButton(fileName);
            jbutton.setBackground(Color.YELLOW);
            jbutton.setEnabled(false);
            filePanel.add(jbutton);
        }
        filePanel.setVisible(true);

        return filePanel;
    }

    // MODIFIES: list
    // EFFECTS: sorts list alphabetically (from A-Z) ignoring case
    private void sortListAlphabetically(List<String> list) {
        // Code taken from Stack Overflow:
        // https://stackoverflow.com/questions/8432581/how-to-sort-a-listobject-alphabetically-using-object-name-field
        list.sort(Comparator.comparing(String::toLowerCase));
    }

    // MODIFIES: this
    // EFFECTS: adds the logo of a small pine tree in the bottom right of the
    // application window
    private void addLogoInBottomRight() {
        try {
            // Taken from [Stack Overflow](https://stackoverflow.com/a/2706730)
            BufferedImage logo = ImageIO.read(new java.io.File("data/images/pine-tree.png"));
            JLabel logoLabel = new JLabel(new ImageIcon(logo));

            logoLabel.setToolTipText("Joshua tree icons created by Pixel perfect - Flaticon");
            add(logoLabel, BorderLayout.SOUTH);
        } catch (IOException e) {
            showErrorMessage("Image failed to load", "Resource Error");
        }
    }
}
