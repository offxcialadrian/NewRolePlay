package de.newrp.API;

import java.util.concurrent.TimeUnit;

public class ClearLog {

    public ClearLog() {
        System.out.println("START LOG CLEANING...");
        for (Log l : Log.values()) {
            long time = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(l.getDays());
            Script.executeAsyncUpdate("DELETE FROM " + l.getTable() + " WHERE until<" + time + " OR until IS NULL");
            System.out.println(l + " CLEANED UP.");
        }
        System.out.println("LOG CLEANING FINISHED.");
    }

    public enum Log {
        ALG("arbeitslosengeld", 14);

        private final String table;
        private final int days;

        Log(String table, int days) {
            this.table = table;
            this.days = days;
        }

        public String getTable() {
            return this.table;
        }

        public int getDays() {
            return this.days;
        }
    }

}
