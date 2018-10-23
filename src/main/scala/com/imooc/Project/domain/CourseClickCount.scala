package com.imooc.Project.domain

/**
  * 实战课程点击实体类
  * @param day_course   rowkey 的设计 day_courseid 20110822_179
  * @param click_count    每天点击的实战课程的点击量
  */
case class CourseClickCount (day_course:String, click_count: Long)
