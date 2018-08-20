package it.chusen.hyg.tools

import java.text.SimpleDateFormat
import java.util.Date

/**
  * @Auther: 楚森
  * @Description:
  * @Company: 枣庄学院
  * @Date: 2018/7/29 15:03
  * @Version: 1.0
  */
object DateUtils {

  val format = new SimpleDateFormat("yyyy-MM-dd")
  // 获得当前格式化的日期
  def formatDate= format.format(new Date())


}
