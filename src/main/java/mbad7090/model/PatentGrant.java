package mbad7090.model;

import org.apache.commons.lang.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * This is for reading an ipa Patent Trade Office Grant XML file.
 * Created by Rajah on 10/20/2015.
 */
public class PatentGrant extends Patent {
    private Set<String> patentCitations = new HashSet<>();
    private Integer lengthOfGrant = null;
    private boolean isInPublicationReference = false;
    private boolean isInApplicationReference = false;
    private boolean isInReferencesCited = false;

    private String pubDocNumber = "";
    private String appDocNumber = "";

    /**
     * Return the patent citations as a list of strings.
     * @return  String, with elements separated by commas.
     */
    public String getPatentCitations() {
        return StringUtils.join(patentCitations.iterator(), ", ");
    }

    public int getLengthOfGrant() {
        if (lengthOfGrant != null) {
            return lengthOfGrant;
        }
        return 0;
    }

    public String getPubDocNumber() { return pubDocNumber; }

    public String getAppDocNumber() { return appDocNumber; }

    /**
     * If appropriate, add the fields related to the Patent Grant to the object.
     *
     * @param currentElement XML tag, like orgname or doc-number
     * @param text           text within the tag, like IBM or 20100281593
     */
    @Override
    public void addField(final String currentElement, final String text) {
        String value = text.trim();
        log.debug("Got currentElement " + currentElement + " and text " + text);
        if (currentElement.equalsIgnoreCase("doc-number")) {
            if (StringUtils.isNotBlank(value)) {
                if (isInPublicationReference) {
                    pubDocNumber += value;
                } else if (isInApplicationReference) {
                    appDocNumber += value;
                } else if (isInReferencesCited) {
                    patentCitations.add(value);
                }
            }
        } else if (currentElement.equalsIgnoreCase("length-of-grant")) {
            if (StringUtils.isNotBlank(value)) {
                lengthOfGrant = Integer.valueOf(value);
            }
        } else {
            super.addField(currentElement, text);
        }
    }

    public void startElement(final String currentElement) {
        if (currentElement.equalsIgnoreCase("publication-reference")) {
            isInPublicationReference = true;
        } else if (currentElement.equalsIgnoreCase("application-reference")) {
            isInApplicationReference = true;
        } else if (currentElement.equalsIgnoreCase("references-cited")) {
            isInReferencesCited = true;
        }
    }

    /**
     * Record an ending tag.
     *
     * @param currentElement closing XML tag, like /orgname
     */
    public void endElement(final String currentElement) {
        log.debug("Closing currentElement " + currentElement + ".");
        if (currentElement.equalsIgnoreCase("publication-reference")) {
            isInPublicationReference = false;
        } else if (currentElement.equalsIgnoreCase("application-reference")) {
            isInApplicationReference = false;
        } else if (currentElement.equalsIgnoreCase("references-cited")) {
            isInReferencesCited = false;
        } else {
            super.endElement(currentElement);
        }
    }

    /**
     * Convert to a string of field names, separated by the default delimiter.
     *
     * @return csv row, separated by default delimiter.
     */
    public String toCsvRow() {
        StringBuffer sb = new StringBuffer();
        sb.append(getPubDocNumber()).append(delim);
        sb.append(getCompanyName()).append(delim);
        sb.append(getAppDocNumber()).append(delim);
 //       sb.append(get).append(delim);
        sb.append('"').append(getAssignee()).append('"').append(delim); // surround by quotes so the commas won't become columns
        sb.append(getLengthOfGrant()).append(delim);
        sb.append('"').append(getPatentCitations()).append('"').append(delim);
        return sb.toString();
    }


}
