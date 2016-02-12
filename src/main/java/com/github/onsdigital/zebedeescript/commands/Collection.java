package com.github.onsdigital.zebedeescript.commands;

import com.github.onsdigital.zebedeescript.commands.http.Endpoint;
import com.github.onsdigital.zebedeescript.commands.http.Http;
import com.github.onsdigital.zebedeescript.commands.http.Response;
import com.github.onsdigital.zebedeescript.commands.json.CollectionDescription;
import com.github.onsdigital.zebedeescript.commands.json.CollectionType;
import com.github.thomasridd.flatsy.FlatsyDatabase;

import com.github.thomasridd.flatsy.FlatsyFlatFileDatabase;
import com.github.thomasridd.flatsy.query.FlatsyCursor;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.http.HttpStatus;

import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

/**
 * Created by thomasridd on 07/12/2015.
 */
public class Collection {
    CollectionDescription description;

    public boolean create(Connection connection, Http session, String collectionName) {
        CollectionDescription description = createCollectionDescription(collectionName);
        try {
            Response<CollectionDescription> response = session.post(connection.collection, description, CollectionDescription.class);
            if (response.statusLine.getStatusCode() == HttpStatus.OK_200) {

                System.out.println("collection " + collectionName + " created");
                this.description = response.body;
                return true;
            } else {
                System.out.println("Error creating collection with error " + response.statusLine.getReasonPhrase());
            }
        } catch (IOException e) {
            System.out.println("Error creating collection");
        }
        return false;
    }

    public boolean download(Connection connection, Http session, String root) throws IOException {
        for (String uri: description.inProgressUris)
            download(connection, session, uri, Paths.get(root).resolve("inprogress"));
        for (String uri: description.completeUris)
            download(connection, session, uri, Paths.get(root).resolve("complete"));
        for (String uri: description.reviewedUris)
            download(connection, session, uri, Paths.get(root).resolve("reviewed"));
        return true;
    }

    /**
     * Download collection content at a uri
     *
     * @param connection
     * @param session
     * @param uri
     * @param root
     * @return
     * @throws IOException
     */
    private boolean download(Connection connection, Http session, String uri, Path root) throws IOException {
        Response<Path> pathResponse = downloadItem(connection, session, this.description.id, uri);
        if (pathResponse.statusLine.getStatusCode() != HttpStatus.OK_200)
            return false;

        String saveUri = uri;
        if (uri.startsWith("/"))
            saveUri = uri.substring(1, uri.length());

        try (InputStream stream=Files.newInputStream(pathResponse.body); OutputStream output=Files.newOutputStream(root.resolve(saveUri))){
            IOUtils.copy(stream, output);
        }

        return true;
    }

    public static CollectionDescription createCollectionDescription(String collectionName) {
        CollectionDescription collection = new CollectionDescription();
        try {
            collection.name = URLEncoder.encode(collectionName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            collection.name = "collection_name_error";
        }
        collection.publishDate = new Date();
        collection.type = CollectionType.manual;
        return collection;
    }

    public void add(Connection connection, Http session, String uri, Path file) {

        try {
            Response<String> upload = upload(connection, session, this.description.id, uri, file.toFile());
            if (upload.statusLine.getStatusCode() == HttpStatus.OK_200) {
                System.out.println("uploaded " + uri);
            } else {
                System.out.println("Error uploading file with code " + upload.statusLine.getReasonPhrase());
            }
        } catch (IOException e) {
            System.out.println("Error uploading file " + file.toString());
        }
    }

    public CollectionDescription build(Connection connection, Http session, String collectionName, Path path) {
        if(!create(connection, session, collectionName)) {
            System.out.println("Build connection failed");
            return null;
        };

        if (this.description != null) {
            FlatsyDatabase db = new FlatsyFlatFileDatabase(path);
            FlatsyCursor cursor = db.root().query("files");
            while (cursor.next()) {
                String uri = cursor.currentObject().uri;
                add(connection, session, uri, path.resolve(Paths.get(uri)));
            }

            try {
                this.description = get(connection, session, this.description.id).body;
            } catch (IOException e) {
                System.out.println("Error loading collection description after upload");
            }
        }

        try {
            this.description = get(connection, session, this.description.id).body;
        } catch (IOException e) {
            System.out.println("Error retrieving collection after build");
        }

        return this.description;
    }

    public void complete(Connection connection, Http session) {
        if (this.description != null) {

            // Update the list of in progress uris
            try {
                this.description = get(connection, session, this.description.id).body;
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (String uri : this.description.inProgressUris) {
                try {
                    Response<String> stringResponse = completeItem(connection, session, this.description.id, uri);
                } catch (IOException e) {
                    System.out.println("!  error reviewing connection");
                }
            }
        }
    }

    public void review(Connection connection, Http session) {
        if (this.description != null) {

            // Update the list of currently complete uris
            try {
                this.description = get(connection, session, this.description.id).body;
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (String uri : this.description.completeUris) {
                try {
                    Response<String> stringResponse = reviewItem(connection, session, this.description.id, uri);
                } catch (IOException e) {
                    System.out.println("!  error reviewing connection");
                }
            }
        }
    }

    private static Response<String> upload(Connection connection, Http session, String collectionId, String uri, File file) throws IOException {
        Endpoint contentEndpoint = connection.content.addPathSegment(collectionId).setParameter("uri", uri);
        return session.post(contentEndpoint, file, String.class);
    }

    private static Response<CollectionDescription> get(Connection connection, Http session, String id) throws IOException {
        Endpoint idUrl = connection.collection.addPathSegment(id);
        return session.get(idUrl, CollectionDescription.class);
    }

    private static Response<String> reviewItem(Connection connection, Http session, String collectionID, String uri) throws IOException {
        Endpoint contentEndpoint = connection.review.addPathSegment(collectionID).setParameter("uri", uri);
        return session.post(contentEndpoint, "", String.class);
    }

    public static Response<String> completeItem(Connection connection, Http session, String collectionID, String uri) throws IOException {
        Endpoint contentEndpoint = connection.complete.addPathSegment(collectionID).setParameter("uri", uri);
        return session.post(contentEndpoint, "", String.class);
    }

    private static Response<Path> downloadItem(Connection connection, Http session, String collectionId, String uri) throws IOException {
        Endpoint contentEndpoint = connection.content.addPathSegment(collectionId).setParameter("uri", uri);
        return session.get(contentEndpoint);
    }
}
