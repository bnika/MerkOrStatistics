package is.merkor.statistics.clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import is.merkor.util.MerkorCSVFile;

/**
 * Phase I of the CBC clustering algorithm (see CBC_Phase2).
 * 
 * Collects similarity lists from a tab separated file.
 * Only uses those lemmata given in a list of datapoints to cluster.
 * 
 * @author Anna B. Nikulasdottir
 *
 */
public class CBC_Phase1 {

	private MerkorCSVFile similarityData;
	private Map<String, Map<String, Double>> similarityLists;
	private List<String> lemmata;
	
	public CBC_Phase1 (String similarityFile, List<DataPoint> datapoints) {
		this(similarityFile);
		lemmata = extractNames(datapoints);
		similarityLists = getSimilaritiesForLemmata (lemmata);
	}
	
	public CBC_Phase1 (String similarityFile) {
		this.similarityData = new MerkorCSVFile(similarityFile, '\t');
	}
	
	/**
	 * Returns a map of similarity maps for every lemma in {@code lemmata}.
	 * 
	 * @param lemmata
	 * @return
	 */
	public Map<String, Map<String, Double>> getSimilaritiesForLemmata (List<String> lemmata) {
		Map<String, Map<String, Double>> simMaps = new HashMap<String, Map<String, Double>>();
		String currentLemma = "";
		for (String[] line : similarityData) {
			if (line.length < 3)
				continue;
			if (line[0].equals(currentLemma)) {
				addToMap(simMaps, line);
			}
			else if (lemmata.contains(line[0])) {
				Map<String, Double> relatedWords = new HashMap<String, Double>();
				simMaps.put(line[0], relatedWords);
				addToMap(simMaps, line);
				currentLemma = line[0];
			}
		}
		return simMaps;
	}
	
	public Map<String, Map<String, Double>> getSimilarityLists () {
		return similarityLists;
	}
	
	private List<String> extractNames (List<DataPoint> datapoints) {
		List<String> names = new ArrayList<String>();
		for (DataPoint dp : datapoints) {
			names.add(dp.getName());
		}
		return names;
	}
	
	private void addToMap (Map<String, Map<String, Double>> map, String[] line) {
		Double value = getDouble(line[2]);
		if (value != null) {
			map.get(line[0]).put(line[1], value);
		}
	}
	
	private Double getDouble (String str) {
		try {
			return Double.parseDouble(str);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return null;
	}
}
