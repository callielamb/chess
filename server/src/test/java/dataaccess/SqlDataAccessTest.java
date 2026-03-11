package dataaccess;
import model.AuthData;
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
    void clearPositive() {
        sqlDataAccess.createUser(testUser);
        sqlDataAccess.createAuth(testAuth);
        sqlDataAccess.clear();

        assertNull(sqlDataAccess.getUser(testAuth.username()));
        assertNull(sqlDataAccess.getAuth(testAuth.authToken()));
        assertEquals(0, sqlDataAccess.listGames().size());
    }
}