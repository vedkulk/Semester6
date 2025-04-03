package Assignment6;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class ShoppingCart {
    private Map<String, Integer> cart = new HashMap<>();

    public void addItem(String item, int quantity) {
        if (quantity <= 0) {
            System.out.println("Error: Invalid quantity");
            return;
        }
        cart.put(item, cart.getOrDefault(item, 0) + quantity);
        System.out.println(quantity + " " + item + "(s) added to cart.");
    }

    public void removeItem(String item) {
        if (!cart.containsKey(item)) {
            System.out.println("Error: Item not found in cart");
            return;
        }
        cart.remove(item);
        System.out.println(item + " removed from cart.");
    }
}
public class Shopping {
	public static void main(String[] args) {
        ShoppingCart cart = new ShoppingCart();
        Scanner sc = new Scanner(System.in);
        
        System.out.print("Enter item name to add: ");
        String item = sc.nextLine();
        System.out.print("Enter quantity: ");
        int quantity = sc.nextInt();
        cart.addItem(item, quantity);
        
        sc.nextLine(); 
        System.out.print("Enter item name to remove: ");
        String removeItem = sc.nextLine();
        cart.removeItem(removeItem);
        
        sc.close();
    }
}