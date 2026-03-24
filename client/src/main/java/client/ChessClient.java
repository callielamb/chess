package client;

import model.GameData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public class ChessClient {

    private final Scanner scanner = new Scanner(System.in);
    private final ServerFacade server = new ServerFacade("http://localhost:8080");

    private boolean running = true;
    private String authToken = null;
    private String username = null;
    private List<GameData> currentGames = new ArrayList<>();

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
                    return "Invalid command. Type 'help' to see options.";
            }
        } catch (RuntimeException ex) {
            return ex.getMessage();
        }
    }

    private String evalPostlogin(String command, String[] tokens) {
        try {
            switch (command) {
                case "help":
                    return postloginHelp();
                case "quit":
                    running = false;
                    return "Thanks for playing, Goodbye!";
                case "logout":
                    return logout();
                case "create":
                    return createGame(tokens);
                case "list":
                    return listGames();
                default:
                    return "Invalid command. Type 'help' to see options.";
            }
        } catch (RuntimeException ex) {
            return ex.getMessage();
        }
    }

    private String login(String[] tokens) {
        if (tokens.length != 3) {
            return "To login, input: login <username> <password>";
        }

        var auth = server.login(tokens[1], tokens[2]);
        authToken = auth.authToken();
        username = auth.username();

        return "Logged in as " + username;
    }

    private String register(String[] tokens) {
        if (tokens.length != 4) {
            return "To register, input: register <username> <password> <email>";
        }

        var auth = server.register(tokens[1], tokens[2], tokens[3]);
        authToken = auth.authToken();
        username = auth.username();

        return "Registered and logged in as " + username;
    }

    private String logout() {
        server.logout(authToken);
        authToken = null;
        username = null;
        currentGames.clear();

        return "Logged out successfully.";
    }

    private String createGame(String[] tokens) {
        if (tokens.length < 2) {
            return "To create game, input: create <gameName>";
        }

        String gameName = String.join(" ", Arrays.copyOfRange(tokens, 1, tokens.length));
        server.createGame(authToken, gameName);

        return "Game " + gameName + " created successfully.";
    }

    private String listGames() {
        Collection<GameData> games = server.listGames(authToken);
        currentGames = new ArrayList<>(games);

        if (currentGames.isEmpty()) {
            return "No games found.";
        }

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < currentGames.size(); i++) {
            GameData game = currentGames.get(i);

            String whitePlayer = game.whiteUsername();
            if (whitePlayer == null) {
                whitePlayer = "-";
            }

            String blackPlayer = game.blackUsername();
            if (blackPlayer == null) {
                blackPlayer = "-";
            }

            int gameNumber = i + 1;

            result.append(gameNumber);
            result.append(": ");
            result.append(game.gameName());
            result.append(" (white: ");
            result.append(whitePlayer);
            result.append(", black: ");
            result.append(blackPlayer);
            result.append(")");
            result.append("\n");
        }

        return result.toString().trim();
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