package client;

import com.google.gson.Gson;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import model.*;
import java.util.Collection;

public class ServerFacade {

    private final String serverUrl;
    private final Gson gson = new Gson();

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