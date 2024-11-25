package persistence;

import model.FileSystem;
import model.exceptions.NameIsTakenException;
import model.exceptions.NoSuchFileFoundException;
import model.exceptions.NoSuchFolderFoundException;
import model.exceptions.NoSuchLabelFoundException;
import persistence.exceptions.InvalidJsonRuntimeException;

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
    public FileSystem read() throws IOException {
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
    private FileSystem parseFileSystem(JSONObject json) {
        FileSystem fileSystem = new FileSystem();
        addLabels(fileSystem, json);
        addFoldersAndFiles(fileSystem, json);
        return fileSystem;
    }

    // MODIFIES: fileSystem
    // EFFECTS: parses labels from jsonObject and creates them in fileSystem
    private void addLabels(FileSystem fileSystem, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("labels");
        for (Object json : jsonArray) {
            JSONObject nextLabel = (JSONObject) json;
            addLabel(fileSystem, nextLabel);
        }
    }

    // MODIFIES: fileSystem
    // EFFECTS: gets the name of the label from jsonObject and creates fileSystem
    private void addLabel(FileSystem fileSystem, JSONObject jsonObject) {
        String name = jsonObject.getString("name");
        try {
            fileSystem.createLabel(name);
        } catch (NameIsTakenException e) {
            throw new InvalidJsonRuntimeException();
        }
    }

    // MODIFIES: fileSystem
    // EFFECTS: parses all Folders and Files from jsonObject and creates them in fileSystem
    private void addFoldersAndFiles(FileSystem fileSystem, JSONObject jsonObject) {
        JSONObject rootFolder = jsonObject.getJSONObject("rootFolder");
        addSubfiles(fileSystem, rootFolder);
        addSubfolders(fileSystem, rootFolder);
    }

    // MODIFIES: fileSystem
    // EFFECTS: parses files from jsonObject and creates them in fileSystem
    private void addSubfiles(FileSystem fileSystem, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("subfiles");
        for (Object json : jsonArray) {
            JSONObject nextFile = (JSONObject) json;
            addSubfile(fileSystem, nextFile);
        }
    }

    // MODIFIES: fileSystem
    // EFFECTS: gets the name and file path of the file from jsonObject and adds it to the fileSystem
    private void addSubfile(FileSystem fileSystem, JSONObject jsonObject) {
        String name = jsonObject.getString("name");
        String filePath = jsonObject.getString("filePath");
        try {
            fileSystem.createFile(name, filePath);
        } catch (NameIsTakenException e) {
            throw new InvalidJsonRuntimeException();
        }
        addLabelsToFile(fileSystem, jsonObject);
    }

    // MODIFIES: fileSystem
    // EFFECTS: parses labels from jsonObject and adds them to the file represented by jsonObject
    private void addLabelsToFile(FileSystem fileSystem, JSONObject jsonObject) {
        String fileName = jsonObject.getString("name");
        JSONArray jsonArray = jsonObject.getJSONArray("labels");
        for (Object json : jsonArray) {
            JSONObject nextLabel = (JSONObject) json;
            addLabelToFile(fileSystem, nextLabel, fileName);
        }
    }

    // MODIFIES: fileSystem
    // EFFECTS: gets the name of the label from jsonObject and adds it to file named fileName
    private void addLabelToFile(FileSystem fileSystem, JSONObject jsonObject, String fileName) {
        String labelName = jsonObject.getString("name");
        try {
            fileSystem.labelFile(fileName, labelName);
        } catch (NoSuchFileFoundException | NoSuchLabelFoundException e) {
            throw new InvalidJsonRuntimeException();
        }
    }

    // MODIFIES: fileSystem
    // EFFECTS: parses folders from jsonObject and creates them in fileSystem
    private void addSubfolders(FileSystem fileSystem, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("subfolders");
        for (Object json : jsonArray) {
            JSONObject nextFolder = (JSONObject) json;
            addSubfolder(fileSystem, nextFolder);
        }
    }

    // MODIFIES: fileSystem
    // EFFECTS: gets the name of the folder from jsonObject and adds it to the fileSystem and then adds the folder's
    // subfiles and subfolders (recursively for the subfolders)
    private void addSubfolder(FileSystem fileSystem, JSONObject jsonObject) {
        String name = jsonObject.getString("name");
        try {
            fileSystem.createFolder(name);
            fileSystem.openFolder(name);
            addSubfiles(fileSystem, jsonObject);
            addSubfolders(fileSystem, jsonObject);
            fileSystem.goUpOneDirectoryLevel();
        } catch (NameIsTakenException | NoSuchFolderFoundException e) {
            throw new InvalidJsonRuntimeException();
        }
    }
}
