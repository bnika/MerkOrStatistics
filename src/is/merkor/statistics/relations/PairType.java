package is.merkor.statistics.relations;

/**
 * Holds necessary information for the computing of mutual information.
 * 
 * Mutual information measures rely on three frequency values:
 * 1) the joint frequency of element 1 and element 2
 * 2) the frequency of element 1 occurring without element 2
 * 3) the frequency of element 2 occurring without element 1
 * 
 * Additionally the value for N is needed, total size of the sample.
 * 
 * (The term 'pair type' is adapted from Stefan Evert, http://www.collocations.de)
 * 
 * @author Anna B. Nikulasdottir
 *
 */
public class PairType {
	
	private final Double jointFrequency;
	private final Double freq_1;
	private final Double freq_2;
	private final Long N;
	
	public PairType (Double jointF, Double f1, Double f2, Long sum) {
		jointFrequency = jointF;
		freq_1 = f1;
		freq_2 = f2;
		N = sum;
	}
	
	public Double jointFrequency () {
		return jointFrequency;
	}
	
	public Double freq_1 () {
		return freq_1;
	}
	
	public Double freq_2 () {
		return freq_2;
	}
	
	public Long getN () {
		return N;
	}
}
