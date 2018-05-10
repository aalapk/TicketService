package ak.maven;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
* This class represents information about a reservation
* The Venue class maintains a collection of Reservation objects
*/
public class Reservation {
	
	private String reservationID;
	private ArrayList<Integer> seatIDsReserved;
	private String customerEmail;
	private LocalDateTime reservationTimestamp;
	
	/**
	 * Constructor
	 * @param reservationID ID generated for the reservation
	 * @param seatIDsReserved Seat Numbers reserved
	 * @param customerEmail Email address of the person making the hold (not really used in this application)
	 * @param reservationTimestamp Timestamp of the reservation (not really used in this application)
	 */
	public Reservation(String reservationID, ArrayList<Integer> seatIDsReserved, String customerEmail, LocalDateTime reservationTimestamp) {
		this.reservationID = reservationID;
		this.seatIDsReserved = seatIDsReserved;
		this.customerEmail = customerEmail;
		this.reservationTimestamp = reservationTimestamp;
	}	

	/**
	 * Getter and Setter methods follow
	 */
	
	public String getReservationID() {
		return this.reservationID;
	}
	
	public ArrayList<Integer> getSeatIDsReserved() {
		return seatIDsReserved;
	}	
}
