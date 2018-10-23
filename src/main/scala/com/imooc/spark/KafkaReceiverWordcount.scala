package com.imooc.spark

import com.imooc.utils.SparkUtil
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

object KafkaReceiverWordcount {
  def main(args: Array[String]): Unit = {

    if (args.length < 4){

      System.err.print("Usage KafkaReceiverWordcount: <zk> <groupid> <topics> <numpartition>")
    }

    val Array(zk, groupid, topics, numThread) = args

    val topicMap = topics.split(" ").map((_,numThread.toInt)).toMap

    val sc = SparkUtil.createSparkContext(true,"KafkaReceiverWordcount")
    val ssc = new StreamingContext(sc,Seconds(5))

    //TODO : kafka spark streaming 整合
    val messageStream = KafkaUtils.createStream(ssc, zk, groupid, topicMap)

    // TODO ： 为什么这里要取第二个，第一个值对我们来说没有意义
    //TODO： 第一个值为key,无意义
   // messageStream.map(_._2).flatMap(_.split(" ")).map((_,1)).reduceByKey(_ + _).print()
      messageStream.count().print()
    ssc.start()
    ssc.awaitTermination()
  }
}
