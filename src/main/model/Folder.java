package model;

import java.util.List;
import java.util.ArrayList;

// Represents a folder with a name that holds files as well as other folders and has a parent folder UNLESS it is the root folder
public class Folder extends NamedObject {
    // REQUIRES: !name.isEmpty()
    public Folder(String name) {

    }

    // EFFECTS: returns a list of folders within this folder
    public List<Folder> containedFolders() {
        return new ArrayList<Folder>();
    }

    // EFFECTS: returns a list of files within this folder
    public List<File> containedFiles() {
        return new ArrayList<File>();
    }

    // REQUIRES: this folder is not the root folder
    // EFFECTS: returns this folder's parent folder
    public Folder getParentFolder() {
        return new Folder("");
    }

    // REQUIRES: !name.isEmpty()
    // MODIFIES: this
    // EFFECTS: creates a new folder that is a subfolder of this folder, named name
    public void makeSubfolder(String name) {

    }

    // EFFECTS: if this folder contains a folder named name return it, otherwise return null
    public Folder getSubfolder(String name) {
        return new Folder(name);
    }

    // REQUIRES: !name.isEmpty()
    // MODIFIES: this
    // EFFECTS creates a new file named name with path path that is within this foler
    public void addFile(String name, String Path) {

    }
}
