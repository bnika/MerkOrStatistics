package is.merkor.statistics.hibernate.data;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@NamedQueries ({
//	@NamedQuery (name = "relation.by.fromLemma",
//			query = "from LexicalRelationComplete as relation where relation.fromItem.lemma = ?"),
//	@NamedQuery (name = "relation.by.toLemma",
//			query = "from LexicalRelationComplete as relation where relation.toItem.lemma = ?"),
//	@NamedQuery (name = "relation.by.bothLemma",
//			query = "from LexicalRelationComplete as relation where relation.fromItem.lemma = ? and relation.toItem.lemma = ?"),
//	@NamedQuery (name = "relation.by.anyLemma",
//			query = "from LexicalRelationComplete as relation where relation.fromItem.lemma = ? or relation.toItem.lemma = ?"),
	@NamedQuery (name = "relation.by.fromId",
			query = "from LexicalRelationCount as relation where relation.fromItem = ?"),
	@NamedQuery (name = "relation.by.toId",
			query = "from LexicalRelationCount as relation where relation.toItem = ?"),
	@NamedQuery (name = "relation.by.bothIds",
			query = "from LexicalRelationCount as relation where relation.fromItem = ? and relation.toItem = ?"),
	@NamedQuery (name = "relation.by.anyId",
			query = "from LexicalRelationCount as relation where relation.fromItem = ? or relation.toItem = ?"),
	@NamedQuery (name = "relation.by.relationid.and.fromid", 
			query = "from LexicalRelationCount as relation where relation.type.id = ? and relation.fromItem = ?"),
	@NamedQuery (name = "relation.by.relationid.and.toid",
			query = "from LexicalRelationCount as relation where relation.type.id = ? and relation.toItem = ?"),
	@NamedQuery (name = "relation.by.relationid.and.id",
			query = "from LexicalRelationCount as relation where relation.type.id = ? and (relation.fromItem = ? or relation.toItem = ?)"),
	@NamedQuery (name = "relation.findAllByLimit",
			query = "from LexicalRelationCount as relation order by relation.id"),
	//@NamedQuery (name = "relation.sumCount",
	//	query = "select sum(count) from lexical_relation_new")
})
@Entity
@Table (name = "LEXICAL_RELATION_NEW")
public class LexicalRelationCount extends AbstractPersistentObject implements Serializable {

		
		private static final long serialVersionUID = 1918674480400615127L;
		
		private Long id;
		private Long fromItem;
		private Long toItem;
		private LexicalRelationType type;
		private int count;
		
		@Id
		public Long getId () {
			return id;
		}
		public void setId (Long id) {
			this.id = id;
		}
		
		@Column (name = "FROM_LEX_UNIT")
		public Long getFromItem () {
			return fromItem;
		}
		public void setFromItem (Long from) {
			this.fromItem = from;
		}
		
		@Column (name = "TO_LEX_UNIT")
		public Long getToItem () {
			return toItem;
		}
		public void setToItem (Long to) {
			this.toItem = to;
		}
		
		public int getCount () {
			return count;
		}
		public void setCount (int count) {
			this.count = count;
		}
		@ManyToOne
		@JoinColumn (name = "REL_ID")
		public LexicalRelationType getType () {
			return type;
		}
		public void setType (LexicalRelationType type) {
			this.type = type;
		}
		
		public String toString () {
			return "[" + this.getClass().getName() + " " + getFromItem() + " " + getType().getName() + " " + getToItem() + " count: " + getCount() + "]"; 
		}
		
	}


