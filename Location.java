import java.util.ArrayList;
import java.util.HashMap;

public class Location {

    //Member Variables for the Location class
    private String name;
    private String description;
    private final ArrayList<Item> items;
    private final HashMap<String, Location> connections;

    public Location(String pName, String pDescription){
        name = pName;
        description = pDescription;
        items = new ArrayList<Item>();
        connections = new HashMap<String, Location>();
    }

    //Getter Methods for the member variables of the Location class
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }

    //Setter Methods for the member variables of the Location class
    public void setName(String pName) {
        name = pName;
    }
    public void setDescription(String pDescription) {
        description = pDescription;
    }

    //The method adds a new item to the location's ArrayList of stored items
    public void addItem(Item currItem){
        items.add(currItem);
    }

    //This method looks through the ArrayList and, with the name provided by the user, 
    // decides if an item with that name exists in the ArrayList. 
    public boolean hasItem(String itemName){
        String normalizedInput = Item.normalizeName(itemName);
        for (Item item : items) {
            if (Item.normalizeName(item.getName()).equals(normalizedInput)) {
                return true;
            }
        }
        return false;
    }

    //This method looks through the array list and with the input from the user returns the item object if it finds it in the array list
    public Item getItem(String itemName){
        String normalizedInput = Item.normalizeName(itemName);
        for (Item item : items) {
            if (Item.normalizeName(item.getName()).equals(normalizedInput)) {
                return item;
            }
        }
        return null;
    }

    //This method returns an object from the items array list, whose index is given to the method as a parameter
    public Item getItem(int itemIndex){
        if (itemIndex < items.size() && itemIndex >= 0){
            return items.get(itemIndex);
        }

        else{
            return null;
        }
    }


    //This method returns a copy of the items ArrayList for safe access
    public ArrayList<Item> getItems() {
        return new ArrayList<>(items);
    }

    //This method returns the size of the items Arraylist
    public int numItems(){
        return items.size();
    }


    //This method goes through the Array list, checks if the given item is there, and removes it from the array list and returns it
    public Item removeItem(String itemName){
        String normalizedInput = Item.normalizeName(itemName);
        Item temp;
        for (int i = 0; i < items.size(); i++){
            if (Item.normalizeName(items.get(i).getName()).equals(normalizedInput)){
                temp = items.get(i);
                items.remove(i);
                return temp;
            }
        }
        return null;
    }


    /* This method takes two parameters of types String and Location, directionName and connectingLoc respectively, and adds an entry 
    into the HashMap that associates the direction to the Location.
    */
    public void connect(String directionName, Location connectingLoc) {
        connections.put(directionName, connectingLoc);
    }


    /* This method takes a String, directionName, as parameter and return true if there is a Location in the HashMap associated with 
    the direction the user enters and false if otherwise
    */
    public boolean canMove(String directionName) {
        return connections.containsKey(directionName);
    }


    /* This method takes a String directionName, as parameter and return the Location object associated with the directionName 
    provided or null if there is no such Location object 
    */
    public Location getLocation(String directionName) {
        return connections.getOrDefault(directionName, null);
    }

    
}


