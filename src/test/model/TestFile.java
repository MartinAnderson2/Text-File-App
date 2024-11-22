package model;

import model.exceptions.NameIsBlankException;
import model.exceptions.NameIsTakenException;
import model.exceptions.NoSuchFolderFoundException;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestFile extends TestNamedObject {
    Folder rootFolder;
    Folder educationFolder;

    File ceeSharpFile;
    File beeSLFile;
    File javaFile;
    File rustFile;

    Label programmingLanguageLabel;
    Label lowerLevelComputerScienceCourseLabel;
    
    @BeforeEach
    void runBefore() {
        try {
            rootFolder = new Folder("root");
            try {
                rootFolder.makeSubfolder("Education");
                educationFolder = rootFolder.getSubfolder("Education");
            } catch (NameIsTakenException | NoSuchFolderFoundException e) {
                fail("Folder method threw exception when it shouldn't have");
            }

            namedObject = new File("name", "C:\\Users\\You\\biography.txt", rootFolder);

            ceeSharpFile = new File("C#", "C:\\Users\\You\\repo\\C Sharp.txt", educationFolder);
            beeSLFile = new File("BSL", "C:\\Users\\You\\Documents\\Dr Racket Files\\lecture 2 notes.txt",
                    educationFolder);
            javaFile = new File("Java", "C:\\Users\\You\\.vscode\\specification.txt", educationFolder);

            rustFile = new File("Rust Language", "D:\\Rust's #1.txt", rootFolder);

            programmingLanguageLabel = new Label("Programming Language");
            lowerLevelComputerScienceCourseLabel = new Label("Taught in lower level computer science courses at UBC");
        } catch (NameIsBlankException e) {
            fail("NameIsEmptyException thrown when name was not empty");
        }
    }

    @Test
    void testConstructor() {
        assertEquals("C#", ceeSharpFile.getName());
        assertEquals("C:\\Users\\You\\repo\\C Sharp.txt", ceeSharpFile.getFilePath());
        assertEquals(educationFolder, ceeSharpFile.getParentFolder());
        assertEquals(0, ceeSharpFile.getNumLabels());
        
        assertEquals("Java", javaFile.getName());
        assertEquals("C:\\Users\\You\\.vscode\\specification.txt", javaFile.getFilePath());
        assertEquals(educationFolder, javaFile.getParentFolder());
        assertEquals(0, javaFile.getNumLabels());
        
        assertEquals("Rust Language", rustFile.getName());
        assertEquals("D:\\Rust's #1.txt", rustFile.getFilePath());
        assertEquals(rootFolder, rustFile.getParentFolder());
        assertEquals(0, rustFile.getNumLabels());

        File testFile = null;
        try {
            testFile = new File("", "E:\\Apps\\App.txt", rootFolder);
            fail("NameIsEmptyException not thrown when creating a File with an empty name");
        } catch (NameIsBlankException e) {
            assertNull(testFile);
        }
    }

    @Test
    void testSetFilepath() {
        ceeSharpFile.setFilePath("D:\\Project.txt");
        assertEquals("D:\\Project.txt", ceeSharpFile.getFilePath());
        beeSLFile.setFilePath("F:\\Proj.txt");
        assertEquals("F:\\Proj.txt", beeSLFile.getFilePath());
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
        assertFalse(ceeSharpFile.removeLabel(programmingLanguageLabel));

        assertEquals(0, ceeSharpFile.getNumLabels());
        assertFalse(ceeSharpFile.isLabelled(programmingLanguageLabel));
    }

    @Test
    void testRemoveOneLabelFileWithOneLabel() {
        beeSLFile.addLabel(programmingLanguageLabel);

        assertTrue(beeSLFile.removeLabel(programmingLanguageLabel));

        assertEquals(0, beeSLFile.getNumLabels());
        assertFalse(beeSLFile.isLabelled(programmingLanguageLabel));
    }

    @Test
    void testRemoveOneLabelFileWithOneDifferentLabel() {
        ceeSharpFile.addLabel(programmingLanguageLabel);
        
        assertFalse(ceeSharpFile.removeLabel(lowerLevelComputerScienceCourseLabel));

        assertEquals(1, ceeSharpFile.numberLabelsTaggedWith());
        assertTrue(ceeSharpFile.isLabelled(programmingLanguageLabel));
        assertFalse(ceeSharpFile.isLabelled(lowerLevelComputerScienceCourseLabel));
    }

    @Test
    void testRemoveMultipleLabelsFileWithOneLabel() {
        javaFile.addLabel(programmingLanguageLabel);

        assertTrue(javaFile.removeLabel(programmingLanguageLabel));
        assertFalse(javaFile.removeLabel(lowerLevelComputerScienceCourseLabel));

        assertEquals(0, javaFile.getNumLabels());
        assertFalse(javaFile.isLabelled(programmingLanguageLabel));
        assertFalse(javaFile.isLabelled(lowerLevelComputerScienceCourseLabel));
    }

    @Test
    void testRemoveMultipleLabelsFileWithOneDifferentLabel() {
        ceeSharpFile.addLabel(programmingLanguageLabel);
        ceeSharpFile.addLabel(lowerLevelComputerScienceCourseLabel);
        javaFile.addLabel(programmingLanguageLabel);
        javaFile.addLabel(lowerLevelComputerScienceCourseLabel);
        
        assertTrue(ceeSharpFile.removeLabel(lowerLevelComputerScienceCourseLabel));
        assertTrue(javaFile.removeLabel(programmingLanguageLabel));
        assertTrue(javaFile.removeLabel(lowerLevelComputerScienceCourseLabel));

        assertEquals(1, ceeSharpFile.numberLabelsTaggedWith());
        assertTrue(ceeSharpFile.isLabelled(programmingLanguageLabel));
        assertFalse(ceeSharpFile.isLabelled(lowerLevelComputerScienceCourseLabel));

        
        assertEquals(0, javaFile.getNumLabels());
        assertFalse(javaFile.isLabelled(programmingLanguageLabel));
        assertFalse(javaFile.isLabelled(lowerLevelComputerScienceCourseLabel));
    }

    @Test
    void testIsLabelledLabelParamLabelledNoLabels() {
        assertFalse(javaFile.isLabelled(programmingLanguageLabel));
        assertFalse(javaFile.isLabelled(lowerLevelComputerScienceCourseLabel));
    }

    @Test
    void testIsLabelledLabelParamOneLabelButNotOther() {
        ceeSharpFile.addLabel(programmingLanguageLabel);

        assertTrue(ceeSharpFile.isLabelled(programmingLanguageLabel));
        assertFalse(ceeSharpFile.isLabelled(lowerLevelComputerScienceCourseLabel));
    }

    @Test
    void testIsLabelledLabelParamMultipleLabelsAllThere() {
        beeSLFile.addLabel(programmingLanguageLabel);
        beeSLFile.addLabel(lowerLevelComputerScienceCourseLabel);

        assertTrue(beeSLFile.isLabelled(programmingLanguageLabel));
        assertTrue(beeSLFile.isLabelled(lowerLevelComputerScienceCourseLabel));
    }

    @Test
    void testIsLabelledLabelParamMultipleFiles() {
        beeSLFile.addLabel(programmingLanguageLabel);
        beeSLFile.addLabel(lowerLevelComputerScienceCourseLabel);
        rustFile.addLabel(programmingLanguageLabel);

        assertTrue(beeSLFile.isLabelled(programmingLanguageLabel));
        assertTrue(beeSLFile.isLabelled(lowerLevelComputerScienceCourseLabel));
        assertTrue(rustFile.isLabelled(programmingLanguageLabel));
        assertFalse(rustFile.isLabelled(lowerLevelComputerScienceCourseLabel));
    }

    @Test
    void testIsLabelledNoParamNotLabelled() {
        assertFalse(ceeSharpFile.isLabelled());
    }

    @Test
    void testIsLabelledNoParamOneLabel() {
        beeSLFile.addLabel(lowerLevelComputerScienceCourseLabel);

        assertTrue(beeSLFile.isLabelled());
    }

    @Test
    void testIsLabelledNoParamMultipleLabels() {
        rustFile.addLabel(programmingLanguageLabel);
        rustFile.addLabel(lowerLevelComputerScienceCourseLabel);
        
        assertTrue(rustFile.isLabelled());
    }

    @Test
    void testIsLabelledNoParamMultipleFilesMultipleLabels() {
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
        rustFile.addLabel(lowerLevelComputerScienceCourseLabel);

        assertEquals(1, rustFile.numberLabelsTaggedWith());
    }

    @Test
    void testNumberLabelsTaggedWithMultiple() {
        javaFile.addLabel(programmingLanguageLabel);
        javaFile.addLabel(lowerLevelComputerScienceCourseLabel);

        assertEquals(2, javaFile.numberLabelsTaggedWith());
    }

    @Test
    void testNumberLabelsTaggedWithMultipleFilesMultipleLabels() {
        beeSLFile.addLabel(programmingLanguageLabel);
        javaFile.addLabel(programmingLanguageLabel);
        javaFile.addLabel(lowerLevelComputerScienceCourseLabel);

        assertEquals(0, ceeSharpFile.numberLabelsTaggedWith());
        assertEquals(1, beeSLFile.numberLabelsTaggedWith());
        assertEquals(2, javaFile.numberLabelsTaggedWith());
    }

    @Test
    void testGetNameOfFileOnDiskNoParam() {
        assertEquals("C Sharp.txt", ceeSharpFile.getNameOfFileOnDisk());
        assertEquals("lecture 2 notes.txt", beeSLFile.getNameOfFileOnDisk());
        assertEquals("specification.txt", javaFile.getNameOfFileOnDisk());
        assertEquals("Rust's #1.txt", rustFile.getNameOfFileOnDisk());
    }

    @Test
    void testGetNameOfFileOnDiskStringParamBackslashes() {
        assertEquals("text.txt", File.getNameOfFileOnDisk("C:\\text.txt"));
        assertEquals("Files at my house's #1 backyard.txt",
                File.getNameOfFileOnDisk("D:\\Documents\\Files at my house's #1 backyard.txt"));
        assertEquals(" Yup", File.getNameOfFileOnDisk("\\ Yup"));
        assertEquals("Hi.a", File.getNameOfFileOnDisk("\\ Tees \\\\ \\ \\ a \\Hi.a"));
        assertEquals("", File.getNameOfFileOnDisk("\\ \\Nah\\"));
    }

    @Test
    void testGetNameOfFileOnDiskStringParamForwardslashes() {
        assertEquals("text.txt", File.getNameOfFileOnDisk("C:/text.txt"));
        assertEquals("Files at my house's #1 backyard.txt",
                File.getNameOfFileOnDisk("D:/Documents/Files at my house's #1 backyard.txt"));
        assertEquals(" Yup", File.getNameOfFileOnDisk("/ Yup"));
        assertEquals("Hi.a", File.getNameOfFileOnDisk("/ Tees // / / a /Hi.a"));
        assertEquals("", File.getNameOfFileOnDisk("/ /Nah/"));
    }

    @Test
    void testGetNameOfFileOnDiskStringParamBothSlashesAndNeither() {
        assertEquals("No slashes", File.getNameOfFileOnDisk("No slashes"));

        assertEquals("text.txt", File.getNameOfFileOnDisk("C:\\/text.txt"));
        assertEquals("Files at my house's #1 backyard.txt",
                File.getNameOfFileOnDisk("D:\\Documents/Files at my house's #1 backyard.txt"));
        assertEquals("Hi.a", File.getNameOfFileOnDisk("\\ Tees //\\ / \\ a \\Hi.a"));
        assertEquals("", File.getNameOfFileOnDisk("\\ /Nah\\"));
    }

    @Test
    void testGetNameOfFileOnDiskWithoutExtensionBackslashes() {
        assertEquals("text", File.getNameOfFileOnDiskWithoutExtension("C:\\text.txt"));
        assertEquals("Files at my house's #1 backyard",
                File.getNameOfFileOnDiskWithoutExtension("D:\\Documents\\Files at my house's #1 backyard.txt"));
        assertEquals("text.txt", File.getNameOfFileOnDiskWithoutExtension("C:\\text.txt.lnk"));
        assertEquals("text.", File.getNameOfFileOnDiskWithoutExtension("C:\\text.."));
        assertEquals("a.nm.jj..", File.getNameOfFileOnDiskWithoutExtension("D:\\hi\\a.nm.jj...p"));
        assertEquals(" Yup", File.getNameOfFileOnDiskWithoutExtension("\\ Yup"));
        assertEquals("Hi", File.getNameOfFileOnDiskWithoutExtension("\\ Tees \\\\ \\ \\ a \\Hi.a"));
        assertEquals("", File.getNameOfFileOnDiskWithoutExtension("\\ \\Nah\\"));
    }

    @Test
    void testGetNameOfFileOnDiskWithoutExtensionForwardslashes() {
        assertEquals("text", File.getNameOfFileOnDiskWithoutExtension("C:/text.txt"));
        assertEquals("Files at my house's #1 backyard",
                File.getNameOfFileOnDiskWithoutExtension("D:/Documents/Files at my house's #1 backyard.txt"));
        assertEquals("text.txt", File.getNameOfFileOnDiskWithoutExtension("C:/text.txt.lnk"));
        assertEquals("text.", File.getNameOfFileOnDiskWithoutExtension("C:/text.."));
        assertEquals("a.nm.jj..", File.getNameOfFileOnDiskWithoutExtension("D:/hi/a.nm.jj...p"));
        assertEquals(" Yup", File.getNameOfFileOnDiskWithoutExtension("/ Yup"));
        assertEquals("Hi", File.getNameOfFileOnDiskWithoutExtension("/ Tees // / / a /Hi.a"));
        assertEquals("", File.getNameOfFileOnDiskWithoutExtension("/ /Nah/"));
    }

    @Test
    void testGetNameOfFileOnDiskWithoutExtensionBothSlashesAndNeither() {
        assertEquals("No slashes", File.getNameOfFileOnDiskWithoutExtension("No slashes"));
        assertEquals("test", File.getNameOfFileOnDiskWithoutExtension("test.txt"));
        assertEquals("yay a file!", File.getNameOfFileOnDiskWithoutExtension("yay a file!.p"));

        assertEquals("text", File.getNameOfFileOnDiskWithoutExtension("C:\\/text.txt"));
        assertEquals("Files at my house's #1 backyard",
                File.getNameOfFileOnDiskWithoutExtension("D:\\Documents/Files at my house's #1 backyard.txt"));
        assertEquals("text.txt", File.getNameOfFileOnDiskWithoutExtension("C:\\text.txt.lnk"));
        assertEquals("text.", File.getNameOfFileOnDiskWithoutExtension("C:\\text.."));
        assertEquals("a.nm.jj..", File.getNameOfFileOnDiskWithoutExtension("D:\\hi\\a.nm.jj...p"));
        assertEquals("Hi", File.getNameOfFileOnDiskWithoutExtension("\\ Tees //\\ / \\ a \\Hi.a"));
        assertEquals("", File.getNameOfFileOnDiskWithoutExtension("\\ \\Nah\\"));
    }
}