package com.github.onsdigital.zebedeescript;

import com.github.onsdigital.zebedeescript.commands.Collection;
import com.github.onsdigital.zebedeescript.commands.Connection;
import com.github.onsdigital.zebedeescript.commands.http.Http;
import com.github.onsdigital.zebedeescript.commands.http.Response;
import com.github.onsdigital.zebedeescript.commands.http.Sessions;
import com.github.onsdigital.zebedeescript.commands.json.Credentials;
import com.github.onsdigital.zebedeescript.commands.json.PermissionDefinition;
import com.github.onsdigital.zebedeescript.commands.json.User;
import com.github.thomasridd.flatsy.FlatsyDatabase;
import com.github.thomasridd.flatsy.FlatsyFlatFileDatabase;
import com.github.thomasridd.flatsy.FlatsyObjectType;
import com.github.thomasridd.flatsy.operations.operators.Delete;
import com.github.thomasridd.flatsy.operations.operators.Rename;
import com.github.thomasridd.flatsy.operations.operators.Replace;
import org.apache.http.HttpStatus;
import org.apache.http.NoHttpResponseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

/**
 * Created by thomasridd on 07/12/2015.
 */
public class ZebedeeScript {

    Credentials credentials;
    Connection connection = new Connection();
    Collection current;
    FlatsyDatabase db;

    Http session;

    public boolean processCommand(String command) {
        List<String> components = Utils.commandArguments(command);

        if (components.get(0).equalsIgnoreCase("connect")) {
            commandConnect(components);
        } else if (components.get(0).equalsIgnoreCase("login")) {
            commandLogin(components);
        } else if (components.get(0).equalsIgnoreCase("collection")) {
            commandCollection(components);
        } else if (components.get(0).equalsIgnoreCase("from")) {
            commandFrom(components);
            return false; // we want flatsy to fix this one too so return false
        } else if (components.get(0).equalsIgnoreCase("move")) {
            commandMove(components);
        } else if (components.get(0).equalsIgnoreCase("delete")) {
            commandDelete(components);
        } else if (components.get(0).equalsIgnoreCase("users")) {
            commandUsers(components);
        } else {
            return false;
        }

        return true;
    }
    private void commandFrom(List<String> components) {
        db = new FlatsyFlatFileDatabase(Paths.get(components.get(1)));
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
                    this.current = new Collection();
                    this.current.build(connection, session, collectionName, Paths.get(sourcePath));
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
        } else if (components.get(1).equalsIgnoreCase("complete")) {
            if (this.current == null) {
                System.out.println("Please sign into a collection with create, build, or checkout before completing items");
            } else {
                this.current.complete(connection, session);
            }
        } else if  (components.get(1).equalsIgnoreCase("review")) {
            if (this.current == null) {
                System.out.println("Please sign into a collection with create, build, or checkout before reviewing items");
            } else {
                this.current.review(connection, session);
            }
        }
        return false;
    }

    private boolean commandMove(List<String> components) {
        if(db != null) {
            String oldUri = components.get(1);
            String newUri = components.get(2);

            // if newUri exists delete
            if (db.get(newUri).getType() != FlatsyObjectType.Null) {
                db.delete(db.get(newUri));
            }

            db.root().apply(new Rename(strip(oldUri), strip(newUri)));

            // big and ugly query (but that is what we are dealing with)
            db.root().query("files").query("uri_ends data.json").apply(new Replace("\"" + oldUri + "\"", "\"" + newUri + "\""));
            db.root().query("files").query("uri_ends data.json").apply(new Replace(oldUri + "/", newUri + "/"));
        }
        return false;
    }

    private boolean commandDelete(List<String> components) {
        if(db != null) {
            String uri = components.get(1);

            // if newUri exists delete
            if (db.get(uri).getType() != FlatsyObjectType.Null) {
                Delete delete = new Delete();
                db.get(uri).cursor().apply(delete);
            }
        }
        return false;
    }


    private boolean commandUsers(List<String> components) {
        if (components.size() == 1) { return false; }

        if (components.get(1).equalsIgnoreCase("add") && components.size() >= 4) {
            User user = new User();
            user.email = components.get(2);
            user.name = components.get(3);
            try {
                Response<User> response = session.post(connection.users, user, User.class);
                if (response.statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    return true;
                } else {
                    System.out.println("user add failed with error:" + response.statusLine.getReasonPhrase());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else if(components.get(1).equalsIgnoreCase("password") && components.size() >= 4) {
            Credentials newCredentials = new Credentials();
            newCredentials.email = components.get(2);
            newCredentials.password = components.get(3);

            try {
                Response<String> response = session.post(connection.password, newCredentials, String.class);
                return response.statusLine.getStatusCode() == HttpStatus.SC_OK;
            } catch (NoHttpResponseException e) {

            } catch (IOException e) {
                e.printStackTrace();
            }


        } else if (components.get(1).equalsIgnoreCase("permissions") && components.size() >= 4) {

            PermissionDefinition definition;
            if (components.get(2).equalsIgnoreCase("publisher")) {
                definition = permission(components.get(3), false, true);
            } else if (components.get(2).equalsIgnoreCase("admin")) {
                definition = permission(components.get(3), true, true);
            } else if (components.get(2).equalsIgnoreCase("viewer")) {
                definition = permission(components.get(3), false, false);
            } else {
                System.out.println("Command error for set permissions: users permissions [publisher/admin/viewer] <email>");
                return false;
            }

            try {
                Response<String> response = session.post(connection.permission, definition , String.class);
                if (response.statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    return true;
                } else {
                    System.out.println("Permissions set failed with error:" + response.statusLine.getReasonPhrase());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return false;
    }

    public PermissionDefinition permission(String email, Boolean admin, Boolean editor) {
        PermissionDefinition permissionDefinition = new PermissionDefinition();
        permissionDefinition.email = email;
        permissionDefinition.admin = admin;
        permissionDefinition.editor = editor;
        return permissionDefinition;
    }

    private String strip(String uri){
        if (uri.startsWith("/")) {
            return uri.substring(1);
        }
        return uri;
    }
}
