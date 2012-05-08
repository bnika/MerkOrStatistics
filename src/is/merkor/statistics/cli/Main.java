package is.merkor.statistics.cli;

/*******************************************************************************
 * MerkOrExtraction
 * Copyright (c) 2012 Anna B. Nikulásdóttir
 * 
 * License: GNU Lesser General Public License. 
 * See: <http://www.gnu.org/licenses> and <README.markdown>
 * 
 *******************************************************************************/

import is.merkor.statistics.relations.LMIProcessing;
import is.merkor.statistics.relations.RelationMerger;
import is.merkor.statistics.relations.RelationTensor;

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

