package ak.maven;

import java.util.*;

/**
* This class represents the Venue entity, mainly the seats available, held and reserved
* It owns data for seat holds, reservations, and provides functions to get/set that data
* It also hosts some configurations, such as messages to end user, hold timeout etc.
* Most methods are getters/setters
*/
public final class Venue {	
	 
    private int noOfSeats;
    private Seat[] seatsChart;
    private ArrayList<SeatHold> seatHolds;
    private ArrayList<Integer> expiredSeatHoldIDs; 
    private ArrayList<Reservation> reservations;
    private final int holdTimeoutInSeconds = 45; //in seconds
    private final String reservationSuccessMessage = "Reservation completed successfully!!";
    private final String notEnoughSeatsAvailableMessage = "Sorry, we don't have as many seats available as you have requested";
    private final String holdNotFoundMessage = "Couldn't find a hold with the hold ID provided";
    private final String holdExpiredMessage = "Sorry, your hold has expired. Please initiate a new reservation.";
    
    
    /**
     * Constructor 
     * @param noOfSeats No. of seats for the venue
     */
    public Venue(int noOfSeats) {    	
		this.seatsChart = new Seat[noOfSeats];
		seatHolds = new ArrayList<SeatHold>();
		reservations = new ArrayList<Reservation>();
		expiredSeatHoldIDs = new ArrayList<Integer>();
		
		for(int i = 0; i < noOfSeats; i++)
			this.seatsChart[i] = new Seat(i+1); //Seat number = 1 for array index 0
    }
    
    /**
     * Finds the SeatHold object corresponding to the Seat Hold ID, if one exists.
     * @param seatHoldID Seat Hold ID
     * @return Optional SeatHold object 
     */
    public Optional<SeatHold> getSeatHoldByID(int seatHoldID){		
		return seatHolds.stream()
				.filter(s -> s.getSeatHoldID() == seatHoldID)
				.findFirst();
	}
    
    /**
     * Adds the SeatHold object to a collection of SeatHold objects
     * @param seatHold SeatHold ID
     */
    public void addHold(SeatHold seatHold) {
		this.seatHolds.add(seatHold);
	}
	
	/**
	 * Removes the SeatHold object from a collection of SeatHol objects
	 * @param seatHold SeatHold ID
	 */
	public void removeHold(SeatHold seatHold){
		this.seatHolds.remove(seatHold);
	}
	
	/**
	 * Adds SeatHold IDs of expired holds to a collection
	 * @param seatHold SeatHold ID
	 */
	public void markHoldExpired(int seatHoldID) {
		expiredSeatHoldIDs.add(seatHoldID);
	}
	
	/**
	 * Adds the Reservation object to a collection of Reservation objects
	 * @param reservation Reservation object
	 */
	public void addReservation(Reservation reservation){
		this.reservations.add(reservation);
	}
	

	/**
	 * Getter methods follow
	 */
	
	public int getNoOfSeats() {
		return noOfSeats;
	}
	
	public Seat[] getSeatsChart() {
		return seatsChart;
	}
	
	public ArrayList<SeatHold> getSeatHolds() {
		return seatHolds;
	}
	
	public ArrayList<Integer> getExpiredSeatHoldIDs() {
		return expiredSeatHoldIDs;
	}	

	public ArrayList<Reservation> getReservations() {
		return reservations;
	}	  
	
	public int getHoldTimeout() {
		return holdTimeoutInSeconds;
	}
	
	public String getReservationSuccessMessage() {
		return reservationSuccessMessage;
	}

	public String getNotEnoughSeatsAvailableMessage() {
		return notEnoughSeatsAvailableMessage;
	}

	public String getHoldNotFoundMessage() {
		return holdNotFoundMessage;
	}

	public String getHoldExpiredMessage() {
		return holdExpiredMessage;
	}
}