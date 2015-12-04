package mbad7090.xml;

import mbad7090.model.PatentAbstract;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;


/**
 * Created by Rajah on 12/1/2015.
 * This class counts assignees.
 * It is starting from the Apache Word Count example at wiki.apache.org/hadoop/WordCount.
 * (Adapting from the existing class had been unsuccessful.)
 */
public class AssigneeCountMapReduce extends CountXmlMapReduce {

    public static class AssigneeMap extends Mapper<LongWritable, Text, Text, IntWritable> {
        public static final String _STOCK_MATERIAL = "428";

        private Text row = new Text();

        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            try {
                PatentAbstract patent = readPatentXml(value);
                if (!patent.containsMainClassification(_STOCK_MATERIAL)) return; // return without writing if it's not the right class

                // mapWrite(context, patent);
                for (String assignee : patent.getAssigneesAsArray()) {
//            row = new Text(assignee + '\t' + patent.getCompanyName());
                    row = new Text(assignee);
                    context.write(row, one);
                }

            } catch (Exception e) {
                log.error("Error processing document:" + e.toString());
            }
        }

        /**
         * Write an assignee (like Smith@Bill) and a company (could be null) and a writable 1.
         *
         * @param context Hadoop context
         * @param patent  patent to be searching
         * @throws IOException
         * @throws InterruptedException
         */
        protected static void mapWrite(Mapper.Context context, PatentAbstract patent) throws IOException, InterruptedException {

//        if (!patent.containsMainClassification(_STOCK_MATERIAL)) return; // return without writing if it's not the right class
            for (String assignee : patent.getAssigneesAsArray()) {
//            Text row = new Text(assignee + '\t' + patent.getCompanyName());
                Text row = new Text(assignee);
                context.write(row, one);
            }
        }
    }

    public static class AssigneeReduce extends Reducer<Text, IntWritable, Text, IntWritable> {

        public void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            context.write(key, new IntWritable(sum));
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();

        Job job = new Job(conf, "assigneeCount");
        job.setJarByClass(AssigneeCountMapReduce.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        job.setMapperClass(AssigneeMap.class);
        job.setReducerClass(AssigneeReduce.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.waitForCompletion(true);
    }

}

