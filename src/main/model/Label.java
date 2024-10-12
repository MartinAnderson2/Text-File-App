package model;

import java.util.List;
import java.util.ArrayList;

// Represents a label having a name and a list of all files labelled with this label
public class Label extends NamedObject {
    List<File> labelledFiles;

    // REQUIRES: !name.isEmpty()
    public Label(String name) {
        this.name = name;
        labelledFiles = new ArrayList<>();
        // TODO: Check if a Set is better-suited
    }

    // MODIFIES: this, file
    // EFFECTS: adds file to the list of all files labelled with this label
    //          and adds this label to the File's list of labels
    public void labelFile(File file) {
        if (!labelledFiles.contains(file)) {
            labelledFiles.add(file);
        }

        file.addLabel(this);
    }

    // EFFECTS: returns all of the files labelled with this label
    public List<File> getLabelledFiles() {
        return labelledFiles;
    }
}
