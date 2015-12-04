package mbad7090.xml;

import mbad7090.model.PatentAbstract;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.testng.Assert.*;

/**
 * Created by Rajah on 28Nov2015.
 */
public class AssigneeCountMapReduceTest {
    private Mapper<LongWritable, Text, Text, IntWritable> mapper;
    private MapDriver<LongWritable, Text, Text, IntWritable> driver;

    @Before
    public void setUp() throws Exception {
        mapper = new AssigneeCountMapReduce.Map();
        driver = new MapDriver<>(mapper);
    }


//    @Test
    @Ignore
    // test is working 1Dec15.
    public void testRunJob() throws Exception {
        driver.withInput(new LongWritable(1L), new Text(TestXml.abstractXml)).withOutput(new Text("THEIRS gy 2006"), new IntWritable(1)).runTest();
    }

}