package ak.maven;

import java.util.*;

public final class Venue {	
	 
    private int noOfSeats;
    private Seat[] seatsChart;
    private ArrayList<SeatHold> seatHolds;
    private ArrayList<Integer> expiredSeatHoldIDs; 
    private ArrayList<Reservation> reservations;
    private final int holdTimeoutInSeconds = 30; //in seconds
    private final String reservationSuccessMessage = "Reservation completed successfully!!";
    private final String notEnoughSeatsAvailableMessage = "Sorry, we don't have as many seats available as you have requested";
    private final String holdNotFoundMessage = "Couldn't find a hold with the hold ID provided";
    private final String holdExpiredMessage = "Sorry, your hold has expired. Please initiate a new reservation.";
    
    
    public Venue(int noOfSeats) {    	
		this.seatsChart = new Seat[noOfSeats];
		seatHolds = new ArrayList<SeatHold>();
		reservations = new ArrayList<Reservation>();
		expiredSeatHoldIDs = new ArrayList<Integer>();
		
		for(int i = 0; i < noOfSeats; i++)
			this.seatsChart[i] = new Seat(i+1); //Seat number = 1 for array index 0
    }   

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
	
	public Optional<SeatHold> getSeatHoldByID(int seatHoldID){
		
		return seatHolds.stream()
				.filter(s -> s.getSeatHoldID() == seatHoldID)
				.findFirst();		
	}
	
	public void addHold(SeatHold seatHold) {
		this.seatHolds.add(seatHold);
	}
	
	public void removeHold(SeatHold seatHold){
		this.seatHolds.remove(seatHold);
	}
	
	public void markHoldExpired(int seatHoldID) {
		expiredSeatHoldIDs.add(seatHoldID);
	}

	public ArrayList<Reservation> getReservations() {
		return reservations;
	}
	
	public void addReservation(Reservation r){
		this.reservations.add(r);
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