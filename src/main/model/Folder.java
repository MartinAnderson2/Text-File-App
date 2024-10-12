package model;

import java.util.List;
import java.util.ArrayList;

// Represents a folder with a name that holds files as well as other folders and has a parent folder unless it is a root folder
public class Folder extends NamedObject {
    private List<Folder> subfolders;
    private List<File> containedFiles;
    private Folder parentFolder;

    // REQUIRES: !name.isEmpty()
    public Folder(String name) {
        this.name = name;
        subfolders = new ArrayList<Folder>();
        containedFiles = new ArrayList<File>();
        parentFolder = null;
    }

    // EFFECTS: returns a list of folders within this folder
    public List<Folder> containedFolders() {
        return subfolders;
    }

    // EFFECTS: returns a list of files within this folder
    public List<File> containedFiles() {
        return containedFiles;
    }

    // REQUIRES: this folder is not the root folder
    // EFFECTS: returns this folder's parent folder
    public Folder getParentFolder() {
        return parentFolder;
    }

    // REQUIRES: !name.isEmpty()
    // MODIFIES: this
    // EFFECTS: creates a new folder that is a subfolder of this folder, named name
    public void makeSubfolder(String name) {
        Folder newFolder = new Folder(name);
        newFolder.parentFolder = this;
        subfolders.add(newFolder);
    }

    // EFFECTS: if this folder contains a folder named name return it, otherwise return null
    public Folder getSubfolder(String name) {
        for (Folder folder : subfolders) {
            if (folder.isNamed(name)) {
                return folder;
            }
        }
        return null;
    }

    // REQUIRES: !name.isEmpty()
    // MODIFIES: this
    // EFFECTS creates a new file named name with path path that is within this foler
    public void addFile(String name, String path) {
        containedFiles.add(new File(name, path));
    }

    // EFFECTS: returns file with given name or null if not found
    public File getFile(String name) {
        for (File file : containedFiles) {
            if (file.isNamed(name)) {
                return file;
            }
        }
        return null;
    }
    
    // TODO: See if this is possible
    // private NamedObject getNamedObject(String name, List<NamedObject> namedObjects) {
    //     for (NamedObject namedObject : namedObjects) {
    //         if (namedObject.isNamed(name)) {
    //             return namedObject;
    //         }
    //     }
    //     return null;
    // }
}
