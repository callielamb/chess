package service;

import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.LogoutResult;
import result.RegisterResult;
import dataaccess.Database;
import model.AuthData;
import model.UserData;
import java.util.UUID;

public class UserService {

    private final Database database;

    public UserService(Database database) {
        this.database = database;
    }

    public RegisterResult register(RegisterRequest request) {

        if (request == null || request.username() == null || request.password() == null || request.email() == null) {
            return new RegisterResult(null, null, "Error: bad request");
        }

        UserData existingUser = database.getUser(request.username());
        if (existingUser != null) {
            return new RegisterResult(null, null, "Error: already taken");
        }

        UserData newUser = new UserData(request.username(), request.password(), request.email());
        database.createUser(newUser);

        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, request.username());
        database.createAuth(auth);
        return new RegisterResult(request.username(), authToken, null);
    }

    public LoginResult login(LoginRequest request) {

        if (request == null || request.username() == null || request.password() == null) {
            return new LoginResult(null, null, "Error: bad request");
        }
        UserData user = database.getUser(request.username());

        if (user == null || !user.password().equals(request.password())) {
            return new LoginResult(null, null, "Error: unauthorized");
        }

        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, request.username());
        database.createAuth(auth);

        return new LoginResult(request.username(), authToken, null);
    }

    public LogoutResult logout(String authToken) {

        if (authToken == null) {
            return new LogoutResult("Error: unauthorized");
        }

        if (database.getAuth(authToken) == null) {
            return new LogoutResult("Error: unauthorized");
        }

        database.deleteAuth(authToken);
        return new LogoutResult(null);
    }
}