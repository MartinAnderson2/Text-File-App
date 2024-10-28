package model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ui.exceptions.NameIsEmptyException;

public abstract class TestNamedObject {
    NamedObject namedObject;

    // Subclasses must instantiated namedObject in runBefore
    @BeforeEach
    abstract void runBefore();

    @Test
    void testIsBeginningOfNameEmptyInput() {
        setNamedObjectNameFailIfExceptionThrown("Apple");
        assertTrue(namedObject.isBeginningOfName(""));
    }

    @Test
    void testIsBeginningOfNameEmptyInputs() {
        setNamedObjectNameFailIfExceptionThrown("Orange");
        assertTrue(namedObject.isBeginningOfName(""));
        assertFalse(namedObject.isBeginningOfName("Apple"));
    }

    @Test
    void testIsBeginningOfNameNoMatch() {
        setNamedObjectNameFailIfExceptionThrown("Apple");
        assertFalse(namedObject.isBeginningOfName("Coconut"));
    }

    @Test
    void testIsBeginningOfNameJustFirstCharacterMatches() {
        setNamedObjectNameFailIfExceptionThrown("Raspberry");
        assertFalse(namedObject.isBeginningOfName("Rose Hip"));
    }

    @Test
    void testIsBeginningOfNameFirstCharactersButNotAllMatch() {
        setNamedObjectNameFailIfExceptionThrown("Apple");
        assertFalse(namedObject.isBeginningOfName("Apricot"));
    }

    @Test
    void testIsBeginningOfNameOneCharacterAndMatches() {
        setNamedObjectNameFailIfExceptionThrown("Apple");
        assertTrue(namedObject.isBeginningOfName("A"));
    }

    @Test
    void testIsBeginningOfNameMultipleCharactersButNotFullAndMatches() {
        setNamedObjectNameFailIfExceptionThrown("Orange");
        assertTrue(namedObject.isBeginningOfName("Ora"));
    }

    @Test
    void testIsBeginningOfNameAllCharactersMatch() {
        setNamedObjectNameFailIfExceptionThrown("Apple");
        assertTrue(namedObject.isBeginningOfName("Apple"));
        assertTrue(namedObject.isBeginningOfName(namedObject.getName()));
    }

    @Test
    void testIsBeginningOfNameAllCharactersMatchButInputIsLonger() {
        setNamedObjectNameFailIfExceptionThrown("Orange");
        assertFalse(namedObject.isBeginningOfName("Oranges"));
    }

    @Test
    void testIsBeginningOfNameWrongCase() {
        setNamedObjectNameFailIfExceptionThrown("Orange");
        assertTrue(namedObject.isBeginningOfName("orange"));
    }


    @Test
    void testIsNamedEmpty() {
        setNamedObjectNameFailIfExceptionThrown("Acer");
        assertFalse(namedObject.isNamed(""));
    }

    @Test
    void testIsNamedAllCharactersWrong() {
        setNamedObjectNameFailIfExceptionThrown("Acer");
        assertFalse(namedObject.isNamed("MSI"));
    }

    @Test
    void testIsNamedFirstCharacterWrong() {
        setNamedObjectNameFailIfExceptionThrown("Lenovo");
        assertFalse(namedObject.isNamed("Penovo"));
    }

    @Test
    void testIsNamedMiddleCharacterWrong() {
        setNamedObjectNameFailIfExceptionThrown("Lenovo");
        assertFalse(namedObject.isNamed("Lenxvo"));
    }

    @Test
    void testIsNamedLastCharacterWrong() {
        setNamedObjectNameFailIfExceptionThrown("ASUS");
        assertFalse(namedObject.isNamed("ASUT"));
    }

    @Test
    void testIsNamedRight() {
        setNamedObjectNameFailIfExceptionThrown("ASUS");
        assertTrue(namedObject.isNamed("ASUS"));
        assertTrue(namedObject.isNamed(namedObject.getName()));
    }

    @Test
    void testIsNamedRightButLonger() {
        setNamedObjectNameFailIfExceptionThrown("HP");
        assertFalse(namedObject.isNamed("HPs"));
    }

    @Test
    void testIsNamedWrongCaseOneCharacter() {
        setNamedObjectNameFailIfExceptionThrown("H");
        assertTrue(namedObject.isNamed("h"));
    }

    @Test
    void testIsNamedRightLowerCaseDiffers() {
        setNamedObjectNameFailIfExceptionThrown("HP");
        assertTrue(namedObject.isNamed("Hp"));
    }

    @Test
    void testIsNamedRightUpperCaseDiffers() {
        setNamedObjectNameFailIfExceptionThrown("Lenovo");
        assertTrue(namedObject.isNamed("LenoVo"));
    }

    @Test
    void testIsNamedRightCasesDiffer() {
        setNamedObjectNameFailIfExceptionThrown("Acer");
        assertTrue(namedObject.isNamed("aCER"));
    }
    
    // EFFECTS: sets namedObject's name to name and fails if NameIsEmptyException thrown
    private void setNamedObjectNameFailIfExceptionThrown(String name) {
        try {
            namedObject.setName(name);
        } catch (NameIsEmptyException e) {
            fail("NameIsEmptyException thrown when name was not empty");
        }
    }
}
