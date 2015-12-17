package com.github.onsdigital.playfair.commands;

import com.github.onsdigital.playfair.Flatsy;
import com.github.onsdigital.playfair.commands.http.Endpoint;
import com.github.onsdigital.playfair.commands.http.Http;
import com.github.onsdigital.playfair.commands.http.Response;
import com.github.onsdigital.playfair.commands.json.CollectionDescription;
import com.github.onsdigital.playfair.commands.json.CollectionType;
import com.github.thomasridd.flatsy.FlatsyDatabase;

import com.github.thomasridd.flatsy.FlatsyFlatFileDatabase;
import com.github.thomasridd.flatsy.query.FlatsyCursor;

import org.eclipse.jetty.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
                System.out.println("! error uploading file with code " + upload.statusLine.getReasonPhrase());
            }
        } catch (IOException e) {
            System.out.println("! error uploading file " + file.toString());
        }
    }

    public CollectionDescription build(Connection connection, Http session, String collectionName, Path path) {
        create(connection, session, collectionName);

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
                System.out.println("!  error loading collection description after upload");
            }
        }

        try {
            this.description = get(connection, session, this.description.id).body;
        } catch (IOException e) {
            e.printStackTrace();
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


}
