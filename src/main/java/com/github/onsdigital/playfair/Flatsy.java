package com.github.onsdigital.playfair;

import com.github.thomasridd.flatsy.FlatsyCommandLine;

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
}
