package is.merkor.statistics.clustering;

import is.merkor.util.FileCommunicatorReading;
import is.merkor.util.MerkorCSVFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Phase I of the CBC clustering algorithm (see CBC_Phase2).
 * 
 * Collects similarity lists from a tab separated file.
 * @author Anna B. Nikulasdottir
 *
 */
public class CBC_Phase1 {

	private Map<String, Map<String, Double>> similarityLists;
	
	public CBC_Phase1 (String similarityFile) {
		similarityLists = new HashMap<String, Map<String, Double>>();
//		try {
//			BufferedReader in = FileCommunicatorReading.createReader(similarityFile);
//			String line = "";
//			String[] lineArr;
//			String currentLemma = "";
//			int counter = 0;
//			while ((line = in.readLine()) != null) {
//				counter++;
//				lineArr = line.split("\t");
//				if (lineArr.length < 3)
//					continue;
//				
//				if (lineArr[0].equals(currentLemma)) {
//					addToSimilarityLists(lineArr);
//				}
//				else {
//					Map<String, Double> relatedWords = new HashMap<String, Double>();
//					similarityLists.put(lineArr[0], relatedWords);
//					addToSimilarityLists(lineArr);
//					currentLemma = lineArr[0];
//				}
//				
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		MerkorCSVFile csv = new MerkorCSVFile(similarityFile, '\t');
		similarityLists = new HashMap<String, Map<String, Double>>();
		String currentLemma = "";
		for (String[] line : csv) {
			if (line.length < 3)
				continue;
			if (line[0].equals(currentLemma)) {
				addToSimilarityLists(line);
			}
			else {
				Map<String, Double> relatedWords = new HashMap<String, Double>();
				similarityLists.put(line[0], relatedWords);
				addToSimilarityLists(line);
				currentLemma = line[0];
			}
		}
	}
	
	public Map<String, Map<String, Double>> getSimilarityLists () {
		return similarityLists;
	}
	
	private void addToSimilarityLists (String[] line) {
		String relWrd = line[1];
		Double value = getDouble(line[2]);
		if (value != null) {
			similarityLists.get(line[0]).put(relWrd, value);
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
