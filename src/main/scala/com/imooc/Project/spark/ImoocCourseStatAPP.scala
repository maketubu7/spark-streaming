package com.imooc.Project.spark

import com.imooc.Project.domain.{ClickLog, CourseClickCount, CourseRefererCount}
import com.imooc.Project.utils.Dateutils
import com.imooc.utils.SparkUtil
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import com.imooc.Project.Dao.{CourseClickCountDAO, CourseRefererCountDAO}

import scala.collection.mutable.ListBuffer

/**
  * sream 对接 flume到kafka  的数据
  */
object ImoocCourseStatAPP {
  def main(args: Array[String]): Unit = {

    if (args.length < 4){

      System.err.print("Usage ImoocCourseStatAPP: <zk> <groupid> <topics> <numpartition>")
      System.exit(1)
    }

    val Array(zk, groupid, topics, numThread) = args

    val topicMap = topics.split(" ").map((_,numThread.toInt)).toMap

    val sc = SparkUtil.createSparkContext(true,"KafkaReceiverWordcount")
    val ssc = new StreamingContext(sc,Seconds(60))

    //TODO : kafka spark streaming 整合
    val messageStream = KafkaUtils.createStream(ssc, zk, groupid, topicMap)

    // TODO ： 为什么这里要取第二个，第一个值对我们来说没有意义
    //TODO： 第一个值为key,无意义
    val log_data = messageStream.map(_._2)

    val clean_log = log_data.map(log =>{

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

    //测试是否清洗成功
    //clean_log.print()

    //需求二 : 对每天的实战课程访问量的每天的总访问量
    clean_log.map(log =>{
      (log.time.substring(0,8) + "_" + log.crouseID,1)
    }).reduceByKey(_ + _).foreachRDD(rdd =>{
      rdd.foreachPartition(recods =>{
        val list = new ListBuffer[CourseClickCount]
        recods.foreach(x => {
          list.append(CourseClickCount(x._1,x._2.toLong))
        })
        //保存数据到hbase
        CourseClickCountDAO.save(list)
      })
    })

    //需求三     引流引擎过来的count

    clean_log.map(log => {
      //  https://cn.bing.com/search?q=hadoop 基础
      val referer = log.referer
      val splits = referer.replaceAll("//","/").split("/")
      var host = ""

      if (splits.length > 2) {
        host = splits(1)
      }
      (host,log.crouseID,log.time)
    }).filter(_._1 != "").map(x =>{
      ((x._3.substring(0,8) + "_" + x._1 + "_" + x._2),1)
    }).reduceByKey(_ + _).foreachRDD(rdd =>{
      rdd.foreachPartition(partitionrecode =>{
        val list = new ListBuffer[CourseRefererCount]
        partitionrecode.foreach(x =>{
          list.append(CourseRefererCount(x._1,x._2.toLong))
        })
        CourseRefererCountDAO.save(list)
      })
    })

    ssc.start()
    ssc.awaitTermination()
  }
}
