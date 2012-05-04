package is.merkor.statistics.hibernate.daos;

/*******************************************************************************
 * MerkOrCore
 * Copyright (c) 2012 Anna B. Nikulásdóttir
 * 
 * License ...
 * 
 *******************************************************************************/

import is.merkor.statistics.hibernate.data.LexicalRelationType;

public class RelationTypeDAO extends HibernateGenericDAO<LexicalRelationType, Long> {

	public RelationTypeDAO () {
		super(LexicalRelationType.class);
	}

}

