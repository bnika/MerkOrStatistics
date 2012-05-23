package is.merkor.statistics.clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cluster implements Comparable<Cluster> {
	ArrayList<DataPoint> dataPoints;
	DataPoint centerDataPoint; //no real data, generated center point!
    String clusterName;
    Double avgPairwiseSimilarity;
    int id;
    int lowerBound = 5;
    int upperBound = 1000;
    boolean needsNewMean = true;


    public Cluster(int id) {
        this.id = id;
        dataPoints = new ArrayList<DataPoint>();
    }
    public void add(DataPoint dataPoint) {
        getDataPoints().add(dataPoint);
        dataPoint.setCluster(this);
        needsNewMean = true;
    }

    public static void swap(Cluster from, Cluster to, DataPoint dataPoint) {
        if (null != from) {
            from.remove(dataPoint);
        }
        if(null == to) {
        	System.out.println("to is null!");
        }
        dataPoint.setCluster(to);
        to.add(dataPoint);
    }

    void remove(DataPoint dataPoint) {
        getDataPoints().remove(dataPoint);
    }
    public List<DataPoint> getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(ArrayList<DataPoint> dataPoints) {
        this.dataPoints = dataPoints;
    }

    public void setCenterDataPoint(DataPoint dp) {
        centerDataPoint = dp;
    }
    
    public boolean isTooSmall() {
        return (dataPoints.size() < lowerBound);
    }
    public boolean isTooBig() {
        return (dataPoints.size() > upperBound);
    }
    public void clear() {
//        for (int i = 0; i < dataPoints.size(); i++) {
//            dataPoints.get(i).cluster = null;
//            remove(dataPoints.get(i));
//        }
        dataPoints = new ArrayList<DataPoint>();
        centerDataPoint = null;
        needsNewMean = true;
    }
    public Cluster split(int id1, int id2) {
        
        DataPoint min = new DataPoint();
        DataPoint max = new DataPoint();
        double simVal = 0.0;
        double maxVal = 0.0;
        double minVal = Double.MAX_VALUE;

        for (DataPoint dp : dataPoints) {
            simVal = centerDataPoint.computeCosineSimilarityTo(dp);
            if (simVal > maxVal) {
                maxVal = simVal;
                max = dp;
            }
            if (simVal < minVal) {
                minVal = simVal;
                min = dp;
            }
        }
        Cluster c1 = new Cluster(id1);
        Cluster c2 = new Cluster(id2);
        //c1.add(centerDataPoint);
        c1.add(min);
        //c2.add(centerDataPoint);
        c2.add(max);
        DataPoint mean1 = c1.getCenter();
        DataPoint mean2 = c2.getCenter();

        this.clear();

        this.centerDataPoint = mean1;
        this.add(min);
        Cluster newCluster = c2;

        return newCluster;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setName(String clusterName) {
        this.clusterName = clusterName;
    }
    public DataPoint computeCenter() {

        ArrayList<Double> meanValues = new ArrayList<Double>();
        ArrayList<Integer> meanColumns = new ArrayList<Integer>();
        Map<Integer, Double> entryMap = new HashMap<Integer, Double>();
        int i = 0;
        //collect all columns represented in the cluster
        //and sum up their values
//        for (Integer ind : columns) {
//            int index = columns.indexOf(ind);
//            if (entryMap.get(ind) == null)
//                entryMap.put(ind, new Double(values.get(index)));
//            else
//                entryMap.put(ind, entryMap.get(ind) + values.get(index));
//        }

        //System.out.println("getCenter(), nr of dataPoints in cluster " + this.id + ": "+ dataPoints.size());
        long start = System.currentTimeMillis();
        for (DataPoint dp : dataPoints) {
            i = 0;
            for (Integer colNr : dp.columns) {
                //int index = dp.columns.indexOf(ind);
                Double before = entryMap.get(colNr);
                double next = dp.values.get(i);
                if (null == before)
                    entryMap.put(colNr, next);
                else 
                    entryMap.put(colNr, before + next);

                i++;
            }
        }
        long end = System.currentTimeMillis();
//        if (dataPoints.size() > 100);
//            System.out.println("finished initializing the entryMap for " + dataPoints.size() + " datapoints. time: " + (end - start) + "ms.");

        //System.out.println("converting map to lists");
        start = System.currentTimeMillis();
        meanColumns.addAll(entryMap.keySet());
        meanValues.addAll(entryMap.values());
        end = System.currentTimeMillis();
        //System.out.println("finished converting to lists. time: " + (end - start) + "ms.");
        //entryMap = null;
        int size = dataPoints.size();
        double val;
       // System.out.println("computing mean");
        start = System.currentTimeMillis();
        // get the mean of every column value
        for (int j = 0; j < meanValues.size(); j++) {
            val = meanValues.get(j)/(size); //this clusters datapoint representation is not included in dataPoints
            meanValues.set(j, val);
        }
        end = System.currentTimeMillis();
        //System.out.println("finished computing means. time: " + (end - start) + "ms.");
        centerDataPoint = new DataPoint(this, meanValues, meanColumns);
        needsNewMean = false;
        return centerDataPoint;
    }
    //added: compute ceneter AFTER each run, otherwise just get center!
    public DataPoint getCenter() {
        return centerDataPoint;
    }

    /**
     * added ABN
     * After clustering a real data point has to be marked as center point.
     * Find the closest point to the generated centerDataPoint
     * @return
     */
    public DataPoint getRealDataCenter() {
        if (dataPoints.isEmpty()) {
            System.out.println("no data!");
            return null;
        }
        DataPoint center = getDataPoints().get(0);
        //double minDistance = Double.MAX_VALUE;
        double maxSimilarity = 0.0D;
        for (int i = 0, size = getDataPoints().size(); i < size; i++) {
            DataPoint dataPoint = getDataPoints().get(i);
            double similarity = 0.0D;
//            for (int j = 0; j < dimensions; j++) {
//                //distance += Math.pow(centerDataPoint.values[i] - dataPoint.values[i], 2);
//            }
//            SparseMatrix matrix1 = new SparseMatrix(centerDataPoint.values, centerDataPoint.columns);
//            SparseMatrix matrix2 = new SparseMatrix(dataPoint.values, dataPoint.columns);

            similarity = centerDataPoint.computeCosineSimilarityTo(dataPoint);
//            distance = Math.sqrt(distance);
//            if (distance < minDistance) {
//                center = dataPoint;
//                minDistance = distance;
//            }
            if (similarity > maxSimilarity) {
                center = dataPoint;
                maxSimilarity = similarity;
            }
        }
        return center;
    }
    
    public void computeAvgPairwiseSimilarity () {
    	double sumSim = 0.0;
    	int count = 0;
    	for (int i = 0; i < dataPoints.size() - 1; i++) {
    		for (int j = i + 1; j < dataPoints.size(); j++) {
    			sumSim += dataPoints.get(i).computeCosineSimilarityTo(dataPoints.get(j));
    			count++;
    		}
    	}
    	avgPairwiseSimilarity = (sumSim / count) * dataPoints.size(); // s. CBC_Phase2 for expl: |c| x avgsim(c)
    }
    public void setAvgSimilarity (Double sim) {
    	avgPairwiseSimilarity = sim;
    }
    public Double getAvgSimilarity() {
    	if (null == avgPairwiseSimilarity)
    		return 0.0;
    	return avgPairwiseSimilarity;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public boolean equals(Cluster cluster) {
    	return id == cluster.getId();
    }
    @Override
    public String toString() {
        //return "Cluster: " + getCenter();
        //return name;
        return getRealDataCenter().name;
    }
    /**
     * Compares two clusters on the base of their avgPairwiseSimilarity.
     * A larger value should be sorted above a smaller value. 
     * If both values are null, 0 is returned.
     */
	@Override
	public int compareTo(Cluster other) {
		if (null == avgPairwiseSimilarity && null == other.getAvgSimilarity())
			return 0;
		if (null == avgPairwiseSimilarity)
			return 1;
		if (null == other.getAvgSimilarity())
			return -1;
		else
			return other.getAvgSimilarity().compareTo(avgPairwiseSimilarity);
	}

}
