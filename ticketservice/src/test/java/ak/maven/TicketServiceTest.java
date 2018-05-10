package ak.maven;

import java.time.LocalDateTime;
import java.util.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;

/**
 * This class contains junit test cases to test functionality mainly of TicketServiceImplementor.java
 * The test method names follow a pattern: methodNameBeingTested_Condition_ExpectedOutcome
 * @Before is used to initialize an instance of TicketServiceImplementor.java before ever test case, and using @After, it's set to null after each test
 */
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
	public void numSeatsAvailable_InitialSetup_AllSeatsAvailable() {		
		assertEquals(tsinstance.getVenue().getSeatsChart().length, tsinstance.numSeatsAvailable());		
	}
	
	@Test
	public void numSeatsAvailable_HoldAdded_HeldSeatsNotAvailable() {	
		tsinstance.findAndHoldSeats(10, "someemail");
		assertEquals(tsinstance.getVenue().getSeatsChart().length - 10, tsinstance.numSeatsAvailable());		
	}
	
	@Test
	public void numSeatsAvailable_HoldAndReservationAdded_HeldAndReservedSeatsNotAvailable() {
		SeatHold s1 = tsinstance.findAndHoldSeats(10, "someemail");
		SeatHold s2 = tsinstance.findAndHoldSeats(10, "someemail");
		tsinstance.reserveSeats(s1.getSeatHoldID(), "someemail");
		assertEquals(tsinstance.getVenue().getSeatsChart().length - 20, tsinstance.numSeatsAvailable());				
	}
	
	@Test
	public void numSeatsAvailable_MoreSeatsRequestedThanAvailable_NotEnoughSeatsAvailable() {
		SeatHold s = tsinstance.findAndHoldSeats(60, "someemail");
		assertNull(s);
	}
	
	@Test
	public void numSeatsAvailable_HoldAddedAndMoreSeatsRequestedThanAvailable_NotEnoughSeatsAvailable() {	
		SeatHold s1 = tsinstance.findAndHoldSeats(20, "someemail");
		SeatHold s2 = tsinstance.findAndHoldSeats(tsinstance.getVenue().getSeatsChart().length - 10, "someemail");
		assertNull(s2);
	}
	
	@Test
	public void numSeatsAvailable_HoldExpired_ExpiredSeatsAvailable() {
		tsinstance.getVenue().addHold(new SeatHold(123456, new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5)), "someemail", LocalDateTime.now().minusSeconds(55))); //expired
		tsinstance.getVenue().addHold(new SeatHold(234567, new ArrayList<Integer>(Arrays.asList(6, 7, 8)), "someemail", LocalDateTime.now().minusSeconds(5))); //valid
		assertEquals(tsinstance.getVenue().getSeatsChart().length - 3, tsinstance.numSeatsAvailable());
	}
	
	@Test
	public void findAndHoldSeats_ValidHoldRequested_HoldCreated() {
		SeatHold s = tsinstance.findAndHoldSeats(20, "someemail");
		assertNotNull(s);
	}
	
	@Test
	public void findAndHoldSeats_HoldAdded_CorrectNumberOfSeatsHeld() {		
		SeatHold s = tsinstance.findAndHoldSeats(10, "someemail");
		assertEquals(10, s.getSeatIDsHeld().size());
	}
	
	@Test
	public void findAndHoldSeats_ZeroSeatsRequested_HoldIDNull() {		
		SeatHold s = tsinstance.findAndHoldSeats(0, "someemail");
		assertNull(s);
	}
	
	@Test
	public void findAndHoldSeats_HoldsAdded_HoldIDLengthsAsExpected() {		
		ArrayList<SeatHold> seatHolds = new ArrayList<SeatHold>();
		for(int i = 0; i < 10; i++) {
			seatHolds.add(tsinstance.findAndHoldSeats(2, "someemail"));
		}
		Boolean allIDLengthsValid = true;
		for(SeatHold s : seatHolds) {
			if(String.valueOf(s.getSeatHoldID()).length() != tsinstance.getVenue().getLengthOfSeatHoldID()) {
				allIDLengthsValid = false;
			}
		}
		assertTrue(allIDLengthsValid);
	}
	
	@Test
	public void findAndHoldSeats_HoldAdded_SeatIDsAsExpected() {		
		SeatHold s = tsinstance.findAndHoldSeats(5, "someemail");
		List<Integer> a = Arrays.asList(1, 2, 3, 4, 5);
		assertEquals(a, s.getSeatIDsHeld());
	}
	
	@Test
	public void findAndHoldSeats_MultipleHoldsAdded_SeatIDsAsExpected() {		
		SeatHold s1 = tsinstance.findAndHoldSeats(5, "someemail");
		SeatHold s2 = tsinstance.findAndHoldSeats(3, "someemail");
		List<Integer> a = Arrays.asList(6, 7, 8);
		assertEquals(a, s2.getSeatIDsHeld());	
	}
	
	@Test
	public void findAndHoldSeats_MultipleHoldsAdded_SeatNumbersWorsenWithHolds() {
		SeatHold s1 = tsinstance.findAndHoldSeats(5, "someemail");
		SeatHold s2 = tsinstance.findAndHoldSeats(5, "someemail");
		assert(s1.getSeatIDsHeld().get(s1.getSeatIDsHeld().size()-1) < s2.getSeatIDsHeld().get(0));
	}
	
	@Test
	public void reserveSeats_ReservationAdded_SuccessfulConfirmation() {
		SeatHold s = tsinstance.findAndHoldSeats(10, "someemail");
		String result =  tsinstance.reserveSeats(s.getSeatHoldID(), "someemail");
		assert(!result.equals(tsinstance.getVenue().getHoldExpiredMessage()) && !result.equals(tsinstance.getVenue().getHoldNotFoundMessage()));
	}
	
	@Test
	public void reserveSeats_InvalidHoldIDProvidedForReservation_CorrectErrorMessage() {
		tsinstance.findAndHoldSeats(10, "someemail");
		String result =  tsinstance.reserveSeats(101, "someemail");
		assertEquals(tsinstance.getVenue().getHoldNotFoundMessage(), result);
	}
	
	@Test
	public void reserveSeats_HoldDelayedButValid_SuccessfulReservation() {
		tsinstance.getVenue().addHold(new SeatHold(123456, new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5)), "someemail", LocalDateTime.now().minusSeconds(15)));
		String result =  tsinstance.reserveSeats(123456, "someemail");
		assert(!result.equals(tsinstance.getVenue().getHoldExpiredMessage()) && !result.equals(tsinstance.getVenue().getHoldNotFoundMessage()));
	}
	
	@Test
	public void reserveSeats_HoldExpired_NoReservationAndCorrectErrorMessage() {
		tsinstance.getVenue().addHold(new SeatHold(123456, new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5)), "someemail", LocalDateTime.now().minusSeconds(51)));
		String result =  tsinstance.reserveSeats(123456, "someemail");
		assertEquals(tsinstance.getVenue().getHoldExpiredMessage(), result);
	}
	
	@Test
	public void reserveSeats_HoldAddedAndReserved_SeatIDsOfHoldAndReservationMatch() {
		SeatHold s = tsinstance.findAndHoldSeats(10, "someemail");
		String result =  tsinstance.reserveSeats(s.getSeatHoldID(), "someemail");
		assertEquals(s.getSeatIDsHeld(),tsinstance.getVenue().getReservations().get(0).getSeatIDsReserved());
	}
	
	@Test
	public void reserveSeats_ReservationCreated_confirmationCodeLengthIsCorrect() {
		tsinstance.getVenue().addHold(new SeatHold(123456, new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5)), "someemail", LocalDateTime.now()));
		String result =  tsinstance.reserveSeats(123456, "someemail");
		assertEquals(tsinstance.getVenue().getLengthOfReservationConfirmationCode(), result.length());
	}
	
	@Test
	public void isHoldExpired_HoldValid_ReturnFalse() {
		tsinstance.getVenue().addHold(new SeatHold(123456, new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5)), "someemail", LocalDateTime.now().minusSeconds(5)));
		String result =  tsinstance.reserveSeats(123456, "someemail");
		assertFalse(tsinstance.isHoldExpired(123456));
	}
	
	@Test
	public void isValidHoldID_ValidHoldID_ReturnTrue() {
		tsinstance.getVenue().addHold(new SeatHold(123456, new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5)), "someemail", LocalDateTime.now().minusSeconds(5)));
		assertTrue(tsinstance.isValidHoldID(123456));
	}
	
	@Test
	public void isValidHoldID_InvalidHoldID_ReturnFalse() {
		tsinstance.getVenue().addHold(new SeatHold(123456, new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5)), "someemail", LocalDateTime.now().minusSeconds(5)));
		assertFalse(tsinstance.isValidHoldID(654321));
	}
	
	@Test
	public void	checkAndRemoveExpiredHolds_ExpiredHoldsExist_ExpiredHoldsRemoved() {
		tsinstance.getVenue().addHold(new SeatHold(123456, new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5)), "someemail", LocalDateTime.now().minusSeconds(5)));
		tsinstance.getVenue().addHold(new SeatHold(234567, new ArrayList<Integer>(Arrays.asList(6, 7, 8)), "someemail", LocalDateTime.now().minusSeconds(55)));
		tsinstance.checkAndRemoveExpiredHolds();
		assertEquals(1, tsinstance.getVenue().getExpiredSeatHoldIDs().size());
	}
}
