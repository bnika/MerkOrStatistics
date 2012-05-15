package is.merkor.statistics.clustering;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Phase 3 of the Clustering-by-Committee algorithm (see {@code ClusteringByCommitte}).
 * 
 * Assign elements to clusters.
 * 
 * Pseudocode (Pantel & Lin, 2002:616):
 * 
 * let C be a list of clusters initially empty
 * let S be the top-200 similar clusters to e
 * 
 * while S is not empty:
 *   let c in S be the most similar cluster to e
 *   if the similarity(e, c) < threshold:
 *     exit the loop
 *   if c is not similar to any cluster in C:	// missing: assign c to C if element assigned - 
 *   											// and shouldn't the clusters in S already be different enough given phase 2?
 *     assign e to c
 *     remove from e its features that overlap with the features of c
 *   remove c from S
 *   
 *   
 * 
 * @author Anna B. Nikulasdottir
 *
 */
public class CBC_Phase3 {
	
	private List<DataPoint> datapoints;
	private List<Cluster> committees;
	
	private final double assignmentThreshold = 0.2;
	private final int nrOfTopSimilar = 3;
	
	public CBC_Phase3 (List<DataPoint> elements, List<Cluster> committees) {
		this.datapoints = elements;
		this.committees = committees;
	}
	public List<Cluster> assignElements () {
		System.out.println("Phase III assigning elements ...");
		for (DataPoint dp : datapoints) {
			List<Cluster> mostSimilar = getMostSimilarClusters (committees, dp);
			assignElementToTopSimilar(mostSimilar, dp);
		}
		
		return committees;
	}
	public List<Cluster> getMostSimilarClusters (List<Cluster> clusters, DataPoint element) {
		List<Cluster> mostSimilar = new ArrayList<Cluster>();
		TreeMap<Double, Cluster> topClusters = new TreeMap<Double, Cluster>(); 
		double minSim = 0.0;
		
		for (Cluster c : clusters) {
			double sim = c.getCenter().computeCosineSimilarityTo(element);
			if (sim > minSim) {
				if (topClusters.size() < nrOfTopSimilar) {
					topClusters.put(sim, c);
					minSim = topClusters.firstKey();
				}
				else if (sim > minSim) {
					topClusters.put(sim, c);
					topClusters.remove(topClusters.firstKey());
					minSim = topClusters.firstKey();
				}
			}
		}
		return mostSimilar;
		
	}
	public void assignElementToTopSimilar (List<Cluster> mostSimilar, DataPoint element) {
		//List<Cluster> clustersAssignedTo = new ArrayList<Cluster>();
		for (Cluster c : mostSimilar) {
			if (c.getCenter().computeCosineSimilarityTo(element) < assignmentThreshold)
				break;
			
			int ind = committees.indexOf(c);
			committees.get(ind).add(element);
			//clustersAssignedTo.add(c);
			element.removeCommonFeaturesWith(c.getCenter());
		}
		//return clustersAssignedTo;
	}
}
