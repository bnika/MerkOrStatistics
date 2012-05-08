# MerkOrStatistics

A package to compute statistical information from extracted relations from text as well as co-occurrence similarity and clustering as used in the MerkOr project. MerkOr is an automatically constructed semantic database for Icelandic.
For the relation extraction package see [MerkOrExtraction]<https://github.com/bnika/MerkOrExtraction>.

## Preparation of data

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

#### Step 2: Computing Local Mutual Information

Having a merged file of the final format from step 1, following command computes the local mutual information for each relation and writes to a new file:

    java -jar merkor-statistics.jar -lmi relationTensorSorted.ds






