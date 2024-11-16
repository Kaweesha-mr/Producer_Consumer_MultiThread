package org.spring.multithread_backend.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.io.File;

public class ConfigurationManager {
    private static final String CONFIG_DIR = "src/main/resources/config"; // Custom directory
    private static final String CONFIG_FILE = CONFIG_DIR + File.separator + "config.json"; // Full path
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Save Configuration to JSON
    public static void saveConfiguration(Configuration configuration) {
        try {
            // Ensure the directory exists
            File directory = new File(CONFIG_DIR);
            if (!directory.exists()) {
                directory.mkdirs(); // Create directory if it does not exist
            }

            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(configuration, writer);
                System.out.println("Configuration saved successfully to: " + CONFIG_FILE);
            }
        } catch (IOException e) {
            System.err.println("Failed to save configuration: " + e.getMessage());
        }
    }

    // Load Configuration from JSON
    public static Configuration loadConfiguration() {
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            return GSON.fromJson(reader, Configuration.class);
        } catch (IOException e) {
            System.err.println("Failed to load configuration. Using default values: " + e.getMessage());
            return getDefaultConfiguration();
        }
    }

    // Default Configuration
    private static Configuration getDefaultConfiguration() {
        Configuration defaultConfiguration = new Configuration();
        defaultConfiguration.setTotalTickets(100);
        defaultConfiguration.setTicketReleaseRate(10);
        defaultConfiguration.setCustomerRetrievalRate(5);
        defaultConfiguration.setMaxTicketCapacity(20);
        return defaultConfiguration;
    }
}