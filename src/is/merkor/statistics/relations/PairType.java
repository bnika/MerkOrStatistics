package is.merkor.statistics.relations;

/**
 * Holds necessary information for the computing of mutual information
 * of cooccurrence pairs.
 * 
 * Two sets of frequencies are included:
 * 1) pure frequencies of items and set size
 * 2) a contingency table
 * 
 * Pure frequencies include:
 * a) joint frequency of the pair
 * b) total frequency of each element in the set
 * c) set size (N)
 * 
 * A contingency table is computed as follows:
 * a) joint frequencies of the pair
 * b) total frequency of each element in the set MINUS the joint frequency (elem_1 AND NOT elem_2 and v.v.)
 * c) set size (N) MINUS all other frequencies in the contingency table.
 * 
 * (The term 'pair type' is adapted from Stefan Evert, http://www.collocations.de)
 * 
 * @author Anna B. Nikulasdottir
 *
 */
public class PairType {
	// pure frequencies
	private Integer jointFrequency;
	private Integer freq_1;
	private Integer freq_2;
	private Integer N;
	// contingency table (jointFrequency is the same as in pure freq)
	private Integer notElem2;
	private Integer notElem1;
	private Integer notElem1_notElem2;
	
	public PairType () {
		jointFrequency = 0;
		freq_1 = 0;
		freq_2 = 0;
		N = 0;
		initContingencyTable();
	}
	public PairType (Integer jointF, Integer f1, Integer f2, Integer sum) {
		jointFrequency = jointF;
		freq_1 = f1;
		freq_2 = f2;
		N = sum;
		initContingencyTable();
	}
	public PairType (String jointF, String f1, String f2, String sum) throws NumberFormatException {
		jointFrequency = Integer.parseInt(jointF);
		freq_1 = Integer.parseInt(f1);
		freq_2 = Integer.parseInt(f2);
		N = Integer.parseInt(sum);
		initContingencyTable();
	}
	
	public Integer jointFrequency () {
		return jointFrequency;
	}
	public Integer freq_1 () {
		return freq_1;
	}
	public Integer freq_2 () {
		return freq_2;
	}
	public Integer getN () {
		return N;
	}
	public Integer notElem1 () {
		return notElem1;
	}
	public Integer notElem2 () {
		return notElem2;
	}
	public Integer not1_Not2 () {
		return notElem1_notElem2;
	}
	
	public void setJointFrequency (Integer freq) {
		jointFrequency = freq;
		initContingencyTable();
	}
	public void setFreq_1 (Integer freq) {
		freq_1 = freq;
		initContingencyTable();
	}
	public void setFreq_2 (Integer freq) {
		freq_2 = freq;
		initContingencyTable();
	}
	public void setN (Integer sum) {
		N = sum;
		initContingencyTable();
	}
	private void initContingencyTable () {
		notElem2 = freq_1 - jointFrequency;
		notElem1 = freq_2 - jointFrequency;
		notElem1_notElem2 = N - freq_1 - freq_2;
	}
}
