package com.imooc.spark

import com.imooc.utils.SparkUtil
import org.apache.spark.streaming.{Seconds, StreamingContext}

object StatefulWordCount {
  def main(args: Array[String]): Unit = {

    val sc = SparkUtil.createSparkContext(true,"StatefulStream")

    val ssc = new StreamingContext(sc,Seconds(2))
    //使用了stateful 的算子  就必须使用checkpoint
    //在生产中将checkpoint设置到hdfs上，
    val checkpointpath = "file:///E:/idea_workspace/spark_stream/checkpoint/StatefulStream"

    ssc.checkpoint(checkpointpath)
    val lines = ssc.textFileStream("file:///E:/idea_workspace/spark_stream/data/")

    //原来的运算逻辑彼不变
    val result = lines.flatMap(_.split(" ")).map((_,1)).reduceByKey(_ + _)
    //带状态的streamingdata
    val state = result.updateStateByKey[Int](updateFunc _)

    state.print()

    ssc.start()
    ssc.awaitTermination()

  }

  //自己实现的updateFunc  ==> updateFunc(current:Seq[Int], preview:Option[Int])
  def updateFunc(current:Seq[Int], preview:Option[Int]): Option[Int] = {
    val cur= current.sum
    val pre = preview.getOrElse(0)

    Some(cur + pre)

  }

}
