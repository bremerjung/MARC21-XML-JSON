package de.oh.aufgabe.suub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class MyXMLtoJSONConverter {

    private static final String INPUT_FILE_NAME = "daten.xml";
    private static final String OUTPUT_FILE_NAME = "result.json";

    public static void main(String[] args) {
        try {
            Document xmlDocument = readXML();

            if (xmlDocument == null) {
                return;
            }

            NodeList recordsNodeList = xmlDocument.getElementsByTagName("record");

            List<Record> recordsList = new ArrayList<>();
            for (int i = 0; i < recordsNodeList.getLength(); i++) {
                Node recordNode = recordsNodeList.item(i);
                Record record = new Record();
                if (recordNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element recordElement = (Element) recordNode;

                    // leader (type)
                    convertLeaderToType(recordElement, record);

                    // controlfields (id, format)
                    convertControlfieldsToIdAndFormat(recordElement, record);

                    // datafields (isbn, language, author(s), title(s), date, keywords, container, url)
                    NodeList datafieldsNodeList = recordElement.getElementsByTagName("datafield");
                    for (int temp = 0; temp < datafieldsNodeList.getLength(); temp++) {
                        Element datafieldElement = (Element) datafieldsNodeList.item(temp);

                        if (datafieldElement.getAttribute("tag").equals("020")) {
                            addIsbnToRecord(datafieldElement, record);
                        }

                        if (datafieldElement.getAttribute("tag").equals("041")) {
                            setRecordLanguage(datafieldElement, record);
                        }

                        if (datafieldElement.getAttribute("tag").equals("100")) {
                            addContributorAuthor(datafieldElement, record);
                        }

                        if (datafieldElement.getAttribute("tag").equals("110")) {
                            addContributorInstitution(datafieldElement, record);
                        }

                        if (datafieldElement.getAttribute("tag").equals("245")) {
                            setRecordTitleAndSubtitle(datafieldElement, record);
                        }

                        if (datafieldElement.getAttribute("tag").equals("264")) {
                            setRecordPublicationDate(datafieldElement, record);
                        }

                        if (datafieldElement.getAttribute("tag").equals("650")) {
                            addKeywordToRecord(datafieldElement, record);
                        }

                        if (datafieldElement.getAttribute("tag").equals("700")) {
                            addContributorAuthor(datafieldElement, record);
                        }

                        if (datafieldElement.getAttribute("tag").equals("710")) {
                            addContributorInstitution(datafieldElement, record);
                        }

                        if (datafieldElement.getAttribute("tag").equals("773")) {
                            addContainerToRecord(datafieldElement, record);
                        }

                        if (datafieldElement.getAttribute("tag").equals("856")) {
                            addUrlToRecord(datafieldElement, record);
                        }
                    }

                }

                // post-processing keywords
                List<Keyword> keywords = sortKeywords(record);
                List<Keyword> cleanedKeywords = cleanKeywords(keywords);
                record.setKeywords(cleanedKeywords);

                recordsList.add(record);
            }

            // JSON generieren
            ObjectMapper mapper = new ObjectMapper();
            ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
            writer.writeValue(new File(OUTPUT_FILE_NAME), recordsList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Document readXML() throws IOException, ParserConfigurationException, SAXException {
        File inputFile = new File(INPUT_FILE_NAME);
        if (!inputFile.exists()) {
            System.err.println("Die Datei '" + INPUT_FILE_NAME + "' wurde nicht gefunden.");
            return null;
        }
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(inputFile);
        return doc;
    }

    private static void convertLeaderToType(Element recordElement, Record record) throws DOMException {
        NodeList leaderNodeList = recordElement.getElementsByTagName("leader");
        if (leaderNodeList.getLength() > 0) {
            Element leaderElement = (Element) leaderNodeList.item(0);
            setRecordType(leaderElement, record);
        }
    }

    private static void setRecordType(Element leaderElement, Record record) throws DOMException {
        String leaderElementTextContent = leaderElement.getTextContent();
        if (leaderElementTextContent != null && leaderElementTextContent.length() >= 8) {
            String typeSubstring = leaderElementTextContent.substring(5, 8);
            if ("cam".equals(typeSubstring)) {
                record.setType("book");
            } else if ("caa".equals(typeSubstring)) {
                record.setType("article");
            }
        }
    }

    private static void convertControlfieldsToIdAndFormat(Element recordElement, Record record) throws DOMException {
        NodeList controlfieldsNodeList = recordElement.getElementsByTagName("controlfield");
        for (int i = 0; i < controlfieldsNodeList.getLength(); i++) {
            Element controlfieldElement = (Element) controlfieldsNodeList.item(i);
            if (controlfieldElement.getAttribute("tag").equals("001")) {
                record.setId(controlfieldElement.getTextContent());
            } else if (controlfieldElement.getAttribute("tag").equals("007")) {
                setRecordFormat(controlfieldElement, record);
            }
        }
    }

    private static void setRecordFormat(Element controlfieldElement, Record record) throws DOMException {
        String controlfieldElementTextContent = controlfieldElement.getTextContent();
        if ("tu".equals(controlfieldElementTextContent)) {
            record.setFormat("print");
        } else if ("cr".equals(controlfieldElementTextContent)) {
            record.setFormat("electronic");
        }
    }

    private static void addIsbnToRecord(Element datafieldElement, Record record) throws DOMException {
        NodeList subfields = datafieldElement.getElementsByTagName("subfield");
        for (int i = 0; i < subfields.getLength(); i++) {
            Element subfield = (Element) subfields.item(i);
            if (subfield.getAttribute("code").equals("a")) {
                Identifier identifier = new Identifier("isbn", subfield.getTextContent());
                record.addIdentifier(identifier);
            }
        }
    }

    private static void setRecordLanguage(Element datafieldElement, Record record) throws DOMException {
        NodeList subfields = datafieldElement.getElementsByTagName("subfield");
        for (int i = 0; i < subfields.getLength(); i++) {
            Element subfield = (Element) subfields.item(i);
            if (subfield.getAttribute("code").equals("a")) {
                record.setLanguage(subfield.getTextContent());
            }
        }
    }

    private static void addContributorAuthor(Element datafieldElement, Record record) throws DOMException {
        String givenName = null;
        String familyName = null;
        String role = null;
        NodeList subfields = datafieldElement.getElementsByTagName("subfield");
        for (int j = 0; j < subfields.getLength(); j++) {
            Element subfield = (Element) subfields.item(j);
            if (subfield.getAttribute("code").equals("a")) {
                String textContent = subfield.getTextContent();
                String[] split = textContent.split(", ");
                givenName = split[1].trim();
                familyName = split[0].trim();
            }
            if (subfield.getAttribute("code").equals("4")) {
                role = convertRole(subfield);
            }
        }
        Contributor contributor = new Contributor(givenName, familyName, role);
        record.addContributor(contributor);
    }

    private static void setRecordTitleAndSubtitle(Element datafieldElement, Record record) throws DOMException {
        String title = datafieldElement.getElementsByTagName("subfield").item(0).getTextContent();
        String subtitle = "";
        NodeList subfields = datafieldElement.getElementsByTagName("subfield");
        for (int j = 0; j < subfields.getLength(); j++) {
            Element subfield = (Element) subfields.item(j);
            if (subfield.getAttribute("code").equals("b")) {
                subtitle = subfield.getTextContent();

            }
        }
        record.setTitle(title + subtitle);
    }

    private static void setRecordPublicationDate(Element datafieldElement, Record record) throws DOMException, NumberFormatException {
        Integer year = null;
        Integer month = null;
        Integer day = null;

        NodeList subfields = datafieldElement.getElementsByTagName("subfield");
        for (int j = 0; j < subfields.getLength(); j++) {
            Element subfield = (Element) subfields.item(j);
            if (subfield.getAttribute("code").equals("c")) {
                year = Integer.parseInt(subfield.getTextContent());
            } else if (subfield.getAttribute("code").equals("d")) {
                month = mapMonth(subfield);
            } else if (subfield.getAttribute("code").equals("e")) {
                day = Integer.parseInt(subfield.getTextContent());
            }
        }
        Integer[] datum = {year, month, day};

        // filter null values
        Integer[] filteredPubDateArray = Arrays.stream(datum).filter(d -> d != null).toArray(Integer[]::new);

        record.setPublicationDate(filteredPubDateArray);
    }

    private static Integer mapMonth(Element subfield) throws DOMException {
        Integer month;

        switch (subfield.getTextContent()) {
            case "Januar":
                month = 1;
                break;
            case "Februar":
                month = 2;
                break;
            case "März":
                month = 3;
                break;
            case "April":
                month = 4;
                break;
            case "Mai":
                month = 5;
                break;
            case "Juni":
                month = 6;
                break;
            case "Juli":
                month = 7;
                break;
            case "August":
                month = 8;
                break;
            case "September":
                month = 9;
                break;
            case "Oktober":
                month = 10;
                break;
            case "November":
                month = 11;
                break;
            case "Dezember":
                month = 12;
                break;
            default:
                month = null;
        }
        return month;
    }

    private static void addKeywordToRecord(Element datafieldElement, Record record) throws DOMException {
        NodeList subfields = datafieldElement.getElementsByTagName("subfield");
        for (int i = 0; i < subfields.getLength(); i++) {
            Element subfield = (Element) subfields.item(i);
            if (subfield.getAttribute("code").equals("a") || subfield.getAttribute("code").equals("x")) {
                Keyword keyword = new Keyword(subfield.getTextContent());
                record.addKeyword(keyword);
            }
        }
    }

    private static List<Keyword> cleanKeywords(List<Keyword> keywords) {
        List<Keyword> cleanedKeywords = keywords.stream()
                .map(keyword -> {
                    String value = keyword.getValue().trim();
                    String replaceAll = value.replaceAll("[.,;]$", "");
                    keyword.setValue(replaceAll);
                    return keyword;
                }).distinct().toList();
        return cleanedKeywords;
    }

    private static List<Keyword> sortKeywords(Record record) {
        List<Keyword> keywords = new ArrayList<>(record.getKeywords());
        keywords.sort(Comparator.comparing(Keyword::getValue));
        return keywords;
    }

    private static void addContributorInstitution(Element datafieldElement, Record record) throws DOMException {
        String name = null;
        String role = null;
        NodeList subfields = datafieldElement.getElementsByTagName("subfield");
        for (int i = 0; i < subfields.getLength(); i++) {
            Element subfield = (Element) subfields.item(i);
            if (subfield.getAttribute("code").equals("a")) {
                name = subfield.getTextContent();
            }
            if (subfield.getAttribute("code").equals("4")) {
                role = convertRole(subfield);
            }
        }
        Contributor contributor = new Contributor(name, role, true);
        record.addContributor(contributor);
    }

    private static String convertRole(Element subfield) throws DOMException {
        String role = null;

        String textContent = subfield.getTextContent();
        if (textContent.equals("aut")) {
            role = "author";
        }
        if (textContent.equals("edt")) {
            role = "editor";
        }
        if (textContent.equals("oth")) {
            role = "other";
        }
        return role;
    }

    private static void addContainerToRecord(Element datafieldElement, Record record) throws DOMException {
        String title = null;
        List<Identifier> identifiers = new ArrayList<>();

        NodeList subfields = datafieldElement.getElementsByTagName("subfield");
        for (int i = 0; i < subfields.getLength(); i++) {
            Element subfield = (Element) subfields.item(i);
            if (subfield.getAttribute("code").equals("t")) {
                title = subfield.getTextContent();
            }
            if (subfield.getAttribute("code").equals("x")) {
                String type = "issn";
                String value = subfield.getTextContent();
                Identifier identifier = new Identifier(type, value);
                identifiers.add(identifier);
            }
        }
        Container container = new Container(title, identifiers);
        record.addContainer(container);
    }

    private static void addUrlToRecord(Element datafieldElement, Record record) throws DOMException {
        NodeList subfields = datafieldElement.getElementsByTagName("subfield");
        for (int i = 0; i < subfields.getLength(); i++) {
            Element subfield = (Element) subfields.item(i);
            if (subfield.getAttribute("code").equals("u")) {
                AccessOption accessOption = new AccessOption(subfield.getTextContent());
                record.addAccessOption(accessOption);
            }
        }
    }

}
