package de.oh.aufgabe.suub;

import java.util.List;

public class Container {

    private String title;
    private List<Identifier> identifiers;

    public Container(String title, List<Identifier> identifiers) {
        this.title = title;
        this.identifiers = identifiers;
    }

    public String getTitle() {
        return title;
    }

    public List<Identifier> getIdentifiers() {
        return identifiers;
    }

}
