package ak.maven;

import java.time.LocalDateTime;
import java.util.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;

public class TicketServiceTest {
	
	private TicketServiceImplementor tsinstance;

	@Before
	public void Initialize() {		
		tsinstance = new TicketServiceImplementor(new Venue(50));
	}
	
	@After
    public void tearDown() {
		tsinstance = null;
    }	
	
	@Test
	public void numSeatsAvailable_testInitialSetup() {		
		assertEquals(50, tsinstance.numSeatsAvailable());		
	}
	
	@Test
	public void numSeatsAvailable_testAfterAHold() {	
		tsinstance.findAndHoldSeats(10, "someemail");
		assertEquals(40, tsinstance.numSeatsAvailable());		
	}
	
	@Test
	public void numSeatsAvailable_testAfterAHoldAndAReservation() {	
		tsinstance.findAndHoldSeats(10, "someemail");
		tsinstance.findAndHoldSeats(10, "someemail");
		tsinstance.reserveSeats(1, "someemail");
		assertEquals(30, tsinstance.numSeatsAvailable());				
	}
	
	@Test
	public void numSeatsAvailable_NotEnoughSeatsAvailable() {		
		SeatHold s = tsinstance.findAndHoldSeats(60, "someemail");
		assertNull(s);
	}
	
	@Test
	public void findAndHoldSeats_testSuccessfulHold() {		
		SeatHold s = tsinstance.findAndHoldSeats(20, "someemail");
		assertNotNull(s);
	}
	
	@Test
	public void findAndHoldSeats_testHoldSize() {		
		SeatHold s = tsinstance.findAndHoldSeats(10, "someemail");
		assertEquals(10, s.getSeatIDsHeld().size());		
	}	
	
	@Test
	public void findAndHoldSeats_testHoldID() {		
		SeatHold s = tsinstance.findAndHoldSeats(10, "someemail");
		assertEquals(1, s.getSeatHoldID());		
	}
	
	@Test
	public void findAndHoldSeats_testHoldIDAfterMultipleHolds() {
		SeatHold s = null;
		for(int i=0; i<4; i++)
		{
			s = tsinstance.findAndHoldSeats(i, "someemail");
		}		
		assertEquals(4, s.getSeatHoldID());		
	}
	
	@Test
	public void findAndHoldSeats_testHoldSeatIDs() {		
		SeatHold s = tsinstance.findAndHoldSeats(5, "someemail");
		List<Integer> a = Arrays.asList(1, 2, 3, 4, 5);  
		assertEquals(a, s.getSeatIDsHeld()); 		
	}
	
	@Test
	public void findAndHoldSeats_testSeatSuperiority() {
		SeatHold s1 = tsinstance.findAndHoldSeats(5, "someemail");
		SeatHold s2 = tsinstance.findAndHoldSeats(5, "someemail");
		assert(s1.getSeatIDsHeld().get(s1.getSeatIDsHeld().size()-1) < s2.getSeatIDsHeld().get(0));
	}
	
	@Test
	public void reserveSeats_testSuccessfulReservation() {
		tsinstance.findAndHoldSeats(10, "someemail");
		String result =  tsinstance.reserveSeats(1, "someemail");
		assertEquals("Reservation completed successfully!!", result);
	}
	
	@Test
	public void reserveSeats_InvalidHoldID() {
		tsinstance.findAndHoldSeats(10, "someemail");
		String result =  tsinstance.reserveSeats(101, "someemail");
		assertEquals("Couldn't find a hold with the hold ID provided", result);
	}
	
	@Test
	public void reserveSeats_testDelayedHold() {
		tsinstance.getVenue().addHold(new SeatHold(1, new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5)), "someemail", LocalDateTime.now().minusSeconds(15)));
		String result =  tsinstance.reserveSeats(1, "someemail");
		assertEquals("Reservation completed successfully!!", result);
	}
	
	@Test
	public void reserveSeats_testExpiredHold() {
		tsinstance.getVenue().addHold(new SeatHold(1, new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5)), "someemail", LocalDateTime.now().minusSeconds(51)));
		String result =  tsinstance.reserveSeats(1, "someemail");
		assertEquals("Your hold has expired. Please initiate a new reservation", result);
	}	
}
