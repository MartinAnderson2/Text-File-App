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
        assertFalse(houseFile.taggedWithLabel(nounLabel));


        nounLabel.labelFile(houseFile);


        List<File> filesLabelledNoun = nounLabel.getLabelledFiles();

        assertEquals(1, filesLabelledNoun.size());
        assertEquals(houseFile, filesLabelledNoun.get(0));
        assertTrue(houseFile.taggedWithLabel(nounLabel));
    }

    @Test
    void testlabelFileTwoFilesOneLabel() {
        assertFalse(homeyFile.taggedWithLabel(adjectiveLabel));
        assertFalse(happyFile.taggedWithLabel(adjectiveLabel));


        adjectiveLabel.labelFile(homeyFile);
        adjectiveLabel.labelFile(happyFile);


        List<File> filesLabelledAdjective = adjectiveLabel.getLabelledFiles();

        assertEquals(1, filesLabelledAdjective.size());
        assertEquals(homeyFile, filesLabelledAdjective.get(0));
        assertTrue(homeyFile.taggedWithLabel(adjectiveLabel));

        assertEquals(1, filesLabelledAdjective.size());
        assertEquals(happyFile, filesLabelledAdjective.get(1));
        assertTrue(happyFile.taggedWithLabel(adjectiveLabel));
    }

    @Test
    void testlabelFileMultipleFilesMultipleLabels() {
        assertFalse(happinessFile.taggedWithLabel(nounLabel));
        assertFalse(homeyFile.taggedWithLabel(adjectiveLabel));
        assertFalse(happyFile.taggedWithLabel(adjectiveLabel));


        nounLabel.labelFile(happinessFile);
        adjectiveLabel.labelFile(homeyFile);
        adjectiveLabel.labelFile(happyFile);


        List<File> filesLabelledNoun = nounLabel.getLabelledFiles();
        List<File> filesLabelledAdjective = adjectiveLabel.getLabelledFiles();

        assertEquals(1, filesLabelledNoun.size());
        assertEquals(happinessFile, filesLabelledNoun.get(0));
        assertTrue(happinessFile.taggedWithLabel(nounLabel));

        assertEquals(1, filesLabelledAdjective.size());
        assertEquals(homeyFile, filesLabelledAdjective.get(0));
        assertTrue(homeyFile.taggedWithLabel(adjectiveLabel));

        assertEquals(1, filesLabelledAdjective.size());
        assertEquals(happyFile, filesLabelledAdjective.get(1));
        assertTrue(happyFile.taggedWithLabel(adjectiveLabel));
    }
}