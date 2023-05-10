package com.btbox.debezium_demo.debezium;

import io.debezium.relational.history.FileDatabaseHistory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/5/6 14:40
 * @version: 1.0
 */
@Configuration
public class PostgresqlConfig {

    @Bean
    public io.debezium.config.Configuration myPgConnector() {
        return io.debezium.config.Configuration.create()
                .with("name", "my_pg_connector")
                .with("connector.class", "io.debezium.connector.postgresql.PostgresConnector")
                .with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
                .with("offset.storage.file.filename", "D:/debezium/qm/offsets/offset.dat")
                .with("offset.flush.interval.ms", "60000")
               .with("database.history", FileDatabaseHistory.class.getName())
                .with("database.history.file.filename", "D:/debezium/qm/history/custom-file-db-history.dat")
                .with("snapshot.mode", "initial")
                .with("database.server.name", "my_pg_connector_server")
                .with("database.hostname", "10.0.0.91")
                .with("database.port", "5451")
                .with("database.user", "root")
                .with("database.password", "ht83336168!")
                .with("database.dbname", "test")
                .with("table.exclude.list", "public.log")

                .with("topic.prefix", "pgtest")
//                with("table.include.list", tableList)

                // .with("transforms", "pg-to-java-timestamp")
                // .with("transforms.pg-to-java-timestamp.type", "com.btbox.debezium_demo.debezium.PostgresToJavaTimestampConverter")
                // .with("transforms.pg-to-java-timestamp.column.name", "update_time")

                .with("converters", "isbn")
                .with("isbn.type", "com.btbox.debezium_demo.debezium.IsbnConverter")

                .with("isbn.schema.name", "io.debezium.postgresql.type.Isbn")


                .build();

    }

}