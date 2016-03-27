package me.bo0tzz.rnmbot;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by boet on 17-3-2016.
 */
public class APIHandler {
    private static final String SEARCH_URL = "https://sl4ry3kp0k.execute-api.eu-west-1.amazonaws.com/prod/search?q=";
    private static final String RANDOM_URL = "https://sl4ry3kp0k.execute-api.eu-west-1.amazonaws.com/prod/random";

    public static JSONArray getResults(String query) {
        HttpResponse<JsonNode> response;
        try {
            response = Unirest.get(SEARCH_URL).queryString("q", query).asJson();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if (response.getBody().getObject().has("errorMessage")) {
            System.out.println("Query " + query + " failed with error message: " + response.getBody().getObject().getString("errorMessage"));
            return null;
        }
        return response.getBody().getArray();
    }

    public static JSONObject getRandomGIF() {
        HttpResponse<JsonNode> response;
        try {
            response = Unirest.get(RANDOM_URL).asJson();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (response.getBody().getObject().optString("message").equals("Missing Authentication Token")) {
            return null;
        }
        return response.getBody().getObject();
    }
}
