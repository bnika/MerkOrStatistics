package hibernate;


import static org.junit.Assert.*;

import java.util.List;

import is.merkor.statistics.hibernate.daos.RelationCountDAO;
import is.merkor.statistics.hibernate.data.LexicalRelationCount;

import org.junit.Before;
import org.junit.Test;

public class RelationCountDAOTest {
	RelationCountDAO dao = new RelationCountDAO();
	
	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void findByLimitTest() {
		int offset = 0;
		int limit = 100;
		
		List<LexicalRelationCount> list = dao.findAllByLimit(offset, limit);
		assertTrue(list.size() == limit);
	}
	
	@Test
	public void findByIdTest() {
		List<LexicalRelationCount> list = dao.findByFromId(11770L);
//		for (LexicalRelationCount c : list) {
//			System.out.println(c.toString());
//		}
//		System.out.println("nr. of relations: " + list.size());
		
		assertTrue(list.size() == 2547);
	}

}
