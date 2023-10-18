package org.sonarbot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bot extends TelegramLongPollingBot {

    private boolean info = false;

    public Cache c;

    final Logger logger = LoggerFactory.getLogger(Bot.class);

    public HashMap<Long, List<String>> notificationList = new HashMap<>();


    public InlineKeyboardMarkup keyboardM1;


    @Override
    public String getBotUsername() {
        return Configuration.loadConfig().getBot_username();
    }

    @Override
    public String getBotToken() {
        return Configuration.loadConfig().getBot_token();
    }

    @Override
    public void onUpdateReceived(Update update) {
        try{



       long id;
     //  logger.error("Hallo \n");
       logger.info("logger ist hier \n");





        if(update.hasCallbackQuery()) {

            id = update.getCallbackQuery().getFrom().getId();
            String data = update.getCallbackQuery().getData();
            buttonTap(id, update.getCallbackQuery().getId(), data);
            StringBuilder s = new StringBuilder();
            notificationList.get(id).stream().forEach(v -> s.append( " " + v + ","));
            s.deleteCharAt(s.length() - 1);

            sendText(id, "Du wirst nun über den Notifier zu Studien der Art:" + s.toString() + " benachrichtigt" );

            String inf = c.t.parseInfo(data.toUpperCase());
            if(!inf.contains("Keine passende Studie gefunden")){
                sendText(id, inf);
            }

        }else {
            Message msg = update.getMessage();
            User user = update.getMessage().getFrom();
             id = user.getId();










        String mesag = msg.getText().toUpperCase();
        //if(msg.isCommand()){
            if(mesag.contains("INFO") || mesag.contains("HELP")){
               // info = true;
                infoDump(id);
            }else if(mesag.contains("DROP NOTIFIER") || msg.getText().equals("/drop_notifier")) {
                stopNotifyMe(id);
                sendText(id, "Du wirst nicht mehr durch den Notifier benachrichtigt" );
            }else if(mesag.contains("NOTIFY")){
                sendMenu(id, "Über welche Art von Studie möchtest du benachrichtigt werden?", keyboardM1);
               // notifyMe(id, );

            }
            else if(mesag.contains("HALLO")){
                sendText(id, "Hallo, ich bin ein Bot. Ich kann dir Infos über Studien des MCM bereitsstellen.\nSchreibe /help für eine Bedienungshilfe.");

            }
            else if(mesag.contains("ALL")){

                getAll(id);

                sendText(id, c.table);
            }else {

                getAll(id);

               String s = c.t.parseInfo(msg.getText());

                sendText(id,s );

            }


        }
        }catch(Exception e){

            System.out.println(e);
        }


    }






    private synchronized void getAll(Long id){
       Date d = new Date();



        if(c.t.rawHtml == null || c.timestamp < d.getTime() - 5 * 60 * 1000) {
           c.t.tableFromHtml();

           //c.t.localTestTableFromHtml();



            c.timestamp = d.getTime();
            c.table = c.t.parseAll();





        }

    }

    private void infoDump(Long id){
        sendText(id, "Mit den folgenden Kürzeln kannst du nach Studien suchen:\n\nStudienkürzel MCM:\n\n /PIIS, /MWK, /MI, /HCI, /KPNM, /MP, /PsyErgo \n\n /all für alle verfügbaren Studien \n\n /notify um über verfügbare Studien benachrichtigt zu werden \n\n /drop_notifier um keine Notifications mehr zu bekommen");


    }

    public void sendMenu(Long who, String txt, InlineKeyboardMarkup kb){
        SendMessage sm = SendMessage.builder().chatId(who.toString())
                .parseMode("HTML").text(txt)
                .replyMarkup(kb).build();



        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }





    public void sendText(Long who, String what){
        SendMessage sm = SendMessage.builder().chatId(who.toString()).text(what).build();

     try {
        execute(sm);                        
    } catch (
    TelegramApiException e) {
        throw new RuntimeException(e);      
    }


}

    private void buttonTap(Long id, String queryId, String data) {



        notifyMe(id, data);



        AnswerCallbackQuery close = AnswerCallbackQuery.builder()
                .callbackQueryId(queryId).build();
        try {
            execute(close);
        }catch (Exception e){}



    }

    public void notifyMe(long who, String str){
        ArrayList<String> strs= new ArrayList<>();
       str =  str.toUpperCase();
        strs.add(str);

       if( notificationList.containsKey(who)) {
           if(notificationList.get(who).contains(str)){
               return;
           }else {
           notificationList.get(who).add(str);}
       }
        notificationList.putIfAbsent(who, strs);

       logToJson();




    }
    public void stopNotifyMe(long id){

        notificationList.remove(id);
        logToJson();

    }


    public void pollThisWebsite(Cache c){
        String[] kuerzel= { "PIIS", "MWK", "MI", "HCI", "KPNM", "MP", "PsyErgo"};


        ScheduledExecutorService executorService = Executors
                .newSingleThreadScheduledExecutor();

        Bot b = this;
        try {
            executorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {

                    System.out.println(LocalDateTime.now() + "checking the notifications");

                    c.t.tableFromHtml();
                    c.table = c.t.parseAll();
                    c.timestamp = new Date().getTime();




                    if (c.firstNote) {
                        c.notifierCache = c.t.elements.clone();
                        c.firstNote = false;
                    }


                    ArrayList<String> studien = new ArrayList<>();
                    ArrayList<String> neueStudien = new ArrayList<>();
                    for (int i = 0; i < c.notifierCache.size(); i++) {
                        studien.add(c.notifierCache.get(i).children().get(1).text());


                    }
                    for (int i = 0; i < c.t.elements.size(); i++) {
                        neueStudien.add(c.t.elements.get(i).children().get(1).text());


                    }


                    for (int i = 0; i < studien.size(); i++) {
                        if (neueStudien.contains(studien.get(i))) {
                            neueStudien.remove(studien.get(i));
                        }

                    }
                 //   logger.info(studien.toString());
                    //logger.info(neueStudien.toString());


                    c.updates.clear();


                    for (String str : kuerzel
                    ) {
                        String s = c.t.parseInfo(str, neueStudien);

                        if (s.contains("Keine passende Studie gefunden")) {

                        } else {
                            c.updates.put(str, s);


                        }

                    }
                    b.notificationList.keySet().stream().forEach(k -> b.notificationList.get(k).stream().forEach(i -> { if(c.update(i).isEmpty()){

                    }else {
                        b.sendText(k, c.update(i));}
                    }));

                    c.notifierCache =   c.t.elements.clone();
                    logger.info(c.updates.toString());

                    System.out.println(LocalDateTime.now() +"finish notification");

                }
            }, 1, 60, TimeUnit.MINUTES);
        }catch (Exception e){
            System.out.println( "Polling issue " + e);
        }
    }

    public void localtestpollThisWebsite(Cache c){
        String[] kuerzel= { "PIIS", "MWK", "MI", "HCI", "KPNM", "MP", "PsyErgo"};


        ScheduledExecutorService executorService = Executors
                .newSingleThreadScheduledExecutor();

        Bot b = this;
        try {
            executorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {

                    System.out.println(LocalDateTime.now().toString() + "checking the notifications");

                    c.t.localTestTableFromHtml();
                    c.table = c.t.parseAll();
                    c.timestamp = new Date().getTime();





                    if (c.firstNote) {
                        c.notifierCache = c.t.elements.clone();
                        c.firstNote = false;
                    }


                    ArrayList<String> studien = new ArrayList<>();
                    ArrayList<String> neueStudien = new ArrayList<>();
                    for (int i = 0; i < c.notifierCache.size(); i++) {
                        studien.add(c.notifierCache.get(i).children().get(1).text());


                    }
                    for (int i = 0; i < c.t.elements.size(); i++) {
                        neueStudien.add(c.t.elements.get(i).children().get(1).text());


                    }


                    for (int i = 0; i < studien.size(); i++) {
                        if (neueStudien.contains(studien.get(i))) {
                            neueStudien.remove(studien.get(i));
                        }

                    }
                  //  logger.info(studien.toString());
                  //  logger.info(neueStudien.toString());


                    c.updates.clear();


                    for (String str : kuerzel
                    ) {
                        String s = c.t.parseInfo(str, neueStudien);

                        if (s.contains("Keine passende Studie gefunden")) {

                        } else {
                            c.updates.put(str, s);


                        }

                    }
                    b.notificationList.keySet().stream().forEach(k -> b.notificationList.get(k).stream().forEach(i -> { if(c.update(i).isEmpty()){

                    }else {
                        b.sendText(k, c.update(i));}
                    }));

                    c.notifierCache =   c.t.elements.clone();
                    logger.info(c.notifierCache.toString() + "Next:" + c.updates.toString());

                    System.out.println(LocalDateTime.now() +"finish notification");

                }
            }, 1, 3, TimeUnit.MINUTES);
        }catch (Exception e){
            System.out.println( "Polling issue " + e);
        }

    }

    public void logToJson(){
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String json = objectMapper.writeValueAsString(notificationList);


            File f = new File("notifier.json");

            FileWriter myWriter = new FileWriter(f);
            myWriter.write(json);
            myWriter.close();

        }catch (Exception e){

            System.out.println(e);
        }

    }

    public void getMapFromJson(){

        ObjectMapper mapper = new ObjectMapper();

        try {

           

            TypeReference<HashMap<Long,List<String>>> typeRef
                    = new TypeReference<HashMap<Long,List<String>>>() {};

            Map<Long, List<String>> map = mapper.readValue(Paths.get("notifier.json").toFile(), typeRef);

            notificationList.putAll(map);


        }catch (Exception e){
            System.out.println(e);
        }


    }






}
