package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.List;

public class SqlDataAccess implements Database {

    @Override
    public void createUser(UserData user) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public UserData getUser(String username) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void createAuth(AuthData auth) {
        throw new UnsupportedOperationException("not implemented");
    }
    

    @Override
    public AuthData getAuth(String authToken) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void deleteAuth(String authToken) {
        throw new UnsupportedOperationException("not implemented");
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

    @Override
    public void clear() {
        throw new UnsupportedOperationException("not implemented");
    }
}