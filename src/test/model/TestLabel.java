package model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.exceptions.NameIsEmptyException;

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
        } catch (NameIsEmptyException e) {
            fail("NameIsEmptyException thrown when name was not empty");
        }
    }

    @Test
    void testConstructor() {
        assertEquals("Noun", nounLabel.getName());
        assertTrue(nounLabel.getLabelledFiles().isEmpty());

        assertEquals("Adjective", adjectiveLabel.getName());
        assertTrue(adjectiveLabel.getLabelledFiles().isEmpty());
    }

    @Test
    void testlabelFileOneFile() {
        assertFalse(houseFile.isLabelled(nounLabel));


        nounLabel.labelFile(houseFile);


        Set<File> filesLabelledNoun = nounLabel.getLabelledFiles();

        assertEquals(1, filesLabelledNoun.size());
        assertTrue(filesLabelledNoun.contains(houseFile));
        assertTrue(houseFile.isLabelled(nounLabel));
    }

    @Test
    void testlabelFileTwoFilesOneLabel() {
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
    void testlabelFileMultipleFilesMultipleLabels() {
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
    void testlabelFileTwoFilesDuplicateLabels() {
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
    void testunlabelFileOneFile() {
        nounLabel.labelFile(houseFile);

        nounLabel.unlabelFile(houseFile);

        Set<File> filesLabelledNoun = nounLabel.getLabelledFiles();

        assertEquals(0, filesLabelledNoun.size());
        assertFalse(filesLabelledNoun.contains(houseFile));
        assertFalse(houseFile.isLabelled(nounLabel));
    }

    @Test
    void testunlabelFileMultipleFilesMultipleLabels() {
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
}