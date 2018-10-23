package com.imooc.test;

import com.imooc.test.kafka.KafkaProperties;
import com.imooc.test.kafka.kafka_consumer;


public class KafkaAppConsumerTest {
    public static void main(String[] args) {

        new kafka_consumer(KafkaProperties.TOPICS).start();

    }
}
