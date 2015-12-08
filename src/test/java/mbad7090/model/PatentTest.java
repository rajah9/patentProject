package mbad7090.model;


import mbad7090.xml.PatentAbstractXmlMapReduce;
import mbad7090.xml.TestXml;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Rajah on 11/28/2015.
 */
public class PatentTest {
    private Mapper<LongWritable, Text, Text, Text> mapper;
    private MapDriver<LongWritable, Text, Text, Text> driver;

    @Before
    public void setUp() throws Exception {
        mapper = new PatentAbstractXmlMapReduce.Map();
        driver = new MapDriver<>(mapper);
    }

    @Test
    public void testContainsMainClassification() throws Exception {
        Patent patent = new PatentAbstract();
        patent.addMainClassification("428");
        patent.addMainClassification("156");
        assertTrue(patent.containsMainClassification("428"));
        assertFalse(patent.containsMainClassification("000"));

    }

    @Test
    public void testGetAssigneesAsArray() throws Exception {
        Patent patent = new PatentAbstract();
        String name1 = "Smith@Bob";
        String name2 = "Van Halen@Eddie";
        patent.addAssignee(name1);

        String[] singleResult = patent.getAssigneesAsArray();
        assertTrue(singleResult.length == 1);
        assertTrue(singleResult[0].equalsIgnoreCase(name1));

        patent.addAssignee(name2);
        String[] dualResult = patent.getAssigneesAsArray();
        assertTrue(dualResult.length == 2);
        assertTrue(dualResult[0].equalsIgnoreCase(name1));
        assertTrue(dualResult[1].equalsIgnoreCase(name2));
    }

            @Test
//    @Ignore
    public void testRunJob() throws Exception {
        driver.withInput(new LongWritable(1L), new Text(TestXml.abstractXml)).withOutput(new Text("foo"), new Text("bar")).runTest();
    }
}