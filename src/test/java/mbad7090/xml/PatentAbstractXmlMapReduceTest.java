package mbad7090.xml;

import mbad7090.model.Patent;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

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

    @Test
    public void testParseMainClassification() {
        // 0202 ==> 000 202
        String test1 = "0202";
        List<String> result1 = Patent.parseMainClassification(test1);
        assertTrue("Expected 000 but got " + result1.get(0), "000".equalsIgnoreCase(result1.get(0)));
        assertTrue("Expected 202 but got " + result1.get(1), "202".equalsIgnoreCase(result1.get(1)));
        //   2  25 ==> 002 025
        String test2 = "  2  25";
        List<String> result2 = Patent.parseMainClassification(test2);
        assertTrue("Expected 002 but got " + result2.get(0), "002".equalsIgnoreCase(result2.get(0)));
        assertTrue("Expected 025 but got " + result2.get(1), "025".equalsIgnoreCase(result2.get(1)));
        // 248215 ==> 248 215 742
        String test3 = "248215742";
        List<String> result3 = Patent.parseMainClassification(test3);
        assertTrue("Expected 248 but got " + result3.get(0), "248".equalsIgnoreCase(result3.get(0)));
        assertTrue("Expected 215 but got " + result3.get(1), "215".equalsIgnoreCase(result3.get(1)));
        assertTrue("Expected 742 but got " + result3.get(1), "742".equalsIgnoreCase(result3.get(2)));
        // 248 742 ==> 248 742
        String test4 = "248 742";
        List<String> result4 = Patent.parseMainClassification(test4);
        assertTrue("Expected 248 but got " + result4.get(0), "248".equalsIgnoreCase(result4.get(0)));
        assertTrue("Expected 742 but got " + result4.get(1), "742".equalsIgnoreCase(result4.get(1)));
        // D19 86 ==> D19 086
        String test5 = "D19 86";
        List<String> result5 = Patent.parseMainClassification(test5);
        assertTrue("Expected D19 but got " + result5.get(0), "D19".equalsIgnoreCase(result5.get(0)));
        assertTrue("Expected 086 but got " + result5.get(1), "086".equalsIgnoreCase(result5.get(1)));
    }

    @Test
    public void testParseClassification() {
        String test1 = "001";
        List<String> result1 = Patent.parseClassification(test1);
        assertTrue("Expected 001 expected but got " + result1.get(0), "001".equalsIgnoreCase(result1.get(0)));
        String test2 = "2";
        List<String> result2 = Patent.parseClassification(test2);
        assertTrue("Expected 002 expected but got " + result2.get(0), "002".equalsIgnoreCase(result2.get(0)));
    }

//        @Test
    @Ignore
    public void testRunJob() throws Exception {
        driver.withInput(new LongWritable(1L), new Text(TestXml.abstractXml)).withOutput(new Text("foo"), new Text("bar")).runTest();
    }
}