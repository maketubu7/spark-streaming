package com.imooc.spark

import com.imooc.utils.SparkUtil
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * 监控一个目录，对move的文件的内容进行实时读取，在windows下必须要 以流的形式写入
  * 业务逻辑结构和其他的一样
  */
object FileTextWordCount {
  def main(args: Array[String]): Unit = {
    val sc = SparkUtil.createSparkContext(true,"FileTextWordCount")

    val ssc = new StreamingContext(sc,Seconds(5))

    val lines = ssc.textFileStream("file:///E:/idea_workspace/spark_stream/data/")
//      val lines = ssc.socketTextStream("0.0.0.0",9999)
    val result = lines.flatMap(_.split(" ")).map((_,1)).reduceByKey(_ + _)
    result.print()

    ssc.start()
    ssc.awaitTermination()
  }
}
