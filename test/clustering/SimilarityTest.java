package clustering;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import is.merkor.statistics.clustering.DataPoint;
import is.merkor.statistics.clustering.SimilarityComputation;

import org.junit.Before;
import org.junit.Test;

/**
 * This test class uses original MerkOr data - takes too long to run
 * at each build.
 * Last successful test run: 10.05.2012
 * 
 * TODO: create a small sparse matrix to test on.
 * @author Anna B. Nikulasdottir
 *
 */
public class SimilarityTest {
	
	SimilarityComputation simComp;
	List<String> wordList;

//	@Before
//	public void setUp() throws Exception {
//		try {
//			simComp = new SimilarityComputation("sparseMatrix_ww3_large/");
//		} catch (Exception e) {
//			
//		}
//		
//		initWordList();
//	}

//	@Test
//	public void testSimilarityListsToSQL() {
//		simComp.similarityListsToSQL(wordList, "similar2sqlTest.sql", 10);
//		File f = new File("similar2sqlTest.sql");
//		assertTrue(f.exists());
//	}

//	@Test
//	public void testSimilarityListsToCSV() {
//		simComp.similarityListsToCSV(wordList, "similar2csvTest.csv", 10);
//		File f = new File("similar2csvTest.csv");
//		assertTrue(f.exists());
//	}

//	@Test
//	public void testGetSimilarity() {
//		double similarity = simComp.getSimilarity(wordList.get(1), wordList.get(1));
//		assertTrue(similarity > 0.99 && similarity < 1.01);
//		
//		similarity = simComp.getSimilarity(wordList.get(0), wordList.get(1));
//		assertTrue(similarity > 0.38 && similarity < 0.39);
//	}
//
//	@Test
//	public void testGetMostSimilarWords() {
//		Map<Double, String> map = simComp.getMostSimilarWords(wordList.get(0), 2);
//		assertTrue(map.values().contains("líf_2271"));
//		assertTrue(map.values().contains("heimur_6134"));
//	}
//
//	@Test
//	public void testGetDataPointFor() {
//		DataPoint dp = simComp.getDataPointFor("bók_11100");
//		assertTrue(dp.getName().equals("bók_11100"));
//	}
//	
//	public void initWordList() {
//		wordList = new ArrayList<String>();
//		wordList.add("líf_2271");
//		wordList.add("ráð_2885");
//		wordList.add("bók_11100");
//		wordList.add("lag_2208");
//		wordList.add("mokveiði_467935");
//		wordList.add("mógröf_123651");
//		wordList.add("munnvatnskirtill_76806");
//		wordList.add("hrognafjöldi_105727");
//		wordList.add("hrossarétt_119751");
//		wordList.add("hrossbein_19117");
//	}

}
