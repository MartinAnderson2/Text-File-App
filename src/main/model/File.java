package model;

import java.util.*;

// Represents a file having a name, a file path where it is stored on the user's computer,
// and a set of labels that it is tagged with
public class File extends NamedObject {
    private String filePath;
    private Set<Label> labels;

    // REQUIRES: !name.isEmpty()
    public File(String name, String filePath) {
        this.name = name;
        this.filePath = filePath;
        labels = new HashSet<Label>();
    }

    // MODIFIES: this
    // EFFECTS: labels this file with label
    public void addLabel(Label label) {
        labels.add(label);
    }

    // MODIFIES: this
    // EFFECTS: removes given label from this file. returns true if it had a label on it and false if it did not
    public boolean removeLabel(Label label) {
        return labels.remove(label);
    }

    // MODIFIES: this
    // EFFECTS: removes all labels from this file
    public void removeAllLabels() {
        labels.clear();
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
