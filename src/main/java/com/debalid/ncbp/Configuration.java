package com.debalid.ncbp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by debalid on 20.04.2016.
 */
public class Configuration {
    private static final String configName = "application.properties";
    private static Configuration instance;

    private Properties properties;

    private Configuration() {
        try (InputStream input = Configuration.class.getResourceAsStream("/" + Configuration.configName)) {
            this.properties = new Properties();
            this.properties.load(input);
        } catch (IOException e) {
            // TODO: handle?
        }
    }

    public static String get(String key) {
        if (instance == null) {
            synchronized (configName) {
                if (instance == null) {
                    instance = new Configuration();
                }
            }
        }
        return instance.properties.getProperty(key);
    }
}
