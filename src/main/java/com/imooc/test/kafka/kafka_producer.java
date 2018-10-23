package com.imooc.test.kafka;


import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.util.Properties;

public class kafka_producer extends Thread{

    private String topic;
    private Producer<Integer,String> producer;




    public kafka_producer (String topic){

        this.topic = topic;
        Properties prop = new Properties();

        prop.put("metadata.broker.list",KafkaProperties.BROKER_LIST);
        prop.put("serializer.class", "kafka.serializer.StringEncoder");
        //三次握手机制，生产上一般用1，如果非常严格就用-1
        prop.put("request.required.acks","1");

        producer = new Producer<Integer, String>(new ProducerConfig(prop));

    }

    @Override
    public void run() {
        int messageNo = 1;

        while  (true){
            String message  = "message_" + messageNo++;

            producer.send(new KeyedMessage<Integer,String>(topic,message));
            System.out.println("send: " + message);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
