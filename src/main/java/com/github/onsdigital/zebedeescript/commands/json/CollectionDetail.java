package com.github.onsdigital.zebedeescript.commands.json;

import java.util.List;

public class CollectionDetail extends CollectionBase {
    public List<ContentDetail> inProgress;
    public List<ContentDetail> complete;
    public List<ContentDetail> reviewed;
    public boolean approvedStatus;

    public Events events;
}
