package mbad7090.model;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static javax.xml.stream.XMLStreamConstants.CHARACTERS;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

/**
 * This is for reading an ipa Patent Trade Office abstract XML file.
 * This is not a Java abstract, this is a patent abstract.
 * Created by Rajah on 10/20/2015.
 */
public class PatentAbstract extends Patent {
    private String patentAbstract = "";

    public String getPatentAbstract() { return patentAbstract; }

    /**
     * If appropriate, add the fields related to the Patent Abstract to the object.
     *
     * @param currentElement XML tag, like orgname or doc-number
     * @param text           text within the tag, like IBM or 20100281593
     */
    @Override
    public void addField(final String currentElement, final String text) {
        String value = text.trim();
        log.debug("Got currentElement " + currentElement + " and text " + text);
//   Handling Abstract (and its embedded tags) in its own method.
        if (currentElement.equalsIgnoreCase("abstract")) {
            patentAbstract += value;
            log.debug("Made patent abstract " + patentAbstract);
        } else {
            super.addField(currentElement, text);
        }

    }

    /**
     * Clean the fields of embedded tabs and linefeeds. If willDisregardAbstract
     * is true, make the patentAbstract into a small handful of stem words.
     *
     * @param willDisregardAbstract if true, call disregardAbstract.
     */
    public void cleanFields(boolean willDisregardAbstract) {
        super.cleanFields();
        if (willDisregardAbstract) {
            disregardAbstract();
        } else {
            patentAbstract = cleanAndTrim(patentAbstract);
        }
    }

    /**
     * Convert to a string of field names, separated by the default delimiter.
     *
     * @return csv row, separated by default delimiter.
     */
    public String toCsvRow() {
        return super.toCsvRow() + getPatentAbstract();
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

    /**
     * Make the abstract just a handful of stem words that will not be regarded in the word cloud.
     */
    public void disregardAbstract() {
        patentAbstract = "the of and it";
    }
}
