package is.merkor.statistics.clustering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

import is.merkor.util.database.DBQueryHandler;

public class CBC_committees {
	private ArrayList<Cluster> committees; //CBC alg = 'C'
	private HashMap<String, HashMap<String, Double>> simLists;
	private ArrayList<CBC_TopSimilarities> avgSimilaritiesList;
	private ArrayList<Cluster> initialClusterList; // clusters built form the top avgSimilaritiesList, CBC alg = 'L'
	private ArrayList<DataPoint> datapoints;
	private ArrayList<DataPoint> elements; //CBC alg = 'E'
	private ArrayList<DataPoint> residues; //CBC alg = 'R'
	private double threshold_1 = 0.4;
	private double threshold_2 = 0.2;
	private int nrOfInitialClusters = 100;
	
	public CBC_committees(ArrayList<DataPoint> datapoints, HashMap<String, HashMap<String, Double>> simLists) {
		this.datapoints = datapoints;
		this.simLists = simLists;
	}
	public ArrayList<Cluster> getCommittees(ArrayList<DataPoint> elements, ArrayList<Cluster> completeCommittes) {
		
		//get lemmaMap from database
		initializeSimilaritiesList(elements);
		initializeInitialClusterList(nrOfInitialClusters);
		initializeCommittees();
		collectResidues(completeCommittes);
		
		return committees;
	}
	
	private void initializeSimilaritiesList(ArrayList<DataPoint> elements) {
		System.out.println("initializing similarities list for " + elements.size() + " elements ...");
		avgSimilaritiesList = new ArrayList<CBC_TopSimilarities>();
		//debug counter:
		int counter = 0;
		for(String s : simLists.keySet()) {
			//debug
//			if(counter > 1000)
//				break;
//			counter++; 
			//until here
			CBC_TopSimilarities sim = new CBC_TopSimilarities(s, simLists.get(s));
			//sim = cleanSimList(sim);
			if(elements.size() > 0) {
				for(DataPoint dp : elements) {
					if(dp.name.equals(s))
						avgSimilaritiesList.add(sim);
				}
			}
			else {
				avgSimilaritiesList.add(sim);
			}
		}
		Collections.sort(avgSimilaritiesList);
		System.out.println("length of simList: " + avgSimilaritiesList.size());
	}
	private CBC_TopSimilarities cleanSimList(CBC_TopSimilarities sim) {
		ArrayList<String> invalidLemmata = new ArrayList<String>();
		for(String s : sim.getSimilarities().keySet()) {
			if(!containsLemmaDatapoints(s))
				invalidLemmata.add(s);
		}
		for(String str : invalidLemmata)
			sim.removeLemma(str);
		return sim;
	}
	private boolean containsLemmaDatapoints(String lemma) {
		for(DataPoint dp : datapoints) {
    		if(dp.name.equals(lemma))
    			return true;
    	}
    	return false;
	}
	private void initializeInitialClusterList(int nrOfClusters) {
		System.out.println("initializing initial clusters list ...");
		initialClusterList = new ArrayList<Cluster>();
		Cluster cluster;
		for(int i = 0; i < nrOfClusters && i < avgSimilaritiesList.size(); i++) {
			cluster = buildClusterFor(avgSimilaritiesList.get(i), i);
			initialClusterList.add(cluster);
		}
	}
	
	private Cluster buildClusterFor(CBC_TopSimilarities sim, int id) {
		Cluster cluster = new Cluster(id);
		SimilarityComputation simComp = new SimilarityComputation(datapoints);
		for(String s : sim.getSimilarities().keySet()) {
			DataPoint dp = simComp.getDataPointFor(s);
			if(null == dp || null == dp.columns) {
				
			}
			else
				cluster.add(dp);
		}
		cluster.computeCenter();
		cluster.setName(cluster.getRealDataCenter().name);
		return cluster;
	}
	private void initializeCommittees() {
		System.out.println("initializing committees ...");
		committees = new ArrayList<Cluster>();
		for(Cluster clust : initialClusterList) {
			boolean addCluster = true;
			if(committees.isEmpty())
				committees.add(clust);
			else {
				for(Cluster comm : committees) {
					if(clust.getCenter().computeCosineSimilarityTo(comm.getCenter()) > threshold_1) {
						addCluster = false;
						break;
					}
				}
				if(addCluster)
					committees.add(clust);
			}
		}
	}
	private void collectResidues(ArrayList<Cluster> completeCommittees) {
	
		completeCommittees.addAll(committees);
		residues = new ArrayList<DataPoint>();
		for(CBC_TopSimilarities sim : avgSimilaritiesList) {
			//for(String s : sim.getSimilarities().keySet()) {
			String s = sim.getLemma();
				boolean valid = false;
				SimilarityComputation simComp = new SimilarityComputation(datapoints);
				DataPoint dp = simComp.getDataPointFor(s);
				if(dp == null || dp.columns == null)
					System.out.println("no datapoint found for " + s);
				else {
					for(Cluster c : completeCommittees) {
						if(c.getCenter().computeCosineSimilarityTo(dp) > threshold_2) {
							valid = true;
							break;
						}
					}
				}
				if(!valid) {
					if(dp != null)
						if(!residues.contains(dp))
							residues.add(dp);
				}
			//}
		}
	}
	public ArrayList<DataPoint> getResidues() {
		return residues;
	}
}
