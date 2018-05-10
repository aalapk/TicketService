package ak.maven;

import java.time.LocalDateTime;
import java.util.*;

public class SeatHold {
	
	private int seatHoldID;
	private ArrayList<Integer> seatIDsHeld;
	private String customerEmail;
	private LocalDateTime holdTimestamp;
	
	public SeatHold(int seatHoldID, ArrayList<Integer> seatIDsHeld, String customerEmail, LocalDateTime localDateTime){
		this.seatHoldID = seatHoldID;
		this.seatIDsHeld = seatIDsHeld;
		this.customerEmail = customerEmail;
		this.holdTimestamp = localDateTime;
	}
	
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
