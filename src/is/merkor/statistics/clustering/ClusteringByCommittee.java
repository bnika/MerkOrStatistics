package is.merkor.statistics.clustering;

import is.merkor.util.FileCommunicatorWriting;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the Clustering-by-Committee algorithm.
 * See: Pantel & Lin (2002): Discovering Word Senses from Text. Proceedings of ACM Conference KDD-02, pp. 613-619.
 * 
 * Three phases:
 * Phase I (CBC_Phase1): find top similar elements
 * Phase II (CBC_Phase2): find committees
 * Phase III (CBC_Phase3): assign elements
 * 
 * 
 * @author Anna B. Nikulasdottir
 *
 */
public class ClusteringByCommittee {
	
	private String similarityFile;
	private String matrixDirectory;
	private List<DataPoint> datapoints;
	
	public ClusteringByCommittee (String similarityFile, String matrixDir) {
		this.similarityFile = similarityFile;
		this.matrixDirectory = matrixDir;
	}
	
	public List<Cluster> getCommittees () {
		System.out.println("CBC - phase I ...");
		CBC_Phase1 phase1 = new CBC_Phase1(similarityFile);
		DataPoints data = getDataPoints();
		if (null == data)
			return new ArrayList<Cluster>();
		
		datapoints = data.getDatapoints();
		System.out.println("CBC - phase II ...");
		CBC_Phase2 phase2 = new CBC_Phase2(datapoints, phase1);
		List<DataPoint> elements = datapoints;
		// initial round:
		List<Cluster> sortedClusters = phase2.clusterElements(elements);
		List<Cluster> committees = phase2.computeCommittees(sortedClusters);
		
		int count = 0;
		// until no more committees or residues are found:
		while (true) {
			count++;
			System.out.println("phase 2, round " + count);
			elements = phase2.collectResidues(committees, elements);
			if (elements.isEmpty())
				break;
			else {
				sortedClusters = phase2.clusterElements(elements);
				int beforeSize = committees.size();
				committees.addAll(phase2.computeCommittees(sortedClusters));
				if (beforeSize == committees.size())
					break;
			}	
		}
		return committees;
	}
	
	public List<Cluster> cluster (List<Cluster> committees) {
		CBC_Phase3 phase3 = new CBC_Phase3(datapoints, committees);
		return phase3.assignElements();
	}
	private DataPoints getDataPoints () {
		try {
			return new DataPoints(matrixDirectory);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main (String[] args) {
		ClusteringByCommittee clustering = new ClusteringByCommittee("release/sim2csv.csv", "release/sparseMatrix_ww3_large/");
		List<Cluster> committees = clustering.getCommittees();
		int counter = 0;
		for (Cluster c : committees) {
			counter++;
			System.out.println("committe nr: " + counter);
			System.out.println(c);
		}
		List<Cluster> finalClusters = clustering.cluster(committees);
		List<String> clustersOut = new ArrayList<String>();
		for (Cluster c : finalClusters) {
			System.out.println("CLUSTER " + c);
			clustersOut.add("CLUSTER " + c + " ==================== ");
			for (DataPoint dp : c.dataPoints) {
				clustersOut.add(dp.getName());
			}
		}
		FileCommunicatorWriting.writeListNonAppend("clusters_cbc.txt", clustersOut);
	}
}
