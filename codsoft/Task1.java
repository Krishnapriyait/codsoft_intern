import java.util.Random;
import java.util.Scanner;
public class Task1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int total = 0;
        while (true) {
            int num = new Random().nextInt(100) + 1;
            int attempts = 5;
            int score = 0;
            System.out.println("Guess the number (between 1 and 100). You have " + attempts + " attempts.");
            while (attempts > 0) {
                System.out.print("Enter your guess: ");
                int guess = scanner.nextInt();
                if (guess < num) {
                    System.out.println("Too low! Try again.");
                } 
		else if (guess > num) {
                    System.out.println("Too high! Try again.");
                } 
		else {
                    System.out.println("Congratulations! You guessed the correct number.");
                    score = 1;
                    break;
                }
                attempts--;
                System.out.println("Attempts left: " + attempts);
            }
            if (attempts == 0) {
                System.out.println("Out of attempts! The correct number was: " + num);
            }
            total += score;
            System.out.println("Your current score: " + total);
            System.out.print("Do you want to play again? (yes/no): ");
            scanner.nextLine();
            String again = scanner.nextLine().trim().toLowerCase();
            if (again.equals("no")) {
                System.out.println("Thanks for playing! Your final score is: " + total);
                break;
            }
        }
        scanner.close();
    }
}