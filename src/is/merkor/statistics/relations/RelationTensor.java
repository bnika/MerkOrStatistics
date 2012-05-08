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
 * createUCSTensorFromDB:
 * 
 * Given an input relation with its number of occurrences, plus frequency information
 * of each item of the relation and the sum of all relations, a tensor for statistical
 * computation is created (items in <> are optional) as needed for e.g. the UCS statistical tool:
 * 
 * <line_id>    lex_id_1    lex_id_2   joint_freq    freq_item_1    freq_item_2    N
 *   
 * @author Anna B. Nikulasdottir
 *
 */

public class RelationTensor {
	private final String outputFile;
	private final RelationCountDAO dao;
	private final Map<Long, Integer> lemmaFrequencies;
	private final String lemmaFrequenciesFile = "resources/lemmaFrequenciesRelations.txt";
	
	// sum of all lexical relations in database
	private final int sumRelations; // = 12827225; //12,827,225
	private final long rowCount;
	private final int fetchSize = 100000;
	
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
		this.rowCount = dao.getRowCount();
		System.out.println("rows: " + rowCount);
		this.sumRelations = dao.getCountSum();
		System.out.println("sum of relations: " + sumRelations);
	}
	
	/**
	 * Collects all relation counts from the database and creates tensors for 
	 * statistical computation. UCS format (pure frequencies).
	 * Writes result tensors to a text file.
	 */
	public void createTensorFromDB (boolean withId) {
		int tableSum = 0;
		
		while (tableSum < rowCount) {
			List<String> tensorLines = new ArrayList<String>();
			List<LexicalRelationCount> relationList = dao.findAllByLimit(tableSum, fetchSize);
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
				
				tensorLines.add(composeTensorLine(rel, freq1, freq2, withId));
			}
			tableSum += fetchSize;
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
	
	private String composeTensorLine (LexicalRelationCount rel, int freq1, int freq2, boolean withId) {
		StringBuffer buffer = new StringBuffer();
		if (withId)
			buffer.append(rel.getId() + "\t");
		
		buffer.append(rel.getFromItem() + "\t")
		.append(rel.getToItem() + "\t")
		.append(rel.getCount() + "\t")
		.append(freq1 + "\t")  
		.append(freq2 + "\t")  
		.append(sumRelations);
		
		return buffer.toString();
	}
	
	private Map<Long, Integer> getFrequencies() {
		return FileCommunicatorReading.getLongIntMapFromFile(lemmaFrequenciesFile);
	}
	
//	public static void main (String[] args) {
//		RelationTensor tensor = new RelationTensor("relationTensor_noId_noRel.txt");
//		tensor.createTensorFromDB(false, false);
//	}
}
