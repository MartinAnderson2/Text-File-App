package model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.exceptions.NameIsBlankException;

public class TestLabel extends TestNamedObject {
    Folder rootFolder;

    Label nounLabel;
    Label adjectiveLabel;

    File houseFile;
    File happinessFile;
    File homeyFile;
    File happyFile;

    @BeforeEach
    void runBefore() {
        try {
            namedObject = new Label("name");

            nounLabel = new Label("Noun");
            adjectiveLabel = new Label("Adjective");

            houseFile = new File("House", "C:\\210", rootFolder);
            happinessFile = new File("Happiness", "C:\\Haha\\Yes", rootFolder);
            homeyFile = new File("Homey", "C:\\110", rootFolder);
            happyFile = new File("Happy", "C:\\yeah", rootFolder);
        } catch (NameIsBlankException e) {
            fail("NameIsEmptyException thrown when name was not empty");
        }
    }

    @Test
    void testConstructor() {
        assertEquals("Noun", nounLabel.getName());
        assertTrue(nounLabel.getLabelledFiles().isEmpty());

        assertEquals("Adjective", adjectiveLabel.getName());
        assertTrue(adjectiveLabel.getLabelledFiles().isEmpty());

        Label label = null;
        try {
            label = new Label("");
            fail("NameIsEmptyException not thrown for empty name");
        } catch (NameIsBlankException e) {
            // Expected
            assertNull(label);
        }
    }

    @Test
    void testLabelFileOneFile() {
        assertFalse(houseFile.isLabelled(nounLabel));


        nounLabel.labelFile(houseFile);


        Set<File> filesLabelledNoun = nounLabel.getLabelledFiles();

        assertEquals(1, filesLabelledNoun.size());
        assertTrue(filesLabelledNoun.contains(houseFile));
        assertTrue(houseFile.isLabelled(nounLabel));
    }

    @Test
    void testLabelFileTwoFilesOneLabel() {
        assertFalse(homeyFile.isLabelled(adjectiveLabel));
        assertFalse(happyFile.isLabelled(adjectiveLabel));


        adjectiveLabel.labelFile(homeyFile);
        adjectiveLabel.labelFile(happyFile);


        Set<File> filesLabelledAdjective = adjectiveLabel.getLabelledFiles();

        assertEquals(2, filesLabelledAdjective.size());
        assertTrue(filesLabelledAdjective.contains(homeyFile));
        assertTrue(homeyFile.isLabelled(adjectiveLabel));

        assertEquals(2, filesLabelledAdjective.size());
        assertTrue(filesLabelledAdjective.contains(happyFile));
        assertTrue(happyFile.isLabelled(adjectiveLabel));
    }

    @Test
    void testLabelFileMultipleFilesMultipleLabels() {
        assertFalse(happinessFile.isLabelled(nounLabel));
        assertFalse(homeyFile.isLabelled(adjectiveLabel));
        assertFalse(happyFile.isLabelled(adjectiveLabel));


        nounLabel.labelFile(happinessFile);
        adjectiveLabel.labelFile(homeyFile);
        adjectiveLabel.labelFile(happyFile);


        Set<File> filesLabelledNoun = nounLabel.getLabelledFiles();
        Set<File> filesLabelledAdjective = adjectiveLabel.getLabelledFiles();

        assertEquals(1, filesLabelledNoun.size());
        assertTrue(filesLabelledNoun.contains(happinessFile));
        assertTrue(happinessFile.isLabelled(nounLabel));

        assertEquals(2, filesLabelledAdjective.size());
        assertTrue(filesLabelledAdjective.contains(homeyFile));
        assertTrue(homeyFile.isLabelled(adjectiveLabel));

        assertEquals(2, filesLabelledAdjective.size());
        assertTrue(filesLabelledAdjective.contains(happyFile));
        assertTrue(happyFile.isLabelled(adjectiveLabel));
    }

    @Test
    void testLabelFileTwoFilesDuplicateLabels() {
        assertFalse(homeyFile.isLabelled(adjectiveLabel));
        assertFalse(happyFile.isLabelled(adjectiveLabel));

        nounLabel.labelFile(happinessFile);
        nounLabel.labelFile(happinessFile);
        adjectiveLabel.labelFile(homeyFile);
        adjectiveLabel.labelFile(happyFile);
        adjectiveLabel.labelFile(happyFile);
        adjectiveLabel.labelFile(homeyFile);


        Set<File> filesLabelledNoun = nounLabel.getLabelledFiles();
        Set<File> filesLabelledAdjective = adjectiveLabel.getLabelledFiles();

        assertEquals(1, filesLabelledNoun.size());
        assertTrue(filesLabelledNoun.contains(happinessFile));
        assertTrue(happinessFile.isLabelled(nounLabel));

        assertEquals(2, filesLabelledAdjective.size());
        assertTrue(filesLabelledAdjective.contains(homeyFile));
        assertTrue(filesLabelledAdjective.contains(happyFile));
        assertTrue(homeyFile.isLabelled(adjectiveLabel));
        assertTrue(happyFile.isLabelled(adjectiveLabel));
    }

    @Test
    void testUnlabelFileOneFile() {
        nounLabel.labelFile(houseFile);

        nounLabel.unlabelFile(houseFile);

        Set<File> filesLabelledNoun = nounLabel.getLabelledFiles();

        assertEquals(0, filesLabelledNoun.size());
        assertFalse(filesLabelledNoun.contains(houseFile));
        assertFalse(houseFile.isLabelled(nounLabel));
    }

    @Test
    void testUnlabelFileMultipleFilesMultipleLabels() {
        nounLabel.labelFile(houseFile);
        nounLabel.labelFile(happinessFile);
        adjectiveLabel.labelFile(homeyFile);
        adjectiveLabel.labelFile(happyFile);

        nounLabel.unlabelFile(houseFile);
        nounLabel.unlabelFile(happinessFile);
        adjectiveLabel.unlabelFile(happyFile);
        adjectiveLabel.unlabelFile(happyFile);


        Set<File> filesLabelledNoun = nounLabel.getLabelledFiles();
        Set<File> filesLabelledAdjective = adjectiveLabel.getLabelledFiles();

        assertEquals(0, filesLabelledNoun.size());
        assertFalse(filesLabelledNoun.contains(happinessFile));
        assertFalse(happinessFile.isLabelled(nounLabel));

        assertEquals(1, filesLabelledAdjective.size());
        assertFalse(filesLabelledAdjective.contains(happyFile));
        assertFalse(happyFile.isLabelled(adjectiveLabel));
        
        assertTrue(filesLabelledAdjective.contains(homeyFile));
        assertTrue(homeyFile.isLabelled(adjectiveLabel));
    }

    @Test
    void testUnlabelAllFilesNoFilesLabelled() {
        Label nothingLabel;
        try {
            nothingLabel = new Label("nothing");
        } catch (NameIsBlankException e) {
            fail("NameIsEmptyException thrown when constructing a label with a non-empty name");
            return;
        }

        nothingLabel.unlabelAllFiles();

        assertTrue(nothingLabel.getLabelledFiles().isEmpty());
        assertFalse(houseFile.isLabelled(nothingLabel));
        assertFalse(happinessFile.isLabelled(nothingLabel));
        assertFalse(homeyFile.isLabelled(nothingLabel));
        assertFalse(happyFile.isLabelled(nothingLabel));
    }

    @Test
    void testUnlabelAllFilesOneFileLabelled() {
        Label objectLabel;
        try {
            objectLabel = new Label("Object");
        } catch (NameIsBlankException e) {
            fail("NameIsEmptyException thrown when constructing a label with a non-empty name");
            return;
        }
        objectLabel.labelFile(houseFile);

        objectLabel.unlabelAllFiles();

        assertTrue(objectLabel.getLabelledFiles().isEmpty());
        assertFalse(houseFile.isLabelled(objectLabel));
        assertFalse(happinessFile.isLabelled(objectLabel));
        assertFalse(homeyFile.isLabelled(objectLabel));
        assertFalse(happyFile.isLabelled(objectLabel));
    }

    @Test
    void testUnlabelAllFilesMultipleFilesLabelled() {
        nounLabel.unlabelAllFiles();

        assertTrue(nounLabel.getLabelledFiles().isEmpty());
        assertFalse(houseFile.isLabelled(nounLabel));
        assertFalse(happinessFile.isLabelled(nounLabel));
        assertFalse(homeyFile.isLabelled(nounLabel));
        assertFalse(happyFile.isLabelled(nounLabel));
    }

    @Test
    void testUnlabelAllFilesMultipleLabelsMultipleFilesLabelled() {
        nounLabel.unlabelAllFiles();
        adjectiveLabel.unlabelAllFiles();

        assertTrue(nounLabel.getLabelledFiles().isEmpty());
        assertFalse(houseFile.isLabelled(nounLabel));
        assertFalse(happinessFile.isLabelled(nounLabel));
        assertFalse(homeyFile.isLabelled(nounLabel));
        assertFalse(happyFile.isLabelled(nounLabel));
        assertTrue(adjectiveLabel.getLabelledFiles().isEmpty());
        assertFalse(houseFile.isLabelled(adjectiveLabel));
        assertFalse(happinessFile.isLabelled(adjectiveLabel));
        assertFalse(homeyFile.isLabelled(adjectiveLabel));
        assertFalse(happyFile.isLabelled(adjectiveLabel));
    }
}