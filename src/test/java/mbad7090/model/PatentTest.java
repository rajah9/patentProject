package mbad7090.model;


import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Rajah on 11/28/2015.
 */
public class PatentTest {

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
}