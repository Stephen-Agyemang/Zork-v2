import java.util.Scanner;


public class Main {
   
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        GameEngine game = new GameEngine();

        game.createWorld();

        System.out.println("Welcome to Zork v2!");
        System.out.println("Type 'HELP' to get the gist of it all.");


        boolean running = true;

        
        while (running) {
            System.out.println("> ");
            String userInput = scanner.nextLine();

            String output = game.processCommand(userInput);
            System.out.println(output);
            
            if(userInput.equalsIgnoreCase("quit")) {
                running = false;
            }
        }

        scanner.close();
        System.out.println("Thank Me for wasting a bit of your TIME!! Goodbye!");

    }
}
