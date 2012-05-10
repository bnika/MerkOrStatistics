package is.merkor.statistics.cli;

/*******************************************************************************
 * MerkOrCore
 * Copyright (c) 2012 Anna B. Nikulásdóttir
 * 
 * License: GNU Lesser General Public License. 
 * See: <http://www.gnu.org/licenses> and <README.markdown>
 * 
 *******************************************************************************/

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

/**
 * Options for the MerkOrStatistics command line interface.
 * 
 * @author Anna B. Nikulasdottir
 * @version 0.8
 *
 */
public class MerkorCommandLineOptions {
	
	private static Option help;
	private static Option help_h;
	private static Option tensor;
	
	private static Option input;
	private static Option output;
	private static Option merge;
	private static Option lmi;
	private static Option lmi2db;
	private static Option tablename;
	private static Option wordwindow;
	private static Option sim;
	private static Option tosql;
	private static Option tocsv;
	private static Option w1;
	private static Option w2;
	private static Option n;
	
	public static Options options = new Options();
	
	public static void createOptions () {
		
		createBooleanOptions();
		createArgumentOptions();
		
		options.addOption(help);
		options.addOption(help_h);
		options.addOption(input);
		options.addOption(output);
		options.addOption(tensor);
		options.addOption(merge);
		options.addOption(lmi);
		options.addOption(lmi2db);
		options.addOption(tablename);
		options.addOption(wordwindow);
		options.addOption(sim);
		options.addOption(w1);
		options.addOption(w2);
		options.addOption(tosql);
		options.addOption(tocsv);
		options.addOption(n);
		
	}

	private static void createBooleanOptions() {
		help = new Option("help", "print this message");
		help_h = new Option("h", "print this message");
		tensor = new Option("tensor", "create tensor from relations db");
		
	}
	private static void createArgumentOptions() {
		input  = OptionBuilder.withArgName("input file or directory")
			.hasArg()
			.withDescription("the input file or directory")
			.create("input");
		output  = OptionBuilder.withArgName("output file or directory")
			.hasArg()
			.withDescription("the output file or directory")
			.create("output");
		merge  = OptionBuilder.withArgName("file to merge")
		.hasArg()
		.withDescription("file to merge")
		.create("merge");
		lmi  = OptionBuilder.withArgName("file to compute lmi")
		.hasArg()
		.withDescription("file to compute lmi")
		.create("lmi");
		lmi2db  = OptionBuilder.withArgName("create db insert statements from argument file")
		.hasArg()
		.withDescription("create db insert statements from argument file")
		.create("lmi2db");
		tablename  = OptionBuilder.withArgName("db tablename")
		.hasArg()
		.withDescription("db tablename")
		.create("tablename");
		wordwindow  = OptionBuilder.withArgName("input file or directory")
		.hasArg()
		.withDescription("file or directory to compute word window cooccurrence statistics")
		.create("wordwindow");
		sim  = OptionBuilder.withArgName("compute similarities using the argument sparse matrix directory")
		.hasArg()
		.withDescription("compute similarities using the argument sparse matrix directory")
		.create("sim");
		tosql  = OptionBuilder.withArgName("sql output file")
		.hasArg()
		.withDescription("sql output file")
		.create("tosql");
		tocsv  = OptionBuilder.withArgName("csv output file")
		.hasArg()
		.withDescription("csv output file")
		.create("tocsv");
		w1  = OptionBuilder.withArgName("word to compute similarities for")
		.hasArg()
		.withDescription("word to compute similarities for")
		.create("w1");
		w2  = OptionBuilder.withArgName("word to compute similarities for")
		.hasArg()
		.withDescription("word to compute similarities for")
		.create("w2");
		n  = OptionBuilder.withArgName("number of similar words to compute")
		.hasArg()
		.withDescription("number of similar words to compute")
		.create("n");
	}

}


