package is.merkor.statistics.cli;

/*******************************************************************************
 * MerkOrExtraction
 * Copyright (c) 2012 Anna B. Nikulásdóttir
 * 
 * License: GNU Lesser General Public License. 
 * See: <http://www.gnu.org/licenses> and <README.markdown>
 * 
 *******************************************************************************/

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import is.merkor.statistics.clustering.Cluster;
import is.merkor.statistics.clustering.ClusteringByCommittee;
import is.merkor.statistics.clustering.DataPoint;
import is.merkor.statistics.clustering.SimilarityComputation;
import is.merkor.statistics.cooccurrence.CooccurrenceProcessing;
import is.merkor.statistics.relations.LMIProcessing;
import is.merkor.statistics.relations.LMI_to_DB;
import is.merkor.statistics.relations.RelationMerger;
import is.merkor.statistics.relations.RelationTensor;
import is.merkor.util.FileCommunicatorReading;
import is.merkor.util.FileCommunicatorWriting;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;

/**
 * A command line interface to the MerkOr Extraction package - work in progress version.
 * 
 * Run java -jar MerkOrExtraction.jar -help to see options available.
 * 
 * More instructions in file README.markdown included in the MerkOrExtraction package.
 * 
 * Usage 1:
 * Create an SQL file containing mappings of ice-nlp tags and BÍN tags for nouns, adjectives
 * and verbs. Filters non-valid words of these classes and writes them out in a text file.
 * 
 * merkorExtractor.jar -bin_mapping -input <inputDir_or_inputFile>
 * 
 * @author Anna B. Nikulasdottir
 * @version 0.8
 *
 */
public class Main {
	
	public static void processCommandLine (final CommandLine cmdLine) {
		String input = null;
		String output = null;
		
		if (cmdLine.hasOption("help") || cmdLine.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -jar merkor-statistics.jar", MerkorCommandLineOptions.options);
		}
		
		if (cmdLine.hasOption("input")) {
			input = cmdLine.getOptionValue("input");
		}
		if (cmdLine.hasOption("output")) {
			output = cmdLine.getOptionValue("output");
		}
		// create tensor from db
		if (cmdLine.hasOption("tensor")) {
			System.out.println("tensor ...");
			output = "relationTensor.txt";
			
			RelationTensor tensor = new RelationTensor(output);
			System.out.println("tensor created ...");
			tensor.createTensorFromDB(false);
		}
		// merge tensor file
		if (cmdLine.hasOption("merge")) {
			String tensorfile = cmdLine.getOptionValue("merge");
			String mergedfile = tensorfile.substring(0, tensorfile.indexOf('.')) + "_merged.ds";
			RelationMerger merger = new RelationMerger();
			merger.mergeTensor(tensorfile, mergedfile);
		}
		if (cmdLine.hasOption("lmi")) {
			String tensorfile = cmdLine.getOptionValue("lmi");
			String lmifile = tensorfile.substring(0, tensorfile.indexOf('.')) + "_lmi.ds";
			LMIProcessing lmi = new LMIProcessing();
			lmi.computeLMIForFile(tensorfile, lmifile);
		}
		if (cmdLine.hasOption("lmi2db")) {
			String tablename = "lexrelation_lmi";
			if (cmdLine.hasOption("tablename"))
				tablename = cmdLine.getOptionValue("tablename");
			
			String lmifile = cmdLine.getOptionValue("lmi2db");
			String dbfile = lmifile.substring(0, lmifile.indexOf('.')) + ".sql";
			LMI_to_DB lmi = new LMI_to_DB();
			lmi.lmiToDB(lmifile, dbfile, tablename);
		}
		
		if (cmdLine.hasOption("wordwindow")) {
			String in = cmdLine.getOptionValue("wordwindow");
			CooccurrenceProcessing cooccProc = new CooccurrenceProcessing();
			cooccProc.computeCooccurrences(in);
		}
		
		if (cmdLine.hasOption("sim")) {
			String sparseMatrixDir = cmdLine.getOptionValue("sim");
			if (cmdLine.hasOption("tosql")) {
				// process 2sql
				String infile = cmdLine.getOptionValue("input");
				List<String> wordlist = FileCommunicatorReading.getLinesFromFileAsStrings(infile);
				String sqlfile = cmdLine.getOptionValue("tosql");
				int nr = 10;
				if (cmdLine.hasOption("n"))
					nr = Integer.parseInt(cmdLine.getOptionValue("n"));
				try {
					SimilarityComputation simComp = new SimilarityComputation(sparseMatrixDir);
					simComp.similarityListsToSQL(wordlist, sqlfile, nr);
				} catch (Exception e) {
					System.err.println("couldn't create datapoints for similarity computation!");
				}
			}
			else if (cmdLine.hasOption("tocsv")) {
				// process 2csv
				String infile = cmdLine.getOptionValue("input");
				List<String> wordlist = FileCommunicatorReading.getLinesFromFileAsStrings(infile);
				String sqlfile = cmdLine.getOptionValue("tocsv");
				int nr = 10;
				if (cmdLine.hasOption("n"))
					nr = Integer.parseInt(cmdLine.getOptionValue("n"));
				try {
					SimilarityComputation simComp = new SimilarityComputation(sparseMatrixDir);
					simComp.similarityListsToCSV(wordlist, sqlfile, nr);
				} catch (Exception e) {
					System.err.println("couldn't create datapoints for similarity computation!");
				}
			}
			else if (cmdLine.hasOption("w1") && cmdLine.hasOption("w2")) {
				// sim of two words
				try {
					SimilarityComputation simComp = new SimilarityComputation(sparseMatrixDir);
					double sim = simComp.getSimilarity(cmdLine.getOptionValue("w1"), cmdLine.getOptionValue("w2"));
					System.out.println("similarity: " + sim);
				} catch (Exception e) {
					
				}
			}
			else if (cmdLine.hasOption("w1") && cmdLine.hasOption("n")) {
				// n sim words of w1
				try {
					SimilarityComputation simComp = new SimilarityComputation(sparseMatrixDir);
					Map<Double, String> map = simComp.getMostSimilarWords(cmdLine.getOptionValue("w1"), Integer.parseInt(cmdLine.getOptionValue("n")));
					for (Double d : map.keySet()) {
						System.out.println(map.get(d) + " - " + d);
					}
				} catch (Exception e) {
					
				}
			}
			else if (cmdLine.hasOption("cbc")) {
				String matrix = cmdLine.getOptionValue("cbc");
				String simFile = cmdLine.getOptionValue("simlists");
				ClusteringByCommittee clustering = new ClusteringByCommittee(simFile, matrix);
				List<Cluster> committees = clustering.getCommittees(FileCommunicatorReading.getWordsFromFileAsList("sparseMatrix/words.txt"));
				int counter = 0;
				for (Cluster c : committees) {
					counter++;
					System.out.println("committe nr: " + counter);
					System.out.println(c);
				}
				List<Cluster> finalClusters = clustering.cluster(committees);
				List<String> clustersOut = new ArrayList<String>();
				for (Cluster c : finalClusters) {
					System.out.println("CLUSTER " + c);
					clustersOut.add("CLUSTER " + c + " ==================== ");
					for (DataPoint dp : c.getDataPoints()) {
						clustersOut.add(dp.getName());
					}
				}
				FileCommunicatorWriting.writeListNonAppend("clusters_cbc.txt", clustersOut);
			}
		}
	}
	
	public static void main (String[] args) throws Exception {
		
		
		CommandLineParser parser = new GnuParser();
	    try {
	    	MerkorCommandLineOptions.createOptions();
	    	processCommandLine(parser.parse(MerkorCommandLineOptions.options, args));
	    }
	    catch(ParseException e) {
	        System.err.println("Parsing failed.  Reason: " + e.getMessage());
	    }	
	}
}

