package com.imooc.test;

import com.imooc.test.kafka.KafkaProperties;
import com.imooc.test.kafka.kafka_producer;

public class KafkaAppProducerTest {
    public static void main(String[] args) {

        new kafka_producer(KafkaProperties.TOPICS).start();

    }
}
