package me.bo0tzz.rnmbot;

import com.google.gson.Gson;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.Collections;
import java.util.List;

/**
 * Created by boet on 17-3-2016.
 */
public class APIHandler {
    private static final String SEARCH_URL = "https://rm.kmp.pw/api/search?q=";
    private static final String RANDOM_URL = "https://rm.kmp.pw/api/random";

    public static List<GIFResult> getResults(String query) {
        Gson gson = new Gson();
        String response;
        try {
            response = Unirest.get(SEARCH_URL + query).asString().getBody();
        } catch (UnirestException e) {
            e.printStackTrace();
            return Collections.EMPTY_LIST;
        }

        List<GIFResult> results;
        try {
            results = gson.fromJson(response, new TypeToken<List<GIFResult>>() {}.getType());
        } catch (JsonSyntaxException e) {
            System.out.println("API returned bad response!");
            e.printStackTrace();
            return Collections.EMPTY_LIST;
        }
        return results;
    }

    public static GIFResult getRandomGIF() {
        Gson gson = new Gson();
        String response;
        try {
            response = Unirest.get(RANDOM_URL).asString().getBody();
        } catch (UnirestException e) {
            e.printStackTrace();
            return null;
        }

        GIFResult result;
        try {
            result = gson.fromJson(response, GIFResult.class);
        } catch (JsonSyntaxException e) {
            System.out.println("API returned bad response!");
            e.printStackTrace();
            return null;
        }
        return result;
    }
}
