package model;

import model.exceptions.NameIsBlankException;

// Represents an arbitrary object that has a name and includes methods for handling that name
public abstract class NamedObject {
    private String name;

    // REQUIRES: name.isBlank() is false
    // EFFECTS: creates a new NamedObject named name
    // throws NameIsBlankException if name.isBlank() is true (name is empty or just whitespace)
    public NamedObject(String name) {
        if (name.isBlank()) {
            throw new NameIsBlankException();
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // REQUIRES: name.isBlank() is false
    // MODIFIES: this
    // EFFECTS: sets name to name
    // throws NameIsBlankException if name.isBlank() is true (name is empty or just whitespace)
    public void setName(String name) {
        if (name.isBlank()) {
            throw new NameIsBlankException();
        }
        this.name = name;
    }

    // EFFECTS: returns true if input matches this file's name regardless of case
    public boolean isNamed(String input) {
        return name.equalsIgnoreCase(input);
    }

    // EFFECTS: returns true if input matches (case insensitive) the beginning of this file's name,
    // including the empty string, otherwise false
    public boolean isBeginningOfName(String input) {
        if (input.length() > name.length()) {
            return false;
        }
        return (input.equalsIgnoreCase(name.substring(0, input.length())));
    }
}
