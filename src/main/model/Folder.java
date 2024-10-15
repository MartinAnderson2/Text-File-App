package model;

import java.util.Set;
import java.util.HashSet;

// Represents a folder with a name that holds files as well as other folders
// and has a parent folder unless it is the root folder
public class Folder extends NamedObject {
    private Set<Folder> subfolders;
    private Set<File> containedFiles;
    private Folder parentFolder;

    // REQUIRES: !name.isEmpty()
    public Folder(String name) {
        this.name = name;
        subfolders = new HashSet<Folder>();
        containedFiles = new HashSet<File>();
        parentFolder = null;
    }

    // EFFECTS: returns a list of folders within this folder
    public Set<Folder> containedFolders() {
        return subfolders;
    }

    // EFFECTS: returns a list of files within this folder
    public Set<File> containedFiles() {
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
    //          and returns a reference to the newly-created folder
    public Folder makeSubfolder(String name) {
        Folder newFolder = new Folder(name);
        newFolder.parentFolder = this;
        subfolders.add(newFolder);

        return newFolder;
    }

    // MODIFIES: this
    // EFFECTS: removes this folder's reference to folder
    public void removeSubfolder(Folder folder) {
        subfolders.remove(folder);
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
    // and returns a reference to the newly created file
    public File addFile(String name, String path) {
        File newFile = new File(name, path);
        containedFiles.add(newFile);
        return newFile;
    }
    
    // EFFECTS: removes this folder's reference to file
    public void removeFile(File file) {
        containedFiles.remove(file);
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
}
