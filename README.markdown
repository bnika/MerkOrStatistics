# MerkOrStatistics

A package to compute statistical information from extracted relations from text as well as co-occurrence similarity and clustering as used in the MerkOr project. MerkOr is an automatically constructed semantic database for Icelandic.
For the relation extraction package see [MerkOrExtraction]<https://github.com/bnika/MerkOrExtraction>.

## Relation statistics - LMI of lexical relations

In order to perform statistical computation like Mutual information, frequency information of the relations and their elements have to be available.

#### Step 1: from database table to frequency tensor

Needed data:  
A postgresql database table of relations with frequency counts of each relation (see javadoc of is.merkor.statistics.relations for detailed information).  
A textfile called <code>lemmaFrequenciesRelations.txt</code>, a two column, tab-separated file with frequency counts of each lemma, i.e. how often does each lemma occur in the relation set.  

Then configure <code>hibernate.cfg.xml</code> using your db settings.  

Run <code>java -jar merkor-statistics.jar -tensor </code> which writes an outputfile <code>relationTensor\_noId\_noRel.txt</code> of the format: 

    item_1_id   item_2_id   relation_name   joint_freq   freq_item_1   freq_item_2   sum_of_relations

In case of MerkOr, the statistical computation was performed on word pairs, i.e. the relation names were left out and all occurrences of a word pair in relations were merged to get a total count of occurrences. Having the tensor file without ids and relation names, the command line command <code>sort -n tensorfile > sortedtensorfile</code> is used to prepare for merging.  

The merge command follows:  <code>java -jar merkor-statistics.jar -merge sortedtensorfile</code>.


An example of the merge process (id\_1  id\_2  joint\_freq  f1  f2  sum):
    One pair, two different (unknown) relations:  
      
    16690	10380	20	42450	7855	12827225  
    16690	10380	26	42450	7855	12827225  
      
    Gets merged to:  
 
    id    16690    10380    46    42450    7855   12827225  

#### Step 2: Compute Local Mutual Information

Having a merged file of the final format from step 1, following command computes the local mutual information for each relation and writes to a new file:

    java -jar merkor-statistics.jar -lmi relationTensorSorted.ds

#### Step 3: LMI data to database

    java -jar merkor-statistics.jar -lmi2db relationTensorSorted_merged_lmi.ds -tablename db-tablename  

## Cooccurrence statistics

#### Cooccurrence matrix

For the computation of word co-occurrence statistics, one has first to define the words that are to be examined. There are on the one hand the words that one wants information about - in MerkOr for example these were all nouns that ... - and on the other hand so called content words. The basic of the cooccurrence statistics is to collect numbers of co-occurrences of the words in the first list with words from the content word list. These lists can of course be identical. See e.g. (ref):  

Create a directory <code>cooccurData</code> with the files <code>contentWordList.csv</code> and <code>objectWordList.csv</code> with the word-frequency tables. To compute a cooccurrence matrix for an input file or directory, run:

    java -Xmx2048M -jar merkor-statistics.jar -wordwindow your-input-file

This program creates a directory <code>sparseMatrix</code> with a Yale representation of the sparse matrix resulting from the cooccurrence computation: <code>columns.txt, indices.txt, values.txt</code>. These files are needed for the further computation of word similarities.

#### Semantic Similarity based on cooccurrences  

There are several possiblilities for similarity computation - all of them need access to the sparse matrix directory, and in that directory additionally to the sparse matrix file, a file <code>words.txt</code> should be placed, containing the list of words for which the matrix was computed for - and very important - in the same order as they were computed in the cooccurrence matrix. So use the <code>objectWordList.csv</code> which was the input for the cooccurrence matrix computation, without the frequency information.  

Similarity information:
1) Compute top n similarities for words from a wordlist - this can be the complete sparseMatrix/words.txt file, or a sublist of these (order does not matter here). If parameter -n is not set, the default value 10 is used. Write the results into an SQL-statement file with insert statements:

    java -Xmx2048M -jar merkor-statistics.jar -sim sparseMatrix/ -tosql sqlfile.sql -input wordlistfile  (-n nrOfRelatedWords)

2) Same as 1), but instead of an SQL file, a tab-separated csv (tsv) file is written:

    java -Xmx2048M -jar merkor-statistics.jar -sim sparseMatrix/ -tocsv csvfile -input wordlistfile

3) Get the top n similarities for one word (prints to standard out, with similarity values):

    java -Xmx2048M -jar merkor-statistics.jar -sim sparseMatrix/ -w1 word (-n nrOfRelatedWords)

4) Get the similarity value for two words from the wordlist (prints to standard out):

    java -Xmx2048M -jar merkor-statistics.jar -sim sparseMatrix/ -w1 word1 -w2 word2
    

## Clustering 

The clustering algorithm implemented in merkor-statistics is the so called Clustering-by-Committee algorithm (Pantel & Lin, 2002). It needs both the sparse matrix data as well as already computed similarity lists from the cooccurrence statistics step above. It uses the similarity lists to find promising cluster candidates (committees) and by thresholds decides if a) a new committee should be created (committees have to have a certain distance) and b) if an element gets assigned to a committee. For a detailed description, see the source code documentation and of course the paper by Pantel and Lin: Pantel & Lin (2002): Discovering Word Senses from Text. Proceedings of ACM Conference KDD-02, pp. 613-619.


  






