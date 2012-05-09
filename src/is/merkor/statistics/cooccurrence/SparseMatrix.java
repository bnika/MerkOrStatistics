package is.merkor.statistics.cooccurrence;

import is.merkor.util.FileCommunicatorWriting;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SparseMatrix represents the Yale format of sparse matrices.
 * The representation bases on three indices to keep track of non-zero elements of the matrix.
 * 
 * A matrix:
 * 
 * [ 1 2 0 0 ]
 * [ 0 3 9 0 ]
 * [ 0 1 4 0 ]
 * 
 * Is represented as (() used to point out numbers from the example below):
 * 
 * values (A): 			 [ 1 2 (3) 9 1 4 ]
 * valueIndices (IA): 	 [ 0 (2) 4 6 ]
 * columnPositions (JA): [ 0 1 (1) 2 1 2 ]
 * 
 * A, non-zero entries of the matrix in left-to-right top-to-bottom order (SparseMatrix.values)
 * IA, has length m + 1 (nr of rows + 1), IA[i] contains the index in A (values) of the first nonzero element of row[i] (SparseMatrix.valueIndices).
 * JA, has the same length as A and contains the column index of each element of A.
 * 
 * Example: to find the position of '3' in the second row in the matrix, we have its position in values at index 2, the second row is represented
 * as valueIndices[1] = 2 = index of '3' in values, and finally the column is represented by columnPositions[2] = 1.
 * That is, '3' is found in the second row at position 1 in the original matrix.
 *  
 * Computes or reads in a sparse matrix.
 * Performs calculations on sparse matrices.
 * @author Anna Nikulasdottir
 */
public class SparseMatrix {
    private List<Double> values; 	//non-zero values
    //valueIndices[i] = index of the first non-zero element of row i in values
    private List<Integer> valueIndices; 	
    //columnPositions[i] = column position of the non-zero element values[i]
    private List<Integer> columnPositions; 
    //a list of words corresponding to valueIndices
    private List<String> words;

    public SparseMatrix() {
        values = new ArrayList<Double>();
        valueIndices = new ArrayList<Integer>();
        columnPositions = new ArrayList<Integer>();
    }

    public SparseMatrix (ArrayList<Double> values, ArrayList<Integer> columns) {
        this.values = values;
        this.columnPositions = columns;
    }
    /**
     * Initializes a sparse matrix representation from files containing
     * sparse matrix arrays
     */
    public SparseMatrix (String valFile, String indFile, String colFile, String wrdFile) {
    	SparseMatrixReader reader = new SparseMatrixReader(valFile, indFile, colFile, wrdFile);
    	values = reader.getValues();
    	valueIndices = reader.getIndices();
    	columnPositions = reader.getColumns();
    	words = reader.getWords();
    }
    /**
     * Computes a new sparse matrix representation from a file containing a
     * sparse matrix
     * @param filename
     */
    public SparseMatrix(String filename) {

    }
    public List<Double> getValues() {
    	return values;
    }
    public List<Integer> getColumns() {
    	return columnPositions;
    }
    public List<Integer> getIndices() {
    	return valueIndices;
    }
    public List<String> getWords() {
    	return words;
    }
    /**
     * Computes a new sparse matrix representation from a two dim. array
     */
    public SparseMatrix (double[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        boolean firstValue;
        boolean allZeros;

        values = new ArrayList<Double>();
        valueIndices = new ArrayList<Integer>();
        columnPositions = new ArrayList<Integer>();

        for (int i = 0; i < rows; i++) {
            firstValue = true;
            allZeros = true;
            for (int j = 0; j < cols; j++) {
                if(matrix[i][j] > 0.0) {
                    values.add(matrix[i][j]);
                    columnPositions.add(j);
                    if(firstValue) {
                        valueIndices.add(values.size() - 1);
                        firstValue = false;
                    }
                    allZeros = false;
                }
                else if (j == (cols - 1) && allZeros) {
                    valueIndices.add(Integer.MAX_VALUE);
                }
            }
        }
	    valueIndices.add(values.size());
	}
    /**
     * Write the matrix directly into files - useful for large matrices
     * that would otherwise cause OutOfMemoryError (e.g. 50000 x 5000).
     * @param matrix
     * @param write
     */
    public SparseMatrix (double[][] matrix, boolean write, String outputDir) {
    	
    	int rows = matrix.length;
        int cols = matrix[0].length;
        //System.out.println("rows: " + rows);
        //System.out.println("cols: " + cols);
        boolean firstValue;
        boolean allZeros;
        int valuesSize = 0;
        try {
	        BufferedWriter fValues = FileCommunicatorWriting.createWriter(outputDir + "values.txt", true);
	        BufferedWriter fValueIndices = FileCommunicatorWriting.createWriter(outputDir + "indices.txt", true);
	        BufferedWriter fColumnPositions = FileCommunicatorWriting.createWriter(outputDir + "columns.txt", true);
	        System.out.println("writing matrix ...");
	        for (int i = 0; i < rows; i++) {
	            firstValue = true;
	            allZeros = true;
	            for (int j = 0; j < cols; j++) {
	                if(matrix[i][j] > 0.0) {
	                	Double val = matrix[i][j];
	                    fValues.write(val.toString());
	                    fValues.write("\n");
	                    valuesSize++;
	                    Integer col = j;
	                    fColumnPositions.write(col.toString());
	                    fColumnPositions.write("\n");
	                    if(firstValue) {
	                    	Integer valS = valuesSize - 1;
	                        fValueIndices.write(valS.toString());
	                        fValueIndices.write("\n");
	                        firstValue = false;
	                    }
	                    allZeros = false;
	                }
	                else if (j == (cols - 1) && allZeros) {
	                	Integer maxVal = Integer.MAX_VALUE;
	                    fValueIndices.write(maxVal.toString());
	                    fValueIndices.write("\n");
	                }
	            }
	        }
	        Integer valS = valuesSize;
		    fValueIndices.write(valS.toString());
		    fValueIndices.write("\n");
		    fValues.close();
		    fValueIndices.close();
		    fColumnPositions.close();
        } catch (IOException e) {
        	e.printStackTrace();
        }
    }
    
    /**
     * Compute the cosine similarity between two sparse matrices.
     * One dimensional vectors are simulated by comparing positions in columnPositions list according
     * to sublists created by iterating over the valueIndices lists,
     * and for every identical position-value pair, the values at the right index in the values lists
     * are computed.
     * 
     * Cosine similarity between two vectors v1 and v2 (v1 X v2 = dot product of v1 and v2):
     * 
     *     (v1 X v2) / (L2norm_v1 * L2norm_v2)
     * 
     * @param matrixToCompare
     * @return cosine similarity
     */
    public double computeCosineSimilarity (SparseMatrix matrixToCompare) {
        
        double counter = 0;
        double value1;
        double value2;
        // compute dot product:
        // for each index in indices, get the index values for that index and the next one
        // to get the sublist (= row) in the value lists to compare. If the corresponding columnPosition lists
        // have identical indices, compute the values.
        for (int i = 0; i < valueIndices.size() - 1; i++) {
        	// which sublists form each value list should be computed?
        	int fromIndex1 = valueIndices.get(i);
        	int toIndex1 = valueIndices.get(i + 1) - 1;
        	int fromIndex2 = matrixToCompare.valueIndices.get(i);
        	int toIndex2 = matrixToCompare.valueIndices.get(i + 1) - 1;
        	
        	// compute the values and columnPos sublists corresponding to one row in the matrix
        	int ind1 = fromIndex1;
        	int ind2 = fromIndex2;
        	while (ind1 <= toIndex1 && ind2 <= toIndex2) {
        		if(columnPositions.get(ind1) == matrixToCompare.columnPositions.get(ind2)) {
        			value1 = values.get(ind1);
        			value2 = matrixToCompare.values.get(ind2);
        			counter += value1 * value2;
        		}
        		ind1++;
        		ind2++;
        	}
        }
        
        double L2norm_1 = computeL2norm(this);
        double L2norm_2 = computeL2norm(matrixToCompare);
        
        double denom = L2norm_1 * L2norm_2;
        double similarity = 0.0D;
        if (denom > 0) {
            similarity = counter/denom;
        }

        return similarity;
    }

    private double computeL2norm (SparseMatrix matrix) {
    	//L2-norm: a vector norm for a complex vector
        double val;
        double sumSqrt = 0.0D;
        for(int i = 0; i < matrix.columnPositions.size(); i++) {
            val = matrix.values.get(i);
            sumSqrt += Math.pow(val, 2);
        }
        return Math.sqrt(sumSqrt);
    }
    
    public void writeCooccurrenceMatrix (String directory) {
    	System.out.println("writing cooccurrence matrix ...");

        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(directory + "values.txt")));
            for (Double d : values) {
                out.write(d.toString());
                out.write("\n");
            }
            out.close();
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(directory + "indices.txt")));
            for (Integer ind : valueIndices) {
                out.write(ind.toString());
                out.write("\n");
            }
            out.close();
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(directory + "columns.txt")));
            for (Integer ind : columnPositions) {
                out.write(ind.toString());
                out.write("\n");
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

