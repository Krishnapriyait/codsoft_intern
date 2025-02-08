import java.util.Scanner;
class Bankaccount {
    private double balance;
    public Bankaccount(double ibal) {
        this.balance = ibal;
    }
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Successfully deposited: $" + amount);
            show();
        } 
	else {
            System.out.println("Invalid deposit amount!");
        }
    }
    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            System.out.println("Successfully withdrawn: $" + amount);
            show();
            return true;
        } 
	else if (amount > balance) {
            System.out.println("Insufficient balance!");
        } 
	else {
            System.out.println("Invalid withdrawal amount!");
        }
        return false;
    }
    public void show() {
        System.out.println("Current Balance: $" + balance);
    }
    public double getBalance() {
        return balance;
    }
}
class ATM {
    private Bankaccount user;
    private Scanner scanner;
    public ATM(Bankaccount account) {
        this.user = account;
        this.scanner = new Scanner(System.in);
    }
    public void showMenu() {
        while (true) {
            System.out.println("\n ATM Menu ");
            System.out.println("1. Withdraw");
            System.out.println("2. Deposit");
            System.out.println("3. Check Balance");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    withdraw();
                    break;
                case 2:
                    deposit();
                    break;
                case 3:
                    user.show();
                    break;
                case 4:
                    System.out.println("Thank you for using the ATM.Bye!");
                    return;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }
    private void withdraw() {
        System.out.print("Enter withdrawal amount: $");
        double amount = scanner.nextDouble();
        user.withdraw(amount);
    }
    private void deposit() {
        System.out.print("Enter deposit amount: $");
        double amount = scanner.nextDouble();
        user.deposit(amount);
    }
}
public class Task3 {
    public static void main(String[] args) {
        Bankaccount user = new Bankaccount(1000.0);
        ATM atm = new ATM(user);
        atm.showMenu();
    }
}