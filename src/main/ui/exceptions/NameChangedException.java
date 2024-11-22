package ui.exceptions;

public class NameChangedException extends Exception {
    private String newName;

    public NameChangedException(String newName) {
        this.newName = newName;
    }

    public String getNewName() {
        return newName;
    }
}
