package de.newrp.config;

import java.io.File;

public interface IConfigService {

    void saveConfig(final File file, final Object object, final boolean overwrite);

    <T> T readConfig(final File file, final Class<T> type);

    boolean configExists(final File file);

}
