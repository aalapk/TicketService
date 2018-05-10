# TicketService
Ticket Service for implementing ticket reservation system for a venue

Assumptions:

1. In absence of persistent storage, the application doesn't support concurrent instances and multiple threads.
2. The application employs a simple greedy logic for finding best seats for users. Seats are numbered sequentially, and a consecutive chunk of next available seats is chosen as best seats. Problem of finding 'best' seats in a venue is an NP-hard problem that would require optimizations and backtracking, and even then, is subjective.
3. Hold timeout is configured as 45 seconds. It can be changed easily by altering a value in Venue.java (private final int holdTimeoutInSeconds = 45). Seat holds become invalid after 45 seconds and can't be reserved. Seats from expired holds become available.
4. Number of seats in a venue is configured as 50. This can be changed easily in AppRunner.java (new Venue(50)).
5. Application uses a simple random number generator to generate unique integer ID of configurable length (currently configured as 6) for SeatHold IDs. It also uses a configurable random alphanumeric generator to generate confirmation codes for reservations (currently configured as 8 characters long).
6. Validation of customer's email address isn't implemented, mainly since the application doesn't send any emails. Helps to test core functionality of the application quicker too. Using Regex to validate an Email address is a clumsy business, given the huge variety of acceptable and non-acceptable email address strings possible. Best approach in Enterprise systems is to send an email and have users validate on its receipt.
7. Reserving a seat hold requires a Seat Hold ID that's generated earlier. Customer Email address isn't sufficient.
8. Reservations cannot be cancelled
9. One customer may hold and reserve all seats

Build instructions:
1. Download/Clone the repository (https://github.com/aalapk/TicketService) and extract in any folder
2. Using command line, navigate to the base folder (ticketservice)
3. Run 'mvn compile' to compile the code and create class files
4. Run 'mvn test' to run and validate tests
5. Run 'mvn exec:java' to run the application. This runs the 'main' method in class AppRunner.java (configured in pom.xml) and should present an interactive command line interface

Dependencies:
1. Java (JRE) 1.8 or higher
2. Maven (takes care of other dependencies, e.g. junit)
