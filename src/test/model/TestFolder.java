package model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.exceptions.NameIsBlankException;
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

            empty = new Folder("Empty");
            educationFolder = new Folder("Education");
            educationFolder.makeSubfolder("CPSC 210");
            cpscTwoTenFolder = educationFolder.getSubfolder("CPSC 210");
            educationFolder.makeSubfolder("MATH 200");
            mathTwoHundredFolder = educationFolder.getSubfolder("MATH 200");
        } catch (NoSuchFolderFoundException e) {
            fail("Folder created then immediately searched for and not found");
        } catch (NameIsBlankException e) {
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
        } catch (NameIsBlankException e) {
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
    @SuppressWarnings("methodlength")
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

        Folder testFolder = null;
        try {
            testFolder = new Folder("");
            fail("NameIsEmptyException not thrown when constructing Folder with empty name");
        } catch (NameIsBlankException e) {
            // expected
            assertNull(testFolder);
        }

        Folder rootFolder;
        try {
            rootFolder = new Folder("root");
        } catch (NameIsBlankException e) {
            fail("NameIsEmptyException thrown when name was not empty");
            return;
        }

        assertEquals("root", rootFolder.getName());
        assertTrue(rootFolder.getSubfolders().isEmpty());
        assertTrue(rootFolder.getSubfiles().isEmpty());
        try {
            rootFolder.getParentFolder();
            fail("Folder with no parent did not throw NoSuchFolderFoundException when getParentFolder() called");
        } catch (NoSuchFolderFoundException e) {
            // expected
        }
    }

    @Test
    void testSubfoldersEmpty() {
        assertTrue(empty.getSubfolders().isEmpty());
    }
    
    @Test
    void testSubfoldersContainsFiles() {
        assertTrue(cpscTwoTenFolder.getSubfolders().isEmpty());
    }

    @Test
    void testSubfoldersContainsFilesAndFolders() {
        assertEquals(educationSubfolders, educationFolder.getSubfolders());
    }

    @Test
    void testSubfoldersContainsFilesAndFoldersMultiple() {
        assertEquals(educationSubfolders, educationFolder.getSubfolders());

        assertTrue(cpscTwoTenFolder.getSubfolders().isEmpty());
    }

    @Test
    void testSubfilesEmpty() {
        assertTrue(empty.getSubfiles().isEmpty());
    }
    
    @Test
    void testSubfilesContainsFiles() {
        assertEquals(cpscTwoTenFiles, cpscTwoTenFolder.getSubfiles());
    }

    @Test
    void testSubfilesContainsFilesAndFolders() {
        assertTrue(educationFolder.getSubfiles().contains(goalsFile));
    }

    @Test
    void testSubfilesContainsFilesAndFoldersMultiple() {
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
            // expected
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
            // expected
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
        } catch (NameIsBlankException e) {
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
            // expected
        }
    }

    @Test
    void testGetSubfileFailEmptyAndOtherFiles() {
        try {
            empty.getSubfile("name");
            fail("Parent folder did not throw NoSuchFileFoundException when returning a file that doesn't exist");
        } catch (NoSuchFileFoundException e) {
            // expected
        }
        try {
            cpscTwoTenFolder.getSubfile("not in here");
            fail("Parent folder did not throw NoSuchFileFoundException when returning a file that doesn't exist");
        } catch (NoSuchFileFoundException e) {
            // expected
        }
    }

    @Test
    void testGetSubfileSucceed() {
        try {
            assertEquals(personalProjectIdeasFile, cpscTwoTenFolder.getSubfile("PerSonAl prOject Ideas"));
            assertEquals(ceeZeroOneQuestionsFile, cpscTwoTenFolder.getSubfile("C01 OH Questions"));

            assertEquals(mywebworkOneAnswersFile, mathTwoHundredFolder.getSubfile("My WeBWorK 1 Answers"));
            assertEquals(myWebworkTwoAnswersFile, mathTwoHundredFolder.getSubfile("My WeBWorK 2 Answers"));

            assertEquals(goalsFile, educationFolder.getSubfile("Goals"));
        } catch (NoSuchFileFoundException e) {
            fail("Parent folder does not have a reference to subfile");
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
        } catch (NameIsBlankException e) {
            fail("NameIsEmptyException thrown when name was not empty");
            // So compiler doesn't complain about newFolder not being assigned a value
            return;
        }

        Set<Folder> foldersInEmpty = empty.getSubfolders();
        
        assertEquals(1, foldersInEmpty.size());
        assertTrue(foldersInEmpty.contains(newFolder));
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
        } catch (NameIsBlankException e) {
            fail("NameIsEmptyException thrown when name was not empty");
            // So compiler doesn't complain about newFolder not being assigned a value
            return;
        }

        Set<Folder> foldersInCompSciTwoTen = cpscTwoTenFolder.getSubfolders();

        assertEquals(1, foldersInCompSciTwoTen.size());
        assertTrue(foldersInCompSciTwoTen.contains(newFolder));
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
        } catch (NameIsBlankException e) {
            fail("NameIsEmptyException thrown when name was not empty");
            // So compiler doesn't complain about newFolder not being assigned a value
            return;
        }

        Set<Folder> foldersInEducation = educationFolder.getSubfolders();

        assertEquals(3, foldersInEducation.size());
        assertTrue(foldersInEducation.contains(newFolder));
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
        } catch (NameIsBlankException e) {
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
        } catch (NameIsBlankException e) {
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
    void testMakeSubfolderNameIsEmpty() {
        try {
            educationFolder.makeSubfolder("");
            fail("No exception thrown when attempting to make a subfolder with an empty name");
        } catch (NameIsBlankException e) {
            // expected
        } catch (NameIsTakenException e) {
            fail("NameIsTakenException thrown when name was not taken");
            // So compiler doesn't complain about newFolder not being assigned a value
        }

        assertEquals(2, educationFolder.getSubfolders().size());

        try {
            cpscTwoTenFolder.makeSubfolder("");
            fail("No exception thrown when attempting to make a subfolder with an empty name");
        } catch (NameIsBlankException e) {
            // expected
        } catch (NameIsTakenException e) {
            fail("NameIsTakenException thrown when name was not taken");
        }

        assertEquals(0, cpscTwoTenFolder.getSubfolders().size());
    }

    @Test
    void testMakeSubfolderNameIsTaken() {
        try {
            educationFolder.makeSubfolder("cpSc 210");
            fail("No exception thrown when attempting to make a subfolder with a name that is taken");
        } catch (NameIsBlankException e) {
            fail("NameIsEmptyException thrown when name was not empty");
        } catch (NameIsTakenException e) {
            // expected
            assertEquals(cpscTwoTenFolder.getName(), e.getCapitalizationOfTakenName());
        }

        try {
            educationFolder.makeSubfolder(mathTwoHundredFolder.getName());
            fail("No exception thrown when attempting to make a subfolder with a name that is taken");
        } catch (NameIsBlankException e) {
            fail("NameIsEmptyException thrown when name was not empty");
        } catch (NameIsTakenException e) {
            // expected
            assertEquals(mathTwoHundredFolder.getName(), e.getCapitalizationOfTakenName());
        }

        assertEquals(2, educationFolder.getSubfolders().size());
    }

    @Test
    void testMakeSubfolderSameNameTwice() {
        try {
            cpscTwoTenFolder.makeSubfolder("Lectures");
            cpscTwoTenFolder.makeSubfolder("lECtureS");
            fail("No exception thrown when attempting to make a subfolder with a name that is taken");
        } catch (NameIsBlankException e) {
            fail("NameIsEmptyException thrown when name was not empty");
        } catch (NameIsTakenException e) {
            // expected
            assertEquals("Lectures", e.getCapitalizationOfTakenName());
        }

        assertEquals(1, cpscTwoTenFolder.getSubfolders().size());
    }

    @Test
    void testRemoveSubfolderIsEmpty() {
        try {
            empty.makeSubfolder("no longer empty");
        } catch (NameIsTakenException e) {
            fail("NameIsTakenException thrown when name was not taken");
            // So compiler doesn't complain about newFolder not being assigned a value
            return;
        } catch (NameIsBlankException e) {
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
        } catch (NameIsBlankException e) {
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
        } catch (NameIsBlankException e) {
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
        } catch (NameIsBlankException e) {
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
    void testRemoveSubfolderFail() {
        try {
            educationFolder.removeSubfolder("Yaah");
            fail("No exception thrown when trying to remove a subfolder that doesn't exist");
        } catch (NoSuchFolderFoundException e) {
            // expected
        }

        assertEquals(2, educationFolder.getSubfolders().size());

        try {
            cpscTwoTenFolder.removeSubfolder("education");
        } catch (NoSuchFolderFoundException e) {
            // expected
        }

        assertEquals(0, cpscTwoTenFolder.getSubfolders().size());
    }

    @Test
    void testHasSubfolderFail() {
        assertFalse(educationFolder.hasSubfolder("Yaah"));
        assertFalse(educationFolder.hasSubfolder("Heheha"));
        assertFalse(cpscTwoTenFolder.hasSubfolder("education"));
        assertFalse(cpscTwoTenFolder.hasSubfolder("CPSC 210"));
    }

    @Test
    void testHasSubfolderSucceed() {
        assertTrue(educationFolder.hasSubfolder("CPSC 210"));
        assertTrue(educationFolder.hasSubfolder("cpsc 210"));
        assertTrue(educationFolder.hasSubfolder("cPsC 210"));
        assertTrue(educationFolder.hasSubfolder("math 200"));
        assertTrue(educationFolder.hasSubfolder("Math 200"));
        assertTrue(educationFolder.hasSubfolder("MaTh 200"));
    }

    @Test
    void testMakeSubfileOneFolder() {
        assertEquals(cpscTwoTenFiles, cpscTwoTenFolder.getSubfiles());
    }

    @Test
    void testMakeSubfileMultipleFolders() {
        assertEquals(cpscTwoTenFiles, cpscTwoTenFolder.getSubfiles());
        assertEquals(mathTwoHundredFiles, mathTwoHundredFolder.getSubfiles());
    }

    @Test
    @SuppressWarnings("methodlength")
    void testMakeSubfileEmptyName() {
        try {
            empty.makeSubfile("", "C:\\Users\\User\\Documents\\");
            fail("No exception thrown when creating subfile with empty name");
        } catch (NameIsBlankException e) {
            // expected
        } catch (NameIsTakenException e) {
            fail("NameIsTakenException thrown when attempting to create subfile with empty name");
        }

        assertEquals(0, empty.getSubfiles().size());


        try {
            educationFolder.makeSubfile("", "C:\\Example");
            fail("No exception thrown when creating subfile with empty name");
        } catch (NameIsBlankException e) {
            // expected
        } catch (NameIsTakenException e) {
            fail("NameIsTakenException thrown when attempting to create subfile with empty name");
        }

        assertEquals(1, educationFolder.getSubfiles().size());

        try {
            mathTwoHundredFolder.makeSubfile("", "");
            fail("No exception thrown when creating subfile with empty name");
        } catch (NameIsBlankException e) {
            // expected
        } catch (NameIsTakenException e) {
            fail("NameIsTakenException thrown when attempting to create subfile with empty name");
        }
        
        assertEquals(2, mathTwoHundredFolder.getSubfiles().size());
    }

    @Test
    void testMakeSubfileNameIsTaken() {
        try {
            educationFolder.makeSubfile(goalsFile.getName(), "Path");
            fail("No exception thrown when creating subfile with name that is already taken");
        } catch (NameIsBlankException e) {
            fail("NameIsEmptyException thrown when creating subfile with name that is already taken");
        } catch (NameIsTakenException e) {
            // expected
        }

        assertEquals(1, educationFolder.getSubfiles().size());

        try {
            mathTwoHundredFolder.makeSubfile("my webwork 1 ANsWeRs", "");
            fail("No exception thrown when creating subfile with name that is already taken");
        } catch (NameIsBlankException e) {
            fail("NameIsEmptyException thrown when creating subfile with name that is already taken");
        } catch (NameIsTakenException e) {
            // expected
        }
        
        assertEquals(2, mathTwoHundredFolder.getSubfiles().size());
    }

    @Test
    @SuppressWarnings("methodlength")
    void testAddExistingSubfileMultiple() {
        try {
            educationFolder.addExistingSubfile(ceeZeroOneQuestionsFile);
        } catch (NameIsTakenException e) {
            fail("NameIsTakenException thrown when adding existing subfile to folder without a file with that name");
        }

        Set<File> educationFolderFiles = educationFolder.getSubfiles();
        assertEquals(2, educationFolderFiles.size());
        assertTrue(educationFolderFiles.contains(ceeZeroOneQuestionsFile));



        try {
            cpscTwoTenFolder.addExistingSubfile(myWebworkTwoAnswersFile);
        } catch (NameIsTakenException e) {
            fail("NameIsTakenException thrown when adding existing subfile to folder without a file with that name");
        }
        
        Set<File> cpscTwoTenFolderFiles = cpscTwoTenFolder.getSubfiles();
        assertEquals(3, cpscTwoTenFolderFiles.size());
        assertTrue(cpscTwoTenFolderFiles.contains(myWebworkTwoAnswersFile));



        try {
            mathTwoHundredFolder.addExistingSubfile(new File("WeBWorK Assignment 3", "", mathTwoHundredFolder));
        } catch (NameIsTakenException e) {
            fail("NameIsTakenException thrown when adding existing subfile to folder without a file with that name");
        } catch (NameIsBlankException e) {
            fail("new File() threw NameIsEmptyException when making a file with a non-empty name");
        }

        assertEquals(3, mathTwoHundredFolder.getSubfiles().size());
        assertTrue(mathTwoHundredFolder.hasSubfile("WeBWorK Assignment 3"));
    }

    @Test
    void testAddExistingSubfileFail() {
        try {
            educationFolder.addExistingSubfile(goalsFile);
            fail("NameIsTakenException not thrown when adding existing subfile to folder containing a file with that "
                    + "name");
        } catch (NameIsTakenException e) {
            // expected
        }
        assertEquals(1, educationFolder.getSubfiles().size());



        try {
            cpscTwoTenFolder.addExistingSubfile(new File("personal ProjEcT Ideas", "", cpscTwoTenFolder));
            fail("NameIsTakenException not thrown when adding existing subfile to folder containing a file with that "
                    + "name");
        } catch (NameIsTakenException e) {
            // expected
        } catch (NameIsBlankException e) {
            fail("new File() threw NameIsEmptyException when name was not empty");
        }
        assertEquals(1, educationFolder.getSubfiles().size());
    }

    @Test
    void testRemoveSubfile() {
        removeSubfileCreatedInRunBefore(educationFolder, goalsFile.getName());

        Set<File> filesInEducation = educationFolder.getSubfiles();

        assertEquals(0, filesInEducation.size());
        try {
            educationFolder.getSubfile("Goals");
            fail("Parent folder did not throw NoSuchFileFoundException when returning a file that was just deleted");
        } catch (NoSuchFileFoundException e) {
            // expected
        }
    }

    @Test
    void testRemoveSubfileMultiple() {
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
            mathTwoHundredFolder.getSubfile("mY webwork 2 aNSweRs");
            fail("Parent folder did not throw NoSuchFileFoundException when returning a file that was just deleted");
        } catch (NoSuchFileFoundException e) {
            // expected
        }

        try {
            assertEquals(personalProjectIdeasFile, cpscTwoTenFolder.getSubfile("Personal Project Ideas"));
        } catch (NoSuchFileFoundException e) {
            fail("Parent folder does not have a reference to subfile");
        }
    }

    @Test
    void testRemoveSubfileFail() {
        try {
            empty.removeSubfile("Test");
            fail("No exception thrown when trying to remove File that doesn't exist");
        } catch (NoSuchFileFoundException e) {
            // expected
        }
        assertEquals(0, empty.getSubfiles().size());
        
        
        try {
            educationFolder.removeSubfile("CPSC 210");
            fail("No exception thrown when trying to remove File that doesn't exist");
        } catch (NoSuchFileFoundException e) {
            // expected
        }
        assertEquals(1, educationFolder.getSubfiles().size());

        try {
            mathTwoHundredFolder.removeSubfile("C06 Problems");
            fail("No exception thrown when trying to remove File that doesn't exist");
        } catch (NoSuchFileFoundException e) {
            // expected
        }
        assertEquals(2, mathTwoHundredFolder.getSubfiles().size());

    }

    @Test
    void testHasSubfileSucceed() {
        assertTrue(educationFolder.hasSubfile("Goals"));
        assertTrue(cpscTwoTenFolder.hasSubfile(ceeZeroOneQuestionsFile.getName()));
        assertTrue(mathTwoHundredFolder.hasSubfile("my webworK 1 answeRS"));
    }

    @Test
    void testHasSubfileFail() {
        assertFalse(empty.hasSubfile("Goals"));
        assertFalse(educationFolder.hasSubfile(ceeZeroOneQuestionsFile.getName()));
        assertFalse(mathTwoHundredFolder.hasSubfile(ceeZeroOneQuestionsFile.getName()));
    }

    // Helper methods (for dealing with exceptions that shouldn't reasonable be thrown)

    // EFFECTS: returns folder named name from parent, fails if none exist
    private Folder getSubfolderJustCreated(Folder parent, String name) {
        try {
            return parent.getSubfolder(name);
        } catch (NoSuchFolderFoundException e) {
            fail("Subfolder just created not found");
        }
        // Won't run
        return null;
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
