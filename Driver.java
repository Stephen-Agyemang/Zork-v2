import java.util.Scanner;

public class Driver {
    private static Location currLocation;
    private static ContainerItem myInventory; 

    /* This is a static method that creates four new Location objects, connects the Location objects together,
    adds items to the Locations, and makes the currLocation variable to store the address of one of the four
    Locations created ,,
    */
    public static void createWorld() {

        Location julian = new Location("Julian", "Science Building filled with cool nerdy stuff duh!");
        Location hoover = new Location("Hoover", "The place for Food! :)");
        Location olin = new Location("Olin", "Biology building for the super nerds!");
        Location gcpa = new Location("GCPA", "The Musical and theatrical place on campus!");
        Location roylibrary = new Location("Roy O. West Library", "It's just a quiet place for nerds to study again dude!");

        myInventory = new ContainerItem("Inventory", "Container", "Player Backpack to store items collected");

        
        julian.connect("north", hoover);
        hoover.connect("south", julian);
        hoover.connect("east", gcpa);
        gcpa.connect("west", hoover);
        hoover.connect("west", olin);
        olin.connect("east", hoover);
        hoover.connect("north", roylibrary);
        roylibrary.connect("south", hoover);


        Item julianItem = new Item("Statue", "Art", "Bust statue of Percy Lavon Julian");
        julian.addItem(julianItem);

        Item hooverItem = new Item("Fountain", "Food", "Dispenser containing a variety of soda drinks or water");
        hoover.addItem(hooverItem);

        Item gcpaItem = new Item("Piano", "Music", "Huge piano in a practice room for music students");
        gcpa.addItem(gcpaItem);

        Item olinItem = new Item("Specimen", "Reptile", "Stuffed snake on display for biology students or visitors");
        olin.addItem(olinItem);

        Item roylibraryItem = new Item( "Bookshelf", "Furniture", "Shelf filled with books of all kinds");
        roylibrary.addItem(roylibraryItem);

        Item defaultItem = new Item("Shirt", "Clothing", "A secret shirt tucked away in players inventory");
        myInventory.addItem(defaultItem);

    

        currLocation = julian;

        ContainerItem hooverContainer = new ContainerItem("ToGoBox", "Container", "A to-go box, and it feels like there's food inside...");
        Item chickenAmerican = new Item("Chicken", "Food", "A chicken piece with American cheese melted on top");
        hooverContainer.addItem(chickenAmerican);
        hoover.addItem(hooverContainer);

        ContainerItem gcpaContainer = new ContainerItem("GuitarCase", "Container", "A big black guitar case with an ominously musical aura around it...");
        Item hauntedGuitar = new Item("GlazedGuitar", "Music Instrument", "A four string based guitar with the last string glowing green.");
        gcpaContainer.addItem(hauntedGuitar);
        gcpa.addItem(gcpaContainer);

        ContainerItem olinContainer = new ContainerItem("PCRMachine", "Container", "A big grey box used to teach Biology, but it feels like someone's inside of it...");
        Item dnaSample = new Item("BatMan's-DNA", "Molecule", "Little test tubes containing DNA of BatMan.");
        olinContainer.addItem(dnaSample);
        olin.addItem(olinContainer);

        ContainerItem julianContainer = new ContainerItem("DisplayCase", "Container", "A glass case once used to display the Monalisa painting, but it seems like something else is inside now...");
        Item rareArtifact = new Item("AncientArtifact", "History", "A mysterious artifact from ancient times.");
        julianContainer.addItem(rareArtifact);
        julian.addItem(julianContainer);

        ContainerItem roylibraryContainer = new ContainerItem("SecretDrawer", "Container", "A hidden drawer in the bookshelf that seems to contain something valuable...");
        Item oldManuscript = new Item("OldManuscript", "Literature", "A fragile manuscript filled with ancient writings and illustrations.");
        roylibraryContainer.addItem(oldManuscript);
        roylibrary.addItem(roylibraryContainer);
    }

    public static void commandDescription() {
        System.out.println("""
                ---------------------------------GAME COMMANDS-------------------------------------
                Look                          : Shows a brief description of your current location.
                Examine <item>                : Gives a description of the specified item.
                Go <direction>                : Moves you to another location (north, south, east, or west).
                Take <item>                   : Picks up an available item in your current location.
                Take <item> from <container   : Picks and item from a specified container into your inventory.
                Drop <item>                   : Removes an item from your inventory and drops it at current location.
                Put <item> in <container>     : Removes the item typed from the inventory to your specified container.
                Inventory                     : Lists all items currently in your possession.
                Help                          : Shows a list of commands and what they do.
                Quit                          : Exits the game.
                """);
    }

    public static void main (String[] args){

        System.out.println();
        System.out.println("Welcome to My Game GOODLUCK!!");

        createWorld();

        //Creating a new scanner object
        Scanner scanner = new Scanner(System.in);

        //a variable to end the infinite loop when the user prompts "quit"
        int temp = 0;

        /* infinite loop that prompts the user to
        enter a command, it reads the command, splits those words into individual cells in an array of Strings, 
        enters a switch-case statement with three defined options, and quits when the user inputs "quit"
        */
        while (temp == 0){
            System.out.println("Enter command.");
            String userInput = scanner.nextLine();
            userInput = userInput.toLowerCase();

            String[] separateUserInput = userInput.split(" ");

            switch(separateUserInput[0]){
                case "look":
                    System.out.println(currLocation.getName() + " - " + currLocation.getDescription());
                    for (int i = 0; i < currLocation.numItems(); i++){
                        System.out.println("+ " + currLocation.getItem(i).getName());        
                    }
                    break;

                case "examine":
                    if (separateUserInput.length > 1){
                        if (currLocation.hasItem(separateUserInput[1])){
                            System.out.println(currLocation.getItem(separateUserInput[1]).toString());
                    }
                        else{
                            System.out.println("Cannot find that item");
                    }
                    }
                    else{
                        System.out.println("Examine what? Give me a full sentence.");
                    }
                    break;

                case "go":
                    if (separateUserInput.length > 1) {
                       if (currLocation.canMove(separateUserInput[1] )) {
                            currLocation = currLocation.getLocation(separateUserInput[1]);
                    }
                        else if (!separateUserInput[1].equals("north") && !separateUserInput[1].equals("south") 
                        && !separateUserInput[1].equals("west") && !separateUserInput[1].equals("east") ) {
                            System.out.println("Enter a valid direction...something like north or south you know :)");

                    }
                        else {
                            System.out.println("You cannot go in that direction");
                    }
                    }
                    else {
                            System.out.println("Go where? Give me a full sentence");

                    }
                    break;

                case "inventory":
                    if (myInventory.getItems().isEmpty()) {
                        System.out.println("Oops! Your inventory is empty.");
                    }
                    else {
                        System.out.println("Your inventory has: ");
                        for(int i = 0; i < myInventory.getItems().size(); i++) {
                            System.out.println("+ " + myInventory.getItems().get(i).getName());
                    }
                    }
                    break;

                case "take":
                    if(separateUserInput.length > 1) {
                        if (separateUserInput.length >= 4 && separateUserInput[2].equals ("from")) {

                            if( !currLocation.hasItem(separateUserInput[3])) {
                                System.out.println("Cannot find that container in here");
                                break;
                            }
                            if (!(currLocation.getItem(separateUserInput[3]) instanceof ContainerItem)) {
                                System.out.println("The container does not exist in here");
                                break;
                            }

                            ContainerItem c = (ContainerItem) currLocation.getItem(separateUserInput[3]);

                            if (!c.hasItem(separateUserInput[1])) {
                                System.out.println("The " + separateUserInput[3] + " doesn't contain this item");
                                break;
                            }

                            myInventory.addItem(c.removeItem(separateUserInput[1]));
                                break;
                        }

                        if (currLocation.hasItem(separateUserInput[1])) {
                            myInventory.addItem(currLocation.removeItem(separateUserInput[1]));
                        }
                        else {
                            System.out.println("Cannot find that item here");
                        }
                    }
    
                    
                    else{
                        System.out.println("Take what? Give me a full sentence.");
                        }
                    break;
                    
                case "drop":
                    if(separateUserInput.length > 1) {
                        if(myInventory.hasItem(separateUserInput[1])) {
                            currLocation.addItem(myInventory.removeItem(separateUserInput[1]));
                    }
                        else {
                            System.out.println("Cannot find that item in your inventory");
                    }
                    }
                    else{
                        System.out.println("Drop what? Give me a full sentence.");
                    }
                    break;
                
                case "put":
                    if (separateUserInput.length >= 4 && separateUserInput[2].equals("in")) {

                        if (!myInventory.hasItem(separateUserInput[1])) {
                            System.out.println("You don't have this item.");
                            break;
                        }

                        if (!currLocation.hasItem(separateUserInput[3])) {
                            System.out.println("Cannot find container here.");
                            break;
                        }

                        if (!(currLocation.getItem(separateUserInput[3]) instanceof ContainerItem)) {
                            System.out.println("The " + separateUserInput[3] + " is not a container.");
                            break;
                        }

                        ((ContainerItem) currLocation.getItem(separateUserInput[3])).addItem(myInventory.removeItem(separateUserInput[1]));

                    }
                    else 
                    {
                    System.out.println("Put what in what? Give me a full sentence");
                    }
                    break;  
                    
                case "help":
                    commandDescription();     
                    break;

                case "quit":
                    System.out.println("Thank you for playing the game!");
                    temp ++;
                    break;

                default :
                    System.out.println("I don't know how to do that");

            }
        }
        scanner.close();
    }
}
