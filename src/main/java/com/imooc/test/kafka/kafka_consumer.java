package com.imooc.test.kafka;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class kafka_consumer extends Thread{
    private String topic;
    private ConsumerConnector consumer;

    public kafka_consumer(String topic){
        this.topic = topic;

    }

    private ConsumerConnector CreatConsumer(){

        Properties prop = new Properties();
        prop.put("zookeeper.connect",KafkaProperties.ZK_URL);
        prop.put("group.id",KafkaProperties.GROUP_ID);
        //javaapi.ConsumerConnector 的创建方法
        return  Consumer.createJavaConsumerConnector(new ConsumerConfig(prop));
    }

    @Override
    public void run() {

        consumer = CreatConsumer();
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();

        topicCountMap.put(topic,1);

        //topic
        //对应的数据流    List<KafkaStream<byte[], byte[]>>
        Map<String, List<KafkaStream<byte[], byte[]>>> messageStreams = consumer.createMessageStreams(topicCountMap);

        KafkaStream<byte[], byte[]> messagedata = messageStreams.get(topic).get(0);//只有这样才能接受到每次的数据

        ConsumerIterator<byte[], byte[]> iterator = messagedata.iterator();

        while (iterator.hasNext()){


            MessageAndMetadata<byte[], byte[]> value = iterator.next();

            /**
             * 这里不能用两个next(),不然会丢数据
             */
            String message = new String(value.message());
            long index = value.offset() ;
//            String message = new String(iterator.next().message());
//            long index = iterator.next().offset();
            System.out.println("receive: " + index  + " "  + message);
        }
    }
}
