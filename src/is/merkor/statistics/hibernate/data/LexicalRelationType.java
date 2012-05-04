package is.merkor.statistics.hibernate.data;

/*******************************************************************************
 * MerkOrCore
 * Copyright (c) 2012 Anna B. Nikulásdóttir
 * 
 * License ...
 * 
 *******************************************************************************/

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * LexicalRelationType maps to the table LEXICAL_RELATION_TYPE in the MerkOr database.
 * It contains a name and a description (which might be more appropriate for display),
 * and a unique id.
 * 
 * @author Anna B. Nikulasdottir
 * @version 1.0
 *
 */
@Entity
@Table (name = "LEXICAL_RELATION_TYPE")
public class LexicalRelationType extends AbstractPersistentObject implements Serializable, Comparable<LexicalRelationType>{
	
	private static final long serialVersionUID = 729339418127590L;
	
	private Long id;
	private String name;
	private String description;
	
	/**
	 * An empty constructor used by the Hibernate framework. No attributes initialized!
	 */
	public LexicalRelationType () {
		
	}
	/**
	 * a constructor for a placeholder object, only attribute {@code name} becomes a real value.
	 * Needed for example in a ComboBox of LexicalRelationTypes where the top-element should show "all relations"
	 * TODO: check another solution, unsave object state!
	 * @param name the name of the new relation
	 */
	public LexicalRelationType (String name) {
		this.name = name;
		this.id = 0L;
		this.description = "";
	}
	public LexicalRelationType (Long id, String name, String description) {
		setId(id);
		setName(name);
		setDescription(description);
	}
	@Id
	public Long getId () {
		return id;
	}
	/**
	 * Sets the {@code id} attribute
	 * @param id
	 * @throws IllegalArgumentException if param 'id' is null
	 */
	public void setId (Long id) {
		if (null == id)
			throw new IllegalArgumentException("param 'id' must not be null!");
		this.id = id;
	}
	public String getName () {
		return name;
	}
	/**
	 * Sets the {@code name} attribute
	 * @param name
	 * @throws IllegalArgumentException if param 'name' is {@code null}
	 */
	public void setName (String name) {
		if (null == name)
			throw new IllegalArgumentException("param 'name' must not be null!");
		this.name = name;
	}
	public String getDescription () {
		return description;
	}
	/**
	 * Sets the {@code description} attribute
	 * @param descr
	 * @throws IllegalArgumentException if param 'descr' is {@code null}
	 */
	public void setDescription (String descr) {
		if (null == name)
			throw new IllegalArgumentException("param 'descr' must not be null!");
		this.description = descr;
	}
	
	/**
	 * Returns a string representation of this lexicalRelation.
	 * It is composed as follows: 
	 * <p>
	 * {@code "[" + this.getClass().getName() + id + name + "]"}
	 * 
	 * @return a string representation of this cluster
	 */
	public String toString () {
		return "[" + this.getClass().getName() + getId() + " " + getName() + "]";
	}
	
	/**
	 * Compares two relationTypes. The comparison is based on the name attributes, with these being equal, 
	 * the id attributes are compared. 
	 * 
	 * @return the value 0 if the argument relationType is equal to this relationType; a value less than 0
	 * if this relationType's name is less than the argument's name, or, these being equal, if this
	 * relationType's id is less than the argument's id; and a value greater than 0 if this
	 * relationType's name is greater than the argument's name, or, these being equal, if this relationType's id is 
	 * greater than the argument's id;
	 */
	@Override
	public int compareTo (LexicalRelationType type) {
		
		int result = getName().compareTo(type.getName());
		if (result == 0)
			result = getId().compareTo(type.getId());
		
		return result;
	}
	/**
	 * Compares the specified object with this object for equality.
	 * Returns {@code true} if and only if the specified object is also
	 * a LexicalRelationType and {@code this.compareTo(relationType2) == 0}.
	 * 
	 * @param obj the object to be compared for equality with this lexicalRelationType
	 * @return {@code true} if the specified object is equal to this lexicalRelationType, otherwise returns {@code false}
	 */
	@Override
	public boolean equals (Object obj) {
		if (obj == this)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		LexicalRelationType type = (LexicalRelationType) obj;
		
		return compareTo(type) == 0;
	}
	/**
	 * Returns the hash code value for this lexicalRelationType. 
	 * The hash code of a lexicalRelationType uses the hash code generator of {@link org.apache.commons.lang.builder.HashCodeBuilder}:
	 * <p>
	 * {@code hashCode = new HashCodeBuilder().append(getId()).append(getName()).toHashCode())}
	 * 
	 * @return the hash code value for this lexicalRelationType
	 * 
	 */
	@Override
	public int hashCode () {
		return new HashCodeBuilder()
        .append(getId())
        .append(getName())
        .toHashCode();
	}
}
