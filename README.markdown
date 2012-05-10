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

## Clustering


  






