package is.merkor.statistics.hibernate.daos;

/*******************************************************************************
 * MerkOrCore
 * Copyright (c) 2012 Anna B. Nikulásdóttir
 * 
 * License ...
 * 
 *******************************************************************************/

import is.merkor.statistics.hibernate.HibernateUtil;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * A generic class for Data Access Objects.
 * Provides basic dao methods like <code>findById()</code>, <code>saveOrUpdate</code>, etc.
 * 
 * @author Anna B. Nikulasdottir
 * @version 1.0
 *
 * @param <T> a class representing a relation in the database
 * @param <ID> the type of id used for that relation
 */

public abstract class HibernateGenericDAO<T, ID extends Serializable> implements GenericDAO<T, ID> {
	
	private Class<T> persistentClass;
	
	public HibernateGenericDAO (Class<T> persistentClass) {
		this.persistentClass = persistentClass;
	}
	
	@Override
	public T findById(ID id) {
		SessionFactory factory = HibernateUtil.getSessionFactory();
		Session session = factory.openSession();
		try {
			T entity = (T) session.get(persistentClass, id);
			return entity;
		} finally {
			session.close();
		}
	}
	

	@Override
	public List<T> findAll() {
		SessionFactory factory = HibernateUtil.getSessionFactory();
		Session session = factory.openSession();
		try {
			Criteria criteria = session.createCriteria(persistentClass);
			List<T> objects = criteria.list();
			return objects;
		} finally {
			session.close();
		}
	}

	@Override
	public long getRowCount() {
		SessionFactory factory = HibernateUtil.getSessionFactory();
		Session session = factory.openSession();
		String className = persistentClass.getName();
		try {
			long rowCount = (Long)session.createQuery("select count(*) from  " + className).iterate().next();
			return rowCount;
		} finally {
			session.close();
		}
	}

}
