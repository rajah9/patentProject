package mbad7090.xml;

import mbad7090.model.CompanyFilter;
import mbad7090.model.Patent;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;
import java.util.Date;

/**
 * Created by Rajah on 10/1/2015.
 * This file extends the Map and does a MapReduce on the Patent files.
 */
public class PatentCountMapReduce extends XMLMapReduce {
    private final static IntWritable one = new IntWritable(1);

    /**
     * Inner class Map.
     */
    public static class Map extends Mapper<LongWritable, Text, Text, IntWritable> {

        /**
         * Map the XML input to a whether this is our patent or not.
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
                Patent patent = readPatentXml(value);

                mapWrite(context, patent);

            } catch (Exception e) {
                log.error("Error processing document:" +  e.toString());
            }
        }
    }

    /**
     * Inner Reducer class CodeOccurrenceReducer
     */
    public static class CodeOccurrenceReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable occurrencesOfCode = new IntWritable();

        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            occurrencesOfCode.set(sum);
            context.write(key, occurrencesOfCode);
        }
    }

    /**
     * Write a code (like OURS2006 or THEIRS2012) and a writable 1.
     * @param context   Hadoop context
     * @param patent    patent to be searching
     * @throws IOException
     * @throws InterruptedException
     */
    protected static void mapWrite(Mapper.Context context, Patent patent) throws IOException, InterruptedException {
        String fileName;
        try {
            fileName = ((FileSplit) context.getInputSplit()).getPath().getName();
        } catch (Exception e) {
            fileName = null;
        }

        Text code = new Text(ownershipYearCode(patent, fileName));

        context.write(code, one);
    }

    /**
     * Return a code based on whether it was our company or theirs and the year
     * from XML file or Patent Year granted.
     *
     * @param patent    Patent
     * @param fileName  filename, like ipa150108.xml
     * @return          code (like "OURS2006" or "THEIRS2012")
     */
    protected static String ownershipYearCode(Patent patent, String fileName) {
        String oursOrTheirs;
        int year;
        if (CompanyFilter.isTarget(patent.getCompanyName())) {
            oursOrTheirs = "OURS";
        } else {
            oursOrTheirs = "THEIRS";
        }
        if (StringUtils.isEmpty(fileName)) {
            // no file name, so use grant year.
            year = patent.getYearGranted(); // could be 0 if never filled in
            oursOrTheirs += "gy";
        } else {
            // file name is like ipa150108.xml, where the 15 is the year.
            String yy = StringUtils.substring(fileName, 3, 5);
            int yr;
            try {
                yr = Integer.parseInt(yy);
            } catch (NumberFormatException e) {
                yr = 0;
            }
            year = 2000 + yr; // makes "15" into 2015
            oursOrTheirs += "fn";
        }
        return String.format("%s%d", oursOrTheirs, year);
    }

    public static void runJob(String input, String output) throws Exception {
        Configuration conf = new Configuration();
        conf.set("key.value.separator.in.input.line", " ");
        conf.set("xmlinput.start", "<us-patent-application");
        conf.set("xmlinput.end", "</us-patent-application>");

        Job job = new Job(conf);
        job.setJarByClass(PatentCountMapReduce.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setMapperClass(Map.class);
        job.setInputFormatClass(XmlInputFormat.class);
        job.setCombinerClass(CodeOccurrenceReducer.class);
        job.setReducerClass(CodeOccurrenceReducer.class);
        job.setNumReduceTasks(4);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.setInputPaths(job, new Path(input));
        Path outPath = new Path(output);
        FileOutputFormat.setOutputPath(job, outPath);
        outPath.getFileSystem(conf).delete(outPath, true);

        job.waitForCompletion(true);

    }

    public static void main(String... args) throws Exception {
        runJob(args[0], args[1]);
    }

}