package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.Connection;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.SQLException;
import java.util.List;

public class SqlDataAccess implements Database {

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
                username VARCHAR(50) NOT NULL PRIMARY KEY,
                password VARCHAR(100) NOT NULL,
                email VARCHAR(100) NOT NULL
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS auth (
                authToken VARCHAR(100) NOT NULL PRIMARY KEY,
                username VARCHAR(50) NOT NULL
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS game (
                gameID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                whiteUsername VARCHAR(50),
                blackUsername VARCHAR(50),
                gameName VARCHAR(100) NOT NULL,
                gameData TEXT NOT NULL
            )
            """
    };

    public SqlDataAccess() {
        configureDatabase();
    }

    private void configureDatabase() {
        try {
            DatabaseManager.createDatabase();

            try (Connection conn = DatabaseManager.getConnection()) {
                for (String statement : createStatements) {
                    try (var preparedStatement = conn.prepareStatement(statement)) {
                        preparedStatement.executeUpdate();
                    }
                }
            }

        } catch (DataAccessException | SQLException ex) {
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
                preparedStatement.setString(1, user.username());
                preparedStatement.setString(2, hashedPassword);
                preparedStatement.setString(3, user.email());
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

                        return new UserData(foundUsername, foundPassword, foundEmail);
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
                preparedStatement.setString(1, authToken);

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
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public GameData getGame(int gameID) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public List<GameData> listGames() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void updateGame(GameData game) {
        throw new UnsupportedOperationException("not implemented");
    }
}