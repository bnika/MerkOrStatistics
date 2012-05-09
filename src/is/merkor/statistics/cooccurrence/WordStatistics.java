package is.merkor.statistics.cooccurrence;

public class WordStatistics {
	private String word;
    private int frequency;

    public WordStatistics(String wrd, int freq) {
        word = wrd;
        frequency = freq;
    }

    public String getWord() {
        return word;
    }

    public int getFrequency() {
        return frequency;
    }
    public String toString() {
    	return word + ", freq: " + frequency;
    }
}
