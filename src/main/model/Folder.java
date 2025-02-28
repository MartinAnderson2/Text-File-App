package model;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;

import model.exceptions.NoSuchFileFoundException;
import model.exceptions.NoSuchFolderFoundException;
import model.exceptions.NameIsTakenException;

// Represents a folder with a name that holds files as well as other folders
// and has a parent folder unless it is the root folder
public class Folder extends NamedObject {
    public static final char FOLDER_SEPERATOR = '\\';

    private Set<Folder> subfolders;
    private Set<File> subfiles;
    private Folder parentFolder;

    // REQUIRES: name.isBlank() is false
    // EFFECTS: constructs a new root folder named name with empty lists of subfiles and subfolders
    // and no parent folder
    // throws NameIsBlankException if name.isBlank() is true (name is empty or just whitespace)
    public Folder(String name) {
        super(name);
        subfolders = new HashSet<>();
        subfiles = new HashSet<>();
        parentFolder = null;
    }

    // EFFECTS: returns a string representing the path of this file in the file system it is part of
    public String getPathInThisFileSystem() {
        if (parentFolder == null) {
            return getNameAsPath();
        } else {
            return parentFolder.getPathInThisFileSystem() + getNameAsPath();
        }
    }

    // EFFECTS: returns the name of this folder plus a backslash
    // in order to represent it in a path for this file system
    private String getNameAsPath() {
        return getName() + FOLDER_SEPERATOR;
    }

    // EFFECTS: returns a reference to the set of folders within this folder
    public Set<Folder> getSubfolders() {
        return subfolders;
    }

    // EFFECTS: returns a reference to the set of files within this folder
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

    // MODIFIES: this
    // EFFECTS: creates a new folder that is a subfolder of this folder, named name
    // throws NameIsTakenException if this already contains a subfolder named name
    // throws NameIsBlankException if name is blank
    public void makeSubfolder(String name) throws NameIsTakenException {
        try {
            Folder folderNamedName = getSubfolder(name);
            throw new NameIsTakenException(folderNamedName.getName());
        } catch (NoSuchFolderFoundException e) {
            // Continue since name is not taken
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

    // MODIFIES: this
    // EFFECTS creates a new file named name with path path that is within this folder
    // throws NameIsTakenException if this contains a file named name (ignores case)
    // throws NameIsBlankException if name is blank
    public void makeSubfile(String name, String path) throws NameIsTakenException {
        try {
            throw new NameIsTakenException(getSubfile(name).getName());
        } catch (NoSuchFileFoundException e) {
            File newFile = new File(name, path, this);
            subfiles.add(newFile);
        }
    }

    // MODIFIES: this
    // EFFECTS: adds a pre-existing subfile
    // throws NameIsTakenException if this already contains a subfile named file.getName()
    public void addExistingSubfile(File file) throws NameIsTakenException {
        try {
            throw new NameIsTakenException(getSubfile(file.getName()).getName());
        } catch (NoSuchFileFoundException e) {
            subfiles.add(file);
        }
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

    // EFFECTS: returns a JSON representation of this file (including its subfolders and files)
    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        json.put("subfiles", subfilesToJson());
        json.put("subfolders", subfoldersToJson());
        return json;
    }

    // EFFECTS: returns a JSONArray of JSON representations of the subfiles in this folder
    private JSONArray subfilesToJson() {
        JSONArray jsonArray = new JSONArray();

        for (File file : subfiles) {
            jsonArray.put(file.toJson());
        }

        return jsonArray;
    }

    // EFFECTS: returns a JSONArray of JSON representations of the subfolders in this folder
    private JSONArray subfoldersToJson() {
        JSONArray jsonArray = new JSONArray();
        
        for (Folder folder : subfolders) {
            jsonArray.put(folder.toJson());
        }

        return jsonArray;
    }
}
