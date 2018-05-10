package ak.maven;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * This class contains junit test cases to test functionality mainly of HelperMethod.java 
 * The test method names follow a pattern: methodNameBeingTested_Condition_ExpectedOutcome
 */
public class HelperMethodsTest {
	
	@Test
	public void isPositiveInteger_PositiveInteger_ReturnsTrue() {
		assertTrue(HelperMethods.isPositiveInteger("12"));
	}
	
	@Test
	public void isPositiveInteger_PaddedPositiveInteger_ReturnsTrue() {
		assertTrue(HelperMethods.isPositiveInteger("003"));
	}
	
	@Test
	public void isPositiveInteger_NegativeInteger_ReturnsFalse() {
		assertFalse(HelperMethods.isPositiveInteger("-3"));
	}
	
	@Test
	public void isPositiveInteger_NonIntegerString_ReturnsFalse() {
		assertFalse(HelperMethods.isPositiveInteger("abcs"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void generateID_InvalidLengthRequested_ThrowsException() {
		HelperMethods.generateID(0);
	}
	
	@Test
	public void generateID_5IDsGenerated_AllAreOfRequiredLength() {
		
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();		
		for(int i = 5; i < 10; i++){
			map.put(HelperMethods.generateID(i), i);
		}
		
		assertEquals(0, map.entrySet().stream().filter(x -> String.valueOf(x.getKey()).length() != x.getValue()).count());	
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void generateRandomString_InvalidLengthRequested_ThrowsException() {
		HelperMethods.generateRandomString(0);
	}
	
	@Test
	public void generateRandomString_10CodesGenerated_AllReturnStringOfRequestedLength() {		
		Map<String, Integer> map = new HashMap<String, Integer>();
		for(int i = 5; i < 14; i++){
			map.put(HelperMethods.generateRandomString(i), i);	
		}
		
		assertEquals(0, map.entrySet().stream().filter(x -> x.getKey().length() != x.getValue()).count());				
	}	
}
