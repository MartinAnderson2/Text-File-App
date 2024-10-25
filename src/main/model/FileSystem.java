package model;

import java.util.Set;

import ui.exceptions.FilePathNoLongerValidException;
import ui.exceptions.SetIsEmptyAndShouldNotBeException;

import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.io.IOException;
import java.awt.Desktop;

// Represents a file system with folders and files as well as labels that can be applied to files.
// Folders can be navigated and it is possible to get all of the folders and all of the files in the current folder.
// Files, folders, and labels can be created and deleted. Files can be labelled and unlabelled. Files can be opened in
// the user's default text editor. Folders can be opened to access their contents. It is possible to list all files
// labelled with a given label.
public class FileSystem {
    public static final String EXAMPLE_FILE_PATH = "C:\\Users\\User\\Documents\\Note Name.txt";

    private Folder rootFolder;
    private Folder currentFolder;

    private Set<Label> labels;

    // EFFECTS: initializes the variables needed for the file system:
    // rootFolder: for the Folder that contains the initial Folders and Files, and indirectly contains every Folder and
    //             File since every Folder or File is a subfolder or subfile of root or one of root's subfolders (or a
    //             subfolder's subfolder, and so on...)
    // currentFolder: initialized to the root folder such that folders can be created and files can be added
    // allLabels: stores all of the labels the user creates
    public FileSystem() {
        rootFolder = new Folder("root");
        currentFolder = rootFolder;

        labels = new HashSet<Label>();
    }


    /* 
     *  Basic Getters and Setters:
     */

    // EFFECTS: returns currentFolder's name
    public String getCurrentFolderName() {
        return currentFolder.getName();
    }

    // REQUIRES: currentFolderHasParent() is true
    // EFFECTS: returns currentFolder's parent's name
    public String getParentFolderName() {
        return currentFolder.getParentFolder().getName();
    }


    /* 
     *  File, Folder, and Label Methods:
     */

    // File:

    // REQUIRES: currentFolder does not contain a File named name and !name.isEmpty()
    // EFFECTS: creates a new File in currentFolder with given name and path
    public void createFile(String name, String path) {
        currentFolder.makeSubfile(name, path);
    }

    // REQUIRES: fileWithNameAlreadyExists(fileName) is true
    // EFFECTS: opens File named fileName in user's default text editor
    // throws FilePathNoLongerValidException if the File no longer exists on their computer
    public void openFile(String fileName) throws FilePathNoLongerValidException {
        File file = currentFolder.getSubfile(fileName);

        if (!FileSystem.isFilePathValid(file.getFilePath())) {
            throw new FilePathNoLongerValidException();
        }


        try {
            Desktop.getDesktop().open(new java.io.File(file.getFilePath()));
            System.out.println(getCurrentFolderName() + " opened");
        } catch (IOException e) {
            System.out.println("File failed to open. Please contact the developer if this issue persists");
        }
    }

    // REQUIRES : fileWithNameAlreadyExists(fileName) is true
    // MODIFIES: this
    // EFFECTS: deletes File named fileName
    public void deleteFile(String fileName) {
        removeAllLabels(fileName);
        currentFolder.removeSubfile(fileName);
    }

    // REQUIRES: fileWithNameAlreadyExists(fileName) is true
    // EFFECTS: sets the name of File named fileName to newName
    public void setFileName(String fileName, String newName) {
        currentFolder.getSubfile(fileName).setName(newName);
    }

    // REQUIRES: fileWithNameAlreadyExists(fileName) is true
    // EFFECTS: returns the file path of File named fileName
    public String getFilePath(String fileName) {
        return currentFolder.getSubfile(fileName).getFilePath();
    }

    // EFFECTS: returns true if currentFolder contains a File named fileName otherwise returns false
    public boolean fileWithNameAlreadyExists(String fileName) {
        return currentFolder.getSubfile(fileName) != null;
    }

    // REQUIRES: fileWithNameAlreadyExists(fileNameWrongCase) is true,
    // i.e. currentFolder contains a File named fileNameWrongCase (but potentially with different capitalization)
    // EFFECTS: returns the actual capitalization of the name of the File named fileNameWrongCase
    public String getCapitalizationOfFile(String fileNameWrongCase) {
        return currentFolder.getSubfile(fileNameWrongCase).getName();
    }

    // REQUIRES: fileWithNameAlreadyExists(fileName) is true and labelExists(labelName)
    // EFFECTS: returns true if File named FileName is labelled with label named LabelName
    public boolean fileLabelled(String fileName, String labelName) {
        File file = currentFolder.getSubfile(fileName);
        Label label = getLabel(labelName);
        return file.isLabelled(label);
    }

    // REQUIRES: fileWithNameAlreadyExists(fileName) is true
    // EFFECTS: returns a list of the names of the Files that are subfiles of currentFolder
    public List<String> getNamesOfAllSubfiles() {
        List<String> namesOfSubfiles = new LinkedList<String>();
        for (File file : currentFolder.getSubfiles()) {
            namesOfSubfiles.add(file.getName());
        }
        return namesOfSubfiles;
    }

    // Folder:

    // REQUIRES: !folderName.isEmpty()
    // MODIFIES: currentFolder
    // EFFECTS: creates a new subfolder in the current directory with name folderName
    public void createFolder(String folderName) {
        currentFolder.makeSubfolder(folderName);
    }

    // REQUIRES: folderWithNameAlreadyExists(folderName) is true
    // MODIFIES: this
    // EFFECTS: opens Folder named folderName, i.e. makes that Folder the current directory
    public void openFolder(String folderName) {
        currentFolder = currentFolder.getSubfolder(folderName);
    }

    // REQURIES: currentFolderHasParent() is true
    // MODIFIES: this
    // EFFECTS: opens the parent folder of currentFolder
    public void goUpOneDirectoryLevel() {
        currentFolder = currentFolder.getParentFolder();
    }

    // MODIFIES: this
    // EFFECTS: opens the root folder
    public void openRootFolder() {
        currentFolder = rootFolder;
    }

    // REQUIRES: folderWithNameAlreadyExists(folderName) is true
    // MODIFIES: this
    // EFFECTS: deletes Folder named folderName
    public void deleteFolder(String folderName) {
        currentFolder.removeSubfolder(folderName);
    }

    // REQUIRES: folderWithNameAlreadyExists(folderName) is true
    // EFFECTS: sets the name of Folder named folderName to newName
    public void setFolderName(String folderName, String newName) {
        currentFolder.getSubfolder(folderName).setName(newName);
    }

    // EFFECTS: returns true if the currently-opened folder has a parent and false if it does not
    public boolean currentFolderHasParent() {
        return currentFolder.getParentFolder() != null;
    }

    // EFFECTS: returns true if currentFolder contains a Folder named folderName otherwise returns false
    public boolean folderWithNameAlreadyExists(String folderName) {
        return currentFolder.getSubfolder(folderName) != null;
    }

    // REQUIRES: folderWithNameAlreadyExists(folderName) is true
    // EFFECTS: returns a list of the names of the Folders that are direct subfolders of currentFolder
    public List<String> getNamesOfAllSubfolders() {
        List<String> namesOfSubfolders = new LinkedList<String>();
        for (Folder folder : currentFolder.getSubfolders()) {
            namesOfSubfolders.add(folder.getName());
        }
        return namesOfSubfolders;
    }

    // REQUIRES: folderWithNameAlreadyExists(folderNameWrongCase) is true,
    // i.e. currentFolder contains a Folder named folderNameWrongCase (but potentially with different capitalization)
    // EFFECTS: returns the actual capitalization of the name of the Folder named folderNameWrongCase
    public String getCapitalizationOfFolder(String folderNameWrongCase) {
        return currentFolder.getSubfolder(folderNameWrongCase).getName();
    }

    // Label:

    // REQUIRES: !labelName.isEmpty()
    // MODIFIES: this
    // EFFECTS: creates a new Label named labelName (and no Files are labelled it)
    public void createLabel(String labelName) {
        labels.add(new Label(labelName));
    }

    // REQUIRES: labelExists(labelName) is true
    // MODIFIES: this
    // EFFECTS: creates a new folder with every File labelled file and sets currentFolder to that new folder
    // this is not an actual folder in the file system but rataher a fake one to view all files labelled with the
    // given label. This is a read-only directory
    public void openLabel(String labelName) {
        Folder labelFolder = new Folder(labelName);
        for (File file : getLabel(labelName).getLabelledFiles()) {
            labelFolder.makeSubfile(file.getName(), file.getFilePath());
        }
        currentFolder = labelFolder;
    }

    // REQUIRES: labelExists(labelName) is true
    // MODIFIES: this
    // EFFECTS: deletes Label with name labelName
    public void deleteLabel(String labelName) {
        Label label = getLabel(labelName);
        label.unlabelAllFiles();
        labels.remove(label);
        System.out.println(labelName + " has been deleted");
    }

    // REQUIRES: exactlyOneLabelExists() is true and fileWithNameAlreadyExists(fileName) is true
    // MODIFIES: this (specifically File named fileName)
    // EFFECTS: labels File named fileName with the only label that the user has made so far
    public void labelFileWithTheOnlyLabel(String fileName) {
        firstLabelFound(labels).labelFile(currentFolder.getSubfile(fileName));
    }

    // REQUIRES: fileWithNameAlreadyExists(fileName) is true and labelExists(labelName) is true
    // MODIFIES: this (specifically File named fileName)
    // EFFECTS: labels File named fileName with the Label named labelName
    public void labelFile(String fileName, String labelName) {
        File file = currentFolder.getSubfile(fileName);
        Label label = getLabel(labelName);
        label.labelFile(file);
    }

    // REQUIRES: fileWithNameAlreadyExists(fileName) is true and labelExists(labelName) is true
    // MODIFIES: this (specifically File named fileName)
    // EFFECTS: removes Label named labelName from the File named fileName
    public void unlabelFile(String fileName, String labelName) {
        File file = currentFolder.getSubfile(fileName);
        Label label = getLabel(labelName);
        label.unlabelFile(file);
    }

    // REQUIRES: labelExists(labelName) is true
    // MODIFIES: this
    // EFFECTS: sets the name of Label named labelName to newName
    public void setLabelName(String labelName, String newName) {
        getLabel(labelName).setName(newName);
    }

    // EFFECTS: returns true if the user has created any labels and false if no labels have been made
    public boolean anyLabelsExist() {
        return !labels.isEmpty();
    }

    // EFFECTS: returns true if the user has created exactly 1 label
    public boolean exactlyOneLabelExists() {
        return labels.size() == 1;
    }

    // EFFECTS: returns true if there exists a label named labelName otherwise returns false
    public boolean labelExists(String labelName) {
        return labels.contains(getLabel(labelName));
    }

    // REQUIRES: labelExists(labelNameWrongCase) is true,
    // i.e. labels contains a Label named labelNameWrongCase (but potentially with different capitalization)
    // EFFECTS: returns the actual capitalization of the name of the Label named labelNameWrongCase
    public String getCapitalizationOfLabel(String labelNameWrongCase) {
        return getLabel(labelNameWrongCase).getName();
    }

    // REQUIRES: fileWithNameAlreadyExists(fileName) is true
    // MODIFIES: this (File named fileName)
    // EFFECTS: removes all of the labels on File named fileName
    public void removeAllLabels(String fileName) {
        File file = currentFolder.getSubfile(fileName);
        
        for (Label label : labels) {
            if (file.isLabelled(label)) {
                label.unlabelFile(file);
            }
        }
    }

    // REQUIRES: exactlyOneLabelExists() is true
    // EFFECTS: returns the name of the only label the user has made
    public String getOnlyLabelName() {
        return firstLabelFound(labels).getName();
    }

    public int getNumLabels() {
        return labels.size();
    }

    // REQUIRES: fileWithNameAlreadyExists(fileName) is true
    public int getNumLabelsOnFile(String fileName) {
        return currentFolder.getSubfile(fileName).getNumLabels();
    }

    // REQUIRES: fileWithNameAlreadyExists(fileName) is true
    // EFFECTS: returns the number of labels that are not on File named fileName
    public int getNumLabelsNotOnFile(String fileName) {
        return getNumLabels() - currentFolder.getSubfile(fileName).getNumLabels();
    }

    // EFFECTS: returns a list of the names of the Labels the user has created so far (empty if none)
    public List<String> getNamesOfAllLabels() {
        List<String> namesOfLabels = new LinkedList<String>();
        for (Label label : labels) {
            namesOfLabels.add(label.getName());
        }
        return namesOfLabels;
    }

    // REQUIRES: fileWithNameAlreadyExists(fileName) is true
    // EFFECTS: returns a list of the names of the Labels on File named fileName
    public List<String> getNamesOfAllLabelsOnFile(String fileName) {
        File file = currentFolder.getSubfile(fileName);
        List<String> namesOfLabelsOnFile = new LinkedList<String>();
        for (Label label : labels) {
            if (file.isLabelled(label)) {
                namesOfLabelsOnFile.add(label.getName());
            }
        }
        return namesOfLabelsOnFile;
    }

    // REQUIRES: fileWithNameAlreadyExists(fileName) is true
    // EFFECTS: returns a list of the names of the Labels not on File named fileName
    public List<String> getNamesOfAllLabelsNotOnFile(String fileName) {
        File file = currentFolder.getSubfile(fileName);
        List<String> namesOfLabelsNotOnFile = new LinkedList<String>();
        for (Label label : labels) {
            if (!file.isLabelled(label)) {
                namesOfLabelsNotOnFile.add(label.getName());
            }
        }
        return namesOfLabelsNotOnFile;
    }


    /*
     *  Static Methods:
     */

    // EFFECTS: returns true if a file exists at path on the user's computer otherwise returns false
    public static boolean isFilePathValid(String path) {
        java.io.File file = new java.io.File(path);

        return file.exists();
    }


    /* 
     *  Helper Methods:
     */

    // EFFECTS: returns the first element found in a set of Labels
    // (this isn't necessarily - and probably isn't - the first object added)
    private Label firstLabelFound(Set<Label> labels) throws SetIsEmptyAndShouldNotBeException {
        for (Label label : labels) {
            return label;
        }
        throw new SetIsEmptyAndShouldNotBeException();
    }

    // TODO:
    // EFFECTS: returns label with given name or null if not found
    private Label getLabel(String name) {
        for (Label label : labels) {
            if (label.isNamed(name)) {
                return label;
            }
        }
        return null;
    }
}
