package com.github.onsdigital.zebedeescript;

import com.github.thomasridd.flatsy.FlatsyDatabase;
import com.github.thomasridd.flatsy.FlatsyFlatFileDatabase;
import com.github.thomasridd.flatsy.FlatsyObject;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by thomasridd on 07/01/2016.
 */
public class Builder {
    public static Path copyFlatFiles() throws IOException {
        Path tempDir = Files.createTempDirectory("flatfile_db_");
        return copyFlatFiles(tempDir);
    }
    public static Path copyFlatFiles(Path toPath) throws IOException {
        URL flatfiletest = Builder.class.getResource("/test_content");
        FileUtils.copyDirectory(Paths.get(flatfiletest.getPath()).toFile(), toPath.toFile());
        return toPath;
    }
    public static Path refreshFlatFiles(Path atPath) throws IOException {
        FileUtils.deleteDirectory(atPath.toFile());
        return copyFlatFiles(atPath);
    }

    public static Path cursorTestDatabase() throws IOException {
        Path tempDir = Files.createTempDirectory("flatfile_db_");

        FlatsyDatabase db = new FlatsyFlatFileDatabase(tempDir);
        db.create(new FlatsyObject("alpha/one.json", db), "Alpha1");
        db.create(new FlatsyObject("alpha/two.json", db), "Alpha2");
        db.create(new FlatsyObject("beta/three.json", db), "Beta1");
        db.create(new FlatsyObject("beta/four.json", db), "Beta2");

        return tempDir;
    }

    public static Path emptyTestDatabase() throws IOException {
        Path tempDir = Files.createTempDirectory("flatfile_db_");

        FlatsyDatabase db = new FlatsyFlatFileDatabase(tempDir);

        return tempDir;
    }

    /**
     * Pull a script from the test directory and substitute in the name of our temporary database
     *
     * @param script the name of the script in the scripts directory
     * @param root the root path to the script (call be null)
     * @return a path to the temporary script
     * @throws IOException
     */
    public static Path testScript(String script, Path root) throws IOException {
        URL scriptURL = Builder.class.getResource("/scripts/" + script);
        Path tempScript = Files.createTempFile("script", ".flatsy");

        FileUtils.copyFile(Paths.get(scriptURL.getPath()).toFile(), tempScript.toFile());

        if(root != null)
            replacePlaceholders(tempScript, "<ROOT>", root.toString());

        return tempScript;
    }

    /**
     * Replace text in the script to swap out placeholders
     *
     * @param script a path to a script
     * @param replaceText the placeholder
     * @param withText the correct value
     * @throws IOException
     */
    public static void replacePlaceholders(Path script, String replaceText, String withText) throws IOException {
        Charset charset = StandardCharsets.UTF_8;

        String content = new String(Files.readAllBytes(script), charset);
        content = content.replaceAll(replaceText, withText);
        Files.write(script, content.getBytes(charset));
    }
}
