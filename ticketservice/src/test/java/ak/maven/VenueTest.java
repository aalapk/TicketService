package ak.maven;

import java.time.LocalDateTime;
import java.util.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;

/**
 * This class contains junit test cases to test functionality mainly of Venue.java
 * @Before is used to initialize an instance of TicketServiceImplementor.java before ever test case, and using @After, it's set to null after each test
 */
public class VenueTest {
	
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
	public void getSeatHoldByID_IDIsValid_ReturnSeatHold() {
		tsinstance.getVenue().addHold(new SeatHold(123456, new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5)), "someemail", LocalDateTime.now()));
		Optional<SeatHold> s = tsinstance.getVenue().getSeatHoldByID(123456);
		assertTrue(s.isPresent()); 
	}
	
	@Test
	public void getSeatHoldByID_IDIsInvalid_IsNull() {
		tsinstance.getVenue().addHold(new SeatHold(123456, new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5)), "someemail", LocalDateTime.now()));
		Optional<SeatHold> s = tsinstance.getVenue().getSeatHoldByID(654321);
		assertFalse(s.isPresent()); 
	}
	
	@Test
	public void addHold_ValidHold_HoldAdded() {
		tsinstance.getVenue().addHold(new SeatHold(123456, new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5)), "someemail", LocalDateTime.now()));
		assertEquals(1, tsinstance.getVenue().getSeatHolds().size());			
	}
	
	@Test
	public void removeHold_ValidHoldRemoval_HoldRemoved() {
		tsinstance.getVenue().addHold(new SeatHold(111111, new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5)), "someemail", LocalDateTime.now()));
		tsinstance.getVenue().addHold(new SeatHold(222222, new ArrayList<Integer>(Arrays.asList(6, 7)), "someemail", LocalDateTime.now()));		
		tsinstance.getVenue().removeHold(tsinstance.getVenue().getSeatHoldByID(222222).get());
		assertEquals(1, tsinstance.getVenue().getSeatHolds().size());
	}
	
	@Test
	public void markHoldExpired_HoldIsExpired_HoldMarkedExpired() {
		tsinstance.getVenue().addHold(new SeatHold(123456, new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5)), "someemail", LocalDateTime.now().minusSeconds(51)));
		tsinstance.getVenue().markHoldExpired(123456);
		assertEquals(1, tsinstance.getVenue().getExpiredSeatHoldIDs().size());		
	}
	
	@Test
	public void addReservation_ValidReservation_ReservationAdded() {		
		tsinstance.getVenue().addReservation(new Reservation("1asdgdw34", new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5)), "someemail", LocalDateTime.now()));
		assertEquals(1, tsinstance.getVenue().getReservations().size());
	}	
}
