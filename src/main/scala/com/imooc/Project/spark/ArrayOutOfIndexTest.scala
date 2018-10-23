package com.imooc.Project.spark

import com.imooc.Project.domain.ClickLog
import com.imooc.Project.utils.Dateutils
import com.imooc.utils.SparkUtil
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka.KafkaUtils

object ArrayOutOfIndexTest {
  def main(args: Array[String]): Unit = {


    val sc = SparkUtil.createSparkContext(true,"KafkaReceiverWordcount")
    val ssc = new StreamingContext(sc,Seconds(2))

    //TODO : kafka spark streaming 整合
   val array:Array[String] = Array(
     "183.127.26.72	2018-08-21 22:47:59	'GET /class/156.html HTTP/1.1'	200	-",
     "128.27.47.187	2018-08-21 22:47:59	'GET /class/128.html HTTP/1.1'	500	-",
     "124.26.72.98	2018-08-21 22:47:59	'GET /class/179.html HTTP/1.1'	404	https://cn.bing.com/search?q=hadoop 基础",
     "124.26.72.98	2018-08-21 22:47:59	'GET /class/179.html HTTP/1.1'	404	https://cn.bing.com/search?q=hadoop 基础"
   )

    val log_data = ssc.sparkContext.parallelize(array)

    // TODO ： 为什么这里要取第二个，第一个值对我们来说没有意义
    //TODO： 第一个值为key,无意义
    //messageStream.map(_._2).flatMap(_.split(" ")).map((_,1)).reduceByKey(_ + _).print()
//    val log_data = messageStream.map(_._2)

    val clean_log: RDD[ClickLog] = log_data.map(log =>{

      val infos = log.split("\t")
      // infos(2) = "GET /class/130.html HTTP/1.1"
      // url = /class/130.html
      val class_url = infos(2).split(" ")(1)
      var courseID = 0
      if (class_url.startsWith("/class")){
        val courseHtml = class_url.split("/")(2)
        courseID = courseHtml.substring(0,courseHtml.lastIndexOf(".")).toInt
      }
      //返回为一个类
      ClickLog(infos(0),Dateutils.parse_time(infos(1)),courseID,infos(3).toInt, infos(4))
    }).filter(clicklog => clicklog.crouseID != 0)

    clean_log.foreach(println)


    //    messageStream.count().print()

  }
}
