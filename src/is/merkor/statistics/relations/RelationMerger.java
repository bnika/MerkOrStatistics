package is.merkor.statistics.relations;

import is.merkor.util.FileCommunicatorWriting;
import is.merkor.util.MerkorCSVFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Merges relations from tensor files, such that word pairs having different relations
 * occur only once with summed up frequencies from all different relations.
 * 
 * Example (id_1  id_2  joint_freq  f1  f2  sum):
 * One pair, two different (unknown) relations:
 *
 *  16690	10380	20	42450	7855	12827225
 *  16690	10380	26	42450	7855	12827225
 *
 * Gets merged to:
 * 
 * 16690    10380    46    42450    7855   12827225
 * 
 * @author Anna B. Nikulasdottir
 *
 */

public class RelationMerger {
	
	private final int maxTensorLines = 100000;
	
	private int idCounter = 0;
	
	/**
	 * Takes as an input a tab separated file with frequency information of word pairs.
	 * Merges the lines with identical item1 and item2 and sums up the frequencies. 
	 * 
	 * One pair, two different (unknown) relations:
	 *
	 * 	16690	10380	20	42450	7855	12827225
	 *	16690	10380	26	42450	7855	12827225
	 *
	 * Gets merged to:
	 * 
	 * 16690    10380    46    42450    7855   12827225
	 */
	public void mergeTensor (String infile, String outfile) {
		System.out.println("merge tensor ...");
		MerkorCSVFile csv = new MerkorCSVFile(infile, '\t');
		String item1;
		String item2;
		String currItem1 = "";
		String currItem2 = "";
		PairType current = new PairType();
		List<String> tensorLines = new ArrayList<String>();
		writeHeader(outfile);
		for (String[] line : csv) {
			if (line.length < 6)
				continue;
			
			item1 = line[0];
			item2 = line[1];
			int jointFreq = Integer.parseInt(line[2]); //joint frequency
			int freq1 = Integer.parseInt(line[3]); // item1
			int freq2 = Integer.parseInt(line[4]); // item2
			int N = Integer.parseInt(line[5]); // all relations
			
			//the same co-occurrence pair as in the last line?
			if (item1.equals(currItem1) && item2.equals(currItem2)) { 
				current.setJointFrequency(current.jointFrequency() + jointFreq);
				current.setFreq_1(freq1);
				current.setFreq_2(freq2);
				current.setN(N);
			}
			else {
				if(!currItem1.isEmpty()) {
					tensorLines.add(composeTensorLine(currItem1, currItem2, current, true));
					current.setJointFrequency(0);
				}
				currItem1 = item1;
				currItem2 = item2;
				current = new PairType(jointFreq, freq1, freq2, N);
			}
			tensorLines = writeIfMaxSizeReached(tensorLines, outfile);
		}
		FileCommunicatorWriting.writeListAppend(tensorLines, outfile);
	}
	
	private String composeTensorLine(String item1, String item2, PairType current, boolean withId) {
		StringBuffer buffer = new StringBuffer();
		if (withId) {
			idCounter++;
			buffer.append(idCounter + "\t");
		}
		
		buffer.append(item1 + "\t")
			.append(item2 + "\t")
			.append(current.jointFrequency() + "\t")
			.append(current.freq_1() + "\t")
			.append(current.freq_2() + "\t")
			.append(current.getN());
		
		return buffer.toString();
	}
	private void writeHeader (String file) {
		FileCommunicatorWriting.writeLineNoAppend(file, "id\tl1\tl2\tf\tf1\tf2\tN");
	}
	
	private List<String> writeIfMaxSizeReached (List<String> lines, String outfile) {
		if (lines.size() > maxTensorLines) {
			FileCommunicatorWriting.writeListAppend(lines, outfile);
			lines.clear();
		}
		return lines;
	}
}
