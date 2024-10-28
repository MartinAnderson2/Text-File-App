package model;

import java.util.Set;

import ui.exceptions.NameIsEmptyException;
import ui.exceptions.NameIsTakenException;
import ui.exceptions.NoSuchFileFoundException;
import ui.exceptions.NoSuchFolderFoundException;

import java.util.HashSet;

// Represents a folder with a name that holds files as well as other folders
// and has a parent folder unless it is the root folder
public class Folder extends NamedObject {
    private Set<Folder> subfolders;
    private Set<File> subfiles;
    private Folder parentFolder;

    // EFFECTS: constructs a new root folder named name with empty lists of subfiles and subfolders
    // and no parent folder
    // throws NameIsEmptyException if name.isEmpty() is true
    public Folder(String name) throws NameIsEmptyException {
        if (name.isEmpty()) {
            throw new NameIsEmptyException();
        }
        this.name = name;
        subfolders = new HashSet<Folder>();
        subfiles = new HashSet<File>();
        parentFolder = null;
    }

    // EFFECTS: returns a list of folders within this folder
    public Set<Folder> getSubfolders() {
        return subfolders;
    }

    // EFFECTS: returns a list of files within this folder
    public Set<File> getSubfiles() {
        return subfiles;
    }

    // EFFECTS: returns this folder's parent folder
    // throws NoSuchFolderFoundException if this folder does not have a parent folder
    public Folder getParentFolder() throws NoSuchFolderFoundException {
        if (parentFolder == null) {
            throw new NoSuchFolderFoundException();
        }
        return parentFolder;
    }

    // MODIFIES: this
    // EFFECTS: creates a new folder that is a subfolder of this folder, named name
    // throws NameIsEmptyException if name is empty
    public void makeSubfolder(String name) throws NameIsEmptyException, NameIsTakenException {
        if (name.isEmpty()) {
            throw new NameIsEmptyException();
        }
        if (hasSubfolder(name)) {
            throw new NameIsEmptyException();
        }
        Folder newFolder = new Folder(name);
        newFolder.parentFolder = this;
        subfolders.add(newFolder);
    }

    // MODIFIES: this
    // EFFECTS: removes this folder's reference to Folder named folderName (ignoring case)
    // throws NoSuchFolderFoundException if this does not contain a subfolder named folderName (ignores case)
    public void removeSubfolder(String folderName) throws NoSuchFolderFoundException {
        subfolders.remove(getSubfolder(folderName));
    }

    // EFFECTS: returns true if this contains a Folder named folderName otherwise returns false
    public boolean hasSubfolder(String folderName) {
        try {
            getSubfolder(folderName);
            return true;
        } catch (NoSuchFolderFoundException e) {
            return false;
        }
    }

    // EFFECTS: if this folder contains a folder named name (ignoring case) returns it,
    // otherwise throws NoSuchFolderFoundException
    public Folder getSubfolder(String name) throws NoSuchFolderFoundException {
        for (Folder folder : subfolders) {
            if (folder.isNamed(name)) {
                return folder;
            }
        }
        throw new NoSuchFolderFoundException();
    }

    // MODIFIES: this
    // EFFECTS creates a new file named name with path path that is within this folder
    // throws NameIsEmptyException if name is empty
    // throws NameIsTakenException if this contains a file named name (ignores case)
    public void makeSubfile(String name, String path) throws NameIsEmptyException, NameIsTakenException {
        if (name.isEmpty()) {
            throw new NameIsEmptyException();
        }
        try {
            throw new NameIsTakenException(getSubfile(name).name);
        } catch (NoSuchFileFoundException e) {
            File newFile = new File(name, path);
            subfiles.add(newFile);
        }
    }

    // MODIFIES: this
    // EFFECTS: adds a pre-existing subfile
    public void addExistingSubfile(File file) {
        subfiles.add(file);
    }
    
    // MODIFIES: this
    // EFFECTS: removes this folder's reference to file named fileName (ignoring case)
    // throws NoSuchFileFoundException if this does not contain a file named fileName (ignoring case)
    public void removeSubfile(String fileName) throws NoSuchFileFoundException {
        subfiles.remove(getSubfile(fileName));
    }

    // EFFECTS: returns true if this contains a File named fileName otherwise returns false
    public boolean hasSubfile(String fileName) {
        try {
            getSubfile(fileName);
            return true;
        } catch (NoSuchFileFoundException e) {
            return false;
        }
    }

    // EFFECTS: returns file with given name
    // throws NoSuchFileExistsException if this folder does not contain a file named fileName (ignoring case)
    public File getSubfile(String name) throws NoSuchFileFoundException {
        for (File file : subfiles) {
            if (file.isNamed(name)) {
                return file;
            }
        }
        throw new NoSuchFileFoundException();
    }
}
