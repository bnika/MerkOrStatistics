package is.merkor.statistics.clustering;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import is.merkor.util.FileCommunicatorReading;
import is.merkor.util.FileCommunicatorWriting;

/**
 * SimilarityComputation provides methods to compute similarities between words according to
 * a List of <code>DataPoint</code>.
 * 
 * Usages:
 * 
 * 1) Given a list of words, to compute the n most similar words of each word and write to an 
 *    SQL insert statement file or a CSV file, call {@code similarityListsToSQL()} or {@code similarityListsToCSV()} respectively.
 *    
 * 2) To compute the similarity of two word strings, call {@code getSimilarity()}
 * 
 * 3) To get a map of n most similar words of a certain word, call {@code getMostSimilarWords()}
 * 
 * 4) To compute and write an n x n matrix of all datapoints, call {@code writeSimilarityMatrix()}
 * 
 * @author Anna B. Nikulásdóttir
 *
 */
public class SimilarityComputation {
	private ArrayList<DataPoint> datapoints;
	
	public SimilarityComputation (String directory) throws Exception {
		DataPoints dp = new DataPoints(directory);
		this.datapoints = dp.getDatapoints();
	}
	/**
	 * Creates a similarityComputation object from sparse matrix in {@code directory},
	 * with datapoints dimension {@code dimension}.
	 * @param directory
	 * @param dimension
	 */
	public SimilarityComputation (String directory, int dimension) throws Exception {
		DataPoints dp = new DataPoints(directory, dimension);
		this.datapoints = dp.getDatapoints();
	}
	public SimilarityComputation (ArrayList<DataPoint> datapoints) {
		this.datapoints = datapoints;
	}
	
	/**
	 * Computes {@code nrOfSimilar} most similar words of all words in {@code wordList} and 
	 * writes SQL insert statements to {@code filename}.
	 * 
	 * @param wordList
	 * @param filename
	 * @param nrOfSimilar
	 */
	public void similarityListsToSQL (List<String> wordList, String filename, int nrOfSimilar) {
		TreeMap<Double, String> similarWords;
		int counter = 0;
		for(String word : wordList) {
			similarWords = getMostSimilarWords(word, nrOfSimilar);
			if(!similarWords.isEmpty()) {
				FileCommunicatorWriting.writeListAppend(createSQLStatements(similarWords, word), filename);
			}
			if (counter % 1000 == 0)
				System.out.println(counter);
			counter++;
		}
	}
	private List<String> createSQLStatements (TreeMap<Double, String> map, String word) {
		List<String> statements = new ArrayList<String>();
		for (Double d : map.keySet()) {
			statements.add("INSERT INTO relationSimilarities (word_pair1, word_pair2, value) VALUES ('" + word + "', '" + map.get(d) + "', " + d + ");");
		}
		return statements;
	}
	/**
	 * Computes {@code nrOfSimilar} most similar words of all words in {@code wordList} and 
	 * writes tab separated lines (word - similar_word - similarity) to {@code filename}.
	 * 
	 * @param wordList
	 * @param filename
	 * @param nrOfSimilar
	 */
	public void similarityListsToCSV (List<String> wordList, String filename, int nrOfSimilar) {
		TreeMap<Double, String> similarWords;
		int counter = 0;
		for(String word : wordList) {
			similarWords = getMostSimilarWords(word, nrOfSimilar);
			if(!similarWords.isEmpty()) {
				FileCommunicatorWriting.writeListAppend(createCSVStatements(similarWords, word), filename);
			}
			if (counter % 1000 == 0)
				System.out.println(counter);
			counter++;
		}
	}
	private List<String> createCSVStatements (TreeMap<Double, String> map, String word) {
		List<String> statements = new ArrayList<String>();
		for (Double d : map.keySet()) {
			statements.add(word + "\t" + map.get(d) + "\t" + d);
		}
		return statements;
	}
	/**
	 * Computes similarity between words according to their representation
	 * in this objects List of <code>DataPoint</code>s, using the cosine similarity
	 * measure
	 * @param wrd1 the first word of the comparison
	 * @param wrd2 the second word of the comparison
	 * @return the similarity value between wrd1 and wrd2 according to the cosine similarity; 
	 * 0 if either wrd1 or wrd2 is not included in the List of DataPoints
	 */
	public double getSimilarity (String wrd1, String wrd2) {
        DataPoint dp1 = new DataPoint();
        DataPoint dp2 = new DataPoint();
        for(DataPoint dp : datapoints) {
            if (dp.name.equals(wrd1)) {
                dp1 = dp;
            }
            if (dp.name.equals(wrd2)) {
                dp2 = dp;
            }
         }
         if(dp1.name == null || dp2.name == null) 
             return 0;
         else
             return dp1.computeCosineSimilarityTo(dp2);
    }
	
	/**
	 * Computes similarity between wrd and other words in this objects List of <code>DataPoint</code>s.
	 * Returns a Map containing the top nrOfSimilarWords and their similarity value according to wrd.
	 * @param wrd the word to compare to other words in the DataPoint-list
	 * @param nrOfSimilarWords number of most similar words to return
	 * @return a List of most similar words, an empty List if wrd is not found in this objects List of <code>DataPoint</code>s.
	 */
	public TreeMap<Double, String> getMostSimilarWords (String word, int nrOfSimilarWords) {
		DataPoint dp1 = getDataPointMatching(word);
		if (null == dp1)
			return new TreeMap<Double, String>();

		return getSimilarWords(dp1, nrOfSimilarWords);
	}
	
	private DataPoint getDataPointMatching (String word) {
		DataPoint dp1 = null;
		for (DataPoint dp : datapoints) {
			if(dp.name.equals(word)) {
				dp1 = dp;
			}
		}
		return dp1;
	}
	private TreeMap<Double, String> getSimilarWords (DataPoint dp, int nrOfSimilarWords) {
		TreeMap<Double, String> similarWords = new TreeMap<Double, String>();
		double currentLowestValue = 0.0;
		System.out.println("computing similarities ...");
		for (DataPoint currDp : datapoints) {
			double value = dp.computeCosineSimilarityTo(currDp);
			if (value > 0.0) {
				if (similarWords.size() < nrOfSimilarWords) {
					similarWords.put(value, currDp.name);
					currentLowestValue = similarWords.firstKey();
				}
				else if (value > currentLowestValue) {
					similarWords.put(value, currDp.name);
					similarWords.remove(similarWords.firstKey());
					currentLowestValue = similarWords.firstKey();	
				}
			}
		}
		return similarWords;
	}
	/**
	 * Compute similarities between all datapoints in <datapoints> and write a similarity
	 * matrix to <filename>
	 * 
	 * @param filename the file to write the similarity matrix to
	 */
	public void writeSimilarityMatrix (String filename) {
		System.out.println("Size of datapoints: " + datapoints.size());
		double[][] matrix = new double[datapoints.size()][datapoints.size()];
		FileCommunicatorWriting.writeListNonAppend("simMatr.names", computeWithinMatrixSimilarity(matrix));
		matrix = fillZeroValues(matrix);
		writeMatrix(collectValues(matrix), filename);
	}
	
	private List<String> computeWithinMatrixSimilarity (double[][] matrix) {
		List<String> nameList = new ArrayList<String>();
		for (int i = 0; i < datapoints.size(); i++) {
			matrix[i][i] = 1;
			DataPoint refDatap = datapoints.get(i);
			System.out.println("Processing  " + refDatap.name + " (nr. " + i + ")");
			nameList.add(refDatap.name + "\n");
			for(int j = i + 1; j < datapoints.size(); j++) {
				matrix[i][j] = refDatap.computeCosineSimilarityTo(datapoints.get(j));
			}
		}
		return nameList;
	}
	
	private double[][] fillZeroValues(double[][] matrix) {
		for (int i = 0; i < datapoints.size(); i++) {
			for (int j = 0; j < datapoints.size(); j++) {
				if (0.0 == matrix[i][j])
					matrix[i][j] = matrix[j][i];
			}
		}
		return matrix;
	}
	/*
	 * collects all values from matrix, row by row, into a list
	 */
	private List<List<Double>> collectValues (double[][] matrix) {
		List<List<Double>> values = new ArrayList<List<Double>>();
		for(int i = 0; i < datapoints.size(); i++) {
			List<Double> row = new ArrayList<Double>();
			for(int j = 0; j < datapoints.size(); j++) {
				row.add(matrix[i][j]);
			}
			values.add(row);
		}
		return values;
	}
	private void writeMatrix (List<List<Double>> values, String filename) {
		try {
			BufferedWriter out = FileCommunicatorWriting.createWriter(filename, true);
			out.write(datapoints.size() + "\n\n");
			for (List<Double> list : values) {
				for (Double val : list) {
					out.write(val + "\t");
				}
				out.write("\n");
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main (String[] args) {
		//read words to compute similarities from file:
		//String filename = "/Users/anna/EclipseProjects/workspace/Data/sparseMatrix_ww5_large/words.txt";
		String filename = "sparseMatrix_patterns/words.txt";
		//DataPoints data = new DataPoints("/Users/anna/MERKOR/Data/sparseMatrix_ww3_large/", 5000);
		try {
			DataPoints data = new DataPoints("sparseMatrix_patterns/", 40);
			SimilarityComputation simComputation = new SimilarityComputation(data.getDatapoints());
			ArrayList<String> wordList = FileCommunicatorReading.getLinesFromFileAsStrings(filename);
		
			simComputation.similarityListsToSQL(wordList, "insertRelSim.sql", 15);
		
		} catch (Exception e) {
			
		}
		
		//write a complete similarity matrix
		//simComputation.writeSimilarityMatrix("simMatr_ww3.mat");
		//create top similarity lists and write sql-statements file
		
		//to get similarities for single words:
//		if(args.length < 1) {
//			System.out.println("Usage: SimilarityComputation <word_id>");
//		}
//		else if (args.length == 1){
//			String word = args[0];
//			SimilarityData data = new SimilarityData("../../Data/sparseMatrix_ww/");
//			SimilarityComputation simComputation = new SimilarityComputation(data.getDatapoints());
//		
//			TreeMap<Double, String> similarWords = simComputation.getMostSimilarWords(word, 10);
//			System.out.println(similarWords.toString());
//		}
//		else if (args.length == 2){
//			String word1 = args[0];
//			String word2 = args[1];
//			SimilarityData data = new SimilarityData("../../Data/sparseMatrix_ww/");
//			SimilarityComputation simComputation = new SimilarityComputation(data.getDatapoints());
//		
//			double similarity = simComputation.getSimilarity(word1, word2);
//			System.out.println(word1 + " similarity to " + word2 + ": " + similarity);
//		}
	}
	public DataPoint getDataPointFor(String s) {
		DataPoint datapoint = new DataPoint();
		for(DataPoint dp : datapoints) {
            if(dp.name.equals(s)) 
                return dp;
		}
		return datapoint;
	}
}

