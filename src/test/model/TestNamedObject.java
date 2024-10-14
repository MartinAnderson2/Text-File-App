package model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class TestNamedObject {
    NamedObject namedObject;

    // Subclasses must instantiated namedObject in runBefore
    @BeforeEach
    abstract void runBefore();

    @Test
    void testIsBeginningOfNameEmptyInput() {
        namedObject.setName("Apple");
        assertTrue(namedObject.isBeginningOfName(""));
    }

    @Test
    void testIsBeginningOfNameEmptyInputs() {
        namedObject.setName("Orange");
        assertTrue(namedObject.isBeginningOfName(""));
        assertFalse(namedObject.isBeginningOfName("Apple"));
    }

    @Test
    void testIsBeginningOfNameNoMatch() {
        namedObject.setName("Apple");
        assertFalse(namedObject.isBeginningOfName("Coconut"));
    }

    @Test
    void testIsBeginningOfNameJustFirstCharacterMatches() {
        namedObject.setName("Raspberry");
        assertFalse(namedObject.isBeginningOfName("Rose Hip"));
    }

    @Test
    void testIsBeginningOfNameFirstCharactersButNotAllMatch() {
        namedObject.setName("Apple");
        assertFalse(namedObject.isBeginningOfName("Apricot"));
    }

    @Test
    void testIsBeginningOfNameOneCharacterAndMatches() {
        namedObject.setName("Apple");
        assertTrue(namedObject.isBeginningOfName("A"));
    }

    @Test
    void testIsBeginningOfNameMultipleCharactersButNotFullAndMatches() {
        namedObject.setName("Orange");
        assertTrue(namedObject.isBeginningOfName("Ora"));
    }

    @Test
    void testIsBeginningOfNameAllCharactersMatch() {
        namedObject.setName("Apple");
        assertTrue(namedObject.isBeginningOfName("Apple"));
        assertTrue(namedObject.isBeginningOfName(namedObject.getName()));
    }

    @Test
    void testIsBeginningOfNameAllCharactersMatchButInputIsLonger() {
        namedObject.setName("Orange");
        assertFalse(namedObject.isBeginningOfName("Oranges"));
    }

    @Test
    void testIsBeginningOfNameWrongCase() {
        namedObject.setName("Orange");
        assertTrue(namedObject.isBeginningOfName("orange"));
    }


    @Test
    void testIsNamedEmpty() {
        namedObject.setName("Acer");
        assertFalse(namedObject.isNamed(""));
    }

    @Test
    void testIsNamedAllCharactersWrong() {
        namedObject.setName("Acer");
        assertFalse(namedObject.isNamed("MSI"));
    }

    @Test
    void testIsNamedFirstCharacterWrong() {
        namedObject.setName("Lenovo");
        assertFalse(namedObject.isNamed("Penovo"));
    }

    @Test
    void testIsNamedMiddleCharacterWrong() {
        namedObject.setName("Lenovo");
        assertFalse(namedObject.isNamed("Lenxvo"));
    }

    @Test
    void testIsNamedLastCharacterWrong() {
        namedObject.setName("ASUS");
        assertFalse(namedObject.isNamed("ASUT"));
    }

    @Test
    void testIsNamedRight() {
        namedObject.setName("ASUS");
        assertTrue(namedObject.isNamed("ASUS"));
        assertTrue(namedObject.isNamed(namedObject.getName()));
    }

    @Test
    void testIsNamedRightButLonger() {
        namedObject.setName("HP");
        assertFalse(namedObject.isNamed("HPs"));
    }

    @Test
    void testIsNamedWrongCaseOneCharacter() {
        namedObject.setName("H");
        assertTrue(namedObject.isNamed("h"));
    }

    @Test
    void testIsNamedRightLowerCaseDiffers() {
        namedObject.setName("HP");
        assertTrue(namedObject.isNamed("Hp"));
    }

    @Test
    void testIsNamedRightUpperCaseDiffers() {
        namedObject.setName("Lenovo");
        assertTrue(namedObject.isNamed("LenoVo"));
    }

    @Test
    void testIsNamedRightCasesDiffer() {
        namedObject.setName("Acer");
        assertTrue(namedObject.isNamed("aCER"));
    }
}
