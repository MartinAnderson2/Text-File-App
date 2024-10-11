package model;

// Represents a file having a name, a file path where it is stored on the user's computer,
// and a set of labels that it is tagged with
public class File extends NamedObject {
    // REQUIRES: !name.isEmpty()
    public File(String name, String filePath) {
    }

    // MODIFIES: this
    // EFFECTS: labels this file with label
    public void addLabel(Label label) {

    }

    // MODIFIES: this
    // EFFECTS: removes given label from this file
    public boolean removeLabel(Label label) {
        return false;
    }

    // MODIFIES: this
    // EFFECTS: removes all labels from this file
    public void removeAllLabels() {
        
    }

    // EFFECTS: returns true if this file is tagged with label otherwise returns false
    public boolean isLabelled(Label label) {
        return false;
    }

    // EFFECTS: returns true if labelled with one or more lables and false if not labelled
    public boolean isLabelled() {
        return false;
    }

    // EFFECTS: returns the number of labels this file is tagged with
    public int numberLabelsTaggedWith() {
        return 0;
    }

    public String getFilePath() {
        return "";
    }

    public void setFilePath() {

    }
}
