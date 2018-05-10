package ak.maven;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * This class contains junit test cases to test functionality mainly of AppRunner.java 
 */
public class AppRunnerTest {
	
	@Test
	public void isPositiveInteger_PositiveInteger_ReturnsTrue() {
		assertTrue(AppRunner.isPositiveInteger("12"));		
	}
	
	@Test
	public void isPositiveInteger_PaddedPositiveInteger_ReturnsTrue() {
		assertTrue(AppRunner.isPositiveInteger("003"));		
	}
	
	@Test
	public void isPositiveInteger_NegativeInteger_ReturnsFalse() {
		assertFalse(AppRunner.isPositiveInteger("-3"));		
	}
	
	@Test
	public void isPositiveInteger_NonIntegerString_ReturnsFalse() {
		assertFalse(AppRunner.isPositiveInteger("abcs"));
	}
}
