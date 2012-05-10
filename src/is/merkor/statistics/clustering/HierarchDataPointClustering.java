package is.merkor.statistics.clustering;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import is.merkor.util.FileCommunicatorWriting;

import com.aliasi.cluster.Dendrogram;
import com.aliasi.cluster.HierarchicalClusterer;
import com.aliasi.cluster.CompleteLinkClusterer;
import com.aliasi.cluster.SingleLinkClusterer;

public class HierarchDataPointClustering {
	private Set<DataPoint> datapointSet;
	public HierarchDataPointClustering(ArrayList<DataPoint> datapoints) {
		convertListToSet(datapoints);
	}
	
	public void completeLinkCluster() {
		HierarchicalClusterer<DataPoint> cClusterer
			= new CompleteLinkClusterer<DataPoint>(DataPoint.COSINE_DISTANCE);
		System.out.println("creating dendrogram ... ");
		Dendrogram<DataPoint> completeLinkDendrogram
			= cClusterer.hierarchicalCluster(datapointSet);
		System.out.println("dendrogram created ... ");
		writeClustersToFile(completeLinkDendrogram);
//		int nr = 40;
//		writeClustersToFile(completeLinkDendrogram, nr);
	}
	public void singleLinkCluster() {
		HierarchicalClusterer<DataPoint> cClusterer
			= new SingleLinkClusterer<DataPoint>(DataPoint.COSINE_DISTANCE);
		Dendrogram<DataPoint> singleLinkDendrogram
			= cClusterer.hierarchicalCluster(datapointSet);
		System.out.println("dendrogram created ...");
		writeClustersToFile(singleLinkDendrogram);
//		int nr = 40;
//		writeClustersToFile(singleLinkDendrogram, nr);
		
	}
	
	private void convertListToSet(ArrayList<DataPoint> datapoints) {
		System.out.println("converting datapoints to set ...");
		datapointSet = new HashSet<DataPoint>();
		for(DataPoint dp : datapoints) {
			datapointSet.add(dp);
		}
	}
	private void writeClustersToFile(Dendrogram<DataPoint> dendrogram) {
		try {
			BufferedWriter out = FileCommunicatorWriting.createWriter("dendro_ww3Centres_singleLink.txt", false);
			for(int k = 1; k <= dendrogram.size(); ++k) {
			//for(int k = 1; k <= 50; ++k) {
				Set<Set<DataPoint>> clusterRes = dendrogram.partitionK(k);
				String[] clusters = clusterRes.toString().split("]");
				for(int i = 0; i < clusters.length; i++)
					out.write(k + " " + clusters[i] + "]\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void writeClustersToFile(Dendrogram<DataPoint> dendrogram, int nrOfClusters) {
		try {
			BufferedWriter out = FileCommunicatorWriting.createWriter("dendro.txt", false);
			Set<Set<DataPoint>> clusterRes = dendrogram.partitionK(nrOfClusters);
			out.write(nrOfClusters + "clusters: " + clusterRes + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

