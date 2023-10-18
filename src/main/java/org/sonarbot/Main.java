package org.sonarbot;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.File;
import java.util.*;


public class Main {



    public static void makeKeyboard(Bot bot){
        var hci = InlineKeyboardButton.builder()
                .text("HCI").callbackData("hci")
                .build();

        var piis = InlineKeyboardButton.builder()
                .text("PIIS").callbackData("piis")
                .build();
        var mp = InlineKeyboardButton.builder()
                .text("MP").callbackData("mp")
                .build();
        var mwk = InlineKeyboardButton.builder()
                .text("MWK").callbackData("mwk")
                .build();
        var mi = InlineKeyboardButton.builder()
                .text("MI").callbackData("mi")
                .build();
        var kpnm = InlineKeyboardButton.builder()
                .text("KPNM").callbackData("kpnm")
                .build();
        var psyergo = InlineKeyboardButton.builder()
                .text("PsyErgo").callbackData("psyergo")
                .build();

        bot.keyboardM1 = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(hci, piis, mp, psyergo, mi, mwk, kpnm)).build();


    }



    public static void main(String[] args) throws TelegramApiException {




        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        Bot bot = new Bot();

        ConfigurationValues cv = Configuration.loadConfig();




        bot.c = new Cache();


        botsApi.registerBot(bot);
        if(new File("notifier.json").exists()){
        bot.getMapFromJson(); }

         bot.pollThisWebsite(bot.c);

      //  bot.localtestpollThisWebsite(bot.c);


        makeKeyboard(bot);
        bot.sendText(cv.getBot_parent(), "Hallo ich bin jetzt erreichbar");








    }















}