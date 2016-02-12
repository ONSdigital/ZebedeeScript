package com.github.onsdigital.zebedeescript.api;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.davidcarboni.restolino.json.Serialiser;
import com.github.onsdigital.zebedeescript.json.Node;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import java.io.IOException;

/**
 * Created by thomasridd on 1/30/16.
 */
@Api
public class Content {

    @POST
    public void postContent(HttpServletRequest request,
                          HttpServletResponse response) throws IOException {
        Node node = Serialiser.deserialise(request, Node.class);

        System.out.println(node.children.size());
    }

    @GET
    public void getContent(HttpServletRequest request,
                           HttpServletResponse response) throws IOException {
        Node node = new Node("my_uri");
        node.filename = "a file";
        node.children.add(new Node("a_child"));
        node.children.add(new Node("another_child"));

        Serialiser.serialise(response, node);
    }
}
