package is.merkor.statistics.hibernate.daos;

/*******************************************************************************
 * MerkOrCore
 * Copyright (c) 2012 Anna B. Nikulásdóttir
 * 
 * License ...
 * 
 *******************************************************************************/

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

//import is.merkor.statistics.database.data.ClusterItem;
import is.merkor.statistics.hibernate.data.LexicalItem;
import is.merkor.statistics.hibernate.HibernateUtil;

/**
 * The read only Direct Access Object for class {@link LexicalItem}
 * 
 * @author Anna B. Nikulasdottir
 * @version 1.0
 */

public class LexicalItemDAO extends HibernateGenericDAO<LexicalItem, Long> {
	
	
	public LexicalItemDAO () {
		super(LexicalItem.class);
	}
	
	/**
	 * Returns a list of {@link LexicalItem}s having lexId {@code id}.
	 * {@code lexId} is not the unique id for a lexicalItem, different
	 * senses of the same lemma have the same lexId.
	 *  
	 * @param id the lexId to search for
	 * @return a List of LexicalItem having id as lexId, an empty List if no element is found
	 */
	public List<LexicalItem> findByLexId (int id) {
		SessionFactory factory = HibernateUtil.getSessionFactory();
		Session session = factory.openSession();
		try {
			Query query = session.getNamedQuery("lexicalitem.by.lexId");
			query.setInteger(0, id);
			List<LexicalItem> result = query.list();
			return result;
		} finally {
			session.close();
		}
	}
	/**
	 * Find all items having param lemma as lemma.
	 * 
	 * @throws {@link IllegalArgumentException} if lemma == null
	 * @param lemma the lemma to search for
	 * @return a List of LexicalItem having param lemma as lemma, an empty List if no element is found
	 */
	public List<LexicalItem> findByLemma (String lemma) {
		if (null == lemma) {
			throw new IllegalArgumentException ("parameter 'lemma' must not be null!");
		}
		SessionFactory factory = HibernateUtil.getSessionFactory();
		Session session = factory.openSession();
		try {
			Query query = session.getNamedQuery("lexicalitem.by.lemma");
			query.setString(0, lemma);
			List<LexicalItem> result = query.list();
			return result;
		} finally {
			session.close();
		}
	}
	
	/**
	 * Find all items having param lemma as lemma.
	 * 
	 * @throws {@link IllegalArgumentException} if lemma == null
	 * @param lemma the lemma to search for
	 * @return a List of LexicalItem having param lemma as lemma, an empty List if no element is found
	 */
	public List<LexicalItem> findByLemmaAndWordclass (String lemma, String wordclass) {
		if (null == lemma || null == wordclass) {
			throw new IllegalArgumentException ("parameter 'lemma' or parameter 'wordclass' must not be null!");
		}
		SessionFactory factory = HibernateUtil.getSessionFactory();
		Session session = factory.openSession();
		try {
			Query query = session.getNamedQuery("lexicalitem.by.lemmaandwordclass");
			query.setString(0, lemma);
			query.setString(1, wordclass);
			List<LexicalItem> result = query.list();
			return result;
		} finally {
			session.close();
		}
	}
	/**
	 * Finds all <code>LexicalItem</code>s matching <code>lemma</code>.
	 * Note: the regex has to be made postgresql conform before calling
	 * this method - replace '(.)*' by '%' and '(.)?' by '_'.
	 * At the moment only simple wild card match is supported, no tests
	 * have been made for example with 'or' or character sets.
	 * 
	 * Fetches {@link ClusterItem}s for each found item.
	 * 
	 * @throws {@link IllegalArgumentException} if regex is {@code null}
	 * @param regex the regex to match
	 * @return a List of LexicalItems matching lemma, an empty List if nothing is found
	 */

	public List<LexicalItem> findItemsMatching (String regex) {
		if (null == regex) {
			throw new IllegalArgumentException ("parameter 'regex' must not be null!");
		}
		SessionFactory factory = HibernateUtil.getSessionFactory();
		Session session = factory.openSession();
		try {
			Criteria criteria = session.createCriteria(LexicalItem.class).setFetchMode("clusterItems", FetchMode.JOIN).add(Restrictions.ilike("lemma", regex));
			List<LexicalItem> result = criteria.list();
			return result;
		} finally {
			session.close();
		}
	}
	/**
	 * Finds and returns the max value for lexId in table LexicalItem
	 * @return the max value for lexId in the table
	 */
	public int findMaxLexId () {
		SessionFactory factory = HibernateUtil.getSessionFactory();
		Session session = factory.openSession();
		try {
			Query query = session.getNamedQuery("lexicalitem.max.lexId");
			int maxId = (Integer)query.uniqueResult();
			return maxId;
		} finally {
			session.close();
		}
	}
	/**
	 * Returns a list of lexicalItems having exactly the number of relations
	 * {@code number}
	 * @param number
	 * @return a list of lexicalItems, an empty list if nothing is found
	 */
	public List<LexicalItem> findItemsWithRelationNumber (int number) {
		return findByRelationNumber(number, "lexicalitem.by.wp_count");
	}
	/**
	 * Returns a list of lexicalItems having at most the number of relations
	 * {@code number}
	 * @param number
	 * @return a list of lexicalItems, an empty list if nothing is found
	 */
	public List<LexicalItem> findItemsWithMaxRelationNumber (int number) {
		return findByRelationNumber(number, "lexicalitem.by.max_wp_count");
	}
	/**
	 * Returns a list of lexicalItems having at least the number of relations
	 * {@code number}
	 * @param number
	 * @return a list of lexicalItems, an empty list if nothing is found
	 */
	public List<LexicalItem> findItemsWithMinRelationNumber (int number) {
		return findByRelationNumber(number, "lexicalitem.by.min_wp_count");
	}
	private List<LexicalItem> findByRelationNumber (int number, String namedQuery) {
		SessionFactory factory = HibernateUtil.getSessionFactory();
		Session session = factory.openSession();
		try {
			Query query = session.getNamedQuery(namedQuery);
			query.setInteger(0, number);
			List<LexicalItem> result = query.list();
			return result;
		} finally {
			session.close();
		}
	}
	
	/**
	 * Returns a list of lexicalItems having at least {@code min} number of 
	 * relations and at the most {@code max} relations.
	 * @param min minimum number of relations for an item
	 * @param max maximum number of relation for an item
	 * @return a list of lexicalItems or an empty list if nothing is found
	 */
	public List<LexicalItem> findItemsWithMinMaxRelationNumber(int min, int max) {
		SessionFactory factory = HibernateUtil.getSessionFactory();
		Session session = factory.openSession();
		try {
			Query query = session.getNamedQuery("lexicalitem.by.min_max_wp_count");
			query.setInteger(0, min);
			query.setInteger(1, max);
			List<LexicalItem> result = query.list();
			return result;
		} finally {
			session.close();
		}
	}
}
