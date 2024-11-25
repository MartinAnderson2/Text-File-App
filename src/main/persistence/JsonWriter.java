package persistence;

import model.FileSystem;

import java.io.PrintWriter;

import org.json.JSONObject;

import java.io.FileNotFoundException;

// Represents a writer for writing all of the labels, files, and folders of the file system to a file, in JSON format
// Based on [JsonSerializationDemo](https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo) 
public class JsonWriter {
    private static final int INDENT_FACTOR = 4;
    private PrintWriter printWriter;
    private String destinationPath;

    // EFFECTS: constructs a new JsonWriter for writing a file to destinationPath
    public JsonWriter(String destinationPath) {
        this.destinationPath = destinationPath;
    }

    // MODIFIES: this
    // EFFECTS: opens the writer such that writing to file can begin
    public void open() throws FileNotFoundException {
        printWriter = new PrintWriter(new java.io.File(destinationPath));
    }

    // MODIFIES: this
    // EFFECTS: writes fileSystem to file as a JSON representation
    public void write(FileSystem fileSystem) {
        JSONObject json = fileSystem.toJson();
        String printableJson = json.toString(INDENT_FACTOR);
        printWriter.print(printableJson);
    }

    // MODIFIES: this
    // EFFECTS: closes the writer for when writing to file is over
    public void close() {
        printWriter.close();
    }
}
