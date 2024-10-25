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
        cpscTwoTenFolder = educationFolder.makeSubfolder("CPSC 210");
        mathTwoHundredFolder = educationFolder.makeSubfolder("MATH 200");

        educationSubfolders = new HashSet<Folder>();
        educationSubfolders.add(cpscTwoTenFolder);
        educationSubfolders.add(mathTwoHundredFolder);
        
        goalsFile = educationFolder.makeSubfile("Goals", "C:\\");
        personalProjectIdeasFile = cpscTwoTenFolder.makeSubfile("Personal Project Ideas", "C:\\210");
        ceeZeroOneQuestionsFile = cpscTwoTenFolder.makeSubfile("C01 OH Questions", "C:\\210");
        mywebworkOneAnswersFile = mathTwoHundredFolder.makeSubfile("My WeBWorK 1 Answers", "C:\\200");
        myWebworkTwoAnswersFile = mathTwoHundredFolder.makeSubfile("My WeBWorK 2 Answers", "C:\\200");
                
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
        assertEquals(cpscTwoTenFiles, cpscTwoTenFolder.getSubfiles());
        assertEquals(educationFolder, mathTwoHundredFolder.getParentFolder());
        assertEquals(mathTwoHundredFiles, mathTwoHundredFolder.getSubfiles());

        assertEquals(educationSubfolders, educationFolder.getSubfolders());
    }

    @Test
    void testContainedFoldersEmpty() {
        assertTrue(empty.getSubfolders().isEmpty());
    }
    
    @Test
    void testContainedFoldersContainsFiles() {
        assertTrue(cpscTwoTenFolder.getSubfolders().isEmpty());
    }

    @Test
    void testContainedFoldersContainsFilesAndFolders() {
        assertEquals(educationSubfolders, educationFolder.getSubfolders());
    }

    @Test
    void testContainedFoldersContainsFilesAndFoldersMultiple() {
        assertEquals(educationSubfolders, educationFolder.getSubfolders());

        assertTrue(cpscTwoTenFolder.getSubfolders().isEmpty());
    }

    @Test
    void testContainedFilesEmpty() {
        assertTrue(empty.getSubfiles().isEmpty());
    }
    
    @Test
    void testContainedFilesContainsFiles() {
        assertEquals(cpscTwoTenFiles, cpscTwoTenFolder.getSubfiles());
    }

    @Test
    void testContainedFilesContainsFilesAndFolders() {
        assertTrue(educationFolder.getSubfiles().contains(goalsFile));
    }

    @Test
    void testContainedFilesContainsFilesAndFoldersMultiple() {
        assertTrue(educationFolder.getSubfiles().contains(goalsFile));

        assertEquals(cpscTwoTenFiles, cpscTwoTenFolder.getSubfiles());
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

        Set<Folder> foldersInEmpty = empty.getSubfolders();
        
        assertFalse(foldersInEmpty.isEmpty());
        assertEquals("no longer empty", newFolder.getName());
        assertEquals(empty, newFolder.getParentFolder());
    }

    @Test
    void testMakeSubfolderHasFiles() {
        Folder newFolder = cpscTwoTenFolder.makeSubfolder("labs");

        Set<Folder> foldersInCompSciTwoTen = cpscTwoTenFolder.getSubfolders();

        assertFalse(foldersInCompSciTwoTen.isEmpty());
        assertEquals("labs", newFolder.getName());
        assertEquals(cpscTwoTenFolder, newFolder.getParentFolder());
    }
    
    
    @Test
    void testMakeSubfolderHasFoldersAndFiles() {
        Folder newFolder = educationFolder.makeSubfolder("CPSC 213");

        Set<Folder> foldersInEducation = educationFolder.getSubfolders();

        assertEquals(3, foldersInEducation.size());
        assertEquals("CPSC 213", newFolder.getName());
        assertEquals(educationFolder, newFolder.getParentFolder());
    }

    @Test
    void testMakeSubfolderMultiple() {
        Folder newCompSciFolder = cpscTwoTenFolder.makeSubfolder("lectures");
        Folder newMathFolder = mathTwoHundredFolder.makeSubfolder("midterms");

        Set<Folder> foldersInCompSciTwoTen = cpscTwoTenFolder.getSubfolders();
        Set<Folder> foldersInMathTwoHundred = mathTwoHundredFolder.getSubfolders();

        assertFalse(foldersInCompSciTwoTen.isEmpty());
        assertEquals("lectures", newCompSciFolder.getName());
        assertEquals(cpscTwoTenFolder, newCompSciFolder.getParentFolder());
        assertFalse(foldersInMathTwoHundred.isEmpty());
        assertEquals("midterms", newMathFolder.getName());
        assertEquals(mathTwoHundredFolder, newMathFolder.getParentFolder());
    }

    @Test
    void testRemoveSubfolderIsEmpty() {
        Folder newFolder = empty.makeSubfolder("no longer empty");

        empty.removeSubfolder(newFolder.getName());

        Set<Folder> foldersInEmpty = empty.getSubfolders();
        assertTrue(foldersInEmpty.isEmpty());
    }

    @Test
    void testRemoveSubfolderHasFiles() {
        Folder newFolder = cpscTwoTenFolder.makeSubfolder("labs");

        cpscTwoTenFolder.removeSubfolder(newFolder.getName());
        newFolder = null;

        Set<Folder> foldersInCompSciTwoTen = cpscTwoTenFolder.getSubfolders();
        assertTrue(foldersInCompSciTwoTen.isEmpty());
    }
    
    @Test
    void testRemoveSubfolderHasFoldersAndFiles() {
        Folder newFolder = educationFolder.makeSubfolder("CPSC 213");

        educationFolder.removeSubfolder(newFolder.getName());;

        Set<Folder> foldersInEducation = educationFolder.getSubfolders();
        assertEquals(2, foldersInEducation.size());
    }

    @Test
    void testRemoveSubfolderMultiple() {
        Folder newCompSciFolder = cpscTwoTenFolder.makeSubfolder("lectures");
        Folder newCompSciFolderTwo = cpscTwoTenFolder.makeSubfolder("labs");
        Folder newMathFolder = mathTwoHundredFolder.makeSubfolder("midterms");
        Folder newMathFolderTwo = mathTwoHundredFolder.makeSubfolder("practice problems");
        
        cpscTwoTenFolder.removeSubfolder(newCompSciFolder.getName());
        cpscTwoTenFolder.removeSubfolder(newCompSciFolderTwo.getName());
        
        mathTwoHundredFolder.removeSubfolder(newMathFolder.getName());

        Set<Folder> foldersInCompSciTwoTen = cpscTwoTenFolder.getSubfolders();
        Set<Folder> foldersInMathTwoHundred = mathTwoHundredFolder.getSubfolders();

        assertTrue(foldersInCompSciTwoTen.isEmpty());
        assertEquals(1, foldersInMathTwoHundred.size());
        assertEquals("practice problems", newMathFolderTwo.getName());
        assertEquals(mathTwoHundredFolder, newMathFolderTwo.getParentFolder());
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
        assertEquals(cpscTwoTenFiles, cpscTwoTenFolder.getSubfiles());
    }

    @Test
    void testAddFileMultipleFolders() {
        assertEquals(cpscTwoTenFiles, cpscTwoTenFolder.getSubfiles());
        assertEquals(mathTwoHundredFiles, mathTwoHundredFolder.getSubfiles());
    }

    @Test
    void testRemoveFile() {
        educationFolder.removeSubfile(goalsFile.getName());

        Set<File> filesInEducation = educationFolder.getSubfiles();

        assertEquals(0, filesInEducation.size());
        assertNull(educationFolder.getSubfolder("Goals"));
    }

    @Test
    void testRemoveFileMultiple() {
        cpscTwoTenFolder.removeSubfile(goalsFile.getName());
        cpscTwoTenFolder.removeSubfile(ceeZeroOneQuestionsFile.getName());
        mathTwoHundredFolder.removeSubfile(myWebworkTwoAnswersFile.getName());
        mathTwoHundredFolder.removeSubfile(mywebworkOneAnswersFile.getName());

        Set<File> filesInCompSciTwoTen = cpscTwoTenFolder.getSubfiles();
        Set<File> filesInMathTwoHundred = mathTwoHundredFolder.getSubfiles();

        assertNotEquals(cpscTwoTenFiles, cpscTwoTenFolder.getSubfiles());
        assertNotEquals(mathTwoHundredFiles, mathTwoHundredFolder.getSubfiles());
        assertEquals(1, filesInCompSciTwoTen.size());
        assertEquals(0, filesInMathTwoHundred.size());
        assertNull(cpscTwoTenFolder.getSubfile("Goals"));
        assertNull(cpscTwoTenFolder.getSubfile("C01 OH Questions"));
        assertNotNull(cpscTwoTenFolder.getSubfile("Personal Project Ideas"));
        assertNull(mathTwoHundredFolder.getSubfile("My WeBWorK 1 Answers"));
        assertNull(mathTwoHundredFolder.getSubfile("My WeBWorK 2 Answers"));
    }

    @Test
    void testGetFileFail() {
        assertNull(empty.getSubfile("name"));

        assertNull(cpscTwoTenFolder.getSubfile("not in here"));
    }

    @Test
    void testGetFileSucceed() {
        assertEquals(personalProjectIdeasFile, cpscTwoTenFolder.getSubfile("Personal Project Ideas"));
        assertEquals(ceeZeroOneQuestionsFile, cpscTwoTenFolder.getSubfile("C01 OH Questions"));

        assertEquals(mywebworkOneAnswersFile, mathTwoHundredFolder.getSubfile("My WeBWorK 1 Answers"));
        assertEquals(myWebworkTwoAnswersFile, mathTwoHundredFolder.getSubfile("My WeBWorK 2 Answers"));

        assertEquals(goalsFile, educationFolder.getSubfile("Goals"));
    }
}
