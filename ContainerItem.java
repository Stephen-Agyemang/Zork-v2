import java.util.ArrayList;

public class ContainerItem extends Item {
    
    private ArrayList<Item> items;

    //Contructor that initialiazes variables of the ContainerItem class
    public ContainerItem(String pName, String pType, String pDescription) {
        super(pName, pType, pDescription);
        items = new ArrayList<Item>();
    }


    // This method is the getter method for the items ArrayList
    public ArrayList<Item> getItems() {
        return items;
    }


    // This method takes a Item object as a type.
    
    public void addItem(Item addedItem) {
        items.add(addedItem);
    }


    /* This method takes a String, addedItem, as a parameter and returns true if
        the ContainerItem ArrayList contains the item with the name provided or false if otherwise
    */
    public boolean hasItem(String itemName) {
        for(int i = 0; i < items.size(); i++) {
            if(items.get(i).getName().equalsIgnoreCase(itemName)) {
                return true;
            }
        }
            
            return false;
            
        }

    
    /* This method take a String, addedItem, as a parameter and removes the item the correlates with the
    itemName provided and returns that Item object removed when completed
    */
    public Item removeItem(String itemName) {
        Item temp = null;
        for(int i = 0; i < items.size(); i++) {
            if((items.get(i).getName().equalsIgnoreCase(itemName))) {
                temp = items.get(i);
                items.remove(i);
                return temp;
            }
        }
        return temp;    
    }

    /* This method overrides the toString method from the Super Class, Item class. What it does in addtion to the 
    original method is to display the items that are present in ContainerItems object 
    */
    @Override
    public String toString() {
        String result = super.toString();
        if (items.isEmpty()) {
            result += "\n + (Empty)";
        }
        else {
        for (int i = 0; i < items.size(); i++) {
            result += "\n + " + items.get(i).getName();
        } 
        }
        return result;
    }


}
    

    
    
    







    
















