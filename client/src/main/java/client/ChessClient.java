package client;

import java.util.Scanner;

public class ChessClient {

    private final Scanner scanner = new Scanner(System.in);
    private final ServerFacade server = new ServerFacade("http://localhost:8080");

    private boolean running = true;
    private String authToken = null;
    private String username = null;

    public void run() {
        System.out.println("Welcome to a game of chess! Type 'help' to get started.");
        while (running) {
            System.out.print("> ");
            String input = scanner.nextLine();

            String result = eval(input);
            if (!result.isBlank()) {
                System.out.println(result);
            }
        }
    }

    private String eval(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }

        String[] tokens = input.trim().split("\\s+");
        String command = tokens[0].toLowerCase();

        if (authToken == null) {
            return evalPrelogin(command, tokens);
        } else {
            return evalPostlogin(command, tokens);
        }
    }

    private String evalPrelogin(String command, String[] tokens) {
        try {
            switch (command) {
                case "help":
                    return preloginHelp();
                case "quit":
                    running = false;
                    return "Thanks for playing, Goodbye!";
                case "login":
                    return login(tokens);
                case "register":
                    return register(tokens);
                default:
                    return "Invalid command. Type 'help' to see the options.";
            }
        } catch (RuntimeException ex) {
            return ex.getMessage();
        }
    }

    private String evalPostlogin(String command, String[] tokens) {
        switch (command) {
            case "help":
                return postloginHelp();
            case "quit":
                running = false;
                return "Thanks for playing, Goodbye!";
            default:
                return "You are logged in as " + username + ". More commands later.";
        }
    }

    private String login(String[] tokens) {
        if (tokens.length != 3) {
            return "Usage: login <username> <password>";
        }
        var auth = server.login(tokens[1], tokens[2]);
        authToken = auth.authToken();
        username = auth.username();
        return "Logged in as " + username;
    }

    private String register(String[] tokens) {
        if (tokens.length != 4) {
            return "To register enter: register <username> <password> <email>";
        }

        var auth = server.register(tokens[1], tokens[2], tokens[3]);
        authToken = auth.authToken();
        username = auth.username();
        return "Registered and logged in as " + username;
    }

    private String preloginHelp() {
        return """
                Available commands:
                help - show this message
                quit - exit the program
                login <username> <password> - sign in
                register <username> <password> <email> - create account
                """;
    }

    private String postloginHelp() {
        return """
                Available commands:
                help - show this message
                logout - sign out
                create <gameName> - create a game
                list - list games
                play <gameNumber> <WHITE|BLACK> - join a game
                observe <gameNumber> - observe a game
                quit - exit the program
                """;
    }
}