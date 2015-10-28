# Helpful commands for running the patent files on Hadoop
cd Patents
hadoop fs -mkdir /user/rchacko1/patents
hadoop fs -put *.xml /user/rchacko1/patents
hadoop fs -ls /user/rchacko1/patents/*.xml
hadoop jar batman-1.1.jar mbad7090.xml.PatentAbstractXmlMapReduce /user/rchacko1/patents /user/rchacko1/outputDir
hadoop fs -ls /user/rchacko1/outputDir
hadoop fs -get /user/rchacko1/outputDir/part-m-0*
cat part-m-0* >allCompanies.csv
hadoop fs -rm /user/rchacko1/outputDir/part-m-0*
hadoop fs -rm -R /user/rchacko1/outputDir
rm part-m-0*
# hadoop job -history output
hadoop fs -ls /user/rchacko1/patents
# numbers for following are 197.8G and 593.3G.
hadoop fs -du -s -h /user/rchacko1/patents
#
hadoop jar batman-1.1.jar mbad7090.xml.PatentCountMapReduce /user/rchacko1/patents /user/rchacko1/outputDir
hadoop fs -get /user/rchacko1/outputDir/part-r-0*
cat part-r-0* >patentCount.csv
rm part-r-0*
#
hadoop fs -mkdir /user/rchacko1/econ
hadoop fs -put *.csv /user/rchacko1/econ
#
hadoop fs -mkdir /user/rchacko1/grants
hadoop fs -put *.xml /user/rchacko1/grants
hadoop jar batman-1.3.jar mbad7090.xml.PatentGrantXmlMapReduce /user/rchacko1/grants /user/rchacko1/outputDir
hadoop fs -get /user/rchacko1/outputDir/part-m-0*