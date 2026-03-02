package Service;
import Request.RegisterRequest;
import Result.RegisterResult;
import dataaccess.Database;
import dataaccess.InsertData;
import Request.LoginRequest;
import Result.LogoutResult;
import Result.LoginResult;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// 2 tests per method: 1 negative and 1 positive for each
//6 tests total for userServiceTests
//Methods to test: registering, login, logout

public class UserServiceTests {
    @Test
    //positive test
    //register user successfull
    public void registerSuccess() {
        Database db = new InsertData();
        UserService service = new UserService(db);

        RegisterRequest req = new RegisterRequest("sam", "pass", "sam@email.com");
        RegisterResult res = service.register(req);

        assertNull(res.message());
        assertEquals("sam", res.username());
        assertNotNull(res.authToken());
    }

    @Test
    //negative test
    //that user is already taken
    public void registerAlreadyTaken() {
        Database db = new InsertData();
        UserService service = new UserService(db);

        //registration
        service.register(new RegisterRequest("bob", "pass", "bob@email.com"));

        //registering the same username
        RegisterResult res = service.register(
                new RegisterRequest("bob", "differentPass", "other@email.com")
        );

        assertEquals("Error: already taken", res.message());
        assertNull(res.authToken());
    }

    @Test
    //postive test
    //login works
    public void loginSuccess() {
        Database db = new InsertData();
        UserService service = new UserService(db);

        //register user
        service.register(new RegisterRequest("rob", "pass", "rob@email.com"));

        //login
        LoginResult res = service.login(new LoginRequest("rob", "pass"));

        assertNull(res.message());
        assertEquals("rob", res.username());
        assertNotNull(res.authToken());
    }

    //Negative Test
    //login fails with wrong password
    @Test
    public void loginUnauthorizedWrongPassword() {
        Database db = new InsertData();
        UserService service = new UserService(db);

        service.register(new RegisterRequest("sarah", "pass", "sarah@email.com"));
        LoginResult res = service.login(new LoginRequest("sarah", "wrongPass"));

        assertEquals("Error: unauthorized", res.message());
        assertNull(res.authToken());
    }

    //Positive Test
    //logout succeeds with correct token
    @Test
    public void logoutSuccess() {
        Database db = new InsertData();
        UserService service = new UserService(db);

        RegisterResult reg = service.register(new RegisterRequest("megan", "pass", "megan@email.com"));
        LogoutResult res = service.logout(reg.authToken());

        assertNull(res.message());
    }

    //Negative Test
    //logout fails with invalid token
    @Test
    public void logoutUnauthorizedBadToken() {
        Database db = new InsertData();
        UserService service = new UserService(db);

        LogoutResult res = service.logout("notARealToken");

        assertEquals("Error: unauthorized", res.message());
    }
}