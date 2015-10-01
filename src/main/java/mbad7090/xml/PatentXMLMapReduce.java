package mbad7090.xml;


import mbad7090.model.Patent;
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
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

import static javax.xml.stream.XMLStreamConstants.CHARACTERS;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

public final class PatentXMLMapReduce extends XMLMapReduce {

    public static void runJob(String input,
                              String output)
            throws Exception {
        Configuration conf = new Configuration();
        conf.set("key.value.separator.in.input.line", " ");
        conf.set("xmlinput.start", "<us-patent-application");
        conf.set("xmlinput.end", "</us-patent-application>");

        Job job = new Job(conf);
        job.setJarByClass(PatentXMLMapReduce.class);
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

    public static class Map extends Mapper<LongWritable, Text, Text, Text> {

        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            String document = value.toString();
            // System.out.println("'" + document + "'");
            try {
                XMLStreamReader reader = XMLInputFactory.newInstance()
                        .createXMLStreamReader(new ByteArrayInputStream(document.getBytes()));
                Patent patent = new Patent();
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
                            patent.endField(currentElement);
                    }
                }
                reader.close();
//                if (!CompanyFilter.isTarget(patent.getCompanyName())) {
//                    patent.disregardAbstract();
//                }
                mapWrite(context, patent);

            } catch (Exception e) {
                log.error("Error processing '" + document + "'", e);
            }
        }
    }

    protected static void mapWrite(Context context, Patent patent) throws IOException, InterruptedException {
        Long patentId;
        log.debug("About to write patent number <" + patent.getPatentNumber() + "> with <" + patent.toCsvRow() + ">.");
        try {
            patentId = Long.parseLong(patent.getPatentNumber());
        } catch (NumberFormatException e) {
            patentId = (new Date()).getTime();
        }
        context.write(patentId, patent.toCsvRow());
    }

    public static void main(String... args) throws Exception {
        runJob(args[0], args[1]);
    }

}
