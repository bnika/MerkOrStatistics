package is.merkor.statistics.hibernate.daos;

/*******************************************************************************
 * MerkOrCore
 * Copyright (c) 2012 Anna B. Nikulásdóttir
 * 
 * License ...
 * 
 *******************************************************************************/

import java.io.Serializable;
import java.util.List;

/**
 * A read only interface providing basic DAO methods to access the MerkOr database
 * 
 * @author Anna B. Nikulasdottir
 * @version 1.0
 *
 * @param <T> a Persistent Object the implementing DAO accesses
 * @param <ID> the id of that object
 */
public interface GenericDAO<T, ID extends Serializable> {
	
	/**
	 * Find the object having {@code id} as id
	 * @param id the id of the object to get
	 * @return the persistent object having {@code id} as id.
	 */
	public T findById (ID id);
	
	/**
	 * Get all elements of a table mapped by the persistent object T
	 * 
	 * @return a List of persistent objects
	 */
	public List<T> findAll ();
	
	/**
	 * Get the number of rows of the table mapped by the persistent object
	 * used to initialize the DAO
	 * 
	 * @return the number of rows
	 */
	public long getRowCount ();
}
