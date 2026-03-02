package Service;

import Request.CreateGameRequest;
import Request.JoinGameRequest;
import Request.RegisterRequest;
import Result.CreateGameResult;
import Result.JoinGameResult;
import Result.ListGamesResult;
import Result.RegisterResult;
import dataaccess.Database;
import dataaccess.InsertData;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// 2 tests per method: 1 negative and 1 positive for each
//6 tests total for gameServiceTests
//Methods to test: create game, list games, join games
public class GameServiceTests {
    //Positive Test
    //createGame succeeds with valid authentication
    @Test
    public void createGameSuccess() {
        Database db = new InsertData();

        UserService userService = new UserService(db);
        GameService gameService = new GameService(db);
        RegisterResult reg = userService.register(new RegisterRequest("kate", "pass", "kate@email.com"));
        CreateGameResult res = gameService.createGame(reg.authToken(), new CreateGameRequest("My Game"));

        assertNull(res.message());
        assertNotNull(res.gameID());
        assertTrue(res.gameID() > 0);
    }

    //negative Test
    //createGame fails with bad authentication
    @Test
    public void createGameUnauthorized() {
        Database db = new InsertData();
        GameService gameService = new GameService(db);

        CreateGameResult res = gameService.createGame("badToken", new CreateGameRequest("My Game"));
        assertEquals("Error: unauthorized", res.message());
        assertNull(res.gameID());
    }

    //Positive Test
    //listGames returns the created games with valid authentication
    @Test
    public void listGamesSuccess() {
        Database db = new InsertData();

        UserService userService = new UserService(db);
        GameService gameService = new GameService(db);

        RegisterResult reg = userService.register(new RegisterRequest("callie", "pass", "callie@email.com"));
        gameService.createGame(reg.authToken(), new CreateGameRequest("Game 1"));
        gameService.createGame(reg.authToken(), new CreateGameRequest("Game 2"));
        ListGamesResult res = gameService.listGames(reg.authToken());

        assertNull(res.message());
        assertNotNull(res.games());
        assertEquals(2, res.games().size());
    }

    //Negative Test
    //listGames fails with bad auth token
    @Test
    public void listGamesUnauthorized() {
        Database db = new InsertData();
        GameService gameService = new GameService(db);

        ListGamesResult res = gameService.listGames("badToken");
        assertEquals("Error: unauthorized", res.message());
        assertNull(res.games());
    }

    //Positive Test
    //joinGame succeeds when spot is open
    @Test
    public void joinGameSuccessWhite() {
        Database db = new InsertData();

        UserService userService = new UserService(db);
        GameService gameService = new GameService(db);

        RegisterResult reg = userService.register(new RegisterRequest("abigail", "pass", "abigail@email.com"));
        Integer gameID = gameService.createGame(reg.authToken(), new CreateGameRequest("Game 1")).gameID();
        JoinGameResult res = gameService.joinGame(reg.authToken(), new JoinGameRequest("WHITE", gameID));

        assertNull(res.message());
        assertEquals("abigail", db.getGame(gameID).whiteUsername());
    }

    //Negative Test
    //joinGame fails when color is already taken
    @Test
    public void joinGameAlreadyTaken() {
        Database db = new InsertData();

        UserService userService = new UserService(db);
        GameService gameService = new GameService(db);

        // user1
        RegisterResult reg1 = userService.register(new RegisterRequest("mira", "pass", "mira@email.com"));
        Integer gameID = gameService.createGame(reg1.authToken(), new CreateGameRequest("Game 1")).gameID();
        gameService.joinGame(reg1.authToken(), new JoinGameRequest("WHITE", gameID));

        //user2
        RegisterResult reg2 = userService.register(new RegisterRequest("jack", "pass", "jack@email.com"));
        JoinGameResult res = gameService.joinGame(reg2.authToken(), new JoinGameRequest("WHITE", gameID));

        assertEquals("Error: already taken", res.message());
    }
}