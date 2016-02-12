package com.github.onsdigital.zebedeescript.api;

import com.github.davidcarboni.restolino.framework.Api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import java.io.IOException;

/**
 * Created by thomasridd on 1/30/16.
 */
@Api
public class Query {

    @POST
    public void postQuery(HttpServletRequest request,
                               HttpServletResponse response) {

    }

    @GET
    public void getResults(HttpServletRequest request,
                          HttpServletResponse response) {

    }
}
