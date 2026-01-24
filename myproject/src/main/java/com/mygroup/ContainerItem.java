package com.mygroup;
import java.util.ArrayList;

public class ContainerItem extends Item {
    
    private final ArrayList<Item> items;

    //Constructor that initializes variables of the ContainerItem class
    public ContainerItem(String pName, String pType, String pDescription) {
        super(pName, pType, pDescription);
        items = new ArrayList<>();
    }


    // This method is the getter method for the items ArrayList
    public ArrayList<Item> getItems() {
        return new ArrayList<>(items);
    }

    // Names of all contained items
    public ArrayList<String> getItemNames() {
        ArrayList<String> names = new ArrayList<>();
        for (Item item : items) {
            names.add(item.getName());
        }
        return names;
    }

    // Alias used by command routing when listing container contents
    public ArrayList<String> getContainerItemNames() {
        return getItemNames();
    }


    // This method takes an Item object as a type.
    
    public void addItem(Item addedItem) {
        items.add(addedItem);
    }


    /* This method takes a String, addedItem, as a parameter and returns true if
        the ContainerItem ArrayList contains the item with the name provided or false if otherwise
    */
    public boolean hasItem(String itemName) {
        String normalizedInput = Item.normalizeName(itemName);
        for (Item item : items) {
            if (Item.normalizeName(item.getName()).equals(normalizedInput)) {
                return true;
            }
        }
            
            return false;
            
        }

    // Fetch item without removing
    public Item getItem(String itemName) {
        String normalizedInput = Item.normalizeName(itemName);
        for (Item item : items) {
            if (Item.normalizeName(item.getName()).equals(normalizedInput)) {
                return item;
            }
        }
        return null;
    }

    
    /* This method take a String, addedItem, as a parameter and removes the item the correlates with the
    itemName provided and returns that Item object removed when completed
    */
    public Item removeItem(String itemName) {
        String normalizedInput = Item.normalizeName(itemName);
        Item temp;
        for(int i = 0; i < items.size(); i++) {
            if(Item.normalizeName(items.get(i).getName()).equals(normalizedInput)) {
                temp = items.get(i);
                items.remove(i);
                return temp;
            }
        }
        return null;
    }

    /* This method overrides the toString method from the Super Class, Item class. What it does in addition to the
    original method is to display the items that are present in ContainerItems object 
    */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(super.toString());
        if (items.isEmpty()) {
            result.append("\n + (Empty)");
        }
        else {
            for (Item item : items) {
                result.append("\n + ").append(item.getName());
            }
        }
        return result.toString();
    }


}