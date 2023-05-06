package com.btbox.debezium_demo.debezium;

import io.debezium.config.Configuration;
import io.debezium.embedded.EmbeddedEngine;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Json;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executors;


@Component
public class PostgresqlListener {


    private final DebeziumEngine<ChangeEvent<String, String>> embeddedEngine;

    private PostgresqlListener(@Qualifier("myPgConnector") Configuration config) {

        embeddedEngine = DebeziumEngine.create(Json.class)
                .using(config.asProperties())
                .notifying(record -> {
                    receiveChangeEvent(record.value(), config.getString("debezium.name"));
                })
                .build();
    }

    private void receiveChangeEvent(String value, String name) {
        if (Objects.nonNull(value)) {
            System.out.println("value = " + value);
        }
    }

    @PostConstruct
    private void start() {
        Executors.newSingleThreadExecutor().execute(embeddedEngine);
    }

    @PreDestroy
    private void stop() {
        if (embeddedEngine != null) {
            try {
                embeddedEngine.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}