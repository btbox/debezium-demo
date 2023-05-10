// package com.btbox.debezium_demo.debezium;
//
// import java.time.LocalDateTime;
// import java.time.ZoneOffset;
// import java.util.Calendar;
// import java.util.GregorianCalendar;
// import java.util.Map;
//
// import org.apache.kafka.common.config.ConfigDef;
// import org.apache.kafka.connect.connector.ConnectRecord;
// import org.apache.kafka.connect.data.Field;
// import org.apache.kafka.connect.data.Schema;
// import org.apache.kafka.connect.data.SchemaBuilder;
// import org.apache.kafka.connect.data.Struct;
// import org.apache.kafka.connect.errors.DataException;
// import org.apache.kafka.connect.sink.SinkRecord;
// import org.apache.kafka.connect.transforms.Transformation;
// import org.apache.kafka.connect.transforms.util.SchemaUtil;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
//
// import io.debezium.time.MicroTimestamp;
//
// public class PostgresToDebeziumMicroTimestampConverter<R extends ConnectRecord<R>> implements Transformation<R> {
//     private static final Logger logger = LoggerFactory.getLogger(PostgresToDebeziumMicroTimestampConverter.class);
//
//     private String tsColumnName;
//
//     @Override
//     public void configure(Map<String, ?> configs) {
//         tsColumnName = (String) configs.get("column.name");
//         if (tsColumnName == null) {
//             throw new IllegalArgumentException("'column.name' configuration parameter cannot be null");
//         }
//     }
//
//     @Override
//     public R apply(R record) {
//         Struct value = (Struct) record.value();
//
//         if (value == null) {
//             return null;
//         }
//
//         Struct after = value.getStruct("after");
//         if (after == null) {
//             logger.debug("Not a connect schema, skipping timestamp conversion. Value={}", value);
//             return record;
//         }
//
//         Struct updatedAfter = convertTimestamp(after);
//
//         Struct updatedValue = value.put("after", updatedAfter);
//
//         return record.newRecord(
//                 record.topic(),
//                 record.kafkaPartition(),
//                 record.keySchema(),
//                 record.key(),
//                 updatedValue.schema(),
//                 updatedValue,
//                 record.timestamp());
//     }
//
//     @Override
//     public ConfigDef config() {
//         return new ConfigDef()
//                 .define("column.name", ConfigDef.Type.STRING, null, ConfigDef.Importance.HIGH, "The name of the timestamp column in the input record");
//     }
//
//     @Override
//     public void close() {
//     }
//
//     @Override
//     public String toString() {
//         return "PostgresToDebeziumMicroTimestampConverter[]";
//     }
//
//     private Struct convertTimestamp(Struct after) {
//         Schema schema = after.schema();
//         Object tsFieldValue = after.get(tsColumnName);
//         if (tsFieldValue instanceof Long) {
//             Long epochMicros = ((Long) tsFieldValue) * 1000L;
//             LocalDateTime dateTime = LocalDateTime.ofEpochSecond(epochMicros / 1000000L, (int) (epochMicros % 1000000L), ZoneOffset.UTC);
//             Calendar calendar = GregorianCalendar.from(dateTime.atZone(ZoneOffset.UTC));
//             long timestamp = calendar.getTimeInMillis() * 1000L;
//             MicroTimestamp microTimestamp = MicroTimestamp.schema().(schema, timestamp);
//
//             Struct updatedAfter = schema == null ? null : new Struct(schema);
//             for (Field field : schema.fields()) {
//                 String fieldName = field.name();
//                 if (field.name().equals(tsColumnName)) {
//                     updatedAfter.put(fieldName, microTimestamp);
//                 } else {
//                     updatedAfter.put(fieldName, after.get(fieldName));
//                 }
//             }
//
//             return updatedAfter;
//         } else {
//             logger.trace("Timestamp column is not of expected type long, value={}", tsFieldValue);
//             return after;
//         }
//     }
// }
