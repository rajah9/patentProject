package mbad7090.xml;

import mbad7090.model.PatentAbstract;
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

import java.io.IOException;

/**
 * Created by Rajah on 11/12/2015.
 * This one counts the classes for 3M.
 *
 */
public class PatentClassXmlMapReduce extends PatentAbstractXmlMapReduce {
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
                PatentAbstract patent = readPatentXml(value);

                mapWrite(context, patent);

            } catch (Exception e) {
                log.error("Error processing document:" +  e.toString());
            }
        }
    }

    /**
     * Inner Reducer class PatClassOccurrenceReducer
     */
    public static class PatClassOccurrenceReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable occurrencesOfPatClass = new IntWritable();

        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            occurrencesOfPatClass.set(sum);
            context.write(key, occurrencesOfPatClass);
        }
    }

    /**
     * Write a main patent class (like 003 or 128) and a writable 1.
     * @param context   Hadoop context
     * @param patent    patent to be searching
     * @throws IOException
     * @throws InterruptedException
     */
    protected static void mapWrite(Mapper.Context context, PatentAbstract patent) throws IOException, InterruptedException {
        String fileName;
        try {
            fileName = ((FileSplit) context.getInputSplit()).getPath().getName();
        } catch (Exception e) {
            fileName = null;
        }

        Text patClass = new Text(patent.getMainClassification());

        context.write(patClass, one);
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
        job.setCombinerClass(PatClassOccurrenceReducer.class);
        job.setReducerClass(PatClassOccurrenceReducer.class);
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
