public class Item {
    //Member Variables of Item class
    private String name;
    private String type;
    private String description;
    
    //Constructor of the Item class
    public Item(String pName, String pType, String pDescription){
        name = pName;
        type = pType;
        description = pDescription;
    }

    //Getter Method for the Item class' member variables
    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }
    public String getDescription() {
        return description;
    }

    //Setter Methods for the Item class' member variables 
    public void setName(String pName) {
        name = pName;
    }   
    public void setType(String pType) {
        type = pType;
    }
    public void setDescription(String pDescription) {
        description = pDescription;
    }

    //the toString method concatenates the item's name, type, and description all together,
    //and returns one String.
    public String toString() {
        return name + " [" + type + "] : " + description;
    }

    // Helper method to normalize item names for flexible matching
    // Removes spaces and special characters, converts to lowercase
    public static String normalizeName(String name) {
        if (name == null) return "";
        return name.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

}
