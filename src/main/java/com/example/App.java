package com.example;

import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.ConfigurationConverter;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Printed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws ConfigurationException {
        LOG.info("App started");

        var streamsConfig = loadConfiguration(
            "/app-config/env-secrets-app-config.properties",
            "/app-config/env-app-config.properties",
            "default-app-config.properties"
        );
        LOG.info("config: {}", streamsConfig);

        final var streamsBuilder = new StreamsBuilder();
        streamsBuilder.stream("input-topic").print(Printed.toSysOut());
        final var topology = streamsBuilder.build();
        final var streamsApp = new KafkaStreams(topology, streamsConfig);
        streamsApp.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streamsApp::close));
    }

    public static Properties loadConfiguration(String... configFileNames) {
        final var config = new CompositeConfiguration();
        for (String fileName : configFileNames) {
            try {
                var fileConfig = new Configurations().properties(fileName);
                config.addConfiguration(fileConfig);
                LOG.info("Loaded app configuration from {}", fileName);
            } catch (ConfigurationException ex) {
                LOG.warn("Could not load app configuration from {}", fileName);
            }
        }

        return ConfigurationConverter.getProperties(config);
    }
}
