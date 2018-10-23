package com.imooc.spark

import com.imooc.test.util.JDBCHelper
import com.imooc.utils.SparkUtil
import org.apache.spark.streaming.{Seconds, StreamingContext}

object ForeachRddApp {
  def main(args: Array[String]): Unit = {

    val sc = SparkUtil.createSparkContext(true, "ForeachRddApp")

    val ssc = new StreamingContext(sc, Seconds(2))

    val lines = ssc.textFileStream("file:///E:/idea_workspace/spark_stream/data/")

    //原来的运算逻辑不变
    val result = lines.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _)
    result.print()

    //数据库插入 参考写法
    // 参考：http://spark.apache.org/docs/2.2.0/streaming-programming-guide.html
    //先用foreachRDD 再针对分区的数据进行foreach对每条记录进行处理
    result.foreachRDD { rdd =>
      rdd.foreachPartition { partitionOfRecords =>
        val connection = JDBCHelper.getInstance().getConnection

//        val connection = createConnection()

        partitionOfRecords.foreach(records => {
          //values()  中的字符串需要加引号
          val sql_in = "insert into wordcount (word, wordcount) values('" + records._1 + "'," + records._2.toInt + ")"
          //val sql_up =
          connection.createStatement().execute(sql_in)
        })

        //有拿就要有还，不然线程会不够
        JDBCHelper.getInstance().returnConnection(connection)
      }
    }



    //TODO:这里要将数据写入到mysql数据库

    ssc.start()
    ssc.awaitTermination()


  }

/*  def createConnection(): Connection = {

    Class.forName("com.mysql.jdbc.Driver")
    val url = "jdbc:mysql://localhost:3306/imooc_spark_stream"
    val user = "root"
    val password = "123456"

    val conn = DriverManager.getConnection(url, user, password)
    conn
  }*/
}
