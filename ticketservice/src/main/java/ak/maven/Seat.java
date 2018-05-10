package ak.maven;

public class Seat {
	
	private int seatNumber;	
	
	//Public Constructor
	public Seat(int seatNumber){
		this.seatNumber = seatNumber;		
	}

	public int getSeatNumber() {
		return seatNumber;
	}

	public void setSeatNumber(int seatNumber) {
		this.seatNumber = seatNumber;
	}	
}