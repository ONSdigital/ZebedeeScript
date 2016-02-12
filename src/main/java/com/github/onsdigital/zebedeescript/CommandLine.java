package com.github.onsdigital.zebedeescript;

import com.github.davidcarboni.restolino.json.Serialiser;
import com.github.onsdigital.zebedeescript.commands.json.IsoDateSerializer;
import com.github.thomasridd.flatsy.util.FlatsyUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * Created by thomasridd on 07/12/2015.
 */
public class CommandLine {
    String mode = "z-script";

    Flatsy flatsy = new Flatsy();
    ZebedeeScript zebedeeScript = new ZebedeeScript();
    PrintStream defaultOut = System.out;
    public static void main(String[] args) {
        Serialiser.getBuilder().registerTypeAdapter(Date.class, new IsoDateSerializer());

        CommandLine commandLine = new CommandLine();
        Scanner scanner = new Scanner(System.in);
        boolean doLoop = true;

        sayWelcome();
        while(doLoop) {
            System.out.print((commandLine.mode + ":     ").substring(0,10));
            String command = scanner.nextLine();

            if (command != null)
                doLoop = commandLine.processCommand(command.trim());

        }
    }
    private static void sayWelcome() {
        System.out.println();
        System.out.println();
        System.out.println("---------------------------------------------------------------------------");
        System.out.println("                        Z e b e d e e S c r i p t                          ");
        System.out.println("                 command line tools for the ons beta website               ");
        System.out.println("---------------------------------------------------------------------------");
        System.out.println();
    }


    public void setOutputStream(PrintStream outputStream) {
        this.defaultOut = outputStream;
        flatsy.setOutputStream(outputStream);
    }

    public boolean processCommand(String command) {
        if (command.equalsIgnoreCase("exit")) {
            return false;
        } else if (command.trim().toLowerCase().startsWith("script")) {
            runScript(command);
        } else if (command.equalsIgnoreCase("") || command.startsWith("#") || command.startsWith("//")) {
            // try to process with zebedeescript ( a zebedee command )

        } else if (zebedeeScript.processCommand(command) == false) {
            // otherwise use flatsy ( a database command )
            flatsy.processCommand(command);
        }
        return true;
    }

    /**
     * Process a script command
     *
     * @param command of the form "script [filename]"
     * @return success
     */
    boolean runScript(String command, boolean quiet) {
        List<String> components = Utils.commandArguments(command);
        return runScript(FlatsyUtil.pathsGet(components.get(1)), quiet);
    }

    /**
     * Run a script by walking all commands
     *
     * @param path the path of the script
     * @return
     */
    public boolean runScript(Path path, boolean quiet) {
        if (!Files.exists(path) || Files.isDirectory(path)) { return false; }

        try (Scanner scanner = new Scanner(path)) {
            while (scanner.hasNextLine()) {
                String command = scanner.nextLine();

                if(!quiet) defaultOut.println(">  " + command);

                processCommand(command);
            }
        } catch (IOException e) {
            return false;
        }

        return true;
    }
    public boolean runScript(String command) {
        return runScript(command, false);
    }
}
