package client;

import org.junit.jupiter.api.*;
import server.Server;
import dataaccess.SqlDataAccess;
import model.AuthData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private SqlDataAccess sqlDataAccess;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @BeforeEach
    void setUp() {
        sqlDataAccess = new SqlDataAccess();
        sqlDataAccess.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    void registerPositive() {
        AuthData authData = facade.register("callie", "password", "p1@email.com");

        assertNotNull(authData);
        assertEquals("callie", authData.username());
        assertNotNull(authData.authToken());
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void registerNegative() {
        facade.register("callie", "password", "callie@email.com");

        assertThrows(RuntimeException.class,
                () -> facade.register("callie", "password", "callie@email.com"));
    }

    @Test
    void loginPositive() {
        facade.register("callie", "password", "callie@email.com");

        AuthData authData = facade.login("callie", "password");

        assertNotNull(authData);
        assertEquals("callie", authData.username());
        assertNotNull(authData.authToken());
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void loginNegative() {
        facade.register("callie", "password", "callie@email.com");

        assertThrows(RuntimeException.class,
                () -> facade.login("callie", "wrongPassword"));
    }

    @Test
    void logoutPositive() {
        AuthData authData = facade.register("callie", "password", "callie@email.com");
        assertDoesNotThrow(() -> facade.logout(authData.authToken()));
    }

    @Test
    void logoutNegative() {
        assertThrows(RuntimeException.class,
                () -> facade.logout("badToken"));
    }

    @Test
    void listGamesPositive() {
        AuthData authData = facade.register("callie", "password", "callie@email.com");
        facade.createGame(authData.authToken(), "gameOne");
        facade.createGame(authData.authToken(), "gameTwo");

        var games = facade.listGames(authData.authToken());

        assertNotNull(games);
        assertEquals(2, games.size());
    }

    @Test
    void listGamesNegative() {
        assertThrows(RuntimeException.class,
                () -> facade.listGames("badToken"));
    }

    @Test
    void createGamePositive() {
        AuthData authData = facade.register("callie", "password", "callie@email.com");
        int gameID = facade.createGame(authData.authToken(), "testGame");
        assertTrue(gameID > 0);
    }

    @Test
    void createGameNegative() {
        assertThrows(RuntimeException.class,
                () -> facade.createGame("badToken", "testGame"));
    }

    @Test
    void joinGamePositive() {
        AuthData authData = facade.register("callie", "password", "callie@email.com");
        int gameID = facade.createGame(authData.authToken(), "testGame");
        assertDoesNotThrow(() -> facade.joinGame(authData.authToken(), "WHITE", gameID));
    }

    @Test
    void joinGameNegative() {
        AuthData authData = facade.register("callie", "password", "callie@email.com");
        int gameID = facade.createGame(authData.authToken(), "testGame");
        facade.joinGame(authData.authToken(), "WHITE", gameID);
        AuthData secondUser = facade.register("megan", "password", "megan@email.com");
        assertThrows(RuntimeException.class,
                () -> facade.joinGame(secondUser.authToken(), "WHITE", gameID));
    }
}
