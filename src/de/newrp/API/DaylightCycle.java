package de.newrp.API;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DaylightCycle {

    private static Date sunriseTime;
    private static Date sunsetTime;

    static {
        String json;
        try {
            json = IOUtils.toString(new URL("http://api.sunrise-sunset.org/json?lat=52.520008&lng=13.404954&formatted=0"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
        if (json == null) {
            try {
                json = IOUtils.toString(new URL("https://api.unicacity.de/daylightCycle/default_daylightCycle.json"));
            } catch (IOException e) {
                e.printStackTrace();
                throw new IllegalStateException(e);
            }
        }
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(json);
        JsonObject jsonObject = jsonElement.getAsJsonObject().get("results").getAsJsonObject();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssX");
        try {
            sunriseTime = sdf.parse(jsonObject.get("sunrise").getAsString());
            sunsetTime = sdf.parse(jsonObject.get("sunset").getAsString());
        } catch (ParseException e) {
            e.printStackTrace();
            sunriseTime = Calendar.getInstance().getTime();
            sunsetTime = Calendar.getInstance().getTime();
        }
    }

    private static long getWorldTime(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(sunsetTime);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        if (date.toInstant().until(new Date(c.getTimeInMillis() + TimeUnit.DAYS.toMillis(1)).toInstant(), ChronoUnit.SECONDS) <= 0) {
            date.setTime(date.getTime() - TimeUnit.DAYS.toMillis(1));
        }

        long differenceToSunrise = sunriseTime.getTime() - date.getTime();
        long differenceToSunset = sunsetTime.getTime() - date.getTime();

        double msDay = sunsetTime.getTime() - sunriseTime.getTime();
        double msPreSunrise = sunriseTime.getTime() - c.getTimeInMillis();
        c.setTimeInMillis(c.getTimeInMillis() + TimeUnit.DAYS.toMillis(1));
        double msPostSunset = c.getTimeInMillis() - sunsetTime.getTime();

        double msPerTickPreSunrise = msPreSunrise / 1900.0;
        double msPerTickDay = msDay / (11616.6 + 1450);
        double msPerTickPostSunset = msPostSunset / 10933.4;

        long ticks = 0;

        if (differenceToSunrise > 0) {
            ticks -= (Math.abs(differenceToSunrise) / msPerTickPreSunrise);
        } else {
            ticks += (Math.abs(Math.max(differenceToSunrise, sunriseTime.getTime() - sunsetTime.getTime())) / msPerTickDay);
        }

        if (differenceToSunset < 0) {
            ticks += Math.abs(differenceToSunset) / msPerTickPostSunset;
        }

        ticks -= 1450;

        if (ticks > 24000)
            ticks -= 24000;

        if (ticks < 0)
            ticks = 24000 - Math.abs(ticks);

        return ticks;
    }

    public static void refreshDaylight() {
        Script.WORLD.setTime(getWorldTime(new Date()));
    }

    public static void setDaylight(Date date) {
        Script.WORLD.setTime(getWorldTime(date));
    }


}
