package de.newrp.config.impl;

import de.newrp.config.IConfigService;

import java.io.File;

public class ConfigService implements IConfigService {
    @Override
    public void saveConfig(File file, Object object, boolean overwrite) {

    }

    @Override
    public <T> T readConfig(File file, Class<T> type) {
        return null;
    }

    @Override
    public boolean configExists(File file) {
        return false;
    }
}
