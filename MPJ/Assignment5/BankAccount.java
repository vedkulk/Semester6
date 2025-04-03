// Custom exceptions
class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String message) {
        super(message);
    }
}

class InvalidAmountException extends Exception {
    public InvalidAmountException(String message) {
        super(message);
    }
}

class Account {
    private double balance;

    public Account(double initialBalance) {
        this.balance = initialBalance;
    }

    public void deposit(double amount) throws InvalidAmountException {
        if (amount <= 0)
            throw new InvalidAmountException("Deposit amount cannot be zero or negative.");
        balance += amount;
        System.out.println("Deposited: Rs" + amount);
    }

    public void withdraw(double amount) throws InsufficientFundsException, InvalidAmountException {
        if (amount < 0)
            throw new InvalidAmountException("Withdrawal amount cannot be negative.");
        if (amount > balance)
            throw new InsufficientFundsException("Insufficient balance.");
        balance -= amount;
        System.out.println("Withdrawn: Rs" + amount);
    }

    public double getBalance() {
        return balance;
    }
}

public class BankAccount {
    public static void main(String[] args) {
        try {
            Account account = new Account(500);
            account.deposit(200);
            account.withdraw(100);
            account.withdraw(700);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
