# Frequent Sequences Discovery

## About 
This project is about the discovery of frequent episodes (frequent subsequences) in sequence datasets. It is an implementation of the algorithm developed in Chapter 4 of my master's thesis in Computer Science. The thesis was written at the Artificial Intelligence Chair of TU Dortmund University in 2006. The thesis is available in German at https://www-ai.cs.tu-dortmund.de/en/PublicPublicationFiles/shaabani_2007a.pdf

In contrast to frequent item sets, the order in which items (events) appear in the records (sequences) matters.

The folder example-datasets contains two datasets:
-	msnbc990928.seq is available in the [UC Irvine Machine Learning Repository](https://archive.ics.uci.edu/dataset/133/msnbc+com+anonymous+web+data)
-	word-cup-2018 is extracted from the [Soccer Dataset](https://github.com/nshcode/soccer).

## How to build
1.	Make sure that your working directory is the directory where you unzipped the project.
2.	Execute
    -  `mvn clean package`
      
  Maven will create a jar file called fpgrowth.jar in the working directory.

## How to execute
1.	freqSeqDiscoverer.jar expects two properties files:
	  -   fpgrowth.properties file (required) that defines the input-parameter of the algorithm. You must update this file to meet your case (see conf/freqSeqDicoverer.properties).
    -  logging.properties file (optional) for the configuration of the logging output (see conf/logging.properties).
2.	If you do not change the default location of the properties files, which is the conf directory  in the working directory, you simply execute

     -   `java -jar freqSeqDiscoverer.jar`
  
    Otherwise, you execute
      -     java  
              -DfreqSeqDiscPropFileName=yourPathTo/freqSeqDicoverer.properties 
              -Djava.util.logging.config.file=yourPathTo/logging.properties 
              -jar freqSeqDiscoverer.jar

    As the logging.properties file  is optional, the default configuration of the java.util.logging framework will be taken if you omit the location of this file. 
    
