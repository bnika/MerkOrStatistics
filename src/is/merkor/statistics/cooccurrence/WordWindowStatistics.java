package is.merkor.statistics.cooccurrence;

import java.util.LinkedList;
import java.util.List;

/**
 * A class to <p>
 * - build a matrix of co-occurences of frequent content words
 * with selected words of a corpus. Co-occurrences are normalized to a log value or PPMI <p>
 * 
 * @author Anna B. Nikulasdottir
 */

public class WordWindowStatistics extends CooccurrenceStatistics {
	
    //A sequence of words from the input. This field should contain max.
    //maxWindowSize + 1 words.
    private LinkedList<String> wordWindow = new LinkedList<String>();
    private int maxWindowSize = 7;		//Max. number of words around a content word, default value
    //Content words to be processed
    private LinkedList<String> contentWrdQueue = new LinkedList<String>();
    private int windowPos = -1;	//Position of a content word in wordWindow

    /**
     * Creates a new WordWindowStatistics object.
     *
     * @param freqFile contains content words
     * @param nounsFile contains nouns
     * @param contentWrdIndex number of content words to be used
     * @param nounIndex number of nouns to be used
     * @param windowSize allowed number of words around a content word
     */
    public WordWindowStatistics (String freqFile, String nounsFile, int contentWrdIndex, int nounIndex, int windowSize) {
    	super(freqFile, nounsFile, contentWrdIndex, nounIndex);
    	maxWindowSize = windowSize;
    	System.out.println("Created a " + subjectWrdIndex + " x " + contentWrdIndex + " matrix!");
    	System.out.println("Using window size " + maxWindowSize);
	}
    public WordWindowStatistics (String freqFile, String nounsFile, int windowSize) {
    	super(freqFile, nounsFile);
    	maxWindowSize = windowSize;
    	System.out.println("Created a " + subjectWrdIndex + " x " + contentWrdIndex + " matrix!");
    	System.out.println("Using window size " + maxWindowSize);
	}
    public WordWindowStatistics (String freqFile, String nounsFile) {
    	super(freqFile, nounsFile);
    	System.out.println("Created a " + subjectWrdIndex + " x " + contentWrdIndex + " matrix!");
    	System.out.println("Using window size " + maxWindowSize);
	}

    /**
     * Processes a new word read from the input.
     * Adds it to <code>fWordWindow</code> and searches for it in
     * <code>fContentWords</code>. If <code>fWordWindow</code> has reached
     * maximum size, this method calls <code>processQueues()</code>.
     * @param wrd the word from input to be processed
     */
    public void processWord (String wrd) {
        // wordWindow.size() should not get bigger than maxWindowSize*2 + 1
        if (wordWindow.size() > ((maxWindowSize * 2) + 1)) {
            processQueues();
        }
        wordWindow.add(wrd);
        //check if wrd is a content word, add to contentWrdQueue
        if (getContentWordIndex(wrd) >= 0) {
            // if there are no content words left for the current wordWindow
            // reset windowPos to be at the position of wrd
            if (contentWrdQueue.isEmpty()) {
                resetWindowPos(wrd);
            }
            contentWrdQueue.add(wrd);
        }
        if (wordWindow.size() >= windowPos + maxWindowSize + 1) {
            processQueues();
        }
    }
    //process contentWrdQueue - count cooccurrences of nouns in the word window 
    //and content words in the queue
    private void processQueues() {
        if (contentWrdQueue.isEmpty()) {
            if(wordWindow.size() > maxWindowSize)
                resetWordWindow();
            return;
        }
        //get the current content word and remove it from the queue
        String contWrd = contentWrdQueue.poll();
        //increment cooccurrence counts for the nouns in wordWindow
        //at the index of contWrd
        countCooccurrences(contWrd);
        //set new position in wordwindow or reset if no further content words
        //are in the queue
        String nextCont;
        if (!contentWrdQueue.isEmpty()) {
            nextCont = contentWrdQueue.getFirst();
            resetWindowPos(nextCont);
            while(windowPos > maxWindowSize) {
                wordWindow.poll();
                windowPos--;
            }
        }
        else
            resetWordWindow();
    }
    
    private void resetWindowPos(String wrd) {
        int tmpPos = windowPos;
        windowPos = wordWindow.indexOf(wrd);
        //if the same content word occurs more than once in the queue
        //we need to find the right position in wordWindow, it should be the
        //next higher index compared to tmpPos (= old windowPos)
        if (windowPos <= tmpPos) {
            List<String> tmpList = wordWindow.subList(windowPos + 1, wordWindow.size());
            int tmpInd = tmpList.indexOf(wrd);
            windowPos += tmpInd + 1;
        }
    }
    //increment cells in the column corresponding to contWrd
    //in cooccurMatrix where words from nouns are found
    //in wordWindow
    private void countCooccurrences(String contWrd) {
        int cIndex = getColumnIndex(contWrd);
        int nounIndex = -1;
        for (String s : wordWindow) {
            if (!s.equals(contWrd)){
            	for(WordStatistics w : subjectWords) {
                    if(w.getWord().equals(s)) {
                    	//the noun from wordWindow was found in
                    	//nouns, get the index and increment the
                    	//corresponding cell in cooccurMatrix
                        nounIndex = subjectWords.indexOf(w);
                        cooccurrMatrix[nounIndex][cIndex]++;
                    }
                }
            }
        }
    }
    private int getColumnIndex (String word) {
    	int index = -1;
    	for (WordStatistics w : contentWords) {
            if(w.getWord().equals(word)) {
                index = contentWords.indexOf(w);
            }
        }
    	return index;
    }
    private void resetWordWindow() {
        wordWindow = new LinkedList<String>();
        windowPos = -1;
    }
}



