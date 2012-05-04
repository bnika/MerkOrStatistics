package is.merkor.statistics.relations;

/**
 * Computes Mutual Information
 * @author Anna B. Nikulasdottir
 *
 */
public class MutualInformation {
	/**
	 * Local mutual information is computed as follows:
	 * 
	 * log(observed_joint_frequency/expected_frequency)
	 * 
	 * @param miObj
	 * @return
	 */
	public static Double computeMI (PairType miObj) {
		double expectedFreq = getExpectedFrequency(miObj);
		
		// use log10 following UCS, otherwise log2 normal
		double mi = Math.log10((miObj.jointFrequency()/expectedFreq));
		// no negative values wanted
		if (mi < 0.0)
			mi = 0.0;
		return mi;
	}

	/**
	 * Local mutual information is computed as follows:
	 * 
	 * observed_joint_frequency * log(observed_joint_frequency/expected_frequency)
	 * 
	 * @param miObj
	 * @return
	 */
	public static Double computeLMI (PairType miObj) {
		
		double expectedFreq = getExpectedFrequency(miObj);
		
		// use log10 following UCS, otherwise log2 normal
		double lmi = miObj.jointFrequency() * Math.log10((miObj.jointFrequency()/expectedFreq));
		// no negative or NaN values wanted
		if (lmi < 0.0 || miObj.jointFrequency() == 0)
			lmi = 0.0;
		return lmi;
	}
	
	/*
	 * Expected frequency: (total_occurrences_elem1 * total_occurrences_elem2) / N
	 */
	private static double getExpectedFrequency (PairType miObj) {
		Double frequencies = (miObj.jointFrequency() + miObj.freq_1()) * (miObj.jointFrequency() + miObj.freq_2());
		return frequencies / miObj.getN();
	}
}
