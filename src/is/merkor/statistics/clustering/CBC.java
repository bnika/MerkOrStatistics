package is.merkor.statistics.clustering;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NavigableSet;
import java.util.TreeMap;

import is.merkor.util.database.DBQueryHandler;

/**
 * Implementation of the Clustering-by-Committee algorithm.
 * See: Pantel & Lin (2002): Discovering Word Senses from Text. Proceedings of ACM Conference KDD-02, pp. 613-619.
 * 
 * Two Phases:
 * Phase I: Find top-similar elements
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
public class CBC {
	// E - list of elements to be clustered
	private ArrayList<DataPoint> datapoints;
	
	private ArrayList<CBC_TopSimilarities> avgSimilaritiesList;
	HashMap<String, HashMap<String, Double>> simLists;
	private CBC_committees committees;
	private ArrayList<Cluster> committeesList;
	double assignThreshold = 0.25;
	
	public CBC (ArrayList<DataPoint> datapoints) {
		this.datapoints = datapoints;
		ArrayList<String> lemmata = getDPLemmata();
		//DBQueryHandler queryHandler = new DBQueryHandler("BIN");
		DBQueryHandler queryHandler = new DBQueryHandler("local", "merkor");
		simLists = queryHandler.getTopSimilarities(lemmata);
		committees = new CBC_committees(datapoints, simLists);
	}
	private ArrayList<String> getDPLemmata() {
		ArrayList<String> lemmata = new ArrayList<String>();
 		for(DataPoint dp : datapoints)
 			lemmata.add(dp.name);
		return lemmata;
	}
	
	public void cluster() {
		ArrayList<DataPoint> elements = new ArrayList<DataPoint>();
		committeesList = new ArrayList<Cluster>();
		committeesList = committees.getCommittees(elements, committeesList);
		for (Cluster c : committeesList)
			System.out.println(c.toString());
		elements = committees.getResidues();
		int counter = 0;
		while(elements.size() > 100) {
			ArrayList<Cluster> tmp = committees.getCommittees(elements, committeesList);
			for(Cluster c : tmp) {
				boolean addCluster = true;
				for(Cluster com : committeesList) {
					if(com.getClusterName().equals(c.getClusterName()))
						addCluster = false;
					
				}
				if(addCluster)
					committeesList.add(c);
			}
			elements = committees.getResidues();
			for(Cluster c : committeesList)
				System.out.println(c.toString());
			System.out.println("Size of committeesList: " + committeesList.size() + "===========");
			counter++;
			if(counter > 20)
				break;
		}
		System.out.println("residues " + elements.size());
		for(Cluster c : committeesList)
			System.out.println(c.toString());
		
		assignElements();
		writeClusters("cbc_clusters_ww3.txt");
		//writeClustersForGraphView();
		
	}
	
	private void assignElements() {
		System.out.println("assigning elements ... ");
		TreeMap<Double, Cluster> mostSimClusters = new TreeMap<Double, Cluster>();
		for(DataPoint dp : datapoints) {
			mostSimClusters = getMostSimilarClusters(dp);
			assignElementToClusters(dp, mostSimClusters);
		}
	}
	private TreeMap<Double, Cluster> getMostSimilarClusters(DataPoint dp) {
		TreeMap<Double, Cluster> mostSimClusters = new TreeMap<Double, Cluster>();
		double currentLowestValue = 0.0;
		for(Cluster c : committeesList) {
			double simValue = c.getCenter().computeCosineSimilarityTo(dp);
			if(mostSimClusters.size() < 100) {
				mostSimClusters.put(simValue, c);
				currentLowestValue = mostSimClusters.firstKey();
			}
			else if(simValue > currentLowestValue) {
				mostSimClusters.put(simValue, c);
				mostSimClusters.remove(mostSimClusters.firstKey());
				currentLowestValue = mostSimClusters.firstKey();
			}
		}
		return mostSimClusters;
	}
	
	private void assignElementToClusters(DataPoint element, TreeMap<Double, Cluster> clusters) {
		ArrayList<Cluster> assignedClusters = new ArrayList<Cluster>();
		for(Double d : clusters.descendingKeySet()) {
			if(d < assignThreshold)
				return;
			else if (!isSimilar(clusters.get(d), assignedClusters)) {
				committeesList.get(committeesList.indexOf(clusters.get(d))).add(element);
				//clusters.remove(d);
				assignedClusters.add(clusters.get(d));
				//element.removeCommonFeaturesWith(clusters.get(d).getCenter());
			}
		}
	}
	private boolean isSimilar(Cluster cluster, ArrayList<Cluster> clusterList) {
		for(Cluster c : clusterList) {
			if(cluster.getCenter().computeCosineSimilarityTo(c.getCenter()) > assignThreshold)
				return true;
		}
		return false;
	}
	public void writeClusters(String filename) {

        /*
         * WRITE TEXTFILE WITH ALL CLUSTERS
         */
        try {
            BufferedWriter clustersOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF8"));
            String name = "";
            DataPoint centerDatapoint = null;
            int simInd = 0;
            double partitionQuality = 0.0;
            
            double simVal = 0.0;
            int counter = 1;
            //for (int i = 0; i < committeesList.size(); i++) {
            for(Cluster c : committeesList) {
                double quality = 0.0;
                double avQuality = 0.0;
                TreeMap<Double, String> clustermap = new TreeMap<Double, String>();
                if (c.getRealDataCenter() == null) {
                    name = "no data!";
                }
                else {
                    //centerDatapoint = c.getRealDataCenter();
                    //name = centerDatapoint.name;
                	centerDatapoint = c.getCenter();
                    name = c.getClusterName();
                    simInd = centerDatapoint.simIndex;
                }
                clustersOut.write("CLUSTER NR. " + counter + ":  " + name + " =============\n");
                for (int j = 0; j < c.dataPoints.size(); j++) {
                    DataPoint dp = c.dataPoints.get(j);
                    if (dp.name != null) {
                        simVal = centerDatapoint.computeCosineSimilarityTo(dp);
                        quality += simVal;
                        clustermap.put(simVal, dp.name);
                    }
                }
                avQuality = quality / (c.dataPoints.size());
                partitionQuality += avQuality;
                NavigableSet<Double> keys = clustermap.descendingKeySet();
                clustersOut.write("CLUSTER QUALITY: " + quality + " AVERAGE QUALITY: " + avQuality + "\n");
                clustersOut.write("Size of cluster: " + c.dataPoints.size() + "\n");
                for (Double d : keys) {
                    clustersOut.write(clustermap.get(d) + "     " + d + "\n");

                }
                counter++;
            }
            //clustersOut.write("QUALITY OF THE PARTITION: " + partitionQuality/c.size());
            clustersOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void writeClustersForGraphView() {
        String name = "";
        DataPoint centerDatapoint = null;
        int simInd = 0;
        double partitionQuality = 0.0;
        String directory = "/Users/anna/EclipseProjects/workspace/Data/graphData/";
        double simVal = 0.0;
        //for (int i = 0; i < clusters.size(); i++) {
        for(Cluster c : committeesList) {
            double quality = 0.0;
            double avQuality = 0.0;
            TreeMap<Double, String> clustermap = new TreeMap<Double, String>();
            if (c.getRealDataCenter() == null) {
                name = "no data!";
            }
            else {
                centerDatapoint = c.getRealDataCenter();
                //name = centerDatapoint.name;
                name = c.getClusterName();
                simInd = centerDatapoint.simIndex;
            }
            try {
                BufferedWriter clustersOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(directory + name + ".xml"), "UTF8"));
                clustersOut.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                clustersOut.write("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\">\n");
                clustersOut.write("<graph edgedefault=\"undirected\">\n");
                clustersOut.write("<key id=\"name\" for=\"node\" attr.name=\"name\" attr.type=\"string\"/>\n");
                clustersOut.write("<key id=\"similarity\" for=\"node\" attr.name=\"similarity\" attr.type=\"double\"/>\n");
                clustersOut.write("<node id=\"1\">\n");
                clustersOut.write("<data key=\"name\">" + name + "</data>\n");
                clustersOut.write("<data key=\"similarity\">1.0</data>\n");
                clustersOut.write("</node>\n");
                for (int j = 0; j < c.dataPoints.size(); j++) {
                    DataPoint dp = c.dataPoints.get(j);
                    if (dp.name != null) {
                        simVal = centerDatapoint.computeCosineSimilarityTo(dp);
                        quality += simVal;
                        clustermap.put(simVal, dp.name);
                    }
                }
                avQuality = quality / (c.dataPoints.size());
                partitionQuality += avQuality;
                NavigableSet<Double> keys = clustermap.descendingKeySet();
                int count = 2;
                for (Double d : keys) {
                    clustersOut.write("<node id=\"" + count + "\">\n");
                    clustersOut.write("<data key=\"name\">" + clustermap.get(d) + "</data>\n");
                    clustersOut.write("<data key=\"similarity\">" + d + "</data>\n");
                    clustersOut.write("</node>\n");
                    count++;
                }
                for (int k = 2; k < c.dataPoints.size() + 2; k++) {
                    clustersOut.write("<edge source=\"1\" target=\"" + k + "\"></edge>\n");
                }
                clustersOut.write("</graph>\n");
                clustersOut.write("</graphml>\n");
                clustersOut.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}

