package cooccurrence;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import is.merkor.statistics.cooccurrence.SparseMatrix;

import org.junit.Before;
import org.junit.Test;

public class SparseMatrixTest {
	
	double[][] matrix;
	double[][] matrix_2;
	SparseMatrix sparseMatrix;
	SparseMatrix sparseMatrix_2;
	List<Double> testValues;
	List<Integer> testColumns;
	List<Integer> testIndices;

	@Before
	public void setUp() throws Exception {
		 matrix = new double[][]{{1,2,0,0},{0,3,9,0},{0,1,4,0}};
		 sparseMatrix = new SparseMatrix(matrix);
		 initTestValues();
		 initTestColumns();
		 initTestIndices();
		 
		 matrix_2 = new double[][] {{1,0,0,0},{0,3,10,0},{0,1,4,1}};
         sparseMatrix_2 = new SparseMatrix(matrix_2);

	}

	@Test
	public void testGetValues() {
		List<Double> values = sparseMatrix.getValues();
		//System.out.println(values.toString());
		assertTrue(testValues.equals(values));
	}

	@Test
	public void testGetColumns() {
		List<Integer> columns = sparseMatrix.getColumns();
		
		assertTrue(testColumns.equals(columns));
	}

	@Test
	public void testGetIndices() {
		List<Integer> indices = sparseMatrix.getIndices();
		
		assertTrue(testIndices.equals(indices));
	}

	@Test
	public void testComputeCosineSimilaritySame() {
		double sim = sparseMatrix.computeCosineSimilarity(sparseMatrix);
		System.out.println("sim: " + sim);
		assertTrue(sim > 0.99 && sim < 1.01);
	}
	@Test
	public void testComputeCosineSimilarityDiff() {
		double sim = sparseMatrix.computeCosineSimilarity(sparseMatrix_2);
		System.out.println("sim: " + sim);
		assertTrue(sim > 0.97 && sim < 0.98);
	}
	
	public void initTestValues() {
		testValues = new ArrayList<Double>();
		testValues.add(1.0);
		testValues.add(2.0);
		testValues.add(3.0);
		testValues.add(9.0);
		testValues.add(1.0);
		testValues.add(4.0);
		
	}
	public void initTestColumns() {
		testColumns = new ArrayList<Integer>();
		testColumns.add(0);
		testColumns.add(1);
		testColumns.add(1);
		testColumns.add(2);
		testColumns.add(1);
		testColumns.add(2);
		
	}
	public void initTestIndices() {
		testIndices = new ArrayList<Integer>();
		testIndices.add(0);
		testIndices.add(2);
		testIndices.add(4);
		testIndices.add(6);
		
	}

}
