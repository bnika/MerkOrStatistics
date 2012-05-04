package is.merkor.statistics.hibernate.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.commons.lang.builder.HashCodeBuilder;

@NamedQueries ({
	@NamedQuery (name = "lexicalitem.by.lemma",
			query = "from LexicalItem where lemma = ?"),
	@NamedQuery (name = "lexicalitem.by.lemmaandwordclass",
			query = "from LexicalItem where lemma = ? and wordclass = ?"),
	@NamedQuery (name = "lexicalitem.by.regex",
			query = "from LexicalItem where lemma like ?"),
	@NamedQuery (name = "lexicalitem.by.lexId",
			query = "from LexicalItem where lexId = ?")
	
})
@Entity
@Table (name = "LEXICAL_ITEM")
@org.hibernate.annotations.BatchSize (size = 10)
public class LexicalItem extends AbstractPersistentObject implements Serializable, Comparable<LexicalItem> {
	
	private Long id;
	private Long lexId;
	
	private String lemma;
	private String wordclass;
	
	@Id
	@Column (name="ID")
	public Long getId () {
		return id;
	}
	public void setId (Long id) {
		this.id = id;
	}
	
	@Column (name="LEX_ID")
	public Long getLexId () {
		return lexId;
	}
	public void setLexId (Long id) {
		this.lexId = id;
	}
	@Column (name="LEMMA")
	public String getLemma() {
		return lemma;
	}
	public void setLemma(String lemma) {
		this.lemma = lemma;
	}
	@Column (name="WORDCLASS")
	public String getWordclass() {
		return wordclass;
	}
	public void setWordclass(String wordclass) {
		this.wordclass = wordclass;
	}
	
	public String toString() {
		if(getLemma().equals(""))
			return "[LexicalItem: empty_lemma]";
		return "[LexicalItem: " + getLemma() + " lex_id: " + lexId + "]";
	}
	
	public boolean equals(Object other) {
        if (this == other) return true;
        if ( !(other instanceof LexicalItem) ) return false;

        final LexicalItem item = (LexicalItem) other;

        if ( !item.getLexId().equals( getLexId() ) ) return false;
        
        return true;
    }
	
	public int hashCode() {
        return new HashCodeBuilder()
            .append(getLexId())
            .toHashCode();
    }
	@Override
	public int compareTo(LexicalItem o) {
		return getLemma().compareTo(o.getLemma());
	}

}

