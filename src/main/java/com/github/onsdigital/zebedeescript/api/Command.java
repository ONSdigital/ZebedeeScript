package com.github.onsdigital.zebedeescript.api;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.davidcarboni.restolino.json.Serialiser;
import com.github.onsdigital.zebedeescript.CommandLine;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by thomasridd on 2/8/16.
 */
@Api
public class Command {

    CommandLine cli = null;

    @POST
    public void postCommand(HttpServletRequest request,
                           HttpServletResponse response) throws IOException {

        // Parse a command list from the request body
        String[] commands = Serialiser.deserialise(request, String[].class);

        // Lazy initialise a command line
        if (cli == null)
            cli = new CommandLine();

        // Run the command list and build an array of output
        List<String> result = new ArrayList<>();
        for(String command: Arrays.asList(commands))
            result.add(runCommand(command));

        // Respond
        Serialiser.serialise(response, result);
    }

    /**
     * Run a command
     *
     * Hijack console output to print to an in memory stream
     *
     * @param command and ZebedeeScript command
     *
     * @return the console output
     * @throws IOException
     */
    private String runCommand(String command) throws IOException {

        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PrintStream ps = new PrintStream(baos);
            cli.setOutputStream(ps);

            // When
            // we read the results of the script to the in memory stream
            cli.processCommand(command);

            return baos.toString("UTF8");
        }
    }
}
