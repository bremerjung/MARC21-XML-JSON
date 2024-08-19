package de.oh.aufgabe.suub;

public class Identifier {

    private String type;
    private String value;

    public Identifier(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

}
