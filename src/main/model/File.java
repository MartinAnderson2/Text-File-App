package model;

import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

// Represents a file having a name, a file path where it is stored on the user's computer,
// and a set of labels that it is labelled with
public class File extends NamedObject {
    private String filePath;
    private Folder parentFolder;
    private Set<Label> labels;

    // REQUIRES: name.isBlank() is false
    // EFFECTS: constructs a new file named name with path filePath, parent Folder parentFolder,
    // and an empty list of labels it is labelled with
    // throws NameIsBlankException if name.isBlank() is true (name is empty or just whitespace)
    public File(String name, String filePath, Folder parentFolder) {
        super(name);
        this.filePath = filePath;
        this.parentFolder = parentFolder;
        labels = new HashSet<>();
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    // EFFECTS: returns a string representing the path of this file in the file system it is part of
    public String getPathInThisFileSystem() {
        return parentFolder.getPathInThisFileSystem() + getName();
    }

    public Folder getParentFolder() {
        return parentFolder;
    }

    public int getNumLabels() {
        return labels.size();
    }

    // MODIFIES: this
    // EFFECTS: labels this file with label
    protected void addLabel(Label label) {
        labels.add(label);
    }

    // MODIFIES: this
    // EFFECTS: removes given label from this file. Returns true if it had a label on it and false if it did not
    protected boolean removeLabel(Label label) {
        return labels.remove(label);
    }

    // EFFECTS: returns true if this file is labelled with label otherwise returns false
    public boolean isLabelled(Label label) {
        return labels.contains(label);
    }

    // EFFECTS: returns true if labelled with one or more lables and false if not labelled
    public boolean isLabelled() {
        return labels.size() >= 1;
    }

    // EFFECTS: returns the number of labels this file is labelled with
    public int numberLabelsTaggedWith() {
        return labels.size();
    }

    // EFFECTS: returns the name of this File in the user's file system on their computer
    public String getNameOfFileOnDisk() {
        return getCharactersAfterLastSlash(this.filePath);
    }

    // EFFECTS: returns the name of a file on the user's computer given a string of its path.
    // This is the contents of the string after the last backslash
    public static String getNameOfFileOnDisk(String path) {
        return getCharactersAfterLastSlash(path);
    }
    

    // Persistence-Related Method:

    // EFFECTS: returns a JSON representation of this file
    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        json.put("filePath", filePath);
        json.put("labels", labelsToJson());
        return json;
    }

    // EFFECTS: returns a JSON representation of the labels on this file
    // This will effectively just be a list of their names such that the labels (that will already have been made) can
    // be added to this file. The labels also have a list of the files that are labelled with them, but this is not
    // saved in their JSON representations
    private JSONArray labelsToJson() {
        JSONArray jsonArray = new JSONArray();

        for (Label label : labels) {
            jsonArray.put(label.toJson());
        }

        return jsonArray;
    }


    // Static Methods:

    // EFFECTS: returns the name of a file on the user's computer given a string of its path, without the file
    // extension. This is the contents of the string after the last backslash minus the text after (and including) the
    // final period
    public static String getNameOfFileOnDiskWithoutExtension(String path) {
        return getCharactersBeforeLastDot(getCharactersAfterLastSlash(path));
    }


    // Helper Methods:

    // EFFECTS: returns all characters after the final forward or backslash of string
    // returns string if no slash is present
    private static String getCharactersAfterLastSlash(String string)  {
        for (int i = string.length() - 1; i >= 0; i--) {
            if (string.charAt(i) == '\\' || string.charAt(i) == '/') {
                return (string.substring(i + 1));
            }
        }
        return string;
    }

    // EFFECTS: returns string with all characters after and including the final dot (.) removed
    // returns string if no dot (.) is contained
    private static String getCharactersBeforeLastDot(String string) {
        for (int i = string.length() - 1; i >= 0; i--) {
            if (string.charAt(i) == '.') {
                return (string.substring(0, i));
            }
        }
        return string;
    }
}