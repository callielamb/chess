package dataaccess;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SqlDataAccessTest {
    private SqlDataAccess sqlDataAccess;
    private UserData testUser;
    private AuthData testAuth;
    @BeforeEach
    void setUp() {
        sqlDataAccess = new SqlDataAccess();
        sqlDataAccess.clear();
        testUser = new UserData("callie", "password123", "callie@email.com");
        testAuth = new AuthData("token123", "callie");
    }

    @Test
    void createUserPositive() {
        sqlDataAccess.createUser(testUser);
        UserData foundUser = sqlDataAccess.getUser(testAuth.username());
        assertNotNull(foundUser);
        assertEquals(testAuth.username(), foundUser.username());
        assertEquals(testUser.email(), foundUser.email());
        assertNotEquals(testUser.password(), foundUser.password());
    }

    @Test
    void createUserNegative() {
        sqlDataAccess.createUser(testUser);
        assertThrows(RuntimeException.class, () -> sqlDataAccess.createUser(testUser));
    }

    @Test
    void getUserPositive() {
        sqlDataAccess.createUser(testUser);
        UserData foundUser = sqlDataAccess.getUser(testAuth.username());
        assertNotNull(foundUser);
        assertEquals(testAuth.username(), foundUser.username());
        assertEquals(testUser.email(), foundUser.email());
    }

    @Test
    void getUserNegative() {
        UserData foundUser = sqlDataAccess.getUser("missingUser");
        assertNull(foundUser);
    }

    @Test
    void createAuthPositive() {
        sqlDataAccess.createUser(testUser);
        sqlDataAccess.createAuth(testAuth);
        AuthData foundAuth = sqlDataAccess.getAuth(testAuth.authToken());

        assertNotNull(foundAuth);
        assertEquals(testAuth.authToken(), foundAuth.authToken());
        assertEquals(testAuth.username(), foundAuth.username());
    }

    @Test
    void createAuthNegative() {
        sqlDataAccess.createUser(testUser);
        sqlDataAccess.createAuth(testAuth);
        assertThrows(RuntimeException.class, () -> sqlDataAccess.createAuth(testAuth));
    }

    @Test
    void getAuthPositive() {
        sqlDataAccess.createUser(testUser);
        sqlDataAccess.createAuth(testAuth);
        AuthData foundAuth = sqlDataAccess.getAuth(testAuth.authToken());

        assertEquals(testAuth.authToken(), foundAuth.authToken());
        assertEquals(testAuth.username(), foundAuth.username());
    }

    @Test
    void getAuthNegative() {
        AuthData foundAuth = sqlDataAccess.getAuth("badToken");
        assertNull(foundAuth);
    }

    @Test
    void deleteAuthPositive() {
        sqlDataAccess.createUser(testUser);
        sqlDataAccess.createAuth(testAuth);
        sqlDataAccess.deleteAuth(testAuth.authToken());
        AuthData foundAuth = sqlDataAccess.getAuth(testAuth.authToken());
        assertNull(foundAuth);
    }

    @Test
    void deleteAuthNegative() {
        assertDoesNotThrow(() -> sqlDataAccess.deleteAuth("badToken"));
        assertNull(sqlDataAccess.getAuth("badToken"));
    }

    @Test
    void clearPositive() {
        sqlDataAccess.createUser(testUser);
        sqlDataAccess.createAuth(testAuth);
        sqlDataAccess.clear();
        assertNull(sqlDataAccess.getUser(testAuth.username()));
        assertNull(sqlDataAccess.getAuth(testAuth.authToken()));
        assertEquals(0, sqlDataAccess.listGames().size());
    }

    @Test
    void createGamePositive() {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(0, null, null, "testGame", chessGame);
        int gameID = sqlDataAccess.createGame(game);

        assertTrue(gameID > 0);
    }

    @Test
    void createGameNegative() {
        assertThrows(NullPointerException.class, () -> sqlDataAccess.createGame(null));
    }

    @Test
    void getGamePositive() {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(0, null, null, "testGame", chessGame);
        int gameID = sqlDataAccess.createGame(game);
        GameData foundGame = sqlDataAccess.getGame(gameID);
        assertNotNull(foundGame);
        assertEquals("testGame", foundGame.gameName());
        assertNotNull(foundGame.game());
    }

    @Test
    void getGameNegative() {
        GameData foundGame = sqlDataAccess.getGame(9999);
        assertNull(foundGame);
    }

    @Test
    void listGamesPositive() {
        ChessGame firstGame = new ChessGame();
        ChessGame secondGame = new ChessGame();
        sqlDataAccess.createGame(new GameData(0, null, null, "gameOne", firstGame));
        sqlDataAccess.createGame(new GameData(0, null, null, "gameTwo", secondGame));

        assertEquals(2, sqlDataAccess.listGames().size());
    }

    @Test
    void listGamesNegative() {
        assertTrue(sqlDataAccess.listGames().isEmpty());
    }

    @Test
    void updateGamePositive() throws Exception {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(0, null, null, "game1", chessGame);
        int gameID = sqlDataAccess.createGame(game);

        ChessMove move = new ChessMove(new ChessPosition(2, 1), new ChessPosition(3, 1), null);
        chessGame.makeMove(move);
        GameData updatedGame = new GameData(gameID, "jack", null, "game1", chessGame);
        sqlDataAccess.updateGame(updatedGame);
        GameData foundGame = sqlDataAccess.getGame(gameID);

        assertEquals("jack", foundGame.whiteUsername());
        assertNull(foundGame.game().getBoard().getPiece(new ChessPosition(2, 1)));
        assertNotNull(foundGame.game().getBoard().getPiece(new ChessPosition(3, 1)));
    }

    @Test
    void updateGameNegative() {
        ChessGame chessGame = new ChessGame();
        GameData fakeGame = new GameData(9999, "jack", null, "fakeGame", chessGame);
        sqlDataAccess.updateGame(fakeGame);

        assertNull(sqlDataAccess.getGame(9999));
    }
}