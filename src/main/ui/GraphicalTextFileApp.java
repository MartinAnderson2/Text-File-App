package ui;

import model.FileSystem;
import model.exceptions.FilePathNoLongerValidException;
import model.exceptions.NameIsBlankException;
import model.exceptions.NameIsTakenException;
import model.exceptions.NoSuchFileFoundException;
import model.exceptions.NoSuchFolderFoundException;
import persistence.exceptions.InvalidJsonException;

import java.util.List;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;

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
        setTitle("Text File Application");
        setSize(WIDTH, HEIGHT);

        addMenu();
        addFoldersAndFiles();

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout());
		setVisible(true);
    }

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

    // EFFECTS: adds item with event handler action to theMenu
    private void addMenuItem(JMenu theMenu, AbstractAction action) {
		JMenuItem menuItem = new JMenuItem(action);
		menuItem.setMnemonic(menuItem.getText().charAt(0));
		theMenu.add(menuItem);
	}

    // Represents the action that should be taken when the user wants to addd a new file to the system
    private class AddFileAction extends AbstractAction {

        AddFileAction() {
            super("Add File");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            String filePath = JOptionPane.showInputDialog(null,
            "Please enter the path of the file",
            "Add File",
            JOptionPane.QUESTION_MESSAGE);
            String fileName = JOptionPane.showInputDialog(null,
            "What would you like to name the file?",
            "Add File",
            JOptionPane.QUESTION_MESSAGE);

            try {
                fileSystem.createFile(fileName, filePath);
                updateFoldersAndFiles();
            } catch (NameIsTakenException e1) {
                JOptionPane.showMessageDialog(null, "Name is taken", "Error Adding",
                JOptionPane.ERROR_MESSAGE);
            } catch (NameIsBlankException e1) {
                JOptionPane.showMessageDialog(null, "Name is invalid", "Error Adding",
                JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Represents the action that should be taken when the user wants to addd a new folder to the system
    private class AddFolderAction extends AbstractAction {

        AddFolderAction() {
            super("Add Folder");
        }
        
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
                JOptionPane.showMessageDialog(null, "Name is taken", "Error Adding",
                JOptionPane.ERROR_MESSAGE);
            } catch (NameIsBlankException e1) {
                JOptionPane.showMessageDialog(null, "Name is invalid", "Error Adding",
                JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Represents the action that should be taken when the user wants to open a file in the current folder
    private class OpenFileAction extends AbstractAction {

        OpenFileAction() {
            super("Open File");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            String fileName = JOptionPane.showInputDialog(null,
            "Which file would you like to open?",
            "Open File",
            JOptionPane.QUESTION_MESSAGE);

            try {
                fileSystem.openFile(fileName);
            } catch (NoSuchFileFoundException e1) {
                JOptionPane.showMessageDialog(null, "There is no file with that name in this folder", "Error Opening",
                JOptionPane.ERROR_MESSAGE);
            } catch (FilePathNoLongerValidException e1) {
                JOptionPane.showMessageDialog(null, "There is no file at that location anymore", "Error Opening",
                JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Represents the action that should be taken when the user wants to open a folder in the current folder
    private class OpenFolderAction extends AbstractAction {

        OpenFolderAction() {
            super("Open Folder");
        }
        
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
                JOptionPane.showMessageDialog(null, "There is no folder with that name in this folder", "Error Opening",
                JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Represents the action that should be taken when the user wants to go up one directory level
    private class OpenParentFolderAction extends AbstractAction {

        OpenParentFolderAction() {
            super("Open Parent Folder");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                fileSystem.goUpOneDirectoryLevel();
                updateFoldersAndFiles();
            } catch (NoSuchFolderFoundException e1) {
                JOptionPane.showMessageDialog(null, "Current folder does not have a parent", "Error Opening",
                JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Represents the action that should be taken when the user wants to go up one directory level
    private class LoadAction extends AbstractAction {

        LoadAction() {
            super("Load File Sytem");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                fileSystem = FileSystem.autoLoad();
                remove(currentFolderPanel);
                updateFoldersAndFiles();
                JOptionPane.showMessageDialog(null, "Loading Succeeded!", "Load",
                JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException | InvalidJsonException e1) {
                JOptionPane.showMessageDialog(null, "Loading Failed", "Load Error",
						JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Represents the action that should be taken when the user wants to go up one directory level
    private class SaveAction extends AbstractAction {

        SaveAction() {
            super("Save File System");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                fileSystem.autoSave();
                JOptionPane.showMessageDialog(null, "Saving Succeeded!", "Save",
                JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(null, "Saving Failed", "Save Error",
                JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: adds the folders and the files to new panels and adds them to the main window
    private void addFoldersAndFiles() {
        currentFolderPanel = new JPanel();

        JPanel folderPanel = new JPanel();
        List<String> folderNames = fileSystem.getNamesOfSubfolders();
        for (String folderName : folderNames) {
            JButton jButton = new JButton(folderName);
            jButton.setBackground(Color.CYAN);
            jButton.setEnabled(false);
            folderPanel.add(jButton);
        }
        folderPanel.setVisible(true);
        currentFolderPanel.add(folderPanel);

        JPanel filePanel = new JPanel();
        List<String> fileNames = fileSystem.getNamesOfSubfiles();
        for (String fileName : fileNames) {
            JButton jButton = new JButton(fileName);
            jButton.setBackground(Color.YELLOW);
            jButton.setEnabled(false);
            folderPanel.add(jButton);
        }
        filePanel.setVisible(true);
        currentFolderPanel.add(filePanel);

        add(currentFolderPanel);

        revalidate();
        repaint();
    }

    // MODIFIES: this
    // EFFECTS: removes the old folders and files, adds the folders and the files to new panels and adds them to the main window
    private void updateFoldersAndFiles() {
        remove(currentFolderPanel);
        currentFolderPanel = new JPanel();

        JPanel folderPanel = new JPanel();
        List<String> folderNames = fileSystem.getNamesOfSubfolders();
        for (String folderName : folderNames) {
            JButton jButton = new JButton(folderName);
            jButton.setBackground(Color.CYAN);
            jButton.setEnabled(false);
            folderPanel.add(jButton);
        }
        folderPanel.setVisible(true);
        currentFolderPanel.add(folderPanel);

        JPanel filePanel = new JPanel();
        List<String> fileNames = fileSystem.getNamesOfSubfiles();
        for (String fileName : fileNames) {
            JButton jButton = new JButton(fileName);
            jButton.setBackground(Color.YELLOW);
            jButton.setEnabled(false);
            folderPanel.add(jButton);
        }
        filePanel.setVisible(true);
        currentFolderPanel.add(filePanel);

        add(currentFolderPanel);

        revalidate();
        repaint();
    }
}
