package Service;

import Request.CreateGameRequest;
import Request.RegisterRequest;
import Result.ClearResult;
import Result.RegisterResult;
import dataaccess.Database;
import dataaccess.InsertData;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// 2 tests per method: 1 negative and 1 positive for each
//2 tests total for clearServiceTests
//Methods to test: clear
public class ClearServiceTests {

    //Positive Test
    //clear "clears" all data
    @Test
    public void clearSuccess() {
        Database db = new InsertData();

        UserService userService = new UserService(db);
        GameService gameService = new GameService(db);

        RegisterResult reg = userService.register(new RegisterRequest("hudson", "pass", "hudson@email.com"));

        gameService.createGame(reg.authToken(), new CreateGameRequest("Game 1"));
        ClearService clearService = new ClearService(db);
        ClearResult res = clearService.clear();

        assertNull(res.message());
        assertNull(db.getUser("hudson"));
        assertEquals(0, db.listGames().size());
    }

    //Negative Test
    //clear errors if database fails
    @Test
    public void clearFailsWhenDatabaseThrows() {
        Database db = new InsertData() {
            @Override
            public void clear() {
                throw new RuntimeException("nope");
            }
        };
        ClearService clearService = new ClearService(db);
        RuntimeException ex = assertThrows(RuntimeException.class, clearService::clear);
        assertEquals("nope", ex.getMessage());
    }
}