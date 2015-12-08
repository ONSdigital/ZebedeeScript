package com.github.onsdigital.playfair;

import com.github.davidcarboni.restolino.json.Serialiser;
import com.github.onsdigital.playfair.commands.json.IsoDateSerializer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * Created by thomasridd on 07/12/2015.
 */
public class Cli {
    String mode = "playfair";

    static Flatsy flatsy = new Flatsy();
    static Playfair playfair = new Playfair();


    public static void main(String[] args) {
        Serialiser.getBuilder().registerTypeAdapter(Date.class, new IsoDateSerializer());

        Cli commandLine = new Cli();
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
        System.out.println("                   P L A Y F A I R   &   F L A T S Y                       ");
        System.out.println("                 command line tools for the ons beta website               ");
        System.out.println("---------------------------------------------------------------------------");
        System.out.println();
    }

    private boolean processCommand(String command) {
        if (command.equalsIgnoreCase("exit")) {
            return false;
        } else if (command.equalsIgnoreCase("flatsy")) {
            mode = "flatsy";
        } else if (command.equalsIgnoreCase("playfair")) {
            mode = "playfair";
        } else if (command.toLowerCase().startsWith("script")) {
            runScript(command);
        } else if (command.equalsIgnoreCase("") || command.startsWith("#") || command.startsWith("//")) {

        } else if (mode.equalsIgnoreCase("flatsy")) {
            flatsy.processCommand(command);
        } else if (mode.equalsIgnoreCase("playfair")) {
            playfair.processCommand(command);
        }
        return true;
    }

    /**
     * Process a script command
     *
     * @param command of the form "script [filename]"
     * @return success
     */
    private boolean runScript(String command) {
        List<String> components = Utils.commandArguments(command);
        return runScript(Paths.get(components.get(1)));
    }

    /**
     * Run a script by walking all commands
     *
     * @param path
     * @return
     */
    private boolean runScript(Path path) {
        if (!Files.exists(path) || Files.isDirectory(path)) { return false; }

        try( Scanner scanner = new Scanner(path)) {
            while (scanner.hasNextLine()) {
                String command = scanner.nextLine();
                System.out.println(">  " + command);
                processCommand(command);
            }
        } catch (IOException e) {
            return false;
        }

        return true;
    }
}
