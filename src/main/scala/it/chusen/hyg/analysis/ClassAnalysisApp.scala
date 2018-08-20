package it.chusen.hyg.analysis

import it.chusen.hyg.tools.{DateUtils, JDBCUtils, ProcessProperty}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ArrayBuffer

/**
  * @Auther: 楚森
  * @Description: class 作业分析
  * @Company: 枣庄学院
  * @Date: 2018/7/29 12:42
  * @Version: 1.0
  */
object ClassAnalysisApp {
  // 加载配置文件
  val classProperty = ProcessProperty.classBean


  def main(args: Array[String]): Unit = {
    // 本地模式 测试
    val conf = new SparkConf().setAppName(classProperty.getSparkAppName).setMaster("local")
    val sc = new SparkContext(conf)
    // 加载数据源
    val classRDD = sc.textFile(classProperty.getSource)
    val firstRDD = classRDD.filter(f => {
      f.split(classProperty.getSplitSeparator).length != classProperty.getLoglength
    })
    firstRDD.cache()
    // 计算第一张表中数据// 总学习人数，分类课程总价值,学习总人数 ,收藏总人数
     courseSumbyAna(firstRDD)
    // course_report_name 计算带课程名称的总人数等...计算插入表中
    courseNameSumbyAna(firstRDD)
    // 计算第三张表 分类计算 收费课程数量 与 免费课程数量
    courseMoneySumbyAna(firstRDD)


    sc.stop()

  }

  /**
    * 按照日期 分类进行做 统计
    *
    * @param classRDD
    * @return
    */
  def courseSumbyAna(classRDD: RDD[String]) = {
    val groupRDD = classRDD.map(line => {
      val items = line.split(classProperty.splitSeparator)
      // 日期+时间 // category,classname,classhour,classday,studynum,collectnum,classmoney
      var money = if (items(6).equals("免费")) 0.0 else items(6).substring(1)
      if (!"".equals(money) && money != 0) {
        money = items(6).substring(1).toDouble
      } else {
        money = 0.0
      }

      (DateUtils.formatDate + "_" + items(0), (items(2).toInt, items(3).toInt, items(4).toInt, items(5).toInt, money.asInstanceOf[Double]))
    }).groupByKey()
    val resRDD = groupRDD.mapValues(f => { // 每一个 value 中进行结果的压缩
      var classhour, classday, studynum, collectnum = 0
      var classmoney: Double = 0.0
      f.foreach(a => {
        classhour += a._1
        classday += a._2
        studynum += a._3
        collectnum += a._4
        classmoney += a._5
      })
      (classhour, classday, studynum, collectnum, classmoney)
    }).map(res => {
      val key = res._1
      val value = res._2
      (key.split("_")(0), key.split("_")(1), value._1, value._2, value._3, value._4, value._5)
    }).foreachPartition(f => {
      val arr = new ArrayBuffer[Object]()
      // 插入数据库
      f.foreach(data => {
        val obj = Array(data._1, data._2, data._3, data._4, data._5, data._6, data._7)
        arr += obj
        //日期
      })
      // 批处理 获得一次数据库连接就可以
      JDBCUtils.insertSqlBatch("insert into course_report_sum values(?,?,?,?,?,?,?)", arr)
    })
  }


  /**
    * 所求字段 清洗数据入库
    * date,category,coursename,studynum,callectnum,price
    *
    * @param firstRDD
    */
  def courseNameSumbyAna(firstRDD: RDD[String]) = {
    //不需要做聚合 直接清减字段插入//category,classname,classhour,classday,studynum,collectnum,classmoney
    firstRDD.map(line => {
      val splited = line.split(classProperty.getSplitSeparator)
      val money = if (splited(6).equals("免费")) 0 else splited(6).substring(1).toDouble
      (DateUtils.formatDate, splited(0), splited(1), splited(4), splited(5), money)
    }).foreachPartition(f => {
      val arr = new ArrayBuffer[Object]()
      // 插入数据库
      f.foreach(data => {
        val obj = Array(data._1, data._2, data._3, data._4, data._5, data._6)
        arr += obj
        //日期
      })
      // 批处理 获得一次数据库连接就可以
      JDBCUtils.insertSqlBatch("insert into course_report_name values(?,?,?,?,?,?)", arr)
    })
  }

  /**
    * 计算第三张表 date,category,moneynum,nomoney
    *
    * @param firstRDD
    */
  def courseMoneySumbyAna(firstRDD: RDD[String]) = {
    val resRDD1 = firstRDD.map(line => {
      val splited = line.split(classProperty.getSplitSeparator)
      val money = if (splited(6).equals("免费")) 0 else 1
      (DateUtils.formatDate + "_" + splited(0) + "_" + money, 1)
    }).reduceByKey(_ + _)
    // 2018_IT_0 23
    // 2018_IT_1 33
    resRDD1.map(line => {
      val key = line._1.split("_")
      val date = key(0)
      val category = key(1)
      val mon = key(2)
      (date + "_" + category,mon+"_"+line._2)
    }).groupByKey().mapValues(f => {
      var nomonery,monery = 0
      f.foreach(res => {
        val flag = res.split("_")(0).toInt
        val count = res.split("_")(1)
        if (flag == 0) {
          nomonery = count.toInt
        } else if (flag ==1 ) {
          monery = count.toInt
        }
      })
      (monery,nomonery)
    }).map(line => {
      val key = line._1.split("_")
      (key(0),key(1),line._2._1,line._2._2)
    }).foreachPartition(f => {
      val arr = new ArrayBuffer[Object]()
      f.foreach(data => {
        val obj = Array(data._1,data._2,data._3,data._4)
        arr += obj
      })
      //插入数据库
      JDBCUtils.insertSqlBatch("insert into course_report_money values (?,?,?,?)",arr)
    })
  }

}
