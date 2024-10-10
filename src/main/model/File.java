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

    // REQUIRES: !name.isEmpty()
    public void setName(String name) {
        
    }

    public String getFilePath() {
        return "";
    }

    public void setFilePath() {

    }
}
