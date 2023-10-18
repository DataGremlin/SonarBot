# SONAR bot

This bot notifies you of available studies of the MCM institute at JMU Wuerzburg.
# How to reach the bot
The bot is available here:

[https://t.me/SONARwue_bot](https://t.me/SONARwue_bot)

@SONARwue_bot

# Compile yourself

This is a Maven Project. 
To compile it yourself package to .jar via Maven and rename **SAMPLEconfig.json** to **config.json** and add your config data. Replace everything between angle brackets with the designated content. Be sure to insert username and password of your Sona User at JMU Wuerzburg into the `loginbody` String here: 

```
userid=<insert SONA username>&ctl00%24ContentPlaceHolder1%24pw=<insert SONA password>
```
and to the `httppost_req` String here: 
```
p_log=<insert SONA username>&p_log=<insert SONA username>"
```

## Running it

Run the jar file with config in the working directory
```
cd sonarbot
java -jar ./target/...jar
```


