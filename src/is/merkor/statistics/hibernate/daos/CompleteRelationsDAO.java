package is.merkor.statistics.hibernate.daos;
/*******************************************************************************
 * MerkOrCore
 * Copyright (c) 2012 Anna B. Nikulásdóttir
 * 
 * License ...
 * 
 *******************************************************************************/

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import is.merkor.statistics.hibernate.data.LexicalRelationComplete;
import is.merkor.statistics.hibernate.HibernateUtil;

/**
 * The read only Direct Access Object for class {@link LexicalRelationComplete}
 * 
 * @author Anna B. Nikulasdottir
 * @version 1.0
 */
public class CompleteRelationsDAO extends HibernateGenericDAO<LexicalRelationComplete, Long> {
	
		public CompleteRelationsDAO () {
			super(LexicalRelationComplete.class);
			
		}
		
		public List<LexicalRelationComplete> findByFromId (Long id) {
			if (null == id)
				throw new IllegalArgumentException ("parameter 'id' must not be null!");
			
			return findByQueryWithId(id, null, "relation.by.fromId");
		}
		
		public List<LexicalRelationComplete> findByFromIdWithLimit (Long id, int offset, int limit) {
			if (null == id)
				throw new IllegalArgumentException ("parameter 'id' must not be null!");
			if (offset < 0 || limit < 0)
				throw new IllegalArgumentException ("parameter 'offset' or 'limit' must not be negative!");
			
			return findByQueryWithIdLimit (id, "relation.by.fromId", offset, limit);
		}
		
		public List<LexicalRelationComplete> findByToId (Long id) {
			if (null == id)
				throw new IllegalArgumentException ("parameter 'lemma' must not be null!");
			
			return findByQueryWithId(id, null, "relation.by.toId");
		}
		public List<LexicalRelationComplete> findByToIdWithLimit (Long id, int offset, int limit) {
			if (null == id)
				throw new IllegalArgumentException ("parameter 'id' must not be null!");
			if (offset < 0 || limit < 0)
				throw new IllegalArgumentException ("parameter 'offset' or 'limit' must not be negative!");
			
			return findByQueryWithIdLimit (id, "relation.by.toId", offset, limit);
		}
		
		public List<LexicalRelationComplete> findByBothIds (Long id1, Long id2) {
			if (null == id1 || null == id2)
				throw new IllegalArgumentException ("parameter 'lemma1' or 'lemma2' must not be null!");
			
			return findByQueryWithId(id1, id2, "relation.by.bothIds");
		}
		
		public List<LexicalRelationComplete> findByFromLemma (String lemma) {
			if (null == lemma)
				throw new IllegalArgumentException ("parameter 'lemma' must not be null!");
			
			return findByQueryWithLemma(lemma, null, "relation.by.fromLemma");
		}
		
		public List<LexicalRelationComplete> findByToLemma (String lemma) {
			if (null == lemma)
				throw new IllegalArgumentException ("parameter 'lemma' must not be null!");
			
			return findByQueryWithLemma(lemma, null, "relation.by.toLemma");
		}
		
		public List<LexicalRelationComplete> findByBothLemma (String lemma1, String lemma2) {
			
			if (null == lemma1 || null == lemma2)
				throw new IllegalArgumentException ("parameter 'lemma1' or 'lemma2' must not be null!");
			
			return findByQueryWithLemma(lemma1, lemma2, "relation.by.bothLemma");
		}
		
		/**
		 * Returns the relations having a {@link is.merkor.core.database.data.LexicalRelationType} 
		 * with the id {@code relationId} and the {@link is.merkor.core.database.data.LexicalItem} with the id
		 * {@code id} as the <i>from</i> (first) element.
		 * 
		 * @param relationId
		 * @param id
		 * @return a list of relations, or an empty list if nothing is found
		 * @throws IllegalArgumentException if param 'relationId' or 'id' is {@code null}
		 */
		public List<LexicalRelationComplete> findByRelationAndFromId (Long relationId, Long id) {
			if (null == relationId || null == id)
				throw new IllegalArgumentException ("parameter 'relationId' or 'id' must not be null!");
			
			return findByRelationAndId(relationId, id, "relation.by.relationid.and.fromid");
		}
		/**
		 * Returns the relations having a {@link is.merkor.core.database.data.LexicalRelationType} 
		 * with the id {@code relationId} and the {@link is.merkor.core.database.data.LexicalItem} with the id
		 * {@code id} as the <i>to</i> (second) element.
		 * 
		 * @param relationId
		 * @param id
		 * @return a list of relations, or an empty list if nothing is found
		 * @throws IllegalArgumentException if param 'relationId' or 'id' is {@code null}
		 */
		public List<LexicalRelationComplete> findByRelationAndToId (Long relationId, Long id) {
			if (null == relationId || null == id)
				throw new IllegalArgumentException ("parameter 'relationId' or 'id' must not be null!");
			
			return findByRelationAndId(relationId, id, "relation.by.relationid.and.toid");
		}
		/**
		 * Returns the relations having a {@link is.merkor.core.database.data.LexicalRelationType} 
		 * with the id {@code relationId} and the {@link is.merkor.core.database.data.LexicalItem} with the id
		 * {@code id} as the <i>from</i> (first) or <i>to</i> (second) element.
		 * 
		 * @param relationId
		 * @param id
		 * @return a list of relations, or an empty list if nothing is found
		 * @throws IllegalArgumentException if param 'relationId' or 'id' is {@code null}
		 */
		public List<LexicalRelationComplete> findByRelationAndId (Long relationId, Long id) {
			if (null == relationId || null == id)
				throw new IllegalArgumentException ("parameter 'relationId' or 'id' must not be null!");
			
			SessionFactory factory = HibernateUtil.getSessionFactory();
			Session session = factory.openSession();
			try {
				Query query = session.getNamedQuery("relation.by.relationid.and.id");
				query.setLong(0, relationId);
				query.setLong(1, id);
				query.setLong(2, id);
				List<LexicalRelationComplete> result = query.list();
				return result;
			} finally {
				session.close();
			}
		}
		/*
		 * private query methods, having search arguments and named queries as parameters
		 */
		private List<LexicalRelationComplete> findByQueryWithId (Long id1, Long id2, String namedQuery) {
			SessionFactory factory = HibernateUtil.getSessionFactory();
			Session session = factory.openSession();
			try {
				Query query = session.getNamedQuery(namedQuery);
				query.setLong(0, id1);
				if (null != id2)
					query.setLong(1, id2);
				List<LexicalRelationComplete> result = query.list();
				return result;
			} finally {
				session.close();
			}
		}
		private List<LexicalRelationComplete> findByQueryWithIdLimit (Long id, String namedQuery, int offset, int limit) {
			SessionFactory factory = HibernateUtil.getSessionFactory();
			Session session = factory.openSession();
			try {
				Query query = session.getNamedQuery(namedQuery);
				query.setLong(0, id);
				query.setFirstResult(offset);
				query.setMaxResults(limit);
				List<LexicalRelationComplete> result = query.list();
				return result;
			} finally {
				session.close();
			}
		}
		private List<LexicalRelationComplete> findByQueryWithLemma (String lemma1, String lemma2, String namedQuery) {
			SessionFactory factory = HibernateUtil.getSessionFactory();
			Session session = factory.openSession();
			try {
				Query query = session.getNamedQuery(namedQuery);
				query.setString(0, lemma1);
				if (null != lemma2)
					query.setString(1, lemma2);
				List<LexicalRelationComplete> result = query.list();
				return result;
			} finally {
				session.close();
			}
		}
		private List<LexicalRelationComplete> findByRelationAndId (Long relationId, Long id, String namedQuery) {
			SessionFactory factory = HibernateUtil.getSessionFactory();
			Session session = factory.openSession();
			try {
				Query query = session.getNamedQuery(namedQuery);
				query.setLong(0, relationId);
				query.setLong(1, id);
				List<LexicalRelationComplete> result = query.list();
				return result;
			} finally {
				session.close();
			}
		}
		

	}


