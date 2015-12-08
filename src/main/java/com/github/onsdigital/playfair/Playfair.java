package com.github.onsdigital.playfair;

import com.github.onsdigital.playfair.commands.Collection;
import com.github.onsdigital.playfair.commands.Connection;
import com.github.onsdigital.playfair.commands.http.Http;
import com.github.onsdigital.playfair.commands.http.Response;
import com.github.onsdigital.playfair.commands.http.Sessions;
import com.github.onsdigital.playfair.commands.json.Credentials;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

/**
 * Created by thomasridd on 07/12/2015.
 */
public class Playfair {

    Credentials credentials;
    Connection connection = new Connection();
    Collection current;
    Http session;

    public boolean processCommand(String command) {
        List<String> components = Utils.commandArguments(command);

        if (components.get(0).equalsIgnoreCase("connect")) {
            commandConnect(components);
        } else if (components.get(0).equalsIgnoreCase("login")) {
            commandLogin(components);
        } else if (components.get(0).equalsIgnoreCase("collection")) {
            commandCollection(components);
        }

        return true;
    }

    /**
     * Connect to an instance of zebedee
     *
     * (defaults to localhost)
     *
     * @param components
     */
    private void commandConnect(List<String> components) {
        if (components.size() > 1) {
            connection = new Connection(components.get(1));
        } else {
            connection = new Connection();
        }
    }
    private boolean commandLogin(List<String> components) {
        if(components.size() <= 1) {
            System.out.println("Command error for: login [username]");
        } else {
            credentials = new Credentials();
            credentials.email = components.get(1);

            if(System.console() == null) {
                System.out.print("password: ");
                Scanner scanner = new Scanner(System.in);
                credentials.password = scanner.nextLine();
            } else {
                System.out.print("password: ");
                char[] pwrd = System.console().readPassword();
                credentials.password = new String(pwrd);
            }

            session = Sessions.get(credentials.email);
            try {
                Response<String> response = session.post(connection.login, credentials, String.class);
                String token = response.body;
                session.addHeader("x-florence-token", token);
                if(token != null) {
                    System.out.println("Login successful");
                    return true;
                } else {
                    System.out.println("Login failed");
                }
            } catch (IOException e) {
                System.out.println("Error during login");
            }
        }
        return false;
    }

    /**
     *
     * @param components
     * @return
     */
    private boolean commandCollection(List<String> components) {
        if (components.size() == 1) { return false; }

        if (components.get(1).equalsIgnoreCase("create")) {
            if (components.size() != 3) {
                System.out.println("Command error for: collection create [collection name]");
                return false;
            } else {
                current = new Collection();
                return current.create(connection, session, components.get(2));
            }
        } else if (components.get(1).equalsIgnoreCase("build")) {
            if (components.size() == 4) {
                String collectionName = components.get(2);
                String sourcePath = components.get(3);

                if (Files.exists(Paths.get(sourcePath))) {
                    Collection collection = new Collection();
                    collection.build(connection, session, collectionName, Paths.get(sourcePath));
                }

            } else {
                System.out.println("Command error for: collection build [collection name] [source path]");
            }
        } else if (components.get(1).equalsIgnoreCase("add")) {
            if (components.size() >= 5) {
                String collectionName = components.get(2);
                String asUri = components.get(3);
                String sourcePath = components.get(4);
            } else {
                System.out.println("Command error for: collection add [collection name] [uri] [source path]");
            }
        }
        return false;
    }

}
