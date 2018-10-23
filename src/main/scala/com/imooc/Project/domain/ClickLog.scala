package com.imooc.Project.domain

/**
  * 点击产生的日志
  * @param ip   用户点击的ip
  * @param time   用户点击的时间
  * @param crouseID   用户点击的实战课程ID
  * @param statusCode   用户浏览的状态码
  * @param referer    用户的跳转前链接
  */
case class ClickLog  (ip:String, time:String, crouseID:Int, statusCode:Int, referer:String)
