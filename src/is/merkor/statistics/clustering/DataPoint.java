package is.merkor.statistics.clustering;


import java.util.ArrayList;

import com.aliasi.util.Distance;

public class DataPoint implements Comparable<DataPoint> {
	int dimensions;
    int simDimensions; 		//dimensions of the similarity vector
    int simIndex = 0;  		//index of this point in the similarity vector
    ArrayList<Double> values;
    ArrayList<Integer> columns;
    int dimension; //total dimensions of this datapoint, including zeroes
    double[] similarities; 	//similarity vector 
    double L2norm; 
    String name; 			//the word represented by this datapoint
    Cluster cluster;
    
    public DataPoint() {
        
    }
    public DataPoint(ArrayList<Double> values) {
        this.values = values;
        this.dimensions = values.size();
    }
    public DataPoint(ArrayList<Double> values, ArrayList<Integer> columns, String name, int index) {
        if(isValidData(values, columns)) {
            this.values = values;
            this.dimensions = values.size();
            this.name = name;
            this.simIndex = index;
            this.columns = columns;
            computeL2norm();
        }
        else
            System.out.println("non valid data!");

    }
    public DataPoint(ArrayList<Double> values, ArrayList<Integer> columns, String name, int index, int dimension) {
    	this(values, columns, name, index);
        this.dimension = dimension;

    }
    public DataPoint(Cluster cluster, ArrayList<Double> values, ArrayList<Integer> columns) {
        this(values);
        this.columns = columns;
        this.cluster = cluster;
        computeL2norm();
    }
    public DataPoint(Cluster cluster, ArrayList<Double> values, ArrayList<Integer> columns, String name, int index, int dimension) {
        this(values, columns, name, index, dimension);
        this.cluster = cluster;
    }

    private void computeL2norm() {
    	//L2-norm: a vector norm for a complex vector
        double val;
        double sumSqrt = 0.0D;
        for(int i = 0; i < columns.size(); i++) {
            val = values.get(i);
            sumSqrt += Math.pow(val, 2);
        }
        L2norm = Math.sqrt(sumSqrt);
    }
    public static Distance<DataPoint> COSINE_DISTANCE
	= new Distance<DataPoint>() {
		public double distance(DataPoint dp1, DataPoint dp2) {
			double similarity = dp1.computeCosineSimilarityTo(dp2);
			if((1 - similarity) > 1)
	        	return 1;
	        if((1 - similarity) < 0)
	        	return 0;
	        return 1 - similarity;
		}
    };
    public ArrayList<Integer> getColumns() {
    	return columns;
    }
    public int getDimensions() {
        return dimensions;
    }

    public int getSimDimensions() {
        return simDimensions;
    }

    public ArrayList<Double> getValues() {
        return values;
    }

    public double[] getSimilarities() {
        return similarities;
    }

    public void setValues(ArrayList<Double> values) {
        this.values = values;
    }

    public void setSimilarities(double[] similarities) {
        this.similarities = similarities;
    }

    private boolean isValidData(ArrayList<Double> vals, ArrayList<Integer> cols) {
        return vals.size() == cols.size();
    }
    /**
     * Computes the cosine similarity between this objects <code>column</code>s and
     * the <code>column</code>s of the parameter <code>DataPoint</code>.
     * 
     * @param dataPoint the <code>DataPoint</code> to which to compute cosine similarity to
     * @return the cosine similarity value between the compared <code>DataPoint</code>s.
     */
    public double computeCosineSimilarityTo(DataPoint dataPoint) {
        double similarity = 0.0D;
        int index;
        double value1;
        double value2;
        double counter = 0.0D;
        double denom;
        if(null == dataPoint)
        	return 0.0;
        //compute counter
        for(int i = 0; i < columns.size(); i++) {
            index = columns.get(i);
            if (dataPoint.columns.contains(index)) {
            	
                value1 = values.get(i);
                value2 = dataPoint.values.get(dataPoint.columns.indexOf(index));
//                if (value1 > 0 && value2 > 0) {
//                	if (!(null == this.name)) {
//                	System.out.println(this.name + " " + dataPoint.name);
//                	System.out.println("common value: " + i);
//                	System.out.println("val1: " + value1 + " val2: " + value2);
//                	}
//                }
                counter += value1 * value2;
            }
        }
        denom = L2norm * dataPoint.L2norm;
        if (denom > 0) {
                similarity = counter/denom;
        }
        //System.out.println("similarity: " + similarity);
        return similarity;
    }
    /**
     * Removes common features this datapoint has with the param dataPoint
     * 
     * @param dataPoint the DataPoint to compare
     */
    public void removeCommonFeaturesWith(DataPoint dataPoint) {
    	ArrayList<Integer> removed = new ArrayList<Integer>();
    	for(int i = 0; i < columns.size(); i++) {
    		int index = columns.get(i);
    		if(dataPoint.columns.contains(index)) {
    			columns.remove(i);
    			values.remove(i);
    			removed.add(i);
    			
    		}
    	}
    	//System.out.println("removed " + removed.toString() + " for " + name);
    }
    /*
     * Creates a full vector representation of this object
     */
    private void setVector() {
    	
    	
    }
    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }
    public String getName() {
		return name;
	}
    
    @Override
    public String toString() {
        //return "DataPoint: " + name; 
    	return name;
    }
	public Double[] toArray() {
		Double[] array = new Double[dimension];
		for(int i = 0; i < dimension; i++) {
			if(columns.contains(i))
				array[i] = values.get(columns.indexOf(i));
			else
				array[i] = 0.0;
		}
		return array;
	}
	@Override
	public int compareTo(DataPoint dp) {
		if (columns.size() < dp.columns.size())
			return 1;
		if (columns.size() > dp.columns.size())
			return -1;
		return 0;
	}
}

