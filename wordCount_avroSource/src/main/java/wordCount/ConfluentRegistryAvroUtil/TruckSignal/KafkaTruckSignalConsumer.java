package wordCount.ConfluentRegistryAvroUtil.TruckSignal;

import com.example.FreqRecord;
import com.example.TruckKey;
import com.example.TruckSignal;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.clients.producer.ProducerConfig;
import wordCount.ConfluentRegistryAvroUtil.KafkaAvroSerDe.AvroDeserialization;
import wordCount.ConfluentRegistryAvroUtil.KafkaAvroSerDe.AvroSerialization;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Properties;

public class KafkaTruckSignalConsumer {

    public static void main(String[] args) throws Exception {

        // String fields
        String brokers = "COMMA-SEPARATED.LIST.OF.KAFKA.BROKERS"; // Remember to specify port for each
        String schemaReg = "http://SCHEMA-REGISTRY-ADDRESS:PORT";

        String inputTopic = "flinkinput";
        String outputTopic = "flinkoutput";
        String jobName = "countFrequencies";
        String groupId = "FlinkConsumers";

        // Define Kafka source (consumer) properties
        Properties propsCons = new Properties();
        propsCons.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG,65536);
        propsCons.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed"); // Only read committed, for exactly-once semantics.
        propsCons.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 120000);
        propsCons.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        // Define Kafka sink (producer) properties
        Properties propsProd = new Properties();
        propsProd.put(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG,120000);
        propsProd.put(ProducerConfig.ACKS_CONFIG, "all");
        propsProd.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 120000);


        // Set up execution environment, with checkpointing method and interval
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment()
                .enableCheckpointing(300L, CheckpointingMode.EXACTLY_ONCE);

        // Set up Kafka consumer
        KafkaSource<Tuple2<TruckKey, TruckSignal>> kafkaSource = KafkaSource.<Tuple2<TruckKey, TruckSignal>>builder()
                .setBootstrapServers(brokers)
                .setTopics(inputTopic)
                .setDeserializer(new AvroDeserialization<>(TruckKey.class, TruckSignal.class, schemaReg))
                .setGroupId(groupId)
                .setStartingOffsets(OffsetsInitializer.committedOffsets(OffsetResetStrategy.EARLIEST))
                .setProperties(propsCons)
                .build();

        // Set watermark strategy
        WatermarkStrategy<Tuple2<TruckKey,TruckSignal>> wmStrategy =
                WatermarkStrategy
                        .<Tuple2<TruckKey,TruckSignal>>forMonotonousTimestamps()
                        .withTimestampAssigner((tuple, prevTS) -> tuple.f1.getTimestamp().atZone(ZoneId.of("Europe/Paris")).toInstant().toEpochMilli()).withIdleness(Duration.ofSeconds(1));

        // Set up source operator
        DataStream<Tuple2<TruckKey,TruckSignal>> truckStream =
                env.fromSource(kafkaSource, wmStrategy, "Truck Source");

        // Set up Kafka sink for minute frequencies
        KafkaSink<Tuple2<TruckKey,FreqRecord>> kafkaSinkM = KafkaSink.<Tuple2<TruckKey,FreqRecord>>builder()
                .setBootstrapServers(brokers)
                .setRecordSerializer(new AvroSerialization<>(TruckKey.class, FreqRecord.class, outputTopic, schemaReg))
                .setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE)
                .setKafkaProducerConfig(propsProd)
                .setTransactionalIdPrefix("sink-m") // Unique ID-prefix
                .build();

        // Set up Kafka sink for minute frequencies
        KafkaSink<Tuple2<TruckKey,FreqRecord>> kafkaSinkH = KafkaSink.<Tuple2<TruckKey,FreqRecord>>builder()
                .setBootstrapServers(brokers)
                .setRecordSerializer(new AvroSerialization<>(TruckKey.class, FreqRecord.class, outputTopic, schemaReg))
                .setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE)
                .setKafkaProducerConfig(propsProd)
                .setTransactionalIdPrefix("sink-h") // Unique ID-prefix
                .build();

        // Define TypeInformation for Tuple2<TruckKey, FreqRecord>. Needed to avoid type erasure caused by the Flink API.
        TypeInformation<Tuple2<TruckKey,FreqRecord>> freqTupleTypeInfo =
                TypeInformation.of(new TypeHint<Tuple2<TruckKey, FreqRecord>>() {});

        // Initial coarse filtering, if NaN in signal value field: continue processing it
        DataStream<Tuple2<TruckKey,FreqRecord>> filteredAndCounted = truckStream
                .filter(tuple -> Float.isNaN(tuple.f1.getValue()))
                .flatMap((Tuple2<TruckKey,TruckSignal> tuple, Collector<Tuple2<TruckKey,FreqRecord>> out)
                        -> {

                    TruckSignal sig = tuple.f1;

                    // Construct FreqRecord
                    FreqRecord fr = new FreqRecord(
                            sig.getTimestamp(),
                            sig.getTimestamp(),
                            sig.getCustomer(),
                            sig.getVehicle(),
                            sig.getSignal(),
                            1,
                            0,
                            0,
                            "none"
                    );

                    out.collect(new Tuple2<>(tuple.f0,fr));

                }).returns(freqTupleTypeInfo);

        // Create sliding window (1 minute, 1 second slide)
        DataStream<Tuple2<TruckKey,FreqRecord>> minSlide = filteredAndCounted
                .keyBy(new KeySelector<Tuple2<TruckKey, FreqRecord>, Tuple3<CharSequence,CharSequence, CharSequence>>() {
                    @Override
                    public Tuple3<CharSequence, CharSequence, CharSequence> getKey(Tuple2<TruckKey,FreqRecord> tuple) {
                        FreqRecord fr = tuple.f1;
                        return new Tuple3<>(fr.getCustomer(),fr.getVehicle(),fr.getSignal());
                    }
                })
                .window(SlidingEventTimeWindows.of(Time.minutes(1), Time.seconds(1)))
                .reduce((ReduceFunction<Tuple2<TruckKey, FreqRecord>>) (t1, t2) -> {

                    FreqRecord fr1 = t1.f1;
                    FreqRecord fr2 = t2.f1;
                    LocalDateTime ts1 = fr1.getEventTime();
                    LocalDateTime ts2 = fr2.getEventTime();
                    // Select latest tuple
                    LocalDateTime latest = ts1.isBefore(ts2) ? ts2 : ts1;

                    LocalDateTime aggEndTime = LocalDateTime.now(ZoneId.of("Europe/Paris"));

                    FreqRecord result =
                            new FreqRecord(
                                    latest,
                                    aggEndTime,
                                    fr1.getCustomer(),
                                    fr1.getVehicle(),
                                    fr1.getSignal(),
                                    fr1.getMinFreq() + fr2.getMinFreq(),
                                    0,
                                    0,
                                    "min"
                            );
                    return new Tuple2<>(t1.f0,result);
                })
                .returns(freqTupleTypeInfo);

        // Create tumbling window (1 minute)
        DataStream<Tuple2<TruckKey,FreqRecord>> minTumble = filteredAndCounted
                .keyBy(new KeySelector<Tuple2<TruckKey, FreqRecord>, Tuple3<CharSequence,CharSequence, CharSequence>>() {
                    @Override
                    public Tuple3<CharSequence, CharSequence, CharSequence> getKey(Tuple2<TruckKey,FreqRecord> tuple) {
                        FreqRecord fr = tuple.f1;
                        return new Tuple3<>(fr.getCustomer(),fr.getVehicle(),fr.getSignal());
                    }
                })
                .window(TumblingEventTimeWindows.of(Time.minutes(1)))
                .reduce((ReduceFunction<Tuple2<TruckKey, FreqRecord>>) (t1, t2) -> {

                    FreqRecord fr1 = t1.f1;
                    FreqRecord fr2 = t2.f1;
                    LocalDateTime ts1 = fr1.getEventTime();
                    LocalDateTime ts2 = fr2.getEventTime();
                    // Select latest tuple
                    LocalDateTime latest = ts1.isBefore(ts2) ? ts2 : ts1;


                    FreqRecord result =
                            new FreqRecord(
                                    latest,
                                    latest,
                                    fr1.getCustomer(),
                                    fr1.getVehicle(),
                                    fr1.getSignal(),
                                    fr1.getMinFreq() + fr2.getMinFreq(),
                                    0,
                                    0,
                                    "none"
                            );
                    return new Tuple2<>(t1.f0,result);
                })
                .returns(freqTupleTypeInfo);

        // Create sliding window for hour frequencies (1 hour, 1 minute slide)
        DataStream<Tuple2<TruckKey,FreqRecord>> hourSlide = minTumble
                .keyBy(new KeySelector<Tuple2<TruckKey, FreqRecord>, Tuple3<CharSequence,CharSequence, CharSequence>>() {
                    @Override
                    public Tuple3<CharSequence, CharSequence, CharSequence> getKey(Tuple2<TruckKey,FreqRecord> tuple) {
                        FreqRecord fr = tuple.f1;
                        return new Tuple3<>(fr.getCustomer(),fr.getVehicle(),fr.getSignal());
                    }
                })
                .window(SlidingEventTimeWindows.of(Time.hours(1), Time.minutes(1)))
                .reduce((ReduceFunction<Tuple2<TruckKey, FreqRecord>>) (t1, t2) -> {

                    FreqRecord fr1 = t1.f1;
                    FreqRecord fr2 = t2.f1;

                    LocalDateTime ts1 = fr1.getEventTime();
                    LocalDateTime ts2 = fr2.getEventTime();
                    // Select latest tuple
                    LocalDateTime latest = ts1.isBefore(ts2) ? ts2 : ts1;

                    LocalDateTime aggEndTime = LocalDateTime.now(ZoneId.of("Europe/Paris"));

                    FreqRecord result =
                            new FreqRecord(
                                    latest,
                                    aggEndTime,
                                    fr1.getCustomer(),
                                    fr1.getVehicle(),
                                    fr1.getSignal(),
                                    fr1.getMinFreq() + fr2.getMinFreq(), // Accumulating value
                                    fr1.getMinFreq() + fr2.getMinFreq(), // Actual hour frequency
                                    0,
                                    "hour"
                            );
                    return new Tuple2<>(t1.f0,result);
                })
                .returns(freqTupleTypeInfo);

        minSlide.sinkTo(kafkaSinkM); // Produce to Kafka minute sink
        hourSlide.sinkTo(kafkaSinkH); // Produce to Kafka hour sink

        env.execute(jobName);
    }

}


