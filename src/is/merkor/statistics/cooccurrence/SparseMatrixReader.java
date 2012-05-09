package is.merkor.statistics.cooccurrence;

import is.merkor.util.FileCommunicatorReading;

import java.util.ArrayList;

public class SparseMatrixReader {
	// lists to store the sparse matrix and the words list
	private ArrayList<Double> values;
	private ArrayList<Integer> columns;
	private ArrayList<Integer> indices;
	private ArrayList<String> words;
	
	//TODO: make a static getInstance factory method which returns a matrix!
	public SparseMatrixReader(String valuesFile, String columnsFile, String indicesFile, String wordsFile) {
		values = FileCommunicatorReading.getLinesFromFileAsDoubles(valuesFile);
    	columns = FileCommunicatorReading.getLinesFromFileAsIntegers(columnsFile);
    	indices = FileCommunicatorReading.getLinesFromFileAsIntegers(indicesFile);
    	words = FileCommunicatorReading.getLinesFromFileAsStrings(wordsFile);	 
	}
	
	public ArrayList<Double> getValues() {
		return values;
	}
	public ArrayList<Integer> getColumns() {
		return columns;
	}
	public ArrayList<Integer> getIndices() {
		return indices;
	}
	public ArrayList<String> getWords() {
		return words;
	}
}

