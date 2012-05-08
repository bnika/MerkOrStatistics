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
		//PairType constructor: PairType(jointFreq, freq1, freq2, N)
		obj_1 = new PairType(3, 4, 4, 10);
		obj_2 = new PairType(0, 4, 4, 100);
	}

	@Test
	public void testComputeMI() {
		double mi = MutualInformation.computeMI(obj_1);
		assertTrue(mi > 0.27 && mi < 0.28);
		//System.out.println(mi);
		
		mi = MutualInformation.computeMI(obj_2);
		assertTrue(mi == 0.0);
	}

	@Test
	public void testComputeLMI() {
		double mi = MutualInformation.computeLMI(obj_1);
		assertTrue(mi > 0.81 && mi < 0.82);
		//System.out.println(mi);
		mi = MutualInformation.computeLMI(obj_2);
		assertTrue(mi == 0.0);
	}

}
