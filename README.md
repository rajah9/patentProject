# patentProject
MBAD 7090 project
Scan lots of XML data and create a CSV file out of them.

#
# How to run it
# This is being compiled and packaged through IntelliJ 15, but you may also build it with a maven command:
#   mvn package to create a jar file, like batman-1.3.jar
# Here are some helpful commands for running the patent files on Hadoop
# I am working in folders in my local Patents subdirectory
cd Patents
# Make an HDFS input directory
hadoop fs -mkdir /user/rchacko1/patents
# put my xml files to that HDFS directory
hadoop fs -put *.xml /user/rchacko1/patents
# Make sure they're there
hadoop fs -ls /user/rchacko1/patents/*.xml
# Kick off the job to run the jar against the HDFS input directory. Specify an outputDir to place the results.
hadoop jar batman-1.3.jar mbad7090.xml.PatentAbstractXmlMapReduce /user/rchacko1/patents /user/rchacko1/outputDir
# Job will take about 10 min to process 500 Gb of XML data. Check the results on the HDFS output directory
hadoop fs -ls /user/rchacko1/outputDir
# Now get the output and put it in my local directory.
hadoop fs -get /user/rchacko1/outputDir/part-m-0*
# Concatenate the files into a tab-delimited csv.
# (May be better to output these to allCompanies.txt, because Excel handles it better).
cat part-m-0* >allCompanies.csv
# get rid of the output dir in HDFS (Helpful, but not essential).
hadoop fs -rm -R /user/rchacko1/outputDir
# remove the partial files on my local disk.
rm part-m-0*
#
# Adding version 1.5 that will create a map reduce of assignees (how many patents each assignee contributed to)
#
# How to use:
# Getting counts of all assignees with the main classification of Stock Materials. (not working!)
#
hadoop jar batman-1.5.jar mbad7090.xml.AssigneeCountMapReduce /user/rchacko1/patents /user/rchacko1/assigneeDir
#
# Used hive to get all of the Stock Materials classes
hive -e "SELECT company, assignee from patent_abstract where mainClassification like '%428%'; " >/users/rchacko1/Patents/assignees.txt
