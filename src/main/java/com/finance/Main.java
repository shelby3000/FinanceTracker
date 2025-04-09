package com.finance;

//package com.finance;

import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        FinanceManager manager = new FinanceManager();
        manager.initializeDatabase();
        Scanner scanner = new Scanner(System.in);

        // Seed test data
        manager.addTransaction(1000, "income", "2025-04-01", "Salary");
        manager.addTransaction(50, "food", "2025-04-02", "Groceries");

        while (true) {
            System.out.println("\n1. Add Transaction");
            System.out.println("2. View Transactions");
            System.out.println("3. View Balance");
            System.out.println("4. View Category Summary");
            System.out.println("5. Export Summary");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Amount: ");
                    double amount = scanner.nextDouble();
                    scanner.nextLine();
                    System.out.print("Category (e.g., income, food): ");
                    String category = scanner.nextLine();
                    System.out.print("Date (e.g., 2025-04-08): ");
                    String date = scanner.nextLine();
                    System.out.print("Description: ");
                    String description = scanner.nextLine();
                    try {
                        manager.addTransaction(amount, category, date, description);
                        System.out.println("Transaction added!");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case 2:
                    for (Transaction t : manager.getTransactions()) {
                        System.out.println(t);
                    }
                    break;
                case 3:
                    System.out.printf("Current Balance: $%.2f%n", manager.getBalance());
                    break;
                case 4:
                    for (Map.Entry<String, Double> entry : manager.getCategorySummary().entrySet()) {
                        System.out.printf("%s: $%.2f%n", entry.getKey(), entry.getValue());
                    }
                    break;
                case 5:
                    manager.exportSummary();
                    System.out.println("Summary exported to summary.txt!");
                    break;
                case 6:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }
}