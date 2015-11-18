package mbad7090.xml;

import mbad7090.model.PatentAbstract;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * Created by Rajah on 10/1/2015.
 * This file extends the Map and does a MapReduce on the Patent files.
 */
public class PatentCountMapReduce extends CountXmlMapReduce {



    /**
     * Write a code (like OURS2006 or THEIRS2012) and a writable 1.
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

        Text code = new Text(patent.ownershipYearCode(fileName));

        context.write(code, one);
    }



    public static void runJob(String input, String output) throws Exception {
        Configuration conf = getConfiguration();

        Job job = getJob(conf);
        job.setJarByClass(PatentCountMapReduce.class);

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
