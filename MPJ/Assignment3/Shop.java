class ShoppingCart {
    public double applyDiscount(int totalAmount) {
        return totalAmount - (totalAmount * 0.10);
    }

    public double applyDiscount(String userType, int totalAmount) {
        double discount = 0;
        if (userType.equals("Gold")) {
            discount = 0.20;
        } else if (userType.equals("Silver")) {
            discount = 0.15;
        }
        return totalAmount - (totalAmount * discount);
    }
}

public class Shop {
    public static void main(String[] args) {
        ShoppingCart cart = new ShoppingCart();
        System.out.println(cart.applyDiscount(1000));
        System.out.println(cart.applyDiscount("Gold", 1000));
    }
}
