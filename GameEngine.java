import java.util.ArrayList;
import java.util.Arrays;


public class GameEngine {

    private Location currLocation;
    private ContainerItem myInventory;

    public void createWorld() {

        Location julian = new Location("Julian", "Science Building filled with cool nerdy stuff duh!");
        Location hoover = new Location("Hoover", "The place for Food! :)");
        Location olin = new Location("Olin", "Biology building for the super nerds!");
        Location gcpa = new Location("GCPA", "The Musical and theatrical place on campus!");
        Location roylibrary = new Location("Roy Library", "It's just a quiet place for nerds to study again dude!");
        Location cdi = new Location("CDI", "Center for Diversity and Inclusion, a place to celebrate diversity on campus.");
        Location lilly = new Location("Lilly Building", "The place for general healthy living and wellness, I mean gym, athletes rooms, and health center.");
        Location duck = new Location("The Fluttering Duck", "The famous schools 5Star restaurant known for good food and music on Thursdays.");
        Location ub = new Location ("The Union Building", "The central hub for student activities, events, and dining options on campus.");
        Location admin = new Location("Administration Building", "The place where all the important school offices and staff are located.");
        Location mason = new Location("Mason Hall", "A residence hall where students live and hang out.");
        Location reese = new Location("Reese Hall", "Another residence hall known to have the RES staff office and student community.");
        Location humbert = new Location("Humbert Hall", "A residence hall the houses cool first year students");

        myInventory = new ContainerItem("Inventory", "Container", "Player Backpack to store items collected");


        cdi.connect("north",olin);
        olin.connect("south", cdi);

        julian.connect("jump", cdi);

        julian.connect("north", hoover);
        hoover.connect("south", julian);

        julian.connect("east",lilly);
        lilly.connect("west", julian);

        lilly.connect("north",gcpa);
        gcpa.connect("south", lilly);

        gcpa.connect("north", ub);
        ub.connect("south", gcpa);

        ub.connect("cross", mason);
        mason.connect("jump", admin);

        mason.connect("south", reese);
        reese.connect("north", mason);

        mason.connect("jump", admin);

        reese.connect("east", humbert);
        humbert.connect("west", reese);

        roylibrary.connect("east", duck);
        duck.connect("west", roylibrary);

        duck.connect("north", admin);
        admin.connect("south", duck);

        roylibrary.connect("jump", admin);

        hoover.connect("east", gcpa);
        gcpa.connect("west", hoover);

        hoover.connect("west", olin);
        olin.connect("east", hoover);

        hoover.connect("north", roylibrary);
        roylibrary.connect("south", hoover);


        Item cdiItem = new Item("Phone", "Communication", "A smartphone used to call Sasha for help");
        cdi.addItem(cdiItem);


        Item lillyItem1 = new Item("GlazedGuitar", "Music", "A four string based guitar with the last string glowing green being played by a Football athlete at the moment.");
        lilly.addItem(lillyItem1);

        Item lillyItem2 = new Item("Treadmill", "Exercise", "A high-tech treadmill used for running exercises and energy(points) boost.");
        lilly.addItem(lillyItem2);


        Item duckItem1 = new Item("Classic Burger", "Meal", "A juicy burger with lettuce, tomato, pickles, and cheese");
        duck.addItem(duckItem1);

        Item duckItem2 = new Item("Mac and Cheese", "Meal", "Creamy macaroni and cheese topped with breadcrumbs");
        duck.addItem(duckItem2);

        Item duckItem3 = new Item("Microphone", "Music", "A wireless microphone used by performers at The Fluttering Duck");
        duck.addItem(duckItem3);


        Item julianItem1 = new Item("Statue", "Art", "Bust statue of Percy Lavon Julian");
        julian.addItem(julianItem1);

        Item julianItem2 = new Item("Macbook", "Electronics", "An lost invaluable school macbook exclusive to the President recently spotted at Julian");
        julian.addItem(julianItem2);


        Item hooverItem1 = new Item("Fountain", "Food", "Dispenser containing a variety of soda drinks or water");
        hoover.addItem(hooverItem1);

        Item hooverItem2 = new Item("Sandwich", "Food", "A delicious sandwich with turkey, lettuce, tomato, and cheese");
        hoover.addItem(hooverItem2);

        Item hooverItem3 = new Item("Microphone", "Music", "An important stolen music microphone left by a music student during their lunch");
        hoover.addItem(hooverItem3);

        Item hooverItem4 = new Item("EndangeredSalmon", "Fish", "A rare species of salmon that has to be protected due to its endangered status");
        hoover.addItem(hooverItem4);


        Item humberItem1 = new Item("Yang", "Music", "A torn half of a music sheet which seems like it should be soomewhere else...");
        humbert.addItem(humberItem1);

        Item reeseItem1 = new Item("Yin", "Music", "A torn half of a music sheet which seems like it should be soomewhere else...");
        reese.addItem(reeseItem1);


        Item gcpaItem1 = new Item("Piano", "Music", "Huge piano in a practice room for music students");
        gcpa.addItem(gcpaItem1);


        Item olinItem1 = new Item("Specimen", "Reptile", "Stuffed snake on display for biology students or visitors");
        olin.addItem(olinItem1);

    
        Item roylibraryItem1 = new Item( "Bookshelf", "Furniture", "Shelf filled with books of all kinds");
        roylibrary.addItem(roylibraryItem1);


        Item defaultItem1 = new Item("Shirt", "Clothing", "A secret shirt tucked away in players inventory");
        Item defaultItem2 = new Item("Water", "Dring", "A bottle of water to keep the player hydrated");
        myInventory.addItem(defaultItem1);
        myInventory.addItem(defaultItem2);

        currLocation = julian;

        ContainerItem hooverContainer = new ContainerItem("ToGoBox", "Container", "A to-go box, and it feels like there's food inside...");
        Item chickenAmerican = new Item("Chicken", "Food", "A chicken piece with American cheese melted on top");
        hooverContainer.addItem(chickenAmerican);
        Item cutleries = new Item("Cutleries", "Utensils", "A set of plastic cutleries to eat your food with");
        hooverContainer.addItem(cutleries); 
        hoover.addItem(hooverContainer);

        ContainerItem gcpaContainer = new ContainerItem("GuitarCase", "Container", "A big black guitar case with an ominously musical aura around it...");
    //    Item hauntedGuitar = new Item("GlazedGuitar", "Music Instrument", "A four string based guitar with the last string glowing green.");
      //  gcpaContainer.addItem(hauntedGuitar);
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


    public String processCommand(String userInput) {

        String[] words = userInput.toLowerCase().trim().split(" ");

        if (userInput.trim().isEmpty()) {
            return "Enter a command...";
        }

        String command = words[0];

        switch (command) {
            case "look":
                StringBuilder lkOutput = new StringBuilder();
                lkOutput.append(currLocation.getName())
                        .append(" - ")
                        .append(currLocation.getDescription());
            ArrayList<Item> locationItems = currLocation.getItems();
            for (Item item : locationItems) {
                lkOutput.append("\n+ ").append(item.getName());
            }
            return lkOutput.toString();

            case "examine":
                if (words.length > 1) {
                    String itemName = String.join(" ", Arrays.copyOfRange(words, 1, words.length));
                    if (currLocation.hasItem(itemName)) {
                        return currLocation.getItem(itemName).toString();
                    } else {
                        return "Cannot find that item";
                    }
                } else {
                    return "Examine what? Give me a full sentence.";
                }

            case "go":
                if (words.length > 1) {
                    if (currLocation.canMove(words[1])) {
                        currLocation = currLocation.getLocation(words[1]);
                    } else if (!words[1].equals("north") && !words[1].equals("south")
                            && !words[1].equals("west") && !words[1].equals("east")) {
                        return "Enter a valid direction...something like north or south you know :)";

                    } else {
                        return "You cannot go in that direction";
                    }
                } else {
                    return "Go where? Give me a full sentence";
                }

            case "inventory":
                StringBuilder inOutput = new StringBuilder();
                ArrayList<Item> inventoryItems = myInventory.getItems();
                if (inventoryItems.isEmpty()) {
                    return "Oops! Your inventory is empty.";
                } else {
                    inOutput.append("Your inventory has: ");
                    for (Item item : inventoryItems) {
                        inOutput.append("\n+ ").append(item.getName());
                    }
                }
                return inOutput.toString();


            case "take":
                if (words.length > 1) {

                    int fromIndex = -1;
                    for (int i = 0; i < words.length; i++) {
                        if (words[i].equals("from")) {
                            fromIndex = i;
                        }
                    }

                    if (fromIndex != -1) {
                        String itemName = String.join(" ", Arrays.copyOfRange(words, 1, fromIndex));
                        String containerName = String.join(" ", Arrays.copyOfRange(words, fromIndex + 1, words.length));

                        if (!currLocation.hasItem(containerName)) {
                            return "Cannot find that container in here";
                        }
                        if (!(currLocation.getItem(containerName) instanceof ContainerItem container)) {
                            return "The container does not exist in here";
                        }

                        if (!container.hasItem(itemName)) {
                            return "The " + containerName + " doesn't contain this item";
                        }

                        myInventory.addItem(container.removeItem(itemName));
                    } else {
                        String itemName = String.join(" ", Arrays.copyOfRange(words, 1, words.length));
                        if (currLocation.hasItem(itemName)) {
                            myInventory.addItem(currLocation.removeItem(itemName));
                            return "You picked up  " + itemName;
                        } else {
                            return "Cannot find that item here";
                        }
                    }
                } else {
                    return "Take what? Give me a full sentence.";
                }


            case "drop":
                if (words.length > 1) {
                    String itemName = String.join(" ", Arrays.copyOfRange(words, 1, words.length));

                    if (myInventory.hasItem(itemName)) {
                        currLocation.addItem(myInventory.removeItem(itemName));
                        return "You dropped " + itemName;
                    } else {
                        return "Cannot find that item in your inventory";
                    }
                } else {
                    return "Drop what? Give me a full sentence.";
                }


            case "put":
                if (words.length >= 4) {

                    int inIndex = -1;
                    for (int i = 0; i < words.length; i++) {
                        if (words[i].equals("in")) {
                            inIndex = i;
                        }
                    }

                    if (inIndex != -1) {
                        String itemName = String.join(" ", Arrays.copyOfRange(words, 1, inIndex));
                        String containerName = String.join(" ", Arrays.copyOfRange(words, inIndex + 1, words.length));

                        if (!myInventory.hasItem(itemName)) {
                            return "You don't have this item.";
                        }

                        if (!currLocation.hasItem(containerName)) {
                            return "Cannot find container here.";
                        }


                        if (!(currLocation.getItem(containerName) instanceof ContainerItem container)) {
                            return "The " + containerName + " is not a container.";
                        }   else {
                                container.addItem(myInventory.removeItem(itemName));
                                return "You put " + itemName + " in " + containerName;
                        }
                        } else {
                            return "Put what in what? Give me a full sentence";
                        }
                } else {
                    return "Put what in what? Give me a full sentence";
                }


            case "help":
                return """
                ---------------------------------GAME COMMANDS-------------------------------------
                Look                          : Shows a brief description of your current location.

                Examine <item>                : Gives a description of the specified item.

                Go <direction>                : Moves you to another location (north, south, east, or west).

                Jump                          : Teleports/Hops you to the specified location when you are some specifically chosen places and out of the Resident Halls (The only places you can jump to are: ....)

                Cross <obstacle>              : Crosses you to the Halls Zone of the place (Places you can sleep at)...The only command to get you there.

                Take <item>                   : Picks up an available item in your current location.

                Take <item> from <container   : Picks and item from a specified container into your inventory.

                Drop <item>                   : Removes an item from your inventory and drops it at current location.

                Put <item> in <container>     : Removes the item typed from the inventory to your specified container.

                Inventory                     : Lists all items currently in your possession.

                Help                          : Shows a list of commands and what they do.

                Quit                          : Exits the game.

                """;


            default:
                return "I don't know how to do that";
            } 
        }
}

