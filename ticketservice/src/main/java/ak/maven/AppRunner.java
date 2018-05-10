package ak.maven;

import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * This class is the entry point for the app execution, through the public static void main(String[] args) method
 * The class does the job of handling interactive command line interface with the end user
 */
public class AppRunner {

	private static Scanner sn;

	/**
	 * Main method. Handles command line interaction through menu of options
	 * Uses Scanner class to capture user inputs
	 * @param args User inputs
	 */
	public static void main(String[] args) {
		
		TicketServiceImplementor tsinstance = new TicketServiceImplementor(new Venue(50));
		
		String userInput;
		sn = new Scanner(System.in);
		
		while(true){
			
			System.out.println("");
			System.out.println("==========================================");
		    System.out.println("| Ticket Booking Service (Main Menu)     |");
		    System.out.println("==========================================");
		    System.out.println("| Options:                               |");
		    System.out.println("|        1. Hold seats                   |");
		    System.out.println("|        2. Reserve Seats                |");
		    System.out.println("|        3. Find No. of seats available  |");	    
		    System.out.println("|        4. Exit                         |");
		    System.out.println("==========================================");
		    System.out.println("Enter your choice: ");
			
		    userInput = sn.next();
			
			switch(userInput){
			
			case "1":
				System.out.println("=======================================================");
				System.out.println("HOLD SEATS MENU");
				System.out.println("=======================================================");
				System.out.println("You need to hold seats before you can reserve them");
				System.out.println("=======================================================");
				System.out.println("Hit '0' (zero) at any stage to go back to the main menu");
				System.out.println("=======================================================");
				
				boolean noOfSeatsValid = false, breakRequested = false;
				String noOfSeats = null;
				
				int noOfSeatsInt = 0;
				while(!noOfSeatsValid) {
					System.out.println("Enter the number of seats to hold:");
					noOfSeats = sn.next();
					
					if(noOfSeats.equals("0")) {
						breakRequested = true;
						break;						
					}
					
					else if(!HelperMethods.isPositiveInteger(noOfSeats)) {
						System.out.println("No. of seats needs to be a positive integer");
						continue;
					}
					
					else {
						noOfSeatsInt = Integer.parseInt(noOfSeats);
						if (tsinstance.numSeatsAvailable() < noOfSeatsInt) {
							System.out.println(tsinstance.getVenue().getNotEnoughSeatsAvailableMessage());
							System.out.println(tsinstance.numSeatsAvailable() + " seats are available for you to hold");
							continue;
						}
						else {
							noOfSeatsValid = true;
						}
					}
				}
				
				if(!breakRequested) {
					System.out.println("Please provide an email address:");
					String emailAddress = sn.next();  //No validation provided for email address
					
					if(emailAddress.equals("0")) {
						System.out.println("");
						System.out.println("Going back to the main menu ...");
						break;
					}
					
					else {
						SeatHold s = tsinstance.findAndHoldSeats(noOfSeatsInt, emailAddress);
						if(s == null) {
							System.out.println("Something went wrong during the hold operation. Please try again.");
						}
							
						else {
							System.out.println("-------------------------------------------------------");
							System.out.println("");
							System.out.println("Your hold was successful!!");
							System.out.println("-------------------------------------------------------");
							System.out.println("Seat IDs held: " + s.getSeatIDsHeld().stream().map(v -> v.toString()).collect(Collectors.joining(",")));	
							System.out.println("You hold ID is: " + s.getSeatHoldID() + ". You would need this hold ID to confirm the reservation!");
							System.out.println("You need to confirm the reservation within next " + tsinstance.getVenue().getHoldTimeout() + " seconds, or else it will become invalid!");
							System.out.println("---------------------------------------------");
						}
					}
				}
				
				System.out.println("");
				System.out.println("Going back to the main menu ...");
				break;
				
			case "2":
				System.out.println("=======================================================");
				System.out.println("RESERVE SEATS MENU");
				System.out.println("=======================================================");
				System.out.println("You need to hold seats before you can reserve them");
				System.out.println("=======================================================");
				System.out.println("Hit '0' (zero) at any stage to go back to the main menu");
				System.out.println("=======================================================");
				
				boolean holdIDIsInt = false, validHoldD = false;				
                String holdID = null;
				int holdIDInt = 0;
				
				while(!validHoldD || !holdIDIsInt) {
					System.out.println("Please provide a Hold ID you received with your seat hold:");
					holdID = sn.next();				
					
					if(holdID.equals("0")) {						
						break;
					}				
					
					else if(!HelperMethods.isPositiveInteger(holdID)) {
						System.out.println("Hold ID should be a positive integer");
						System.out.println("-------------------------------------------------------");
						continue;
					}
					
					else {
						holdIDIsInt = true;
						holdIDInt = Integer.parseInt(holdID);
						
						if(tsinstance.isHoldExpired(holdIDInt)) {   
							System.out.println("-------------------------------------------------------");
							System.out.println(tsinstance.getVenue().getHoldExpiredMessage());
							System.out.println("-------------------------------------------------------");
							break;
						}
						
						else if(tsinstance.isValidHoldID(holdIDInt)) {
							Optional<SeatHold> sh = tsinstance.getVenue().getSeatHoldByID(holdIDInt);
							String reservationCode = tsinstance.reserveSeats(holdIDInt, sh.get().getCustomerEmail());
							System.out.println(tsinstance.getVenue().getReservationSuccessMessage());
							System.out.println("-------------------------------------------------------");
							System.out.println("Reservation details are below:");
							System.out.println("Reservation confirmation code: " + reservationCode);							
							System.out.println("Seat IDs reserved: " + sh.get().getSeatIDsHeld().stream().map(v -> v.toString()).collect(Collectors.joining(",")));							
							System.out.println("You'll receive a confirmation email at " + sh.get().getCustomerEmail());
							System.out.println("-------------------------------------------------------");
							validHoldD = true;
						}
						
						else {							
							System.out.println(tsinstance.getVenue().getHoldNotFoundMessage());
							System.out.println("-------------------------------------------------------");
						}
					}
				}
				
				System.out.println("");			
				System.out.println("Going back to the main menu ...");
				break;
				
			case "3":
				int noOfSeatsAvailable = tsinstance.numSeatsAvailable();
				System.out.println("No. of seats available: " + noOfSeatsAvailable);
				break;
				
			case "4":
				System.out.println("Exiting...");
				System.exit(0);
				
			default:
			      System.out.println("Invalid selection");
			      break;
			}
		}
	}
}
