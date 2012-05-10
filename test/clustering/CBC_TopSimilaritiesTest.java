package clustering;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import is.merkor.statistics.clustering.CBC_TopSimilarities;

import org.junit.Before;
import org.junit.Test;

public class CBC_TopSimilaritiesTest {
	CBC_TopSimilarities similarities_1;
	Map<String, Double> simMap_1;
	CBC_TopSimilarities similarities_2;
	Map<String, Double> simMap_2;
	String lemma;
	
	@Before
	public void setUp() throws Exception {
		initSimMap();
		lemma = "ráð_2885";
		similarities_1 = new CBC_TopSimilarities(lemma, simMap_1);
	}

	@Test
	public void testGetSimilarities() {
		assertTrue(similarities_1.getSimilarities().equals(simMap_1));
	}

	@Test
	public void testGetAvgSimilarity() {
		double sim = similarities_1.getAvgSimilarity();
		System.out.println(sim);
		assertTrue(sim > 0.5 && sim < 0.51);
	}

	@Test
	public void testIncludesLemma() {
		assertTrue(similarities_1.includesLemma("þjóð_10419"));
	}

	@Test
	public void testRemoveLemma() {
		similarities_1.removeLemma("bóndi_10162");
		assertFalse(similarities_1.includesLemma("bóndi_10162"));
	}

	@Test
	public void testCompareTo() {
		initSimMap_2();
		String lemma_2 = "orð_2635";
		similarities_2 = new CBC_TopSimilarities(lemma_2, simMap_2);
		assertTrue(similarities_2.compareTo(similarities_1) == -1);
	}
	
	public void initSimMap() {
		simMap_1 = new HashMap<String, Double>();
		simMap_1.put("borg_10441", 0.431);
		simMap_1.put("kostur_5669", 0.432);
		simMap_1.put("konungur_6720", 0.44);
		simMap_1.put("þjóð_10419", 0.445);
		simMap_1.put("áætlun_138223", 0.45);
		simMap_1.put("ríkisstjórn_120312", 0.451);
		simMap_1.put("bóndi_10162", 0.456);
		simMap_1.put("þing_4116", 0.459);
		simMap_1.put("fé_1013", 0.48);
		simMap_1.put("ráð_2885", 1.0);
	}
	public void initSimMap_2() {
		simMap_2 = new HashMap<String, Double>();
		simMap_2.put("auga_4315", 0.62);
		simMap_2.put("andi_8837", 0.621);
		simMap_2.put("heimur_6134", 0.622);
		simMap_2.put("guð_5633", 0.63);
		simMap_2.put("faðir_4386", 0.635);
		simMap_2.put("saga_16690", 0.64);
		simMap_2.put("hugur_5757", 0.645);
		simMap_2.put("trú_191190", 0.65);
		simMap_2.put("líf_2271", 0.69);
		simMap_2.put("orð_2635", 1.0);
	}
}
