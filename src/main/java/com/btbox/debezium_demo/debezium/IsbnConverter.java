package com.btbox.debezium_demo.debezium;

import cn.hutool.core.date.DateUtil;
import io.debezium.spi.converter.CustomConverter;
import io.debezium.spi.converter.RelationalColumn;
import org.apache.kafka.connect.data.SchemaBuilder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Properties;

public class IsbnConverter implements CustomConverter<SchemaBuilder, RelationalColumn> {

    private SchemaBuilder isbnSchema;

    @Override
    public void configure(Properties props) {
        isbnSchema = SchemaBuilder.string().name(props.getProperty("payload.after"));
    }

    @Override
    public void converterFor(RelationalColumn column,
            ConverterRegistration<SchemaBuilder> registration) {

        System.out.println("column.typeName() = " + column.typeName());
        if ("timestamp".equals(column.typeName())) {
            System.out.println("进来判断");
            registration.register(isbnSchema, x -> {
                String[] parsePatterns = {"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "yyyy-MM-dd HH:mm:ss"};
                Date date = DateUtil.parse(x.toString(), parsePatterns);
                LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
                return localDateTime.toString();
            });
        }
    }
}