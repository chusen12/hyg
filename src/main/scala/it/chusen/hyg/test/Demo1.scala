package it.chusen.hyg.test

import scala.collection.mutable.ArrayBuffer

/**
  * @Auther: 楚森
  * @Description:
  * @Company: 枣庄学院
  * @Date: 2018/7/29 15:57
  * @Version: 1.0
  */
object Demo1 {

  def main(args: Array[String]): Unit = {
//    val money = "￥19.8"
//    val m = money.substring(1)
//    println( m)
    val arr = new ArrayBuffer[String]
    arr += "211"
    arr += "31"
    arr += "11"
    arr += "2"
    for (i <- 0 to 3) {
      println(i)
    }
  }
}
