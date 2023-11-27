package org.sonarbot;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConfigurationValues {

    @JsonProperty("loginbody")
    String loginbody;

    @JsonProperty("httppost_req")
    String httppost_req;

    @JsonProperty("bot_token")
    String bot_token;

    @JsonProperty("bot_username")
    String bot_username;

    @JsonProperty("bot_parent_userID")
    Long bot_parent;

    @JsonProperty("urlget_req")
    String get_req;

    @JsonProperty("boolean_test")
    Boolean testb;

    @JsonProperty("initial_get")
    String initialGetReq;

    public Long getBot_parent() {
        return bot_parent;
    }

    public String getBot_token() {
        return bot_token;
    }

    public String getBot_username() {
        return bot_username;
    }

    public String getLoginbody() {
        return loginbody;
    }

    public String getHttppost_req() {
        return httppost_req;
    }

    public String getGet_req(){
        return get_req;
    }
    public Boolean getTestb(){
        return testb;
    }
    public String getInitialGetReq(){
        return initialGetReq;
    }
}
