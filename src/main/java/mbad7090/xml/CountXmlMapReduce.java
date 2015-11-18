package mbad7090.xml;

import mbad7090.model.PatentAbstract;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * abstract class for any kind of MapReduce that counts something.
 *
 * Created by Rajah on 11/13/2015.
 */
abstract public class CountXmlMapReduce extends PatentAbstractXmlMapReduce {
    protected final static IntWritable one = new IntWritable(1);

    protected static Job getJob(Configuration conf) throws IOException {
        Job job = new Job(conf);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setMapperClass(Map.class);
        job.setInputFormatClass(XmlInputFormat.class);
        job.setCombinerClass(OccurrenceReducer.class);
        job.setReducerClass(OccurrenceReducer.class);
        job.setNumReduceTasks(4);
        job.setOutputValueClass(IntWritable.class);
        return job;
    }

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
     * Inner Reducer class OccurrenceReducer
     */
    public static class OccurrenceReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
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

}
