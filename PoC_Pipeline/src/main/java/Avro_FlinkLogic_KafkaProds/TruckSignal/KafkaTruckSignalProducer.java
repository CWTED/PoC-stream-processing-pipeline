package Avro_FlinkLogic_KafkaProds.TruckSignal;

import com.schemas.TruckKey;
import com.schemas.TruckSignal;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import Avro_FlinkLogic_KafkaProds.TruckSignal.TruckSignalGeneration.TruckSignalGenerator;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Properties;

public class KafkaTruckSignalProducer {

    public static void main(String[] args) {

        String brokers = "COMMA-SEPARATED.LIST.OF.KAFKA.BROKERS"; // Remember to specify port for each
        String schemaReg = "http://SCHEMA-REGISTRY-ADDRESS:PORT";
        String topic = "flinkinput";

        Properties props = new Properties();

        // Kafka producer configuration. Some of these are default settings, but they are specified for clarity here.
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        props.setProperty("schema.registry.url", schemaReg);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.LINGER_MS_CONFIG, "1");
        props.put(ProducerConfig.BATCH_SIZE_CONFIG,65536);
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 120000);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "prod-1"); // Unique ID
        props.put(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, 120000);

        // Create Kafka producer
        Producer<TruckKey, TruckSignal> producer = new KafkaProducer<>(props);
        // Initiate transactions
        producer.initTransactions();

        try {
            // Create new TruckSignalGenerator object
            TruckSignalGenerator tg = new TruckSignalGenerator();
            int i = 0;
            while (true) {
                // Begin a transaction
                producer.beginTransaction();
                // Produce a pre-defined number of tuples to the flinkinput topic. Increase prodC to increase data rate.
                for (int prodC = 0; prodC < 125; prodC++) {
                    TruckSignal ts = tg.getRandomTruckSignal(i);
                    TruckKey tk = new TruckKey(ts.getCustomer(), ts.getVehicle());
                    ts.setTimestamp(LocalDateTime.now(ZoneId.of("Europe/Paris")));
                    ProducerRecord<TruckKey, TruckSignal> record = new ProducerRecord<>(topic, tk, ts);
                    producer.send(record);
                    i = (i + 1) % 10;
                }
                // Commit transaction
                producer.commitTransaction();
            }} catch (Exception e) {
            producer.abortTransaction();
            e.printStackTrace();
        } finally {
            producer.close();
        }
    }
}