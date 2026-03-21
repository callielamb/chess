package client;

import model.GameData;
import java.util.List;

record LoginResponse(String username, String authToken, String message) {}
record RegisterResponse(String username, String authToken, String message) {}
record CreateGameResponse(Integer gameID, String message) {}
record ListGamesResponse(List<GameData> games, String message) {}
record JoinGameResponse(String message) {}
record ErrorResponse(String message) { }