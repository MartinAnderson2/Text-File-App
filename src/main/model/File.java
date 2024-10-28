package model;

import java.util.*;

import ui.exceptions.NameIsEmptyException;

// Represents a file having a name, a file path where it is stored on the user's computer,
// and a set of labels that it is tagged with
public class File extends NamedObject {
    private String filePath;
    private Set<Label> labels;

    // EFFECTS: constructs a new file named name with path filePath and an empty list of labels it is labelled with
    // throws NameIsEmptyException if name is empty
    public File(String name, String filePath) throws NameIsEmptyException {
        if (name.isEmpty()) {
            throw new NameIsEmptyException();
        }
        this.name = name;
        this.filePath = filePath;
        labels = new HashSet<Label>();
    }

    // MODIFIES: this
    // EFFECTS: labels this file with label
    // INVARIANT: label must have a reference to this while this is labelled
    protected void addLabel(Label label) {
        labels.add(label);
    }

    // MODIFIES: this
    // EFFECTS: removes given label from this file. returns true if it had a label on it and false if it did not
    protected boolean removeLabel(Label label) {
        boolean succeeded = labels.remove(label);
        return succeeded;
    }

    // EFFECTS: returns true if this file is tagged with label otherwise returns false
    public boolean isLabelled(Label label) {
        return labels.contains(label);
    }

    // EFFECTS: returns true if labelled with one or more lables and false if not labelled
    public boolean isLabelled() {
        return labels.size() >= 1;
    }

    // EFFECTS: returns the number of labels this file is tagged with
    public int numberLabelsTaggedWith() {
        return labels.size();
    }

    // EFFECTS: returns the name of this File in the user's file system on their computer
    public String getNameOfFileOnDisk() {
        return getCharactersAfterLastBackslash(this.filePath);
    }


    // Static Methods:

    // EFFECTS: returns the name of a file on the user's computer given a string of its path.
    // This is the contents of the string after the last backslash
    public static String getNameOfFileOnDisk(String path) {
        return getCharactersAfterLastBackslash(path);
    }

    // EFFECTS: returns the name of a file on the user's computer given a string of its path, without the file
    // extension. This is the contents of the string after the last backslash minus the text after (and including) the
    // final period
    public static String getNameOfFileOnDiskWithoutExtension(String path) {
        return getCharactersBeforeLastDot(getCharactersAfterLastBackslash(path));
    }


    // Basic Getters and Setters:

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getNumLabels() {
        return labels.size();
    }


    // Helper Methods:

    // EFFECTS: returns all characters after the final backslash of string; returns string if no backlash is present
    private static String getCharactersAfterLastBackslash(String string)  {
        for (int i = string.length() - 1; i > 0; i--) {
            if (string.charAt(i) == '\\') {
                return (string.substring(i + 1));
            }
        }
        return string;
    }

    // EFFECTS: returns string with all characters after and including the final dot (.) removed
    // returns string if no dot (.) is contained
    private static String getCharactersBeforeLastDot(String string) {
        for (int i = string.length() - 1; i > 0; i--) {
            if (string.charAt(i) == '.') {
                return (string.substring(0, i));
            }
        }
        return string;
    }
}