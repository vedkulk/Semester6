class Loan {
    public double calculateInterest(double principal, int years) {
        return principal * 0.05 * years;
    }

    public double calculateInterest(String loanType, double principal, int years) {
        double rate = 0;
        if (loanType.equals("Home")) {
            rate = 0.06;
        } else if (loanType.equals("Car")) {
            rate = 0.07;
        } else if (loanType.equals("Personal")) {
            rate = 0.10;
        }
        return principal * rate * years;
    }
}

public class BankLoan {
    public static void main(String[] args) {
        Loan loan = new Loan();
        System.out.println(loan.calculateInterest(100000, 5));
        System.out.println(loan.calculateInterest("Home", 100000, 5));
    }
}
