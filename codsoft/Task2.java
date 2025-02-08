import java.util.Scanner;
public class Task2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of subjects: ");
        int subjects = scanner.nextInt();
        int totmarks = 0;
        for (int i = 1; i <= subjects; i++) {
            System.out.print("Enter marks obtained in subject " + i + " out of 100: ");
            int marks = scanner.nextInt();
            while (marks < 0 || marks > 100) {
                System.out.print("Invalid marks! Enter again 0-100: ");
                marks = scanner.nextInt();
            }
            totmarks += marks;
	}
        double avg = (double) totmarks /subjects;
        char grade;
        if (avg >= 90) {
            grade = 'A';
        } 
	else if (avg >= 80) {
            grade = 'B';
        } 
	else if (avg >= 70) {
            grade = 'C';
        } 
	else if (avg >= 60) {
            grade = 'D';
        } 
	else if (avg >= 50) {
            grade = 'E';
        } 
	else {
            grade = 'F';
        }
        System.out.println("\nResults:");
        System.out.println("Total Marks: " + totmarks);
        System.out.println("Average Percentage: " + avg+"%");
        System.out.println("Grade: " + grade);
        scanner.close();
    }
}