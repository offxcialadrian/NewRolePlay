package de.newrp.config.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.newrp.config.IConfigService;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class ConfigService implements IConfigService {

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @Override
    public void saveConfig(File file, Object object, boolean overwrite) {
        if(file.exists() && !overwrite) {
            Bukkit.getLogger().info("Not overwriting " + file.getName() + " because overwrite is false!");
            return;
        }

        try(final FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(this.gson.toJson(object));
            fileWriter.flush();
        } catch(final Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public <T> T readConfig(File file, Class<T> type) {
        try(final FileReader fileReader = new FileReader(file)) {
            return this.gson.fromJson(fileReader, type);
        } catch(final Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean configExists(File file) {
        return file.exists();
    }
}
