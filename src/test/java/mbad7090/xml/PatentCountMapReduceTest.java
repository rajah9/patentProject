package mbad7090.xml;

import mbad7090.model.Patent;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.junit.Ignore;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by Rajah on 10/3/2015.
 */
public class PatentCountMapReduceTest {
    private Mapper<LongWritable, Text, Text, IntWritable> mapper;
    private MapDriver<LongWritable, Text, Text, IntWritable> driver;

    @BeforeMethod
    public void setUp() throws Exception {
        mapper = new PatentCountMapReduce.Map();
        driver = new MapDriver<LongWritable, Text, Text, IntWritable>(mapper);
    }

    @Test
//    @Ignore
    public void testRunJob() throws Exception {
        driver.withInput(new LongWritable(1L), new Text(TestXml.testXml)).withOutput(new Text("THEIRSgy2006"), new IntWritable(1)).runTest();
    }

    @Test(dataProvider="ownershipYear")
    public void testOwnershipYearCode(String company, String fileName, int year, String expected) throws Exception {
        Patent patent = new Patent();
        patent.setCompanyName(company);
        patent.setYearGranted(year);
        assertEquals(PatentCountMapReduce.ownershipYearCode(patent, fileName), expected);
    }

    @DataProvider
    public Object[][] ownershipYear() {
        return new Object[][]{
                {"3M Innovations", "ipa150108.xml", 2006, "OURSfn2015"},
                {"Honeywell", "ipa120108.xml", 2007, "THEIRSfn2012"},
                {"3M Innovations", "", 2009, "OURSgy2009"},
                {"3M Innovations", "noyearinthisfile", 2010, "OURSfn2000"},
                {"3M Innovations", "Shrt", 2010, "OURSfn2000"}, // filename too short
                {"Honeywell", null, 2011, "THEIRSgy2011"}
        };
    }
}