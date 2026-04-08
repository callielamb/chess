package client;

import chess.ChessPiece;
import model.GameData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import chess.ChessMove;
import chess.ChessPosition;

public class ChessClient {

    private final Scanner scanner = new Scanner(System.in);
    private final String serverUrl = "http://localhost:8080";
    private final ServerFacade server = new ServerFacade(serverUrl);

    private WebSocketClient ws;
    private int currentGameID;
    private String currentColor;
    private GameData activeGame;
    private boolean inGameplay = false;

    private boolean running = true;
    private String authToken = null;
    private String username = null;
    private List<GameData> currentGames = new ArrayList<>();
    private final BoardPrinter boardPrinter = new BoardPrinter();

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
        } else if (inGameplay) {
            return evalGameplay(command, tokens);
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
                case "play":
                    return playGame(tokens);
                case "observe":
                    return observeGame(tokens);
                case "clear":
                    return clearDatabase();
                default:
                    return "Invalid command. Type 'help' to see options.";
            }
        } catch (RuntimeException ex) {
            return ex.getMessage();
        }
    }

    private String evalGameplay(String command, String[] tokens) {
        try {
            switch (command) {
                case "help":
                    return gameplayHelp();
                case "redraw":
                    return redrawBoard();
                case "leave":
                    return leaveGame();
                case "move":
                    return movePiece(tokens);
                case "resign":
                    return resignGame(tokens);
                case "highlight":
                    return highlightMoves(tokens);
                case "quit":
                    running = false;
                    return "Thanks for playing, Goodbye!";
                default:
                    return "Invalid command. Type 'help' to see options.";
            }
        } catch (RuntimeException ex) {
            return ex.getMessage();
        }
    }

    private String clearDatabase() {
        try {
            server.clear();
            authToken = null;
            username = null;
            currentGames.clear();
            activeGame = null;
            inGameplay = false;

            return "Database cleared.";
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
        activeGame = null;
        inGameplay = false;

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

    private GameData getGameFromNumber(String gameNumberText) {
        if (currentGames.isEmpty()) {
            throw new RuntimeException("No games listed. Use 'list' to view games.");
        }
        int gameNumber;
        try {
            gameNumber = Integer.parseInt(gameNumberText);
        } catch (NumberFormatException ex) {
            throw new RuntimeException("Game number must be a real number.");
        }

        if (gameNumber < 1 || gameNumber > currentGames.size()) {
            throw new RuntimeException("Invalid game number.");
        }

        return currentGames.get(gameNumber - 1);
    }
    public void handleSocketClosed() {
        ws = null;
        inGameplay = false;
        activeGame = null;
        currentGameID = 0;
        currentColor = null;
        System.out.println("Game connection closed. Returning to menu.");
    }

    private String playGame(String[] tokens) {
        if (tokens.length != 3) {
            return "To play, input: play <gameNumber> <WHITE|BLACK>";
        }

        GameData game = getGameFromNumber(tokens[1]);
        String playerColor = tokens[2].toUpperCase();

        if (!playerColor.equals("WHITE") && !playerColor.equals("BLACK")) {
            return "Player color must be WHITE or BLACK.";
        }

        server.joinGame(authToken, playerColor, game.gameID());

        currentGameID = game.gameID();
        currentColor = playerColor;
        activeGame = game;
        inGameplay = true;

        connectWebSocket();
        sendConnect();

        return "Joining game...";
    }

    private String observeGame(String[] tokens) {
        if (tokens.length != 2) {
            return "To observe, input: observe <gameNumber>";
        }
        GameData game = getGameFromNumber(tokens[1]);

        currentGameID = game.gameID();
        currentColor = null;
        activeGame = game;
        inGameplay = true;
        connectWebSocket();
        sendConnect();
        return "Observing game...";
    }

    private void connectWebSocket() {
        String wsUrl = serverUrl.replace("http", "ws") + "/ws";
        ws = new WebSocketClient(this, wsUrl);
    }

    private void sendConnect() {
        var command = new websocket.commands.ConnectCommand(authToken, currentGameID, currentColor);
        String json = new com.google.gson.Gson().toJson(command);
        ws.send(json);
    }

    public void updateGame(GameData game) {
        activeGame = game;
        if ("BLACK".equals(currentColor)) {
            System.out.println(boardPrinter.printBlackBoard(game.game()));
        } else {
            System.out.println(boardPrinter.printWhiteBoard(game.game()));
        }
    }

    private String redrawBoard() {
        if (activeGame == null) {
            return "No active game to redraw.";
        }
        if ("BLACK".equals(currentColor)) {
            return boardPrinter.printBlackBoard(activeGame.game());
        } else {
            return boardPrinter.printWhiteBoard(activeGame.game());
        }
    }

    private String leaveGame() {
        if (ws != null) {
            var command = new websocket.commands.LeaveCommand(authToken, currentGameID);
            String json = new com.google.gson.Gson().toJson(command);
            ws.send(json);
        }
        inGameplay = false;
        activeGame = null;
        currentGameID = 0;
        currentColor = null;
        return "Left game.";
    }

    private String movePiece(String[] tokens) {
        if (ws == null) {
            return "Not connected to a game anymore.";
        }
        if (tokens.length != 3 && tokens.length != 4) {
            return "To move, input: move <start> <end> [queen|rook|bishop|knight]";
        }

        ChessPosition start = parsePosition(tokens[1]);
        ChessPosition end = parsePosition(tokens[2]);

        ChessPiece.PieceType promotionPiece = null;
        if (tokens.length == 4) {
            promotionPiece = parsePromotionPiece(tokens[3]);
        }
        ChessMove move = new ChessMove(start, end, promotionPiece);

        var command = new websocket.commands.MakeMoveCommand(authToken, currentGameID, move);
        String json = new com.google.gson.Gson().toJson(command);
        ws.send(json);

        return "Move sent.";
    }
    private ChessPiece.PieceType parsePromotionPiece(String text) {
        String piece = text.toLowerCase();
        switch (piece) {
            case "queen":
                return ChessPiece.PieceType.QUEEN;
            case "rook":
                return ChessPiece.PieceType.ROOK;
            case "bishop":
                return ChessPiece.PieceType.BISHOP;
            case "knight":
                return ChessPiece.PieceType.KNIGHT;
            default:
                throw new RuntimeException("Promotion piece must be queen, rook, bishop, or knight.");
        }
    }

    private String resignGame(String[] tokens) {
        return "Resign command coming soon.";
    }

    private String highlightMoves(String[] tokens) {
        return "Highlight command coming soon.";
    }

    private ChessPosition parsePosition(String text) {
        if (text.length() != 2) {
            throw new RuntimeException("Position must look like e2.");
        }
        char fileChar = Character.toLowerCase(text.charAt(0));
        char rankChar = text.charAt(1);
        if (fileChar < 'a' || fileChar > 'h' || rankChar < '1' || rankChar > '8') {
            throw new RuntimeException("Position must be between a1 and h8.");
        }

        int col = fileChar - 'a' + 1;
        int row = rankChar - '0';
        return new ChessPosition(row, col);
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

    private String gameplayHelp() {
        return """
                Available gameplay commands:
                help - show this message
                redraw - redraw the board
                leave - leave the game
                move <start> <end> [queen|rook|bishop|knight] - make a move
                resign - resign the game
                highlight <position> - show legal moves
                quit - exit the program
                """;
    }
}