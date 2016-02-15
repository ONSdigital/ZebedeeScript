package com.github.onsdigital.zebedeescript;

import com.github.thomasridd.flatsy.FlatsyCommandLine;

import java.io.PrintStream;

/**
 * Created by thomasridd on 07/12/2015.
 */
public class Flatsy {
    private FlatsyCommandLine flatsyCli;

    public Flatsy() {
        flatsyCli = new FlatsyCommandLine();
    }

    public boolean processCommand(String command) {
        flatsyCli.runCommand(command);

        return true;
    }
    public void setOutputStream(PrintStream outputStream) {
        this.flatsyCli.defaultOut = outputStream;

    }
}
