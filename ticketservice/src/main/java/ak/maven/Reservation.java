package ak.maven;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Reservation {
	
	private String reservationID;
	private ArrayList<Integer> seatIDsReserved;
	private String customerEmail;
	private LocalDateTime reservationTimestamp;
	
	public Reservation(String reservationID, ArrayList<Integer> seatIDsReserved, String customerEmail, LocalDateTime reservationTimestamp) {
		this.reservationID = reservationID;
		this.seatIDsReserved = seatIDsReserved;
		this.customerEmail = customerEmail;
		this.reservationTimestamp = reservationTimestamp;
	}	

	public String getReservationID() {
		return this.reservationID;
	}
	
	public ArrayList<Integer> getSeatIDsReserved() {
		return seatIDsReserved;
	}	
}
