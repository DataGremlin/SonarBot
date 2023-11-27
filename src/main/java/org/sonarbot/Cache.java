package org.sonarbot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class Cache {
  public java.util.Date date;
    public TableFetcher t = new TableFetcher();

    HashMap<String, String> updates = new HashMap<>();
  public Long timestamp;

  public Elements notifierCache;

 // public List<String> currentStudien;



  public List<String> studienCache = new ArrayList<>();

  public boolean firstNote = true;

   public String table;

    public Cache(){
        try {
            date = new Date();
            timestamp = date.getTime();
            t.tableFromHtml();
            // t.localTestTableFromHtml();
            table = t.parseAll();
            readStudienCache();
        }catch (Exception e){
            System.out.println(e);
        }

    }

    public void readStudienCache(){
        if(new File("studienCache.json").exists()){
            try {

                ObjectMapper mapper = new ObjectMapper();

                TypeReference<List<String>> typeRef
                        = new TypeReference<List<String>>() {
                };

                studienCache = mapper.readValue(Paths.get("studienCache.json").toFile(), typeRef);
            }catch (Exception e){
                System.out.println(e + "\n loading old studies failed");
            }}

    }


    public void writeNewStudienCache(List<String> neueStudien){

        try {


            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(neueStudien);


            File f = new File("studienCache.json");

            FileWriter myWriter = new FileWriter(f);
            myWriter.write(json);
            myWriter.close();
        }catch (Exception e){
            System.out.println(e + " \n Writing into Studien Cache failed");
        }

    }

    public List<String> getCurrentStudienElementsToList(){
        List<String> currentStudien = new ArrayList<>();

        for (int i = 0; i < t.elements.size(); i++) {
           currentStudien.add( t.elements.get(i).children().get(1).text());

        }
        return currentStudien;

    }

    public String getUpdateForKuerzel(String kuerzel){
        if(updates.containsKey(kuerzel)){

        return updates.get(kuerzel);}else {
            return "";
        }


    }





}
