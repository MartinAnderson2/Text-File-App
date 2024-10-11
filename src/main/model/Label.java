package model;

import java.util.List;
import java.util.ArrayList;

// Represents a label having a name and a list of all files labelled with this label
public class Label extends NamedObject {
    // REQUIRES: !name.isEmpty()
    public Label(String name) {

    }

    // MODIFIES: this
    // EFFECTS: adds file to the list of all files labelled with this label
    public void labelFile(File file) {

    }

    // EFFECTS: returns all of the files labelled with this label
    public List<File> getLabelledFiles() {
        return new ArrayList<File>();
    }
}
