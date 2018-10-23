package com.imooc.Project.utils

import java.util.Date

import org.apache.commons.lang3.time.FastDateFormat

object Dateutils {

  val PRE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss")

  val LAST_FORMAT = FastDateFormat.getInstance("yyyyMMddHHmmss")



  def get_time(time : String) = {
    PRE_FORMAT.parse(time).getTime
  }

  def parse_time(time : String) = {
    val date = new Date(get_time(time))
    LAST_FORMAT.format(date)
  }

}
