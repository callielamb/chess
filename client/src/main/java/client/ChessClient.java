package client;

import java.util.Scanner;

public class ChessClient {

    private final Scanner scanner = new Scanner(System.in);
    private boolean running = true;

    public void run() {
        System.out.println("Welcome to a game of chess! Type 'help' to get started.");
        while (running) {
            System.out.print("> ");
            String input = scanner.nextLine();

            String result = eval(input);
            System.out.println(result);
        }
    }

    private String eval(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }
        String[] tokens = input.trim().split("\\s+");
        String command = tokens[0].toLowerCase();

        switch (command) {
            case "help":
                return help();
            case "quit":
                running = false;
                return "Thanks for playing, Goodbye!";
            default:
                return "Invalid command. Type 'help' to see options.";
        }
    }

    private String help() {
        return """
                Available commands:
                help - show this message
                quit - exit the program
                """;
    }
}