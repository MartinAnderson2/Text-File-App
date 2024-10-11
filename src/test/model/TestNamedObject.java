package model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class TestNamedObject {
    NamedObject namedObject;

    // Subclasses must instantiated namedObject in runBefore
    @BeforeEach
    abstract void runBefore();

    @Test
    void sampleTest() {
        assertTrue(true);
    }
}
