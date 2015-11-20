package mbad7090.xml;
/**
 * This is part of MBAD 7090
 *  - First group homework.
 * It reads Patent XML files and maps them to csv files.
 */

import mbad7090.model.CompanyFilter;
import mbad7090.model.PatentAbstract;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.Mapper.Context;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

import static javax.xml.stream.XMLStreamConstants.CHARACTERS;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

public class PatentAbstractXmlMapReduce extends XMLMapReduce {

    /**
     * Inner class Map
     */
    public static class Map extends Mapper<LongWritable, Text, Text, Text> {

        /**
         * Map the XML input to CSV output.
         * @param key       File offset
         * @param value     XML document
         * @param context   Hadoop context
         * @throws IOException
         * @throws InterruptedException
         */
        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            try {
                PatentAbstract patent = readPatentXml(value);

                boolean willDisregard = !CompanyFilter.isTarget(patent.getCompanyName()); // or set to false to never disregard
                willDisregard = false;
                patent.cleanFields(willDisregard);

//                if (!willDisregard)  // comment this out to write all companies. Leave to just write screened companies (3M, Honeywell, GE...)
//                if (CompanyFilter.is3M(patent.getCompanyName())) // comment this out to write all companies. Leave to just write 3M
                    mapWrite(context, patent);


            } catch (Exception e) {
                log.error("Error processing document:" +  e.toString());
            }
        }
    }

    /**
     * Read through the given XML and fill in the fields in Patent.
     * @param value                 Split text containing the XML of one patent
     * @return                      patent object
     * @throws XMLStreamException
     */
    protected static PatentAbstract readPatentXml(Text value) throws XMLStreamException {
        String document = value.toString();
        try {
            XMLStreamReader reader = XMLInputFactory.newInstance()
                    .createXMLStreamReader(new ByteArrayInputStream(document.getBytes()));
            PatentAbstract patent = new PatentAbstract();
            String currentElement = "";
            while (reader.hasNext()) {
                int code = reader.next();
                switch (code) {
                    case START_ELEMENT:
                        currentElement = reader.getLocalName();
                        if (currentElement.equalsIgnoreCase("abstract")) {
                            addNested(reader, currentElement, patent);
                        }
                        break;
                    case CHARACTERS:
                        patent.addField(currentElement, reader.getText());
                        break;
                    case END_ELEMENT:
                        currentElement = reader.getLocalName();
                        patent.endElement(currentElement);
                }
            }
            reader.close();
            return patent;
        } catch (Exception e) {
            log.error("Error parsing xml document: " +  document);
            log.info("Returning null.");
            return null;
        }
    }

    /**
     * Write the patentId and the patent as a CSV row to the context.
     * @param context       Hadoop context
     * @param patent        Patent object
     * @throws IOException
     * @throws InterruptedException
     */
    protected static void mapWrite(Context context, PatentAbstract patent) throws IOException, InterruptedException {
        Long patentId;
        log.debug("About to write patent number <" + patent.getPatentNumber() + "> with <" + patent.toCsvRow() + ">.");
        try {
            patentId = Long.parseLong(patent.getPatentNumber());
        } catch (NumberFormatException e) {
            patentId = (new Date()).getTime();
        }
        context.write(patentId, patent.toCsvRow());
//        context.write(patentId, patent.toCsvDate());
    }

    public static void runJob(String input, String output) throws Exception {
        Configuration conf = getConfiguration();

        Job job = new Job(conf);
        job.setJarByClass(PatentAbstractXmlMapReduce.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.setMapperClass(Map.class);
        job.setInputFormatClass(XmlInputFormat.class);
        job.setNumReduceTasks(0);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.setInputPaths(job, new Path(input));
        Path outPath = new Path(output);
        FileOutputFormat.setOutputPath(job, outPath);
        outPath.getFileSystem(conf).delete(outPath, true);

        job.waitForCompletion(true);
    }

    protected static Configuration getConfiguration() {
        Configuration conf = new Configuration();
        conf.set("key.value.separator.in.input.line", " ");
        conf.set("xmlinput.start", "<us-patent-application");
        conf.set("xmlinput.end", "</us-patent-application>");
        return conf;
    }

    public static void main(String... args) throws Exception {
        runJob(args[0], args[1]);
    }
}
