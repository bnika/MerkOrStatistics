package is.merkor.statistics.clustering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * 
 * Phase II, Pseudocode (Pantel & Lin 2002:615):
 * 
 * INPUT: A list of elements E to be clustered, a similarity database S from Phase I, threshold th_1 and th_2.
 * 
 * STEP 1: For each e in E:
 *           Cluster the top similar elements of e from S using avg-link clustering
 *           For each cluster c discovered:
 *             Compute |c| x avgsim(c), where |c| is the number of elements in c and avgsim(c) is the average pairwise 
 *             similarity between elements in c.
 *           Store the highest-scoring cluster in a list L.
 * 
 * STEP 2: Store the clusters in L in descending order of their scores.
 * 
 * STEP 3: Let C be a list of committees, initially empty.
 *         For each cluster c in L in sorted order:
 *           Compute the centroid of c by averaging the frequency vectors of its elements and computing
 *           the mutual information vector of the centroid in the same way as we did for individual elements.
 *           If c's similarity to the centroid of each committee previously added to C is below a threshold th_1,
 *           add c to C.
 *           
 * STEP 4: If C is empty, we are done and return C.
 * 
 * STEP 5: For each e in E:
 *           If e's similarity to every committee in C is below threshold th_2, add e to a list of residues R.
 *           
 * STEP 6: If R is empty, we are done and return C.
 *         Otherwise:
 *           Return the union of C and the output of a recursive call to Phase II using the same input
 *           except replacing E with R.
 *           
 * OUTPUT: A list of committees.
 * 
 * @author Anna B. Nikulasdottir
 *
 */

public class CBC_Phase2 {
	
	// E - list of elements to be clustered
	private List<DataPoint> datapoints;
	// S - similarity lists
	private Map<String, Map<String, Double>> similarityLists;
	// increment at every new Cluster creation, use as id
	private int clusterCounter = 0; 
	// committee centers have to have distance below this threshold
	private final double committeeThreshold = 0.35;
	// elements not reaching this threshold for any committe are residues
	private final double residuesThreshold = 0.2;
	
	public CBC_Phase2 (List<DataPoint> elements, CBC_Phase1 phase1) {
		this.datapoints = elements;
		this.similarityLists = phase1.getSimilarityLists();
	}
	public List<DataPoint> getDatapoints () {
		return datapoints;
	}
	
	//STEP 1 & 2
	public List<Cluster> clusterElements (List<DataPoint> elements) {
		System.out.println("clustering " + elements.size() + " elements ...");
		List<Cluster> highestScoringClusters = new ArrayList<Cluster>();
		for (DataPoint dp : elements) {
			// the most similar words to each element are already
			// listed in similarityLists - fetch the list directly, if it exists
			if (similarityLists.containsKey(dp.getName())) {
				Map<String, Double> simMap = similarityLists.get(dp.getName());
				Cluster c = createClusterFromMap(simMap);
				c.computeCenter();
				c.computeAvgPairwiseSimilarity();
				highestScoringClusters.add(c);
			}
		}
		Collections.sort(highestScoringClusters);
		return highestScoringClusters;
	}
	
	// STEP 3
	public List<Cluster> computeCommittees (List<Cluster> clusters) {
		System.out.println("computing committees out of " + clusters.size() + " clusters ...");
		List<Cluster> committees = new ArrayList<Cluster>();
		List<DataPoint> committeeCenters = new ArrayList<DataPoint>();
		for (Cluster c : clusters) {
			DataPoint currentCenter = c.getCenter();
			if (committeeCenters.isEmpty()) {
				committeeCenters.add(currentCenter);
				committees.add(c);
			}
			else if (isBelowThreshold(currentCenter, committeeCenters)) {
				committeeCenters.add(currentCenter);
				committees.add(c);
			}
		}
		return committees;
	}
	/*
	 * Returns true if the similarity of center and each point in committeeCenters
	 * is below committeeThreshold, returns false at the moment when a higher similarity
	 * is computed.
	 */
	private boolean isBelowThreshold (DataPoint center, List<DataPoint> committeeCenters) {
		for (DataPoint cent : committeeCenters) {
			if (cent.computeCosineSimilarityTo(center) > committeeThreshold) {
				return false;
			}
		}
		return true;
	}
	
	// STEP 4
	// check if committees is empty, then stop
	
	// STEP 5
	// find residues
	public List<DataPoint> collectResidues (List<Cluster> committees, List<DataPoint> elements) {
		System.out.println("collecting residues out of " + elements.size() + " using " + committees.size() + " committees");
		List<DataPoint> residues = new ArrayList<DataPoint>();
		for (DataPoint elem : elements) {
			boolean thresholdReached = false;
			for (Cluster c : committees) {
				if (c.getCenter().computeCosineSimilarityTo(elem) > residuesThreshold) {
					thresholdReached = true;
					break;
				}
			}
			if (!thresholdReached)
				residues.add(elem);
		}
		return residues;
	}
	
	private Cluster createClusterFromMap (Map<String, Double> simMap) {
		Cluster c = new Cluster(++clusterCounter);
		for (DataPoint dp : datapoints) {
			if (simMap.containsKey(dp.getName()))
				c.add(dp);
		}
		return c;
	}
}
