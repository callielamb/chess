package client;

import model.*;
import java.util.Collection;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public AuthData login(String username, String password) {
        return null;
    }

    public AuthData register(String username, String password, String email) {
        return null;
    }

    public void logout(String authToken) {

    }

    public int createGame(String authToken, String gameName) {
        return 0;
    }

    public Collection<GameData> listGames(String authToken) {
        return null;
    }

    public void joinGame(String authToken, String playerColor, int gameID) {

    }
}