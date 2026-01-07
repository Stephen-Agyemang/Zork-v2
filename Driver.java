import java.util.Arrays;
import java.util.Scanner;

public class Driver {
    private static Location currLocation;
    private static ContainerItem myInventory;

    /* This is a static method that creates five new Location objects, connects the Location objects together,
    adds items to the Locations, and makes the currLocation variable to store the address of one of the five
    Locations created
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
        boolean running = true;

        /*
           Infinite loop that:
           - Prompts the user for a command
           - Reads and tokenizes the input
           - Processes the command using a switch statement
           - Exits when the user enters "quit"
        */
        while (running){
            System.out.println("Enter command.");
            String userInput = scanner.nextLine().toLowerCase().trim();

            String[] words = userInput.split(" ");

            if (words.length == 0) continue;

            String command = words[0];

            switch(command){
                case "look":
                    System.out.println(currLocation.getName() + " - " + currLocation.getDescription());
                    for (int i = 0; i < currLocation.numItems(); i++){
                        System.out.println("+ " + currLocation.getItem(i).getName());
                    }
                    break;

                case "examine":
                    if (words.length > 1){
                        String itemName = String.join(" ", Arrays.copyOfRange(words, 1, words.length));
                        if (currLocation.hasItem(itemName)){
                            System.out.println(currLocation.getItem(itemName).toString());
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
                    if (words.length > 1) {
                       if (currLocation.canMove(words[1] )) {
                            currLocation = currLocation.getLocation(words[1]);
                    }
                        else if (!words[1].equals("north") && !words[1].equals("south")
                        && !words[1].equals("west") && !words[1].equals("east") ) {
                            System.out.println("Enter a valid direction...something like north or south you know :)");

                    }   else {
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
                    if(words.length > 1) {

                        int fromIndex = -1;
                        for (int i = 0; i < words.length; i++) {
                            if (words[i].equals("from")) {
                                fromIndex = i;
                                break;
                            }
                        }

                        if (fromIndex != -1) {
                            String itemName = String.join(" ", Arrays.copyOfRange(words, 1, fromIndex));
                            String containerName = String.join(" ", Arrays.copyOfRange(words, fromIndex + 1, words.length));

                            if (!currLocation.hasItem(containerName)) {
                                System.out.println("Cannot find that container in here");
                                break;
                            }
                            if (!(currLocation.getItem(containerName) instanceof ContainerItem container)) {
                                System.out.println("The container does not exist in here");
                                break;
                            }

                            if (!container.hasItem(itemName)) {
                                System.out.println("The " + containerName + " doesn't contain this item");
                                break;
                            }

                            myInventory.addItem(container.removeItem(itemName));
                        } else {
                            String itemName = String.join(" ", Arrays.copyOfRange(words, 1, words.length));
                            if (currLocation.hasItem(itemName)) {
                                myInventory.addItem(currLocation.removeItem(itemName));
                            } else {
                                System.out.println("Cannot find that item here");
                            }
                        }
                    }

                    else {
                        System.out.println("Take what? Give me a full sentence.");
                        }
                    break;


                case "drop":
                    if(words.length > 1) {
                        String itemName = String.join(" ", Arrays.copyOfRange(words, 1, words.length));

                        if(myInventory.hasItem(itemName)) {
                            currLocation.addItem(myInventory.removeItem(itemName));
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
                    if (words.length >= 4) {

                        int inIndex = -1;
                        for (int i = 0; i < words.length; i++) {
                            if (words[i].equals("in")) {
                                inIndex = i;
                                break;
                            }
                        }

                        if (inIndex != -1) {
                            String itemName = String.join(" ", Arrays.copyOfRange(words, 1, inIndex));
                            String containerName = String.join(" ", Arrays.copyOfRange(words, inIndex + 1, words.length));

                            if (!myInventory.hasItem(itemName)) {
                                System.out.println("You don't have this item.");
                                break;
                            }

                            if (!currLocation.hasItem(containerName)) {
                                System.out.println("Cannot find container here.");
                                break;
                            }


                            if (!(currLocation.getItem(containerName) instanceof ContainerItem container)) {
                                System.out.println("The " + containerName + " is not a container.");
                                break;
                            }

                            container.addItem(myInventory.removeItem(itemName));
                        } else {
                            System.out.println("Put what in what? Give me a full sentence");
                        }
                    }

                    else {
                        System.out.println("Put what in what? Give me a full sentence");
                    }
                    break;


                case "help":
                    commandDescription();
                    break;


                case "quit":
                    System.out.println("Thank you for playing the game!");
                    running = false;
                    break;


                default :
                    System.out.println("I don't know how to do that");

            }
        }
        scanner.close();
    }
}
