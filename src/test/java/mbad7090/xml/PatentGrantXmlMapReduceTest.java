package mbad7090.xml;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.junit.Before;
import org.junit.Ignore;

/**
 * Created by Rajah on 9/23/2015.
 */
public class PatentGrantXmlMapReduceTest {

    private Mapper<LongWritable, Text, Text, Text> mapper;
    private MapDriver<LongWritable, Text, Text, Text> driver;


//    @Test
    @Ignore
    public void testRunJob() throws Exception {
        mapper = new PatentGrantXmlMapReduce.Map();
        driver = new MapDriver<>(mapper);
        Text grantXml = generateLongText();
        try {
            driver.withInput(new LongWritable(1L), grantXml).withOutput(new Text("foo"), new Text("bar")).runTest();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Text generateLongText() {
        Text longText = new Text(TestXml.grantXml01);
/*
        longText.append(TestXml.grantXml02.getBytes(), 0, TestXml.grantXml02.length());
        longText.append(TestXml.grantXml03.getBytes(), 0, TestXml.grantXml03.length());
        longText.append(TestXml.grantXml04.getBytes(), 0, TestXml.grantXml04.length());
        longText.append(TestXml.grantXml05.getBytes(), 0, TestXml.grantXml05.length());
        longText.append(TestXml.grantXml06.getBytes(), 0, TestXml.grantXml06.length());
        longText.append(TestXml.grantXml07.getBytes(), 0, TestXml.grantXml07.length());
        longText.append(TestXml.grantXml08.getBytes(), 0, TestXml.grantXml08.length());
        longText.append(TestXml.grantXml09.getBytes(), 0, TestXml.grantXml09.length());
        longText.append(TestXml.grantXml10.getBytes(), 0, TestXml.grantXml10.length());

        longText.append(TestXml.grantXml11.getBytes(), 0, TestXml.grantXml11.length());
        longText.append(TestXml.grantXml12.getBytes(), 0, TestXml.grantXml12.length());
        longText.append(TestXml.grantXml13.getBytes(), 0, TestXml.grantXml13.length());
        longText.append(TestXml.grantXml14.getBytes(), 0, TestXml.grantXml14.length());
        longText.append(TestXml.grantXml15.getBytes(), 0, TestXml.grantXml15.length());
        longText.append(TestXml.grantXml16.getBytes(), 0, TestXml.grantXml16.length());
        longText.append(TestXml.grantXml17.getBytes(), 0, TestXml.grantXml17.length());
        longText.append(TestXml.grantXml18.getBytes(), 0, TestXml.grantXml18.length());
        longText.append(TestXml.grantXml19.getBytes(), 0, TestXml.grantXml19.length());

        longText.append(TestXml.grantXml20.getBytes(), 0, TestXml.grantXml20.length());
        longText.append(TestXml.grantXml21.getBytes(), 0, TestXml.grantXml21.length());
*/
        return longText;
/*
        + TestXml.grantXml02 + TestXml.grantXml03 + TestXml.grantXml04 +
                TestXml.grantXml05 + TestXml.grantXml06 + TestXml.grantXml07 + TestXml.grantXml08 +
                TestXml.grantXml09 + TestXml.grantXml10 + TestXml.grantXml11 + TestXml.grantXml12 +
                TestXml.grantXml13 + TestXml.grantXml14 + TestXml.grantXml15 + TestXml.grantXml16 +
                TestXml.grantXml17 + TestXml.grantXml18 + TestXml.grantXml19 + TestXml.grantXml20 + TestXml.grantXml21);
*/

    }
}