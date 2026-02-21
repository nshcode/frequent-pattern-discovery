# Frequent Pattern Discovery

## About 
The project currently contains the Java source code of an implementation of the FP-Growth algorithm for discovering frequent item sets in transactional datasets.
The title of the original scientific paper is [Mining Frequent Patterns without Candidate Generation](https://dl.acm.org/doi/10.1145/335191.335372)

## How to build
1.	Make sure that your working directory is the directory where you unzipped the project.
2.	Execute
    -  `mvn clean package`
      
  Maven will create a jar file called fpgrowth.jar in the working directory.

## How to execute
1.	fpgrowth.jar expects two properties files:
	  -   fpgrowth.properties file (required) that defines the input-parameter of the algorithm. You must update this file to meet your case (see conf/fpgrowth.properties).
    -  logging.properties file (optional) for the configuration of the logging output (see conf/logging.properties).
2.	If you do not change the default location of the properties ‘files, which is the directory conf in the directory of the jar file, you simply execute

     -   `java -jar fpgrothw.jar`
  
    Otherwise, you execute
      -     java  
              -DfpGrowthPropFileName=yourPathTo/fpgrowth.properties 
              -Djava.util.logging.config.file=yourPathTo/logging.properties 
              -jar fpgrowth.jar

    As the logging.properties file  is optional, the default configuration of the java.util.logging framework will be taken if you omit the location of this file. 
    
