package de.newrp.Runnable;

import de.newrp.API.DaylightCycle;
import de.newrp.API.Debug;
import de.newrp.API.Script;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncDaylightCycle extends BukkitRunnable {
    @Override
    public void run() {
        DaylightCycle.refreshDaylight();
        String currentWeather = null;
        try {
            String apiKey = "08ce8dd5e80b428283d3cb21bed7db42";
            String city = "Berlin";
            String apiUrl = "http://api.weatherstack.com/current?access_key=" + apiKey + "&query=" + city;

            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            currentWeather = response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String currentWeatherDescription = currentWeather;
        if (currentWeatherDescription.contains("Sunny") || currentWeatherDescription.contains("Partly cloudy") || currentWeatherDescription.contains("Clear") || currentWeatherDescription.contains("Overcast")) {
            Script.WORLD.setStorm(false);
        } else if (currentWeatherDescription.contains("rain") || currentWeatherDescription.contains("Light Rain") || currentWeatherDescription.contains("Light Drizzle") || currentWeatherDescription.contains("Light Drizzle And Rain, Mist, Light") || currentWeatherDescription.contains("Light Rain,") || currentWeatherDescription.contains("Light Drizzle,")) {
            Script.WORLD.setStorm(true);
            Script.WORLD.setThundering(false);
        } else if (currentWeatherDescription.contains("thunder")) {
            Script.WORLD.setStorm(true);
            Script.WORLD.setThundering(true);
        }
    }
}
