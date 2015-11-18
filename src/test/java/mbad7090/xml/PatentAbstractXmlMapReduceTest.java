package mbad7090.xml;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by Rajah on 9/23/2015.
 */
public class PatentAbstractXmlMapReduceTest {

    private Mapper<LongWritable, Text, Text, Text> mapper;
    private MapDriver<LongWritable, Text, Text, Text> driver;

    @Before
    public void setUp() throws Exception {
        mapper = new PatentAbstractXmlMapReduce.Map();
        driver = new MapDriver<>(mapper);
    }

//        @Test
    @Ignore
    public void testRunJob() throws Exception {
        driver.withInput(new LongWritable(1L), new Text(TestXml.abstractXml)).withOutput(new Text("foo"), new Text("bar")).runTest();
    }
}