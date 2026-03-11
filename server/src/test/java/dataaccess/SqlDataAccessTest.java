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
        UserData foundUser = sqlDataAccess.getUser("callie");
        assertNotNull(foundUser);
        assertEquals("callie", foundUser.username());
        assertEquals("callie@email.com", foundUser.email());
        assertNotEquals("password123", foundUser.password());
    }

    @Test
    void createUserNegative() {
        sqlDataAccess.createUser(testUser);
        assertThrows(RuntimeException.class, () -> sqlDataAccess.createUser(testUser));
    }

    @Test
    void getUserPositive() {
        sqlDataAccess.createUser(testUser);
        UserData foundUser = sqlDataAccess.getUser("callie");
        assertNotNull(foundUser);
        assertEquals("callie", foundUser.username());
        assertEquals("callie@email.com", foundUser.email());
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
        AuthData foundAuth = sqlDataAccess.getAuth("token123");
        assertNotNull(foundAuth);
        assertEquals("token123", foundAuth.authToken());
        assertEquals("callie", foundAuth.username());
    }

    @Test
    void getAuthPositive() {
        sqlDataAccess.createUser(testUser);
        sqlDataAccess.createAuth(testAuth);
        AuthData foundAuth = sqlDataAccess.getAuth("token123");

        assertNotNull(foundAuth);
        assertEquals("token123", foundAuth.authToken());
        assertEquals("callie", foundAuth.username());
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
        sqlDataAccess.deleteAuth("token123");
        AuthData foundAuth = sqlDataAccess.getAuth("token123");
        assertNull(foundAuth);
    }

    @Test
    void clearPositive() {
        sqlDataAccess.createUser(testUser);
        sqlDataAccess.createAuth(testAuth);
        sqlDataAccess.clear();

        assertNull(sqlDataAccess.getUser("callie"));
        assertNull(sqlDataAccess.getAuth("token123"));
        assertEquals(0, sqlDataAccess.listGames().size());
    }
}