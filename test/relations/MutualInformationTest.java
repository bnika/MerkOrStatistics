package relations;

import static org.junit.Assert.*;
import is.merkor.statistics.relations.PairType;
import is.merkor.statistics.relations.MutualInformation;

import org.junit.Before;
import org.junit.Test;

public class MutualInformationTest {
	PairType obj_1;
	PairType obj_2;
	@Before
	public void setUp() throws Exception {
		obj_1 = new PairType(2.0, 4.0, 4.0, (long) 100.0);
		obj_2 = new PairType(0.0, 4.0, 4.0, (long) 100.0);
	}

	@Test
	public void testComputeMI() {
		double mi = MutualInformation.computeMI(obj_1);
		assertTrue(mi > 0.74);
		
		mi = MutualInformation.computeMI(obj_2);
		assertTrue(mi == 0.0);
	}

	@Test
	public void testComputeLMI() {
		double mi = MutualInformation.computeLMI(obj_1);
		assertTrue(mi > 1.4);
		
		mi = MutualInformation.computeLMI(obj_2);
		assertTrue(mi == 0.0);
	}

}
