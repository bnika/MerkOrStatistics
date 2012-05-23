package is.merkor.statistics.clustering;

import java.util.ArrayList;

import is.merkor.util.FileCommunicatorReading;

/**
 * Clustering provides methods to cluster data according to a List of <code>DataPoint</code>
 * @author Anna B. Nikulásdóttir
 *
 */
public class Clustering {
	/**
     * Clusters datapoints according to the k-means algorithm.
     * 
     * @param nrOfClusters number of clusters to build
     * @param datapoints a list of <code>DataPoint</code>s containing cooccurrence vectors
     */
    public void clusterKMeans(int nrOfClusters, ArrayList<DataPoint> datapoints) {
        if(null == datapoints) {
        	System.out.println("no datapoints to cluster!");
        	return;
        }
        KMeans kmeans = new KMeans();
        kmeans.dataPoints = datapoints;
        kmeans.generateRandomClusters(nrOfClusters, 0, datapoints.size());
        kmeans.process();
        kmeans.writeClusters("clusters_Kmeans_pattern_w_wl_k2.csv");
       // kmeans.writeClustersForGraphView();
	}
    
    /**
     * Clusters datapoints according to the Clustering by Committee algorithm
     * 
     * @param datapoints a list of <code>DataPoint</code>s containing cooccurrence vectors
     */
    public void clusterCBC(ArrayList<DataPoint> datapoints) {
    	if(null == datapoints) {
    		System.out.println("no datapoints to cluster!");
    		return;
    	}
    	CBC cbc = new CBC(datapoints);
    	cbc.cluster();
    }
    
    public void clusterHierarchical(ArrayList<DataPoint> datapoints) {
    	if(null == datapoints) {
    		System.out.println("no datapoints to cluster!");
    		return;
    	}
    	HierarchDataPointClustering clusterer = new HierarchDataPointClustering(datapoints);
    	clusterer.singleLinkCluster();
    	//clusterer.completeLinkCluster();
    }
    
    public static void main(String[] args) {
    	// has to be k+1! (check kMeans better, why ...)
    	int nrOfClusters = 2;
    	//cluster only words from a seperated word list:
    	//ArrayList<String> wordList = FileCommunicator.getLinesFromFileAsStrings("/Users/anna/MERKOR/Data/clusteringResults_cbc/cbc_clusters_ww3centres.txt");
    	ArrayList<String> wordList = FileCommunicatorReading.getLinesFromFileAsStrings("cbcTestwrd2.txt");
    	//DataPoints data = new DataPoints("/Users/anna/MERKOR/Data/sparseMatrix_ww3_large/", wordList, 5000);
    	//cluster everything from the input sparse matrix:
    	try {
    		DataPoints data = new DataPoints("release/sparseMatrix_ww3_large/");//, wordList, 5000);
    	//DataPoints data = new DataPoints("sparseMatrix_relatedWords/", 2000);
    	//DataPoints data = new DataPoints("/Users/anna/EclipseProjects/workspace/Data/testMatrix/", 15);
    	//ArrayList<Double[]> datapointVectors = data.getDatapointsAsVectors();
    		Clustering clustering = new Clustering();
    	//	clustering.clusterKMeans(nrOfClusters, data.getDatapoints());	
    	clustering.clusterCBC(data.getDatapoints());
    	//clustering.clusterHierarchical(data.getDatapoints());
    	//data.printColumnNumbers();
    	} catch (Exception e) {
    		
    	}
    }

}
