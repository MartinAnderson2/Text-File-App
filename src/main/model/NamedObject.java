package model;

public abstract class NamedObject {
    // EFFECTS: returns true if input perfectly matches the beginning of this file's name including the empty string
    //                  otherwise false
    public boolean isBeginningOfName(String input) {
        return false;
    }

    // EFFECTS: returns true if input perfectly matches this file's name
    public boolean isNamed(String input) {
        return false;
    }

    // REQUIRES: !name.isEmpty()
    public void setName(String name) {

    }

    public String getName() {
        return "name";
    }
}
