package com.github.onsdigital.zebedeescript.commands;


import com.github.onsdigital.zebedeescript.commands.http.Endpoint;
import com.github.onsdigital.zebedeescript.commands.http.Host;

public class Connection {

    public Host zebedee;

    public Endpoint login;
    public Endpoint users;
    public Endpoint password;
    public Endpoint permission;
    public Endpoint approve;
    public Endpoint collections;
    public Endpoint collection;
    public Endpoint content;
    public Endpoint transfer;
    public Endpoint browse;
    public Endpoint complete;
    public Endpoint review;
    public Endpoint teams;
    public Endpoint publish;
    public Endpoint collectionDetails;


    public Connection() {
        this("http://localhost:8082");
    }
    public Connection(String connectionURL) {
        zebedee = new Host(connectionURL);
                
        login = new Endpoint(zebedee, "login");
        users = new Endpoint(zebedee, "users");
        password = new Endpoint(zebedee, "password");
        permission = new Endpoint(zebedee, "permission");
        approve = new Endpoint(zebedee, "approve");
        collections = new Endpoint(zebedee, "collections");
        collection = new Endpoint(zebedee, "collection");
        content = new Endpoint(zebedee, "content");
        transfer = new Endpoint(zebedee, "transfer");
        browse = new Endpoint(zebedee, "browse");
        complete = new Endpoint(zebedee, "complete");
        review = new Endpoint(zebedee, "review");
        teams = new Endpoint(zebedee, "teams");
        publish = new Endpoint(zebedee, "publish");
        collectionDetails = new Endpoint(zebedee, "collectionDetails");
    }
}
