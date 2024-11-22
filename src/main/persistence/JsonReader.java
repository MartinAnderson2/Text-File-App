package persistence;

import model.FileSystem;

// Represents a reader for reading all of the labels, files, and folders of the file system from a file, in JSON format
// Based on [JsonSerializationDemo](https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo) 
public class JsonReader {

    // EFFECTS: constructs a new JsonReader for reading file located at filePath
    public JsonReader(String filePath) {
        
    }

    // EFFECTS: reads file at filePath and parses the JSON representation into a FileSystem
    public FileSystem read() {
        return new FileSystem(); // stub
    }
}
