package hibernate;


import static org.junit.Assert.*;

import java.util.List;

import is.merkor.statistics.hibernate.daos.LexicalItemDAO;
import is.merkor.statistics.hibernate.data.LexicalItem;

import org.junit.Before;
import org.junit.Test;

public class LexicalItemDAOTest {
	
	LexicalItemDAO dao;

	@Before
	public void setUp() throws Exception {
		dao = new LexicalItemDAO();
	}
	
	@Test
	public void findByIdTest() {
		LexicalItem item = dao.findById(7893L);
		//System.out.println(item);
	}
	@Test
	public void findByLexIdTest() {
		List<LexicalItem> itList = dao.findByLexId(47722);
		//System.out.println(itList.toString());
	}
	@Test
	public void findByLemmaTest() {
		List<LexicalItem> itList = dao.findByLemma("dálæti");
		//System.out.println(itList.toString());
	}
	@Test
	public void findCombinedTest() {
		LexicalItem item = dao.findById(7893L);
		List<LexicalItem> itList_1 = dao.findByLexId(47722);
		List<LexicalItem> itList_2 = dao.findByLemma("dálæti");
		assertTrue(item.equals(itList_1.get(0)));
		assertTrue(item.equals(itList_2.get(0)));
	}

}
