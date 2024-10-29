package model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.exceptions.NameIsEmptyException;
import model.exceptions.NameIsTakenException;
import model.exceptions.NoSuchFileFoundException;
import model.exceptions.NoSuchFolderFoundException;

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
        try {
            namedObject = new Folder("name");
        } catch (NameIsEmptyException e) {
            fail("NameIsEmptyException thrown when name was not empty");
        }

        try {
            empty = new Folder("Empty");
            educationFolder = new Folder("Education");
            educationFolder.makeSubfolder("CPSC 210");
            cpscTwoTenFolder = educationFolder.getSubfolder("CPSC 210");
            educationFolder.makeSubfolder("MATH 200");
            mathTwoHundredFolder = educationFolder.getSubfolder("MATH 200");
        } catch (NoSuchFolderFoundException e) {
            fail("Folder created then immediately searched for and not found");
        } catch (NameIsEmptyException e) {
            fail("NameIsEmptyException thrown when name was not empty");
        } catch (NameIsTakenException e) {
            fail("Empty folder threw NameIsTakenException");
        }

        educationSubfolders = new HashSet<Folder>();
        educationSubfolders.add(cpscTwoTenFolder);
        educationSubfolders.add(mathTwoHundredFolder);
        
        makeSubfiles();
                
        makeSubfolderFileLists();
    }

    // EFFECTS: makes all of the test files for each of the folders and stores a reference to them
    private void makeSubfiles() {
        try {
            educationFolder.makeSubfile("Goals", "C:\\");
            goalsFile = educationFolder.getSubfile("Goals");
            cpscTwoTenFolder.makeSubfile("Personal Project Ideas", "C:\\210");
            personalProjectIdeasFile = cpscTwoTenFolder.getSubfile("Personal Project Ideas");
            cpscTwoTenFolder.makeSubfile("C01 OH Questions", "C:\\210");
            ceeZeroOneQuestionsFile = cpscTwoTenFolder.getSubfile("C01 OH Questions");
            mathTwoHundredFolder.makeSubfile("My WeBWorK 1 Answers", "C:\\200");
            mywebworkOneAnswersFile = mathTwoHundredFolder.getSubfile("My WeBWorK 1 Answers");
            mathTwoHundredFolder.makeSubfile("My WeBWorK 2 Answers", "C:\\200");
            myWebworkTwoAnswersFile = mathTwoHundredFolder.getSubfile("My WeBWorK 2 Answers");
        } catch (NoSuchFileFoundException e) {
            fail("File created then immediately searched for and not found");
        } catch (NameIsTakenException e) {
            fail("renBefore method written incorrectly: attempted to create file with duplicate name "
                    + "(or method is wrong)");
        } catch (NameIsEmptyException e) {
            fail("NameIsEmptyException thrown when name was not empty");
        }
    }

    // EFFECTS: adds all of the files to file lists
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
        try {
            assertEquals(educationFolder, cpscTwoTenFolder.getParentFolder());
            assertEquals(educationFolder, mathTwoHundredFolder.getParentFolder());
        } catch (NoSuchFolderFoundException e) {
            fail("Newly-created subfolders do not have a parent");
        }
        assertEquals(cpscTwoTenFiles, cpscTwoTenFolder.getSubfiles());
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
        try {
            assertEquals(educationFolder, cpscTwoTenFolder.getParentFolder());
        } catch (NoSuchFolderFoundException e) {
            fail("Subfolder of folder does not have a parent");
        }
    }

    @Test
    void testGetParentFolderMultiple() {
        try {
            assertEquals(educationFolder, cpscTwoTenFolder.getParentFolder());
            assertEquals(educationFolder, mathTwoHundredFolder.getParentFolder());
        } catch (NoSuchFolderFoundException e) {
            fail("Subfolders of folder do not have a parent");
        }
    }

    @Test
    void testGetParentFolderNoParent() {
        try {
            educationFolder.getParentFolder();
            fail("Folder with no parent did not throw NoSuchFolderFoundException");
        } catch (NoSuchFolderFoundException e) {
            // Expected
        }
    }

    @Test
    void testMakeSubfolderIsEmpty() {
        Folder newFolder;
        try {
            empty.makeSubfolder("no longer empty");
            newFolder = empty.getSubfolder("no longer empty");
        } catch (NoSuchFolderFoundException e) {
            fail("Subfolder just created not found");
            // So compiler doesn't complain about newFolder not being assigned a value
            return;
        } catch (NameIsTakenException e) {
            fail("NameIsTakenException thrown when name was not taken");
            // So compiler doesn't complain about newFolder not being assigned a value
            return;
        } catch (NameIsEmptyException e) {
            fail("NameIsEmptyException thrown when name was not empty");
            // So compiler doesn't complain about newFolder not being assigned a value
            return;
        }

        Set<Folder> foldersInEmpty = empty.getSubfolders();
        
        assertFalse(foldersInEmpty.isEmpty());
        assertEquals("no longer empty", newFolder.getName());
        try {
            assertEquals(empty, newFolder.getParentFolder());
        } catch (NoSuchFolderFoundException e) {
            fail("Subfolder does not have a parent");
        }
    }

    @Test
    void testMakeSubfolderHasFiles() {
        Folder newFolder;
        try {
            cpscTwoTenFolder.makeSubfolder("labs");
            newFolder = cpscTwoTenFolder.getSubfolder("labs");
        } catch (NoSuchFolderFoundException e) {
            fail("Subfolder just created not found");
            // So compiler doesn't complain about newFolder not being assigned a value
            return;
        } catch (NameIsTakenException e) {
            fail("NameIsTakenException thrown when name was not taken");
            // So compiler doesn't complain about newFolder not being assigned a value
            return;
        } catch (NameIsEmptyException e) {
            fail("NameIsEmptyException thrown when name was not empty");
            // So compiler doesn't complain about newFolder not being assigned a value
            return;
        }

        Set<Folder> foldersInCompSciTwoTen = cpscTwoTenFolder.getSubfolders();

        assertFalse(foldersInCompSciTwoTen.isEmpty());
        assertEquals("labs", newFolder.getName());
        try {
            assertEquals(cpscTwoTenFolder, newFolder.getParentFolder());
        } catch (NoSuchFolderFoundException e) {
            fail("Subfolder does not have a parent");
        }
    }
    
    
    @Test
    void testMakeSubfolderHasFoldersAndFiles() {
        Folder newFolder;
        try {
            educationFolder.makeSubfolder("CPSC 213");
            newFolder = educationFolder.getSubfolder("CPSC 213");
        } catch (NoSuchFolderFoundException e) {
            fail("Subfolder just created not found");
            // So compiler doesn't complain about newFolder not being assigned a value
            return;
        } catch (NameIsTakenException e) {
            fail("NameIsTakenException thrown when name was not taken");
            // So compiler doesn't complain about newFolder not being assigned a value
            return;
        } catch (NameIsEmptyException e) {
            fail("NameIsEmptyException thrown when name was not empty");
            // So compiler doesn't complain about newFolder not being assigned a value
            return;
        }

        Set<Folder> foldersInEducation = educationFolder.getSubfolders();

        assertEquals(3, foldersInEducation.size());
        assertEquals("CPSC 213", newFolder.getName());
        try {
            assertEquals(educationFolder, newFolder.getParentFolder());
        } catch (NoSuchFolderFoundException e) {
            fail("Subfolder does not have a parent");
        }
    }

    @Test
    @SuppressWarnings("methodlength")
    void testMakeSubfolderMultiple() {
        Folder newCompSciFolder;
        try {
            cpscTwoTenFolder.makeSubfolder("lectures");
            newCompSciFolder = cpscTwoTenFolder.getSubfolder("lectures");
        } catch (NoSuchFolderFoundException e) {
            fail("Subfolder just created not found");
            // So compiler doesn't complain about newFolder not being assigned a value
            return;
        } catch (NameIsTakenException e) {
            fail("NameIsTakenException thrown when name was not taken");
            // So compiler doesn't complain about newFolder not being assigned a value
            return;
        } catch (NameIsEmptyException e) {
            fail("NameIsEmptyException thrown when name was not empty");
            // So compiler doesn't complain about newFolder not being assigned a value
            return;
        }

        Folder newMathFolder;
        try {
            mathTwoHundredFolder.makeSubfolder("midterms");
            newMathFolder = mathTwoHundredFolder.getSubfolder("midterms");
        } catch (NoSuchFolderFoundException e) {
            fail("Subfolder just created not found");
            // So compiler doesn't complain about newFolder not being assigned a value
            return;
        } catch (NameIsTakenException e) {
            fail("NameIsTakenException thrown when name was not taken");
            // So compiler doesn't complain about newFolder not being assigned a value
            return;
        } catch (NameIsEmptyException e) {
            fail("NameIsEmptyException thrown when name was not empty");
            // So compiler doesn't complain about newFolder not being assigned a value
            return;
        }

        Set<Folder> foldersInCompSciTwoTen = cpscTwoTenFolder.getSubfolders();
        Set<Folder> foldersInMathTwoHundred = mathTwoHundredFolder.getSubfolders();

        assertFalse(foldersInCompSciTwoTen.isEmpty());
        assertEquals("lectures", newCompSciFolder.getName());
        try {
            assertEquals(cpscTwoTenFolder, newCompSciFolder.getParentFolder());
        } catch (NoSuchFolderFoundException e) {
            fail("Subfolder does not have a parent");
        }
        assertFalse(foldersInMathTwoHundred.isEmpty());
        assertEquals("midterms", newMathFolder.getName());
        try {
            assertEquals(mathTwoHundredFolder, newMathFolder.getParentFolder());
        } catch (NoSuchFolderFoundException e) {
            fail("Subfolder does not have a parent");
        }
    }

    @Test
    void testRemoveSubfolderIsEmpty() {
        try {
            empty.makeSubfolder("no longer empty");
        } catch (NameIsTakenException e) {
            fail("NameIsTakenException thrown when name was not taken");
            // So compiler doesn't complain about newFolder not being assigned a value
            return;
        } catch (NameIsEmptyException e) {
            fail("NameIsEmptyException thrown when name was not empty");
            // So compiler doesn't complain about newFolder not being assigned a value
            return;
        }
        Folder newFolder = getSubfolderJustCreated(empty, "no longer empty");
        
        removeSubfolderJustCreated(empty, newFolder.getName());

        Set<Folder> foldersInEmpty = empty.getSubfolders();
        assertTrue(foldersInEmpty.isEmpty());
    }

    @Test
    void testRemoveSubfolderHasFiles() {
        try {
            cpscTwoTenFolder.makeSubfolder("labs");
        } catch (NameIsTakenException e) {
            fail("NameIsTakenException thrown when name was not taken");
            // So compiler doesn't complain about newFolder not being assigned a value
            return;
        } catch (NameIsEmptyException e) {
            fail("NameIsEmptyException thrown when name was not empty");
            // So compiler doesn't complain about newFolder not being assigned a value
            return;
        }
        Folder newFolder = getSubfolderJustCreated(cpscTwoTenFolder, "labs");

        removeSubfolderJustCreated(cpscTwoTenFolder, newFolder.getName());

        Set<Folder> foldersInCompSciTwoTen = cpscTwoTenFolder.getSubfolders();
        assertTrue(foldersInCompSciTwoTen.isEmpty());
    }
    
    @Test
    void testRemoveSubfolderHasFoldersAndFiles() {
        try {
            educationFolder.makeSubfolder("CPSC 213");
        } catch (NameIsTakenException e) {
            fail("NameIsTakenException thrown when name was not taken");
            // So compiler doesn't complain about newFolder not being assigned a value
            return;
        } catch (NameIsEmptyException e) {
            fail("NameIsEmptyException thrown when name was not empty");
            // So compiler doesn't complain about newFolder not being assigned a value
            return;
        }
        Folder newFolder = getSubfolderJustCreated(educationFolder, "CPSC 213");
        
        removeSubfolderJustCreated(educationFolder, newFolder.getName());

        Set<Folder> foldersInEducation = educationFolder.getSubfolders();
        assertEquals(2, foldersInEducation.size());
    }

    @Test
    @SuppressWarnings("methodlength")
    void testRemoveSubfolderMultiple() {
        Folder newCompSciFolder;
        Folder newCompSciFolderTwo;
        Folder newMathFolder;
        Folder newMathFolderTwo;
        try {
            cpscTwoTenFolder.makeSubfolder("lectures");
            newCompSciFolder = getSubfolderJustCreated(cpscTwoTenFolder, "lectures");
            cpscTwoTenFolder.makeSubfolder("labs");
            newCompSciFolderTwo = getSubfolderJustCreated(cpscTwoTenFolder, "labs");
            mathTwoHundredFolder.makeSubfolder("midterms");
            newMathFolder = getSubfolderJustCreated(mathTwoHundredFolder, "midterms");
            mathTwoHundredFolder.makeSubfolder("practice problems");
            newMathFolderTwo = getSubfolderJustCreated(mathTwoHundredFolder, "practice problems");
        } catch (NameIsTakenException e) {
            fail("NameIsTakenException thrown when name was not taken");
            // So compiler doesn't complain about newFolder not being assigned a value
            return;
        } catch (NameIsEmptyException e) {
            fail("NameIsEmptyException thrown when name was not empty");
            // So compiler doesn't complain about newFolder not being assigned a value
            return;
        }
        
        removeSubfolderJustCreated(cpscTwoTenFolder, newCompSciFolder.getName());
        removeSubfolderJustCreated(cpscTwoTenFolder, newCompSciFolderTwo.getName());
        removeSubfolderJustCreated(mathTwoHundredFolder, newMathFolder.getName());

        Set<Folder> foldersInCompSciTwoTen = cpscTwoTenFolder.getSubfolders();
        Set<Folder> foldersInMathTwoHundred = mathTwoHundredFolder.getSubfolders();

        assertTrue(foldersInCompSciTwoTen.isEmpty());
        assertEquals(1, foldersInMathTwoHundred.size());
        assertEquals("practice problems", newMathFolderTwo.getName());
        try {
            assertEquals(mathTwoHundredFolder, newMathFolderTwo.getParentFolder());
        } catch (NoSuchFolderFoundException e) {
            fail("Subfolder doesn't have a parent");
        }
    }

    @Test
    void testGetSubfolder() {
        try {
            assertEquals(cpscTwoTenFolder, educationFolder.getSubfolder("CPSC 210"));
        } catch (NoSuchFolderFoundException e) {
            fail("Parent folder does not have a reference to subfolder");
        }
    }

    @Test
    void testGetSubfolderMultiple() {
        try {
            assertEquals(cpscTwoTenFolder, educationFolder.getSubfolder("CPSC 210"));
            assertEquals(mathTwoHundredFolder, educationFolder.getSubfolder("MATH 200"));
        } catch (NoSuchFolderFoundException e) {
            fail("Parent folder does not have a reference to subfolder");
        }
    }

    @Test
    void testGetSubfolderFail() {
        try {
            educationFolder.getSubfolder("CPSC 200");
            fail("Parent folder did not throw NoSuchFolderFoundException when it did not have a folder");
        } catch (NoSuchFolderFoundException e) {
            // Expected
        }
    }

    @Test
    void testGetSubfolderMultipleSomeFail() {
        try {
            educationFolder.getSubfolder("MATH 223");
            fail("Parent folder did not throw NoSuchFolderFoundException when it did not have a folder");
        } catch (NoSuchFolderFoundException e) {
            // Expcted
        }
        try {
            assertEquals(cpscTwoTenFolder, educationFolder.getSubfolder("CPSC 210"));
            assertEquals(mathTwoHundredFolder, educationFolder.getSubfolder("MATH 200"));
        } catch (NoSuchFolderFoundException e) {
            fail("Parent folder does not have a reference to subfolder");
        }
    }

    @Test
    void testGetSubfolderSubSubFolder() {
        try {
            cpscTwoTenFolder.makeSubfolder("labs");
        } catch (NameIsTakenException e) {
            fail("NameIsTakenException thrown when name was not taken");
            // So compiler doesn't complain about newFolder not being assigned a value
            return;
        } catch (NameIsEmptyException e) {
            fail("NameIsEmptyException thrown when name was not empty");
            // So compiler doesn't complain about newFolder not being assigned a value
            return;
        }

        Folder labsFolder = getSubfolderJustCreated(cpscTwoTenFolder, "labs");
        try {
            assertEquals(cpscTwoTenFolder, educationFolder.getSubfolder("CPSC 210"));
            assertEquals(labsFolder, cpscTwoTenFolder.getSubfolder("labs"));
        } catch (NoSuchFolderFoundException e) {
            fail("Parent folder does not have a reference to subfolder");
        }
        try {
            educationFolder.getSubfolder("labs");
            fail("Parent folder did not throw NoSuchFolderFoundException when it did not have a folder");
        } catch (NoSuchFolderFoundException e) {
            // Expected
        }
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
        removeSubfileCreatedInRunBefore(educationFolder, goalsFile.getName());

        Set<File> filesInEducation = educationFolder.getSubfiles();

        assertEquals(0, filesInEducation.size());
        try {
            educationFolder.getSubfolder("Goals");
            fail("Parent folder did not throw NoSuchFileFoundException when returning a file that was just deleted");
        } catch (NoSuchFolderFoundException e) {
            // Expected
        }
    }

    @Test
    void testRemoveFileMultiple() {
        removeSubfileCreatedInRunBefore(educationFolder, goalsFile.getName());
        removeSubfileCreatedInRunBefore(cpscTwoTenFolder, ceeZeroOneQuestionsFile.getName());
        removeSubfileCreatedInRunBefore(mathTwoHundredFolder, myWebworkTwoAnswersFile.getName());
        removeSubfileCreatedInRunBefore(mathTwoHundredFolder, mywebworkOneAnswersFile.getName());

        Set<File> filesInCompSciTwoTen = cpscTwoTenFolder.getSubfiles();
        Set<File> filesInMathTwoHundred = mathTwoHundredFolder.getSubfiles();

        assertNotEquals(cpscTwoTenFiles, cpscTwoTenFolder.getSubfiles());
        assertNotEquals(mathTwoHundredFiles, mathTwoHundredFolder.getSubfiles());
        assertEquals(1, filesInCompSciTwoTen.size());
        assertEquals(0, filesInMathTwoHundred.size());

        try {
            cpscTwoTenFolder.getSubfile("Goals");
            cpscTwoTenFolder.getSubfile("C01 OH Questions");
            mathTwoHundredFolder.getSubfile("My WeBWorK 1 Answers");
            mathTwoHundredFolder.getSubfile("My WeBWorK 2 Answers");
            fail("Parent folder did not throw NoSuchFileFoundException when returning a file that was just deleted");
        } catch (NoSuchFileFoundException e) {
            // Expected
        }

        try {
            assertEquals(personalProjectIdeasFile, cpscTwoTenFolder.getSubfile("Personal Project Ideas"));
        } catch (NoSuchFileFoundException e) {
            fail("Parent folder does not have a reference to subfile");
        }
    }

    @Test
    void testGetFileFail() {
        try {
            empty.getSubfile("name");
            fail("Parent folder did not throw NoSuchFileFoundException when returning a file that doesn't exist");
        } catch (NoSuchFileFoundException e) {
            // Expected
        }
        try {
            cpscTwoTenFolder.getSubfile("not in here");
            fail("Parent folder did not throw NoSuchFileFoundException when returning a file that doesn't exist");
        } catch (NoSuchFileFoundException e) {
            // Expected
        }
    }

    @Test
    void testGetFileSucceed() {
        try {
            assertEquals(personalProjectIdeasFile, cpscTwoTenFolder.getSubfile("Personal Project Ideas"));
            assertEquals(ceeZeroOneQuestionsFile, cpscTwoTenFolder.getSubfile("C01 OH Questions"));

            assertEquals(mywebworkOneAnswersFile, mathTwoHundredFolder.getSubfile("My WeBWorK 1 Answers"));
            assertEquals(myWebworkTwoAnswersFile, mathTwoHundredFolder.getSubfile("My WeBWorK 2 Answers"));

            assertEquals(goalsFile, educationFolder.getSubfile("Goals"));
        } catch (NoSuchFileFoundException e) {
            fail("Parent folder does not have a reference to subfile");
        }
    }

    // EFFECTS: returns folder named name from parent, fails if none exist
    private Folder getSubfolderJustCreated(Folder parent, String name) {
        try {
            return parent.getSubfolder(name);
        } catch (NoSuchFolderFoundException e) {
            fail("Subfolder just created not found");
            // Won't run
            return null;
        }
    }

    // MODIFIES: parent
    // EFFECTS: deletes folder named name from parent, fails if none exist
    private void removeSubfolderJustCreated(Folder parent, String name) {
        try {
            parent.removeSubfolder(name);
        } catch (NoSuchFolderFoundException e) {
            fail("Subfolder just created not found");
        }
    }

    // MODIFIES: parent
    // EFFECTS: deletes file named name from parent, fails if none exist
    private void removeSubfileCreatedInRunBefore(Folder parent, String name) {
        try {
            parent.removeSubfile(name);
        } catch (NoSuchFileFoundException e) {
            fail("Subfile created in runBefore() not found");
        }
    }
}
