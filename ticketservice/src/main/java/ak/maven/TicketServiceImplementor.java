package ak.maven;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class TicketServiceImplementor implements TicketService{
	
	private ArrayList<Integer> IDsGenerated;
	@Inject private final Venue venue;
	
	@Inject
	public TicketServiceImplementor(Venue venue){
		this.venue = venue;
		IDsGenerated = new ArrayList<Integer>();
	}
	
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
	
	public boolean isHoldExpired(int seatHoldId) {
		
		System.out.println("Expired seat IDs at this stage:");
		venue.getExpiredSeatHoldIDs().stream().forEach(System.out::println);
		
		checkAndRemoveExpiredHolds();
		
		System.out.println("Expired seat IDs at this stage:");
		
		venue.getExpiredSeatHoldIDs().stream().forEach(System.out::println);
		
		if(venue.getExpiredSeatHoldIDs().contains(seatHoldId)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean isValidHoldID(int seatHoldId) {		
		
		Optional<SeatHold> s = venue.getSeatHoldByID(seatHoldId);
		
		if(!s.isPresent())
			return false;
		else {
			return true;
		}
	}
	
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
	
	private int generateID() {
		
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
