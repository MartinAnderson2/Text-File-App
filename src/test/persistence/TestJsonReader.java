package persistence;

import model.FileSystem;
import model.exceptions.NoSuchFileFoundException;
import model.exceptions.NoSuchFolderFoundException;
import model.exceptions.NoSuchLabelFoundException;
import persistence.exceptions.InvalidJsonException;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.List;

// Based on [JsonSerializationDemo](https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo) 
public class TestJsonReader {
    @Test
    void testReaderFileDoesNotExist() {
        JsonReader jsonReader = new JsonReader("data\\test\\Please do not create a file with this path.json");
        try {
            jsonReader.read();
            fail("IOException not thrown when reading a file that shouldn't exist");
        } catch (IOException e) {
            // expected
        } catch (InvalidJsonException e) {
            fail("IOException not thrown when reading a file that shouldn't exist");
        }

        // This violates the requires clause but it is useful to know about the exception
        jsonReader = new JsonReader("data\\test\\invalid ?:$@! file path \0.json");
        try {
            jsonReader.read();
            fail("InvalidPathException not thrown when file path was invalid");
        } catch (IOException e) {
            fail("IOException thrown when file path was invalid");
        } catch (InvalidPathException e) {
            // expected
        } catch (InvalidJsonException e) {
            fail("IOException not thrown when reading a file that shouldn't exist");
        }
    }

    @Test
    void testReaderEmptyFileSystem() {
        JsonReader jsonReader = new JsonReader("data\\test\\testReaderEmptyFileSystem.json");

        try {
            FileSystem loadedFileSystem = jsonReader.read();
            assertEquals("root", loadedFileSystem.getCurrentFolderName());
            assertTrue(loadedFileSystem.getNamesOfSubfiles().isEmpty());
            assertTrue(loadedFileSystem.getNamesOfSubfolders().isEmpty());
            assertTrue(loadedFileSystem.getNamesOfLabels().isEmpty());
            assertTrue(loadedFileSystem.getNamesOfRecentlyOpenedFiles().isEmpty());
            assertTrue(loadedFileSystem.getNamesOfRecentlyOpenedFolders().isEmpty());
            assertTrue(loadedFileSystem.getNamesOfRecentlyOpenedLabels().isEmpty());
        } catch (IOException | InvalidJsonException e) {
            fail("Read of premade JSON file failed");
        }
    }

    @Test
    void testReaderRegularFileSystem() {
        JsonReader jsonReader = new JsonReader("data\\test\\testReaderRegularFileSystem.json");

        try {
            FileSystem loadedFileSystem = jsonReader.read();
            testIsRegularFileSystem(loadedFileSystem);
        } catch (IOException | InvalidJsonException e) {
            fail("Read of premade JSON file failed");
        }
    }

    // EFFECTS: confirms that fileSystem is the default file system used for tests (i.e. it has the appropriate premade
    // files, folders, and labels)
    private void testIsRegularFileSystem(FileSystem fileSystem) {
        assertEquals("root", fileSystem.getCurrentFolderName());
        assertFalse(fileSystem.currentFolderHasParent());

        assertTrue(fileSystem.anyLabelsExist());
        assertEquals(2, fileSystem.getNumLabels());

        try {
            assertEquals(0, fileSystem.getNumLabelsOnFile("File"));
        } catch (NoSuchFileFoundException e) {
            fail();
        }

        assertTrue(fileSystem.getNamesOfRecentlyOpenedFiles().isEmpty());
        List<String> recentlyOpenedFolders = fileSystem.getNamesOfRecentlyOpenedFolders();
        assertEquals(2, recentlyOpenedFolders.size());
        assertEquals("CPSC 210", recentlyOpenedFolders.get(0));
        assertEquals("Education", recentlyOpenedFolders.get(1));
        assertTrue(fileSystem.getNamesOfRecentlyOpenedLabels().isEmpty());


        try {
            fileSystem.openFolder("Education");
            assertEquals("Education", fileSystem.getCurrentFolderName());
            assertTrue(fileSystem.currentFolderHasParent());
            assertEquals(1, fileSystem.getNumLabelsOnFile("test"));
            assertTrue(fileSystem.fileLabelled("test", "School"));
        } catch (NoSuchFileFoundException | NoSuchLabelFoundException | NoSuchFolderFoundException e) {
            fail();
        }

        try {
            fileSystem.openFolder("CPSC 210");
            assertEquals("CPSC 210", fileSystem.getCurrentFolderName());
            assertTrue(fileSystem.currentFolderHasParent());
            assertEquals(2, fileSystem.getNumLabelsOnFile("Personal Project Ideas"));
            assertTrue(fileSystem.fileLabelled("Personal Project Ideas", "School"));
            assertTrue(fileSystem.fileLabelled("Personal Project Ideas", "Personal Project"));
            assertEquals(1, fileSystem.getNumLabelsOnFile("A"));
            assertTrue(fileSystem.fileLabelled("A", "School"));
        } catch (NoSuchFileFoundException | NoSuchLabelFoundException | NoSuchFolderFoundException e) {
            fail();
        }

        fileSystem.openRootFolder();
        try {
            fileSystem.openFolder("Hobbies");
        } catch (NoSuchFolderFoundException e) {
            fail();
        }
        assertEquals("Hobbies", fileSystem.getCurrentFolderName());
        assertTrue(fileSystem.currentFolderHasParent());

        recentlyOpenedFolders = fileSystem.getNamesOfRecentlyOpenedFolders();
        assertEquals(3, recentlyOpenedFolders.size());
        assertEquals("Hobbies", recentlyOpenedFolders.get(0));
        assertEquals("CPSC 210", recentlyOpenedFolders.get(1));
        assertEquals("Education", recentlyOpenedFolders.get(2));
    }
}
