package me.bo0tzz.rnmbot;

import org.json.JSONArray;
import org.json.JSONObject;
import pro.zackpollard.telegrambot.api.chat.inline.send.InlineQueryResponse;
import pro.zackpollard.telegrambot.api.chat.inline.send.results.InlineQueryResult;
import pro.zackpollard.telegrambot.api.chat.inline.send.results.InlineQueryResultGif;
import pro.zackpollard.telegrambot.api.chat.message.send.InputFile;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableVideoMessage;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.inline.InlineQueryReceivedEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by boet on 17-3-2016.
 */
public class RnMListener implements Listener {
    private final RnMBot main;
    private final Map<String, Consumer<CommandMessageReceivedEvent>> commands = new HashMap<String, Consumer<CommandMessageReceivedEvent>>(){{
        RnMListener that = RnMListener.this;
        put("get", that::getGIF);
        put("random", that::randomGIF);
    }};

    public RnMListener(RnMBot main) {
        this.main = main;
    }

    public void onInlineQueryReceived(InlineQueryReceivedEvent event) {
        if (event.getQuery().getQuery().equals("")) {
            return;
        }
        System.out.println("Inline query received: " + event.getQuery().getQuery());
        List<InlineQueryResult> queryResults = new ArrayList<>();
        System.out.println("Results list instantiated");
        JSONArray gifResults = APIHandler.getResults(event.getQuery().getQuery());
        System.out.println("Got results JSON");
        if (gifResults.length() == 0) {
            System.out.println("Results was empty!");
            return;
        }

        System.out.println("Starting element looping");
        for (int i = 0; i < gifResults.length(); i++) {
            try {
                System.out.println("Element " + i);
                if (gifResults.isNull(i)) {
                    System.out.println("Was null!");
                    break;
                }
                System.out.println("Creating URL");
                URL url = new URL(gifResults.getJSONObject(i).getString("url"));
                System.out.println("URL is " + url);
                queryResults.add(InlineQueryResultGif.builder()
                        .gifUrl(url)
                        .thumbUrl(url)
                        .caption(gifResults.getJSONObject(i).getString("text"))
                        .build()
                );
                System.out.println("Added result to list");
            } catch (MalformedURLException e) {
                System.out.println("MalformedURLException");
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("Error on JSONObject: " + gifResults.getJSONObject(i) + " - with query: " + event.getQuery().getQuery());
                e.printStackTrace();
            }
        }
        System.out.println("Finished element looping");
        System.out.println("building response");
        System.out.println("Results list: ");
        queryResults.forEach((r) -> {
            System.out.println(r);
        });
        InlineQueryResponse response = InlineQueryResponse.builder()
                .results(queryResults)
                .build();

        event.getQuery().answer(main.getTelegramBot(), response);
    }

    public void onCommandMessageReceived(CommandMessageReceivedEvent event) {
        commands.getOrDefault(event.getCommand(), (e) -> {}).accept(event);
    }

    public void getGIF(CommandMessageReceivedEvent event) {
        if (event.getArgsString() == null || event.getArgsString().equals("")) {
            event.getChat().sendMessage("Don't forget to enter a search term! Usage: /get your search term here", main.getTelegramBot());
            return;
        }

        JSONArray results = APIHandler.getResults(event.getArgsString());
        if (results == null) {
            event.getChat().sendMessage("Something went wrong while getting the gif! If this happens again, contact the bot maintainer at @bo0tzz.", main.getTelegramBot());
            return;
        } else if (results.length() == 0) {
            event.getChat().sendMessage("Couldn't find a gif! Try again with a different term.", main.getTelegramBot());
            return;
        }

        JSONObject result = results.getJSONObject(0);
        InputFile gif;
        try {
            gif = new InputFile(new URL(result.getString("url")));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            event.getChat().sendMessage("Something went wrong while getting the gif! If this happens again, contact the bot maintainer at @bo0tzz.", main.getTelegramBot());
            return;
        }
        event.getChat().sendMessage(SendableVideoMessage.builder().video(gif).caption(result.getString("text")).build(), main.getTelegramBot());
    }

    public void randomGIF(CommandMessageReceivedEvent event) {
        JSONObject result = APIHandler.getRandomGIF();
        if (result == null) {
            event.getChat().sendMessage("Something went wrong while getting the gif! If this happens again, contact the bot maintainer at @bo0tzz.", main.getTelegramBot());
            return;
        }
        InputFile gif;
        try {
            gif = new InputFile(new URL(result.getString("url")));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            event.getChat().sendMessage("Something went wrong while getting the gif! If this happens again, contact the bot maintainer at @bo0tzz.", main.getTelegramBot());
            return;
        }
        event.getChat().sendMessage(SendableVideoMessage.builder().video(gif).caption(result.getString("url")).build(), main.getTelegramBot());
    }
}
