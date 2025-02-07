class Restaurant {
    public double calculateBill(int quantity, double pricePerItem) {
        return quantity * pricePerItem;
    }

    public double calculateBill(String itemName, int quantity, double pricePerItem) {
        return quantity * pricePerItem;
    }

    public double calculateBill(String couponCode, int quantity, double pricePerItem, boolean applyCoupon) {
        double total = quantity * pricePerItem;
        if (applyCoupon && couponCode.equals("DISCOUNT5")) {
            return total * 0.95;
        }
        return total;
    }
}

public class RestaurantBill {
    public static void main(String[] args) {
        Restaurant restaurant = new Restaurant();
        System.out.println(restaurant.calculateBill(5, 100));
        System.out.println(restaurant.calculateBill("Pasta", 2, 200));
        System.out.println(restaurant.calculateBill("DISCOUNT5", 5, 100, true));
    }
}
