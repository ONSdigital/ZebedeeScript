package com.github.onsdigital.zebedeescript.api;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.davidcarboni.restolino.json.Serialiser;
import com.github.onsdigital.zebedeescript.json.Command;
import com.github.onsdigital.zebedeescript.json.ZebedeeResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by thomasridd on 1/30/16.
 */
@Api
public class Action {

    @POST
    public void postAction(HttpServletRequest request,
                            HttpServletResponse response) throws IOException {
        Command command = Serialiser.deserialise(request, Command.class);

        ZebedeeResponse zebedeeResponse = new ZebedeeResponse();
        zebedeeResponse.command = command;
        zebedeeResponse.console.add("Result");

        Serialiser.serialise(response, zebedeeResponse);
    }
}
