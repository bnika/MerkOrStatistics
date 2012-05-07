package is.merkor.statistics.relations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import is.merkor.statistics.hibernate.data.LexicalRelationCount;
import is.merkor.statistics.hibernate.daos.RelationCountDAO;

import is.merkor.util.FileCommunicatorReading;
import is.merkor.util.FileCommunicatorWriting;

/**
 * A class that creates a tensor from the frequency information of relations.
 * 
 * Given an input relation with its number of occurrences, plus frequency information
 * of each item of the relation and the sum of all relations, a tensor for statistical
 * computation is created:
 * 
 * line_id    lex_id_1    lex_id_2    relation    joint_freq    freq_item_1    freq_item_2    sum_minus_all_frequencies
 *     
 * @author Anna B. Nikulasdottir
 *
 */

public class RelationTensor {
	private final String outputFile;
	private final RelationCountDAO dao;
	private final Map<Long, Integer> lemmaFrequencies;
	private final String lemmaFrequenciesFile = "patternFrequencies.txt";
	
	// sum of all lexical relations in database
	private final int SUM_RELATIONS = 12827225; //12,827,225
	
	/**
	 * The default constructor creates an outputFile {@code relationTensor.txt}.
	 * To specify another outputFile, use {@code RelationFile(filename)}
	 */
	public RelationTensor () {
		this("relationTensor.txt");
	}
	public RelationTensor (String outputFile) {
		this.outputFile = outputFile;
		this.dao = new RelationCountDAO();
		this.lemmaFrequencies = getFrequencies();
	}
	
	/**
	 * Collects all relation counts from the database and creates tensors for 
	 * statistical computation.
	 * Writes result tensors to a text file.
	 */
	public void createTensorFromDB () {
		int tableSum = 0;
		int limit = 100000;
		
		while (tableSum < 3500000) {
			List<String> tensorLines = new ArrayList<String>();
			List<LexicalRelationCount> relationList = dao.findAllByLimit(tableSum, limit);
			System.out.println("got list elems " + tableSum);
			Integer freq1;
			Integer freq2;
			for (LexicalRelationCount rel : relationList) {
				Long id1 = rel.getFromItem();
				Long id2 = rel.getToItem();
				freq1 = lemmaFrequencies.get(id1);
				freq2 = lemmaFrequencies.get(id2);
				if (!validFreq(rel, freq1, freq2))
					continue;
				
				tensorLines.add(composeTensorLine(rel, freq1, freq2));
			}
			tableSum += limit;
			FileCommunicatorWriting.writeListAppend(tensorLines, outputFile);
			tensorLines.clear();
		}
	}
	
	/*
	 * If for some reason an element of a relation is not contained in the lemma frequencies list,
	 * make a notice about that and return false.
	 */
	private boolean validFreq (LexicalRelationCount rel, Integer freq1, Integer freq2) {
		if (null == freq1) {
			System.out.println("no freq for id1:" + rel.getFromItem() + "(" + rel.toString() + ", id2: " + rel.getToItem() + ") COUNT: " + rel.getCount());
			return false;
		}
		if (null == freq2) {
			System.out.println("no freq for id2:" + rel.getFromItem() + "(" + rel.toString() + ", id2: " + rel.getToItem() + ") COUNT: " + rel.getCount());
			return false;
		}
		return true;
	}
	
	private String composeTensorLine (LexicalRelationCount rel, int freq1, int freq2) {
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(rel.getId() + "\t")
		.append(rel.getFromItem() + "\t")
		.append(rel.getToItem() + "\t")
		.append(rel.getType().getName() + "\t")
		.append(rel.getCount() + "\t")
		.append((freq1 - rel.getCount()) + "\t")  // occurrences of item1 without item2
		.append((freq2 - rel.getCount()) + "\t")  // occurrences of item2 without item1
		.append(SUM_RELATIONS - rel.getCount() - freq1 - freq2); // relations without item1 and item2
		
		return buffer.toString();
	}
	
	private Map<Long, Integer> getFrequencies() {
		return FileCommunicatorReading.getLongIntMapFromFile(lemmaFrequenciesFile);
	}
	
	public static void main (String[] args) {
		RelationTensor tensor = new RelationTensor();
		tensor.createTensorFromDB();
	}
}
