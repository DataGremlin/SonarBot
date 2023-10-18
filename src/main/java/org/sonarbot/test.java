package org.sonarbot;

public class test {

    public static void main(String[] args) {
        TableFetcher t = new TableFetcher();
        t.localTestTableFromHtml();
        String table = "";
        for (int i = 0; i < t.elements.size(); i++) {
            table = table.concat( t.elements.get(i).children().get(1).text() + "\n\n");

        }

        //System.out.println(table);
    }



}
