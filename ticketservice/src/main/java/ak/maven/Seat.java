package ak.maven;

/**
* This class represents information about a seat
* Other classes maintain a collection of Seat objects
* The only relevant data in this class for this application is the seat number, but in other scenarios, we could have seat type, price etc.
*/
public class Seat {
	
	private int seatNumber;	
	
	/**
	 * Constructor
	 * @param seatNumber Seat Number
	 */
	public Seat(int seatNumber) {
		this.seatNumber = seatNumber;		
	}
	
	/**
	 * Getter and Setter methods follow
	 */

	public int getSeatNumber() {
		return seatNumber;
	}

	public void setSeatNumber(int seatNumber) {
		this.seatNumber = seatNumber;
	}	
}