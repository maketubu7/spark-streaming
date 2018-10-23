package com.imooc.spark

import com.imooc.utils.SparkUtil
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * 黑名单过滤，对于要进行匹配的字段要放在同一个位置
  * 基于map的变换
  */
object BlacklistFilterApp {
  def main(args: Array[String]): Unit = {
    val sc = SparkUtil.createSparkContext(true,"BlacklistFilterApp")

    val ssc = new StreamingContext(sc,Seconds(2))

    val list = List("big mom","kaisa")

    //数据格式   （big mom，ture）（kaisa，ture）
    val blacklist = sc.parallelize(list).map(x => (x,true))

    val lines: DStream[String] = ssc.textFileStream("file:///E:/idea_workspace/spark_stream/data/")
    // 20180821，name => (name,(20180821,name))  => (name,((20180821,name),optition))
    val reslog = lines.map(x => (x.split(",")(1),x))
      .transform(rdd  =>{
      //用leftjoin可以保留原来所有的左边的数据，即使没有匹配到，也不会丢失
      rdd.leftOuterJoin(blacklist)
        .filter(x => x._2._2.getOrElse(false) != true)
        .map(_._2._1)
    })

    reslog.print()

    ssc.start()
    ssc.awaitTermination()
  }
}
