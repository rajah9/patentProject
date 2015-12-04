package mbad7090.model;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Integer.parseInt;

/**
 * Patent handles the desired patent fields.
 * It has a separate way to handle the abstract tag.
 * Created by Rajah on 9/22/2015.
 */
public abstract class Patent {
    protected static final Logger log = LoggerFactory.getLogger(Patent.class);
    public static final String[] REMOVELIST = {"\n", "\t"};
    public static final String[] REPLACELIST = {" ", " "};
    private static final boolean USEYEARS = false;
    public static final int _CLASSLEN = 3;
    public static final String _CLASS_SEPARATOR = "|";
    public static final String _ASSIGNEE_SEPARATOR = "|";
    protected char delim = '\t';
    private String companyName = "";
    private String assignee = "";
    private Integer yearGranted = null;
    private Integer yearApplied = null;
    private String dateGranted = null;
    private String dateApplied = null;
    private Integer whenPublished = null;
    private Set<String> patentClass = new HashSet<>();
    private List<String> mainClassification = new ArrayList<>();
    private String patentNumber = "";
    private String patentTitle = "";
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


    public String getDateGranted() {
        if (dateGranted != null) {
            return dateGranted;
        } else {
            return "";
        }

    }

    public String getDateApplied() {
        if (dateApplied != null) {
            return dateApplied;
        } else {
            return "";
        }
    }

    public Integer getWhenPublished() {
        if (null != whenPublished) { return whenPublished; }
        return 0;
    }

    /**
     * Return the patent class as a list of strings.
     * @return  String, with elements separated by commas.
     */
    public String getPatentClass() {
        return StringUtils.join(patentClass.iterator(), ", ");
    }


    public String getAssignee() {
        if (assignee.length() > 0) {
            return StringUtils.trim(assignee);
        } else {
            return companyName;
        }
    }

    public String[] getAssigneesAsArray() {
        return StringUtils.split(assignee, _ASSIGNEE_SEPARATOR);
    }

    /**
     * Add the assignee to the given list.
     * @param assignee  Should be of the form Last@First.
     *
     */
    public void addAssignee(final String myAssignee) {
        assignee += myAssignee + _ASSIGNEE_SEPARATOR;
    }

    public String getPatentNumber() {
        return patentNumber;
    }

    public String getPatentTitle() {
        return patentTitle;
    }

    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public void setYearGranted(Integer yearGranted) { this.yearGranted = yearGranted; }

    public String getMainClassification() {
        return StringUtils.join(mainClassification.iterator(), _CLASS_SEPARATOR);
    }

    public boolean containsMainClassification(final String findMe) {
        return StringUtils.contains(getMainClassification(), findMe);
    }

    void addMainClassification(String addThisClass) {
        mainClassification.add(addThisClass);
    }
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
//            Integer year;
            if (StringUtils.isNotBlank(value)) {
                String dt = StringUtils.substring(value, 0,4) + "-" + StringUtils.substring(value, 4,6) + "-" + StringUtils.substring(value, 6,8);

                if (isInApplicationReference) {
                    dateApplied = dt;
                    yearApplied = Integer.valueOf(value.substring(0,4));
                    isInApplicationReference = false;
                }
                if (isInDocumentId) {
                    dateGranted = dt;
                    yearGranted = Integer.valueOf(value.substring(0,4));
                    isInDocumentId = false;
                }
                log.debug("made year granted: " + yearGranted);
            }

        } else if (currentElement.equalsIgnoreCase("class")) {
            if (StringUtils.isNotBlank(value)) {
                patentClass.add(value);
            }
            log.debug("Patent class has " + patentClass.size() + " elements.");
        } else if (currentElement.equalsIgnoreCase("main-classification")) {
            if (StringUtils.isNotBlank(value)) {
                mainClassification.addAll(parseMainClassification(value));
            }
            log.debug("Main classification is " + mainClassification);
        } else if (currentElement.equalsIgnoreCase("doc-number")) {
            patentNumber += value;
            if (StringUtils.startsWith(patentNumber,"20")) {
                // patent number starts with a publish date, like 2012029
                // TODO: Make sure the following works correctly.
                whenPublished = parseInt(patentNumber.substring(0, 4));
            }
            log.debug("Made doc number " + patentNumber);
        } else if (currentElement.equalsIgnoreCase("last-name")) {
            if (StringUtils.isNotBlank(value)) {
                assignee += value + "@";
                log.debug("Made assignee " + assignee);
            }
        } else if (currentElement.equalsIgnoreCase("first-name")) {
            if (StringUtils.isNotBlank(value)) {
                assignee += value + _ASSIGNEE_SEPARATOR;
                log.debug("Made assignee " + assignee);
            }
        } else if (currentElement.equalsIgnoreCase("invention-title")) {
            patentTitle += value;
            log.debug("Made patent title " + patentTitle);
        } else if (currentElement.equalsIgnoreCase("document-id")) {
            isInDocumentId = true;
            log.debug("Inside document id.");
        } else if (currentElement.equalsIgnoreCase("application-reference")) {
            isInApplicationReference = true;
            log.debug("Inside application reference.");
        }
    }

    /**
     * Takes a main classification like " 123 20" and returns a List of triplets, padded with 0s, like "123 020"
     *
     * @param value     Main classification string, like " 123 20 "
     * @return          List of strings, 3 chars long.
     */
    static public List<String> parseMainClassification(String value) {
        List<String> ans = new ArrayList<>();
        String[] classifications = StringUtils.split(value);

        for (String classification : classifications) {
            ans.addAll(parseClassification(classification));
        }
        return ans;
    }
    /**
     * Takes a main classification like "20" and returns a List of triplets, padded with 0s, like "020"
     *
     * @param value
     * @return
     */

    static public List<String> parseClassification(String value) {
        List<String> ans = new ArrayList<>();
        StringBuffer parseMe = new StringBuffer(15);
        int currentLen = StringUtils.trim(value).length();
        // Make a sb of 0s that make the desired buffer length become a multiple of 3.
        // If currentLen is 7, make it 9. If it's 1, make it 3.
        while (currentLen % _CLASSLEN > 0) {
            parseMe.append('0');
            currentLen++;
        }

        parseMe.append(StringUtils.trim(value));  // turns a 20 into a 020.

        for (int i = 0; i < parseMe.length() - _CLASSLEN + 1; i += _CLASSLEN) {
            ans.add(parseMe.substring(i, i + _CLASSLEN).toString());
        }

        return ans;
    }

    /**
     * Record an ending tag.
     *
     * @param currentElement closing XML tag, like /orgname
     */
    public void endElement(final String currentElement) {
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
     * Clean the fields of embedded tabs and linefeeds.
     *
     */
    public void cleanFields() {
        companyName = cleanAndTrim(companyName);
        assignee = cleanAndTrim(assignee);
        patentTitle = cleanAndTrim(patentTitle);
    }

    protected String cleanAndTrim(String cleanMe) {
        return StringUtils.replaceEachRepeatedly(cleanMe, REMOVELIST, REPLACELIST).trim();
    }

    /**
     * Convert to a string of field names, separated by the default delimiter.
     *
     * @return csv row, separated by default delimiter.
     */
    public String toCsvRow() {
        StringBuffer sb = new StringBuffer();
        sb.append(getCompanyName()).append(delim);
        sb.append('"').append(getAssignee()).append('"').append(delim); // surround by quotes so the commas won't become columns
        if (USEYEARS) {
            sb.append(getYearGranted()).append(delim);
            sb.append(getYearApplied()).append(delim);
        }
        else {
            sb.append(getDateGranted()).append(delim);
            sb.append(getDateApplied()).append(delim);
        }
        sb.append(getWhenPublished()).append(delim);
        sb.append(getPatentClass()).append(delim);
        sb.append(getMainClassification()).append(delim);
        sb.append(getPatentTitle()).append(delim);
        return sb.toString();
    }

    public String toCsvDate() {
        StringBuffer sb = new StringBuffer();
        sb.append(getCompanyName()).append(delim);
        sb.append(getDateGranted()).append(delim);
        sb.append(getPatentClass()).append(delim);
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
        return parseInt(value.substring(0, 4));
    }

    protected void getAttribs(XMLStreamReader reader) {
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String attrib = reader.getAttributeValue(i);
            log.debug("Got an attribute: " + attrib);
        }
    }

    /**
     * Return a code based on whether it was our company or theirs and the year
     * from XML file or Patent Year granted.
     *
     * @param fileName  filename, like ipa150108.xml
     * @return          code (like "OURS2006" or "THEIRS2012")
     */
    public String ownershipYearCode(String fileName) {
        String oursOrTheirs;
        int year;
        if (CompanyFilter.is3M(getCompanyName())) {
            oursOrTheirs = "OURS";
        } else {
            oursOrTheirs = "THEIRS";
        }
        if (StringUtils.isEmpty(fileName)) {
            // no file name, so use grant year.
            year = getYearGranted(); // could be 0 if never filled in
            oursOrTheirs += " gy ";
        } else {
            // file name is like ipa150108.xml, where the 15 is the year.
            String yy = StringUtils.substring(fileName, 3, 5);
            int yr;
            try {
                yr = parseInt(yy);
            } catch (NumberFormatException e) {
                yr = 0;
            }
            year = 2000 + yr; // makes "15" into 2015
            oursOrTheirs += " fn ";
        }
        return String.format("%s%d", oursOrTheirs, year);
    }
}
