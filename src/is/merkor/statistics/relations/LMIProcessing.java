package is.merkor.statistics.relations;

import is.merkor.util.FileCommunicatorWriting;
import is.merkor.util.MerkorCSVFile;

import java.util.ArrayList;
import java.util.List;

public class LMIProcessing {
	
	private final String header = "id\tl1\tl2\tf\tf1\tf2\tN\tLMI";
	private final int maxTensorLines = 200000;
	
	public void computeLMIForFile (String infile, String outfile) {
		MerkorCSVFile csv = new MerkorCSVFile(infile, '\t');
		List<String> tensorLines = new ArrayList<String>();
		writeHeader(outfile);
		int counter = 0;
		for (String[] line : csv) {
			if (line.length < 7)
				continue;
			if (line[0].equals("id"))
				continue;
			
			try {
				counter++;
				if (counter % 1000 == 0)
					System.out.print("\rlines: " + counter);
				PairType pair = new PairType(line[3], line[4], line[5], line[6]);
				double lmi = MutualInformation.computeLMI(pair);
				tensorLines.add(composeTensorLine(line, lmi));
			} catch (NumberFormatException e) {
				e.printStackTrace();
				continue;
			}
			tensorLines = writeIfMaxSizeReached (tensorLines, outfile);
		}
		FileCommunicatorWriting.writeListAppend(tensorLines, outfile);
		System.out.println("lines: " + counter + "\n");
	}
	
	private void writeHeader (String outfile) {
		FileCommunicatorWriting.writeLineNoAppend(outfile, header);
	}
	
	private String composeTensorLine (String[] line, double lmi) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(line[0] + "\t")
		.append(line[1] + "\t")
		.append(line[2] + "\t")
		.append(line[3] + "\t")
		.append(line[4] + "\t")
		.append(line[5] + "\t")
		.append(line[6] + "\t")
		.append(lmi);
		
		return buffer.toString();
	}
	private List<String> writeIfMaxSizeReached (List<String> lines, String outfile) {
		if (lines.size() > maxTensorLines) {
			FileCommunicatorWriting.writeListAppend(lines, outfile);
			lines.clear();
		}
		return lines;
	}

}
