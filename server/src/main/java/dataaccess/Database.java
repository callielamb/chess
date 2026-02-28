package dataaccess;

import model.UserData;
import model.AuthData;
import model.GameData;

import java.util.List;

public interface Database {

    //Data
    void createUser(UserData user);
    UserData getUser(String username);

    //Authentication
    void createAuth(AuthData auth);
    AuthData getAuth(String authToken);
    void deleteAuth(String authToken);

    //Game
    int createGame(GameData game);
    GameData getGame(int gameID);
    List<GameData> listGames();
    void updateGame(GameData game);

    //Clear
    void clear();
}