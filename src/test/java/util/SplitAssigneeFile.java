package util;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * This is a utility to split a text file from Hive into individual assignees.
 *
 * This is a one-off (one time) file because I couldn't get AssigneeCountMapReduce to do what I wanted it to.
 *
 * It will take two lines like
 *
 * C. R. Bard, Inc.	"Sheetz@Kevin W.|Burnside@Eddie K.|Lowe@Matthew M.|Gerondale@Jay D.at|Sts@Jason R.|"
 * C. R. Bard, Inc.	"Sheetz@Kevin W.|"
 *
 * and come up with a map, such as
 *  C. R. Bard, Inc.	Sheetz@Kevin W.  2
 *  C. R. Bard, Inc.	Burnside@Eddie K.  1
 *  C. R. Bard, Inc.	Lowe@Matthew M.  1
 *  C. R. Bard, Inc.	Gerondale@Jay D.at  1
 *  C. R. Bard, Inc.	Sts@Jason R.  1
 *
 * Created by Rajah on 12/3/2015.
 *
 */
public class SplitAssigneeFile {
    public static final String OUTPUTFILE = "c:\\temp\\assignee.txt";
    public static final char _TAB = '\t';
    public static final char _NEWLINE = '\n';
    private final Logger log = LoggerFactory.getLogger(SplitAssigneeFile.class);

    private String fileToOpen = "C:\\Users\\Rajah\\Documents\\UNCC\\MBad 7090 Big Data\\Patent project\\assignees.txt";
    private BufferedWriter out = null;
    private AssigneeMap assigneeMap = new AssigneeMap();

    public void doParse() {
        int lineCount = 0;
        String company;
        int assigneeIndex;
        Scanner scanner = null;
        try {
            scanner = new Scanner(new FileInputStream(fileToOpen));
            while (scanner.hasNextLine()) {
                String inLine = scanner.nextLine();
                // Split the input line to Company and assignee at the tab.
                String[] cols = StringUtils.split(inLine, _TAB);
                if (cols.length == 1) {
                    company = "";
                    assigneeIndex = 0;
                } else {
                    company = cols[0];
                    assigneeIndex = 1;
                }

                // Now create as many AssigneeRows as there are Assignees.
                log.debug("About to parse assignees <" + cols[assigneeIndex] + ">.");
                String[] assignees = StringUtils.split(cols[assigneeIndex], "|\"");
                for (String assignee :
                        assignees) {
                    log.debug("Creating company: " + company + " and assignee " + assignee);
                    AssigneeRow row = new AssigneeRow(company, assignee);
                    assigneeMap.addAssignee(row);
                }
            }
        } catch (FileNotFoundException e) {
            log.error("Couldn't find file " + fileToOpen + ". Terminating.");
        }
        if (scanner != null) scanner.close();
        writeToFile();
    }

    private void writeToFile() {
        initOutputFile(); // initializes BufferedWriter out
        try {
            out.write(assigneeMap.allEntries().toString());
        } catch (IOException e) {
            log.error("Trouble writing to file: " + e.getMessage());
        }
        closeOutputFile();
    }


    private void initOutputFile() {
        try {
            out = new BufferedWriter(new FileWriter(OUTPUTFILE));
        } catch (IOException e) {
            log.error("Unable to open output file, " + OUTPUTFILE);
        }
    }

    private void closeOutputFile() {
        try {
            out.close();
        } catch (IOException e) {
            log.error("Trouble closing output file, " + OUTPUTFILE);
        }
    }

    public static void main(String[] args) {
        SplitAssigneeFile splitAssigneeFile = new SplitAssigneeFile();
        splitAssigneeFile.doParse();
    }

    /**
     * Inner class to represent the {Company, Assignee} pair.
     */
    private class AssigneeRow {
        public static final int _COMPANY_STEM_LEN = 22;  // only use this many chars for the Company name.
        private String company;
        private String assignee;

        public String getCompany() { return company; }

        public void setCompany(String company) { this.company = company; }

        public String getAssignee() { return assignee; }

        public void setAssignee(String assignee) { this.assignee = assignee; }

        public AssigneeRow(String company, String assignee) {
            this.company = org.apache.commons.lang.StringUtils.left(company, _COMPANY_STEM_LEN);
            this.assignee = StringUtils.stripToEmpty(assignee);
        }

        @Override
        public String toString() {
            return assignee + _TAB + company;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            AssigneeRow that = (AssigneeRow) o;

            if (company != null ? !company.equals(that.company) : that.company != null) return false;
            return !(assignee != null ? !assignee.equals(that.assignee) : that.assignee != null);

        }

        @Override
        public int hashCode() {
            int result = company != null ? company.hashCode() : 0;
            result = 31 * result + (assignee != null ? assignee.hashCode() : 0);
            return result;
        }
    }

    private class AssigneeMap {
        private Map<AssigneeRow, Integer> assignees;

        /**
         * Constructor. Initialize the Map.
         */
        public AssigneeMap() {
            this.assignees = new HashMap<>();
        }

        public void addAssignee(AssigneeRow row) {
            if (assignees.containsKey(row)) {
                int count = assignees.get(row);
                assignees.put(row, count + 1);
            } else {
                assignees.put(row, 1);
            }
        }

        public void printAll() {
            System.out.println(allEntries().toString());
        }

        public StringBuffer allEntries() {
            StringBuffer ans = new StringBuffer();
            for (Map.Entry<AssigneeRow, Integer> pair : assignees.entrySet()) {
                ans.append(pair.getKey().toString()).append(_TAB).append(pair.getValue()).append(_NEWLINE);
            }
            return ans;
        }
    }


}
