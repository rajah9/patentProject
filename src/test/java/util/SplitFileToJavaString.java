package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Scanner;

/**
 * This is a utility to split files into Java Strings.
 * Since the grant patents are so long (on the order of 105 Meg) they won't fit into a Java constant,
 * which has a maximum length of 65,535 bytes.
 *
 * Created by Rajah on 10/22/2015.
 */
public class SplitFileToJavaString {
    public static final char NEWLINE = '\n';
    public static final String OUTPUTFILE = "c:\\temp\\file.java";
    private final Logger log = LoggerFactory.getLogger(SplitFileToJavaString.class);
    private final String JAVA_VAR_PREFIX = "grantXml";
    private final int MAX_LEN = 65535;

    private String fileToOpen = "C:\\Users\\Rajah\\Documents\\UNCC\\MBad 7090 Big Data\\Patent project\\Patent Grant\\ipg120214.xml";
    int whichFile = 0;
    private BufferedWriter out = null;
    private StringBuffer decls = new StringBuffer();

    public void doSplit() {
        StringBuffer sb = null;
        initOutputFile();

        sb = initNewVar();
        Scanner scanner = null;
        try {
            scanner = new Scanner(new FileInputStream(fileToOpen));
            while (scanner.hasNextLine() && whichFile < 45) {
                String inLine = scanner.nextLine();
                if (inLine.length() + sb.length() > MAX_LEN) {
                    // time to emit the buffer
                    emitVariable(sb);
                    sb = initNewVar();
                }
                sb.append(inLine);
            }
        } catch (FileNotFoundException e) {
            log.error("Couldn't find file " + fileToOpen + ". Terminating.");
        }
        scanner.close();
        closeOutputFile();
        return;

    }

    private void emitVariable(StringBuffer sb) {
        sb.append(NEWLINE).append(NEWLINE);
        System.out.println(sb.toString());
        if (null != out) {
            try {
                out.write(sb.toString());
            } catch (IOException e) {
                log.error("Error writing to output file: " + OUTPUTFILE);
            }
        }
    }

    private StringBuffer initNewVar() {
        whichFile++;
        StringBuffer ans = new StringBuffer(MAX_LEN);
        ans.append("static final String grantXml");
        ans.append(String.format("%02d", whichFile));
        ans.append(" = ").append("\"insertHere\";").append(NEWLINE);
        decls.append(ans);
        return ans;
    }

    private void initOutputFile() {
        try {
            out = new BufferedWriter(new FileWriter(OUTPUTFILE));
        } catch (IOException e) {
            log.error("Unable to write to output file, " + OUTPUTFILE);
        }
    }

    private void closeOutputFile() {
        try {
            out.write("Just Java declaractions:\n");
            out.write(decls.toString());
            out.close();
        } catch (IOException e) {
            log.error("Trouble closing output file, " + OUTPUTFILE);
        }
    }

    public static void main(String[] args) {
        SplitFileToJavaString splitFileToJavaString = new SplitFileToJavaString();

        splitFileToJavaString.doSplit();
    }
}
