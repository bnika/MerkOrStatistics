package is.merkor.statistics.cooccurrence;

import is.merkor.util.MerkorCSVFile;

import java.util.ArrayList;

/**
 * CooccurrenceStatistics provides data structures and methods for the computation
 * of co-occurrence matrices.
 * Two files are needed to initialize the data structures <code>contentWords</code> and
 * <code>nouns</code>: Both files have to be .csv files with '\t' separator containing
 * words (content words / nouns to analyze) in the first column and a frequency number
 * according to the used corpus in the second column. Further, one has to decide how many rows from these
 * files should be used - these numbers build the indices for the <code>double[][] cooccurMatrix</code>
 * used to save co-occurrence counts from the analyzed data. Subclasses can either fill in the cooccurMatrix 
 * during corpus analysis (<code>WordWindowStatistics</code>) or use results from already performed 
 * corpus analysis (<code>GrammaticalFunctionsStatistics</code>).
 * 
 * Subclasses:
 * <li>WordWindowStatistics</li> <li>GrammaticalFunctionsStatistics</li>
 * 
 * @author Anna B. Nikulasdottir
 *
 */
public abstract class CooccurrenceStatistics {
	
	//The content words used to define common context of nouns
    protected ArrayList<WordStatistics> contentWords = new ArrayList<WordStatistics>();
    //The words to be compared with respect to common context
    protected ArrayList<WordStatistics> subjectWords = new ArrayList<WordStatistics>();
    //The weighted co-occurences of nouns with content words.
    //Columns = fContentWrdIndex, rows = fNounIndex
    protected double[][] cooccurrMatrix;
    protected SparseMatrix sparseCooccurrences;
    protected int contentWrdIndex = 5000;	//Size of the content word list, default value
    protected int subjectWrdIndex = 50000;	//Size of the nouns list, default value
    protected double corpusSize = 250000000; // default value
    
    /**
     * Creates a new CooccurrenceStatistics object.
     *
     * @param freqFile contains content words
     * @param nounsFile contains nouns
     * @param contentWrdIndex number of content words to be used
     * @param nounIndex number of nouns to be used
     * @param windowSize allowed number of words around a content word
     */
    public CooccurrenceStatistics(String freqFile, String nounsFile, int contWrdIndex, int nounIndex) {
        this.contentWrdIndex = contWrdIndex;
        this.subjectWrdIndex = nounIndex;
        this.cooccurrMatrix = new double[subjectWrdIndex][contentWrdIndex];
        initializeWordListFromFile(freqFile, contentWords, contentWrdIndex);
        initializeWordListFromFile(nounsFile, subjectWords, subjectWrdIndex);
	}
    public CooccurrenceStatistics(String freqFile, String nounsFile) {
        this.cooccurrMatrix = new double[subjectWrdIndex][contentWrdIndex];
        initializeWordListFromFile(freqFile, contentWords, contentWrdIndex);
        initializeWordListFromFile(nounsFile, subjectWords, subjectWrdIndex);
	}
    
    public abstract void processWord (String word);
    
    protected void initializeWordListFromFile(String filename, ArrayList<WordStatistics> wordList, int index) {
    	MerkorCSVFile csv = new MerkorCSVFile(filename, '\t');
    	int count = 0;
    	String word;
    	for (String[] line : csv) {
    		if (count >= index)
    			break;
    		word = line[0].replaceAll("\"", "");
    		int freq = parseNumber(line[1]);
    		wordList.add(new WordStatistics(word, freq));
    		count++;
    	}
    }
    
    protected int parseNumber (String number) {
    	try {
    		int nr = Integer.parseInt(number);
    		return nr;
    	} catch (NumberFormatException e) {
    		e.printStackTrace();
    		return 0;
    	}
    }
    //Computes weights for all entries in the cooccurence matrix (1 + log10(entry)),
    //or uses PPMI
    protected void computeCooccurMatrix() {
        double val;
        double weight;
        for(int row = 0; row < subjectWrdIndex; row++) {
            //System.out.println("computing cooccurmatr. row: " + row + "...");
            for(int col = 0; col < contentWrdIndex; col++) {
                val = cooccurrMatrix[row][col];
                //PPMI (Positive Pointwise Mutual Information)
                if (val > 0) {
                    //PPMI: I(c,t) = log2(p(c|t)/p(c)) = log2(p(c,t)/p(t)p(c)
                    double nFreq = subjectWords.get(row).getFrequency(); // t
                    double cFreq = contentWords.get(col).getFrequency(); // c
                    double counter = val/corpusSize; // p(c,t)
                    double denom = (nFreq/corpusSize) * (cFreq/corpusSize); //p(t)p(c)
                    //need log(val)/log(2) to get log2(val)):
                    weight = Math.log(counter/denom) / Math.log(2.0);
                    // PPMI means just use positive weights
                    if (weight < 0)
                        weight = 0;
                }

            // logarithmic weight - can be used in stead of PPMI
//	            if (val > 0) {
//	                weight = 1 + Math.log10(val);
//	            }
                else
                    weight = 0;
                cooccurrMatrix[row][col] = weight;
            }
        }
    }
    protected int getContentWordIndex(String wrd) {
        for(WordStatistics w : contentWords) {
            if (w.getWord().equals(wrd))
                return contentWords.indexOf(w);
        }
        return -1;
    }
//    public void writeSparseMatrix (String directory) {
//    	SparseMatrix sparseMatrix = new SparseMatrix(cooccurrMatrix);
//    	sparseMatrix.writeCooccurrenceMatrix(directory);
//    }
    public void writeSparseMatrix (String directory) {
    	SparseMatrix sparseMatrix = new SparseMatrix(cooccurrMatrix, true, directory);
    }
}

