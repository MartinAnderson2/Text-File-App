package model;

public abstract class NamedObject {
    protected String name;

    // EFFECTS: returns true if input perfectly matches the beginning of this file's name including the empty string
    //                  otherwise false
    public boolean isBeginningOfName(String input) {
        if (input.length() <= name.length()) {
            return input.isEmpty() || (input.equals(name.substring(0, input.length())));
        }
        else {
            return false;
        }
    }

    // EFFECTS: returns true if input matches this file's name regardless of case
    public boolean isNamed(String input) {
        String nameLowerCase = name.toLowerCase();
        String inputLowerCase = input.toLowerCase();

        return nameLowerCase.equals(inputLowerCase);
    }

    // REQUIRES: !name.isEmpty()
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
