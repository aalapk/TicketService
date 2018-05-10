package ak.maven;

import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class AppRunner {

	private static Scanner sn;

	public static void main(String[] args) {
		
		TicketServiceImplementor tsinstance = new TicketServiceImplementor(new Venue(50));
		
		String userInput;
		sn = new Scanner(System.in);
		
		while(true){
			// Display menu graphics
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
		    
			//Capture the user input in scanner object and store it in a pre decalred variable
            //inputValue = UserInputHelper.inInt(" Select option: ");
		    userInput = sn.next();
		    
			//Check the user input
			switch(userInput){
			case "1":				
				System.out.println("=======================================================");
				System.out.println("HOLD SEATS MENU");
				System.out.println("=======================================================");
				System.out.println("You need to hold seats before you can reserve them");
				System.out.println("=======================================================");
				System.out.println("Hit '0' (zero) at any stage to go back to the main menu");
				System.out.println("=======================================================");
				
				boolean noOfSeatsValid = false;
				String noOfSeats = null;
				boolean breakRequested = false;
				
				int noOfSeatsInt = 0;
				while(!noOfSeatsValid) {
					System.out.println("Enter the number of seats to hold:");
					noOfSeats = sn.next();
					
					if(noOfSeats.equals("0")) {
						System.out.println("Going back to the main menu ...");
						breakRequested = true;
						break;
					}
					
					else if(!isPositiveInteger(noOfSeats)) {
						System.out.println("No. of seats needs to be a positive integer");						
					}
					
					else {
						noOfSeatsInt = Integer.parseInt(noOfSeats);
						if (tsinstance.numSeatsAvailable() < noOfSeatsInt) {
							System.out.println(tsinstance.getVenue().getNotEnoughSeatsAvailableMessage());
							System.out.println("Please try with fewer seats if you like");
						}
						else {
							noOfSeatsValid = true;
						}	
					}					
				}
				
				if(!breakRequested) {					 
					
					String emailAddress = null;
					boolean validEmail = false;
					while(!validEmail) {					
						System.out.println("Please provide an email address:");
						emailAddress = sn.next();
						if(noOfSeats.equals("0")) {
							System.out.println("Going back to the main menu ...");							
							break;							 
						}
						
						else if(emailAddress.length() > 30) {
							System.out.println("Please provide an email address less than 30 characters");
						}
						else {
							validEmail = true;
						}
					}
					
					//Now proceed to seat hold

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
				
				boolean holdIDIsInt = false;
				boolean validHoldD = false;
                String holdID = null;				
				int holdIDInt = 0;
				
				while(!validHoldD || !holdIDIsInt) {
					System.out.println("Please provide a Hold ID you received with your seat hold:");
					holdID = sn.next();
					
					System.out.println("");
					
					if(holdID.equals("0")) {
						System.out.println("Going back to the main menu ...");
						break;
					}
					
					else if(!isPositiveInteger(holdID)) {
						System.out.println("Hold ID should be a positive integer");
						continue;
					}
					
					else {  //Hold ID is valid int
						holdIDIsInt = true;
						holdIDInt = Integer.parseInt(holdID);
						
						System.out.println("Here now 1");
						
						if(tsinstance.isHoldExpired(holdIDInt)) {   //Expired hold
							System.out.println("-------------------------------------------------------");
							System.out.println(tsinstance.getVenue().getHoldExpiredMessage());
							System.out.println("-------------------------------------------------------");
							break;	
						}
						
						else if(tsinstance.isValidHoldID(holdIDInt)) {   //Valid active hold
							Optional<SeatHold> s = tsinstance.getVenue().getSeatHoldByID(holdIDInt);
							String reservationCode = tsinstance.reserveSeats(holdIDInt, s.get().getCustomerEmail());
							System.out.println(tsinstance.getVenue().getReservationSuccessMessage());
							System.out.println("-------------------------------------------------------");
							System.out.println("Reservation details are below:");							
							System.out.println("Reservation confirmation code: " + reservationCode);							
							System.out.println("Seat IDs reserved: " + s.get().getSeatIDsHeld().stream().map(v -> v.toString()).collect(Collectors.joining(",")));							
							System.out.println("You'll receive a confirmation email at " + s.get().getCustomerEmail());
							System.out.println("-------------------------------------------------------");
							validHoldD = true;
						}
						
						else {   //Not a valid Hold ID
							System.out.println("-------------------------------------------------------");
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
				//exit from the program
				System.out.println("Exiting...");
				System.exit(0);
			default:
			      System.out.println("Invalid selection");
			      break; // This break is not really necessary
			}
		}
	}
	
	public static boolean isPositiveInteger(String s) {
	      boolean isValidInteger = false;
	      try{
	         int i = Integer.parseInt(s);	         
	         if(i > 0)
	        	 isValidInteger = true;
	      }
	      catch (NumberFormatException ex){
	         // s is not a positive integer
	      }	 
	      return isValidInteger;
	   }
}
