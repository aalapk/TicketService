package ak.maven;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
* This is the central class for the application, and implements critical functionality for holding and reserving seats
* It provides methods for checking available seats and expired holds, in addition to committing holds and reservations
* Dependency Injection is used by providing an instance of Venue class to the constructor of this class, making it extensible to other Venue types/instances
*/
public class TicketServiceImplementor implements TicketService{
	
	private ArrayList<Integer> IDsGenerated;
	@Inject private final Venue venue;
	
	/**
	 * constructor
	 * @param venue: instance of Venue class, injected
	 */
	@Inject
	public TicketServiceImplementor(Venue venue){
		this.venue = venue;
		IDsGenerated = new ArrayList<Integer>();
	}
	
	/**
	 * Checks for 'expired' holds and mark them as such
	 */
	public void checkAndRemoveExpiredHolds() {
		
		ArrayList<SeatHold> holdsToBeRemoved = new ArrayList<SeatHold>();
		
		for(SeatHold s : venue.getSeatHolds()) {
			
			long difference = Duration.between(s.getHoldTimestamp(), LocalDateTime.now()).getSeconds();   // time since hold request	
			
			if(difference > venue.getHoldTimeout()) {   //hold is expired	
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
	 * Returns null if no seats are available
	 * Generates a unique 6-digit positive number and uses it as its ID (which is then needed to reserve the held seats)
	 * @param numSeats: number of seats requested to be held
	 * @param customerEmail: email address of the person making the request
	 * @return a SeatHold object that represents the seat hold
	 */
	public SeatHold findAndHoldSeats(int numSeats, String customerEmail) {		
		
		checkAndRemoveExpiredHolds();	
		
		if (numSeatsAvailable() < numSeats) {
			return null;
		}
		else{
			SeatHold hold = new SeatHold(generateID(), (ArrayList<Integer>) getNextBestSeatNumbers(numSeats), customerEmail, LocalDateTime.now());
			venue.addHold(hold);
			return hold;
		}
	}

	
	/**
	 * Reserves seats contained in a SeatHold
	 * Ensures that the Seat Hold isn't expired 
	 * Generates a unique 8-digit alphanumeric confirmation code
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
			venue.removeHold(s.get()); //remove hold, whether or not it's valid (since reserving seats should remove the hold too)
			
			long difference = Duration.between(s.get().getHoldTimestamp(), LocalDateTime.now()).getSeconds(); //time between hold and reservation request		
			
			if(difference > venue.getHoldTimeout()) { 			
				return venue.getHoldExpiredMessage();
			}
		
			else {
				RandomString gen = new RandomString(8, ThreadLocalRandom.current());
				String confirmationCode = gen.nextString();
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
	 * @return a list of integer values represneting seat numbers
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
	
	/**
	 * Generates an 8-digit random positive integer to be used as an ID for SeatHold
	 * Keeps track of previously generated IDs to ensure the next one is unique 
	 * @return a unique (not used previously) 8-digit random positive integer
	 */
	int generateID() {
		
		boolean isIDUnique = false;
		int n = 0;
		
		while(!isIDUnique) {
			Random rnd = new Random();
			n = 100000 + rnd.nextInt(900000);
			if(!IDsGenerated.contains(n)) {
				isIDUnique = true;
			}
		}	
		
		IDsGenerated.add(n);
		return n;
	}
	
	public Venue getVenue() {
		return this.venue;
	}	
}
