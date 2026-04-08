package websocket;
import chess.ChessGame;
import chess.ChessMove;
import websocket.messages.NotificationMessage;
import websocket.messages.LoadGameMessage;
import com.google.gson.Gson;
import dataaccess.Database;
import io.javalin.websocket.WsContext;
import model.AuthData;
import model.GameData;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;

public class WebSocketHandler {
    private final Gson gson = new Gson();
    private final ConnectionManager connectionManager = new ConnectionManager();
    private final Database database;

    public WebSocketHandler(Database database) {
        this.database = database;
    }
    public void onConnect(WsContext session) {
        System.out.println("WebSocket connected: " + session.sessionId());
    }
    public void onClose(WsContext session) {
        connectionManager.remove(session);
        System.out.println("WebSocket closed: " + session.sessionId());
    }

    public void onMessage(WsContext session, String message) {
        try {
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> {
                    websocket.commands.ConnectCommand connectCommand =
                            gson.fromJson(message, websocket.commands.ConnectCommand.class);
                    handleConnect(session, connectCommand);
                }
                case MAKE_MOVE -> {
                    websocket.commands.MakeMoveCommand moveCommand =
                            gson.fromJson(message, websocket.commands.MakeMoveCommand.class);
                    handleMakeMove(session, moveCommand);
                }                case LEAVE -> {
                    websocket.commands.LeaveCommand leaveCommand =
                            gson.fromJson(message, websocket.commands.LeaveCommand.class);
                    handleLeave(session, leaveCommand);
                }
                case RESIGN -> System.out.println("RESIGN not implemented yet");
            }
        } catch (Exception ex) {
            System.out.println("WebSocket message error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    private void handleConnect(WsContext session, websocket.commands.ConnectCommand command) {
        try {
            String authToken = command.getAuthToken();
            int gameID = command.getGameID();
            AuthData auth = database.getAuth(authToken);
            if (auth == null) {
                sendError(session, "Error: unauthorized");
                return;
            }
            String username = auth.username();

            GameData game = database.getGame(gameID);
            if (game == null) {
                sendError(session, "Error: game not found");
                return;
            }
            connectionManager.add(gameID, username, session);

            LoadGameMessage loadMessage = new LoadGameMessage(game);
            session.send(gson.toJson(loadMessage));

            String requestedColor = command.getPlayerColor();
            String role;
            if (requestedColor == null) {
                role = "OBSERVER";
            } else {
                role = requestedColor.toUpperCase();
            }

            System.out.println(username + " connected as role: " + role);

            String joinMessage;
            if (role.equals("OBSERVER")) {
                joinMessage = username + " joined the game as an observer.";
            } else {
                joinMessage = username + " joined the game as " + role + ".";
            }

            connectionManager.broadcastExcept(
                    gameID,
                    username,
                    new NotificationMessage(joinMessage)
            );
        } catch (Exception ex) {
            System.out.println("EXCEPTION in CONNECT:");
            ex.printStackTrace();
            sendError(session, ex.getMessage());
        }
    }
    private void handleMakeMove(WsContext session, websocket.commands.MakeMoveCommand command) {
        try {
            String authToken = command.getAuthToken();
            int gameID = command.getGameID();
            ChessMove move = command.getMove();
            if (move == null) {
                System.out.println("Move is NULL");
                sendError(session, "Error: bad move data");
                return;
            }
            AuthData auth = database.getAuth(authToken);
            if (auth == null) {
                System.out.println("Auth NOT FOUND");
                sendError(session, "Error: unauthorized");
                return;
            }

            String username = auth.username();

            GameData game = database.getGame(gameID);
            if (game == null) {
                System.out.println("Game NOT FOUND");
                sendError(session, "Error: game not found");
                return;
            }

            boolean isWhite = username.equals(game.whiteUsername());
            boolean isBlack = username.equals(game.blackUsername());

            if (!isWhite && !isBlack) {
                System.out.println("User is observer, rejecting move");
                sendError(session, "Error: observers cannot make moves");
                return;
            }

            ChessGame chessGame = game.game();

            ChessGame.TeamColor teamColor = chessGame.getTeamTurn();
            if (isWhite && teamColor != ChessGame.TeamColor.WHITE) {
                System.out.println("White tried to move out of turn");
                sendError(session, "Error: not your turn");
                return;
            }
            if (isBlack && teamColor != ChessGame.TeamColor.BLACK) {
                System.out.println("Black tried to move out of turn");
                sendError(session, "Error: not your turn");
                return;
            }
            chessGame.makeMove(move);
            GameData updatedGame = new GameData(
                    game.gameID(),
                    game.whiteUsername(),
                    game.blackUsername(),
                    game.gameName(),
                    chessGame
            );
            database.updateGame(updatedGame);

            String moveText = positionToString(move.getStartPosition()) + " to " +
                    positionToString(move.getEndPosition());

            if (move.getPromotionPiece() != null) {
                moveText += " promoting to " + move.getPromotionPiece().toString().toLowerCase();
            }

            connectionManager.broadcast(gameID, new LoadGameMessage(updatedGame));
            connectionManager.broadcast(
                    gameID,
                    new NotificationMessage(username + " moved " + moveText)
            );
        } catch (Exception ex) {
            System.out.println("EXCEPTION in MAKE_MOVE:");
            ex.printStackTrace();
            sendError(session, ex.getMessage());
        }
    }
    private String positionToString(chess.ChessPosition position) {
        char file = (char) ('a' + position.getColumn() - 1);
        int rank = position.getRow();
        return "" + file + rank;
    }
    private void handleLeave(WsContext session, websocket.commands.LeaveCommand command) {
        try {
            String authToken = command.getAuthToken();
            int gameID = command.getGameID();

            AuthData auth = database.getAuth(authToken);
            if (auth == null) {
                sendError(session, "Error: unauthorized");
                return;
            }
            String username = auth.username();

            GameData game = database.getGame(gameID);
            if (game == null) {
                sendError(session, "Error: game not found");
                return;
            }
            if (username.equals(game.whiteUsername())) {
                GameData updatedGame = new GameData(
                        game.gameID(),
                        null,
                        game.blackUsername(),
                        game.gameName(),
                        game.game()
                );
                database.updateGame(updatedGame);
            } else if (username.equals(game.blackUsername())) {
                GameData updatedGame = new GameData(
                        game.gameID(),
                        game.whiteUsername(),
                        null,
                        game.gameName(),
                        game.game()
                );
                database.updateGame(updatedGame);
            }
            connectionManager.remove(session);
            connectionManager.broadcast(
                    gameID,
                    new NotificationMessage(username + " left the game.")
            );
            System.out.println(username + " left game " + gameID);
        } catch (Exception ex) {
            sendError(session, ex.getMessage());
        }
    }
    private void sendError(WsContext session, String errorMessage) {
        try {
            ErrorMessage error = new ErrorMessage(errorMessage);
            session.send(gson.toJson(error));
        } catch (Exception ignored) {}
    }
}