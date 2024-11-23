package persistence;

import model.FileSystem;
import model.exceptions.NameIsTakenException;
import model.exceptions.NoSuchFileFoundException;
import model.exceptions.NoSuchFolderFoundException;
import model.exceptions.NoSuchLabelFoundException;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

// Based on [JsonSerializationDemo](https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo) 
public class TestJsonWriter {
    @Test
    void testJsonWriterInvalidPath() {
        JsonWriter jsonWriter = new JsonWriter("data\\invalid !#$!??//::\"#$!(*# file name).json");
        try {
            jsonWriter.open();
            fail("open did not throw FileNotFoundException when opening an invalid file");
        } catch (IOException e) {
            // expected
        }

        jsonWriter = new JsonWriter("data\\this file name should also be invalid \0");
        try {
            jsonWriter.open();
            fail("open did not throw FileNotFoundException when opening an invalid file");
        } catch (IOException e) {
            // expected
        }
    }

    @Test
    void testJsonWriterEmptyFileSystem() {
        try {
            FileSystem fileSystem = new FileSystem();
            JsonWriter jsonWriter = new JsonWriter("data\\test\\testWriteEmptyFileSystem.json");

            jsonWriter.open();
            jsonWriter.write(fileSystem);
            jsonWriter.close();

            JsonReader jsonReader = new JsonReader("data\\test\\testWriteEmptyFileSystem.json");

            FileSystem loadedFileSystem = jsonReader.read();
            assertEquals("root", loadedFileSystem.getCurrentFolderName());
            assertTrue(loadedFileSystem.getNamesOfSubfiles().isEmpty());
            assertTrue(loadedFileSystem.getNamesOfSubfolders().isEmpty());
            assertTrue(loadedFileSystem.getNamesOfLabels().isEmpty());
            assertTrue(loadedFileSystem.getNamesOfRecentlyOpenedFiles().isEmpty());
            assertTrue(loadedFileSystem.getNamesOfRecentlyOpenedFolders().isEmpty());
            assertTrue(loadedFileSystem.getNamesOfRecentlyOpenedLabels().isEmpty());
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    void testJsonWriterRegularFileSystem() {
        try {
            FileSystem fileSystem = new FileSystem();
            createRegularFileSystem(fileSystem);

            JsonWriter jsonWriter = new JsonWriter("data\\test\\testWriteRegularFileSystem.json");

            jsonWriter.open();
            jsonWriter.write(fileSystem);
            jsonWriter.close();

            JsonReader jsonReader = new JsonReader("data\\test\\testWriteRegularFileSystem.json");

            FileSystem loadedFileSystem = jsonReader.read();
            testIsRegularFileSystem(loadedFileSystem);
        } catch (IOException e) {
            fail();
        }
    }

    // EFFECTS: creates a files, folders and labels in file system, some of which are nested
    private void createRegularFileSystem(FileSystem fileSystem) {
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
            fail();
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

        List<String> recentlyOpenedFolders = fileSystem.getNamesOfRecentlyOpenedFolders();
        assertTrue(fileSystem.getNamesOfRecentlyOpenedFiles().isEmpty());
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
