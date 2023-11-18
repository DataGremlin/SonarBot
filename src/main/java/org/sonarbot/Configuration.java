package org.sonarbot;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

public class Configuration {
    public static ConfigurationValues loadConfig(){
        ObjectMapper objectMapper = new ObjectMapper();
       // System.out.println(System.getProperties().get("user.dir"));

        //System.out.println(new File("configLocalTest.json").exists());
        try{
        ConfigurationValues cv = objectMapper.readValue(new File("config.json"), ConfigurationValues.class);
        return cv;
        }catch (Exception e){
            System.out.println(e);
            throw new NullPointerException("missing config file");
        }



    }
}
