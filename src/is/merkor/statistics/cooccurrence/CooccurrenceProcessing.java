package is.merkor.statistics.cooccurrence;

import is.merkor.util.FileCommunicatorReading;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StreamTokenizer;

public class CooccurrenceProcessing {
	private String freqFile = "cooccurData/contentWordList.csv"; //the most frequent contentwords
    private String objectWordsFile = "cooccurData/nounsListMinusTop100.csv"; //the nouns for which to compute cooccurrences
    private CooccurrenceStatistics statistics;
    // default values:
    
    /**
     * Create a statistics object using all default values.
     */
    public CooccurrenceProcessing () {
    	statistics = new WordWindowStatistics(freqFile, objectWordsFile);
    }
    /**
     * Create a statistics object using all default values but window size.
     */
    public CooccurrenceProcessing (int winSize) {
    	statistics = new WordWindowStatistics(freqFile, objectWordsFile, winSize);
    }
    /**
     * Create a statistics object with custom word files, using other default values
     */
    public CooccurrenceProcessing (String freqFile, String objFile) {
    	statistics = new WordWindowStatistics(freqFile, objFile);
    }
    /**
     * Create a statistics object setting custom word files and window size
     */
    public CooccurrenceProcessing (String freqFile, String objFile, int winSize) {
    	statistics = new WordWindowStatistics(freqFile, objFile, winSize);
    }
    /**
     * Create a statistics object setting all custom parameters (indices and window size)
     */
    public CooccurrenceProcessing (String freqFile, String objFile, int contWrdInd, int objWrdInd, int winSize) {
    	statistics = new WordWindowStatistics(freqFile, objFile, contWrdInd, objWrdInd, winSize);
    }
    
    public void computeCooccurrences (String input) {
    	String out = "sparseMatrix/";
    	File dir = new File(out);
    	dir.mkdir();
    	processDirectory(input);
    	statistics.computeCooccurMatrix();
    	statistics.writeSparseMatrix(out);
    }
    public void computeCooccurrences (String input, String outputDir) {
    	File dir = new File(outputDir);
    	dir.mkdir();
    	processDirectory(input);
    	statistics.computeCooccurMatrix();
    	statistics.writeSparseMatrix(outputDir);
    }
    
	/*
	 * Iterates over files in dataDirectory and computes
	 * word-window statistics for the content of each file.
	 * 
	 */
	private void processDirectory (String dataDirectory) {
		
    	File f = new File(dataDirectory);
    	if(f.isDirectory()) {
    		String[] dir = f.list();
    		String dataFile = "";
    		for (int i = 1; i < dir.length; i++) {
    			dataFile = dir[i];
    			processFile(dataDirectory + "/", dataFile, i);
    		}
    	}
    	else
    		processFile("", dataDirectory, 1);
    }
    private void processFile (String dataDirectory, String filename, int fileNr) {
        try {
            BufferedReader input = FileCommunicatorReading.createReader(dataDirectory + filename);
            System.out.println("processing file nr: " + fileNr + " (" + filename + ")... ");
            StreamTokenizer tokenizer = new StreamTokenizer(input);
            tokenizer.wordChars('_', '_');
			while(tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
				if(tokenizer.ttype == StreamTokenizer.TT_WORD)
					//System.out.println(tokenizer.sval);
					statistics.processWord(tokenizer.sval);
			}
			input.close();
        } catch (IOException e) {
            System.out.println("Couldn't open " + filename);
            System.out.println(e.getMessage());
        }
    }

//    public static void main(String[] args) {
//        //input files:
//        String dataDirectory = "lemmatized.txt";
//        String outputDir = "sparseMatrix_ww3_large/";
//        CooccurrenceProcessing coocc = new CooccurrenceProcessing();
//        coocc.computeCooccurrences(dataDirectory, outputDir);
//        
//    }

}
