package model;

import java.util.Set;

import model.exceptions.NameIsEmptyException;

import java.util.HashSet;

// Represents a label having a name and a list of all files labelled with this label
public class Label extends NamedObject {
    Set<File> labelledFiles;

    // EFFECTS: constructs a label named name with an empty list of files labelled with it
    // throws NameIsEmptyException if name is empty
    public Label(String name) throws NameIsEmptyException {
        if (name.isEmpty()) {
            throw new NameIsEmptyException();
        }
        this.name = name;
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
            this.unlabelFile(file);
        }
    }

    // EFFECTS: returns all of the files labelled with this label
    public Set<File> getLabelledFiles() {
        return labelledFiles;
    }

}
