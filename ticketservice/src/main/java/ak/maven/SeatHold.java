package ak.maven;

import java.time.LocalDateTime;
import java.util.*;

/**
* This class represents information about a Seat Hold
* The Venue class maintains a collection of SeatHold objects
* Most methods are getters/setters
*/
public class SeatHold {
	
	private int seatHoldID;
	private ArrayList<Integer> seatIDsHeld;
	private String customerEmail;
	private LocalDateTime holdTimestamp;
	
	
	/**
	 * Constructor
	 * @param seatHoldID ID for the Seat Hold
	 * @param seatIDsHeld Seat Numbers held
	 * @param customerEmail Email address of the person making the hold
	 * @param localDateTime Timestamp of the hold (to be used to chek for expired holds)
	 */
	public SeatHold(int seatHoldID, ArrayList<Integer> seatIDsHeld, String customerEmail, LocalDateTime localDateTime){
		this.seatHoldID = seatHoldID;
		this.seatIDsHeld = seatIDsHeld;
		this.customerEmail = customerEmail;
		this.holdTimestamp = localDateTime;
	}
	
	/**
	 * Getter and Setter methods follow
	 */
	 
	public int getSeatHoldID() {
		return seatHoldID;
	}
	
	public void setSeatHoldID(int seatHoldID) {
		this.seatHoldID = seatHoldID;
	}
	
	public ArrayList<Integer> getSeatIDsHeld() {
		return seatIDsHeld;
	}
	
	public void setSeatIDsHeld(ArrayList<Integer> seatIDsHeld) {
		this.seatIDsHeld = seatIDsHeld;
	}
	
	public String getCustomerEmail() {
		return customerEmail;
	}
	
	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}
	
	public LocalDateTime getHoldTimestamp() {
		return holdTimestamp;
	}
	
	public void setHoldTimestamp(LocalDateTime holdTimestamp) {
		this.holdTimestamp = holdTimestamp;
	}
}
