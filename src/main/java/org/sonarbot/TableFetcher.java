package org.sonarbot;


import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.nio.charset.StandardCharsets;

import java.util.List;

public class TableFetcher {



    public Elements elements;

    public String rawHtml;


    public void localTestTableFromHtml() {
        CookieStore co = new BasicCookieStore();

        HttpClientBuilder builder = HttpClientBuilder.create().setDefaultCookieStore(co).setDefaultRequestConfig(RequestConfig.custom().setConnectTimeout(3000).setConnectionRequestTimeout(3000).setSocketTimeout(3000).build());
        try {


            CloseableHttpClient c = builder.build();


            CloseableHttpResponse last = c.execute(new HttpGet(Configuration.loadConfig().getGet_req()));

            String s = new String(last.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);

            rawHtml = s;
            elements = Jsoup.parse(rawHtml).body().getElementsByAttributeValue("class", "table table-bordered table-striped").first().children().get(1).children();


        } catch (Exception e) {
            System.out.println(e);
        }

    }


    public void tableFromHtml() {

        if(Configuration.loadConfig().getTestb()){

            localTestTableFromHtml();



        }else {
            CookieStore co = new BasicCookieStore();
            HttpClientBuilder builder = HttpClientBuilder.create().setDefaultCookieStore(co).setDefaultRequestConfig(RequestConfig.custom().setConnectTimeout(3000).setConnectionRequestTimeout(3000).setSocketTimeout(3000).build());


            CloseableHttpClient c = builder.build();

            CloseableHttpResponse http = null;

            try {
                http = c.execute(new HttpGet("https://psywue.sona-systems.com/Default.aspx?ReturnUrl=%2fall_exp_participant.aspx"));

                http.close();


                HttpPost h = new HttpPost(Configuration.loadConfig().getHttppost_req());


                h.setEntity(new StringEntity(Configuration.loadConfig().getLoginbody(), ContentType.create("application/x-www-form-urlencoded")));
                CloseableHttpResponse close = c.execute(h);
                System.out.println(close.getStatusLine().toString());
                close.close();


                CloseableHttpResponse last = c.execute(new HttpGet(Configuration.loadConfig().getGet_req()));

                System.out.println(last.getStatusLine().toString());

                String s = new String(last.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);

                rawHtml = s;
                elements = Jsoup.parse(rawHtml).body().getElementsByAttributeValue("class", "table table-bordered table-striped").first().children().get(1).children();


            } catch (Exception e) {
                System.out.println(e);
            }


            //System.out.println(co.getCookies());

        }
    }

    public String parseAll() {


        String table = "";
        for (int i = 0; i < elements.size(); i++) {
            table = table.concat(elements.get(i).children().get(1).text() + "\n\n");

        }


        return table;
    }

    public static String formatKuerzel(String info) {

        String text;
        if (info.equals("PsyErgo") || info.equals("/PsyErgo") || info.equals("PSYERGO") || info.equals("psyergo")) {
            text = "PsyErgo";
        } else {
            text = info.toUpperCase();
            if (text.startsWith("/")) {
                text = text.substring(1);
            }
        }
        return text;

    }

    public String parseInfo(String info, List<String> studien) {
        info = formatKuerzel(info);
        String res = "";
        for (int i = 0; i < studien.size(); i++) {
            if (studien.get(i).contains(info)) {
                res = res.concat(studien.get(i) + "\n\n");
            }

        }
        if (res.isEmpty()) {
            return "Keine passende Studie gefunden" + "\n \n" + "/help";

        }

        return res.stripTrailing();

    }

    public String parseInfo(String info) {

        String res = "";

        String text = formatKuerzel(info);

        if (info.length() == 1) {
            return "Keine passende Studie gefunden" + "\n \n" + "/help";
        }
        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i).children().get(1).text().contains(text)) {
                res = res.concat(elements.get(i).children().get(1).text() + "\n \n");
            }
            ;


        }
        if (res.isEmpty()) {
            return "Keine passende Studie gefunden" + "\n \n" + "/help";

        }
        return res.stripTrailing();


    }
}
