package ui.exceptions;

public class NameIsTakenException extends Exception {
    private String capitalizationOfTakenName;

    public NameIsTakenException(String capitalizationOfTakenName) {
        this.capitalizationOfTakenName = capitalizationOfTakenName;
    }

    public String getCapitalizationOfTakenName() {
        return capitalizationOfTakenName;
    }
}
