package is.merkor.statistics.clustering;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import is.merkor.statistics.cooccurrence.SparseMatrix;
import is.merkor.util.FileCommunicatorWriting;


/**
 * DataPoints stores a list of DataPoint objects. It needs files representing a sparse matrix and the corresponding list of words.
 * The class indcludes a List of <code>DataPoint</code> initialized from these files.  
 * SimilarityData can be used as input to a clustering algorithm or to compute distributional 
 * similarities between words.
 * 
 * @author Anna B. Nikulásdóttir
 *
 */
public class DataPoints {

	private SparseMatrix matrix;
	private String valuesFile = "values.txt";
	private String columnsFile = "columns.txt";
	private String indicesFile = "indices.txt";
	private String wordsFile = "words.txt";
	//if datapoints should only be initialized with certain words, they are kept here
	private ArrayList<String> wordList; 
	private ArrayList<DataPoint> datapoints;
	private int dimension; //dimension of the full vector of the DataPoint objects, i.e. with zero values
	
	private int maxOccurrences = 3000; //f. merkor sim comp: maxOcc = 3000
	private int minOccurrences = 10; //10; //f. merkor sim comp: minOcc = 10
	
	public DataPoints (String directory) throws Exception {
		matrix = new SparseMatrix(directory + valuesFile, directory + columnsFile, directory + indicesFile, directory + wordsFile);
	    datapoints = new ArrayList<DataPoint>();
	    if(!isValidMatrix()) {
        	printNonValidMessages();
        	throw new Exception();
        	//System.exit(-1);
        }     
        initializeDatapoints();
    }

	/**
	 * Initializes a List of <code>DataPoint</code> from files representing a sparse matrix
	 * and the corresponding list of words.
	 */
    public DataPoints (String directory, int dimension) throws Exception {
    	matrix = new SparseMatrix(directory + valuesFile, directory + columnsFile, directory + indicesFile, directory + wordsFile);
	    datapoints = new ArrayList<DataPoint>();
	    this.dimension = dimension;
        if(!isValidMatrix()) {
        	printNonValidMessages();
        	throw new Exception();
        	//System.exit(-1);
        }     
        initializeDatapoints();
    }
    
    /**
	 * Initializes a List of <code>DataPoint</code> from files representing a sparse matrix
	 * and the corresponding list of words, using only words from wordList
	 */
    public DataPoints (String directory, ArrayList<String> wordList, int dimension) throws Exception {
    	this.wordList = wordList;
    	matrix = new SparseMatrix(directory + valuesFile, directory + columnsFile, directory + indicesFile, directory + wordsFile);
	    datapoints = new ArrayList<DataPoint>();
	    this.dimension = dimension;
        if(!isValidMatrix()) {
        	printNonValidMessages();
        	throw new Exception();
        	//System.exit(-1);
        }     
        initializeDatapoints();
    }
        
    private boolean isValidMatrix() {
    	if (matrix.getValues().size() != matrix.getColumns().size() || matrix.getIndices().size() - 1 != matrix.getWords().size()) 
            return false;
    	
    	return true;
    }
    private void printNonValidMessages() {
    	System.out.println("SimilarityData.SimilarityData():");
    	System.out.println("initialized matrix is not valid - corrupt input data?");
    	System.out.println("(the line numbers of values and columns have to be equal and");
    	System.out.println("the line numbers of indices minus one have to be equal to line numbers of words)");
    }
    private void initializeDatapoints() {
    	System.out.println("initializing datapoints ...");
    	ArrayList<Double> tmpValues;
        ArrayList<Integer> tmpCols;
        int from = 0;
        int to = 0;
        String name;
        for (int i = 0; i < matrix.getWords().size(); i++) {
            from = to;
            to = matrix.getIndices().get(i + 1);
            if (to != Integer.MAX_VALUE) {
                tmpValues = new ArrayList<Double>(matrix.getValues().subList(from, to));
                tmpCols = new ArrayList<Integer>(matrix.getColumns().subList(from, to));
                double sum = 0.0;
                //deduce the nr of occurrences with log - not possible with PPMI
//		        if (tmpCols.size() < (minOccurrences + 1)) {
//		             for(Double d : tmpValues) {
//		                 if (d.equals(1.0))
//		                     sum += 1;
//		                 else {
//		                     sum += Math.pow(d - 1, 10);
//		                 }
//		             }
//		         }
//		         else
//	                sum = 19;
                
                if ((tmpCols.size() < maxOccurrences) && (tmpCols.size() > minOccurrences)) {
                    name = matrix.getWords().get(i);
                    if(null != wordList) {
                    	if(wordList.contains(name)) {
                    		DataPoint dp = new DataPoint(tmpValues, tmpCols, name, i, dimension);
                    		datapoints.add(dp);
                    	}
                    }
                    else if (isValid(name)){
                    	//for (Double val : tmpValues) {
                    		//if (val > 1.0) {
                    		//if (val > 0.0) {
                    			DataPoint dp = new DataPoint(tmpValues, tmpCols, name, i, dimension);
                    			datapoints.add(dp);
                    			//continue;
                    		//}
                    	//}
                    }
                }
            }
            else {
                to = from;
                //System.out.println(matrix.getWords().get(i));
            }
        }
    }
    
    private boolean isValid(String name) {
    	if(name.startsWith("voði_"))
    		return false;
    	if(name.startsWith("svaki_"))
    		return false;
    	if(name.startsWith("rosi_"))
    		return false;
    	
    	return true;
    }
    public ArrayList<DataPoint> getDatapoints() {
    	return datapoints;
    }
    public boolean containsLemma(String lemma) {
    	for(DataPoint dp : datapoints) {
    		if(dp.name.equals(lemma))
    			return true;
    	}
    	return false;
    }
    public ArrayList<Double[]> getDatapointsAsVectors() {
    	ArrayList<Double[]> vectors = new ArrayList<Double[]>();
    	for(DataPoint dp : datapoints) {
    		vectors.add(dp.toArray());
    	}
    	return vectors;
    }
    public void printColumnNumbers() {
    	try {
    		BufferedWriter out = FileCommunicatorWriting.createWriter("columnNumbers.csv", false);
    		for(DataPoint dp : datapoints) {
    			out.write(dp.name + "\t" + dp.columns.size() + "\n");
    		}
    		out.close();
    		
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
}
