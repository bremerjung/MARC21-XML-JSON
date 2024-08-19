package de.oh.aufgabe.suub;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"id", "type", "format", "language", "title",
    "publication_date", "contributors", "identifiers", "keywords", "containers",
    "access_options"})
public class Record {

    private String id;
    private String type;
    private String format;
    private String language;
    private String title;
    @JsonProperty("publication_date")
    private Integer[] publicationDate;
    private List<Contributor> contributors;
    private List<Identifier> identifiers;
    private List<Keyword> keywords;
    private List<Container> containers;
    @JsonProperty("access_options")
    private List<AccessOption> accessOptions;

    public Record() {
        this.contributors = new ArrayList<>();
        this.identifiers = new ArrayList<>();
        this.keywords = new ArrayList<>();
        this.containers = new ArrayList<>();
        this.accessOptions = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer[] getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Integer[] publicationDate) {
        this.publicationDate = publicationDate;
    }

    public List<Contributor> getContributors() {
        return contributors;
    }

    public void setContributors(List<Contributor> contributors) {
        this.contributors = contributors;
    }

    public void addContributor(Contributor contributor) {
        this.contributors.add(contributor);
    }

    public List<Identifier> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<Identifier> identifiers) {
        this.identifiers = identifiers;
    }

    public void addIdentifier(Identifier identifier) {
        this.identifiers.add(identifier);
    }

    public List<Keyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<Keyword> keywords) {
        this.keywords = keywords;
    }

    public void addKeyword(Keyword keyword) {
        this.keywords.add(keyword);
    }

    public List<Container> getContainers() {
        return containers;
    }

    public void setContainers(List<Container> containers) {
        this.containers = containers;
    }

    public void addContainer(Container container) {
        this.containers.add(container);
    }

    public List<AccessOption> getAccessOptions() {
        return accessOptions;
    }

    public void setAccessOptions(List<AccessOption> accessOptions) {
        this.accessOptions = accessOptions;
    }

    public void addAccessOption(AccessOption accessOption) {
        this.accessOptions.add(accessOption);
    }

}
