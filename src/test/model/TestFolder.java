package model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

public class TestFolder extends TestNamedObject {
    Folder empty;
    Folder educationFolder;
    Folder cpscTwoTenFolder;
    Folder mathTwoHundredFolder;

    Set<Folder> educationSubfolders;

    File goalsFile;
    File personalProjectIdeasFile;
    File ceeZeroOneQuestionsFile;
    File mywebworkOneAnswersFile;
    File myWebworkTwoAnswersFile;

    Set<File> cpscTwoTenFiles;
    Set<File> mathTwoHundredFiles;

    @BeforeEach
    void runBefore() {
        namedObject = new Folder("name");

        empty = new Folder("Empty");
        educationFolder = new Folder("Education");
        educationFolder.makeSubfolder("CPSC 210");
        educationFolder.makeSubfolder("MATH 200");

        cpscTwoTenFolder = educationFolder.getSubfolder("CPSC 210");
        mathTwoHundredFolder = educationFolder.getSubfolder("MATH 200");

        educationSubfolders = new HashSet<Folder>();
        educationSubfolders.add(cpscTwoTenFolder);
        educationSubfolders.add(mathTwoHundredFolder);
        
        
        educationFolder.addFile("Goals", "C:\\");
        cpscTwoTenFolder.addFile("Personal Project Ideas", "C:\\210");
        cpscTwoTenFolder.addFile("C01 OH Questions", "C:\\210");
        mathTwoHundredFolder.addFile("My WeBWorK 1 Answers", "C:\\200");
        mathTwoHundredFolder.addFile("My WeBWorK 2 Answers", "C:\\200");
        
        goalsFile = educationFolder.getFile("Goals");
        personalProjectIdeasFile = cpscTwoTenFolder.getFile("Personal Project Ideas");
        ceeZeroOneQuestionsFile = cpscTwoTenFolder.getFile("C01 OH Questions");
        mywebworkOneAnswersFile = mathTwoHundredFolder.getFile("My WeBWorK 1 Answers");
        myWebworkTwoAnswersFile = mathTwoHundredFolder.getFile("My WeBWorK 2 Answers");
        
        makeSubfolderFileLists();
    }

    private void makeSubfolderFileLists() {
        cpscTwoTenFiles = new HashSet<File>();
        mathTwoHundredFiles = new HashSet<File>();
        cpscTwoTenFiles.add(personalProjectIdeasFile);
        cpscTwoTenFiles.add(ceeZeroOneQuestionsFile);
        mathTwoHundredFiles.add(mywebworkOneAnswersFile);
        mathTwoHundredFiles.add(myWebworkTwoAnswersFile);
    }

    @Test
    void testConstructor() {
        assertEquals(educationFolder, cpscTwoTenFolder.getParentFolder());
        assertEquals(cpscTwoTenFiles, cpscTwoTenFolder.containedFiles());
        assertEquals(educationFolder, mathTwoHundredFolder.getParentFolder());
        assertEquals(mathTwoHundredFiles, mathTwoHundredFolder.containedFiles());

        assertEquals(educationSubfolders, educationFolder.containedFolders());
    }

    @Test
    void testContainedFoldersEmpty() {
        assertTrue(empty.containedFolders().isEmpty());
    }
    
    @Test
    void testContainedFoldersContainsFiles() {
        assertTrue(cpscTwoTenFolder.containedFolders().isEmpty());
    }

    @Test
    void testContainedFoldersContainsFilesAndFolders() {
        assertEquals(educationSubfolders, educationFolder.containedFolders());
    }

    @Test
    void testContainedFoldersContainsFilesAndFoldersMultiple() {
        assertEquals(educationSubfolders, educationFolder.containedFolders());

        assertTrue(cpscTwoTenFolder.containedFolders().isEmpty());
    }

    @Test
    void testContainedFilesEmpty() {
        assertTrue(empty.containedFiles().isEmpty());
    }
    
    @Test
    void testContainedFilesContainsFiles() {
        assertEquals(cpscTwoTenFiles, cpscTwoTenFolder.containedFiles());
    }

    @Test
    void testContainedFilesContainsFilesAndFolders() {
        assertTrue(educationFolder.containedFiles().contains(goalsFile));
    }

    @Test
    void testContainedFilesContainsFilesAndFoldersMultiple() {
        assertTrue(educationFolder.containedFiles().contains(goalsFile));

        assertEquals(cpscTwoTenFiles, cpscTwoTenFolder.containedFiles());
    }

    @Test
    void testGetParentFolder() {
        assertEquals(educationFolder, cpscTwoTenFolder.getParentFolder());
    }

    @Test
    void testGetParentFolderMultiple() {
        assertEquals(educationFolder, cpscTwoTenFolder.getParentFolder());
        assertEquals(educationFolder, mathTwoHundredFolder.getParentFolder());
    }

    @Test
    void testMakeSubfolderIsEmpty() {
        Folder newFolder = empty.makeSubfolder("no longer empty");

        Set<Folder> foldersInEmpty = empty.containedFolders();
        
        assertFalse(foldersInEmpty.isEmpty());
        assertEquals("no longer empty", newFolder.getName());
        assertEquals(empty, newFolder.getParentFolder());
    }

    @Test
    void testMakeSubfolderHasFiles() {
        Folder newFolder = cpscTwoTenFolder.makeSubfolder("labs");

        Set<Folder> foldersInCompSciTwoTen = cpscTwoTenFolder.containedFolders();

        assertFalse(foldersInCompSciTwoTen.isEmpty());
        assertEquals("labs", newFolder.getName());
        assertEquals(cpscTwoTenFolder, newFolder.getParentFolder());
    }
    
    
    @Test
    void testMakeSubfolderHasFoldersAndFiles() {
        Folder newFolder = educationFolder.makeSubfolder("CPSC 213");

        Set<Folder> foldersInEducation = educationFolder.containedFolders();

        assertEquals(3, foldersInEducation.size());
        assertEquals("CPSC 213", newFolder.getName());
        assertEquals(educationFolder, newFolder.getParentFolder());
    }

    @Test
    void testMakeSubfolderMultiple() {
        Folder newCompSciFolder = cpscTwoTenFolder.makeSubfolder("lectures");
        Folder newMathFolder = mathTwoHundredFolder.makeSubfolder("midterms");

        Set<Folder> foldersInCompSciTwoTen = cpscTwoTenFolder.containedFolders();
        Set<Folder> foldersInMathTwoHundred = mathTwoHundredFolder.containedFolders();

        assertFalse(foldersInCompSciTwoTen.isEmpty());
        assertEquals("lectures", newCompSciFolder.getName());
        assertEquals(cpscTwoTenFolder, newCompSciFolder.getParentFolder());
        assertFalse(foldersInMathTwoHundred.isEmpty());
        assertEquals("midterms", newMathFolder.getName());
        assertEquals(mathTwoHundredFolder, newMathFolder.getParentFolder());
    }

    @Test
    void testGetSubfolder() {
        assertEquals(cpscTwoTenFolder, educationFolder.getSubfolder("CPSC 210"));
    }

    @Test
    void testGetSubfolderMultiple() {
        assertEquals(cpscTwoTenFolder, educationFolder.getSubfolder("CPSC 210"));
        assertEquals(mathTwoHundredFolder, educationFolder.getSubfolder("MATH 200"));
    }

    @Test
    void testGetSubfolderFail() {
        assertNull(educationFolder.getSubfolder("CPSC 200"));
    }

    @Test
    void testGetSubfolderMultipleSomeFail() {
        assertNull(educationFolder.getSubfolder("MATH 223"));
        assertEquals(cpscTwoTenFolder, educationFolder.getSubfolder("CPSC 210"));
        assertEquals(mathTwoHundredFolder, educationFolder.getSubfolder("MATH 200"));
    }

    @Test
    void testGetSubfolderSubSubFolder() {
        cpscTwoTenFolder.makeSubfolder("labs");
        assertEquals(cpscTwoTenFolder, educationFolder.getSubfolder("CPSC 210"));
        assertNull(educationFolder.getSubfolder("labs"));
    }

    @Test
    void testAddFileOneFolder() {
        assertEquals(cpscTwoTenFiles, cpscTwoTenFolder.containedFiles());
    }

    @Test
    void testAddFileMultipleFolders() {
        assertEquals(cpscTwoTenFiles, cpscTwoTenFolder.containedFiles());
        assertEquals(mathTwoHundredFiles, mathTwoHundredFolder.containedFiles());
    }

    @Test
    void testGetFileFail() {
        assertNull(empty.getFile("name"));

        assertNull(cpscTwoTenFolder.getFile("not in here"));
    }

    @Test
    void testGetFileSucceed() {
        assertEquals(personalProjectIdeasFile, cpscTwoTenFolder.getFile("Personal Project Ideas"));
        assertEquals(ceeZeroOneQuestionsFile, cpscTwoTenFolder.getFile("C01 OH Questions"));

        assertEquals(mywebworkOneAnswersFile, mathTwoHundredFolder.getFile("My WeBWorK 1 Answers"));
        assertEquals(myWebworkTwoAnswersFile, mathTwoHundredFolder.getFile("My WeBWorK 2 Answers"));

        assertEquals(goalsFile, educationFolder.getFile("Goals"));
    }
}
