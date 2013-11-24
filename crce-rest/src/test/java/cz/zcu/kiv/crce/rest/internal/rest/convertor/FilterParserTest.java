package cz.zcu.kiv.crce.rest.internal.rest.convertor;

import cz.zcu.kiv.crce.rest.internal.convertor.FilterParser;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;


/**
 * 
 * Test parser of requirement filter, that is part of {@link ConvertorToBeans}.
 * @author Jan Reznicek
 *
 */
public class FilterParserTest {
	
	private static final String FILTER1 = "(&(package=cz.zcu.kiv.example)(version>=2.0.1))";
	
	private static final String FILTER2 = "(&(osgi.wiring.package=cz.zcu.kiv.obcc.example.carpark.arrivals)(version&gt;1.0.0))";
	
	private static final String FILTER3 = "(&amp;(osgi.wiring.package=cz.zcu.kiv.obcc.example.container)(version&gt;=1.0.0.RELEASE)(version&lt;=1.0.0.RELEASE))";
	
	
	
	@Test
	public void testFilterParser1() {		
		
		try {
			FilterParser filterParser = new FilterParser();			
			
			String[] result =  filterParser.parseFilter(FILTER1);
			
			 assertTrue("Result lenght",result.length == 3);
			 assertTrue("Name","cz.zcu.kiv.example".equals(result[0]));
			 assertTrue("Version","2.0.1".equals(result[1]));
			 assertTrue("Operation","greater-equal".equals(result[2]));

		} catch (SecurityException e) {
			e.printStackTrace();
			fail();;
		} 
 
	}

	@Test
	public void testFilterParser2() {		

		try {
			FilterParser filterParser = new FilterParser();			
			
			String[] result =  filterParser.parseFilter(FILTER2);			
			
			 assertTrue("Result lenght",result.length == 3);
			 assertTrue("Name","cz.zcu.kiv.obcc.example.carpark.arrivals".equals(result[0]));
			 assertTrue("Version","1.0.0".equals(result[1]));
			 assertTrue("Operation","greater-than".equals(result[2]));

		} catch (SecurityException e) {
			e.printStackTrace();
			fail();

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail();
		}

 
	}
	
	@Test
	public void testFilterParser3() {		
		
		try {
			FilterParser filterParser = new FilterParser();			
			
			String[] result =  filterParser.parseFilter(FILTER3);				
			
			 assertTrue("Result lenght",result.length == 5);
			 assertTrue("Name","cz.zcu.kiv.obcc.example.container".equals(result[0]));
			 assertTrue("Version","1.0.0.RELEASE".equals(result[1]));
			 assertTrue("Operation","greater-equal".equals(result[2])); 
			 assertTrue("Version2","1.0.0.RELEASE".equals(result[3]));
			 assertTrue("Operation2","less-equal".equals(result[4]));

		} catch (SecurityException e) {
			e.printStackTrace();
			fail();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail();
		}

 
	}
}
