package Service;
import Request.CreateGameRequest;
import Request.JoinGameRequest;
import Result.CreateGameResult;
import Result.JoinGameResult;
import Result.ListGamesResult;
import chess.ChessGame;
import dataaccess.Database;
import model.AuthData;
import model.GameData;
import java.util.List;

public class GameService {

    private final Database database;

    public GameService(Database database) {
        this.database = database;
    }

    private AuthData getAuth(String authToken) {
        if (authToken == null) {
            return null;
        }
        return database.getAuth(authToken);
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest request) {

        AuthData auth = getAuth(authToken);
        if (auth == null) {
            return new CreateGameResult(null, "Error: unauthorized");
        }

        if (request == null || request.gameName() == null) {
            return new CreateGameResult(null, "Error: bad request");
        }

        ChessGame chessGame = new ChessGame();
        GameData newGame = new GameData(0, null, null, request.gameName(), chessGame);

        int gameID = database.createGame(newGame);
        return new CreateGameResult(gameID, null);
    }

    public ListGamesResult listGames(String authToken) {

        AuthData auth = getAuth(authToken);
        if (auth == null) {
            return new ListGamesResult(null, "Error: unauthorized");
        }

        List<GameData> games = database.listGames();
        return new ListGamesResult(games, null);
    }

    public JoinGameResult joinGame(String authToken, JoinGameRequest request) {

        AuthData auth = getAuth(authToken);
        if (auth == null) {
            return new JoinGameResult("Error: unauthorized");
        }

        if (request == null || request.gameID() <= 0 || request.playerColor() == null) {
            return new JoinGameResult("Error: bad request");
        }

        GameData game = database.getGame(request.gameID());
        if (game == null) {
            return new JoinGameResult("Error: bad request");
        }

        String username = auth.username();
        String color = request.playerColor().toUpperCase();

        if (color.equals("WHITE")) {

            if (game.whiteUsername() != null) {
                return new JoinGameResult("Error: already taken");
            }

            GameData updated = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());

            database.updateGame(updated);
            return new JoinGameResult(null);
        }

        if (color.equals("BLACK")) {

            if (game.blackUsername() != null) {
                return new JoinGameResult("Error: already taken");
            }
            GameData updated = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());

            database.updateGame(updated);
            return new JoinGameResult(null);
        }

        return new JoinGameResult("Error: bad request");
    }
}