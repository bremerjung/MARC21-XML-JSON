package de.oh.aufgabe.suub;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"given_name", "name", "family_name", "role", "corporation"})
public class Contributor {

    @JsonProperty("given_name")
    private String givenName;
    @JsonProperty("family_name")
    private String familyName;
    private String role;
    private String name;
    private Boolean corporation;

    public Contributor(String givenName, String familyName, String role) {
        this.givenName = givenName;
        this.familyName = familyName;
        this.role = role;
    }

    public Contributor(String name, String role, Boolean corporation) {
        this.name = name;
        this.role = role;
        this.corporation = corporation;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    public Boolean getCorporation() {
        return corporation;
    }

}
