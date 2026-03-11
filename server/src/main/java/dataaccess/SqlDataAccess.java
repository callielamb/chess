package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import org.mindrot.jbcrypt.BCrypt;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

public class SqlDataAccess implements Database {
    public SqlDataAccess() {
        configureDatabase();
    }

    private void configureDatabase() {
        try {
            DatabaseManager.createDatabase();
            DatabaseManager.createTables();
        } catch (DataAccessException ex) {
            throw new RuntimeException("unable to configure database", ex);
        }
    }

    @Override
    public void clear() {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.createStatement()) {
                statement.executeUpdate("DELETE FROM auth");
                statement.executeUpdate("DELETE FROM game");
                statement.executeUpdate("DELETE FROM users");
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to clear database", ex);
        }
    }

    @Override
    public void createUser(UserData user) {
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        String statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";

        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1,user.username());
                preparedStatement.setString(2,hashedPassword);
                preparedStatement.setString(3,user.email());
                preparedStatement.executeUpdate();
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to create user", ex);
        }
    }

    @Override
    public UserData getUser(String username) {
        String statement = "SELECT username, password, email FROM users WHERE username=?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        String foundUsername = rs.getString("username");
                        String foundPassword = rs.getString("password");
                        String foundEmail = rs.getString("email");
                        return new UserData(foundUsername,foundPassword, foundEmail);
                    }
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to get user", ex);
        }

        return null;
    }

    @Override
    public void createAuth(AuthData auth) {
        String statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";

        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, auth.authToken());
                preparedStatement.setString(2, auth.username());
                preparedStatement.executeUpdate();
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to create auth", ex);
        }
    }

    @Override
    public AuthData getAuth(String authToken) {
        String statement = "SELECT authToken, username FROM auth WHERE authToken=?";

        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1,authToken);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        String foundToken = rs.getString("authToken");
                        String foundUsername = rs.getString("username");
                        return new AuthData(foundToken, foundUsername);
                    }
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to get auth", ex);
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) {
        String statement = "DELETE FROM auth WHERE authToken=?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);
                preparedStatement.executeUpdate();
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to delete auth", ex);
        }
    }

    @Override
    public int createGame(GameData game) {
        String gameJson = new Gson().toJson(game.game());
        String statement = "INSERT INTO game (whiteUsername, blackUsername, gameName, gameData) VALUES (?, ?, ?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1,game.whiteUsername());
                preparedStatement.setString(2,game.blackUsername());
                preparedStatement.setString(3,game.gameName());
                preparedStatement.setString(4, gameJson);
                preparedStatement.executeUpdate();

                try (var rs = preparedStatement.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to create game", ex);
        }
        return 0;
    }

    @Override
    public GameData getGame(int gameID) {
        String statement = "SELECT gameID, whiteUsername, blackUsername, gameName, gameData FROM game WHERE gameID=?";

        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, gameID);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        int id = rs.getInt("gameID");
                        String white = rs.getString("whiteUsername");
                        String black = rs.getString("blackUsername");
                        String name = rs.getString("gameName");
                        String gameJson = rs.getString("gameData");
                        ChessGame game = new Gson().fromJson(gameJson, ChessGame.class);
                        return new GameData(id, white, black, name, game);
                    }
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to get game", ex);
        }
        return null;
    }

    @Override
    public List<GameData> listGames() {
        List<GameData> games = new ArrayList<>();
        String statement = "SELECT gameID, whiteUsername, blackUsername, gameName, gameData FROM game";

        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("gameID");
                        String white = rs.getString("whiteUsername");
                        String black = rs.getString("blackUsername");
                        String name = rs.getString("gameName");

                        String gameJson = rs.getString("gameData");
                        ChessGame game = new Gson().fromJson(gameJson,ChessGame.class);
                        games.add(new GameData(id,white,black,name,game));
                    }
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to list games", ex);
        }
        return games;
    }

    @Override
    public void updateGame(GameData game) {
        String gameJson = new Gson().toJson(game.game());
        String statement = """
            UPDATE game SET whiteUsername=?, blackUsername=?, gameName=?, gameData=? WHERE gameID=?
            """;

        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1,game.whiteUsername());
                preparedStatement.setString(2, game.blackUsername());
                preparedStatement.setString(3,game.gameName());
                preparedStatement.setString(4,gameJson);
                preparedStatement.setInt(5, game.gameID());
                preparedStatement.executeUpdate();
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to update game", ex);
        }
    }
}