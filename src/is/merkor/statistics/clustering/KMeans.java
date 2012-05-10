package is.merkor.statistics.clustering;

import java.util.ArrayList;
import java.util.NavigableSet;
import java.util.Random;
import java.util.TreeMap;
import java.io.*;

import is.merkor.util.FileCommunicatorWriting;

public class KMeans {
	int dimensions;
    //Cluster[] clusters;
    ArrayList<Cluster> clusters;
    ArrayList<DataPoint> dataPoints;
    ArrayList<Integer> checkedIndices;
    static Random randomizer = new Random();
    int realRun = 0;

    /**
     *
     * @param clusterCount
     * @param start
     * @param end
     */
    public void generateRandomClusters(int clusterCount, int start, int end) {
        clusters = new ArrayList<Cluster>();
        System.out.println("end: = " + end);
        int index = randomizer.nextInt(end);
        Cluster cluster = new Cluster(0);
        cluster.add(dataPoints.get(index));
        cluster.centerDataPoint = dataPoints.get(index);
        //get a certain starting datapoint:
//        for(DataPoint dp : dataPoints) {
//            if(dp.name.equals("6137")) {
//                cluster.add(dp);
//                cluster.centerDataPoint = dp;
//                index = dataPoints.indexOf(dp);
//                break;
//            }
//        }
        cluster.setId(0);
        clusters.add(cluster);
        checkedIndices = new ArrayList<Integer>();
        checkedIndices.add(index);
        DataPoint centerPoint;
        for (int i = 1; i < clusterCount; i++) {
            cluster = new Cluster(i);
            centerPoint = getMinSimilarityPoint();
            cluster.add(centerPoint);
            cluster.centerDataPoint = centerPoint;
//            index = randomizer.nextInt(end);
//            cluster = new Cluster(dataPoints.get(index));
            //cluster.setId(i);
            clusters.add(cluster);
            System.out.println("added cluster " + i);
        }
//        clusters = new Cluster[clusterCount];
//        for (int i = 0; i < clusterCount; i++) {
//            int index = randomizer.nextInt(end);
////            double[] values = new double[dimensions];
////            for (int j = 0; j < values.length; j++) {
////                values[j] = start + randomizer.nextInt(end - start);
////
////            }
//            Cluster cluster = new Cluster(dataPoints[index]);
//            cluster.setId(i);
//            cluster.setCenterDataPoint(dataPoints[index]);
//            clusters[i] = cluster;
//        }

        for (Cluster c : clusters) {
            System.out.println(c.dataPoints.get(0).name);
        }

        System.out.println("Clusters found!");
       // clusters.remove(0);
    }
    /**
     * ABN
     * @param dp
     * @param centres
     * @return
     */
    private DataPoint getMinSimilarityPoint() {

        double min = Double.MAX_VALUE;
        int simIndex = 0;
        

        for(int i = 0; i < dataPoints.size(); i++) {
            //datamatrix = new SparseMatrix(dataPoints.get(i).values, dataPoints.get(i).columns);
            double sum = 0.0;
            for (Integer index : checkedIndices) {
                //centermatrix = new SparseMatrix(dataPoints.get(index).values, dataPoints.get(index).columns);
                sum += dataPoints.get(index).computeCosineSimilarityTo(dataPoints.get(i));
            }
            if (sum < min) {
                //if (isValidCenter(dataPoints.get(i))) {
                    if (!checkedIndices.contains(i)) {
                        min = sum;
                        simIndex = i;
                    }
               // }
            }
        }

        checkedIndices.add(simIndex);
        return dataPoints.get(simIndex);
    }

    private boolean isValidCenter(DataPoint datapoint) {
        int counter = 0;
        double simVal = 0.0;
        //for (DataPoint dp : dataPoints) {
        for (int i = 0; i < dataPoints.size(); i++) {
            if(!checkedIndices.contains(i)) {
                simVal = datapoint.computeCosineSimilarityTo(dataPoints.get(i));
                if (simVal > 0.4) {
                    counter++;
                    if (counter > 9)
                        return true;
                }
            }
        }
        checkedIndices.add(dataPoints.indexOf(datapoint));
        return false;
    }

    /**
     * 
     * @param countOfDataPointsToBeGenerated
     * @param start
     * @param end
     */
//    public void generateRandomDataPoints(int countOfDataPointsToBeGenerated, int start, int end) {
//        DataPoint[] dataPoints = new DataPoint[countOfDataPointsToBeGenerated];
//        for (int i = 0; i < dataPoints.length; i++) {
//            double[] values = new double[dimensions];
//            for (int j = 0; j < dimensions; j++) {
//                values[j] = start + randomizer.nextInt(end - start);
//            }
//            dataPoints[i] = new DataPoint(values);
//        }
//        this.dataPoints = dataPoints;
//    }

    /**
     *
     * @param dataPoints
     */
    public void setData(ArrayList<DataPoint> dataPoints) {
        this.dataPoints = dataPoints;
    }

    /**
     *
     * 
     */
    public void process() {
        int countOfSwapsOld = -1;
        int countOfCurrentSwaps = 401;
        //int countOfCurrentSwaps = 251;
        int run = 0;
        

        //while (countOfSwapsOld != countOfCurrentSwaps) {
        while (run != 10 && countOfCurrentSwaps > 400) {
//            if (run > 2) {
//                replaceSmallClusters();
//            }
//            if (run > 2) {
//                splitBigClusters();
//                run = 1;
//            }
            countOfSwapsOld = countOfCurrentSwaps;
            countOfCurrentSwaps = 0;

            System.out.println("Run: " + (run++));
            System.out.println("RealRun: " + (realRun++));

            for (int j = 0; j < dataPoints.size(); j++) {
                DataPoint currentDataPoint = dataPoints.get(j);

                Cluster nearestCluster = null;
                double currentMinimumDistance = 0.0;//Double.MAX_VALUE;
                for (int i = 0; i < clusters.size(); i++) {
                    Cluster currentCluster = clusters.get(i);
                    double distanceToCluster;
//                    if (null != presenter) {
//                        presenter.render(currentCluster);
//                    }                    
                    
//                    if (currentCluster.needsNewMean)
                        distanceToCluster = currentCluster.getCenter().computeCosineSimilarityTo(currentDataPoint);
//                    else
//                        distanceToCluster = currentCluster.centerDataPoint.computeCosineSimilarityTo(currentDataPoint);

                    //if (distanceToCluster < currentMinimumDistance) {
                    if (distanceToCluster >= currentMinimumDistance) {
                        currentMinimumDistance = distanceToCluster;
                        nearestCluster = currentCluster;
                    }

                }
                
                

                if (nearestCluster != currentDataPoint.getCluster() && null != nearestCluster) {
                    Cluster.swap(currentDataPoint.getCluster(), nearestCluster, currentDataPoint);
                    countOfCurrentSwaps++;
                    System.out.println("swapping nr " + countOfCurrentSwaps);
                }

//                if (null != presenter) {
//                    presenter.render(currentDataPoint);
//                }

                // System.out.println("##########################");
            }

            // try {
            // TimeUnit.SECONDS.sleep(1);
            //} catch (InterruptException e) {
            //e.printStackTrace();
            //}

            System.out.println("Swaps: " + countOfCurrentSwaps);
        }

        

//        if (!validateClusters()) {
//            for (Cluster c : clusters) {
//                c.computeCenter();
//            }
//            process();
//        }
        for (Cluster c : clusters) {
            c.computeCenter();
        }
    }

    private boolean validateClusters() {
        boolean validClusters = true;
        ArrayList<Cluster> rmList = new ArrayList<Cluster>();
        //int clusterSize = clusters.size();
        for (int i = 0; i < clusters.size(); i++) {
            if (clusters.get(i).dataPoints.size() < 4) {
                validClusters = false;
                rmList.add(clusters.get(i));
                for (DataPoint dp : clusters.get(i).dataPoints)
                    dp.cluster = null;
            }
        }
        clusters.removeAll(rmList);
        System.out.println("removed " + rmList.size() + " clusters");
        
        int size;
        double simVal;
        
        for (int i = 0; i < clusters.size(); i++) {
            TreeMap<Double, DataPoint> simMap = new TreeMap<Double, DataPoint>();
            ArrayList<DataPoint> tmpList = new ArrayList<DataPoint>();
            if (clusters.get(i).dataPoints.size() > 200) {
//                for (DataPoint dp : clusters.get(i).dataPoints) {
//                    simVal = clusters.get(i).centerDataPoint.computeCosineSimilarityTo(dp);
//                    simMap.put(simVal, dp);
//                }
//                NavigableSet<Double> keys = simMap.descendingKeySet();
//                int j = 0;
//                for (Double k : keys) {
//                    if (j < 100) {
//                        tmpList.add(simMap.get(k));
//                        j++;
//                    }
//                }
//                clusters.get(i).dataPoints.removeAll(tmpList);
//                Cluster c = new Cluster(clusters.size() + randomizer.nextInt());
//                c.add(clusters.get(i).dataPoints.get(0));
//                clusters.get(i).dataPoints = tmpList;
                size = clusters.get(i).dataPoints.size();
                System.out.println("splitting cluster, size: " + size);
                tmpList = new ArrayList(clusters.get(i).dataPoints.subList(100, size));//(size/2)));
                clusters.get(i).dataPoints.removeAll(tmpList);
                Cluster c = new Cluster(clusters.size() + randomizer.nextInt());

                //c.add(tmpList.get(101));
                //original loop:
                for (DataPoint dp : tmpList) {
                    c.add(dp);
                }
                clusters.add(c);
                validClusters = false;
            }
        }

        return validClusters;
    }
    private void replaceSmallClusters() {
        ArrayList<Cluster> rmList = new ArrayList<Cluster>();
        int clusterSize = clusters.size();
        for (int i = 0; i < clusters.size(); i++) {
            if (clusters.get(i).dataPoints.size() < 4) {
                rmList.add(clusters.get(i));
                for (DataPoint dp : clusters.get(i).dataPoints)
                    dp.cluster = null;
            }
        }
        clusters.removeAll(rmList);

        for (int j = 0; j < rmList.size(); j++) {
            Cluster c = new Cluster(rmList.get(j).id);
            DataPoint dp = dataPoints.get(randomizer.nextInt(dataPoints.size()));
            c.add(dp);
            clusters.add(c);
        }
        System.out.println("removed " + rmList.size() + " clusters");
    }

    private void splitBigClusters() {
        for (int i = 0; i < clusters.size(); i++) {
            if (clusters.get(i).isTooBig()) {
                Cluster newCluster = clusters.get(i).split(clusters.size() + 10, clusters.size() + 11);
                clusters.add(newCluster);
                System.out.println("Splitted a cluster");
             }
        }
    }
    private String getLemma (String id) {
    	return id;
//		
//		HibernateLexicalItemDao lexDao = new HibernateLexicalItemDao();
//	
//		LexicalItem lemma = (LexicalItem)lexDao.findById(Long.parseLong(id));
//		return lemma.getLemma() + "_" + id;
//		
	}

    public void writeClusters(String filename) {

        /*
         * WRITE TEXTFILE WITH ALL CLUSTERS
         */
        try {
            BufferedWriter clustersOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF8"));
            BufferedWriter valuesOut = FileCommunicatorWriting.createWriter("valuesIndices.txt", true);
            String name = "";
            DataPoint centerDatapoint = null;
            int simInd = 0;
            double partitionQuality = 0.0;
            
            double simVal = 0.0;
            for (int i = 0; i < clusters.size(); i++) {
                double quality = 0.0;
                double avQuality = 0.0;
                //eliminates cluster members with the same value, eg. if many have 0.0!
               // TreeMap<Double, String> clustermap = new TreeMap<Double, String>();
                TreeMap<String, Double> clustermap = new TreeMap<String, Double>();
                if (clusters.get(i).getRealDataCenter() == null) {
                    name = "no data!";
                }
                else {
                    centerDatapoint = clusters.get(i).getRealDataCenter();
                    name = centerDatapoint.name;
                    simInd = centerDatapoint.simIndex;
                }
                valuesOut.write("CLUSTER NR. " + i + ":  " + name + " =============\n");
                clustersOut.write("CLUSTER NR. " + i + ":  " + name + " =============\n");
                for (int j = 0; j < clusters.get(i).dataPoints.size(); j++) {
                    DataPoint dp = clusters.get(i).dataPoints.get(j);
                    if (dp.name != null) {
                        simVal = centerDatapoint.computeCosineSimilarityTo(dp);
                        quality += simVal;
                        //clustermap.put(simVal, dp.name);
                        String lemmaId = getLemma(dp.name);
                        clustermap.put(lemmaId, simVal);
                        valuesOut.write(dp.name + "- " + dp.getValues().toString() + " indices: " + dp.getColumns().toString());
                        valuesOut.write("\n");
                        
                    }
                }
                avQuality = quality / (clusters.get(i).dataPoints.size());
                partitionQuality += avQuality;
                //NavigableSet<Double> keys = clustermap.descendingKeySet();
                clustersOut.write("CLUSTER QUALITY: " + quality + " AVERAGE QUALITY: " + avQuality + "\n");
                clustersOut.write("Size of cluster: " + clusters.get(i).dataPoints.size() + "\n");
               // for (Double d : keys) {
                for (String s : clustermap.keySet()) {
                   // clustersOut.write(clustermap.get(d) + "     " + d + "\n");
                    clustersOut.write(s + "\t" + clustermap.get(s) + "\n");

                }
            }
            clustersOut.write("QUALITY OF THE PARTITION: " + partitionQuality/clusters.size());
            clustersOut.close();
            valuesOut.close();
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
        for (int i = 0; i < clusters.size(); i++) {
            double quality = 0.0;
            double avQuality = 0.0;
            TreeMap<Double, String> clustermap = new TreeMap<Double, String>();
            if (clusters.get(i).getRealDataCenter() == null) {
                name = "no data!";
            }
            else {
                centerDatapoint = clusters.get(i).getRealDataCenter();
                name = centerDatapoint.name;
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
                for (int j = 0; j < clusters.get(i).dataPoints.size(); j++) {
                    DataPoint dp = clusters.get(i).dataPoints.get(j);
                    if (dp.name != null) {
                        simVal = centerDatapoint.computeCosineSimilarityTo(dp);
                        quality += simVal;
                        clustermap.put(simVal, dp.name);
                    }
                }
                avQuality = quality / (clusters.get(i).dataPoints.size());
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
                for (int k = 2; k < clusters.get(i).dataPoints.size() + 2; k++) {
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

    public int getDimensions() {
        return dimensions;
    }

    public void setDimensions(int dimensions) {
        this.dimensions = dimensions;
    }

}
