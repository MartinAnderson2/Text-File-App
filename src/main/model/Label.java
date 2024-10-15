package model;

import java.util.Set;
import java.util.HashSet;

// Represents a label having a name and a list of all files labelled with this label
public class Label extends NamedObject {
    Set<File> labelledFiles;

    // REQUIRES: !name.isEmpty()
    public Label(String name) {
        this.name = name;
        labelledFiles = new HashSet<>();
    }

    // MODIFIES: this, file
    // EFFECTS: adds file to the list of all files labelled with this label
    //          and adds this label to the File's list of labels
    public void labelFile(File file) {
        labelledFiles.add(file);

        file.addLabel(this);
    }

    // EFFECTS: returns all of the files labelled with this label
    public Set<File> getLabelledFiles() {
        return labelledFiles;
    }
}
