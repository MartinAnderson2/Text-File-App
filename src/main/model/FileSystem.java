package model;

import model.exceptions.*;
import persistence.JsonReader;
import persistence.JsonWriter;
import persistence.Writable;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.awt.Desktop;

// Represents a file system with Folders and Files as well as Labels that can be applied to Files.
// Folders can be navigated and it is possible to get all of the Folders and all of the Files in the current Folder.
// Files, Folders, and Labels can be created and deleted. Files can be labelled and unlabelled. Files can be opened in
// the user's default text editor. Folders can be opened to access their contents. It is possible to list all Files
// labelled with a given Label.
public class FileSystem implements Writable {
    public static final String AUTOSAVE_FILE_PATH = "data\\Autosave.json";
    public static final String EXAMPLE_FILE_PATH = "C:\\Users\\User\\Documents\\Note Name.txt";
    private static final int MAX_NUM_RECENTLY_OPENED_STORED = 10;

    private static JsonReader autoLoadJsonReader = new JsonReader(AUTOSAVE_FILE_PATH);
    private JsonWriter autoSaveJsonWriter;

    private Folder rootFolder;
    private Folder currentFolder;

    private Set<Label> labels;

    private List<File> recentlyOpenedFiles;
    private List<Folder> recentlyOpenedFolders;
    private List<Label> recentlyOpenedLabels;
    private boolean keepTrackOfRecents;

    // EFFECTS: initializes the variables needed for the file system:
    // rootFolder: for the Folder that contains the initial Folders and Files, and indirectly contains every Folder and
    //             File since every Folder or File is a subfolder or subfile of root or one of root's subfolders (or a
    //             subfolder's subfolder, and so on...)
    // currentFolder: initialized to the root Folder such that Folders can be created and Files can be added
    // labels: stores all of the Labels the user creates
    // recentlyOpenedFile: stores the names of the MAX_RECENTLY_OPENED_STORED most recently-opened Files
    // recentlyOpenedFolder: stores the MAX_RECENTLY_OPENED_STORED most recently-opened Folders
    // recentlyOpenedLabel: stores the MAX_RECENTLY_OPENED_STORED most recently-opened Label
    public FileSystem() {
        autoSaveJsonWriter = new JsonWriter(AUTOSAVE_FILE_PATH);

        rootFolder = new Folder("root");
        currentFolder = rootFolder;

        labels = new HashSet<Label>();
        recentlyOpenedFiles = new LinkedList<File>();
        recentlyOpenedFolders = new LinkedList<Folder>();
        recentlyOpenedLabels = new LinkedList<Label>();
        keepTrackOfRecents = true;
    }

    // EFFECTS: returns currentFolder's name
    public String getCurrentFolderName() {
        return currentFolder.getName();
    }

    // EFFECTS: returns currentFolder's parent's name
    // throws NoSuchFolderFoundException if currentFolder does not have a parent
    public String getParentFolderName() throws NoSuchFolderFoundException {
        return currentFolder.getParentFolder().getName();
    }

    // MODIFIES: this
    // EFFECTS: stops (or continues not) keeping track of recently-opened files, folders, or labels
    public void stopKeepingTrackOfRecents() {
        keepTrackOfRecents = false;
    }

    // MODIFIES: this
    // EFFECTS: starts (or keeps) keeping track of recently-opened files, folders, or labels
    public void startKeepingTrackOfRecents() {
        keepTrackOfRecents = true;
    }


    /* 
     *  File, Folder, and Label Methods:
     */

    // File:

    // REQUIRES: name.isBlank() is false
    // MODIFIES: this
    // EFFECTS: creates a new File in currentFolder with given name and path
    // throws NameIsTakenException if currentFolder already contains a Folder named name
    // throws NameIsBlankException if name.isBlank() is true
    public void createFile(String name, String path) throws NameIsTakenException {
        currentFolder.makeSubfile(name, path);
    }

    // EFFECTS: does not open File named fileName in user's default text editor. Adds File named fileName to list of
    // recently-opened Files
    // throws FilePathNoLongerValidException if the File no longer exists on their computer
    // throws NoSuchFileFoundException if there are no Files named fileName in currentFolder
    public void openFileButNotOnComputer(String fileName) throws NoSuchFileFoundException,
            FilePathNoLongerValidException {
        openFile(currentFolder.getSubfile(fileName), false);
    }

    // EFFECTS: opens File named fileName in user's default text editor. Adds File named fileName to list of
    // recently-opened Files
    // throws FilePathNoLongerValidException if the File no longer exists on their computer
    // throws NoSuchFileFoundException if there are no Files named fileName in currentFolder
    public void openFile(String fileName) throws NoSuchFileFoundException, FilePathNoLongerValidException {
        openFile(currentFolder.getSubfile(fileName), true);
    }

    // EFFECTS: opens file in user's default text editor. Adds File named fileName to list of recently-opened Files
    // throws FilePathNoLongerValidException if the File no longer exists on their computer
    // throws NoSuchFileFoundException if there are no Files named fileName in currentFolder
    private void openFile(File file, boolean openOnComputer) throws NoSuchFileFoundException,
            FilePathNoLongerValidException {
        if (!FileSystem.isFilePathValid(file.getFilePath())) {
            throw new FilePathNoLongerValidException();
        }

        try {
            if (openOnComputer) {
                Desktop.getDesktop().open(new java.io.File(file.getFilePath()));
            }
            addRecentlyOpenedFile(file);
        } catch (IOException e) {
            throw new FilePathNoLongerValidException();
        }
    }

    // MODIFIES: this
    // EFFECTS: deletes File named fileName
    // throws NoSuchFileFoundException if there are no Files named fileName in currentFolder
    public void deleteFile(String fileName) throws NoSuchFileFoundException {
        File file = currentFolder.getSubfile(fileName);
        recentlyOpenedFiles.remove(file);
        removeAllLabels(fileName);

        if (file.getParentFolder() != currentFolder) {
            file.getParentFolder().removeSubfile(fileName);
        }
        currentFolder.removeSubfile(fileName);
    }

    // REQUIRES: newName.isBlank() is false
    // EFFECTS: sets the name of File named fileName to newName
    // throws NameIsTakenException if currentFolder already contains a Folder named name
    // throws NoSuchFileFoundException if there are no Files named fileName in currentFolder
    // throws NameIsBlankException if newName.isBlank() is true
    public void setFileName(String fileName, String newName) throws NoSuchFileFoundException, NameIsTakenException {
        if (containsFile(newName)) {
            throw new NameIsTakenException(getCapitalizationOfFile(newName));
        }
        currentFolder.getSubfile(fileName).setName(newName);
    }

    // EFFECTS: returns the file path of File named fileName
    // throws NoSuchFileFoundException if there are no Files named fileName in currentFolder
    public String getFilePath(String fileName) throws NoSuchFileFoundException {
        return currentFolder.getSubfile(fileName).getFilePath();
    }

    // EFFECTS: returns true if currentFolder contains a File named fileName otherwise returns false
    public boolean containsFile(String fileName) {
        try {
            currentFolder.getSubfile(fileName);
            return true;
        } catch (NoSuchFileFoundException e) {
            return false;
        }
    }

    // REQUIRES: containsFile(fileNameWrongCase) is true
    // EFFECTS: returns the actual capitalization of the name of the File named fileNameWrongCase
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
    public List<String> getNamesOfSubfiles() {
        List<String> namesOfSubfiles = new LinkedList<String>();
        for (File file : currentFolder.getSubfiles()) {
            namesOfSubfiles.add(file.getName());
        }
        return namesOfSubfiles;
    }

    // EFFECTS: returns a list of the names of the up to MAX_NUM_RECENTLY_OPENED_STORED last files opened
    public List<String> getNamesOfRecentlyOpenedFiles() {
        List<String> namesOfRecentlyOpenedFiles = new ArrayList<String>();
        for (File file : recentlyOpenedFiles) {
            namesOfRecentlyOpenedFiles.add(file.getName());
        }
        return namesOfRecentlyOpenedFiles;
    }

    // EFFECTS: opens File named fileName in user's default text editor if it is in recentlyOpenedFiles
    // throws NoSuchFileFoundException if there is no File named fileName in recentlyOpenedFiles
    // throws FilePathNoLongerValidException if the File named fileName failed to open (due to the path no longer being
    // valid)
    public void openRecentlyOpenedFile(String fileName)
            throws NoSuchFileFoundException, FilePathNoLongerValidException {
        for (File file : recentlyOpenedFiles) {
            if (file.isNamed(fileName)) {
                openFile(file, true);
                return;
            }
        }
        throw new NoSuchFileFoundException();
    }

    // Folder:

    // REQUIRES: folderName.isBlank() is false
    // MODIFIES: currentFolder
    // EFFECTS: creates a new subfolder in the current directory with name folderName
    // throws NameIsTakenException if containsFolder(folderName) is true
    // throws NameIsBlankException if folderName.isBlank() is true
    public void createFolder(String folderName) throws NameIsTakenException {
        currentFolder.makeSubfolder(folderName);
    }

    // MODIFIES: this
    // EFFECTS: opens Folder named folderName, i.e. makes that Folder the current directory. Adds Folder named
    // folderName to list of recently-opened Folders
    // throws NoSuchFolderFoundException if there are no Folders named folderName in currentFolder
    public void openFolder(String folderName) throws NoSuchFolderFoundException {
        openFolder(currentFolder.getSubfolder(folderName));
    }
    
    // MODIFIES: this
    // EFFECTS: opens folderToOpen, i.e. makes that Folder the current directory. Adds folderToOpen to list of
    // recently-opened Folders
    private void openFolder(Folder folderToOpen) {
        currentFolder = folderToOpen;
        addRecentlyOpenedFolder(folderToOpen);
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
        recentlyOpenedFolders.remove(currentFolder.getSubfolder(folderName));
        currentFolder.removeSubfolder(folderName);
    }

    // REQUIRES: newName.isBlank() is false
    // EFFECTS: sets the name of Folder named folderName to newName
    // throws NameIsTakenException if currentFolder already contains a Folder named name
    // throws NoSuchFolderFoundException if there are no Folders named folderName in currentFolder
    // throws NameIsBlankException if ewName.isBlank() is true
    public void setFolderName(String folderName, String newName)
            throws NoSuchFolderFoundException, NameIsTakenException {
        if (containsFolder(newName)) {
            throw new NameIsTakenException(getCapitalizationOfFolder(newName));
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
    public boolean containsFolder(String folderName) {
        return currentFolder.hasSubfolder(folderName);
    }

    // EFFECTS: returns a list of the names of the Folders that are direct subfolders of currentFolder
    public List<String> getNamesOfSubfolders() {
        List<String> namesOfSubfolders = new LinkedList<String>();
        for (Folder folder : currentFolder.getSubfolders()) {
            namesOfSubfolders.add(folder.getName());
        }
        return namesOfSubfolders;
    }

    // REQUIRES: containsFolder(folderNameWrongCase) is true
    // EFFECTS: returns the actual capitalization of the name of the Folder named folderNameWrongCase
    public String getCapitalizationOfFolder(String folderNameWrongCase) {
        try {
            return currentFolder.getSubfolder(folderNameWrongCase).getName();
        } catch (NoSuchFolderFoundException e) {
            throw new RequiresClauseNotMetRuntimeException();
        }
    }

    // EFFECTS: returns a list of the names of the up to MAX_NUM_RECENTLY_OPENED_STORED last folders opened
    public List<String> getNamesOfRecentlyOpenedFolders() {
        List<String> namesOfRecentlyOpenedFolders = new ArrayList<String>();
        for (Folder folder : recentlyOpenedFolders) {
            namesOfRecentlyOpenedFolders.add(folder.getName());
        }
        return namesOfRecentlyOpenedFolders;
    }

    // EFFECTS: opens Folder named folderName (sets currentFolder to it) if it is in recentlyOpenedFolders
    // throws NoSuchFolderFoundException if there is no Folder named folderName in recentlyOpenedFolders
    public void openRecentlyOpenedFolder(String folderName) throws NoSuchFolderFoundException {
        for (Folder folder : recentlyOpenedFolders) {
            if (folder.isNamed(folderName)) {
                openFolder(folder);
                return;
            }
        }
        throw new NoSuchFolderFoundException();
    }

    // Label:

    // REQUIRES: labelName.isBlank() is false
    // MODIFIES: this
    // EFFECTS: creates a new Label named labelName (and no Files are labelled it)
    // throws NameIsTakenException if there is already a label named labelName
    // throws NameIsBlankException if labelName.isBlank() is true
    public void createLabel(String labelName) throws NameIsTakenException {
        if (labelExists(labelName)) {
            throw new NameIsTakenException(getCapitalizationOfLabel(labelName));
        }
        labels.add(new Label(labelName));
    }

    // MODIFIES: this
    // EFFECTS: creates a new Folder with every File labelled with Label named labelName and sets currentFolder to that
    // new Folder. This is not an actual Folder in the file system but rathaer a 'fake' one to view all Files labelled
    // with the given Label. This is a brand-new directory to which adding and removing Files and Folders is pointless,
    // though editing them (renaming, changing labels, deleting) isn't: those changes do affect the real file system
    // throws NoSuchLabelFoundException if there are no Labels named labelName
    public void openLabel(String labelName) throws NoSuchLabelFoundException {
        openLabel(getLabel(labelName));
    }

    // MODIFIES: this
    // EFFECTS: creates a new Folder with every File labelled label and sets currentFolder to that new Folder
    // this is not an actual Folder in the File system but rataher a fake one to view all Files labelled with the
    // given Label. This is a brand-new directory to which adding and removing Files and Folders is pointless
    private void openLabel(Label label) {
        Folder labelFolder = new Folder(label.getName());

        for (File file : label.getLabelledFiles()) {
            try {
                labelFolder.addExistingSubfile(file);
            } catch (NameIsTakenException e) {
                addDifferentiatorUntilSuccess(labelFolder, file);
            }
        }
        currentFolder = labelFolder;
        addRecentlyOpenedLabel(label);
    }

    // MODIFIES: this
    // EFFECTS: adds a differentiator of the form (1) to the end of file's name and then attempts to add it to
    // labelFolder. If there is already a File with that name, removes the differentiator and then loops, adding a
    // differentiator with a number 1 larger until it succeeds in adding it
    private void addDifferentiatorUntilSuccess(Folder labelFolder, File file) {
        for (int i = 1; true; i++) {
            String name = file.getName();
            String differentiator = " (" + String.valueOf(i) + ")";
            file.setName(name + differentiator);
            try {
                labelFolder.addExistingSubfile(file);
                break;
            } catch (NameIsTakenException e) {
                file.setName(name);
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: deletes Label with name labelName
    // throws NoSuchLabelFoundException if there are no Labels named labelName
    public void deleteLabel(String labelName) throws NoSuchLabelFoundException {
        Label label = getLabel(labelName);
        label.unlabelAllFiles();
        recentlyOpenedLabels.remove(label);
        labels.remove(label);
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

    // REQUIRES: newName.isBlank() is false
    // MODIFIES: this
    // EFFECTS: sets the name of Label named labelName to newName
    // throws NameIsTakenException if currentFolder already contains a Folder named name
    // throws NoSuchLabelFoundException if there are no Labels named labelName
    // throws NameIsBlankException if newName.isBlank() is true
    public void setLabelName(String labelName, String newName) throws NoSuchLabelFoundException, NameIsTakenException {
        if (labelExists(newName)) {
            throw new NameIsTakenException(getCapitalizationOfLabel(newName));
        }
        getLabel(labelName).setName(newName);
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

    // EFFECTS: returns the name of the only Label the user has made. returns null if they haven't made any
    public String getOnlyLabelName() {
        for (Label label : labels) {
            return label.getName();
        }
        return null;
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
    public List<String> getNamesOfLabels() {
        List<String> namesOfLabels = new LinkedList<String>();
        for (Label label : labels) {
            namesOfLabels.add(label.getName());
        }
        return namesOfLabels;
    }

    // EFFECTS: returns a list of the names of the Labels on File named fileName
    // throws NoSuchFileFoundException if there are no Files named fileName in currentFolder
    public List<String> getNamesOfLabelsOnFile(String fileName) throws NoSuchFileFoundException {
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
    public List<String> getNamesOfLabelsNotOnFile(String fileName) throws NoSuchFileFoundException {
        File file = currentFolder.getSubfile(fileName);
        List<String> namesOfLabelsNotOnFile = new LinkedList<String>();
        for (Label label : labels) {
            if (!file.isLabelled(label)) {
                namesOfLabelsNotOnFile.add(label.getName());
            }
        }
        return namesOfLabelsNotOnFile;
    }

    // EFFECTS: returns a list of the names of the up to MAX_NUM_RECENTLY_OPENED_STORED last labels 'opened'
    public List<String> getNamesOfRecentlyOpenedLabels() {
        List<String> namesOfRecentlyOpenedLabels = new ArrayList<String>();
        for (Label label : recentlyOpenedLabels) {
            namesOfRecentlyOpenedLabels.add(label.getName());
        }
        return namesOfRecentlyOpenedLabels;
    }

    // EFFECTS: 'opens' Label named labelName (opens a new Folder containing every File labelled with Label) if it is
    // in recentlyOpenedLabels
    // throws NoSuchLabelFoundException if there is no Label named labelName in recentlyOpenedLabels
    public void openRecentlyOpenedLabel(String labelName) throws NoSuchLabelFoundException {
        for (Label label : recentlyOpenedLabels) {
            if (label.isNamed(labelName)) {
                openLabel(label);
                return;
            }
        }
        throw new NoSuchLabelFoundException();
    }


    /*
     *  Persistence-related Methods:
     */

    // EFFECTS: returns a JSON representation of this file system
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("currentFolderPath", currentFolder.getPathInThisFileSystem());
        json.put("labels", labelsToJson());
        json.put("recentlyOpenedFilePaths", recentFilePathsToJson());
        json.put("recentlyOpenedFolderPaths", recentFolderPathsToJson());
        json.put("recentlyOpenedLabels", recentLabelsToJson());
        json.put("rootFolder", rootFolder.toJson());
        return json;
    }

    // EFFECTS: returns a JSON representation of all created labels
    private JSONArray labelsToJson() {
        JSONArray jsonArray = new JSONArray();

        for (Label label : labels) {
            jsonArray.put(label.toJson());
        }
        
        return jsonArray;
    }

    // EFFECTS: returns a JSON representation of the paths of the recently-opened files
    private JSONArray recentFilePathsToJson() {
        JSONArray jsonArray = new JSONArray();
        for (File file : recentlyOpenedFiles) {
            jsonArray.put(file.getPathInThisFileSystem());
        }
        return jsonArray;
    }

    // EFFECTS: returns a JSON representation of the paths of the recently-opened folders
    private JSONArray recentFolderPathsToJson() {
        JSONArray jsonArray = new JSONArray();
        for (Folder folder : recentlyOpenedFolders) {
            jsonArray.put(folder.getPathInThisFileSystem());
        }
        return jsonArray;
    }

    // EFFECTS: returns a JSON representation of the names of the recently-opened labels
    private JSONArray recentLabelsToJson() {
        JSONArray jsonArray = new JSONArray();
        for (Label label : recentlyOpenedLabels) {
            jsonArray.put(label.toJson());
        }
        return jsonArray;
    }

    // EFFECTS: saves a JSON representation of this file system to AUTOSAVE_FILE_PATH
    public void autoSave() throws FileNotFoundException {
        autoSaveJsonWriter.open();
        autoSaveJsonWriter.write(this);
        autoSaveJsonWriter.close();
    }

    // EFFECTS: saves a jSON representation of this file system to filePath
    public void manuallySave(String filePath) throws FileNotFoundException {
        JsonWriter jsonWriter = new JsonWriter(filePath);
        jsonWriter.open();
        jsonWriter.write(this);
        jsonWriter.close();
    }

    // EFFECTS: loads a JSON representation of a file system
    public static FileSystem autoLoad() throws IOException {
        FileSystem fileSystem = autoLoadJsonReader.read();
        return fileSystem;
    }

    // EFFECTS: loads a JSON representation of a file system from filePath
    public static FileSystem manuallyLoad(String filePath) throws IOException {
        JsonReader jsonReader = new JsonReader(filePath);
        FileSystem fileSystem = jsonReader.read();
        return fileSystem;
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

    // REQUIRES: recentlyOpenedFile.size() <= MAX_RECENTLY_OPENED_STORED
    // MODIFIES: this
    // EFFECTS: if recentlyOpenedFiles contains file, removes it from its current position and puts it at the start
    // Else If recentlyOpenedFiles contains fewer than MAX_RECENTLY_OPENED_STORED Strings, adds file.
    // Else (if recentlyOpenedFiles contains MAX_RECENTLY_OPENED_STORED Strings) removes the last element and adds
    // file to the front of the list.
    private void addRecentlyOpenedFile(File file) {
        if (!keepTrackOfRecents) {
            return;
        }

        if (recentlyOpenedFiles.contains(file)) {
            recentlyOpenedFiles.remove(file);
        } else {
            int size = recentlyOpenedFiles.size();
            int indexOfLast = size - 1;
    
            if (size == MAX_NUM_RECENTLY_OPENED_STORED) {
                recentlyOpenedFiles.remove(indexOfLast);
            }
        }
        recentlyOpenedFiles.add(0, file);
    }

    // REQUIRES: recentlyOpenedFolder.size() <= MAX_RECENTLY_OPENED_STORED
    // MODIFIES: this
    // EFFECTS: if recentlyOpenedFolders contains folder, removes it from its current position and puts it at the start
    // Else if recentlyOpenedFolders contains fewer than MAX_RECENTLY_OPENED_STORED Strings, adds folder.
    // Else (if recentlyOpenedFolders contains MAX_RECENTLY_OPENED_STORED Strings) removes the last element and adds
    // folder to the front of the list.
    private void addRecentlyOpenedFolder(Folder folder) {
        if (!keepTrackOfRecents) {
            return;
        }

        if (recentlyOpenedFolders.contains(folder)) {
            recentlyOpenedFolders.remove(folder);
        } else {
            int size = recentlyOpenedFolders.size();
            
            if (size == MAX_NUM_RECENTLY_OPENED_STORED) {
                int indexOfLast = size - 1;
                recentlyOpenedFolders.remove(indexOfLast);
            }
        }
        recentlyOpenedFolders.add(0, folder);
    }

    // REQUIRES: recentlyOpenedLabel.size() <= MAX_RECENTLY_OPENED_STORED
    // MODIFIES: this
    // EFFECTS: if recentlyOpenedLabels contains label, removes it from its current position and puts it at the start
    // Else if recentlyOpenedLabels contains fewer than MAX_RECENTLY_OPENED_STORED Strings, adds label.
    // Else (if recentlyOpenedFolders contains MAX_RECENTLY_OPENED_STORED Strings) removes the last element and adds
    // label to the front of the list.
    private void addRecentlyOpenedLabel(Label label) {
        if (!keepTrackOfRecents) {
            return;
        }
        
        if (recentlyOpenedLabels.contains(label)) {
            recentlyOpenedLabels.remove(label);
        } else {
            int size = recentlyOpenedLabels.size();
            int indexOfLast = size - 1;

            if (size == MAX_NUM_RECENTLY_OPENED_STORED) {
                recentlyOpenedLabels.remove(indexOfLast);
            }
        }
        recentlyOpenedLabels.add(0, label);
    }
}
