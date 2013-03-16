package cz.zcu.kiv.crce.rest.internal.rest.convertor;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;


/**
 * 
 * Test parser of requirement filter, that is part of {@link ConvertorToBeans}.
 * @author Jan Reznicek
 *
 */
public class FilterParserTest {
	
	private static final String FILTER1 = "(&(package=cz.zcu.kiv.example)(version>=2.0.1))";
	
	private static final String FILTER2 = "(&(osgi.wiring.package=cz.zcu.kiv.obcc.example.carpark.arrivals)(version&gt;=1.0.0))";
	
	private static final String FILTER3 = "(&amp;(osgi.wiring.package=cz.zcu.kiv.obcc.example.container)(version&gt;=1.0.0.RELEASE)(version&lt;=1.0.0.RELEASE))";
	
	
	
	@Test
	public void testFilterParser1() {		
		
		try {
			ConvertorToBeans conv = new ConvertorToBeans();
			Method method = ConvertorToBeans.class.getDeclaredMethod("parseFilter", String.class);
			method.setAccessible(true);
			String[] result =  (String[])method.invoke(conv, FILTER1);
			
			 assertTrue("Result lenght",result.length == 3);
			 assertTrue("Name","cz.zcu.kiv.example".equals(result[0]));
			 assertTrue("Version","2.0.1".equals(result[1]));
			 assertTrue("Operation","greater-than".equals(result[2]));
		} catch (NoSuchMethodException e) {			
			e.printStackTrace();
			fail();
		} catch (SecurityException e) {
			e.printStackTrace();
			fail();
		} catch (IllegalAccessException e) {			
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			fail();
		} 
 
	}

	@Test
	public void testFilterParser2() {		

		try {
			ConvertorToBeans conv = new ConvertorToBeans();
			Method method = ConvertorToBeans.class.getDeclaredMethod("parseFilter", String.class);
			method.setAccessible(true);
			String[] result =  (String[])method.invoke(conv, FILTER2);
			
			
			 assertTrue("Result lenght",result.length == 3);
			 assertTrue("Name","cz.zcu.kiv.obcc.example.carpark.arrivals".equals(result[0]));
			 assertTrue("Version","1.0.0".equals(result[1]));
			 assertTrue("Operation","greater-than".equals(result[2]));
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			fail();
		} catch (SecurityException e) {
			e.printStackTrace();
			fail();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			fail();
		} 
 
	}
	
	@Test
	public void testFilterParser3() {		
		
		try {
			ConvertorToBeans conv = new ConvertorToBeans();
			Method method = ConvertorToBeans.class.getDeclaredMethod("parseFilter", String.class);
			method.setAccessible(true);
			String[] result =  (String[])method.invoke(conv, FILTER3);
			
			
			 assertTrue("Result lenght",result.length == 5);
			 assertTrue("Name","cz.zcu.kiv.obcc.example.container".equals(result[0]));
			 assertTrue("Version","1.0.0.RELEASE".equals(result[1]));
			 assertTrue("Operation","greater-than".equals(result[2])); 
			 assertTrue("Version2","1.0.0.RELEASE".equals(result[3]));
			 assertTrue("Operation2","lower-than".equals(result[4]));
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			fail();
		} catch (SecurityException e) {
			e.printStackTrace();
			fail();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			fail();
		} 
 
	}
}
