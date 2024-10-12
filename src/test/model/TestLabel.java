package model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestLabel extends TestNamedObject {
    Label nounLabel;
    Label adjectiveLabel;

    File houseFile;
    File happinessFile;
    File homeyFile;
    File happyFile;

    @BeforeEach
    void runBefore() {
        namedObject = new Label("name");

        nounLabel = new Label("Noun");
        adjectiveLabel = new Label("Adjective");

        houseFile = new File("House", "C:\\210");
        happinessFile = new File("Happiness", "C:\\Haha\\Yes");
        homeyFile = new File("Homey", "C:\\110");
        happyFile = new File("Happy", "C:\\yeah");
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


        List<File> filesLabelledNoun = nounLabel.getLabelledFiles();

        assertEquals(1, filesLabelledNoun.size());
        assertEquals(houseFile, filesLabelledNoun.get(0));
        assertTrue(houseFile.isLabelled(nounLabel));
    }

    @Test
    void testlabelFileTwoFilesOneLabel() {
        assertFalse(homeyFile.isLabelled(adjectiveLabel));
        assertFalse(happyFile.isLabelled(adjectiveLabel));


        adjectiveLabel.labelFile(homeyFile);
        adjectiveLabel.labelFile(happyFile);


        List<File> filesLabelledAdjective = adjectiveLabel.getLabelledFiles();

        assertEquals(2, filesLabelledAdjective.size());
        assertEquals(homeyFile, filesLabelledAdjective.get(0));
        assertTrue(homeyFile.isLabelled(adjectiveLabel));

        assertEquals(2, filesLabelledAdjective.size());
        assertEquals(happyFile, filesLabelledAdjective.get(1));
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


        List<File> filesLabelledNoun = nounLabel.getLabelledFiles();
        List<File> filesLabelledAdjective = adjectiveLabel.getLabelledFiles();

        assertEquals(1, filesLabelledNoun.size());
        assertEquals(happinessFile, filesLabelledNoun.get(0));
        assertTrue(happinessFile.isLabelled(nounLabel));

        assertEquals(2, filesLabelledAdjective.size());
        assertEquals(homeyFile, filesLabelledAdjective.get(0));
        assertTrue(homeyFile.isLabelled(adjectiveLabel));

        assertEquals(2, filesLabelledAdjective.size());
        assertEquals(happyFile, filesLabelledAdjective.get(1));
        assertTrue(happyFile.isLabelled(adjectiveLabel));
    }
}