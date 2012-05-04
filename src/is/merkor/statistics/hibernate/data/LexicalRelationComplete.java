package is.merkor.statistics.hibernate.data;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@NamedQueries ({
	@NamedQuery (name = "relation.by.fromLemma",
			query = "from LexicalRelationComplete as relation where relation.fromItem.lemma = ?"),
	@NamedQuery (name = "relation.by.toLemma",
			query = "from LexicalRelationComplete as relation where relation.toItem.lemma = ?"),
	@NamedQuery (name = "relation.by.bothLemma",
			query = "from LexicalRelationComplete as relation where relation.fromItem.lemma = ? and relation.toItem.lemma = ?"),
	@NamedQuery (name = "relation.by.anyLemma",
			query = "from LexicalRelationComplete as relation where relation.fromItem.lemma = ? or relation.toItem.lemma = ?"),
	@NamedQuery (name = "relation.by.fromId",
			query = "from LexicalRelationComplete as relation where relation.fromItem.id = ?"),
	@NamedQuery (name = "relation.by.toId",
			query = "from LexicalRelationComplete as relation where relation.toItem.id = ?"),
	@NamedQuery (name = "relation.by.bothIds",
			query = "from LexicalRelationComplete as relation where relation.fromItem.id = ? and relation.toItem.id = ?"),
	@NamedQuery (name = "relation.by.anyId",
			query = "from LexicalRelationComplete as relation where relation.fromItem.id = ? or relation.toItem.id = ?"),
	@NamedQuery (name = "relation.by.relationid.and.fromid", 
			query = "from LexicalRelationComplete as relation where relation.type.id = ? and relation.fromItem.id = ?"),
	@NamedQuery (name = "relation.by.relationid.and.toid",
			query = "from LexicalRelationComplete as relation where relation.type.id = ? and relation.toItem.id = ?"),
	@NamedQuery (name = "relation.by.relationid.and.id",
			query = "from LexicalRelationComplete as relation where relation.type.id = ? and (relation.fromItem.id = ? or relation.toItem.id = ?)")
})
@Entity
@Table (name = "LEX_RELATIONS_COMPLETE")
public class LexicalRelationComplete extends AbstractPersistentObject implements Serializable {

		
		private static final long serialVersionUID = 1918674480400615127L;
		
		private Long id;
		private LexicalItem fromItem;
		private LexicalItem toItem;
		private LexicalRelationType type;
		
		@Id
		public Long getId () {
			return id;
		}
		public void setId (Long id) {
			this.id = id;
		}
		@ManyToOne
		@JoinColumn (name = "FROM_LEX_UNIT")
		public LexicalItem getFromItem () {
			return fromItem;
		}
		public void setFromItem (LexicalItem from) {
			this.fromItem = from;
		}
		@ManyToOne
		@JoinColumn (name = "TO_LEX_UNIT")
		public LexicalItem getToItem () {
			return toItem;
		}
		public void setToItem (LexicalItem to) {
			this.toItem = to;
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
			return "[" + this.getClass().getName() + " " + getFromItem().getLemma() + " " + getType().getName() + " " + getToItem().getLemma() + "]"; 
		}
		
	}


