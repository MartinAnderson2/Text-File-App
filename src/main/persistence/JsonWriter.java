package persistence;

import model.FileSystem;

import java.io.FileNotFoundException;

// Represents a writer for writing all of the labels, files, and folders of the file system to a file, in JSON format
// Based on [JsonSerializationDemo](https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo) 
public class JsonWriter {

    // EFFECTS: constructs a new JsonWriter for writing a file to destinationPath
    public JsonWriter(String destinationPath) {

    }

    // MODIFIES: this
    // EFFECTS: opens the writer such that writing to file can begin
    public void open() throws FileNotFoundException {
        
    }

    // MODIFIES: this
    // EFFECTS: writes fileSystem to file as a JSON representation
    public void write(FileSystem fileSystem) {

    }

    // MODIFIES: this
    // EFFECTS: closes the writer for when writing to file is over
    public void close() {

    }
}
