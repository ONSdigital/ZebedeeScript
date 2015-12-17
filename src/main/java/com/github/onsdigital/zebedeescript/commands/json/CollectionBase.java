package com.github.onsdigital.zebedeescript.commands.json;

import java.net.URI;
import java.util.Date;

public class CollectionBase {
    /**
     * The unique identifier of this {@link com.github.onsdigital.zebedee.model.Collection}.
     */
    public String id;
    /**
     * The readable name of this {@link com.github.onsdigital.zebedee.model.Collection}.
     */
    public String name;
    /**
     * The type of the collection to determine the publish behaviour.
     */
    public CollectionType type;
    /**
     * The date-time when this {@link com.github.onsdigital.zebedee.model.Collection} should be published (if it has
     * a publish date).
     */
    public Date publishDate;
    /**
     * If the collection is associated with a release the releaseUri determines that release.
     */
    public URI releaseUri;
}
