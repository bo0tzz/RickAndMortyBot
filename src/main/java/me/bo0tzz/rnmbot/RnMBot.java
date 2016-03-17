package me.bo0tzz.rnmbot;

import pro.zackpollard.telegrambot.api.TelegramBot;

/**
 * Created by boet on 17-3-2016.
 */
public class RnMBot {
    private final String key;
    private TelegramBot bot;

    public static void main(String[] args) {
        new RnMBot(args[0]).run();
    }

    public RnMBot(String key) {
        this.key = key;
    }

    public void run() {
        //Initialise telegram API connection
        System.out.println("Starting bot");
        this.bot = TelegramBot.login(key);
        bot.getEventsManager().register(new RnMListener(this));
        bot.startUpdates(false);
        System.out.println("Bot started");

        //Start sleeper thread
        new Thread(() -> {
            try {
                Thread.sleep(Integer.MAX_VALUE);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("Sleeper thread got interrupted!");
                System.exit(1);
            }
        }).start();
    }

    public TelegramBot getTelegramBot() {
        return bot;
    }
}
