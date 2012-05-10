package is.merkor.statistics.clustering;

import java.util.Map;

/**
 * A class that contains a list of the top related words of a lemma. The list is stored as a map
 * where similarity values serve as keys and related words as values.
 * From all similarity values in the map an average similarity value is computed at object creation.
 * 
 * @author Anna B. Nikulasdottir
 *
 */
public class CBC_TopSimilarities implements Comparable<CBC_TopSimilarities> {
	
	private String lemma;
	private Map<String, Double> similarities;
	private double avgSimilarity = 0.0;
	
	public CBC_TopSimilarities (String s, Map<String, Double> similarityMap) {
		lemma = s;
		similarities = similarityMap;
		computeAvgSimilarity();
	}
	private void computeAvgSimilarity() {
		double sum = 0.0;
		for (Double d : similarities.values()) {
			sum += d;
		}
		avgSimilarity = sum/similarities.size();
	}
	
	public String getLemma() {
		return lemma;
	}
	public Map<String, Double> getSimilarities() {
		return similarities;
	}
	public double getAvgSimilarity() {
		return avgSimilarity;
	}
	public boolean includesLemma (String lemma) {
		return similarities.containsKey(lemma);
	}
	public void removeLemma (String lemma) {
		similarities.remove(lemma);
	}
	/**
	 * Objects with higher avgSimilarity should be sorted before objects with lower
	 * avgSimilarity.
	 * @return 1 if this avgSimilarity is less than other.avgSimilarity, the result of 
	 * lemma.compareTo(other.lemma) if avgSimilarity is equal to other.avgSimilarity,
	 * -1 otherwise.
	 *
	 */
	@Override
	public int compareTo (CBC_TopSimilarities other) {
		
		if (avgSimilarity < other.getAvgSimilarity())
			return 1;
		else if (avgSimilarity == other.getAvgSimilarity())
			return lemma.compareTo(other.getLemma());
		else 
			return -1;
	}

}

