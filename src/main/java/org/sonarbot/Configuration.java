package org.sonarbot;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

public class Configuration {
    public static ConfigurationValues loadConfig(){
        ObjectMapper objectMapper = new ObjectMapper();

        try{
        ConfigurationValues cv = objectMapper.readValue(new File("config.json"), ConfigurationValues.class);
        return cv;
        }catch (Exception e){
            throw new NullPointerException("missing config file");
        }



    }
}
