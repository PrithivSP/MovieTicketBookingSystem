package utils;

import exceptions.ExitApp;
import exceptions.ExitToUserMenu;

import java.util.Scanner;

public class ConsoleUtils {

    private static final Scanner SCANNER = new Scanner(System.in);

    public static String readLine(String prompt) {
        if (prompt != null && !prompt.isEmpty()) System.out.print(prompt);
        String line = SCANNER.nextLine().trim();
        if (line.equalsIgnoreCase("exit") || line.equals("-1")) {
            throw new ExitToUserMenu();
        }
        return line;
    }

    public static int readInt(String prompt) {
        while (true) {
            if (prompt != null && !prompt.isEmpty()) System.out.print(prompt);

            String line = SCANNER.nextLine().trim();
            if (line.equalsIgnoreCase("home") || line.equals("-1")) {
                throw new ExitToUserMenu();
            }
            if (line.isEmpty()) {
                System.out.println("Empty input. Try again.");
                continue;
            }
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    public static String readLineInAuth(String prompt) {
        if (prompt != null && !prompt.isEmpty()) System.out.print(prompt);
        String line = SCANNER.nextLine().trim();
        if (line.equalsIgnoreCase("exit") || line.equals("-1")) {
            throw new ExitApp();
        }
        return line;
    }

    public static int readAuthChoice(String prompt) {
        while (true) {
            if (prompt != null && !prompt.isEmpty()) System.out.print(prompt);

            String line = SCANNER.nextLine().trim();
            if (line.equalsIgnoreCase("exit") || line.equals("-1")) {
                throw new ExitApp();
            }
            if (line.isEmpty()) {
                System.out.println("Empty input. Try again.");
                continue;
            }
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    public static void printHeader(String title) {
        System.out.println("\n==========================================");
        System.out.printf("  %s%n", title);
        System.out.println("==========================================\n");
    }

}
