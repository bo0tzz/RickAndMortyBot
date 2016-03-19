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
    private static final String SEARCH_URL = "https://rm.kmp.pw/api/search?q=";
    private static final String RANDOM_URL = "https://rm.kmp.pw/api/random";

    public static JSONArray getResults(String query) {
        HttpResponse<JsonNode> response;
        try {
            response = Unirest.get(SEARCH_URL).queryString("q", query).asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
            return null;
        }

        JSONArray results;
        try {
            results = new JSONArray(response.getBody());
        } catch (JSONException e) {
            System.out.println("API returned bad response!");
            e.printStackTrace();
            return null;
        }
        return results;
    }

    public static JSONObject getRandomGIF() {
        HttpResponse<JsonNode> response;
        try {
            response = Unirest.get(RANDOM_URL).asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
            return null;
        }

        JSONObject result;
        try {
            result = new JSONObject(response.getBody());
        } catch (JSONException e) {
            System.out.println("API returned bad response!");
            e.printStackTrace();
            return null;
        }
        return result;
    }
}
