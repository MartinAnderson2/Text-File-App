package model;

import java.util.Set;

import model.exceptions.*;

import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.io.IOException;
import java.awt.Desktop;

// Represents a file system with Folders and Files as well as Labels that can be applied to Files.
// Folders can be navigated and it is possible to get all of the Folders and all of the Files in the current Folder.
// Files, Folders, and Labels can be created and deleted. Files can be labelled and unlabelled. Files can be opened in
// the user's default text editor. Folders can be opened to access their contents. It is possible to list all Files
// labelled with a given Label.
public class FileSystem {
    public static final String EXAMPLE_FILE_PATH = "C:\\Users\\User\\Documents\\Note Name.txt";

    private Folder rootFolder;
    private Folder currentFolder;

    private Set<Label> labels;

    // EFFECTS: initializes the variables needed for the file system:
    // rootFolder: for the Folder that contains the initial Folders and Files, and indirectly contains every Folder and
    //             File since every Folder or File is a subfolder or subfile of root or one of root's subfolders (or a
    //             subfolder's subfolder, and so on...)
    // currentFolder: initialized to the root Folder such that Folders can be created and Files can be added
    // allLabels: stores all of the Labels the user creates
    public FileSystem() {
        try {
            rootFolder = new Folder("root");
        } catch (NameIsEmptyException e) {
            // Won't happen unless Folder is implemented incorrectly
        }
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

    // EFFECTS: returns currentFolder's parent's name. Throws NoSuchFolderFoundException if currentFolder does not have
    // a parent Folder
    // throws NoSuchFolderFoundException if currentFolder does not have a parent
    public String getParentFolderName() throws NoSuchFolderFoundException {
        return currentFolder.getParentFolder().getName();
    }


    /* 
     *  File, Folder, and Label Methods:
     */

    // File:

    // MODIFIES: this
    // EFFECTS: creates a new File in currentFolder with given name and path
    // throws NameIsEmptyException if the provided name is empty
    // throws NameIsTakenException if currentFolder already contains a Folder named name
    public void createFile(String name, String path) throws NameIsEmptyException, NameIsTakenException {
        currentFolder.makeSubfile(name, path);
    }

    // EFFECTS: opens File named fileName in user's default text editor
    // throws FilePathNoLongerValidException if the File no longer exists on their computer
    // throws NoSuchFileFoundException if there are no Files named fileName in currentFolder
    public void openFile(String fileName) throws NoSuchFileFoundException, FilePathNoLongerValidException {
        File file = currentFolder.getSubfile(fileName);

        if (!FileSystem.isFilePathValid(file.getFilePath())) {
            throw new FilePathNoLongerValidException();
        }


        try {
            Desktop.getDesktop().open(new java.io.File(file.getFilePath()));
        } catch (IOException e) {
            throw new FilePathNoLongerValidException();
        }
    }

    // MODIFIES: this
    // EFFECTS: deletes File named fileName
    // throws NoSuchFileFoundException if there are no Files named fileName in currentFolder
    public void deleteFile(String fileName) throws NoSuchFileFoundException {
        removeAllLabels(fileName);
        currentFolder.removeSubfile(fileName);
    }

    // EFFECTS: sets the name of File named fileName to newName
    // throws NoSuchFileFoundException if there are no Files named fileName in currentFolder
    // throws NameIsTakenException if currentFolder already contains a Folder named name
    // throws NameIsEmptyException is fileName.isEmpty() is true
    public void setFileName(String fileName, String newName)
            throws NoSuchFileFoundException, NameIsTakenException, NameIsEmptyException {
        if (fileWithNameAlreadyExists(fileName)) {
            throw new NameIsTakenException(getCapitalizationOfFile(fileName));
        }
        currentFolder.getSubfile(fileName).setName(newName);
    }

    // EFFECTS: returns the file path of File named fileName
    // throws NoSuchFileFoundException if there are no Files named fileName in currentFolder
    public String getFilePath(String fileName) throws NoSuchFileFoundException {
        return currentFolder.getSubfile(fileName).getFilePath();
    }

    // EFFECTS: returns true if currentFolder contains a File named fileName otherwise returns false
    public boolean fileWithNameAlreadyExists(String fileName) {
        try {
            currentFolder.getSubfile(fileName);
            return true;
        } catch (NoSuchFileFoundException e) {
            return false;
        }
    }

    // REQUIRES: fileWithNameAlreadyExists(fileNameWrongCase) is true
    // EFFECTS: returns the actual capitalization of the name of the File named fileNameWrongCase
    // throws NoSuchFileFoundException if there are no Files named fileName in currentFolder
    public String getCapitalizationOfFile(String fileNameWrongCase) {
        try {
            return currentFolder.getSubfile(fileNameWrongCase).getName();
        } catch (NoSuchFileFoundException e) {
            throw new RequiresClauseNotMetRuntimeException();
        }
    }

    // EFFECTS: returns true if File named FileName is labelled with Label named LabelName
    // throws NoSuchFileFoundException if there are no files named fileName in currentFolder
    // throws NoSuchLabelFoundException if there are no labels named labelName
    public boolean fileLabelled(String fileName, String labelName)
            throws NoSuchFileFoundException, NoSuchLabelFoundException {
        File file = currentFolder.getSubfile(fileName);
        Label label = getLabel(labelName);
        return file.isLabelled(label);
    }

    // EFFECTS: returns a list of the names of the Files that are subfiles of currentFolder
    public List<String> getNamesOfAllSubfiles() {
        List<String> namesOfSubfiles = new LinkedList<String>();
        for (File file : currentFolder.getSubfiles()) {
            namesOfSubfiles.add(file.getName());
        }
        return namesOfSubfiles;
    }

    // Folder:

    // MODIFIES: currentFolder
    // EFFECTS: creates a new subfolder in the current directory with name folderName
    // throws NameIsEmptyException is folderName.isEmpty() is true
    // throws NameIsTakenException is folderWithNameAlreadyExists(folderName) is true
    public void createFolder(String folderName) throws NameIsEmptyException, NameIsTakenException {
        currentFolder.makeSubfolder(folderName);
    }

    // MODIFIES: this
    // EFFECTS: opens Folder named folderName, i.e. makes that Folder the current directory
    // throws NoSuchFolderFoundException if there are no Folders named folderName in currentFolder
    public void openFolder(String folderName) throws NoSuchFolderFoundException {
        currentFolder = currentFolder.getSubfolder(folderName);
    }

    // MODIFIES: this
    // EFFECTS: opens the parent Folder of currentFolder
    // throws NoSuchFolderFoundException if currentFolder does not have a parent
    public void goUpOneDirectoryLevel() throws NoSuchFolderFoundException {
        currentFolder = currentFolder.getParentFolder();
    }

    // MODIFIES: this
    // EFFECTS: opens the root Folder
    public void openRootFolder() {
        currentFolder = rootFolder;
    }

    // MODIFIES: this
    // EFFECTS: deletes Folder named folderName
    // throws NoSuchFolderFoundException if there are no Folders named folderName in currentFolder
    public void deleteFolder(String folderName) throws NoSuchFolderFoundException {
        currentFolder.removeSubfolder(folderName);
    }

    // EFFECTS: sets the name of Folder named folderName to newName
    // throws NoSuchFolderFoundException if there are no Folders named folderName in currentFolder
    // throws NameIsTakenException if currentFolder already contains a Folder named name
    // throws NameIsEmptyException is folderName.isEmpty() is true
    public void setFolderName(String folderName, String newName)
            throws NoSuchFolderFoundException, NameIsTakenException, NameIsEmptyException {
        if (folderWithNameAlreadyExists(folderName)) {
            throw new NameIsTakenException(getCapitalizationOfFolder(folderName));
        }
        currentFolder.getSubfolder(folderName).setName(newName);
    }

    // EFFECTS: returns true if the currently-opened Folder has a parent and false if it does not
    public boolean currentFolderHasParent() {
        try {
            currentFolder.getParentFolder();
            return true;
        } catch (NoSuchFolderFoundException e) {
            return false;
        }
    }

    // EFFECTS: returns true if currentFolder contains a Folder named folderName otherwise returns false
    public boolean folderWithNameAlreadyExists(String folderName) {
        return currentFolder.hasSubfolder(folderName);
    }

    // EFFECTS: returns a list of the names of the Folders that are direct subfolders of currentFolder
    public List<String> getNamesOfAllSubfolders() {
        List<String> namesOfSubfolders = new LinkedList<String>();
        for (Folder folder : currentFolder.getSubfolders()) {
            namesOfSubfolders.add(folder.getName());
        }
        return namesOfSubfolders;
    }

    // REQUIRES: folderWithNameAlreadyExists(folderNameWrongCase) is true
    // EFFECTS: returns the actual capitalization of the name of the Folder named folderNameWrongCase
    // throws NoSuchFolderFoundException if there are no Folders named folderNameWrongCase in currentFolder
    public String getCapitalizationOfFolder(String folderNameWrongCase) {
        try {
            return currentFolder.getSubfolder(folderNameWrongCase).getName();
        } catch (NoSuchFolderFoundException e) {
            throw new RequiresClauseNotMetRuntimeException();
        }
    }

    // Label:

    // MODIFIES: this
    // EFFECTS: creates a new Label named labelName (and no Files are labelled it)
    // throws NameIsEmptyException if labelName is empty
    // throws NameIsTakenException if there is already a label named labelName
    public void createLabel(String labelName) throws NameIsEmptyException, NameIsTakenException {
        if (labelName.isEmpty()) {
            throw new NameIsEmptyException();
        }
        if (labelExists(labelName)) {
            throw new NameIsTakenException(getCapitalizationOfLabel(labelName));
        }
        labels.add(new Label(labelName));
    }

    // MODIFIES: this
    // EFFECTS: creates a new Folder with every File labelled File and sets currentFolder to that new Folder
    // this is not an actual Folder in the File system but rataher a fake one to view all Files labelled with the
    // given Label. This is a brand-new directory to which adding and removing Files and Folders is pointless
    // throws NoSuchLabelFoundException if there are no Labels named labelName
    // throws NameIsTakenException if currentFolder already contains a 
    public void openLabel(String labelName) throws NameIsEmptyException, NoSuchLabelFoundException {
        if (labelName.isEmpty()) {
            throw new NameIsEmptyException();
        }
        Folder labelFolder = new Folder(labelName);
        for (File file : getLabel(labelName).getLabelledFiles()) {
            labelFolder.addExistingSubfile(file);
        }
        currentFolder = labelFolder;
    }

    // MODIFIES: this
    // EFFECTS: deletes Label with name labelName
    // throws NoSuchLabelFoundException if there are no Labels named labelName
    public void deleteLabel(String labelName) throws NoSuchLabelFoundException {
        Label label = getLabel(labelName);
        label.unlabelAllFiles();
        labels.remove(label);
        System.out.println(labelName + " has been deleted");
    }

    // MODIFIES: this
    // EFFECTS: labels File named fileName with the only Label that the user has made so far
    // throws SetIsEmptyAndShouldNotBeException if there are no Labels in labels
    // throws NoSuchFileFoundException if there are no Files named fileName
    // throws ThereExistsMoreThanOneLabelException if there is more than one Label in labels
    public void labelFileWithTheOnlyLabel(String fileName)
            throws SetIsEmptyAndShouldNotBeException, NoSuchFileFoundException, ThereExistsMoreThanOneLabelException {
        if (!exactlyOneLabelExists()) {
            throw new ThereExistsMoreThanOneLabelException();
        }
        firstLabelFound(labels).labelFile(currentFolder.getSubfile(fileName));
    }

    // MODIFIES: this
    // EFFECTS: labels File named fileName with the Label named labelName
    // throws NoSuchFileFoundException if there are no Files named fileName in currentFolder
    // throws NoSuchLabelFoundException if there are no Labels named labelName
    public void labelFile(String fileName, String labelName)
            throws NoSuchFileFoundException, NoSuchLabelFoundException {
        File file = currentFolder.getSubfile(fileName);
        Label label = getLabel(labelName);
        label.labelFile(file);
    }

    // MODIFIES: this
    // EFFECTS: removes Label named labelName from the File named fileName
    // throws NoSuchFileFoundException if there are no Files named fileName in currentFolder
    // throws NoSuchFileFoundException if there are no Files named fileName in currentFolder
    public void unlabelFile(String fileName, String labelName)
            throws NoSuchFileFoundException, NoSuchLabelFoundException {
        File file = currentFolder.getSubfile(fileName);
        Label label = getLabel(labelName);
        label.unlabelFile(file);
    }

    // MODIFIES: this
    // EFFECTS: sets the name of Label named labelName to newName
    // throws NoSuchLabelFoundException if there are no Labels named labelName
    public void setLabelName(String labelName, String newName) throws NoSuchLabelFoundException {
        try {
            getLabel(labelName).setName(newName);
        } catch (NameIsEmptyException e) {
            throw new NoSuchLabelFoundException();
        }
    }

    // EFFECTS: returns true if the user has created any Labels and false if no Labels have been made
    public boolean anyLabelsExist() {
        return !labels.isEmpty();
    }

    // EFFECTS: returns true if the user has created exactly 1 Label
    public boolean exactlyOneLabelExists() {
        return labels.size() == 1;
    }

    // EFFECTS: returns true if there exists a Label named labelName otherwise returns false
    public boolean labelExists(String labelName) {
        try {
            getLabel(labelName);
            return true;
        } catch (NoSuchLabelFoundException e) {
            return false;
        }
    }

    // REQUIRES: labelExists(labelNameWrongCase) is true
    // EFFECTS: returns the actual capitalization of the name of the Label named labelNameWrongCase
    // throws NoSuchLabelFoundException if there are no Labels named labelNameWrongCase
    public String getCapitalizationOfLabel(String labelNameWrongCase) {
        try {
            return getLabel(labelNameWrongCase).getName();
        } catch (NoSuchLabelFoundException e) {
            throw new RequiresClauseNotMetRuntimeException();
        }
    }

    // MODIFIES: this (File named fileName)
    // EFFECTS: removes all of the Labels on File named fileName
    // throws NoSuchFileFoundException if there are no Files named fileName
    public void removeAllLabels(String fileName) throws NoSuchFileFoundException {
        File file = currentFolder.getSubfile(fileName);
        
        for (Label label : labels) {
            if (file.isLabelled(label)) {
                label.unlabelFile(file);
            }
        }
    }

    // EFFECTS: returns the name of the only Label the user has made
    // throws ThereExistsMoreThanOneLabelException if there is more than one Label
    public String getOnlyLabelName() throws ThereExistsMoreThanOneLabelException {
        if (!exactlyOneLabelExists()) {
            throw new ThereExistsMoreThanOneLabelException();
        }
        return firstLabelFound(labels).getName();
    }

    public int getNumLabels() {
        return labels.size();
    }

    // EFFECTS: returns the number of Labels on File named fileName
    // throws NoSuchFileFoundException if there are no Files named fileName in currentFolder
    public int getNumLabelsOnFile(String fileName) throws NoSuchFileFoundException {
        return currentFolder.getSubfile(fileName).getNumLabels();
    }

    // EFFECTS: returns the number of Labels that are not on File named fileName
    // throws NoSuchFileFoundException if there are no Files named fileName in currentFolder
    public int getNumLabelsNotOnFile(String fileName) throws NoSuchFileFoundException {
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

    // EFFECTS: returns a list of the names of the Labels on File named fileName
    // throws NoSuchFileFoundException if there are no Files named fileName in currentFolder
    public List<String> getNamesOfAllLabelsOnFile(String fileName) throws NoSuchFileFoundException {
        File file = currentFolder.getSubfile(fileName);
        List<String> namesOfLabelsOnFile = new LinkedList<String>();
        for (Label label : labels) {
            if (file.isLabelled(label)) {
                namesOfLabelsOnFile.add(label.getName());
            }
        }
        return namesOfLabelsOnFile;
    }

    // EFFECTS: returns a list of the names of the Labels not on File named fileName
    // throws NoSuchFileFoundException if there are no Files named fileName in currentFolder
    public List<String> getNamesOfAllLabelsNotOnFile(String fileName) throws NoSuchFileFoundException {
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

    // EFFECTS: returns true if a File exists at path on the user's computer otherwise returns false
    public static boolean isFilePathValid(String path) {
        java.io.File file = new java.io.File(path);

        return file.exists();
    }


    /* 
     *  Helper Methods:
     */

    // EFFECTS: returns the first element found in a set of Labels
    // (this isn't necessarily - and probably isn't - the first object added)
    // throws SetIsEmptyAndShouldNotBeException if labels is empty
    private Label firstLabelFound(Set<Label> labels) throws SetIsEmptyAndShouldNotBeException {
        for (Label label : labels) {
            return label;
        }
        throw new SetIsEmptyAndShouldNotBeException();
    }

    // EFFECTS: returns Label with given name or null if not found
    // throws NoSuchLabelFoundException if there are no Labels named labelName
    private Label getLabel(String name) throws NoSuchLabelFoundException {
        for (Label label : labels) {
            if (label.isNamed(name)) {
                return label;
            }
        }
        throw new NoSuchLabelFoundException();
    }
}
