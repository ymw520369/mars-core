package org.alan.mars.kafka.producer;

import org.alan.mars.kafka.record.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * Created on 2017/5/22.
 *
 * @author Alan
 * @scene 1.0
 */
public class LogProducer {
    @Autowired
    private KafkaTemplate<?, Object> kafkaTemplate;

    public void send(Record record) {
        kafkaTemplate.send(record.topic, record.data);
    }
}
