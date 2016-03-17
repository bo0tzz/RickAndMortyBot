package me.bo0tzz.rnmbot;

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
        List<InlineQueryResult> queryResults = new ArrayList<>();
        List<GIFResult> gifResults = APIHandler.getResults(event.getQuery().getQuery());
        if (gifResults == null) {
            return;
        }

        for (GIFResult gif : gifResults) {
            try {
                queryResults.add(InlineQueryResultGif.builder()
                        .gifUrl(new URL(gif.getUrl()))
                        .caption(gif.getText())
                        .build()
                );
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        InlineQueryResponse response = InlineQueryResponse.builder()
                .results(queryResults)
                .build();

        event.getQuery().answer(main.getTelegramBot(), response);
    }

    public void onCommandMessageReceived(CommandMessageReceivedEvent event) {
        commands.getOrDefault(event.getCommand(), (e) -> {}).accept(event);
    }

    public void getGIF(CommandMessageReceivedEvent event) {
        if (event.getArgsString() == null) {
            event.getChat().sendMessage("Don't forget to enter a search term! Usage: /getgif your search term here", main.getTelegramBot());
            return;
        }
        GIFResult result = APIHandler.getResults(event.getArgsString()).get(0);
        if (result == null) {
            event.getChat().sendMessage("Couldn't find a gif! Try again with a different term.", main.getTelegramBot());
            return;
        }
        InputFile gif;
        try {
            gif = new InputFile(new URL(result.getUrl()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            event.getChat().sendMessage("Something went wrong while getting the gif! If this happens again, contact the bot maintainer at @bo0tzz.", main.getTelegramBot());
            return;
        }
        event.getChat().sendMessage(SendableVideoMessage.builder().video(gif).caption(result.getText()).build(), main.getTelegramBot());
    }

    public void randomGIF(CommandMessageReceivedEvent event) {
        GIFResult result = APIHandler.getRandomGIF();
        InputFile gif;
        try {
            gif = new InputFile(new URL(result.getUrl()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            event.getChat().sendMessage("Something went wrong while getting the gif! If this happens again, contact the bot maintainer at @bo0tzz.", main.getTelegramBot());
            return;
        }
        event.getChat().sendMessage(SendableVideoMessage.builder().video(gif).caption(result.getText()).build(), main.getTelegramBot());
    }
}
