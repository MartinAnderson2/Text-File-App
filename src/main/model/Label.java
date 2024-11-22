package model;

import persistence.Writable;

import java.util.Set;

import org.json.JSONObject;

import java.util.HashSet;

// Represents a label having a name and a list of all files labelled with this label
public class Label extends NamedObject implements Writable {
    Set<File> labelledFiles;

    // REQUIRES: name.isBlank() is false
    // EFFECTS: constructs a label named name with an empty list of files labelled with it
    // throws NameIsBlankException if name.isBlank() is true (name is empty or just whitespace)
    public Label(String name) {
        super(name);
        labelledFiles = new HashSet<>();
    }

    // MODIFIES: this, file
    // EFFECTS: adds file to the list of all files labelled with this label
    //          and adds this label to the file's list of labels
    public void labelFile(File file) {
        labelledFiles.add(file);

        file.addLabel(this);
    }

    // MODIFIES: this, file
    // EFFECTS: removes file from the list of all files labelled with this label
    //          and removes this label from file's list of labels
    public void unlabelFile(File file) {
        labelledFiles.remove(file);

        file.removeLabel(this);
    }
    
    // MODIFIES: this, every File in labelledFiles
    // EFFECTS: removes this label from every files's list of labels and removes every File reference this label stores
    public void unlabelAllFiles() {
        for (File file : labelledFiles) {
            file.removeLabel(this);
        }
        labelledFiles.clear();
    }

    // EFFECTS: returns all of the files labelled with this label
    public Set<File> getLabelledFiles() {
        return labelledFiles;
    }

    // EFFECTS: returns a JSON representation of this label
    @Override
    public JSONObject toJson() {
        return new JSONObject(); // stub
    }
}
