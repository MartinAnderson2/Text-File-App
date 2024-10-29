package model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.exceptions.NameIsEmptyException;

public class TestFile extends TestNamedObject {
    Folder rootFolder;

    File ceeSharpFile;
    File beeSLFile;
    File javaFile;

    Label programmingLanguageLabel;
    Label lowerLevelComputerScienceCourseLabel;
    
    @BeforeEach
    void runBefore() {
        try {
            rootFolder = new Folder("root");

            namedObject = new File("name", "C:\\Users\\You\\biography.txt", rootFolder);

            ceeSharpFile = new File("C#", "C:\\Users\\You\\repo\\C Sharp.txt", rootFolder);
            beeSLFile = new File("BSL", "C:\\Users\\You\\Documents\\Dr Racket Files\\lecture 2 notes.txt", rootFolder);
            javaFile = new File("Java", "C:\\Users\\You\\.vscode\\specification.txt", rootFolder);

            programmingLanguageLabel = new Label("Programming Language");
            lowerLevelComputerScienceCourseLabel = new Label("Taught in lower level computer science courses at UBC");
        } catch (NameIsEmptyException e) {
            fail("NameIsEmptyException thrown when name was not empty");
        }
    }

    @Test
    void testConstructor() {
        assertEquals("C:\\Users\\You\\repo\\C Sharp.txt", ceeSharpFile.getFilePath());
        assertFalse(ceeSharpFile.isLabelled());
        assertEquals("C:\\Users\\You\\.vscode\\specification.txt", javaFile.getFilePath());
        assertFalse(javaFile.isLabelled());
    }

    @Test
    void testAddLabelOneLabel() {
        ceeSharpFile.addLabel(programmingLanguageLabel);

        assertEquals(1, ceeSharpFile.numberLabelsTaggedWith());
        assertTrue(ceeSharpFile.isLabelled(programmingLanguageLabel));
    }

    @Test
    void testAddLabelMultipleLabels() {
        beeSLFile.addLabel(programmingLanguageLabel);
        beeSLFile.addLabel(lowerLevelComputerScienceCourseLabel);

        assertEquals(2, beeSLFile.numberLabelsTaggedWith());
        assertTrue(beeSLFile.isLabelled(programmingLanguageLabel));
        assertTrue(beeSLFile.isLabelled(lowerLevelComputerScienceCourseLabel));
    }

    @Test
    void testAddLabelOneLabelMultipleFiles() {
        ceeSharpFile.addLabel(programmingLanguageLabel);
        javaFile.addLabel(programmingLanguageLabel);

        assertEquals(1, ceeSharpFile.numberLabelsTaggedWith());
        assertTrue(ceeSharpFile.isLabelled(programmingLanguageLabel));
        assertEquals(1, javaFile.numberLabelsTaggedWith());
        assertTrue(javaFile.isLabelled(programmingLanguageLabel));
    }

    @Test
    void testAddLabelMultilpleLabelsMultipleFiles() {
        ceeSharpFile.addLabel(programmingLanguageLabel);
        javaFile.addLabel(programmingLanguageLabel);
        javaFile.addLabel(lowerLevelComputerScienceCourseLabel);

        assertEquals(1, ceeSharpFile.numberLabelsTaggedWith());
        assertTrue(ceeSharpFile.isLabelled(programmingLanguageLabel));
        assertEquals(2, javaFile.numberLabelsTaggedWith());
        assertTrue(javaFile.isLabelled(programmingLanguageLabel));
        assertTrue(javaFile.isLabelled(lowerLevelComputerScienceCourseLabel));
    }

    @Test
    void testRemoveLabelNoLabels() {
        ceeSharpFile.removeLabel(programmingLanguageLabel);

        assertFalse(ceeSharpFile.isLabelled());
        assertFalse(ceeSharpFile.isLabelled(programmingLanguageLabel));
    }

    @Test
    void testRemoveOneLabelFileWithOneLabel() {
        beeSLFile.addLabel(programmingLanguageLabel);

        beeSLFile.removeLabel(programmingLanguageLabel);

        assertFalse(beeSLFile.isLabelled());
        assertFalse(beeSLFile.isLabelled(programmingLanguageLabel));
    }

    @Test
    void testRemoveOneLabelFileWithOneDifferentLabel() {
        ceeSharpFile.addLabel(programmingLanguageLabel);
        
        ceeSharpFile.removeLabel(lowerLevelComputerScienceCourseLabel);

        assertEquals(1, ceeSharpFile.numberLabelsTaggedWith());
        assertTrue(ceeSharpFile.isLabelled(programmingLanguageLabel));
        assertFalse(ceeSharpFile.isLabelled(lowerLevelComputerScienceCourseLabel));
    }

    @Test
    void testRemoveMultipleLabelsFileWithOneLabel() {
        javaFile.addLabel(programmingLanguageLabel);

        javaFile.removeLabel(programmingLanguageLabel);
        javaFile.removeLabel(lowerLevelComputerScienceCourseLabel);

        assertFalse(javaFile.isLabelled());
        assertFalse(javaFile.isLabelled(programmingLanguageLabel));
    }

    @Test
    void testRemoveMultipleLabelsFileWithOneDifferentLabel() {
        ceeSharpFile.addLabel(programmingLanguageLabel);
        ceeSharpFile.addLabel(lowerLevelComputerScienceCourseLabel);
        javaFile.addLabel(lowerLevelComputerScienceCourseLabel);
        javaFile.addLabel(programmingLanguageLabel);
        
        ceeSharpFile.removeLabel(lowerLevelComputerScienceCourseLabel);
        javaFile.removeLabel(programmingLanguageLabel);
        javaFile.removeLabel(lowerLevelComputerScienceCourseLabel);

        assertEquals(1, ceeSharpFile.numberLabelsTaggedWith());
        assertTrue(ceeSharpFile.isLabelled(programmingLanguageLabel));
        assertFalse(ceeSharpFile.isLabelled(lowerLevelComputerScienceCourseLabel));

        
        assertFalse(javaFile.isLabelled());
        assertFalse(javaFile.isLabelled(programmingLanguageLabel));
        assertFalse(javaFile.isLabelled(lowerLevelComputerScienceCourseLabel));
    }

    @Test
    void testIsLabelledLabelVersionTaggedNoLabels() {
        assertFalse(javaFile.isLabelled(programmingLanguageLabel));
        assertFalse(javaFile.isLabelled(lowerLevelComputerScienceCourseLabel));
    }

    @Test
    void testIsLabelledLabelVersionOneLabelButNotOther() {
        ceeSharpFile.addLabel(programmingLanguageLabel);

        assertTrue(ceeSharpFile.isLabelled(programmingLanguageLabel));
        assertFalse(ceeSharpFile.isLabelled(lowerLevelComputerScienceCourseLabel));
    }

    @Test
    void testIsLabelledLabelVersionMultipleLabelsAllThere() {
        beeSLFile.addLabel(programmingLanguageLabel);
        beeSLFile.addLabel(lowerLevelComputerScienceCourseLabel);

        assertTrue(beeSLFile.isLabelled(programmingLanguageLabel));
        assertTrue(beeSLFile.isLabelled(lowerLevelComputerScienceCourseLabel));
    }

    @Test
    void testIsLabelledLabelVersionMultipleFiles() {
        beeSLFile.addLabel(programmingLanguageLabel);
        beeSLFile.addLabel(lowerLevelComputerScienceCourseLabel);
        ceeSharpFile.addLabel(programmingLanguageLabel);

        assertTrue(beeSLFile.isLabelled(programmingLanguageLabel));
        assertTrue(beeSLFile.isLabelled(lowerLevelComputerScienceCourseLabel));
        assertTrue(beeSLFile.isLabelled(programmingLanguageLabel));
        assertTrue(beeSLFile.isLabelled(lowerLevelComputerScienceCourseLabel));
    }

    @Test
    void testIsLabelledVoidVersionNotLabelled() {
        assertFalse(ceeSharpFile.isLabelled());
    }

    @Test
    void testIsLabelledVoidVersionOneLabel() {
        beeSLFile.addLabel(lowerLevelComputerScienceCourseLabel);

        assertTrue(beeSLFile.isLabelled());
    }

    @Test
    void testIsLabelledVoidVersionMultipleLabels() {
        javaFile.addLabel(programmingLanguageLabel);
        javaFile.addLabel(lowerLevelComputerScienceCourseLabel);
        
        assertTrue(javaFile.isLabelled());
    }

    @Test
    void testIsLabelledVoidVersionMultipleFilesMultipleLabels() {
        ceeSharpFile.addLabel(programmingLanguageLabel);
        javaFile.addLabel(programmingLanguageLabel);
        javaFile.addLabel(lowerLevelComputerScienceCourseLabel);
        
        assertTrue(ceeSharpFile.isLabelled());
        assertTrue(javaFile.isLabelled());
        assertFalse(beeSLFile.isLabelled());
    }

    @Test
    void testNumberLabelsTaggedWithZero() {
        assertEquals(0, ceeSharpFile.numberLabelsTaggedWith());
    }

    @Test
    void testNumberLabelsTaggedWithOne() {
        beeSLFile.addLabel(lowerLevelComputerScienceCourseLabel);

        assertEquals(1, beeSLFile.numberLabelsTaggedWith());
    }

    @Test
    void testNumberLabelsTaggedWithMultiple() {
        javaFile.addLabel(programmingLanguageLabel);
        javaFile.addLabel(lowerLevelComputerScienceCourseLabel);

        assertEquals(2, javaFile.numberLabelsTaggedWith());
    }

    @Test
    void testNumberLabelsTaggedWithMultipleFilesMultipleLabels() {
        ceeSharpFile.addLabel(programmingLanguageLabel);
        javaFile.addLabel(programmingLanguageLabel);
        javaFile.addLabel(lowerLevelComputerScienceCourseLabel);

        assertEquals(1, ceeSharpFile.numberLabelsTaggedWith());
        assertEquals(2, javaFile.numberLabelsTaggedWith());
        assertEquals(0, beeSLFile.numberLabelsTaggedWith());
    }

    @Test
    void testSetFilepath() {
        ceeSharpFile.setFilePath("D:\\Project.txt");
        assertEquals("D:\\Project.txt", ceeSharpFile.getFilePath());
        beeSLFile.setFilePath("F:\\Proj.txt");
        assertEquals("F:\\Proj.txt", beeSLFile.getFilePath());
    }
}
