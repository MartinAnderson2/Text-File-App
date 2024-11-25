package persistence;

import model.FileSystem;
import model.Folder;
import model.exceptions.NameIsTakenException;
import model.exceptions.NoSuchFileFoundException;
import model.exceptions.NoSuchFolderFoundException;
import model.exceptions.NoSuchLabelFoundException;
import persistence.exceptions.InvalidJsonException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

// Represents a reader for reading all of the labels, files, and folders of the file system from a file, in JSON format
// Based on [JsonSerializationDemo](https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo) 
public class JsonReader {
    private String filePath;

    // EFFECTS: constructs a new JsonReader for reading file located at filePath
    public JsonReader(String filePath) {
        this.filePath = filePath;
    }

    // REQUIRES: filePath of this JsonReader must be a valid file path (it doesn't necessarily have to lead to a file,
    // but it cannot contain illegal characters)
    // EFFECTS: reads file at filePath and parses the JSON representation into a FileSystem
    // throws IOException if there is a problem with the file
    // throws InvalidJsonException if there was a problem making the file system the JSON represents
    public FileSystem read() throws IOException, InvalidJsonException {
        String printedJson = readFile(filePath);
        JSONObject json = new JSONObject(printedJson);
        return parseFileSystem(json);
    }

    // EFFECTS: reads file at filePath and returns it
    private String readFile(String filePath) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
            stream.forEach(s -> stringBuilder.append(s));
        }

        return stringBuilder.toString();
    }

    // EFFECTS: parses json to create file system and returns loaded file system
    // throws InvalidJsonException if there was a problem making the file system the JSON represents
    private FileSystem parseFileSystem(JSONObject json) throws InvalidJsonException {
        FileSystem fileSystem = new FileSystem();
        fileSystem.stopKeepingTrackOfRecents();

        addLabels(fileSystem, json);

        addFoldersAndFiles(fileSystem, json);

        openRecentlyOpenedFiles(fileSystem, json);
        openRecentlyOpenedFolders(fileSystem, json);
        openRecentlyOpenedLabels(fileSystem, json);
        
        openCurrentFolder(fileSystem, json);
        
        fileSystem.startKeepingTrackOfRecents();
        return fileSystem;
    }


    /* 
     *  Labels:
     */

    // MODIFIES: fileSystem
    // EFFECTS: parses labels from jsonObject and creates them in fileSystem
    // throws InvalidJsonException if there is a duplicate label name in the JSON
    private void addLabels(FileSystem fileSystem, JSONObject jsonObject) throws InvalidJsonException {
        JSONArray jsonArray = jsonObject.getJSONArray("labels");
        for (Object json : jsonArray) {
            JSONObject nextLabel = (JSONObject) json;
            addLabel(fileSystem, nextLabel);
        }
    }

    // MODIFIES: fileSystem
    // EFFECTS: gets the name of the label from jsonObject and creates fileSystem
    // throws InvalidJsonException if there is a duplicate label name in the JSON
    private void addLabel(FileSystem fileSystem, JSONObject jsonObject) throws InvalidJsonException {
        String name = jsonObject.getString("name");
        try {
            fileSystem.createLabel(name);
        } catch (NameIsTakenException e) {
            throw new InvalidJsonException();
        }
    }


    /* 
     *  Folders and Files:
     */

    // MODIFIES: fileSystem
    // EFFECTS: parses all Folders and Files from jsonObject and creates them in fileSystem
    // throws InvalidJsonException if there is a duplicate folder or file name in the JSON
    private void addFoldersAndFiles(FileSystem fileSystem, JSONObject jsonObject) throws InvalidJsonException {
        JSONObject rootFolder = jsonObject.getJSONObject("rootFolder");
        addSubfiles(fileSystem, rootFolder);
        addSubfolders(fileSystem, rootFolder);
    }

    // MODIFIES: fileSystem
    // EFFECTS: parses files from jsonObject and creates them in fileSystem
    // throws InvalidJsonException if there is a duplicate folder or file name in the JSON
    private void addSubfiles(FileSystem fileSystem, JSONObject jsonObject) throws InvalidJsonException {
        JSONArray jsonArray = jsonObject.getJSONArray("subfiles");
        for (Object json : jsonArray) {
            JSONObject nextFile = (JSONObject) json;
            addSubfile(fileSystem, nextFile);
        }
    }

    // MODIFIES: fileSystem
    // EFFECTS: gets the name and file path of the file from jsonObject and adds it to the fileSystem
    // throws InvalidJsonException if there is a duplicate name in the JSON
    private void addSubfile(FileSystem fileSystem, JSONObject jsonObject) throws InvalidJsonException {
        String name = jsonObject.getString("name");
        String filePath = jsonObject.getString("filePath");
        try {
            fileSystem.createFile(name, filePath);
        } catch (NameIsTakenException e) {
            throw new InvalidJsonException();
        }
        addLabelsToFile(fileSystem, jsonObject);
    }

    // MODIFIES: fileSystem
    // EFFECTS: parses labels from jsonObject and adds them to the file represented by jsonObject
    // throws InvalidJsonException if the label on the file named fileName doesn't exist
    private void addLabelsToFile(FileSystem fileSystem, JSONObject jsonObject) throws InvalidJsonException {
        String fileName = jsonObject.getString("name");
        JSONArray jsonArray = jsonObject.getJSONArray("labels");
        for (Object json : jsonArray) {
            JSONObject nextLabel = (JSONObject) json;
            addLabelToFile(fileSystem, nextLabel, fileName);
        }
    }

    // MODIFIES: fileSystem
    // EFFECTS: gets the name of the label from jsonObject and adds it to file named fileName
    // throws InvalidJsonException if the label on the file named fileName doesn't exist
    private void addLabelToFile(FileSystem fileSystem, JSONObject jsonObject, String fileName)
            throws InvalidJsonException {
        String labelName = jsonObject.getString("name");
        try {
            fileSystem.labelFile(fileName, labelName);
        } catch (NoSuchFileFoundException | NoSuchLabelFoundException e) {
            throw new InvalidJsonException();
        }
    }

    // MODIFIES: fileSystem
    // EFFECTS: parses folders from jsonObject and creates them in fileSystem
    // throws InvalidJsonException if there is a duplicate file or folder name in the JSON
    private void addSubfolders(FileSystem fileSystem, JSONObject jsonObject) throws InvalidJsonException {
        JSONArray jsonArray = jsonObject.getJSONArray("subfolders");
        for (Object json : jsonArray) {
            JSONObject nextFolder = (JSONObject) json;
            addSubfolder(fileSystem, nextFolder);
        }
    }

    // MODIFIES: fileSystem
    // EFFECTS: gets the name of the folder from jsonObject and adds it to the fileSystem and then adds the folder's
    // subfiles and subfolders (recursively for the subfolders)
    // throws InvalidJsonException if there is a duplicate file or folder name in the JSON
    private void addSubfolder(FileSystem fileSystem, JSONObject jsonObject) throws InvalidJsonException {
        String name = jsonObject.getString("name");
        try {
            fileSystem.createFolder(name);
            fileSystem.openFolder(name);
            addSubfiles(fileSystem, jsonObject);
            addSubfolders(fileSystem, jsonObject);
            fileSystem.goUpOneDirectoryLevel();
        } catch (NameIsTakenException | NoSuchFolderFoundException e) {
            throw new InvalidJsonException();
        }
    }


    /* 
     *  Recently-opened Files:
     */

    // MODIFIES: fileSystem
    // EFFECTS: opens the files that were opened recently (and ensures their opening is tracked)
    // throws InvalidJsonException if any of the file paths are not accurate
    private void openRecentlyOpenedFiles(FileSystem fileSystem, JSONObject jsonObject) throws InvalidJsonException {
        JSONArray jsonArray = jsonObject.getJSONArray("recentlyOpenedFilePaths");
        for (int i = jsonArray.length() - 1; i >= 0; i--) {
            String nextFilePath = jsonArray.getString(i);
            try {
                openFile(fileSystem, nextFilePath);
            } catch (NoSuchFolderFoundException | NoSuchFileFoundException e) {
                throw new InvalidJsonException();
            }
        }
    }

    // MODIFIES: fileSystem
    // EFFECTS: opens a file given its file path in fileSystem
    // throws NoSuchFolderFoundException if any of the folders in filePath do not exist
    // throws NoSuchFileFoundException if the file in filePath does not exist
    // throws FilePathNoLongerValidException if the file in filePath does not exist
    private void openFile(FileSystem fileSystem, String filePath) throws NoSuchFolderFoundException,
            NoSuchFileFoundException {
        fileSystem.openRootFolder();
        openFoldersFromPath(fileSystem, getFoldersFromFileFilePath(filePath));
        openFileAndTrack(fileSystem, getFileNameFromFilePath(filePath));
    }

    // MODIFIES: fileSystem
    // EFFECTS: opens file with name fileName
    // throws NoSuchFileFoundException if there is not file named fileName if fileSystem's current folder
    private void openFileAndTrack(FileSystem fileSystem, String fileName) throws NoSuchFileFoundException {
        fileSystem.startKeepingTrackOfRecents();
        fileSystem.openFileButNotOnComputerEvenIfNoLongerValid(fileName);
        fileSystem.stopKeepingTrackOfRecents();
    }


    /* 
     *  Recently-opened Folders:
     */

    // MODIFIES: fileSystem
    // EFFECTS: opens the folders that were opened recently (and ensures their opening is tracked)
    // throws InvalidJsonException if any of the folder paths are not accurate
    private void openRecentlyOpenedFolders(FileSystem fileSystem, JSONObject jsonObject) throws InvalidJsonException {
        JSONArray jsonArray = jsonObject.getJSONArray("recentlyOpenedFolderPaths");
        for (int i = jsonArray.length() - 1; i >= 0; i--) {
            String nextFolderPath = jsonArray.getString(i);
            try {
                openFolder(fileSystem, nextFolderPath);
            } catch (NoSuchFolderFoundException e) {
                throw new InvalidJsonException();
            }
        }
    }

    // MODIFIES: fileSystem
    // EFFECTS: opens a folder given its folder path in fileSystem
    // throws NoSuchFolderFoundException if any of the folders in filePath do not exist
    private void openFolder(FileSystem fileSystem, String folderPath) throws NoSuchFolderFoundException {
            fileSystem.openRootFolder();
            String filePathWithoutRoot = getFoldersFromFileFilePathMinusFirst(folderPath);
            String filePathWithoutRootOrLast = getFoldersFromFileFilePathMinusLast(filePathWithoutRoot);
            openFoldersFromPath(fileSystem, filePathWithoutRootOrLast);
            openFolderAndTrack(fileSystem, getNameOfLastFolderFromFolderPath(folderPath));
    }

    // MODIFIES: fileSystem
    // EFFECTS: opens folder with name folderName
    // throws NoSuchFileFoundException if there is not file named fileName if fileSystem's current folder
    private void openFolderAndTrack(FileSystem fileSystem, String folderName) throws NoSuchFolderFoundException {
        fileSystem.startKeepingTrackOfRecents();
        fileSystem.openFolder(folderName);
        fileSystem.stopKeepingTrackOfRecents();
    }


    /* 
     *  Recently-opened labels:
     */

    // MODIFIES: fileSystem
    // EFFECTS: opens the labels that were opened recently
    // throws InvalidJsonException if any of the labels do not exist
    private void openRecentlyOpenedLabels(FileSystem fileSystem, JSONObject jsonObject) throws InvalidJsonException {
        JSONArray jsonArray = jsonObject.getJSONArray("recentlyOpenedLabels");
        for (int i = jsonArray.length() - 1; i >= 0; i--) {
            JSONObject nextLabel = (JSONObject) jsonArray.get(i);
            try {
                openLabel(fileSystem, nextLabel);
            } catch (NoSuchLabelFoundException e) {
                throw new InvalidJsonException();
            }
        }
        fileSystem.openRootFolder();
    }

    // MODIFIES: fileSystem
    // EFFECTS: opens a label given a JSONObject representation of it (of its name)
    // throws NoSuchLabelFoundException if there is no label with the same name as label
    private void openLabel(FileSystem fileSystem, JSONObject label) throws NoSuchLabelFoundException {
        String labelName = label.getString("name");
        openLabelAndTrack(fileSystem, labelName);
    }

    // MODIFIES: fileSystem
    // EFFECTS: opens label with name labelName and ensures it is tracked as opened
    // throws NoSuchLabelFoundException if there is no label with the same name as label
    private void openLabelAndTrack(FileSystem fileSystem, String labelName) throws NoSuchLabelFoundException {
        fileSystem.startKeepingTrackOfRecents();
        fileSystem.openLabel(labelName);
        fileSystem.stopKeepingTrackOfRecents();
    }

    
    
    /* 
     *  Current Folder:
     */
    // MODIFIES: fileSystem
    // EFFECTS: opens the folder that the user had open when they saved the file system
    // throws InvalidJsonException if the folder path saved for currentFolderPath was invalid
    private void openCurrentFolder(FileSystem fileSystem, JSONObject jsonObject) throws InvalidJsonException {
        String folderPath = jsonObject.getString("currentFolderPath");
        fileSystem.openRootFolder();
        try {
            openFoldersFromPath(fileSystem, getFoldersFromFileFilePathMinusFirst(folderPath));
        } catch (NoSuchFolderFoundException e) {
            throw new InvalidJsonException();
        }
    }


    /* 
     *  Common Helpers:
     */
    // MODIFIES: fileSystem
    // EFFECTS: opens all of the folders in folderPath
    // throws NoSuchFolderFoundException if any of the folders in folderPath do not exist
    private void openFoldersFromPath(FileSystem fileSystem, String folderPath) throws NoSuchFolderFoundException {
        if (!folderPath.isEmpty()) {
            fileSystem.openFolder(getFirstFolderFromFilePath(folderPath));

            openFoldersFromPath(fileSystem, getFoldersFromFileFilePathMinusFirst(folderPath));
        }
    }


    /* 
     *  File/Folder path methods:
     */

    // EFFECTS: returns filePath without the file at the end
    // returns the empty string if there are no folders
    private String getFoldersFromFileFilePath(String filePath) {
        for (int i = filePath.length() - 1; i >= 0; i--) {
            if (filePath.charAt(i) == Folder.FOLDER_SEPERATOR) {
                return (filePath.substring(0, i));
            }
        }
        return "";
    }

    // EFFECTS: returns the name of the file that filePath leads to
    // returns filePath if filePath already has no folders in it
    private String getFileNameFromFilePath(String filePath) {
        for (int i = filePath.length() - 1; i >= 0; i--) {
            if (filePath.charAt(i) == Folder.FOLDER_SEPERATOR) {
                return (filePath.substring(i + 1));
            }
        }
        return filePath;
    }

    // EFFECTS: returns the name of the first folder in folderPath
    // returns folderPath if there are no folders in folderPath
    private String getFirstFolderFromFilePath(String folderPath) {
        for (int i = 0; i < folderPath.length(); i++) {
            if (folderPath.charAt(i) == Folder.FOLDER_SEPERATOR) {
                return (folderPath.substring(0, i));
            }
        }
        return folderPath;
    }

    // EFFECTS: returns the name(s) of the folder(s) after the first one as a path
    // returns the empty string if there are no folders in folderPath
    private String getFoldersFromFileFilePathMinusFirst(String folderPath) {
        for (int i = 0; i < folderPath.length(); i++) {
            if (folderPath.charAt(i) == Folder.FOLDER_SEPERATOR) {
                return (folderPath.substring(i + 1));
            }
        }
        return "";
    }

    // EFFECTS: returns the name(s) of the folder(s) after the first one as a path
    // returns the empty string if there are no folders in folderPath
    private String getFoldersFromFileFilePathMinusLast(String folderPath) {
        for (int i = folderPath.length() - 1 - 1; i >= 0; i--) {
            if (folderPath.charAt(i) == Folder.FOLDER_SEPERATOR) {
                return (folderPath.substring(0, i + 1));
            }
        }
        return "";
    }

    // EFFECTS: returns the name of the last folder in folderPath
    // returns folderPath if there is only one folder
    private String getNameOfLastFolderFromFolderPath(String folderPath) {
        for (int i = folderPath.length() - 1 - 1; i >= 0; i--) {
            if (folderPath.charAt(i) == Folder.FOLDER_SEPERATOR) {
                return (folderPath.substring(i + 1, folderPath.length() - 1));
            }
        }
        return folderPath;
    }
}
