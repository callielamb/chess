package client;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Collection;

public class ServerFacade {

    private final String serverUrl;
    private final Gson gson = new Gson();

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public AuthData login(String username, String password) {
        try {

            var request = new LoginRequest(username, password);
            var result = makeRequest("POST", "/session", request, null, LoginResponse.class);

            if (result.message() != null) {
                throw new RuntimeException(result.message());
            }
            return new AuthData(result.authToken(), result.username());
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public AuthData register(String username, String password, String email) {
        try {
            var request = new RegisterRequest(username, password, email);
            var result = makeRequest("POST", "/user", request, null, RegisterResponse.class);

            if (result.message() != null) {
                throw new RuntimeException(result.message());
            }
            return new AuthData(result.authToken(), result.username());
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public void logout(String authToken) {
        try {
            makeRequest("DELETE", "/session", null, authToken, null);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public int createGame(String authToken, String gameName) {
        return 0;
    }

    public Collection<GameData> listGames(String authToken) {
        try {
            var result = makeRequest("GET", "/game", null, authToken, ListGamesResponse.class);

            if (result.message() != null) {
                throw new RuntimeException(result.message());
            }
            return result.games();
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public void joinGame(String authToken, String playerColor, int gameID) {

    }

    private <T> T makeRequest(String method, String path, Object requestBody, String authToken, Class<T> responseClass) throws Exception {
        var url = new URI(serverUrl + path).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setDoOutput(true);
        connection.addRequestProperty("Content-Type", "application/json");

        if (authToken != null) {
            connection.addRequestProperty("Authorization", authToken);
        }

        if (requestBody != null) {
            String json = gson.toJson(requestBody);
            try (OutputStream os = connection.getOutputStream()) {
                os.write(json.getBytes());
            }
        }
        connection.connect();
        int status = connection.getResponseCode();

        if (status >= 400) {
            try (InputStream err = connection.getErrorStream()) {
                if (err != null) {
                    var errorResponse = gson.fromJson(new InputStreamReader(err), Object.class);
                    throw new RuntimeException("Request failed: " + errorResponse);
                }
            }
            throw new RuntimeException("Request failed. Status: " + status);
        }

        if (responseClass == null) {
            return null;
        }
        try (InputStream is = connection.getInputStream()) {
            return gson.fromJson(new InputStreamReader(is), responseClass);
        }
    }
}