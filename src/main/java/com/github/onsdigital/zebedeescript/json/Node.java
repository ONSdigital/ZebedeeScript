package com.github.onsdigital.zebedeescript.json;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomasridd on 1/31/16.
 */
public class Node {
    public String uri;
    public String filename;
    public List<Node> children = new ArrayList<>();

    public Node(String uri) {
        this.uri = uri;
    }
}
