package is.merkor.statistics.clustering;

import is.merkor.util.FileCommunicatorReading;
import is.merkor.util.FileCommunicatorWriting;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeMap;

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
	
	public List<Cluster> getCommittees (List<String> wordlist) {
		//System.out.println("CBC - phase I ...");
		//CBC_Phase1 phase1 = new CBC_Phase1(similarityFile);
		DataPoints data = getDataPoints(wordlist);
		if (null == data)
			return new ArrayList<Cluster>();
		System.out.println("CBC - phase I ...");
		CBC_Phase1 phase1 = new CBC_Phase1(similarityFile, data.getDatapoints());
		datapoints = data.getDatapoints();
		System.out.println("CBC - phase II ...");
		CBC_Phase2 phase2 = new CBC_Phase2(datapoints, phase1);
		List<DataPoint> elements = datapoints;
		// initial round:
		List<Cluster> sortedClusters = phase2.clusterElements(elements);
		List<Cluster> committees = phase2.computeCommittees(sortedClusters);
		for (Cluster c : committees)
			System.out.println(c.toString());
		List<Cluster> tmpCommittees;
		int count = 0;
		// until no more committees or residues are found:
		// while (true) {
		// use stop criteria
		while (elements.size() > 100 && count < 20) {
			count++;
			System.out.println("phase 2, round " + count);
			elements = phase2.collectResidues(committees, elements);
			if (elements.isEmpty())
				break;
			else {
				sortedClusters = phase2.clusterElements(elements);
				int beforeSize = committees.size();
				//committees.addAll(phase2.computeCommittees(sortedClusters));
				tmpCommittees = phase2.computeCommittees(sortedClusters);
				committees = addCommittees(committees, tmpCommittees);
				if (beforeSize == committees.size())
					break;
			}	
		}
		return committees;
	}
	/*
	 * adds all elements from newComm to oldComm, whose names are not found in oldComm
	 */
	private List<Cluster> addCommittees (List<Cluster> oldComm, List<Cluster> newComm) {
		
		for (Cluster nc : newComm) {
			boolean found = false;
			for (Cluster oc : oldComm) {
				if (nc.toString().equals(oc.toString())) {
					System.out.println(nc.toString() + " already a committee!");
					found = true;
				}
			}
			if (!found)
				oldComm.add(nc);
		}
		return oldComm;
	}
	
	public List<Cluster> cluster (List<Cluster> committees) {
		CBC_Phase3 phase3 = new CBC_Phase3(datapoints, committees);
		return phase3.assignElements();
	}
	private DataPoints getDataPoints (List<String> wordlist) {
		try {
			if (null != wordlist)
				return new DataPoints(matrixDirectory, wordlist, 5000);
			else
				return new DataPoints(matrixDirectory);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main (String[] args) {
		List<String> wordlist = FileCommunicatorReading.getWordsFromFileAsList("cbcTestwrd2.txt");
		ClusteringByCommittee clustering = new ClusteringByCommittee("release/sim2csv_top15.csv", "release/sparseMatrix_ww3_large/");
		List<Cluster> committees = clustering.getCommittees(null);
		int counter = 0;
		for (Cluster c : committees) {
			counter++;
			System.out.println("committe nr: " + counter);
			System.out.println(c);
		}
		List<Cluster> finalClusters = clustering.cluster(committees);
//		List<String> clustersOut = new ArrayList<String>();
//		for (Cluster c : finalClusters) {
//			System.out.println("CLUSTER " + c);
//			clustersOut.add("CLUSTER " + c + " ==================== ");
//			for (DataPoint dp : c.dataPoints) {
//				clustersOut.add(dp.getName());
//			}
//		}
//		FileCommunicatorWriting.writeListNonAppend("clusters_cbc.txt", clustersOut);
		clustering.writeClusters("clusters_cbc.txt", finalClusters);
	}
	public void writeClusters(String filename, List<Cluster> clusters) {

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
            for(Cluster c : clusters) {
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

}
