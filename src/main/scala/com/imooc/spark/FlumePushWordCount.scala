package com.imooc.spark

import com.imooc.utils.SparkUtil
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.flume.FlumeUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * flume 整合 streaming 第一种方式  push
  */
object FlumePushWordCount {
  def main(args: Array[String]): Unit = {
    val sc = SparkUtil.createSparkContext(true,"FileTextWordCount")

    val ssc = new StreamingContext(sc,Seconds(5))

    val Array(hostname,port) = args

    //    val lines = ssc.textFileStream("file:///E:/idea_workspace/spark_stream/datas/")
    //todo : 如何使用flume 整合 streaming
    val lines = FlumeUtils.createStream(ssc,hostname,port.toInt)
    val resrdd: DStream[(String, Int)] = lines.map(x => new String(x.event.getBody.array()).trim)
      .flatMap(_.split(" "))
      .map((_,1))
      .reduceByKey(_ + _)

    resrdd.print()

    ssc.start()
    ssc.awaitTermination()
  }
}
