package model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.exceptions.*;
import persistence.JsonReader;

import java.io.IOException;
import java.util.List;

public class TestFileSystem {
    // A file that is valid on your computer in order to be able to test the open file and related functions
    private static final String VALID_FILE_PATH = "data\\test\\File for testing.txt";

    FileSystem emptyFileSystem;
    FileSystem fileSystem;

    @BeforeEach
    @SuppressWarnings("methodlength")
    void runBefore() {
        emptyFileSystem = new FileSystem();

        fileSystem = new FileSystem();
        try {
            fileSystem.createFile("File", "C:\\");

            fileSystem.createLabel("School");
            fileSystem.createLabel("Personal Project");

            fileSystem.createFolder("Education");
            fileSystem.createFolder("Hobbies");

            fileSystem.openFolder("Education");
            fileSystem.createFile("test", "invalid path");
            fileSystem.labelFile("test", "School");
            fileSystem.createFolder("CPSC 210");
            
            fileSystem.openFolder("CPSC 210");
            fileSystem.createFile("Personal Project Ideas", "D:\\Users\\User\\Documents\\Personal Project Ideas.txt");
            fileSystem.labelFile("Personal Project Ideas", "School");
            fileSystem.labelFile("Personal Project Ideas", "Personal Project");
            fileSystem.createFile("A", "A, eh");
            fileSystem.labelFile("A", "School");

            fileSystem.openRootFolder();
        } catch (NameIsTakenException | NoSuchFolderFoundException | NoSuchFileFoundException
                | NoSuchLabelFoundException e) {
            // Shouldn't happen
            fail();
        }

        java.io.File testFile = new java.io.File(VALID_FILE_PATH);
        try {
            testFile.createNewFile();
        } catch (IOException e) {
            fail(".txt file needed for tests failed to be created. You can manually create one as long as it is named"
                    + VALID_FILE_PATH);
        }
    }

    @Test
    void testConstructor() {
        testEmptyFileSystemConstruction();
        
        testFileSystemConstruction();
    }

    private void testEmptyFileSystemConstruction() {
        assertEquals("root", emptyFileSystem.getCurrentFolderName());
        assertFalse(emptyFileSystem.currentFolderHasParent());

        assertFalse(emptyFileSystem.anyLabelsExist());
        assertEquals(0, emptyFileSystem.getNumLabels());

        assertTrue(emptyFileSystem.getNamesOfRecentlyOpenedFiles().isEmpty());
        assertTrue(emptyFileSystem.getNamesOfRecentlyOpenedFolders().isEmpty());
        assertTrue(emptyFileSystem.getNamesOfRecentlyOpenedLabels().isEmpty());
    }

    @SuppressWarnings("methodlength")
    private void testFileSystemConstruction() {
        assertEquals("root", fileSystem.getCurrentFolderName());
        assertFalse(fileSystem.currentFolderHasParent());

        assertTrue(fileSystem.anyLabelsExist());
        assertEquals(2, fileSystem.getNumLabels());

        try {
            assertEquals(0, fileSystem.getNumLabelsOnFile("File"));
        } catch (NoSuchFileFoundException e) {
            fail();
        }

        List<String> recentlyOpenedFolders = fileSystem.getNamesOfRecentlyOpenedFolders();
        assertTrue(fileSystem.getNamesOfRecentlyOpenedFiles().isEmpty());
        assertEquals(2, recentlyOpenedFolders.size());
        assertEquals("CPSC 210", recentlyOpenedFolders.get(0));
        assertEquals("Education", recentlyOpenedFolders.get(1));
        assertTrue(fileSystem.getNamesOfRecentlyOpenedLabels().isEmpty());


        openFolderFailIfFailed("Education");
        assertEquals("Education", fileSystem.getCurrentFolderName());
        assertTrue(fileSystem.currentFolderHasParent());
        try {
            assertEquals(1, fileSystem.getNumLabelsOnFile("test"));
            assertTrue(fileSystem.fileLabelled("test", "School"));
        } catch (NoSuchFileFoundException | NoSuchLabelFoundException e) {
            fail();
        }

        openFolderFailIfFailed("CPSC 210");
        assertEquals("CPSC 210", fileSystem.getCurrentFolderName());
        assertTrue(fileSystem.currentFolderHasParent());
        try {
            assertEquals(2, fileSystem.getNumLabelsOnFile("Personal Project Ideas"));
            assertTrue(fileSystem.fileLabelled("Personal Project Ideas", "School"));
            assertTrue(fileSystem.fileLabelled("Personal Project Ideas", "Personal Project"));
            assertEquals(1, fileSystem.getNumLabelsOnFile("A"));
            assertTrue(fileSystem.fileLabelled("A", "School"));
        } catch (NoSuchFileFoundException | NoSuchLabelFoundException e) {
            fail();
        }

        fileSystem.openRootFolder();
        openFolderFailIfFailed("Hobbies");
        assertEquals("Hobbies", fileSystem.getCurrentFolderName());
        assertTrue(fileSystem.currentFolderHasParent());

        recentlyOpenedFolders = fileSystem.getNamesOfRecentlyOpenedFolders();
        assertEquals(3, recentlyOpenedFolders.size());
        assertEquals("Hobbies", recentlyOpenedFolders.get(0));
        assertEquals("CPSC 210", recentlyOpenedFolders.get(1));
        assertEquals("Education", recentlyOpenedFolders.get(2));
    }

    @Test
    void testGetCurrentFolderNameRoot() {
        assertEquals("root", emptyFileSystem.getCurrentFolderName());
        assertEquals("root", fileSystem.getCurrentFolderName());
    }

    @Test
    void testGetCurrentFolderNameOpenFolders() {
        openFolderFailIfFailed("Education");
        assertEquals("Education", fileSystem.getCurrentFolderName());
        
        openFolderFailIfFailed("CPSC 210");
        assertEquals("CPSC 210", fileSystem.getCurrentFolderName());

        fileSystem.openRootFolder();
        openFolderFailIfFailed("Hobbies");
        assertEquals("Hobbies", fileSystem.getCurrentFolderName());
    }

    @Test
    void testGetParentFolderNameFail() {
        String parentName = null;
        try {
            parentName = emptyFileSystem.getParentFolderName();
            fail("NoSuchFolderFoundException not thrown when trying to get parent folder when at root");
        } catch (NoSuchFolderFoundException e) {
            // expected
        }
        assertNull(parentName);

        try {
            parentName = fileSystem.getParentFolderName();
            fail("NoSuchFolderFoundException not thrown when trying to get parent folder when at root");
        } catch (NoSuchFolderFoundException e) {
            // expected
        }
        assertNull(parentName);
    }

    @Test
    void testGetParentFolderNameSucceed() {
        try {
            fileSystem.openFolder("Education");
            assertEquals("root", fileSystem.getParentFolderName());
            
            fileSystem.openFolder("CPSC 210");
            assertEquals("Education", fileSystem.getParentFolderName());
        } catch (NoSuchFolderFoundException e) {
            fail("NoSuchFolderFoundException thrown when trying to get parent of non-root folder");
        }

        fileSystem.openRootFolder();
        try {
            fileSystem.openFolder("Hobbies");
            assertEquals("root", fileSystem.getParentFolderName());
        } catch (NoSuchFolderFoundException e) {
            fail("NoSuchFolderFoundException thrown when trying to get parent of non-root folder");
        }
    }


    /* 
     *  Tests for File Methods:
     */

    @Test
    @SuppressWarnings("methodlength")
    void testCreateFileAllGood() {
        try {
            fileSystem.createFile("A File", "C:\\Documents\\file.txt");
            fileSystem.createFile("A", "A");
        } catch (NameIsTakenException e) {
            fail();
        }
        List<String> subfileNames = fileSystem.getNamesOfSubfiles();
        assertEquals(3, subfileNames.size());
        assertTrue(subfileNames.contains("A File"));
        assertTrue(subfileNames.contains("A"));
        assertEquals("C:\\Documents\\file.txt", getFilePathFailIfFailed("A File"));
        assertEquals("A", getFilePathFailIfFailed("A"));

        openFolderFailIfFailed("Hobbies");

        try {
            fileSystem.createFile("DnD", "C:\\Documents\\my character sheet.txt");
            fileSystem.createFile("MtG", "D:\\Documents\\my deck.txt");
        } catch (NameIsTakenException e) {
            fail();
        }
        subfileNames = fileSystem.getNamesOfSubfiles();
        assertEquals(2, subfileNames.size());
        assertTrue(subfileNames.contains("DnD"));
        assertTrue(subfileNames.contains("MtG"));
        assertEquals("C:\\Documents\\my character sheet.txt", getFilePathFailIfFailed("DnD"));
        assertEquals("D:\\Documents\\my deck.txt", getFilePathFailIfFailed("MtG"));
    }

    @Test
    @SuppressWarnings("methodlength")
    void testCreateFileNameIsEmpty() {
        try {
            emptyFileSystem.createFile("", "path");
            fail("NameIsBlankException not thrown when name was empty");
        } catch (NameIsBlankException e) {
            // expected
        } catch (NameIsTakenException e) {
            fail("NameIsTakenException thrown when name was empty");
        }

        try {
            emptyFileSystem.createFile("", "");
            fail("NameIsBlankException not thrown when name was empty");
        } catch (NameIsBlankException e) {
            // expected
        } catch (NameIsTakenException e) {
            fail("NameIsTakenException thrown when name was empty");
        }

        assertTrue(emptyFileSystem.getNamesOfSubfiles().isEmpty());

        try {
            fileSystem.createFile("", "");
            fail("NameIsBlankException not thrown when name was empty");
        } catch (NameIsBlankException e) {
            // expected
        } catch (NameIsTakenException e) {
            fail("NameIsTakenException thrown when name was empty");
        }

        List<String> subfileNames = fileSystem.getNamesOfSubfiles();
        assertEquals(1, subfileNames.size());
        assertFalse(subfileNames.contains(""));
    }

    @Test
    @SuppressWarnings("methodlength")
    void testCreateFileNameIsTaken() {
        try {
            fileSystem.createFile("File", "C:\\");
            fail("NameIsTakenException not thrown when name was taken");
        } catch (NameIsTakenException e) {
            // expected
            assertEquals("File", e.getCapitalizationOfTakenName());
        }
        List<String> subfileNames = fileSystem.getNamesOfSubfiles();
        assertEquals(1, subfileNames.size());

        try {
            fileSystem.createFile("fiLe", "path");
            fail("NameIsTakenException not thrown when name was taken");
        } catch (NameIsTakenException e) {
            // expected
            assertEquals("File", e.getCapitalizationOfTakenName());
        }
        subfileNames = fileSystem.getNamesOfSubfiles();
        assertEquals(1, subfileNames.size());
        assertNotEquals("path", getFilePathFailIfFailed("File"));

        openFolderFailIfFailed("Education");
        try {
            fileSystem.createFile("file", "path");
            fileSystem.createFile("File", "other path");
            fail("NameIsTakenException not thrown when name was taken");
        } catch (NameIsTakenException e) {
            // expected
            assertEquals("file", e.getCapitalizationOfTakenName());
        }
        subfileNames = fileSystem.getNamesOfSubfiles();
        assertEquals(2, subfileNames.size());
        assertTrue(subfileNames.contains("file"));
        assertEquals("path", getFilePathFailIfFailed("File"));
    }

    @Test
    @SuppressWarnings("methodlength")
    void testOpenFilePathValid() {
        try {
            fileSystem.createFile("F", VALID_FILE_PATH);
            fileSystem.openFile("f");
            fileSystem.openFile("F");
        } catch (NameIsTakenException | NoSuchFileFoundException e) {
            fail();
        } catch (FilePathNoLongerValidException e) {
            fail("VALID_FILE_PATH is not valid, please change it (or implementation is wrong)");
        }
        List<String> recentlyOpenedFiles = fileSystem.getNamesOfRecentlyOpenedFiles();
        assertEquals(1, recentlyOpenedFiles.size());
        assertTrue(recentlyOpenedFiles.contains("F"));

        openFolderFailIfFailed("Hobbies");

        try {
            fileSystem.createFile("Albatross", VALID_FILE_PATH);
            fileSystem.openFile("Albatross");
        } catch (NameIsTakenException | NoSuchFileFoundException e) {
            fail();
        } catch (FilePathNoLongerValidException e) {
            fail("VALID_FILE_PATH is not valid, please change it (or implementation is wrong)");
        }
        recentlyOpenedFiles = fileSystem.getNamesOfRecentlyOpenedFiles();
        assertEquals(2, recentlyOpenedFiles.size());
        assertEquals("Albatross", recentlyOpenedFiles.get(0));
        assertEquals("F", recentlyOpenedFiles.get(1));

        try {
            fileSystem.createFile("F", VALID_FILE_PATH);
            fileSystem.openFile("F");
        } catch (NameIsTakenException | NoSuchFileFoundException e) {
            fail();
        } catch (FilePathNoLongerValidException e) {
            fail("VALID_FILE_PATH is not valid, please change it (or implementation is wrong)");
        }
        recentlyOpenedFiles = fileSystem.getNamesOfRecentlyOpenedFiles();
        assertEquals(3, recentlyOpenedFiles.size());
        assertEquals("F", recentlyOpenedFiles.get(0));
        assertEquals("Albatross", recentlyOpenedFiles.get(1));
        assertEquals("F", recentlyOpenedFiles.get(2));
        // File should also have opened in computer's default .txt editor (only once in total, thankfully)
    }

    @Test
    @SuppressWarnings("methodlength")
    void testOpenFileMoreThanTenRecents() {
        try {
            createNumberedFiles();
        } catch (NameIsTakenException e) {
            fail();
        }

        assertTrue(fileSystem.getNamesOfRecentlyOpenedFiles().isEmpty());

        try {
            fileSystem.openFile("1");
            fileSystem.openFile("2");
            fileSystem.openFile("3");
            fileSystem.openFile("4");
            fileSystem.openFile("5");
            fileSystem.openFile("6");
            fileSystem.openFile("7");
            fileSystem.openFile("8");
            fileSystem.openFile("9");
            fileSystem.openFile("10");
            fileSystem.openFile("11");
        } catch (NoSuchFileFoundException e) {
            fail();
        } catch (FilePathNoLongerValidException e) {
            fail("VALID_FILE_PATH is not valid, please change it (or implementation is wrong)");
        }

        List<String> recentlyOpenedFiles = fileSystem.getNamesOfRecentlyOpenedFiles();
        assertEquals(10, recentlyOpenedFiles.size());
        assertEquals("11", recentlyOpenedFiles.get(0));
        assertEquals("10", recentlyOpenedFiles.get(1));
        assertEquals("9", recentlyOpenedFiles.get(2));
        assertEquals("8", recentlyOpenedFiles.get(3));
        assertEquals("7", recentlyOpenedFiles.get(4));
        assertEquals("6", recentlyOpenedFiles.get(5));
        assertEquals("5", recentlyOpenedFiles.get(6));
        assertEquals("4", recentlyOpenedFiles.get(7));
        assertEquals("3", recentlyOpenedFiles.get(8));
        assertEquals("2", recentlyOpenedFiles.get(9));
    }

    @Test
    void testOpenFilePathInvalid() {
        try {
            fileSystem.openFolder("Education");
            fileSystem.openFile("test");
            fail("Trying to open file with invalid path not throwing exception");
        } catch (NoSuchFolderFoundException | NoSuchFileFoundException e) {
            fail();
        } catch (FilePathNoLongerValidException e) {
            // expected
        }
        assertTrue(fileSystem.getNamesOfRecentlyOpenedFiles().isEmpty());
    }

    @Test
    @SuppressWarnings("methodlength")
    void testOpenFilePathInitiallyValidButInvalidWhenOpening() {
        String extensionWithNoDefaultApplication = ".386";
        java.io.File newFile = new java.io.File("File for testing IO Exception" + extensionWithNoDefaultApplication);
        try {
            try {
                newFile.createNewFile();
            } catch (IOException e) {
                fail();
            }

            try {
                fileSystem.createFile("New File", newFile.getPath());
            } catch (NameIsTakenException e) {
                fail();
            }

            try {
                fileSystem.openFile("New File");
                fail("You have a default application set for the .386 file extension. Change "
                        + "extensionWithNoDefaultApplication to a real file extension for which you do not have a "
                        + "default app set");
            } catch (NoSuchFileFoundException e) {
                fail();
            } catch (FilePathNoLongerValidException e) {
                // expected
            }

            assertTrue(fileSystem.getNamesOfRecentlyOpenedFiles().isEmpty());
        } finally {
            newFile.delete();
        }
    }

    @Test
    void testOpenFileNoFileWithThatName() {
        try {
            fileSystem.openFile("F");
            fail("Trying to open non-existent File not throwing exception");
        } catch (NoSuchFileFoundException e) {
            // expected
        } catch (FilePathNoLongerValidException e) {
            fail();
        }

        try {
            fileSystem.openFolder("Education");
            fileSystem.openFile("File");
            fail("Trying to open non-existent File not throwing exception");
        } catch (NoSuchFileFoundException e) {
            // expected
        } catch (NoSuchFolderFoundException | FilePathNoLongerValidException e) {
            fail();
        }

        assertTrue(fileSystem.getNamesOfRecentlyOpenedFiles().isEmpty());
    }

    @Test
    @SuppressWarnings("methodlength")
    void testOpenFilePathValidSomeRecents() {
        try {
            fileSystem.createFile("F", VALID_FILE_PATH);
            fileSystem.openFile("f");
            fileSystem.openFile("F");
        } catch (NameIsTakenException | NoSuchFileFoundException e) {
            fail();
        } catch (FilePathNoLongerValidException e) {
            fail("VALID_FILE_PATH is not valid, please change it (or implementation is wrong)");
        }
        List<String> recentlyOpenedFiles = fileSystem.getNamesOfRecentlyOpenedFiles();
        assertEquals(1, recentlyOpenedFiles.size());
        assertTrue(recentlyOpenedFiles.contains("F"));

        fileSystem.stopKeepingTrackOfRecents();
        openFolderFailIfFailed("Hobbies");
        try {
            fileSystem.createFile("Albatross", VALID_FILE_PATH);
            fileSystem.openFile("Albatross");
        } catch (NameIsTakenException | NoSuchFileFoundException e) {
            fail();
        } catch (FilePathNoLongerValidException e) {
            fail("VALID_FILE_PATH is not valid, please change it (or implementation is wrong)");
        }
        recentlyOpenedFiles = fileSystem.getNamesOfRecentlyOpenedFiles();
        assertEquals(1, recentlyOpenedFiles.size());
        assertTrue(recentlyOpenedFiles.contains("F"));

        fileSystem.startKeepingTrackOfRecents();

        try {
            fileSystem.createFile("F", VALID_FILE_PATH);
            fileSystem.openFile("F");
        } catch (NameIsTakenException | NoSuchFileFoundException e) {
            fail();
        } catch (FilePathNoLongerValidException e) {
            fail("VALID_FILE_PATH is not valid, please change it (or implementation is wrong)");
        }
        recentlyOpenedFiles = fileSystem.getNamesOfRecentlyOpenedFiles();
        assertEquals(2, recentlyOpenedFiles.size());
        assertEquals("F", recentlyOpenedFiles.get(0));
        assertEquals("F", recentlyOpenedFiles.get(1));
        // File should also have opened in computer's default .txt editor (only once in total, thankfully)
    }

    @Test
    @SuppressWarnings("methodlength")
    void testOpenFilePathValidNoRecents() {
        fileSystem.stopKeepingTrackOfRecents();
        try {
            fileSystem.createFile("F", VALID_FILE_PATH);
            fileSystem.openFile("f");
            fileSystem.openFile("F");
        } catch (NameIsTakenException | NoSuchFileFoundException e) {
            fail();
        } catch (FilePathNoLongerValidException e) {
            fail("VALID_FILE_PATH is not valid, please change it (or implementation is wrong)");
        }
        assertTrue(fileSystem.getNamesOfRecentlyOpenedFiles().isEmpty());

        openFolderFailIfFailed("Hobbies");
        try {
            fileSystem.createFile("Albatross", VALID_FILE_PATH);
            fileSystem.openFile("Albatross");
        } catch (NameIsTakenException | NoSuchFileFoundException e) {
            fail();
        } catch (FilePathNoLongerValidException e) {
            fail("VALID_FILE_PATH is not valid, please change it (or implementation is wrong)");
        }
        assertTrue(fileSystem.getNamesOfRecentlyOpenedFiles().isEmpty());


        try {
            fileSystem.createFile("F", VALID_FILE_PATH);
            fileSystem.openFile("F");
        } catch (NameIsTakenException | NoSuchFileFoundException e) {
            fail();
        } catch (FilePathNoLongerValidException e) {
            fail("VALID_FILE_PATH is not valid, please change it (or implementation is wrong)");
        }
        assertTrue(fileSystem.getNamesOfRecentlyOpenedFiles().isEmpty());

        // File should also have opened in computer's default .txt editor (only once in total (unless closed part of
        // the way through), thankfully)
    }

    @Test
    @SuppressWarnings("methodlength")
    void testOpenFileMoreThanTenRecentsNoRecents() {
        fileSystem.stopKeepingTrackOfRecents();
        try {
            createNumberedFiles();
        } catch (NameIsTakenException e) {
            fail();
        }
        assertTrue(fileSystem.getNamesOfRecentlyOpenedFiles().isEmpty());

        try {
            fileSystem.openFile("1");
            fileSystem.openFile("2");
            fileSystem.openFile("3");
            fileSystem.openFile("4");
            fileSystem.openFile("5");
            fileSystem.openFile("6");
            fileSystem.openFile("7");
            fileSystem.openFile("8");
            fileSystem.openFile("9");
            fileSystem.openFile("10");
            fileSystem.openFile("11");
        } catch (NoSuchFileFoundException e) {
            fail();
        } catch (FilePathNoLongerValidException e) {
            fail("VALID_FILE_PATH is not valid, please change it (or implementation is wrong)");
        }
        assertTrue(fileSystem.getNamesOfRecentlyOpenedFiles().isEmpty());
    }

    @Test
    void testDeleteFileValid() {
        try {
            fileSystem.deleteFile("File");
        } catch (NoSuchFileFoundException e) {
            fail("NoSuchFileFoundException when File exists");
        }
        assertTrue(fileSystem.getNamesOfSubfiles().isEmpty());

        openFolderFailIfFailed("Education");
        try {
            fileSystem.deleteFile("TEST");
        } catch (NoSuchFileFoundException e) {
            fail("NoSuchFileFoundException when File exists");
        }
        assertTrue(fileSystem.getNamesOfSubfiles().isEmpty());

        openFolderFailIfFailed("CPSC 210");
        try {
            fileSystem.deleteFile("Personal Project Ideas");
        } catch (NoSuchFileFoundException e) {
            fail("NoSuchFileFoundException when File exists");
        }
        assertEquals(1, fileSystem.getNamesOfSubfiles().size());
    }

    @Test
    void testDeleteFileInLabel() {
        try {
            fileSystem.openLabel("School");
        } catch (NoSuchLabelFoundException e) {
            fail();
        }

        try {
            fileSystem.deleteFile("test");
            fileSystem.deleteFile("Personal Project Ideas");
        } catch (NoSuchFileFoundException e) {
            fail("NoSuchFileFoundException when File exists");
        }

        fileSystem.openRootFolder();

        openFolderFailIfFailed("Education");
        assertTrue(fileSystem.getNamesOfSubfiles().isEmpty());

        openFolderFailIfFailed("CPSC 210");
        List<String> cpscTwoTenFileNames = fileSystem.getNamesOfSubfiles();
        assertEquals(1, cpscTwoTenFileNames.size());
        assertTrue(cpscTwoTenFileNames.contains("A"));
        try {
            assertTrue(fileSystem.fileLabelled("A", "School"));
        } catch (NoSuchFileFoundException | NoSuchLabelFoundException e) {
            fail();
        }
    }

    @Test
    void testDeleteFileFail() {
        try {
            fileSystem.deleteFile("Hello World!");
            fail("No exception thrown when deleting non-existent File");
        } catch (NoSuchFileFoundException e) {
            // expected
        }
        try {
            fileSystem.deleteFile("What's up?");
            fail("No exception thrown when deleting non-existent File");
        } catch (NoSuchFileFoundException e) {
            // expected
        }
        assertEquals(1, fileSystem.getNamesOfSubfiles().size());
    }

    @Test
    void testSetFileNameSucceed() {
        try {
            fileSystem.setFileName("File", "Non-silly file name");
        } catch (NoSuchFileFoundException | NameIsTakenException e) {
            fail();
        }
        assertFalse(fileSystem.containsFile("File"));
        assertTrue(fileSystem.containsFile("non-silly fIle name"));

        openFolderFailIfFailed("Education");
        try {
            fileSystem.setFileName("test", "Future Plans");
        } catch (NoSuchFileFoundException | NameIsTakenException e) {
            fail();
        }
        assertFalse(fileSystem.containsFile("test"));
        assertTrue(fileSystem.containsFile("Future Plans"));
    }

    @Test
    void testSetFileNameNoneExist() {
        try {
            fileSystem.setFileName("This file doesn't exist", "Name");
            fail();
        } catch (NoSuchFileFoundException e) {
            // expected
        } catch (NameIsTakenException e) {
            fail();
        }
        List<String> subfileNames = fileSystem.getNamesOfSubfiles();
        assertEquals(1, subfileNames.size());
        assertTrue(subfileNames.contains("File"));

        openFolderFailIfFailed("Education");
        try {
            fileSystem.setFileName("Real file for sure", "Filename");
            fail();
        } catch (NoSuchFileFoundException e) {
            // expected
        } catch (NameIsTakenException e) {
            fail();
        }
        subfileNames = fileSystem.getNamesOfSubfiles();
        assertEquals(1, subfileNames.size());
        assertTrue(subfileNames.contains("test"));
    }

    @Test
    void testSetFileNameNameIsTaken() {
        openFolderFailIfFailed("Education");
        openFolderFailIfFailed("CPSC 210");

        try {
            fileSystem.setFileName("A", "Personal Project Ideas");
            fail();
        } catch (NameIsTakenException e) {
            // expected
            assertEquals("Personal Project Ideas", e.getCapitalizationOfTakenName());
        } catch (NoSuchFileFoundException e) {
            fail();
        }
        List<String> subfileNames = fileSystem.getNamesOfSubfiles();
        assertEquals(2, subfileNames.size());
        assertTrue(subfileNames.contains("A"));
        assertTrue(subfileNames.contains("Personal Project Ideas"));
    }

    @Test
    void testSetFileNameTakenAndNotFound() {
        try {
            fileSystem.setFileName("This file doesn't exist", "file");
            fail();
        } catch (NameIsTakenException e) {
            // expected
            assertEquals("File", e.getCapitalizationOfTakenName());
        } catch (NoSuchFileFoundException e) {
            // NameIsTakenException should be thrown first
            fail();
        }
        List<String> subfileNames = fileSystem.getNamesOfSubfiles();
        assertEquals(1, subfileNames.size());
        assertTrue(subfileNames.contains("File"));
    }

    @Test
    void testSetFileNameEmptyAndNotFound() {
        try {
            fileSystem.setFileName("", "");
            fail();
        } catch (NameIsTakenException e) {
            fail();
        } catch (NoSuchFileFoundException e) {
            // expected
        }
        List<String> subfileNames = fileSystem.getNamesOfSubfiles();
        assertEquals(1, subfileNames.size());
        assertTrue(subfileNames.contains("File"));
    }

    @Test
    void testSetFileNameTakenAndEmpty() {
        try {
            fileSystem.setFileName("", "file");
            fail();
        } catch (NoSuchFileFoundException e) {
            // NameIsTakenException should be thrown first
            fail();
        } catch (NameIsTakenException e) {
            // expected
            assertEquals("File", e.getCapitalizationOfTakenName());
        }
        List<String> subfileNames = fileSystem.getNamesOfSubfiles();
        assertEquals(1, subfileNames.size());
        assertTrue(subfileNames.contains("File"));
    }

    @Test
    void testSetFileNameNewNameEmpty() {
        try {
            fileSystem.setFileName("File", "");
            fail();
        } catch (NameIsTakenException | NoSuchFileFoundException e) {
            fail();
        } catch (NameIsBlankException e) {
            // expected
        }
        List<String> subfileNames = fileSystem.getNamesOfSubfiles();
        assertEquals(1, subfileNames.size());
        assertTrue(subfileNames.contains("File"));
    }

    @Test
    void testGetFilePathSuccess() {
        try {
            assertEquals("C:\\", fileSystem.getFilePath("File"));
            assertEquals("C:\\", fileSystem.getFilePath("fiLe"));
        } catch (NoSuchFileFoundException e) {
            fail();
        }

        openFolderFailIfFailed("Education");
        try {
            assertEquals("invalid path", fileSystem.getFilePath("test"));
        } catch (NoSuchFileFoundException e) {
            fail();
        }
    }

    @Test
    void testGetFilePathFail() {
        try {
            fileSystem.getFilePath("Hello World!");
            fail();
        } catch (NoSuchFileFoundException e) {
            // expected
        }

        openFolderFailIfFailed("Education");
        try {
            fileSystem.getFilePath("File");
            fail();
        } catch (NoSuchFileFoundException e) {
            // expected
        }
    }

    @Test
    void testContainsFile() {
        assertTrue(fileSystem.containsFile("File"));
        assertFalse(fileSystem.containsFile("A"));
        assertFalse(fileSystem.containsFile("test"));

        openFolderFailIfFailed("Education");
        assertTrue(fileSystem.containsFile("test"));
        assertFalse(fileSystem.containsFile("File"));

        openFolderFailIfFailed("CPSC 210");
        assertTrue(fileSystem.containsFile("Personal Project Ideas"));
        assertTrue(fileSystem.containsFile("A"));
        assertFalse(fileSystem.containsFile("File"));
        assertFalse(fileSystem.containsFile("test"));
    }

    @Test
    @SuppressWarnings("methodlength")
    void testGetCapitalizationOfFile() {
        assertEquals("File", fileSystem.getCapitalizationOfFile("File"));
        assertEquals("File", fileSystem.getCapitalizationOfFile("file"));
        assertEquals("File", fileSystem.getCapitalizationOfFile("fIlE"));
        assertEquals("File", fileSystem.getCapitalizationOfFile("fIle"));
        assertEquals("File", fileSystem.getCapitalizationOfFile("fiLE"));
        assertEquals("File", fileSystem.getCapitalizationOfFile("FILE"));

        openFolderFailIfFailed("Education");
        assertEquals("test", fileSystem.getCapitalizationOfFile("TEST"));
        assertEquals("test", fileSystem.getCapitalizationOfFile("test"));
        assertEquals("test", fileSystem.getCapitalizationOfFile("tEst"));
        assertEquals("test", fileSystem.getCapitalizationOfFile("tEST"));
        assertEquals("test", fileSystem.getCapitalizationOfFile("TEsT"));

        openFolderFailIfFailed("CPSC 210");
        assertEquals("A", fileSystem.getCapitalizationOfFile("A"));
        assertEquals("A", fileSystem.getCapitalizationOfFile("a"));
        assertEquals("Personal Project Ideas", fileSystem.getCapitalizationOfFile("Personal Project IDeas"));
        assertEquals("Personal Project Ideas", fileSystem.getCapitalizationOfFile("Personal Project Ideas"));
        assertEquals("Personal Project Ideas", fileSystem.getCapitalizationOfFile("PERSONAL PROJECT IDEAS"));
        assertEquals("Personal Project Ideas", fileSystem.getCapitalizationOfFile("personal project ideas"));
        assertEquals("Personal Project Ideas", fileSystem.getCapitalizationOfFile("pERSONAL pROJECT iDEAS"));

        // Breaking REQUIRES clause for code coverage:
        try {
            fileSystem.getCapitalizationOfFile("F");
            fail();
        } catch (RequiresClauseNotMetRuntimeException e) {
            // expected
        }
    }

    @Test
    void testFileLabelledNoExceptions() {
        try {
            assertFalse(fileSystem.fileLabelled("File", "School"));
            assertFalse(fileSystem.fileLabelled("File", "Personal Project"));
        } catch (NoSuchFileFoundException | NoSuchLabelFoundException e) {
            fail();
        }

        openFolderFailIfFailed("Education");
        try {
            assertTrue(fileSystem.fileLabelled("test", "School"));
            assertFalse(fileSystem.fileLabelled("test", "Personal Project"));
        } catch (NoSuchFileFoundException | NoSuchLabelFoundException e) {
            fail();
        }

        openFolderFailIfFailed("CPSC 210");
        try {
            assertTrue(fileSystem.fileLabelled("Personal Project Ideas", "School"));
            assertTrue(fileSystem.fileLabelled("Personal projEct IdEas", "Personal Project"));
            assertTrue(fileSystem.fileLabelled("a", "School"));
            assertFalse(fileSystem.fileLabelled("A", "Personal Project"));
        } catch (NoSuchFileFoundException | NoSuchLabelFoundException e) {
            fail();
        }
    }

    @Test
    void testFileLabelledNoSuchFile() {
        try {
            fileSystem.fileLabelled("test", "School");
            fail();
        } catch (NoSuchFileFoundException e) {
            // expected
        } catch (NoSuchLabelFoundException e) {
            fail();
        }

        try {
            fileSystem.fileLabelled("Hello World!", "Personal Project");
            fail();
        } catch (NoSuchFileFoundException e) {
            // expected
        } catch (NoSuchLabelFoundException e) {
            fail();
        }

        openFolderFailIfFailed("Education");
        try {
            fileSystem.fileLabelled("File", "school");
            fail();
        } catch (NoSuchFileFoundException e) {
            // expected
        } catch (NoSuchLabelFoundException e) {
            fail();
        }
    }

    @Test
    void testFileLabelledNoSuchLabel() {
        try {
            fileSystem.fileLabelled("file", "CPSC 210");
            fail();
        } catch (NoSuchFileFoundException e) {
            fail();
        } catch (NoSuchLabelFoundException e) {
            // expected
        }

        try {
            fileSystem.fileLabelled("File", "Real Label, trust");
            fail();
        } catch (NoSuchFileFoundException e) {
            fail();
        } catch (NoSuchLabelFoundException e) {
            // expected
        }

        openFolderFailIfFailed("Education");
        try {
            fileSystem.fileLabelled("test", "The School");
            fail();
        } catch (NoSuchFileFoundException e) {
            fail();
        } catch (NoSuchLabelFoundException e) {
            // expected
        }
    }

    @Test
    void testFileLabelledNoLabelOrFile() {
        try {
            fileSystem.fileLabelled("not file", "CPSC 210");
            fail();
        } catch (NoSuchFileFoundException e) {
            // expected
        } catch (NoSuchLabelFoundException e) {
            fail();
        }

        try {
            fileSystem.fileLabelled("test", "Real Label, trust");
            fail();
        } catch (NoSuchFileFoundException e) {
            // expected
        } catch (NoSuchLabelFoundException e) {
            fail();
        }

        openFolderFailIfFailed("Education");
        try {
            fileSystem.fileLabelled("File", "The School");
            fail();
        } catch (NoSuchFileFoundException e) {
            // expected
        } catch (NoSuchLabelFoundException e) {
            fail();
        }
    }

    @Test
    void testGetNamesOfSubfilesNone() {
        assertTrue(emptyFileSystem.getNamesOfSubfiles().isEmpty());
    }

    @Test
    void testGetNamesOfSubfilesOne() {
        List<String> subfiles = fileSystem.getNamesOfSubfiles();
        assertEquals(1, subfiles.size());
        assertTrue(subfiles.contains("File"));
    }

    @Test
    void testGetNamesOfSubfilesMultiple() {
        openFolderFailIfFailed("Education");
        openFolderFailIfFailed("CPSC 210");

        List<String> subfiles = fileSystem.getNamesOfSubfiles();
        assertEquals(2, subfiles.size());
        assertTrue(subfiles.contains("Personal Project Ideas"));
        assertTrue(subfiles.contains("A"));
    }

    @Test
    @SuppressWarnings("methodlength")
    void testGetNamesOfRecentlyOpenedFiles() {
        try {
            fileSystem.openFile("Hello World!");
            fail();
        } catch (NoSuchFileFoundException e) {
            // expected
        } catch (FilePathNoLongerValidException e) {
            fail();
        }

        openFolderFailIfFailed("Education");
        try {
            fileSystem.openFile("test");
            fail();
        } catch (NoSuchFileFoundException e) {
            fail();
        } catch (FilePathNoLongerValidException e) {
            // expected
        }
        assertTrue(fileSystem.getNamesOfRecentlyOpenedFiles().isEmpty());

        try {
            fileSystem.createFile("File", VALID_FILE_PATH);
            fileSystem.openFile("File");
        } catch (NameIsTakenException | NoSuchFileFoundException e) {
            fail();
        } catch (FilePathNoLongerValidException e) {
            fail("VALID_FILE_PATH is not valid, please change it (or implementation is wrong)");            
        }

        List<String> recentlyOpenedFiles = fileSystem.getNamesOfRecentlyOpenedFiles();
        assertEquals(1, recentlyOpenedFiles.size());
        assertEquals("File", recentlyOpenedFiles.get(0));

        fileSystem.openRootFolder();
        try {
            fileSystem.createFile("This is a great news story", VALID_FILE_PATH);
            fileSystem.openFile("This is a great news story");
            fileSystem.openFile("This is a great news story");
        } catch (NameIsTakenException | NoSuchFileFoundException e) {
            fail();
        } catch (FilePathNoLongerValidException e) {
            fail("VALID_FILE_PATH is not valid, please change it (or implementation is wrong)");            
        }

        recentlyOpenedFiles = fileSystem.getNamesOfRecentlyOpenedFiles();
        assertEquals(2, recentlyOpenedFiles.size());
        assertEquals("This is a great news story", recentlyOpenedFiles.get(0));
        assertEquals("File", recentlyOpenedFiles.get(1));


        openFolderFailIfFailed("Education");
        try {
            fileSystem.openFile("File");
        } catch (NoSuchFileFoundException e) {
            fail();
        } catch (FilePathNoLongerValidException e) {
            fail("VALID_FILE_PATH is not valid, please change it (or implementation is wrong)");            
        }

        recentlyOpenedFiles = fileSystem.getNamesOfRecentlyOpenedFiles();
        assertEquals(2, recentlyOpenedFiles.size());
        assertEquals("File", recentlyOpenedFiles.get(0));
        assertEquals("This is a great news story", recentlyOpenedFiles.get(1));

        fileSystem.openRootFolder();
        try {
            fileSystem.createFile("We really need more files", VALID_FILE_PATH);
            fileSystem.openFile("We really need more files");
        } catch (NameIsTakenException | NoSuchFileFoundException e) {
            fail();
        } catch (FilePathNoLongerValidException e) {
            fail("VALID_FILE_PATH is not valid, please change it (or implementation is wrong)");            
        }

        recentlyOpenedFiles = fileSystem.getNamesOfRecentlyOpenedFiles();
        assertEquals(3, recentlyOpenedFiles.size());
        assertEquals("We really need more files", recentlyOpenedFiles.get(0));
        assertEquals("File", recentlyOpenedFiles.get(1));
        assertEquals("This is a great news story", recentlyOpenedFiles.get(2));

        try {
            fileSystem.openFile("This is a great news story");
        } catch (NoSuchFileFoundException e) {
            fail();
        } catch (FilePathNoLongerValidException e) {
            fail("VALID_FILE_PATH is not valid, please change it (or implementation is wrong)");            
        }

        recentlyOpenedFiles = fileSystem.getNamesOfRecentlyOpenedFiles();
        assertEquals(3, recentlyOpenedFiles.size());
        assertEquals("This is a great news story", recentlyOpenedFiles.get(0));
        assertEquals("We really need more files", recentlyOpenedFiles.get(1));
        assertEquals("File", recentlyOpenedFiles.get(2));

        try {
            fileSystem.openFile("We really need more files");
        } catch (NoSuchFileFoundException e) {
            fail();
        } catch (FilePathNoLongerValidException e) {
            fail("VALID_FILE_PATH is not valid, please change it (or implementation is wrong)");            
        }

        recentlyOpenedFiles = fileSystem.getNamesOfRecentlyOpenedFiles();
        assertEquals(3, recentlyOpenedFiles.size());
        assertEquals("We really need more files", recentlyOpenedFiles.get(0));
        assertEquals("This is a great news story", recentlyOpenedFiles.get(1));
        assertEquals("File", recentlyOpenedFiles.get(2));

        openFolderFailIfFailed("Education");
        openFolderFailIfFailed("CPSC 210");
        try {
            fileSystem.createFile("File", VALID_FILE_PATH);
            fileSystem.openFile("File");
        } catch (NameIsTakenException | NoSuchFileFoundException e) {
            fail();
        } catch (FilePathNoLongerValidException e) {
            fail("VALID_FILE_PATH is not valid, please change it (or implementation is wrong)");            
        }

        recentlyOpenedFiles = fileSystem.getNamesOfRecentlyOpenedFiles();
        assertEquals(4, recentlyOpenedFiles.size());
        assertEquals("File", recentlyOpenedFiles.get(0));
        assertEquals("We really need more files", recentlyOpenedFiles.get(1));
        assertEquals("This is a great news story", recentlyOpenedFiles.get(2));
        assertEquals("File", recentlyOpenedFiles.get(3));
    }

    @Test
    @SuppressWarnings("methodlength")
    void testGetNamesOfRecentlyOpenedFilesLengthLimit() {
        try {
            createNumberedFiles();

            openFileAndCheck("1", 1);
            openFileAndCheck("2", 2);
            openFileAndCheck("3", 3);
            openFileAndCheck("4", 4);
            openFileAndCheck("5", 5);
            openFileAndCheck("6", 6);
            openFileAndCheck("7", 7);
            openFileAndCheck("8", 8);
            openFileAndCheck("9", 9);
            openFileAndCheck("10", 10);
            openFileAndCheck("11", 10);
        } catch (NameIsTakenException | NoSuchFileFoundException e) {
            fail();
        } catch (FilePathNoLongerValidException e) {
            fail("VALID_FILE_PATH is not valid, please change it (or implementation is wrong)");
        }

        List<String> recentlyOpenedFiles = fileSystem.getNamesOfRecentlyOpenedFiles();
        assertEquals(10, recentlyOpenedFiles.size());
        assertEquals("11", recentlyOpenedFiles.get(0));
        assertEquals("10", recentlyOpenedFiles.get(1));
        assertEquals("9", recentlyOpenedFiles.get(2));
        assertEquals("8", recentlyOpenedFiles.get(3));
        assertEquals("7", recentlyOpenedFiles.get(4));
        assertEquals("6", recentlyOpenedFiles.get(5));
        assertEquals("5", recentlyOpenedFiles.get(6));
        assertEquals("4", recentlyOpenedFiles.get(7));
        assertEquals("3", recentlyOpenedFiles.get(8));
        assertEquals("2", recentlyOpenedFiles.get(9));
    }

    // EFFECTS: opens File named fileName then asserts that the recentlyOpenedFiles list is of size listSize and that
    // its first element is fileName
    private void openFileAndCheck(String fileName, int listSize)
            throws NoSuchFileFoundException, FilePathNoLongerValidException {
        fileSystem.openFile(fileName);
        List<String> recentlyOpenedFiles = fileSystem.getNamesOfRecentlyOpenedFiles();
        assertEquals(listSize, recentlyOpenedFiles.size());
        assertEquals(fileName, recentlyOpenedFiles.get(0));
    }

    @Test
    @SuppressWarnings("methodlength")
    void testGetNamesOfRecentlyOpenedFilesStopTrackingRecents() {
        assertTrue(fileSystem.getNamesOfRecentlyOpenedFiles().isEmpty());
        openFolderFailIfFailed("Education");
        try {
            fileSystem.createFile("File", VALID_FILE_PATH);
            fileSystem.openFile("File");
        } catch (NameIsTakenException | NoSuchFileFoundException e) {
            fail();
        } catch (FilePathNoLongerValidException e) {
            fail("VALID_FILE_PATH is not valid, please change it (or implementation is wrong)");            
        }
        List<String> recentlyOpenedFiles = fileSystem.getNamesOfRecentlyOpenedFiles();
        assertEquals(1, recentlyOpenedFiles.size());
        assertEquals("File", recentlyOpenedFiles.get(0));

        fileSystem.stopKeepingTrackOfRecents();
        fileSystem.openRootFolder();
        try {
            fileSystem.createFile("This is a great news story", VALID_FILE_PATH);
            fileSystem.openFile("This is a great news story");
            fileSystem.openFile("This is a great news story");
        } catch (NameIsTakenException | NoSuchFileFoundException e) {
            fail();
        } catch (FilePathNoLongerValidException e) {
            fail("VALID_FILE_PATH is not valid, please change it (or implementation is wrong)");            
        }
        recentlyOpenedFiles = fileSystem.getNamesOfRecentlyOpenedFiles();
        assertEquals(1, recentlyOpenedFiles.size());
        assertEquals("File", recentlyOpenedFiles.get(0));

        fileSystem.startKeepingTrackOfRecents();
        fileSystem.openRootFolder();
        try {
            fileSystem.createFile("We really need more files", VALID_FILE_PATH);
            fileSystem.openFile("We really need more files");
        } catch (NameIsTakenException | NoSuchFileFoundException e) {
            fail();
        } catch (FilePathNoLongerValidException e) {
            fail("VALID_FILE_PATH is not valid, please change it (or implementation is wrong)");            
        }
        recentlyOpenedFiles = fileSystem.getNamesOfRecentlyOpenedFiles();
        assertEquals(2, recentlyOpenedFiles.size());
        assertEquals("We really need more files", recentlyOpenedFiles.get(0));
        assertEquals("File", recentlyOpenedFiles.get(1));
    }

    @Test
    @SuppressWarnings("methodlength")
    void testOpenRecentlyOpenedFile() {
        try {
            createNumberedFiles();
        } catch (NameIsTakenException e) {
            fail();
        }

        try {
            fileSystem.openFile("1");
            fileSystem.openFile("2");
            fileSystem.openFile("3");
            fileSystem.openFile("4");
            fileSystem.openFile("5");
            fileSystem.openFile("6");
            fileSystem.openFile("7");
            fileSystem.openFile("8");
            fileSystem.openFile("9");
            fileSystem.openFile("10");
            fileSystem.openFile("11");
        } catch (NoSuchFileFoundException e) {
            fail();
        } catch (FilePathNoLongerValidException e) {
            fail("VALID_FILE_PATH is not valid, please change it (or implementation is wrong)");
        }
        

        try {
            fileSystem.openRecentlyOpenedFile("11");
            fileSystem.openRecentlyOpenedFile("9");
            fileSystem.openRecentlyOpenedFile("2");
        } catch (NoSuchFileFoundException e) {
            fail();
        } catch (FilePathNoLongerValidException e) {
            fail("VALID_FILE_PATH is not valid, please change it (or implementation is wrong)");
        }

        try {
            fileSystem.openRecentlyOpenedFile("1");
            fail();
        } catch (NoSuchFileFoundException e) {
            // expected
        } catch (FilePathNoLongerValidException e) {
            fail();
        }

        try {
            fileSystem.openRecentlyOpenedFile("File");
            fail();
        } catch (NoSuchFileFoundException e) {
            // expected
        } catch (FilePathNoLongerValidException e) {
            fail();
        }
    }

    @Test
    @SuppressWarnings("methodlength")
    void testOpenRecentlyOpenedFileStopTrackingRecents() {
        try {
            createNumberedFiles();
        } catch (NameIsTakenException e) {
            fail();
        }

        try {
            fileSystem.openFile("1");

            fileSystem.stopKeepingTrackOfRecents();
            fileSystem.openFile("2");
            fileSystem.openFile("3");
            fileSystem.openFile("4");
            fileSystem.startKeepingTrackOfRecents();

            fileSystem.openFile("5");
            fileSystem.openFile("6");
            fileSystem.openFile("7");
            fileSystem.openFile("8");

            fileSystem.stopKeepingTrackOfRecents();
            fileSystem.openFile("9");
            fileSystem.openFile("10");
            fileSystem.startKeepingTrackOfRecents();

            fileSystem.openFile("11");
        } catch (NoSuchFileFoundException e) {
            fail();
        } catch (FilePathNoLongerValidException e) {
            fail("VALID_FILE_PATH is not valid, please change it (or implementation is wrong)");
        }
        
        try {
            fileSystem.openRecentlyOpenedFile("1");
            fileSystem.openRecentlyOpenedFile("6");
            fileSystem.openRecentlyOpenedFile("8");
            fileSystem.openRecentlyOpenedFile("11");
        } catch (NoSuchFileFoundException e) {
            fail();
        } catch (FilePathNoLongerValidException e) {
            fail("VALID_FILE_PATH is not valid, please change it (or implementation is wrong)");
        }

        assertThrows(NoSuchFileFoundException.class, () -> fileSystem.openRecentlyOpenedFile("2"));
        assertThrows(NoSuchFileFoundException.class, () -> fileSystem.openRecentlyOpenedFile("3"));
        assertThrows(NoSuchFileFoundException.class, () -> fileSystem.openRecentlyOpenedFile("4"));
        assertThrows(NoSuchFileFoundException.class, () -> fileSystem.openRecentlyOpenedFile("9"));
        assertThrows(NoSuchFileFoundException.class, () -> fileSystem.openRecentlyOpenedFile("10"));
        assertThrows(NoSuchFileFoundException.class, () -> fileSystem.openRecentlyOpenedFile("15"));
    }


    /* 
     *  Tests for Folder Methods:
     */

    @Test
    @SuppressWarnings("methodlength")
    void testCreateFolderAllGood() {
        try {
            fileSystem.createFolder("A Folder");
            fileSystem.createFolder("B");
            fileSystem.createFolder("55%");
        } catch (NameIsTakenException e) {
            fail();
        }
        List<String> subfolderNames = fileSystem.getNamesOfSubfolders();
        assertEquals(2 + 3, subfolderNames.size());
        assertTrue(subfolderNames.contains("A Folder"));
        assertTrue(subfolderNames.contains("B"));
        assertTrue(subfolderNames.contains("55%"));

        openFolderFailIfFailed("Hobbies");
        try {
            fileSystem.createFolder("DnD");
            fileSystem.createFolder("MtG");
        } catch (NameIsTakenException e) {
            fail();
        }
        subfolderNames = fileSystem.getNamesOfSubfolders();
        assertEquals(2, subfolderNames.size());
        assertTrue(subfolderNames.contains("DnD"));
        assertTrue(subfolderNames.contains("MtG"));
    }

    @Test
    @SuppressWarnings("methodlength")
    void testCreateFolderNameIsEmpty() {
        try {
            emptyFileSystem.createFolder("");
            fail("NameIsBlankException not thrown when name was empty");
        } catch (NameIsBlankException e) {
            // expected
        } catch (NameIsTakenException e) {
            fail("NameIsTakenException thrown when name was empty");
        }
    }

    @Test
    @SuppressWarnings("methodlength")
    void testCreateFolderNameIsTaken() {
        try {
            fileSystem.createFolder("Education");
            fail("NameIsTakenException not thrown when name was taken");
        } catch (NameIsTakenException e) {
            // expected
            assertEquals("Education", e.getCapitalizationOfTakenName());
        }
        List<String> subfolderNames = fileSystem.getNamesOfSubfolders();
        assertEquals(2, subfolderNames.size());

        try {
            fileSystem.createFolder("hobbIes");
            fail("NameIsTakenException not thrown when name was taken");
        } catch (NameIsTakenException e) {
            // expected
            assertEquals("Hobbies", e.getCapitalizationOfTakenName());
        }
        subfolderNames = fileSystem.getNamesOfSubfolders();
        assertEquals(2, subfolderNames.size());

        openFolderFailIfFailed("Education");
        try {
            fileSystem.createFolder("CPSC 210");
            fail("NameIsTakenException not thrown when name was taken");
        } catch (NameIsTakenException e) {
            // expected
            assertEquals("CPSC 210", e.getCapitalizationOfTakenName());
        }
        subfolderNames = fileSystem.getNamesOfSubfolders();
        assertEquals(1, subfolderNames.size());
    }

    @Test
    @SuppressWarnings("methodlength")
    void testOpenFolderValid() {
        try {
            fileSystem.openFolder("Education");
        } catch (NoSuchFolderFoundException e) {
            fail();
        }
        assertEquals("Education", fileSystem.getCurrentFolderName());
        try {
            assertEquals("root", fileSystem.getParentFolderName());
        } catch (NoSuchFolderFoundException e) {
            fail();
        }
        
        List<String> recentlyOpenedFolders = fileSystem.getNamesOfRecentlyOpenedFolders();
        assertEquals(2, recentlyOpenedFolders.size());
        assertEquals("Education", recentlyOpenedFolders.get(0));
        assertEquals("CPSC 210", recentlyOpenedFolders.get(1));

        try {
            fileSystem.openFolder("CPSC 210");
        } catch (NoSuchFolderFoundException e) {
            fail();
        }
        assertEquals("CPSC 210", fileSystem.getCurrentFolderName());
        try {
            assertEquals("Education", fileSystem.getParentFolderName());
        } catch (NoSuchFolderFoundException e) {
            fail();
        }

        recentlyOpenedFolders = fileSystem.getNamesOfRecentlyOpenedFolders();
        assertEquals(2, recentlyOpenedFolders.size());
        assertEquals("CPSC 210", recentlyOpenedFolders.get(0));
        assertEquals("Education", recentlyOpenedFolders.get(1));
    }

    @Test
    @SuppressWarnings("methodlength")
    void testOpenFolderMoreThanTenRecents() {
        try {
            createNumberedFolders();
        } catch (NameIsTakenException e) {
            fail();
        }

        try {
            fileSystem.openFolder("1");
            fileSystem.openRootFolder();
            fileSystem.openFolder("2");
            fileSystem.openRootFolder();
            fileSystem.openFolder("3");
            fileSystem.openRootFolder();
            fileSystem.openFolder("4");
            fileSystem.openRootFolder();
            fileSystem.openFolder("5");
            fileSystem.openRootFolder();
            fileSystem.openFolder("6");
            fileSystem.openRootFolder();
            fileSystem.openFolder("7");
            fileSystem.openRootFolder();
            fileSystem.openFolder("8");
            fileSystem.openRootFolder();
            fileSystem.openFolder("9");
            fileSystem.openRootFolder();

            List<String> recentlyOpenedFolders = fileSystem.getNamesOfRecentlyOpenedFolders();
            assertEquals(10, recentlyOpenedFolders.size());
            assertFalse(recentlyOpenedFolders.contains("Education"));
            assertTrue(recentlyOpenedFolders.contains("CPSC 210"));

            fileSystem.openFolder("10");
            fileSystem.openRootFolder();
            fileSystem.openFolder("11");
            fileSystem.openRootFolder();
        } catch (NoSuchFolderFoundException e) {
            fail();
        }

        List<String> recentlyOpenedFolders = fileSystem.getNamesOfRecentlyOpenedFolders();
        assertEquals(10, recentlyOpenedFolders.size());
        assertEquals("11", recentlyOpenedFolders.get(0));
        assertEquals("10", recentlyOpenedFolders.get(1));
        assertEquals("9", recentlyOpenedFolders.get(2));
        assertEquals("8", recentlyOpenedFolders.get(3));
        assertEquals("7", recentlyOpenedFolders.get(4));
        assertEquals("6", recentlyOpenedFolders.get(5));
        assertEquals("5", recentlyOpenedFolders.get(6));
        assertEquals("4", recentlyOpenedFolders.get(7));
        assertEquals("3", recentlyOpenedFolders.get(8));
        assertEquals("2", recentlyOpenedFolders.get(9));
    }

    @Test
    void testOpenFolderInvalid() {
        try {
            fileSystem.openFolder("Skills");
            fail();
        } catch (NoSuchFolderFoundException e) {
            // expected
        }

        openFolderFailIfFailed("Education");
        try {
            fileSystem.openFolder("Hobbies");
            fail();
        } catch (NoSuchFolderFoundException e) {
            // expected
        }
    }

    @Test
    @SuppressWarnings("methodlength")
    void testOpenFolderValidSomeRecents() {
        try {
            fileSystem.openFolder("Education");
        } catch (NoSuchFolderFoundException e) {
            fail();
        }
        List<String> recentlyOpenedFolders = fileSystem.getNamesOfRecentlyOpenedFolders();
        assertEquals(2, recentlyOpenedFolders.size());
        assertEquals("Education", recentlyOpenedFolders.get(0));
        assertEquals("CPSC 210", recentlyOpenedFolders.get(1));

        fileSystem.stopKeepingTrackOfRecents();
        try {
            fileSystem.openFolder("CPSC 210");
        } catch (NoSuchFolderFoundException e) {
            fail();
        }
        recentlyOpenedFolders = fileSystem.getNamesOfRecentlyOpenedFolders();
        assertEquals(2, recentlyOpenedFolders.size());
        assertEquals("Education", recentlyOpenedFolders.get(0));
        assertEquals("CPSC 210", recentlyOpenedFolders.get(1));

        fileSystem.startKeepingTrackOfRecents();
        try {
            fileSystem.goUpOneDirectoryLevel();
            fileSystem.openFolder("CPSC 210");
        } catch (NoSuchFolderFoundException e) {
            fail();
        }
        recentlyOpenedFolders = fileSystem.getNamesOfRecentlyOpenedFolders();
        assertEquals(2, recentlyOpenedFolders.size());
        assertEquals("CPSC 210", recentlyOpenedFolders.get(0));
        assertEquals("Education", recentlyOpenedFolders.get(1));
    }

    @Test
    @SuppressWarnings("methodlength")
    void testOpenFolderValidNoRecents() {
        fileSystem.stopKeepingTrackOfRecents();
        try {
            fileSystem.openFolder("Education");
        } catch (NoSuchFolderFoundException e) {
            fail();
        }
        List<String> recentlyOpenedFolders = fileSystem.getNamesOfRecentlyOpenedFolders();
        assertEquals(2, recentlyOpenedFolders.size());
        assertEquals("CPSC 210", recentlyOpenedFolders.get(0));
        assertEquals("Education", recentlyOpenedFolders.get(1));
    }

    @Test
    @SuppressWarnings("methodlength")
    void testOpenFolderMoreThanTenRecentsNoRecents() {
        fileSystem.stopKeepingTrackOfRecents();
        try {
            createNumberedFolders();
        } catch (NameIsTakenException e) {
            fail();
        }

        try {
            fileSystem.openFolder("1");
            fileSystem.openRootFolder();
            fileSystem.openFolder("2");
            fileSystem.openRootFolder();
            fileSystem.openFolder("3");
            fileSystem.openRootFolder();
            fileSystem.openFolder("4");
            fileSystem.openRootFolder();
            fileSystem.openFolder("5");
            fileSystem.openRootFolder();
            fileSystem.openFolder("6");
            fileSystem.openRootFolder();
            fileSystem.openFolder("7");
            fileSystem.openRootFolder();
            fileSystem.openFolder("8");
            fileSystem.openRootFolder();
            fileSystem.openFolder("9");
            fileSystem.openRootFolder();

            List<String> recentlyOpenedFolders = fileSystem.getNamesOfRecentlyOpenedFolders();
            assertEquals(2, recentlyOpenedFolders.size());
            assertEquals("CPSC 210", recentlyOpenedFolders.get(0));
            assertEquals("Education", recentlyOpenedFolders.get(1));

            fileSystem.openFolder("10");
            fileSystem.openRootFolder();
            fileSystem.openFolder("11");
            fileSystem.openRootFolder();
        } catch (NoSuchFolderFoundException e) {
            fail();
        }
        List<String> recentlyOpenedFolders = fileSystem.getNamesOfRecentlyOpenedFolders();
        assertEquals(2, recentlyOpenedFolders.size());
        assertEquals("CPSC 210", recentlyOpenedFolders.get(0));
        assertEquals("Education", recentlyOpenedFolders.get(1));
    }

    @Test
    void testGoUpOneDirectoryLevelFailRoot() {
        try {
            fileSystem.goUpOneDirectoryLevel();
            fail();
        } catch (NoSuchFolderFoundException e) {
            // expected
        }

        assertEquals("root", fileSystem.getCurrentFolderName());
    }

    @Test
    void testGoUpOneDirectoryLevelFailLabel() {
        try {
            fileSystem.openLabel("School");
        } catch (NoSuchLabelFoundException e) {
            fail();
        }

        try {
            fileSystem.goUpOneDirectoryLevel();
            fail();
        } catch (NoSuchFolderFoundException e) {
            // expected
        }

        assertEquals("School", fileSystem.getCurrentFolderName());
    }

    @Test
    void testGoUpOneDirectoryLevelToRoot() {
        openFolderFailIfFailed("Hobbies");

        try {
            fileSystem.goUpOneDirectoryLevel();
        } catch (NoSuchFolderFoundException e) {
            fail();
        }
        assertEquals("root", fileSystem.getCurrentFolderName());
    }

    @Test
    void testGoUpOneDirectoryLevelToNotRoot() {
        openFolderFailIfFailed("Education");
        openFolderFailIfFailed("CPSC 210");

        try {
            fileSystem.goUpOneDirectoryLevel();
        } catch (NoSuchFolderFoundException e) {
            fail();
        }

        assertEquals("Education", fileSystem.getCurrentFolderName());
    }

    @Test
    void testOpenRootFolderFromRoot() {
        fileSystem.openRootFolder();

        assertEquals("root", fileSystem.getCurrentFolderName());
    }

    @Test
    void testOpenRootFolderFromOneDeep() {
        openFolderFailIfFailed("Hobbies");

        fileSystem.openRootFolder();

        assertEquals("root", fileSystem.getCurrentFolderName());
    }

    @Test
    void testOpenRootFolderFromMultipleDeep() {
        openFolderFailIfFailed("Education");
        openFolderFailIfFailed("CPSC 210");

        fileSystem.openRootFolder();

        assertEquals("root", fileSystem.getCurrentFolderName());
    }

    @Test
    void testOpenRootFolderFromLabel() {
        try {
            fileSystem.openLabel("Personal Project");
        } catch (NoSuchLabelFoundException e) {
            fail();
        }
        
        fileSystem.openRootFolder();

        assertEquals("root", fileSystem.getCurrentFolderName());
    }

    @Test
    @SuppressWarnings("methodlength")
    void testDeleteFolderValidName() {
        try {
            fileSystem.deleteFolder("Hobbies");
        } catch (NoSuchFolderFoundException e) {
            fail("NoSuchFolderFoundException when Folder exists");
        }
        assertEquals(1, fileSystem.getNamesOfSubfolders().size());
        assertFalse(fileSystem.containsFolder("Hobbies"));

        openFolderFailIfFailed("Education");

        try {
            fileSystem.deleteFolder("CPSC 210");
        } catch (NoSuchFolderFoundException e) {
            fail("NoSuchFolderFoundException when Folder exists");
        }
        assertTrue(fileSystem.getNamesOfSubfolders().isEmpty());
        assertFalse(fileSystem.containsFolder("CPSC 210"));

        try {
            fileSystem.createFolder("TEST");
        } catch (NameIsTakenException e) {
            fail();
        }
        try {
            fileSystem.deleteFolder("TEST");
        } catch (NoSuchFolderFoundException e) {
            fail("NoSuchFolderFoundException when Folder exists");
        }
        assertTrue(fileSystem.getNamesOfSubfolders().isEmpty());
        assertFalse(fileSystem.containsFolder("TEST"));
    }

    @Test
    void testDeleteFolderFail() {
        try {
            fileSystem.deleteFolder("Hello World!");
            fail("No exception thrown when deleting non-existent Folder");
        } catch (NoSuchFolderFoundException e) {
            // expected
        }
        try {
            fileSystem.deleteFolder("What's up?");
            fail("No exception thrown when deleting non-existent Folder");
        } catch (NoSuchFolderFoundException e) {
            // expected
        }
        assertEquals(2, fileSystem.getNamesOfSubfolders().size());
    }

    @Test
    void testSetFolderNameValidName() {
        try {
            fileSystem.setFolderName("Education", "Non-silly folder name");
        } catch (NoSuchFolderFoundException | NameIsTakenException e) {
            fail();
        }
        assertFalse(fileSystem.containsFolder("Education"));
        assertTrue(fileSystem.containsFolder("non-silly fOlDEr name"));

        openFolderFailIfFailed("non-SILLY folder NAme");
        try {
            fileSystem.setFolderName("CPSC 210", "Best course I'm currently taking");
        } catch (NoSuchFolderFoundException | NameIsTakenException e) {
            fail();
        }
        assertFalse(fileSystem.containsFolder("CPSC 210"));
        assertTrue(fileSystem.containsFolder("Best course I'm currently taking"));
    }

    @Test
    void testSetFolderNameNonexistentFolders() {
        try {
            fileSystem.setFolderName("This Folder doesn't exist", "Name");
            fail();
        } catch (NoSuchFolderFoundException e) {
            // expected
        } catch (NameIsTakenException e) {
            fail();
        }
        List<String> subfolderNames = fileSystem.getNamesOfSubfolders();
        assertEquals(2, subfolderNames.size());
        assertTrue(subfolderNames.contains("Education"));
        assertTrue(subfolderNames.contains("Hobbies"));

        openFolderFailIfFailed("Education");
        openFolderFailIfFailed("CPSC 210");
        try {
            fileSystem.setFolderName("Real Folder for sure", "Foldername");
            fail();
        } catch (NoSuchFolderFoundException e) {
            // expected
        } catch (NameIsTakenException e) {
            fail();
        }
        assertTrue(fileSystem.getNamesOfSubfolders().isEmpty());
    }

    @Test
    void testSetFolderNameNameIsTaken() {
        try {
            fileSystem.setFolderName("Hobbies", "Education");
            fail();
        } catch (NameIsTakenException e) {
            // expected
            assertEquals("Education", e.getCapitalizationOfTakenName());
        } catch (NoSuchFolderFoundException e) {
            fail();
        }
        List<String> subfolderNames = fileSystem.getNamesOfSubfolders();
        assertEquals(2, subfolderNames.size());
        assertTrue(subfolderNames.contains("Hobbies"));
        assertTrue(subfolderNames.contains("Education"));
    }

    @Test
    void testSetFolderNameTakenAndNotFound() {
        try {
            fileSystem.setFolderName("This folder doesn't exist", "hobBies");
            fail();
        } catch (NameIsTakenException e) {
            // expected
            assertEquals("Hobbies", e.getCapitalizationOfTakenName());
        } catch (NoSuchFolderFoundException e) {
            // NameIsTakenException should be thrown first
            fail();
        }
        List<String> subfolderNames = fileSystem.getNamesOfSubfolders();
        assertEquals(2, subfolderNames.size());
        assertTrue(subfolderNames.contains("Education"));
        assertTrue(subfolderNames.contains("Hobbies"));
    }

    @Test
    void testSetFolderNameEmptyAndNotFound() {
        openFolderFailIfFailed("EDUCATION");
        try {
            fileSystem.setFolderName("", "");
            fail();
        } catch (NameIsTakenException e) {
            fail();
        } catch (NoSuchFolderFoundException e) {
            // expected
        }
        List<String> subfolderNames = fileSystem.getNamesOfSubfolders();
        assertEquals(1, subfolderNames.size());
        assertTrue(subfolderNames.contains("CPSC 210"));
    }

    @Test
    void testSetFolderNameTakenAndEmpty() {
        openFolderFailIfFailed("Education");
        try {
            fileSystem.setFolderName("", "CPSc 210");
            fail();
        } catch (NoSuchFolderFoundException e) {
            fail();
        } catch (NameIsTakenException e) {
            // expected
            assertEquals("CPSC 210", e.getCapitalizationOfTakenName());
        }
        List<String> subfolderNames = fileSystem.getNamesOfSubfolders();
        assertEquals(1, subfolderNames.size());
        assertTrue(subfolderNames.contains("CPSC 210"));
    }

    @Test
    void testSetFolderNameNewNameEmpty() {
        try {
            fileSystem.setFolderName("Education", "");
            fail();
        } catch (NameIsTakenException | NoSuchFolderFoundException e) {
            fail();
        } catch (NameIsBlankException e) {
            // expected
        }
        List<String> subfolderNames = fileSystem.getNamesOfSubfolders();
        assertEquals(2, subfolderNames.size());
        assertTrue(subfolderNames.contains("Education"));
        assertTrue(subfolderNames.contains("Hobbies"));
    }

    @Test
    void testCurrentFolderHasParentRoot() {
        assertFalse(fileSystem.currentFolderHasParent());
    }

    @Test
    void testCurrentFolderHasParentLabel() {
        try {
            fileSystem.openLabel("School");
        } catch (NoSuchLabelFoundException e) {
            fail();
        }
        
        assertFalse(fileSystem.currentFolderHasParent());

        try {
            fileSystem.openLabel("Personal Project");
        } catch (NoSuchLabelFoundException e) {
            fail();
        }

        assertFalse(fileSystem.currentFolderHasParent());
    }

    @Test
    void testCurrentFolderHasParentOneDeep() {
        openFolderFailIfFailed("Hobbies");

        assertTrue(fileSystem.currentFolderHasParent());
    }

    @Test
    void testCurrentFolderHasParentMultipleDeep() {
        openFolderFailIfFailed("Education");
        openFolderFailIfFailed("CPSC 210");

        assertTrue(fileSystem.currentFolderHasParent());
    }

    @Test
    void testContainsFolderEmptyFolder() {
        openFolderFailIfFailed("Hobbies");

        assertFalse(fileSystem.containsFolder("There are no folders here"));
        assertFalse(fileSystem.containsFolder("root"));
        assertFalse(fileSystem.containsFolder("Education"));
        assertFalse(fileSystem.containsFolder("Hobbies"));
        assertFalse(fileSystem.containsFolder("CPSC 210"));
    }

    @Test
    void testContainsFolderOneFolder() {
        openFolderFailIfFailed("Education");

        assertTrue(fileSystem.containsFolder("CPSC 210"));

        assertFalse(fileSystem.containsFolder("There are is one folder here"));
        assertFalse(fileSystem.containsFolder("root"));
        assertFalse(fileSystem.containsFolder("Education"));
        assertFalse(fileSystem.containsFolder("Hobbies"));
    }

    @Test
    void testContainsFolderMultipleFolders() {
        assertTrue(fileSystem.containsFolder("Education"));
        assertTrue(fileSystem.containsFolder("Hobbies"));

        assertFalse(fileSystem.containsFolder("root"));
        assertFalse(fileSystem.containsFolder("CPSC 210"));
        assertFalse(fileSystem.containsFolder("There are is multiple folders here"));
    }

    @Test
    void testGetNamesOfSubfoldersNone() {
        openFolderFailIfFailed("Hobbies");

        List<String> subfolders = fileSystem.getNamesOfSubfolders();

        assertTrue(subfolders.isEmpty());

        fileSystem.openRootFolder();

        openFolderFailIfFailed("Education");
        openFolderFailIfFailed("CPSC 210");

        subfolders = fileSystem.getNamesOfSubfolders();

        assertTrue(subfolders.isEmpty());
    }

    @Test
    void testGetNamesOfSubfoldersOne() {
        openFolderFailIfFailed("Education");

        List<String> subfolders = fileSystem.getNamesOfSubfolders();

        assertEquals(1, subfolders.size());
        assertTrue(subfolders.contains("CPSC 210"));
    }

    @Test
    void testGetNamesOfSubfoldersMultiple() {
        List<String> subfolders = fileSystem.getNamesOfSubfolders();

        assertEquals(2, subfolders.size());
        assertTrue(subfolders.contains("Education"));
        assertTrue(subfolders.contains("Hobbies"));
    }

    @Test
    void testGetCapitalizationOfFolder() {
        assertEquals("Hobbies", fileSystem.getCapitalizationOfFolder("Hobbies"));
        assertEquals("Hobbies", fileSystem.getCapitalizationOfFolder("hobbies"));
        assertEquals("Hobbies", fileSystem.getCapitalizationOfFolder("hObBiEs"));
        assertEquals("Hobbies", fileSystem.getCapitalizationOfFolder("hoBbies"));
        assertEquals("Hobbies", fileSystem.getCapitalizationOfFolder("HObbiEs"));
        assertEquals("Hobbies", fileSystem.getCapitalizationOfFolder("HOBBIES"));

        openFolderFailIfFailed("Education");
        assertEquals("CPSC 210", fileSystem.getCapitalizationOfFolder("CPSC 210"));
        assertEquals("CPSC 210", fileSystem.getCapitalizationOfFolder("cpsc 210"));
        assertEquals("CPSC 210", fileSystem.getCapitalizationOfFolder("cpSc 210"));
        assertEquals("CPSC 210", fileSystem.getCapitalizationOfFolder("CPsc 210"));
        assertEquals("CPSC 210", fileSystem.getCapitalizationOfFolder("CPsC 210"));

        // Breaking REQUIRES clause for code coverage:
        try {
            fileSystem.getCapitalizationOfFolder("F");
            fail();
        } catch (RequiresClauseNotMetRuntimeException e) {
            // expected
        }
    }

    @Test
    @SuppressWarnings("methodlength")
    void testGetNamesOfRecentlyOpenedFolders() {
        openFolderFailIfFailed("Hobbies");

        List<String> recentlyOpenedFolders = fileSystem.getNamesOfRecentlyOpenedFolders();
        assertEquals(3, recentlyOpenedFolders.size());
        assertEquals("Hobbies", recentlyOpenedFolders.get(0));
        assertEquals("CPSC 210", recentlyOpenedFolders.get(1));
        assertEquals("Education", recentlyOpenedFolders.get(2));
        
        fileSystem.openRootFolder();
        assertEquals(3, recentlyOpenedFolders.size());
        openFolderFailIfFailed("Education");

        recentlyOpenedFolders = fileSystem.getNamesOfRecentlyOpenedFolders();
        assertEquals(3, recentlyOpenedFolders.size());
        assertEquals("Education", recentlyOpenedFolders.get(0));
        assertEquals("Hobbies", recentlyOpenedFolders.get(1));
        assertEquals("CPSC 210", recentlyOpenedFolders.get(2));

        try {
            fileSystem.openFolder("Hello World!");
            fail();
        } catch (NoSuchFolderFoundException e) {
            // expected
        }

        recentlyOpenedFolders = fileSystem.getNamesOfRecentlyOpenedFolders();
        assertEquals(3, recentlyOpenedFolders.size());
        assertEquals("Education", recentlyOpenedFolders.get(0));
        assertEquals("Hobbies", recentlyOpenedFolders.get(1));
        assertEquals("CPSC 210", recentlyOpenedFolders.get(2));
    }

    @Test
    @SuppressWarnings("methodlength")
    void testGetNamesOfRecentlyOpenedFoldersLengthLimit() {
        try {
            createNumberedFolders();

            assertEquals(2, fileSystem.getNamesOfRecentlyOpenedFolders().size());
            openFolderAndCheck("1", 3);
            openFolderAndCheck("2", 4);
            openFolderAndCheck("3", 5);
            openFolderAndCheck("4", 6);
            openFolderAndCheck("5", 7);
            openFolderAndCheck("6", 8);
            openFolderAndCheck("7", 9);
            openFolderAndCheck("8", 10);
            openFolderAndCheck("9", 10);
            openFolderAndCheck("10", 10);
            openFolderAndCheck("11", 10);
        } catch (NameIsTakenException | NoSuchFolderFoundException e) {
            fail();
        }
        List<String> recentlyOpenedFolders = fileSystem.getNamesOfRecentlyOpenedFolders();
        assertEquals(10, recentlyOpenedFolders.size());
        assertEquals("11", recentlyOpenedFolders.get(0));
        assertEquals("10", recentlyOpenedFolders.get(1));
        assertEquals("9", recentlyOpenedFolders.get(2));
        assertEquals("8", recentlyOpenedFolders.get(3));
        assertEquals("7", recentlyOpenedFolders.get(4));
        assertEquals("6", recentlyOpenedFolders.get(5));
        assertEquals("5", recentlyOpenedFolders.get(6));
        assertEquals("4", recentlyOpenedFolders.get(7));
        assertEquals("3", recentlyOpenedFolders.get(8));
        assertEquals("2", recentlyOpenedFolders.get(9));
    }

    // EFFECTS: opens Folder named folderName then asserts that the recentlyOpenedFolders list is of size listSize and
    // that its first element is folderName
    private void openFolderAndCheck(String folderName, int listSize) throws NoSuchFolderFoundException {
        fileSystem.openFolder(folderName);
        List<String> recentlyOpenedFolders = fileSystem.getNamesOfRecentlyOpenedFolders();
        assertEquals(listSize, recentlyOpenedFolders.size());
        assertEquals(folderName, recentlyOpenedFolders.get(0));
        fileSystem.openRootFolder();
    }

    @Test
    @SuppressWarnings("methodlength")
    void testGetNamesOfRecentlyOpenedFoldersStopTrackingRecents() {
        openFolderFailIfFailed("Hobbies");

        List<String> recentlyOpenedFolders = fileSystem.getNamesOfRecentlyOpenedFolders();
        assertEquals(3, recentlyOpenedFolders.size());
        assertEquals("Hobbies", recentlyOpenedFolders.get(0));
        assertEquals("CPSC 210", recentlyOpenedFolders.get(1));
        assertEquals("Education", recentlyOpenedFolders.get(2));
        
        fileSystem.stopKeepingTrackOfRecents();
        fileSystem.openRootFolder();
        assertEquals(3, recentlyOpenedFolders.size());
        openFolderFailIfFailed("Education");

        recentlyOpenedFolders = fileSystem.getNamesOfRecentlyOpenedFolders();
        assertEquals(3, recentlyOpenedFolders.size());
        assertEquals("Hobbies", recentlyOpenedFolders.get(0));
        assertEquals("CPSC 210", recentlyOpenedFolders.get(1));
        assertEquals("Education", recentlyOpenedFolders.get(2));

        fileSystem.startKeepingTrackOfRecents();
        try {
            fileSystem.openFolder("Hello World!");
            fail();
        } catch (NoSuchFolderFoundException e) {
            // expected
        }
        recentlyOpenedFolders = fileSystem.getNamesOfRecentlyOpenedFolders();
        assertEquals(3, recentlyOpenedFolders.size());
        assertEquals("Hobbies", recentlyOpenedFolders.get(0));
        assertEquals("CPSC 210", recentlyOpenedFolders.get(1));
        assertEquals("Education", recentlyOpenedFolders.get(2));

        openFolderFailIfFailed("CPSC 210");
        recentlyOpenedFolders = fileSystem.getNamesOfRecentlyOpenedFolders();
        assertEquals(3, recentlyOpenedFolders.size());
        assertEquals("CPSC 210", recentlyOpenedFolders.get(0));
        assertEquals("Hobbies", recentlyOpenedFolders.get(1));
        assertEquals("Education", recentlyOpenedFolders.get(2));
    }

    @Test
    @SuppressWarnings("methodlength")
    void testOpenRecentlyOpenedFolder() {
        try {
            createNumberedFolders();
        } catch (NameIsTakenException e) {
            fail();
        }

        try {
            fileSystem.openFolder("1");
            fileSystem.openRootFolder();
            fileSystem.openFolder("2");
            fileSystem.openRootFolder();
            fileSystem.openFolder("3");
            fileSystem.openRootFolder();
            fileSystem.openFolder("4");
            fileSystem.openRootFolder();
            fileSystem.openFolder("5");
            fileSystem.openRootFolder();
            fileSystem.openFolder("6");
            fileSystem.openRootFolder();
            fileSystem.openFolder("7");
            fileSystem.openRootFolder();
            fileSystem.openFolder("8");
            fileSystem.openRootFolder();
            fileSystem.openFolder("9");
            fileSystem.openRootFolder();
            fileSystem.openFolder("10");
            fileSystem.openRootFolder();
            fileSystem.openFolder("11");
            fileSystem.openRootFolder();
        } catch (NoSuchFolderFoundException e) {
            fail();
        }
        

        try {
            fileSystem.openRecentlyOpenedFolder("11");
            fileSystem.openRecentlyOpenedFolder("9");
            fileSystem.openRecentlyOpenedFolder("2");
        } catch (NoSuchFolderFoundException e) {
            fail();
        }

        try {
            fileSystem.openRecentlyOpenedFolder("1");
            fail();
        } catch (NoSuchFolderFoundException e) {
            // expected
        }

        try {
            fileSystem.openRecentlyOpenedFolder("File");
            fail();
        } catch (NoSuchFolderFoundException e) {
            // expected
        }
    }

    @Test
    @SuppressWarnings("methodlength")
    void testOpenRecentlyOpenedFolderStopTrackingRecents() {
        try {
            createNumberedFolders();
        } catch (NameIsTakenException e) {
            fail();
        }

        try {
            fileSystem.openFolder("1");
            fileSystem.openRootFolder();

            fileSystem.stopKeepingTrackOfRecents();
            fileSystem.openFolder("2");
            fileSystem.openRootFolder();
            fileSystem.openFolder("3");
            fileSystem.openRootFolder();
            fileSystem.openFolder("4");
            fileSystem.openRootFolder();
            fileSystem.startKeepingTrackOfRecents();

            fileSystem.openFolder("5");
            fileSystem.openRootFolder();
            fileSystem.openFolder("6");
            fileSystem.openRootFolder();
            fileSystem.openFolder("7");
            fileSystem.openRootFolder();
            fileSystem.openFolder("8");
            fileSystem.openRootFolder();

            fileSystem.stopKeepingTrackOfRecents();
            fileSystem.openFolder("9");
            fileSystem.openRootFolder();
            fileSystem.openFolder("10");
            fileSystem.openRootFolder();
            fileSystem.startKeepingTrackOfRecents();

            fileSystem.openFolder("11");
            fileSystem.openRootFolder();
        } catch (NoSuchFolderFoundException e) {
            fail();
        }
        
        try {
            fileSystem.openRecentlyOpenedFolder("1");
            fileSystem.openRecentlyOpenedFolder("6");
            fileSystem.openRecentlyOpenedFolder("8");
            fileSystem.openRecentlyOpenedFolder("11");
        } catch (NoSuchFolderFoundException e) {
            fail();
        }

        assertThrows(NoSuchFolderFoundException.class, () -> fileSystem.openRecentlyOpenedFolder("2"));
        assertThrows(NoSuchFolderFoundException.class, () -> fileSystem.openRecentlyOpenedFolder("3"));
        assertThrows(NoSuchFolderFoundException.class, () -> fileSystem.openRecentlyOpenedFolder("4"));
        assertThrows(NoSuchFolderFoundException.class, () -> fileSystem.openRecentlyOpenedFolder("9"));
        assertThrows(NoSuchFolderFoundException.class, () -> fileSystem.openRecentlyOpenedFolder("10"));
        assertThrows(NoSuchFolderFoundException.class, () -> fileSystem.openRecentlyOpenedFolder("15"));
    }


    /* 
     *  Tests for Label Methods:
     */

    @Test
    @SuppressWarnings("methodlength")
    void testCreateLabelAllGood() {
        try {
            emptyFileSystem.createLabel("A Label");
            emptyFileSystem.createLabel("A");
        } catch (NameIsTakenException e) {
            fail();
        }
        assertEquals(2, fileSystem.getNumLabels());
        assertTrue(emptyFileSystem.labelExists("A Label"));
        assertTrue(emptyFileSystem.labelExists("A"));

        openFolderFailIfFailed("Hobbies");
        try {
            fileSystem.createLabel("DnD");
            fileSystem.createLabel("MtG");
        } catch (NameIsTakenException e) {
            fail();
        }
        assertEquals(4, fileSystem.getNumLabels());
        assertTrue(fileSystem.labelExists("DnD"));
        assertTrue(fileSystem.labelExists("MtG"));
        assertTrue(fileSystem.labelExists("School"));
        assertTrue(fileSystem.labelExists("Personal Project"));
        assertFalse(fileSystem.labelExists("A Label"));
        assertFalse(fileSystem.labelExists("A"));
        try {
            checkEveryFileNotLabelledWith("DnD");
            checkEveryFileNotLabelledWith("MtG");
        } catch (NoSuchLabelFoundException e) {
            fail("Label just confirmed to be created not found");
        }
    }

    @Test
    @SuppressWarnings("methodlength")
    void testCreateLabelNameIsEmpty() {
        try {
            emptyFileSystem.createLabel("");
            fail("NameIsBlankException not thrown when name was empty");
        } catch (NameIsBlankException e) {
            // expected
        } catch (NameIsTakenException e) {
            fail("NameIsTakenException thrown when name was empty");
        }
        assertFalse(emptyFileSystem.anyLabelsExist());
        

        try {
            fileSystem.createLabel("");
            fail("NameIsBlankException not thrown when name was empty");
        } catch (NameIsBlankException e) {
            // expected
        } catch (NameIsTakenException e) {
            fail("NameIsTakenException thrown when name was empty");
        }
        assertFalse(fileSystem.labelExists(""));
        assertEquals(2, fileSystem.getNumLabels());

        openFolderFailIfFailed("Hobbies");
        try {
            fileSystem.createLabel("");
            fail("NameIsBlankException not thrown when name was empty");
        } catch (NameIsBlankException e) {
            // expected
        } catch (NameIsTakenException e) {
            fail("NameIsTakenException thrown when name was empty");
        }
        assertFalse(fileSystem.labelExists(""));
        assertEquals(2, fileSystem.getNumLabels());
    }

    @Test
    @SuppressWarnings("methodlength")
    void testCreateLabelNameIsTaken() {
        try {
            fileSystem.createLabel("School");
            fail("NameIsTakenException not thrown when name was taken");
        } catch (NameIsTakenException e) {
            // expected
            assertEquals("School", e.getCapitalizationOfTakenName());
        }
        assertEquals(2, fileSystem.getNumLabels());

        try {
            fileSystem.createLabel("Personal projeCT");
            fail("NameIsTakenException not thrown when name was taken");
        } catch (NameIsTakenException e) {
            // expected
            assertEquals("Personal Project", e.getCapitalizationOfTakenName());
        }
        assertEquals(2, fileSystem.getNumLabels());

        openFolderFailIfFailed("Education");
        try {
            fileSystem.createLabel("school");
            fail("NameIsTakenException not thrown when name was taken");
        } catch (NameIsTakenException e) {
            // expected
            assertEquals("School", e.getCapitalizationOfTakenName());
        }
        assertEquals(2, fileSystem.getNumLabels());

        try {
            fileSystem.createLabel("Science");
            fileSystem.createLabel("Science");
            fail("NameIsTakenException not thrown when name was taken");
        } catch (NameIsTakenException e) {
            // expected
            assertEquals("Science", e.getCapitalizationOfTakenName());
        }
        assertEquals(3, fileSystem.getNumLabels());
        assertTrue(fileSystem.labelExists("Science"));
        assertFalse(emptyFileSystem.anyLabelsExist());
    }

    // EFFECTS: goes through the premade folders of fileSystem to check that all of the premade files are not labelled
    private void checkEveryFileNotLabelledWith(String labelName) throws NoSuchLabelFoundException {
        try {
            fileSystem.openRootFolder();

            assertFalse(fileSystem.fileLabelled("File", labelName));

            fileSystem.openFolder("Education");
            assertFalse(fileSystem.fileLabelled("test", labelName));
            
            fileSystem.openFolder("CPSC 210");
            assertFalse(fileSystem.fileLabelled("Personal Project Ideas", labelName));
            assertFalse(fileSystem.fileLabelled("A", labelName));
        } catch (NoSuchFileFoundException | NoSuchFolderFoundException e) {
            fail("runBefore() changed");
        }
    }

    @Test
    @SuppressWarnings("methodlength")
    void testOpenLabelValid() {
        try {
            fileSystem.openLabel("School");
        } catch (NoSuchLabelFoundException e) {
            fail();
        }
        assertEquals("School", fileSystem.getCurrentFolderName());
        assertFalse(fileSystem.currentFolderHasParent());

        List<String> labelledFiles = fileSystem.getNamesOfSubfiles();
        assertEquals(3, labelledFiles.size());
        assertTrue(labelledFiles.contains("test"));
        assertTrue(labelledFiles.contains("Personal Project Ideas"));
        assertTrue(labelledFiles.contains("A"));
        
        List<String> recentlyOpenedLabels = fileSystem.getNamesOfRecentlyOpenedLabels();
        assertEquals(1, recentlyOpenedLabels.size());
        assertEquals("School", recentlyOpenedLabels.get(0));

        try {
            fileSystem.openLabel("Personal Project");
        } catch (NoSuchLabelFoundException e) {
            fail();
        }
        assertEquals("Personal Project", fileSystem.getCurrentFolderName());
        assertFalse(fileSystem.currentFolderHasParent());

        labelledFiles = fileSystem.getNamesOfSubfiles();
        assertEquals(1, labelledFiles.size());
        assertTrue(labelledFiles.contains("Personal Project Ideas"));

        recentlyOpenedLabels = fileSystem.getNamesOfRecentlyOpenedLabels();
        assertEquals(2, recentlyOpenedLabels.size());
        assertEquals("Personal Project", recentlyOpenedLabels.get(0));
        assertEquals("School", recentlyOpenedLabels.get(1));

        fileSystem.openRootFolder();
        openFolderFailIfFailed("Education");
        try {
            fileSystem.openLabel("School");
        } catch (NoSuchLabelFoundException e) {
            fail();
        }
        assertEquals("School", fileSystem.getCurrentFolderName());
        assertFalse(fileSystem.currentFolderHasParent());

        labelledFiles = fileSystem.getNamesOfSubfiles();
        assertEquals(3, labelledFiles.size());
        assertTrue(labelledFiles.contains("test"));
        assertTrue(labelledFiles.contains("Personal Project Ideas"));
        assertTrue(labelledFiles.contains("A"));
        
        recentlyOpenedLabels = fileSystem.getNamesOfRecentlyOpenedLabels();
        assertEquals(2, recentlyOpenedLabels.size());
        assertEquals("School", recentlyOpenedLabels.get(0));
        assertEquals("Personal Project", recentlyOpenedLabels.get(1));
    }

    @Test
    @SuppressWarnings("methodlength")
    void testOpenLabelDuplicateNames() {
        try {
            fileSystem.createFile("File Name", "some path");
            fileSystem.labelFile("File Name", "School");

            fileSystem.openFolder("Hobbies");
            fileSystem.createFile("File Name", "some other path just because");
            fileSystem.labelFile("File Name", "School");

            fileSystem.openRootFolder();
            fileSystem.openFolder("Education");
            fileSystem.createFile("File Name", "some path");
            fileSystem.labelFile("File Name", "School");

            fileSystem.openFolder("CPSC 210");
            fileSystem.createFile("File Name", "some path");
            fileSystem.labelFile("File Name", "School");

            fileSystem.openLabel("School");
        } catch (NameIsTakenException | NoSuchFileFoundException | NoSuchFolderFoundException
                | NoSuchLabelFoundException e) {
            fail();
        }

        List<String> labelledFiles = fileSystem.getNamesOfSubfiles();
        assertEquals(3 + 4, labelledFiles.size());
        assertTrue(labelledFiles.contains("File Name"));
        assertTrue(labelledFiles.contains("File Name (1)"));
        assertTrue(labelledFiles.contains("File Name (2)"));
        assertTrue(labelledFiles.contains("File Name (3)"));
        assertFalse(labelledFiles.contains("File Name (4)"));
    }

    @Test
    @SuppressWarnings("methodlength")
    void testOpenLabelMoreThanTenRecents() {
        try {
            createNumberedLabels();
        } catch (NameIsTakenException e) {
            fail();
        }

        try {
            fileSystem.openLabel("1");
            fileSystem.openLabel("2");
            fileSystem.openLabel("3");
            fileSystem.openLabel("4");
            fileSystem.openLabel("5");
            fileSystem.openLabel("6");
            fileSystem.openLabel("7");
            fileSystem.openLabel("8");
            fileSystem.openLabel("9");

            List<String> recentlyOpenedLabels = fileSystem.getNamesOfRecentlyOpenedLabels();
            assertEquals(9, recentlyOpenedLabels.size());

            fileSystem.openLabel("10");
            fileSystem.openLabel("11");
        } catch (NoSuchLabelFoundException e) {
            fail();
        }

        List<String> recentlyOpenedLabels = fileSystem.getNamesOfRecentlyOpenedLabels();
        assertEquals(10, recentlyOpenedLabels.size());
        assertEquals("11", recentlyOpenedLabels.get(0));
        assertEquals("10", recentlyOpenedLabels.get(1));
        assertEquals("9", recentlyOpenedLabels.get(2));
        assertEquals("8", recentlyOpenedLabels.get(3));
        assertEquals("7", recentlyOpenedLabels.get(4));
        assertEquals("6", recentlyOpenedLabels.get(5));
        assertEquals("5", recentlyOpenedLabels.get(6));
        assertEquals("4", recentlyOpenedLabels.get(7));
        assertEquals("3", recentlyOpenedLabels.get(8));
        assertEquals("2", recentlyOpenedLabels.get(9));
    }

    @Test
    void testOpenLabelInvalid() {
        try {
            fileSystem.openLabel("Skills");
            fail();
        } catch (NoSuchLabelFoundException e) {
            // expected
        }

        try {
            fileSystem.openLabel("Hobbies");
            fail();
        } catch (NoSuchLabelFoundException e) {
            // expected
        }
    }

    @Test
    @SuppressWarnings("methodlength")
    void testOpenLabelValidSomeRecents() {
        try {
            fileSystem.openLabel("School");
        } catch (NoSuchLabelFoundException e) {
            fail();
        }

        List<String> recentlyOpenedLabels = fileSystem.getNamesOfRecentlyOpenedLabels();
        assertEquals(1, recentlyOpenedLabels.size());
        assertEquals("School", recentlyOpenedLabels.get(0));

        fileSystem.stopKeepingTrackOfRecents();
        try {
            fileSystem.openLabel("Personal Project");
        } catch (NoSuchLabelFoundException e) {
            fail();
        }
        recentlyOpenedLabels = fileSystem.getNamesOfRecentlyOpenedLabels();
        assertEquals(1, recentlyOpenedLabels.size());
        assertEquals("School", recentlyOpenedLabels.get(0));

        fileSystem.startKeepingTrackOfRecents();
        fileSystem.openRootFolder();
        openFolderFailIfFailed("Education");
        try {
            fileSystem.openLabel("Personal Project");
        } catch (NoSuchLabelFoundException e) {
            fail();
        }
        recentlyOpenedLabels = fileSystem.getNamesOfRecentlyOpenedLabels();
        assertEquals(2, recentlyOpenedLabels.size());
        assertEquals("Personal Project", recentlyOpenedLabels.get(0));
        assertEquals("School", recentlyOpenedLabels.get(1));
    }

    @Test
    @SuppressWarnings("methodlength")
    void testOpenLabelValidNoRecents() {
        fileSystem.stopKeepingTrackOfRecents();
        try {
            fileSystem.openLabel("School");
        } catch (NoSuchLabelFoundException e) {
            fail();
        }
        assertTrue(fileSystem.getNamesOfRecentlyOpenedLabels().isEmpty());

        try {
            fileSystem.openLabel("Personal Project");
        } catch (NoSuchLabelFoundException e) {
            fail();
        }
        assertTrue(fileSystem.getNamesOfRecentlyOpenedLabels().isEmpty());

        fileSystem.openRootFolder();
        openFolderFailIfFailed("Education");
        try {
            fileSystem.openLabel("School");
        } catch (NoSuchLabelFoundException e) {
            fail();
        }
        assertTrue(fileSystem.getNamesOfRecentlyOpenedLabels().isEmpty());
    }

    @Test
    @SuppressWarnings("methodlength")
    void testOpenLabelMoreThanTenRecentsNoRecents() {
        fileSystem.stopKeepingTrackOfRecents();
        try {
            createNumberedLabels();
        } catch (NameIsTakenException e) {
            fail();
        }

        try {
            fileSystem.openLabel("1");
            fileSystem.openLabel("2");
            fileSystem.openLabel("3");
            fileSystem.openLabel("4");
            fileSystem.openLabel("5");
            fileSystem.openLabel("6");
            fileSystem.openLabel("7");
            fileSystem.openLabel("8");
            fileSystem.openLabel("9");

            assertTrue(fileSystem.getNamesOfRecentlyOpenedLabels().isEmpty());

            fileSystem.openLabel("10");
            fileSystem.openLabel("11");
        } catch (NoSuchLabelFoundException e) {
            fail();
        }
        assertTrue(fileSystem.getNamesOfRecentlyOpenedLabels().isEmpty());
    }

    @Test
    @SuppressWarnings("methodlength")
    void testDeleteLabelValid() {
        try {
            fileSystem.deleteLabel("School");
        } catch (NoSuchLabelFoundException e) {
            fail("NoSuchLabelFoundException when Label exists");
        }
        assertEquals(1, fileSystem.getNumLabels());
        assertFalse(fileSystem.labelExists("School"));
        assertFalse(fileSystem.getNamesOfRecentlyOpenedLabels().contains("School"));
        
        
        openFolderFailIfFailed("Education");
        try {
            assertEquals(0, fileSystem.getNumLabelsOnFile("test"));
        } catch (NoSuchFileFoundException e) {
            fail();
        }

        try {
            fileSystem.deleteLabel("Personal Project");
        } catch (NoSuchLabelFoundException e) {
            fail("NoSuchLabelFoundException when Label exists");
        }

        assertEquals(0, fileSystem.getNumLabels());
        assertFalse(fileSystem.labelExists("Personal Project"));
        assertFalse(fileSystem.getNamesOfRecentlyOpenedLabels().contains("School"));
        
        
        openFolderFailIfFailed("CPSC 210");
        try {
            assertEquals(0, fileSystem.getNumLabelsOnFile("Personal Project Ideas"));
            assertEquals(0, fileSystem.getNumLabelsOnFile("A"));
        } catch (NoSuchFileFoundException e) {
            fail();
        }
    }

    @Test
    void testDeleteLabelFail() {
        try {
            fileSystem.deleteLabel("Hello World!");
            fail("No exception thrown when deleting non-existent Label");
        } catch (NoSuchLabelFoundException e) {
            // expected
        }
        try {
            fileSystem.deleteLabel("What's up?");
            fail("No exception thrown when deleting non-existent Label");
        } catch (NoSuchLabelFoundException e) {
            // expected
        }
        assertEquals(2, fileSystem.getNumLabels());
    }

    @Test
    void testLabelFileValid() {
        try {
            fileSystem.labelFile("File", "School");

            assertEquals(1, fileSystem.getNumLabelsOnFile("File"));
            assertTrue(fileSystem.fileLabelled("File", "School"));
        } catch (NoSuchFileFoundException | NoSuchLabelFoundException e) {
            fail();
        }

        openFolderFailIfFailed("Education");
        try {
            fileSystem.labelFile("test", "School");
            fileSystem.labelFile("test", "School");

            assertEquals(1, fileSystem.getNumLabelsOnFile("test"));
            assertTrue(fileSystem.fileLabelled("test", "School"));
            
            fileSystem.labelFile("test", "Personal Project");
            assertEquals(2, fileSystem.getNumLabelsOnFile("test"));
            assertTrue(fileSystem.fileLabelled("test", "Personal Project"));
        } catch (NoSuchFileFoundException | NoSuchLabelFoundException e) {
            fail();
        }
    }

    @Test
    void testLabelFileNoSuchFile() {
        try {
            fileSystem.labelFile("this is a great, very real file", "School");
            fail();
        } catch (NoSuchFileFoundException e) {
            // expected
        } catch (NoSuchLabelFoundException e) {
            fail();
        }

        openFolderFailIfFailed("Hobbies");
        try {
            fileSystem.labelFile("File", "Personal Project");
            fail();
        } catch (NoSuchFileFoundException e) {
            // expected
        } catch (NoSuchLabelFoundException e) {
            fail();
        }
    }

    @Test
    void testLabelFileNoSuchLabel() {
        try {
            fileSystem.labelFile("File", "super real label");
            fail();
        } catch (NoSuchFileFoundException e) {
            fail();
        } catch (NoSuchLabelFoundException e) {
            // expected
        }

        openFolderFailIfFailed("Education");
        try {
            fileSystem.labelFile("test", "Label");
            fail();
        } catch (NoSuchFileFoundException e) {
            fail();
        } catch (NoSuchLabelFoundException e) {
            // expected
        }
    }

    @Test
    void testLabelFileNoSuchFileAndNoSuchLabel() {
        try {
            fileSystem.labelFile("super existing", "exists, trust me");
            fail();
        } catch (NoSuchFileFoundException e) {
            // expected
        } catch (NoSuchLabelFoundException e) {
            fail();
        }

        openFolderFailIfFailed("Hobbies");
        try {
            fileSystem.labelFile("File", "Scone");
            fail();
        } catch (NoSuchFileFoundException e) {
            // expected
        } catch (NoSuchLabelFoundException e) {
            fail();
        }
    }

    @Test
    void testUnlabelFileValid() {
        openFolderFailIfFailed("Education");
        try {
            fileSystem.unlabelFile("test", "School");

            assertEquals(0, fileSystem.getNumLabelsOnFile("test"));
            assertFalse(fileSystem.fileLabelled("test", "School"));
        } catch (NoSuchFileFoundException | NoSuchLabelFoundException e) {
            fail();
        }

        openFolderFailIfFailed("CPSC 210");
        try {
            fileSystem.unlabelFile("A", "School");
            fileSystem.unlabelFile("A", "School");

            assertEquals(0, fileSystem.getNumLabelsOnFile("A"));
            assertFalse(fileSystem.fileLabelled("A", "School"));
            
            fileSystem.unlabelFile("Personal Project Ideas", "Personal Project");
            assertEquals(1, fileSystem.getNumLabelsOnFile("Personal Project Ideas"));
            assertFalse(fileSystem.fileLabelled("Personal Project Ideas", "Personal Project"));
            assertTrue(fileSystem.fileLabelled("Personal Project Ideas", "School"));
        } catch (NoSuchFileFoundException | NoSuchLabelFoundException e) {
            fail();
        }
    }

    @Test
    void testUnlabelFileNoSuchFile() {
        try {
            fileSystem.unlabelFile("this is a great, very real file", "School");
            fail();
        } catch (NoSuchFileFoundException e) {
            // expected
        } catch (NoSuchLabelFoundException e) {
            fail();
        }

        openFolderFailIfFailed("Hobbies");
        try {
            fileSystem.unlabelFile("File", "Personal Project");
            fail();
        } catch (NoSuchFileFoundException e) {
            // expected
        } catch (NoSuchLabelFoundException e) {
            fail();
        }
    }

    @Test
    void testUnlabelFileNoSuchLabel() {
        try {
            fileSystem.unlabelFile("File", "super real label");
            fail();
        } catch (NoSuchFileFoundException e) {
            fail();
        } catch (NoSuchLabelFoundException e) {
            // expected
        }

        openFolderFailIfFailed("Education");
        try {
            fileSystem.unlabelFile("test", "Label");
            fail();
        } catch (NoSuchFileFoundException e) {
            fail();
        } catch (NoSuchLabelFoundException e) {
            // expected
        }
    }

    @Test
    void testUnlabelFileNoSuchFileAndNoSuchLabel() {
        try {
            fileSystem.unlabelFile("super existing", "exists, trust me");
            fail();
        } catch (NoSuchFileFoundException e) {
            // expected
        } catch (NoSuchLabelFoundException e) {
            fail();
        }

        openFolderFailIfFailed("Hobbies");
        try {
            fileSystem.unlabelFile("File", "Scone");
            fail();
        } catch (NoSuchFileFoundException e) {
            // expected
        } catch (NoSuchLabelFoundException e) {
            fail();
        }
    }

    @Test
    void testSetLabelNameSucceed() {
        try {
            fileSystem.setLabelName("School", "Non-silly label name");
        } catch (NoSuchLabelFoundException | NameIsTakenException e) {
            fail();
        }
        assertFalse(fileSystem.labelExists("Label"));
        assertTrue(fileSystem.labelExists("Non-silly label name"));

        openFolderFailIfFailed("Education");
        try {
            fileSystem.setLabelName("Personal Project", "Future Plans");
        } catch (NoSuchLabelFoundException | NameIsTakenException e) {
            fail();
        }
        assertFalse(fileSystem.labelExists("Personal Project"));
        assertTrue(fileSystem.labelExists("Future Plans"));
    }

    @Test
    @SuppressWarnings("methodlength")
    void testSetLabelNameNameIsTaken() {
        try {
            fileSystem.setLabelName("School", "PersOnal project");
            fail();
        } catch (NameIsTakenException e) {
            // expected
            assertEquals("Personal Project", e.getCapitalizationOfTakenName());
        } catch (NoSuchLabelFoundException e) {
            fail();
        }
        confirmLabelsUnchanged();

        openFolderFailIfFailed("Hobbies");
        try {
            fileSystem.setLabelName("Personal Project", "Personal ProJEct");
            fail();
        } catch (NameIsTakenException e) {
            // expected
            assertEquals("Personal Project", e.getCapitalizationOfTakenName());
        } catch (NoSuchLabelFoundException e) {
            fail();
        }
        try {
            fileSystem.setLabelName("Personal Project", "sCHOOL");
            fail();
        } catch (NameIsTakenException e) {
            // expected
            assertEquals("School", e.getCapitalizationOfTakenName());
        } catch (NoSuchLabelFoundException e) {
            fail();
        }
        confirmLabelsUnchanged();
    }

    @Test
    void testSetLabelNameNotFound() {
        try {
            fileSystem.setLabelName("This label doesn't exist", "Name");
            fail();
        } catch (NoSuchLabelFoundException e) {
            // expected
        } catch (NameIsTakenException e) {
            fail();
        }
        confirmLabelsUnchanged();

        openFolderFailIfFailed("Education");
        try {
            fileSystem.setLabelName("Real label for sure", "Labelname");
            fail();
        } catch (NoSuchLabelFoundException e) {
            // expected
        } catch (NameIsTakenException e) {
            fail();
        }
        confirmLabelsUnchanged();
    }

    @Test
    void testSetLabelNameTakenAndNotFound() {
        try {
            fileSystem.setLabelName("This file doesn't exist", "ScHoOl");
            fail();
        } catch (NameIsTakenException e) {
            // expected
            assertEquals("School", e.getCapitalizationOfTakenName());
        } catch (NoSuchLabelFoundException e) {
            // NameIsTakenException should be thrown first
            fail();
        }
        confirmLabelsUnchanged();
    }

    @Test
    void testSetLabelNameNewNameEmpty() {
        try {
            fileSystem.setLabelName("School", "");
            fail();
        } catch (NameIsTakenException | NoSuchLabelFoundException e) {
            fail();
        } catch (NameIsBlankException e) {
            // expected
        }
        confirmLabelsUnchanged();
    }

    @Test
    void testSetLabelNameEmptyAndNotFound() {
        try {
            fileSystem.setLabelName("non-existent label", "");
            fail();
        } catch (NameIsTakenException e) {
            fail();
        } catch (NoSuchLabelFoundException e) {
            // expected
        }
        confirmLabelsUnchanged();
        
        try {
            fileSystem.setLabelName("", "");
            fail();
        } catch (NameIsTakenException e) {
            fail();
        } catch (NoSuchLabelFoundException e) {
            // expected
        }
        confirmLabelsUnchanged();
    }

    @Test
    void testSetLabelNameTakenAndEmpty() {
        try {
            fileSystem.setLabelName("", "Personal Project");
            fail();
        } catch (NoSuchLabelFoundException e) {
            // NameIsTakenException should be thrown first
            fail();
        } catch (NameIsTakenException e) {
            // expected
            assertEquals("Personal Project", e.getCapitalizationOfTakenName());
        }
        confirmLabelsUnchanged();
        
        try {
            fileSystem.setLabelName("", "School");
            fail();
        } catch (NoSuchLabelFoundException e) {
            // NameIsTakenException should be thrown first
            fail();
        } catch (NameIsTakenException e) {
            // expected
            assertEquals("School", e.getCapitalizationOfTakenName());
        }
        confirmLabelsUnchanged();
    }

    @Test
    void testAnyLabelsExist() {
        assertTrue(fileSystem.anyLabelsExist());
        assertFalse(emptyFileSystem.anyLabelsExist());

        try {
            emptyFileSystem.createLabel("New Label");
        } catch (NameIsTakenException e) {
            fail();
        }
        assertTrue(emptyFileSystem.anyLabelsExist());
    }

    @Test
    void testExactlyOneLabelExists() {
        assertFalse(fileSystem.exactlyOneLabelExists());
        assertFalse(emptyFileSystem.exactlyOneLabelExists());

        try {
            emptyFileSystem.createLabel("New Label");
        } catch (NameIsTakenException e) {
            fail();
        }
        assertTrue(emptyFileSystem.exactlyOneLabelExists());
    }

    @Test
    void testLabelExists() {
        assertFalse(fileSystem.labelExists("Label"));
        assertFalse(fileSystem.labelExists("random label #3"));
        assertFalse(fileSystem.labelExists(""));
        assertTrue(fileSystem.labelExists("School"));
        assertTrue(fileSystem.labelExists("school"));
        assertTrue(fileSystem.labelExists("SCHOOL"));
        assertTrue(fileSystem.labelExists("sChOoL"));
        assertTrue(fileSystem.labelExists("sCHOOL"));
        assertTrue(fileSystem.labelExists("schOOl"));
        assertTrue(fileSystem.labelExists("Personal Project"));
    }

    @Test
    @SuppressWarnings("methodlength")
    void testGetCapitalizationOfLabel() {
        assertEquals("School", fileSystem.getCapitalizationOfLabel("School"));
        assertEquals("School", fileSystem.getCapitalizationOfLabel("school"));
        assertEquals("School", fileSystem.getCapitalizationOfLabel("schOol"));
        assertEquals("School", fileSystem.getCapitalizationOfLabel("scHool"));
        assertEquals("School", fileSystem.getCapitalizationOfLabel("schOOL"));
        assertEquals("School", fileSystem.getCapitalizationOfLabel("SCHOOL"));

        openFolderFailIfFailed("Hobbies");

        assertEquals("Personal Project", fileSystem.getCapitalizationOfLabel("Personal Project"));
        assertEquals("Personal Project", fileSystem.getCapitalizationOfLabel("Personal Project"));
        assertEquals("Personal Project", fileSystem.getCapitalizationOfLabel("PERSONAL PROJECT"));
        assertEquals("Personal Project", fileSystem.getCapitalizationOfLabel("personal project"));
        assertEquals("Personal Project", fileSystem.getCapitalizationOfLabel("pERSONAL pROJECT"));

        // Breaking REQUIRES clause for code coverage:
        try {
            fileSystem.getCapitalizationOfLabel("F");
            fail();
        } catch (RequiresClauseNotMetRuntimeException e) {
            // expected
        }
    }

    @Test
    void testRemoveAllLabelsNoLabels() {
        try {
            fileSystem.removeAllLabels("File");
            assertEquals(0, fileSystem.getNumLabelsOnFile("File"));
        } catch (NoSuchFileFoundException e) {
            fail();
        }

        openFolderFailIfFailed("Hobbies");
        try {
            fileSystem.createFile("File", "path");
        } catch (NameIsTakenException e) {
            fail();
        }
        try {
            fileSystem.removeAllLabels("File");
            assertEquals(0, fileSystem.getNumLabelsOnFile("File"));
        } catch (NoSuchFileFoundException e) {
            fail();
        }
    }

    @Test
    void testRemoveAllLabelsOneLabels() {
        openFolderFailIfFailed("Education");
        try {
            fileSystem.removeAllLabels("test");
            assertEquals(0, fileSystem.getNumLabelsOnFile("test"));
        } catch (NoSuchFileFoundException e) {
            fail();
        }

        openFolderFailIfFailed("CPSC 210");
        try {
            fileSystem.removeAllLabels("A");
            assertEquals(0, fileSystem.getNumLabelsOnFile("A"));
        } catch (NoSuchFileFoundException e) {
            fail();
        }
    }

    @Test
    void testRemoveAllLabelsMultipleLabels() {
        openFolderFailIfFailed("Education");
        openFolderFailIfFailed("CPSC 210");
        try {
            fileSystem.removeAllLabels("Personal Project Ideas");
            assertEquals(0, fileSystem.getNumLabelsOnFile("Personal Project Ideas"));
        } catch (NoSuchFileFoundException e) {
            fail();
        }
    }

    @Test
    void testRemoveAllLabelsNoSuchFile() {
        try {
            fileSystem.removeAllLabels("this file doesn't exist");
            fail();
        } catch (NoSuchFileFoundException e) {
            // expected
        }
        
        openFolderFailIfFailed("Hobbies");
        try {
            fileSystem.removeAllLabels("File");
            fail();
        } catch (NoSuchFileFoundException e) {
            // expected
        }
    }

    @Test
    @SuppressWarnings("methodlength")
    void testGetOnlyLabelName() {
        try {
            fileSystem.deleteLabel("Personal Project");
        } catch (NoSuchLabelFoundException e) {
            fail();
        }
        assertEquals("School", fileSystem.getOnlyLabelName());

        try {
            fileSystem.deleteLabel("School");
            fileSystem.createLabel("new Label");
        } catch (NoSuchLabelFoundException | NameIsTakenException e) {
            fail();
        }
        assertEquals("new Label", fileSystem.getOnlyLabelName());

        try {
            emptyFileSystem.createLabel("Bears");
        } catch (NameIsTakenException e) {
            fail();
        }
        assertEquals("Bears", emptyFileSystem.getOnlyLabelName());

        try {
            fileSystem.deleteLabel("new Label");
            assertNull(fileSystem.getOnlyLabelName());
        } catch (NoSuchLabelFoundException e) {
            fail();
        }

        try {
            emptyFileSystem.deleteLabel("Bears");
            assertNull(emptyFileSystem.getOnlyLabelName());
        } catch (NoSuchLabelFoundException e) {
            fail();
        }
    }

    @Test
    void testGetNumLabelsOnFileNoLabels() {
        try {
            assertEquals(0, fileSystem.getNumLabelsOnFile("File"));

            fileSystem.openFolder("Education");
            fileSystem.createFile("other file", "path");
            assertEquals(0, fileSystem.getNumLabelsOnFile("other file"));

            fileSystem.openFolder("CPSC 210");
            fileSystem.createFile("File", "yup");
            assertEquals(0, fileSystem.getNumLabelsOnFile("File"));
        } catch (NoSuchFileFoundException | NoSuchFolderFoundException | NameIsTakenException e) {
            fail();
        }
    }

    @Test
    void testGetNumLabelsOnFileOneLabel() {
        try {
            fileSystem.createFile("other file", "path");
            fileSystem.labelFile("other file", "School");
            assertEquals(1, fileSystem.getNumLabelsOnFile("other file"));

            fileSystem.openFolder("Education");
            assertEquals(1, fileSystem.getNumLabelsOnFile("test"));
        } catch (NoSuchFileFoundException | NoSuchFolderFoundException | NameIsTakenException
                | NoSuchLabelFoundException e) {
            fail();
        }
    }

    @Test
    void testGetNumLabelsOnFileMultipleLabels() {
        try {
            fileSystem.createFile("other file", "path");
            fileSystem.labelFile("other file", "School");
            fileSystem.labelFile("other file", "Personal Project");
            assertEquals(2, fileSystem.getNumLabelsOnFile("other file"));

            fileSystem.openFolder("Education");
            fileSystem.openFolder("CPSC 210");
            assertEquals(2, fileSystem.getNumLabelsOnFile("Personal Project Ideas"));
        } catch (NoSuchFileFoundException | NoSuchFolderFoundException | NameIsTakenException
                | NoSuchLabelFoundException e) {
            fail();
        }
    }

    @Test
    void testGetNumLabelsOnFileNotFound() {
        try {
            fileSystem.getNumLabelsOnFile("Non-existent file");
            fail();
        } catch (NoSuchFileFoundException e) {
            // expected
        }

        openFolderFailIfFailed("Hobbies");
        try {
            fileSystem.getNumLabelsOnFile("titanium");
            fail();
        } catch (NoSuchFileFoundException e) {
            // expected
        }
    }

    @Test
    void testGetNumLabelsNotOnFileNoLabels() {
        try {
            assertEquals(2, fileSystem.getNumLabelsNotOnFile("File"));

            fileSystem.openFolder("Education");
            fileSystem.createFile("other file", "path");
            fileSystem.createLabel("New Label");
            assertEquals(3, fileSystem.getNumLabelsNotOnFile("other file"));

            fileSystem.openFolder("CPSC 210");
            fileSystem.createFile("File", "yup");
            assertEquals(3, fileSystem.getNumLabelsNotOnFile("File"));
        } catch (NoSuchFileFoundException | NoSuchFolderFoundException | NameIsTakenException e) {
            fail();
        }
    }

    @Test
    void testGetNumLabelsNotOnFileOneLabel() {
        try {
            fileSystem.createFile("other file", "path");
            fileSystem.labelFile("other file", "School");
            assertEquals(1, fileSystem.getNumLabelsNotOnFile("other file"));

            fileSystem.openFolder("Education");
            fileSystem.createLabel("Lab");
            assertEquals(2, fileSystem.getNumLabelsNotOnFile("test"));
        } catch (NoSuchFileFoundException | NoSuchFolderFoundException | NameIsTakenException
                | NoSuchLabelFoundException e) {
            fail();
        }
    }

    @Test
    void testGetNumLabelsNotOnFileMultipleLabels() {
        try {
            fileSystem.createFile("other file", "path");
            fileSystem.createLabel("new Label");
            fileSystem.labelFile("other file", "School");
            fileSystem.labelFile("other file", "Personal Project");
            fileSystem.labelFile("other file", "new Label");
            assertEquals(0, fileSystem.getNumLabelsNotOnFile("other file"));

            openFolderFailIfFailed("Education");
            openFolderFailIfFailed("CPSC 210");
            assertEquals(1, fileSystem.getNumLabelsNotOnFile("Personal Project Ideas"));
        } catch (NoSuchFileFoundException | NameIsTakenException | NoSuchLabelFoundException e) {
            fail();
        }
    }

    @Test
    void testGetNumLabelsNotOnFileNotFound() {
        try {
            fileSystem.getNumLabelsNotOnFile("Non-existent file");
            fail();
        } catch (NoSuchFileFoundException e) {
            // expected
        }

        openFolderFailIfFailed("Hobbies");
        try {
            fileSystem.getNumLabelsNotOnFile("titanium");
            fail();
        } catch (NoSuchFileFoundException e) {
            // expected
        }
    }

    @Test
    void testGetNamesOfLabels() {
        assertTrue(emptyFileSystem.getNamesOfLabels().isEmpty());

        List<String> labels = fileSystem.getNamesOfLabels();
        assertEquals(2, labels.size());
        assertTrue(labels.contains("School"));
        assertTrue(labels.contains("Personal Project"));

        try {
            fileSystem.createLabel("Brand New label");
        } catch (NameIsTakenException e) {
            fail();
        }

        labels = fileSystem.getNamesOfLabels();
        assertEquals(3, labels.size());
        assertTrue(labels.contains("School"));
        assertTrue(labels.contains("Personal Project"));
        assertTrue(labels.contains("Brand New label"));
    }

    @Test
    void testGetNamesOfLabelsOnFileNone() {
        try {
            assertTrue(fileSystem.getNamesOfLabelsOnFile("File").isEmpty());

            openFolderFailIfFailed("Hobbies");
            fileSystem.createFile("Labelless File", "random path");

            assertTrue(fileSystem.getNamesOfLabelsOnFile("Labelless File").isEmpty());
        } catch (NoSuchFileFoundException | NameIsTakenException e) {
            fail();
        }
    }

    @Test
    void testGetNamesOfLabelsOnFileOne() {
        try {
            openFolderFailIfFailed("Education");
            List<String> labels = fileSystem.getNamesOfLabelsOnFile("test");
            assertEquals(1, labels.size());
            assertTrue(labels.contains("School"));
            
            openFolderFailIfFailed("CPSC 210");
            labels = fileSystem.getNamesOfLabelsOnFile("A");
            assertEquals(1, labels.size());
            assertTrue(labels.contains("School"));
        } catch (NoSuchFileFoundException e) {
            fail();
        }
    }

    @Test
    void testGetNamesOfLabelsOnFileMultiple() {
        try {
            openFolderFailIfFailed("Education");
            fileSystem.labelFile("test", "Personal Project");
            List<String> labels = fileSystem.getNamesOfLabelsOnFile("test");
            assertEquals(2, labels.size());
            assertTrue(labels.contains("School"));
            assertTrue(labels.contains("Personal Project"));
            
            openFolderFailIfFailed("CPSC 210");
            labels = fileSystem.getNamesOfLabelsOnFile("Personal Project Ideas");
            assertEquals(2, labels.size());
            assertTrue(labels.contains("School"));
            assertTrue(labels.contains("Personal Project"));
        } catch (NoSuchFileFoundException | NoSuchLabelFoundException e) {
            fail();
        }
    }

    @Test
    void testGetNamesOfLabelsOnFileNotFound() {
        try {
            fileSystem.getNamesOfLabelsOnFile("File that doesn't exist");
            fail();
        } catch (NoSuchFileFoundException e) {
            // expected
        }

        openFolderFailIfFailed("Hobbies");
        try {
            fileSystem.getNamesOfLabelsOnFile("File");
            fail();
        } catch (NoSuchFileFoundException e) {
            // expected
        }
    }

    @Test
    void testGetNamesOfLabelsNotOnFileNone() {
        try {
            List<String> labels = fileSystem.getNamesOfLabelsNotOnFile("File");
            assertEquals(2, labels.size());
            assertTrue(labels.contains("School"));
            assertTrue(labels.contains("Personal Project"));

            openFolderFailIfFailed("Hobbies");
            fileSystem.createFile("Labelless File", "random path");

            labels = fileSystem.getNamesOfLabelsNotOnFile("Labelless File");
            assertEquals(2, labels.size());
            assertTrue(labels.contains("School"));
            assertTrue(labels.contains("Personal Project"));
        } catch (NoSuchFileFoundException | NameIsTakenException e) {
            fail();
        }
    }

    @Test
    void testGetNamesOfLabelsNotOnFileOne() {
        try {
            openFolderFailIfFailed("Education");
            List<String> labels = fileSystem.getNamesOfLabelsNotOnFile("test");
            assertEquals(1, labels.size());
            assertFalse(labels.contains("School"));
            assertTrue(labels.contains("Personal Project"));
            
            openFolderFailIfFailed("CPSC 210");
            fileSystem.createLabel("new label");
            fileSystem.createFile("new File", "the path");
            fileSystem.labelFile("new File", "Personal Project");

            labels = fileSystem.getNamesOfLabelsNotOnFile("new File");
            assertEquals(2, labels.size());
            assertTrue(labels.contains("School"));
            assertFalse(labels.contains("Personal Project"));
            assertTrue(labels.contains("new label"));
        } catch (NoSuchFileFoundException | NameIsTakenException | NoSuchLabelFoundException e) {
            fail();
        }
    }

    @Test
    void testGetNamesOfLabelsNotOnFileMultiple() {
        try {
            openFolderFailIfFailed("Education");
            fileSystem.labelFile("test", "Personal Project");
            assertTrue(fileSystem.getNamesOfLabelsNotOnFile("test").isEmpty());
            
            openFolderFailIfFailed("CPSC 210");
            fileSystem.createLabel("Brand New Label");
            List<String> labels = fileSystem.getNamesOfLabelsNotOnFile("Personal Project Ideas");
            assertEquals(1, labels.size());
            assertTrue(labels.contains("Brand New Label"));
            assertFalse(labels.contains("School"));
            assertFalse(labels.contains("Personal Project"));
        } catch (NoSuchFileFoundException | NoSuchLabelFoundException | NameIsTakenException e) {
            fail();
        }
    }

    @Test
    void testGetNamesOfLabelsNotOnFileNotFound() {
        try {
            fileSystem.getNamesOfLabelsNotOnFile("File that doesn't exist");
            fail();
        } catch (NoSuchFileFoundException e) {
            // expected
        }

        openFolderFailIfFailed("Hobbies");
        try {
            fileSystem.getNamesOfLabelsNotOnFile("File");
            fail();
        } catch (NoSuchFileFoundException e) {
            // expected
        }
    }

    @Test
    @SuppressWarnings("methodlength")
    void testGetNamesOfRecentlyOpenedLabels() {
        try {
            fileSystem.openLabel("School");
        } catch (NoSuchLabelFoundException e) {
            fail();
        }

        List<String> recentlyOpenedLabels = fileSystem.getNamesOfRecentlyOpenedLabels();
        assertEquals(1, recentlyOpenedLabels.size());
        assertEquals("School", recentlyOpenedLabels.get(0));
        

        try {
            fileSystem.openLabel("Personal Project");
        } catch (NoSuchLabelFoundException e) {
            fail();
        }

        recentlyOpenedLabels = fileSystem.getNamesOfRecentlyOpenedLabels();
        assertEquals(2, recentlyOpenedLabels.size());
        assertEquals("Personal Project", recentlyOpenedLabels.get(0));
        assertEquals("School", recentlyOpenedLabels.get(1));

        try {
            fileSystem.openLabel("Hello World!");
            fail();
        } catch (NoSuchLabelFoundException e) {
            // expected
        }

        try {
            fileSystem.createLabel("New Label");
            fileSystem.openLabel("New Label");
        } catch (NoSuchLabelFoundException | NameIsTakenException e) {
            fail();
        }

        recentlyOpenedLabels = fileSystem.getNamesOfRecentlyOpenedLabels();
        assertEquals(3, recentlyOpenedLabels.size());
        assertEquals("New Label", recentlyOpenedLabels.get(0));
        assertEquals("Personal Project", recentlyOpenedLabels.get(1));
        assertEquals("School", recentlyOpenedLabels.get(2));

        try {
            fileSystem.openLabel("Personal Project");
        } catch (NoSuchLabelFoundException e) {
            fail();
        }
        
        recentlyOpenedLabels = fileSystem.getNamesOfRecentlyOpenedLabels();
        assertEquals(3, recentlyOpenedLabels.size());
        assertEquals("Personal Project", recentlyOpenedLabels.get(0));
        assertEquals("New Label", recentlyOpenedLabels.get(1));
        assertEquals("School", recentlyOpenedLabels.get(2));

        try {
            fileSystem.openLabel("School");
        } catch (NoSuchLabelFoundException e) {
            fail();
        }
        recentlyOpenedLabels = fileSystem.getNamesOfRecentlyOpenedLabels();
        assertEquals(3, recentlyOpenedLabels.size());
        assertEquals("School", recentlyOpenedLabels.get(0));
        assertEquals("Personal Project", recentlyOpenedLabels.get(1));
        assertEquals("New Label", recentlyOpenedLabels.get(2));
    }

    @Test
    @SuppressWarnings("methodlength")
    void testGetNamesOfRecentlyOpenedLabelsLengthLimit() {
        try {
            createNumberedLabels();

            openLabelAndCheck("1", 1);
            openLabelAndCheck("2", 2);
            openLabelAndCheck("3", 3);
            openLabelAndCheck("4", 4);
            openLabelAndCheck("5", 5);
            openLabelAndCheck("6", 6);
            openLabelAndCheck("7", 7);
            openLabelAndCheck("8", 8);
            openLabelAndCheck("9", 9);
            openLabelAndCheck("10", 10);
            openLabelAndCheck("11", 10);
        } catch (NameIsTakenException | NoSuchLabelFoundException e) {
            fail();
        }
        List<String> recentlyOpenedLabels = fileSystem.getNamesOfRecentlyOpenedLabels();
        assertEquals(10, recentlyOpenedLabels.size());
        assertEquals("11", recentlyOpenedLabels.get(0));
        assertEquals("10", recentlyOpenedLabels.get(1));
        assertEquals("9", recentlyOpenedLabels.get(2));
        assertEquals("8", recentlyOpenedLabels.get(3));
        assertEquals("7", recentlyOpenedLabels.get(4));
        assertEquals("6", recentlyOpenedLabels.get(5));
        assertEquals("5", recentlyOpenedLabels.get(6));
        assertEquals("4", recentlyOpenedLabels.get(7));
        assertEquals("3", recentlyOpenedLabels.get(8));
        assertEquals("2", recentlyOpenedLabels.get(9));
    }

    // EFFECTS: opens Label named labelName then asserts that the recentlyOpenedLabels list is of size listSize and
    // that its first element is labelName
    private void openLabelAndCheck(String labelName, int listSize) throws NoSuchLabelFoundException {
        fileSystem.openLabel(labelName);
        List<String> recentlyOpenedLabels = fileSystem.getNamesOfRecentlyOpenedLabels();
        assertEquals(listSize, recentlyOpenedLabels.size());
        assertEquals(labelName, recentlyOpenedLabels.get(0));
    }

    @Test
    @SuppressWarnings("methodlength")
    void testGetNamesOfRecentlyOpenedLabelsStopTrackingRecents() {
        try {
            fileSystem.openLabel("School");
        } catch (NoSuchLabelFoundException e) {
            fail();
        }
        List<String> recentlyOpenedLabels = fileSystem.getNamesOfRecentlyOpenedLabels();
        assertEquals(1, recentlyOpenedLabels.size());
        assertEquals("School", recentlyOpenedLabels.get(0));
        
        fileSystem.stopKeepingTrackOfRecents();
        try {
            fileSystem.openLabel("Personal Project");
        } catch (NoSuchLabelFoundException e) {
            fail();
        }
        recentlyOpenedLabels = fileSystem.getNamesOfRecentlyOpenedLabels();
        assertEquals(1, recentlyOpenedLabels.size());
        assertEquals("School", recentlyOpenedLabels.get(0));

        fileSystem.startKeepingTrackOfRecents();
        try {
            fileSystem.createLabel("New Label");
            fileSystem.openLabel("New Label");
        } catch (NoSuchLabelFoundException | NameIsTakenException e) {
            fail();
        }
        recentlyOpenedLabels = fileSystem.getNamesOfRecentlyOpenedLabels();
        assertEquals(2, recentlyOpenedLabels.size());
        assertEquals("New Label", recentlyOpenedLabels.get(0));
        assertEquals("School", recentlyOpenedLabels.get(1));
    }

    @Test
    @SuppressWarnings("methodlength")
    void testOpenRecentlyOpenedLabels() {
        try {
            createNumberedLabels();
        } catch (NameIsTakenException e) {
            fail();
        }

        try {
            fileSystem.openLabel("1");
            fileSystem.openLabel("2");
            fileSystem.openLabel("3");
            fileSystem.openLabel("4");
            fileSystem.openLabel("5");
            fileSystem.openLabel("6");
            fileSystem.openLabel("7");
            fileSystem.openLabel("8");
            fileSystem.openLabel("9");
            fileSystem.openLabel("10");
            fileSystem.openLabel("11");
        } catch (NoSuchLabelFoundException e) {
            fail();
        }
        

        try {
            fileSystem.openRecentlyOpenedLabel("11");
            fileSystem.openRecentlyOpenedLabel("9");
            fileSystem.openRecentlyOpenedLabel("2");
        } catch (NoSuchLabelFoundException e) {
            fail();
        }

        try {
            fileSystem.openRecentlyOpenedLabel("1");
            fail();
        } catch (NoSuchLabelFoundException e) {
            // expected
        }

        try {
            fileSystem.openRecentlyOpenedLabel("File");
            fail();
        } catch (NoSuchLabelFoundException e) {
            // expected
        }
    }

    @Test
    @SuppressWarnings("methodlength")
    void testOpenRecentlyOpenedLabelsStopTrackingRecents() {
        try {
            createNumberedLabels();
        } catch (NameIsTakenException e) {
            fail();
        }

        try {
            fileSystem.openLabel("1");

            fileSystem.stopKeepingTrackOfRecents();
            fileSystem.openLabel("2");
            fileSystem.openLabel("3");
            fileSystem.openLabel("4");
            fileSystem.startKeepingTrackOfRecents();

            fileSystem.openLabel("5");
            fileSystem.openLabel("6");
            fileSystem.openLabel("7");
            fileSystem.openLabel("8");

            fileSystem.stopKeepingTrackOfRecents();
            fileSystem.openLabel("9");
            fileSystem.openLabel("10");
            fileSystem.startKeepingTrackOfRecents();

            fileSystem.openLabel("11");
        } catch (NoSuchLabelFoundException e) {
            fail();
        }
        
        try {
            fileSystem.openRecentlyOpenedLabel("1");
            fileSystem.openRecentlyOpenedLabel("6");
            fileSystem.openRecentlyOpenedLabel("8");
            fileSystem.openRecentlyOpenedLabel("11");
        } catch (NoSuchLabelFoundException e) {
            fail();
        }

        assertThrows(NoSuchLabelFoundException.class, () -> fileSystem.openRecentlyOpenedLabel("2"));
        assertThrows(NoSuchLabelFoundException.class, () -> fileSystem.openRecentlyOpenedLabel("3"));
        assertThrows(NoSuchLabelFoundException.class, () -> fileSystem.openRecentlyOpenedLabel("4"));
        assertThrows(NoSuchLabelFoundException.class, () -> fileSystem.openRecentlyOpenedLabel("9"));
        assertThrows(NoSuchLabelFoundException.class, () -> fileSystem.openRecentlyOpenedLabel("10"));
        assertThrows(NoSuchLabelFoundException.class, () -> fileSystem.openRecentlyOpenedLabel("15"));
    }


    /* 
     *  Tests for Persistence-related Methods:
     */
    @Test
    void testAutoSave() {
        try {
            emptyFileSystem.autoSave();
            JsonReader emptyFileSystemJsonReader = new JsonReader(FileSystem.AUTOSAVE_FILE_PATH);
            emptyFileSystem = emptyFileSystemJsonReader.read();
        } catch (IOException e) {
            fail();
        }
        testEmptyFileSystemConstruction();

        try {
            fileSystem.autoSave();
            JsonReader fileSystemJsonReader = new JsonReader(FileSystem.AUTOSAVE_FILE_PATH);
            fileSystem = fileSystemJsonReader.read();
        } catch (IOException e) {
            fail();
        }
        testFileSystemConstructionIgnoreRecent();
    }

    @Test
    void testManuallySave() {
        try {
            emptyFileSystem.manuallySave("data\\customSave.json");
            JsonReader emptyFileSystemJsonReader = new JsonReader("data\\customSave.json");
            emptyFileSystem = emptyFileSystemJsonReader.read();
        } catch (IOException e) {
            fail();
        }
        testEmptyFileSystemConstruction();


        try {
            fileSystem.manuallySave("data\\customSave2.json");
            JsonReader fileSystemJsonReader = new JsonReader("data\\customSave2.json");
            fileSystem = fileSystemJsonReader.read();
        } catch (IOException e) {
            fail();
        }
        testFileSystemConstructionIgnoreRecent();
    }

    @Test
    void testAutoLoad() {
        try {
            emptyFileSystem.autoSave();;
            emptyFileSystem = FileSystem.autoLoad();
            testEmptyFileSystemConstruction();
        } catch (IOException e) {
            fail();
        }

        try {
            fileSystem.autoSave();;
            fileSystem = FileSystem.autoLoad();
            testFileSystemConstructionIgnoreRecent();
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    void testManuallyLoad() {
        try {
            emptyFileSystem.manuallySave("data\\customSave.json");
            emptyFileSystem = FileSystem.manuallyLoad("data\\customSave.json");
            testEmptyFileSystemConstruction();
        } catch (IOException e) {
            fail();
        }

        try {
            fileSystem.manuallySave("data\\customSave2.json");
            fileSystem = FileSystem.manuallyLoad("data\\customSave2.json");
        } catch (IOException e) {
            fail();
        }
        testFileSystemConstructionIgnoreRecent();
    }

    @SuppressWarnings("methodlength")
    private void testFileSystemConstructionIgnoreRecent() {
        assertEquals("root", fileSystem.getCurrentFolderName());
        assertFalse(fileSystem.currentFolderHasParent());

        assertTrue(fileSystem.anyLabelsExist());
        assertEquals(2, fileSystem.getNumLabels());

        try {
            assertEquals(0, fileSystem.getNumLabelsOnFile("File"));
        } catch (NoSuchFileFoundException e) {
            fail();
        }

        List<String> recentlyOpenedFolders = fileSystem.getNamesOfRecentlyOpenedFolders();
        // assertTrue(fileSystem.getNamesOfRecentlyOpenedFiles().isEmpty());
        // assertEquals(2, recentlyOpenedFolders.size());
        // assertEquals("CPSC 210", recentlyOpenedFolders.get(0));
        // assertEquals("Education", recentlyOpenedFolders.get(1));
        // assertTrue(fileSystem.getNamesOfRecentlyOpenedLabels().isEmpty());


        openFolderFailIfFailed("Education");
        assertEquals("Education", fileSystem.getCurrentFolderName());
        assertTrue(fileSystem.currentFolderHasParent());
        try {
            assertEquals(1, fileSystem.getNumLabelsOnFile("test"));
            assertTrue(fileSystem.fileLabelled("test", "School"));
        } catch (NoSuchFileFoundException | NoSuchLabelFoundException e) {
            fail();
        }

        openFolderFailIfFailed("CPSC 210");
        assertEquals("CPSC 210", fileSystem.getCurrentFolderName());
        assertTrue(fileSystem.currentFolderHasParent());
        try {
            assertEquals(2, fileSystem.getNumLabelsOnFile("Personal Project Ideas"));
            assertTrue(fileSystem.fileLabelled("Personal Project Ideas", "School"));
            assertTrue(fileSystem.fileLabelled("Personal Project Ideas", "Personal Project"));
            assertEquals(1, fileSystem.getNumLabelsOnFile("A"));
            assertTrue(fileSystem.fileLabelled("A", "School"));
        } catch (NoSuchFileFoundException | NoSuchLabelFoundException e) {
            fail();
        }

        fileSystem.openRootFolder();
        openFolderFailIfFailed("Hobbies");
        assertEquals("Hobbies", fileSystem.getCurrentFolderName());
        assertTrue(fileSystem.currentFolderHasParent());

        recentlyOpenedFolders = fileSystem.getNamesOfRecentlyOpenedFolders();
        assertEquals(3, recentlyOpenedFolders.size());
        assertEquals("Hobbies", recentlyOpenedFolders.get(0));
        assertEquals("CPSC 210", recentlyOpenedFolders.get(1));
        assertEquals("Education", recentlyOpenedFolders.get(2));
    }


    /* 
     *  Test for Static Method:
     */
    @Test
    void testIsFilePathValid() {
        assertTrue(FileSystem.isFilePathValid(VALID_FILE_PATH));
        assertFalse(FileSystem.isFilePathValid("random not . $ @ { ]} path / ( aa asd)"));
        assertFalse(FileSystem.isFilePathValid("text that does not lead to a file"));
    }


    // Helper Methods:

    // MODIFIES: fileSystem
    // EFFECTS: opens Folder named folderName in fileSystem. Fails if none is found
    private void openFolderFailIfFailed(String folderName) {
        try {
            fileSystem.openFolder(folderName);
        } catch (NoSuchFolderFoundException e) {
            fail();
        }
    }

    // EFFECTS: returns name of File named fileName. Fails if none exist
    private String getFilePathFailIfFailed(String fileName) {
        try {
            return fileSystem.getFilePath(fileName);
        } catch (NoSuchFileFoundException e) {
            fail();
            // For compiler:
            return null;
        }
    }

    // EFFECTS: confirms that the labels have not changed since runBefore() was run
    private void confirmLabelsUnchanged() {
        List<String> labelNames = fileSystem.getNamesOfLabels();
        assertEquals(2, labelNames.size());
        assertFalse(labelNames.contains("Name"));
        assertTrue(labelNames.contains("School"));
        assertTrue(labelNames.contains("Personal Project"));
    }

    // MODIFIES: fileSystem
    // EFFECTS: creates Files named "1", "2", "3", ..., "10", "11" in fileSystem's current directory
    private void createNumberedFiles() throws NameIsTakenException {
        for (int i = 1; i <= 11; i++) {
            fileSystem.createFile(String.valueOf(i), VALID_FILE_PATH);
        }
    }

    // MODIFIES: fileSystem
    // EFFECTS: creates Folders named "1", "2", "3", ..., "10", "11" in fileSystem's current directory
    private void createNumberedFolders() throws NameIsTakenException {
        for (int i = 1; i <= 11; i++) {
            fileSystem.createFolder(String.valueOf(i));
        }
    }

    // MODIFIES: fileSystem
    // EFFECTS: creates Labels named "1", "2", "3", ..., "10", "11"
    private void createNumberedLabels() throws NameIsTakenException {
        for (int i = 1; i <= 11; i++) {
            fileSystem.createLabel(String.valueOf(i));
        }
    }
}