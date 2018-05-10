package ak.maven;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
* This is the central class for the application, and implements critical functionality for holding and reserving seats
* It implements methods for checking available seats and expired holds, in addition to committing holds and reservations
* Dependency Injection is used by providing an instance of Venue class to the constructor of this class, making it extensible to other Venue types
*/
public class TicketServiceImplementor implements TicketService {
	
	private ArrayList<Integer> IDsGenerated;
	private ArrayList<String> confirmationCodesGenerated;
	
	@Inject private final Venue venue;
	
	/**
	 * constructor
	 * @param venue: instance of Venue class, injected
	 */
	@Inject
	public TicketServiceImplementor(Venue venue) {
		this.venue = venue;
		IDsGenerated = new ArrayList<Integer>();
		confirmationCodesGenerated = new ArrayList<String>();
	}
	
	/**
	 * Checks for 'expired' holds and mark them as such
	 * Frees up seats held against them
	 */
	public void checkAndRemoveExpiredHolds() {
		
		ArrayList<SeatHold> holdsToBeRemoved = new ArrayList<SeatHold>();
		
		for(SeatHold s : venue.getSeatHolds()) {
			
			long difference = Duration.between(s.getHoldTimestamp(), LocalDateTime.now()).getSeconds();   // time since hold request	
			
			if(difference > venue.getHoldTimeout()) {
				holdsToBeRemoved.add(s);	
			}
		}
		
		for (SeatHold s : holdsToBeRemoved) {
			venue.markHoldExpired(s.getSeatHoldID());
			venue.getSeatHolds().remove(s);
		}
	}

	/**
	 * Gets the number of 'Available' seats, which is all seats that are not held or reserved	
	 * @return the number of seats available 
	 */
	public int numSeatsAvailable() {
		
		checkAndRemoveExpiredHolds();
		Set<Integer> heldSeatIDs = venue.getSeatHolds().stream().map(s -> s.getSeatIDsHeld()).flatMap(Collection::stream).collect(Collectors.toSet());
		Set<Integer> reservedSeatIDs = venue.getReservations().stream().map(s -> s.getSeatIDsReserved()).flatMap(Collection::stream).collect(Collectors.toSet());
		
		return (int) Arrays.stream(venue.getSeatsChart())
				.map(s -> s.getSeatNumber())
				.filter(s -> !heldSeatIDs.contains(s))
				.filter(s -> !reservedSeatIDs.contains(s))
				.count();
	}
	
	/**
	 * Find next best seats (by calling a method for that), and creates a seat hold
	 * Calls method 'getNextBestSeatNumbers' to get next best seats
	 * Returns null if no seats are available
	 * Generates a unique 6-digit positive number and uses it as its ID (which is needed to reserve the held seats)
	 * @param numSeats: number of seats requested to be held
	 * @param customerEmail: email address of the person making the request
	 * @return a SeatHold object that represents the seat hold
	 */
	public SeatHold findAndHoldSeats(int numSeats, String customerEmail) {
		
		checkAndRemoveExpiredHolds();
		
		if (numSeatsAvailable() < numSeats || numSeats == 0) {
			return null;
		}
		
		else {
			
			int randomSeatHoldID = 0;
			boolean isIDUnique = false;
			
			/*
			 * This is done to handle a rare scenario where a random number generated was generated and used previously.
			 * In such a case, the application will keep fetching new random numbers, until it finds one not used previously
			 */  
			while(!isIDUnique) {
				randomSeatHoldID = HelperMethods.generateID(venue.getLengthOfSeatHoldID());				
				if(!IDsGenerated.contains(randomSeatHoldID)) {
					isIDUnique = true;
				}
			}
			
			SeatHold hold = new SeatHold(randomSeatHoldID, (ArrayList<Integer>) getNextBestSeatNumbers(numSeats), customerEmail, LocalDateTime.now());
			venue.addHold(hold);
			return hold;
		}
	}
	
	/**
	 * Reserves seats contained in a SeatHold object
	 * Ensures that the Seat Hold isn't expired 
	 * Generates a unique alphanumeric confirmation code, of preconfigured length
	 * @param seatHoldId: ID of the SeatHold object
	 * @param customerEmail: email address of the person making the request
	 * @return a string confirmation code for successful reservation, or appropriate error message otherwise
	 */
	public String reserveSeats(int seatHoldId, String customerEmail) {
		
		checkAndRemoveExpiredHolds();
		
		if(isHoldExpired(seatHoldId)) {
			return venue.getHoldExpiredMessage();
		}
		
		else if(!isValidHoldID(seatHoldId)){
			return venue.getHoldNotFoundMessage();
		}
		
		else {
			Optional<SeatHold> s = venue.getSeatHoldByID(seatHoldId);
			venue.removeHold(s.get()); //remove hold, whether or not it's valid, since reserving seats should remove the hold too
			
			long difference = Duration.between(s.get().getHoldTimestamp(), LocalDateTime.now()).getSeconds();
			
			if(difference > venue.getHoldTimeout()) {
				return venue.getHoldExpiredMessage();
			}
		
			else {
				
				String confirmationCode = null;
				Boolean randomCodeFound = false;
				
				/*
				 * This is done to handle a rare scenario where a random string generated was generated and used previously.
				 * In such a case, the application will keep fetching new random strings, until it finds one not used previously
				 */
				while(!randomCodeFound) {
					confirmationCode = HelperMethods.generateRandomString(venue.getLengthOfReservationConfirmationCode());
					if(!confirmationCodesGenerated.contains(confirmationCode)) {
						randomCodeFound = true;
						confirmationCodesGenerated.add(confirmationCode);
					}
				}
				
				venue.addReservation(new Reservation(confirmationCode, s.get().getSeatIDsHeld(), s.get().getCustomerEmail(), LocalDateTime.now()));
				
				return confirmationCode;
			}
		}
	}
	
	/**
	 * Checks if provided SeatHold is expired. Calls the 'checkAndRemoveExpiredHolds()' method first, which detects and marks expired holds invalid
	 * @param seatHoldId ID for SeatHold object
	 * @return true if the hold is expired, false otherwise
	 */
	public boolean isHoldExpired(int seatHoldId) {		
		
		checkAndRemoveExpiredHolds();
		
		if(venue.getExpiredSeatHoldIDs().contains(seatHoldId)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Checks if provided SeatHold ID is valid (i.e. a SeatHold exists for it)
	 * @param seatHoldId ID for SeatHold object
	 * @return true if the SeatHold ID is valid, false otherwise
	 */
	public boolean isValidHoldID(int seatHoldId) {
		
		Optional<SeatHold> s = venue.getSeatHoldByID(seatHoldId);
		
		if(!s.isPresent())
			return false;
		else {
			return true;
		}
	}
	
	/**
	 * Finds and returns next best N seat numbers. Employs a simple greedy logic to get next available seats from a list of seats
	 * Called internally by method findAndHoldSeats. Not available for public use.
	 * @param numSeats Number of seats requested for hold
	 * @return a list of integer values representing seat numbers
	 */
	private List<Integer> getNextBestSeatNumbers(int numSeats){
		
		Set<Integer> heldSeatIDs = venue.getSeatHolds().stream().map(s -> s.getSeatIDsHeld()).flatMap(Collection::stream).collect(Collectors.toSet());
		Set<Integer> reservedSeatIDs = venue.getReservations().stream().map(s -> s.getSeatIDsReserved()).flatMap(Collection::stream).collect(Collectors.toSet());
		
		return  Arrays.stream(venue.getSeatsChart())
				.map(s -> s.getSeatNumber())
				.filter(s -> !heldSeatIDs.contains(s))
				.filter(s -> !reservedSeatIDs.contains(s))
				.limit(numSeats)
				.collect(Collectors.toList());
	}
	
	public Venue getVenue() {
		return this.venue;
	}
}
