package com.imooc.Project.Dao

import com.imooc.Project.domain.CourseRefererCount
import com.imooc.test.util.HbaseUtils
import org.apache.hadoop.hbase.client.Get
import org.apache.hadoop.hbase.util.Bytes

import scala.collection.mutable.ListBuffer

object CourseRefererCountDAO {
  //  create 'imooc_course_clickcount', 'info'
  val tableName = "imooc_referer_clickcount"
  val cf = "info"
  val qualiter = "click_count"

  /**
    * 保存数据到对应的表中
    * @param list   包含数据的 实例对象的集合
    */
  def save (list:ListBuffer[CourseRefererCount]): Unit ={

    val table = HbaseUtils.getInstance().gettable(tableName)

    for (ele <- list){
      //可以对value值进行累加
      table.incrementColumnValue(
        Bytes.toBytes(ele.day_referer_courseid),
        Bytes.toBytes(cf),
        Bytes.toBytes(qualiter),
        ele.click_count
      )
    }
  }

  /**
    * 根据rowkey 得到对应的click_count的long值
    * @param rowkey
    * @return
    */
  def getcount (rowkey:String): Long = {
    val click_count = 0L

    val table =  HbaseUtils.getInstance().gettable(tableName)
    val get = new Get(Bytes.toBytes(rowkey))
    //获得某个列的值
    val value: Array[Byte] = table.get(get).getValue(Bytes.toBytes(cf),
      Bytes.toBytes(qualiter))

    if (value == null){
      0L
    }else{
      Bytes.toLong(value)
    }
  }

  //测试  引流数据入库是否可行
  def main(args: Array[String]): Unit = {
    val list = ListBuffer[CourseRefererCount](
      CourseRefererCount("20180822_www.sougou.com_179",2),
      CourseRefererCount("20180822_www.bing.com.131",7),
      CourseRefererCount("20180822_www.baidu.com_148",25)
    )
    save(list)
    println(getcount("20180822_www.sougou.com_179") + " : " + getcount("20180822_www.bing.com.131") + " : " + getcount("20180822_www.baidu.com_148"))
  }
}
