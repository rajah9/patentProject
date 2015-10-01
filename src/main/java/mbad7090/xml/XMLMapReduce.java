package mbad7090.xml;

import mbad7090.model.Patent;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static javax.xml.stream.XMLStreamConstants.CHARACTERS;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

/**
 * Created by Rajah on 10/1/2015.
 */
abstract class XMLMapReduce {
    protected static final Logger log = LoggerFactory.getLogger(PatentXMLMapReduce.class);

    /**
     * Need special processing for the abstract tag, which will have one or more
     * tags (such as p and b) embedded.
     *
     * @param reader        Continue reading from this stream until the end of the parentElement tag.
     * @param parentElement Parent element, such as "abstract."
     * @throws XMLStreamException
     */
    protected static void addNested(XMLStreamReader reader, String parentElement, Patent patent) throws XMLStreamException {
        getAttribs(reader);
        String currentElement;
        StringBuffer value = new StringBuffer();

        while (reader.hasNext()) {
            int code = reader.next();
            switch (code) {
                case START_ELEMENT:
                    currentElement = reader.getLocalName();
                    if (currentElement.equalsIgnoreCase("p")) { getAttribs(reader); }
//                    if (currentElement.equalsIgnoreCase("b")) {  }
                    break;
                case CHARACTERS:
                    value.append(reader.getText());
                    break;
                case END_ELEMENT:
                    currentElement = reader.getLocalName();
                    if (currentElement.equalsIgnoreCase(parentElement)) {
                        // got the matching closing tag, such as </abstract>
                        patent.addField(parentElement, value.toString());
                        return;
                    }
            }
        }
    }

    private static void getAttribs(XMLStreamReader reader) {
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String attrib = reader.getAttributeValue(i);
            log.debug("Got an attribute: " + attrib);
        }
    }

}
