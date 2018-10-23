package com.imooc.spark

import com.imooc.utils.SparkUtil
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.flume.FlumeUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
/**
  * flume 整合 streaming 第二种方式  pull
  */
object FlumePullWordCount {
  def main(args: Array[String]): Unit = {

    if (args.length < 2){
      System.err.print("usage: <hostname>,<port>")
      System.exit(1)
    }

    val sc = SparkUtil.createSparkContext(true,"FlumePullWordCount")

    val ssc = new StreamingContext(sc,Seconds(5))

    val Array(hostname,port) = args

    //    val lines = ssc.textFileStream("file:///E:/idea_workspace/spark_stream/datas/")
    //todo : 如何使用flume 整合 streaming 生产上一般用第二种方式实现
    val lines = FlumeUtils.createPollingStream(ssc,hostname,port.toInt)

    val resrdd: DStream[(String, Int)] = lines.map(x => new String(x.event.getBody.array()).trim)
      .flatMap(_.split(" "))
      .map((_,1))
      .reduceByKey(_ + _)

    resrdd.print()

    ssc.start()
    ssc.awaitTermination()
  }
}
