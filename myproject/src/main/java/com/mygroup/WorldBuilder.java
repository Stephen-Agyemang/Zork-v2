package com.mygroup;

/**
 * WorldBuilder constructs the game world: all locations, items, containers, and connections.
 * Separated from GameEngine to allow independent testing and world modifications.
 */
public class WorldBuilder {
    private final GameState state;

    public WorldBuilder(GameState state) {
        this.state = state;
    }

    /**
     * Build the complete game world with all locations, items, and connections
     */
    public void buildWorld() {
        Location julian = new Location("Julian", "Science Building filled with cool nerdy stuff duh!");
        Location hoover = new Location("Hoover", "The place for Food. The legendary musician has always loved the food here. Strange—there's a tense atmosphere in the kitchen today.");
        Location olin = new Location("Olin", "Biology building for the super nerds!");
        Location gcpa = new Location("GCPA", "The Musical and theatrical venue. The stage is set, the crowd is gathering. The legend's performance will be here tonight. Everything is ready... except the music equipment.");
        Location roylibrary = new Location("Roy Library", "It's just a quiet place for nerds to study again dude!");
        Location cdi = new Location("CDI", "Center for Diversity and Inclusion, a place to celebrate diversity on campus.");
        Location lilly = new Location("Lilly Building", "The place for general healthy living and wellness, I mean gym, athletes rooms, and health center.");
        state.setLillyLocation(lilly);
        Location duck = new Location("The Fluttering Duck", "The famous school's 5-star restaurant. Jazz musicians are setting up for Thursday's performance. It's packed with an excited crowd waiting for something special.");
        Location ub = new Location ("The Union Building", "The central hub for student activities, events, and dining options on campus.");
        Location admin = new Location("Administration Building", "The place where all the important school offices and staff are located.");
        Location mason = new Location("Mason Hall", "A residence hall where students live and hang out.");
        Location reese = new Location("Reese Hall", "Another residence hall known to have the RES staff office and student community.");
        Location humbert = new Location("Humbert Hall", "A residence hall the houses cool first year students");

        Item inventoryItem1 = new Item("Help", "Guide", "A small guide to help you navigate through the game");
        state.getInventory().addItem(inventoryItem1);
        // Starter coupons
        state.getInventory().addItem(new Item("FoodCoupon", "Coupon", "A coupon that can be redeemed for a free meal at Hoover or The Fluttering Duck"));
        state.getInventory().addItem(new Item("FoodCoupon", "Coupon", "A coupon that can be redeemed for a free meal at Hoover or The Fluttering Duck"));
        state.getInventory().addItem(new Item("FoodCoupon", "Coupon", "A coupon that can be redeemed for a free meal at Hoover or The Fluttering Duck"));
        // Starter snack (counts as food)
        state.getInventory().addItem(new Item("StarterSnack", "Food", "A quick bite to keep you going."));
        
        // All initialization delegated to GameState
        state.setHelpDropLocation(null);


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


        Item humberItem1 = new Item("Yang", "Music", "A torn half of a music sheet which seems like it should be somewhere else...");
        humbert.addItem(humberItem1);

        Item reeseItem1 = new Item("Yin", "Music", "A torn half of a music sheet which seems like it should be soomewhere else...");
        reese.addItem(reeseItem1);


        Item gcpaItem1 = new Item("Piano", "Music", "Huge piano in a practice room for music students");
        gcpa.addItem(gcpaItem1);


        Item olinItem1 = new Item("Specimen", "Reptile", "Stuffed snake on display for biology students or visitors");
        olin.addItem(olinItem1);

    
        Item roylibraryItem1 = new Item( "Bookshelf", "Furniture", "Shelf filled with books of all kinds");
        roylibrary.addItem(roylibraryItem1);


        state.setCurrLocation(julian);


        ContainerItem duckContainer = new ContainerItem("TreasureBox", "Container", "A wooden mystery box filled with snakes that should not be opened...");
        Item snake = new Item("Snakes", "Reptile", "A bunch of venomous snakes slithering around inside the box");
        duckContainer.addItem(snake);
        duck.addItem(duckContainer);

        ContainerItem hooverContainer = new ContainerItem("ToGoBox", "Container", "A to-go box, and it feels like there's food inside...");
        Item chickenAmerican = new Item("Chicken", "Food", "A chicken piece with American cheese melted on top");
        hooverContainer.addItem(chickenAmerican);
        Item cutleries = new Item("Cutleries", "Utensils", "A set of plastic cutleries to eat your food with");
        hooverContainer.addItem(cutleries); 
        hoover.addItem(hooverContainer);
        
        ContainerItem gcpaContainer = new ContainerItem("GuitarCase", "Container", "A big black guitar case with an ominously musical aura around it...");
        gcpa.addItem(gcpaContainer);

        ContainerItem olinContainer1 = new ContainerItem("PCRMachine", "Container", "A big grey box used to teach Biology, but it feels like someone's inside of it...");
        Item dnaSample = new Item("BatMan's-D   NA", "Molecule", "Little test tubes containing DNA of BatMan.");
        olinContainer1.addItem(dnaSample); 
        olin.addItem(olinContainer1);

        ContainerItem olinContainer2 = new ContainerItem("Aquarium", "Container", "A beautiful aquarium filled with special water that a rare Salmon fish needs to survive");
        olin.addItem(olinContainer2);

        ContainerItem olinContainer3 = new ContainerItem("SafeBox", "Container", "A secure containment box designed for dangerous items. The TreasureBox must be placed inside this for safe storage.");
        olin.addItem(olinContainer3);

        ContainerItem julianContainer = new ContainerItem("DisplayCase", "Container", "A glass case once used to display the Monalisa painting, but it seems like something else is inside now...");
        Item rareArtifact = new Item("AncientArtifact", "History", "A mysterious artifact from ancient times.");
        julianContainer.addItem(rareArtifact);
        julian.addItem(julianContainer);

        ContainerItem roylibraryContainer = new ContainerItem("SecretDrawer", "Container", "A hidden drawer in the bookshelf that seems to contain something valuable...");
        Item oldManuscript = new Item("OldManuscript", "Literature", "A fragile manuscript filled with ancient writings and illustrations.");
        roylibraryContainer.addItem(oldManuscript);
        roylibrary.addItem(roylibraryContainer);

        ContainerItem adminContainer = new ContainerItem("MacbookCase", "Container", "A sleek case designed to hold and protect the prestigious Macbook laptop.");
        admin.addItem(adminContainer);
    }
}
