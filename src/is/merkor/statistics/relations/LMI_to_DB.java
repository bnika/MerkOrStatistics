package is.merkor.statistics.relations;

import is.merkor.util.FileCommunicatorWriting;
import is.merkor.util.MerkorCSVFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Reads a tab separated file with frequency and lmi information of relations.
 * Creates an .sql file for insertion into database.
 * 
 * Following information is written:
 * 
 * relation_id  item1_id  item2_id  lmi_value
 * 
 * @author Anna B. Nikulasdottir
 *
 */
public class LMI_to_DB {
	
	private final int maxStatementLines = 200000;
	
	public void lmiToDB (String infile, String outfile, String tablename) {
		MerkorCSVFile csv = new MerkorCSVFile(infile, '\t');
		List<String> statementLines = new ArrayList<String>();
		int counter = 0;
		for (String[] line : csv) {
			if (line.length < 8)
				continue;
			if (line[0].equals("id"))
				continue;
			
			counter++;
			if (counter % 1000 == 0)
				System.out.print("\rlines: " + counter);
				
			statementLines.add(composeStatementLine(line, tablename));
			statementLines = writeIfMaxSizeReached (statementLines, outfile);
		}
		FileCommunicatorWriting.writeListAppend(statementLines, outfile);
		System.out.println("lines: " + counter + "\n");
	}
	
	private String composeStatementLine (String[] line, String tablename) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("INSERT INTO ")
		.append(tablename)
		.append(" (id, from_lex_unit, to_lex_unit, lmi) VALUES (")
		.append(line[0] + ", ")
		.append(line[1] + ", ")
		.append(line[2] + ", ")
		.append(line[7]+ ");");
		
		return buffer.toString();
	}
	
	private List<String> writeIfMaxSizeReached (List<String> lines, String outfile) {
		if (lines.size() > maxStatementLines) {
			FileCommunicatorWriting.writeListAppend(lines, outfile);
			lines.clear();
		}
		return lines;
	}
}
