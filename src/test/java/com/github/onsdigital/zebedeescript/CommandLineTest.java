package com.github.onsdigital.zebedeescript;

import com.github.thomasridd.flatsy.FlatsyCommandLine;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * Created by thomasridd on 07/01/2016.
 */
public class CommandLineTest {
    Path root = null;
    Path simple = null;
    Path script = null;

    @Before
    public void setUp() throws Exception {
        // For all tests we copy the flatFileTest example dataset

        root = Builder.copyFlatFiles();
        simple = Builder.cursorTestDatabase();

        // creating script as a before variable
        // and so we can easily rip down
        script = Files.createTempFile(".script", "zebedee");
    }

    @After
    public void tearDown() throws Exception {
        // Garbage collection (try to avoid choking the file system)
        FileUtils.deleteDirectory(simple.toFile());
        FileUtils.deleteDirectory(root.toFile());
        Files.delete(script);
    }

    private String getScriptOutput(Path script) throws IOException {
        String content = "";
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PrintStream ps = new PrintStream(baos);
            CommandLine cli = new CommandLine();
            cli.setOutputStream(ps);

            // When
            // we read the results of the script to the in memory stream
            cli.runScript(script, true);

            content = baos.toString("UTF8");
        }
        return content;
    }

    @Test
    public void list_withEmptyList_returnsBlank() throws IOException {
        // Given
        // a script that implements a complex operation
        try(PrintWriter writer = new PrintWriter(script.toFile(), "UTF-8")) {
            writer.println("from " + root);
            writer.println("filter uri_ends bosstanksidewalkjamnittygritty");
            writer.println("list");
        }

        // When
        // we run the script
        String result = getScriptOutput(script);

        // Then
        // we expect output of only commands
        assertEquals("", result);
    }

    @Test
    public void list_whenFilesExpected_doesNotReturnBlank() throws IOException {
        // Given
        // a script that implements a complex operation
        try(PrintWriter writer = new PrintWriter(script.toFile(), "UTF-8")) {
            writer.println("from " + root);
            writer.println("filter uri_ends data.json");
            writer.println("list");
        }

        // When
        // we run the script
        String result = getScriptOutput(script);

        // Then
        // we expect output of only commands
        assertNotEquals("", result);
    }

    @Test
    public void filterJsonValid_withJsonValidFile_returnsFile() throws IOException {
        // Given
        // a script that implements a complex operation
        try(PrintWriter writer = new PrintWriter(script.toFile(), "UTF-8")) {
            writer.println("from " + root);
            writer.println("filter uri_ends births/data.json");
            writer.println("filter json valid");
            writer.println("list");
        }

        // When
        // we run the script
        String result = getScriptOutput(script);

        // Then
        // we expect output of only commands
        assertNotEquals("", result);
    }

    @Test
    public void filterNotJsonValid_withInvalidFile_returnsFile() throws IOException {
        // Given
        // a script that implements a complex operation
        try(PrintWriter writer = new PrintWriter(script.toFile(), "UTF-8")) {
            writer.println("from " + root);
            writer.println("filter uri_ends births/adoption/bulletins/englandandwales/283368d3.html");
            writer.println("filter not json valid");
            writer.println("list");
        }

        // When
        // we run the script
        String result = getScriptOutput(script);

        // Then
        // we expect output of only commands
        assertNotEquals("", result);
    }
}