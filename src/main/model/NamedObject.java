package model;

import ui.exceptions.NameIsEmptyException;

// Represents an arbitrary object that has a name and includes methods for handling that name
public abstract class NamedObject {
    protected String name;

    // EFFECTS: returns true if input matches (case insensitive) the beginning of this file's name,
    // including the empty string, otherwise false
    public boolean isBeginningOfName(String input) {
        if (input.length() > name.length()) {
            return false;
        }
// TODO: check if either of these other conditions are needed
        return input.isEmpty() || (input.equalsIgnoreCase(name.substring(0, input.length())));
    }

    // EFFECTS: returns true if input matches this file's name regardless of case
    public boolean isNamed(String input) {
        return name.equalsIgnoreCase(input);
    }

    public void setName(String name) throws NameIsEmptyException {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
