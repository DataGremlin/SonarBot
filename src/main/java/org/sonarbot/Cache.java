package org.sonarbot;

import org.jsoup.select.Elements;

import java.util.Date;
import java.util.HashMap;


public class Cache {
  public java.util.Date date;
    public TableFetcher t = new TableFetcher();

    HashMap<String, String> updates = new HashMap<>();
  public Long timestamp;

  public Elements notifierCache;

  public boolean firstNote = true;

   public String table;

    public Cache(){
        date = new Date();
        timestamp = date.getTime();
        t.tableFromHtml();
       // t.localTestTableFromHtml();
        table = t.parseAll();
    }

    public String update(String what){
        if(updates.containsKey(what)){

        return updates.get(what);}else {
            return "";
        }


    }





}
