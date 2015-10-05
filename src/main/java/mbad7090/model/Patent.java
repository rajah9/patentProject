package mbad7090.model;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static javax.xml.stream.XMLStreamConstants.CHARACTERS;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

/**
 * Patent handles the desired patent fields.
 * It has a separate way to handle the abstract tag.
 * Created by Rajah on 9/22/2015.
 */
public class Patent {
    private static final Logger log = LoggerFactory.getLogger(Patent.class);
    public static final String[] REMOVELIST = {"\n", "\t"};
    public static final String[] REPLACELIST = {" ", " "};
    private char delim = '\t';
    private String companyName = "";
    private String assignee = "";
    private Integer yearGranted = null;
    private Integer yearApplied = null;
    private String patentClass = "";
    private String patentNumber = "";
    private String patentTitle = "";
    private String patentAbstract = "";
    private boolean isInDocumentId = false;
    private boolean isInApplicationReference = false;

    public String getCompanyName() {
        return companyName;
    }

    public int getYearGranted() {
        if (yearGranted != null) {
            return yearGranted;
        } else {
            return 0;
        }
    }

    public int getYearApplied() {
        if (yearApplied != null) {
            return yearApplied;
        } else {
            return 0;
        }
    }

    public String getPatentClass() {
        return patentClass;
    }

    public String getAssignee() {
        if (assignee.length() > 0) {
            return StringUtils.trim(assignee);
        } else {
            return companyName;
        }
    }

    public String getPatentNumber() {
        return patentNumber;
    }

    public String getPatentTitle() {
        return patentTitle;
    }

    public String getPatentAbstract() { return patentAbstract; }

    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public void setYearGranted(Integer yearGranted) { this.yearGranted = yearGranted; }

    /**
     * If appropriate, add the field to the Patent.
     *
     * @param currentElement XML tag, like orgname or doc-number
     * @param text           text within the tag, like IBM or 20100281593
     */
    public void addField(final String currentElement, final String text) {
        String value = text.trim();
        log.debug("Got currentElement " + currentElement + " and text " + text);
        if (currentElement.equalsIgnoreCase("orgname")) {
            companyName += value;
            log.debug("Made companyName: " + value);
        } else if (currentElement.equalsIgnoreCase("date")) {
            // Figure out whether this is year granted or year applied. Must look at surrounding tags.
            Integer year;
            try {
                year = yyyyMMddToYear(value);
            } catch (NumberFormatException e) {
                log.error("Unable to find the year in the first 4 digits of <" + value + ">.");
                year = null;
            }
            if (isInApplicationReference) {
                yearApplied = year;
                isInApplicationReference = false;
            }
            if (isInDocumentId) {
                yearGranted = year;
                isInDocumentId = false;
            }
            log.debug("made year granted: " + yearGranted);
        } else if (currentElement.equalsIgnoreCase("main-classification")) {
            patentClass += value + " ";
            log.debug("Made patent class" + patentClass);
        } else if (currentElement.equalsIgnoreCase("doc-number")) {
            patentNumber += value;
            log.debug("Made doc number " + patentNumber);
        } else if (currentElement.equalsIgnoreCase("last-name")) {
            assignee += value + " ";
            log.debug("Made assignee " + assignee);
        } else if (currentElement.equalsIgnoreCase("first-name")) {
            assignee += value + " ";
            log.debug("Made assignee " + assignee);
        } else if (currentElement.equalsIgnoreCase("invention-title")) {
            patentTitle += value;
            log.debug("Made patent title " + patentTitle);
//   Handling Abstract (and its embedded tags) in its own method.
        } else if (currentElement.equalsIgnoreCase("abstract")) {
            patentAbstract += value;
            log.debug("Made patent abstract " + patentAbstract);
        } else if (currentElement.equalsIgnoreCase("document-id")) {
            isInDocumentId = true;
            log.debug("Inside document id.");
        } else if (currentElement.equalsIgnoreCase("application-reference")) {
            isInApplicationReference = true;
            log.debug("Inside application reference.");
        }
    }

    /**
     * Record an ending tag.
     *
     * @param currentElement closing XML tag, like /orgname
     */
    public void endField(final String currentElement) {
        log.debug("Closing currentElement " + currentElement + ".");
        if (currentElement.equalsIgnoreCase("document-id")) {
            isInDocumentId = false;
            log.debug("Exiting document id.");
        } else if (currentElement.equalsIgnoreCase("application-reference")) {
            isInApplicationReference = false;
            log.debug("Exiting application reference.");
        }
    }

    /**
     * Clean the fields of embedded tabs and linefeeds. If willDisregardAbstract
     * is true, make the patentAbstract into a small handful of stem words.
     *
     * @param willDisregardAbstract if true, call disregardAbstract.
     */
    public void cleanFields(boolean willDisregardAbstract) {
        companyName = cleanAndTrim(companyName);
        assignee = cleanAndTrim(assignee);
        patentClass = cleanAndTrim(patentClass);
        patentTitle = cleanAndTrim(patentTitle);
        if (willDisregardAbstract) {
            disregardAbstract();
        } else {
            patentAbstract = cleanAndTrim(patentAbstract);
        }
    }

    private String cleanAndTrim(String cleanMe) {
        return StringUtils.replaceEachRepeatedly(cleanMe, REMOVELIST, REPLACELIST).trim();
    }

    /**
     * Make the abstract just a handful of stem words that will not be regarded in the word cloud.
     */
    public void disregardAbstract() {
        patentAbstract = "the of and it";
    }

    /**
     * Convert to a string of field names, separated by the default delimiter.
     *
     * @return csv row, separated by default delimiter.
     */
    public String toCsvRow() {
        StringBuffer sb = new StringBuffer();
        sb.append(getCompanyName()).append(delim);
        sb.append(getAssignee()).append(delim);
        sb.append(getYearGranted()).append(delim);
        sb.append(getYearApplied()).append(delim);
        sb.append(getPatentClass()).append(delim);
        sb.append(getPatentTitle()).append(delim);
        sb.append(getPatentAbstract()).append(delim);
        return sb.toString();
    }

    public String toCsvRow(char delim) {
        this.delim = delim;
        return toCsvRow();
    }

    private int yyyyMMddToYear(String value) throws NumberFormatException {
        if (value.length() < 4) {
            throw new NumberFormatException("Cannot get yyyy from string of length " + value.length());
        }
        return Integer.parseInt(value.substring(0, 4));
    }

    /**
     * Need special processing for the abstract tag, which will have one or more
     * tags (such as p and b) embedded.
     *
     * @param reader Continue reading from this stream until the ending abstract tag.
     */
    public void addAbstract(XMLStreamReader reader) throws XMLStreamException {
        getAttribs(reader);
        String currentElement;

        while (reader.hasNext()) {
            int code = reader.next();
            switch (code) {
                case START_ELEMENT:
                    currentElement = reader.getLocalName();
                    if (currentElement.equalsIgnoreCase("p")) { getAttribs(reader); }
//                    if (currentElement.equalsIgnoreCase("b")) {  }
                    break;
                case CHARACTERS:
                    patentAbstract += reader.getText();
                    log.debug("Abstract is now: " + getPatentAbstract());
                    break;
                case END_ELEMENT:
                    currentElement = reader.getLocalName();
                    if (currentElement.equalsIgnoreCase("abstract")) {
                        return;
                    }
            }
        }
    }

    private void getAttribs(XMLStreamReader reader) {
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String attrib = reader.getAttributeValue(i);
            log.debug("Got an attribute: " + attrib);
        }
    }
}
