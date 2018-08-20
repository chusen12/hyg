package it.chusen.hyg.analysis

import it.chusen.hyg.tools.{DateUtils, JDBCUtils, ProcessProperty, ProvinceTools}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.json4s.jackson.JsonMethods._

import scala.collection.mutable.ArrayBuffer

/**
  * @Auther: 楚森
  * @Description: 职位分析app
  * @Company: 枣庄学院
  * @Date: 2018/7/29 12:53
  * @Version: 1.0
  */
object PositionAnalysis {

  val positionBean = ProcessProperty.positionBean


  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName(positionBean.getSparkAppName).setMaster("local")
    val sc = new SparkContext(conf)
    val RDD = sc.textFile(positionBean.getSource)
    val firstRDD = RDD.filter(f => {
      f.split(positionBean.getSplitSeparator).size != positionBean.loglength
    })
    firstRDD.cache()
    // 计算指标
    // 工作地点 计算
    locationPositionAnalysis(firstRDD)
     //薪水范围统计
    salaryPositionAnalysis(firstRDD)
   // 学历范围统计
    studyPositionAnalysis(firstRDD)
   // 经验范围统计
    workyearPositionAnalysis(firstRDD)
    //职位范围统计
    positionNamePositionAnalysis(firstRDD)
    basePositionAna(firstRDD)
    //公司招聘人数统计
    companyPeopleNumAnalysis(firstRDD)
      //  根据更新时间统计
    updateTimeAnalysis(firstRDD)
    //统计省份人数
    //provincePositionAnalysis(firstRDD)
    sc.stop()


  }

  /**
    * 根据更新时间统计
    *
    * @param firstRDD
    */
  def updateTimeAnalysis(firstRDD: RDD[String]) = {
    firstRDD.map(line => {
      val splited = line.split(positionBean.getSplitSeparator)
      (splited(7), 1)
    }).reduceByKey(_ + _).map(m => ("updateTime", m._1, m._2, DateUtils.formatDate)).foreachPartition(f => {
      val arr = new ArrayBuffer[Object]()
      f.foreach(res => {
        val obj = Array(res._1, res._2, res._3, res._4)
        arr += obj
      })
      //插入数据库
      JDBCUtils.insertSqlBatch("insert into position_report values (?,?,?,?)", arr)
    })
  }

  /**
    * 每个公司总招聘人数
    *
    * @param firstRDD
    */
  def companyPeopleNumAnalysis(firstRDD: RDD[String]) = {
    firstRDD.map(line => {
      val splited = line.split(positionBean.getSplitSeparator)
      (splited(1), splited(5).toInt)
    }).reduceByKey(_ + _).map(m => ("companyPeopleNum", m._1, m._2, DateUtils.formatDate)).foreachPartition(f => {
      val arr = new ArrayBuffer[Object]()
      f.foreach(res => {
        val obj = Array(res._1, res._2, res._3, res._4)
        arr += obj
      })
      //插入数据库
      JDBCUtils.insertSqlBatch("insert into position_report values (?,?,?,?)", arr)
    })
  }

  /**
    * 职位分析
    *
    * @param firstRDD
    */
  def positionNamePositionAnalysis(firstRDD: RDD[String]) = {
    firstRDD.map(line => {
      val splited = line.split(positionBean.getSplitSeparator)
      var positionName = "other"
      if (splited(0).contains("销售")) {
        positionName = "销售"
      }
      if (splited(0).contains("分析师")) {
        positionName = "分析师"
      }
      if (splited(0).contains("设计师")) {
        positionName = "设计师"
      }
      if (splited(0).contains("工程师")) {
        positionName = "工程师"
      }
      if (splited(0).contains("咨询师")) {
        positionName = "咨询师"
      }
      if (splited(0).contains("顾问")) {
        positionName = "顾问"
      }
      if (splited(0).contains("管培生")) {
        positionName = "管培生"
      }
      if (splited(0).contains("助理")) {
        positionName = "助理"
      }
      (positionName, 1)
    }).reduceByKey(_ + _).map(m => ("positionName", m._1, m._2, DateUtils.formatDate)).foreachPartition(f => {
      val arr = new ArrayBuffer[Object]()
      f.foreach(res => {
        val obj = Array(res._1, res._2, res._3, res._4)
        arr += obj
      })
      //插入数据库
      JDBCUtils.insertSqlBatch("insert into position_report values (?,?,?,?)", arr)
    })
  }

  /**
    * 基本数据导入
    *
    * @param firstRDD
    */
  def basePositionAna(firstRDD: RDD[String]) = {
    firstRDD.map(line => { //positionName,companyName,salary,study,workyear,peopleNum,location,updatetime
      val splited = line.split(positionBean.getSplitSeparator)
      (splited(0), splited(1), splited(2), splited(3), splited(4), splited(5), splited(6), splited(7), DateUtils.formatDate)
    }).foreachPartition(f => {
      val arr = new ArrayBuffer[Object]()
      f.foreach(res => {
        val obj = Array(res._1, res._2, res._3, res._4, res._5, res._6, res._7, res._8, res._9)
        arr += obj
      })
      //插入数据库
      JDBCUtils.insertSqlBatch("insert into position_base values (?,?,?,?,?,?,?,?,?)", arr)
    })
  }


  /**
    * 工作经验统计
    *
    * @param firstRDD
    */
  def workyearPositionAnalysis(firstRDD: RDD[String]) = {
    firstRDD.map(line => {
      //positionName,companyName,salary,study,workyear,peopleNum,location,updatetime
      val splited = line.split(positionBean.getSplitSeparator)
      val work = if (splited(4).equals("无经验")) "不限" else splited(4)
      (work, 1)
    }).reduceByKey(_ + _).map(m => ("workyear", m._1, m._2, DateUtils.formatDate)).foreachPartition(f => {
      val arr = new ArrayBuffer[Object]()
      f.foreach(res => {
        val obj = Array(res._1, res._2, res._3, res._4)
        arr += obj
      })
      //插入数据库
      JDBCUtils.insertSqlBatch("insert into position_report values (?,?,?,?)", arr)
    })
  }

  /**
    * 学历范围统计
    *
    * @param firstRDD
    */
  def studyPositionAnalysis(firstRDD: RDD[String]) = {
    firstRDD.map(line => {
      //positionName,companyName,salary,study,workyear,peopleNum,location,updatetime
      val splited = line.split(positionBean.getSplitSeparator)
      (splited(3), 1)
    }).reduceByKey(_ + _).map(m => ("study", m._1, m._2, DateUtils.formatDate)).foreachPartition(f => {
      val arr = new ArrayBuffer[Object]()
      f.foreach(res => {
        val obj = Array(res._1, res._2, res._3, res._4)
        arr += obj
      })
      //插入数据库
      JDBCUtils.insertSqlBatch("insert into position_report values (?,?,?,?)", arr)
    })
  }

  /**
    * 计算薪酬范围 统计
    *
    * @param firstRDD
    */
  def salaryPositionAnalysis(firstRDD: RDD[String]) = {
    firstRDD.map(line => {
      //positionName,companyName,salary,study,workyear,peopleNum,location,updatetime
      val splited = line.split(positionBean.getSplitSeparator)
      (splited(2), 1)
    }).reduceByKey(_ + _).map(m => ("salary", m._1, m._2, DateUtils.formatDate)).foreachPartition(f => {
      val arr = new ArrayBuffer[Object]()
      f.foreach(res => {
        val obj = Array(res._1, res._2, res._3, res._4)
        arr += obj
      })
      //插入数据库
      JDBCUtils.insertSqlBatch("insert into position_report values (?,?,?,?)", arr)
    })
  }

  /**
    * 工作省份分析
    */

  def provincePositionAnalysis(firstRDD: RDD[String]) = {
    val res1: RDD[(String, Int)] = firstRDD.flatMap(line => {
      //positionName,companyName,salary,study,workyear,peopleNum,location,updatetime
      val splited = line.split(positionBean.getSplitSeparator)
      val location = splited(6)
      val manyLocation = location.split(",")
      if (manyLocation.size > 0) {
        val tuples: Array[(String, Int)] = manyLocation.map(loc => {
          val provinceName = ProvinceTools.processProvince(loc)
          Thread.sleep(250)
          val code = compact(parse(provinceName) \ "error_code")
          if (code.toInt != 0) {
            (loc,1)
          } else {
            val province = compact(parse(compact(parse(provinceName) \ "data" )) \ "province")
            //province.substring(1,province.length-1)
            (province.substring(1,province.length-2) , 1)
          }
        })
        tuples
      } else {
        val arr = new ArrayBuffer[(String, Int)]()
        val provinceName = ProvinceTools.processProvince(location)
        Thread.sleep(250)
        val code = compact(parse(provinceName) \ "error_code")
        if (code.toInt != 0) {
          arr += (location -> 1)
        } else {
          val province = compact(parse(compact(parse(provinceName) \ "data" )) \ "province")
          arr += (province.substring(1,province.length-2) -> 1)
        }
      }
    })
    res1.reduceByKey(_ + _).map(m => (m._1, m._2, DateUtils.formatDate)) foreachPartition (f => {
      val arr = new ArrayBuffer[Object]()
      f.foreach(res => {
        val obj = Array(res._1, res._2, res._3)
        arr += obj
      })
      // 插入数据库
      JDBCUtils.insertSqlBatch("insert into location_province values (?,?,?)", arr)
    })
  }

  /**
    * 计算工作地点
    *
    * @param firstRDD
    */
  def locationPositionAnalysis(firstRDD: RDD[String]): Unit = {
    val res1: RDD[(String, Int)] = firstRDD.flatMap(line => {
      //positionName,companyName,salary,study,workyear,peopleNum,location,updatetime
      val splited = line.split(positionBean.getSplitSeparator)
      val location = splited(6)
      val manyLocation = location.split(",")
      if (manyLocation.size > 0) {
        val tuples: Array[(String, Int)] = manyLocation.map(loc => {
          (loc, 1)
        })
        tuples
      } else {
        val arr = new ArrayBuffer[(String, Int)]()
        arr += (location -> 1)
      }
    })
    res1.reduceByKey(_ + _).map(m => ("location", m._1, m._2, DateUtils.formatDate)) foreachPartition (f => {
      val arr = new ArrayBuffer[Object]()
      f.foreach(res => {
        val obj = Array(res._1, res._2, res._3, res._4)
        arr += obj
      })
      // 插入数据库
      JDBCUtils.insertSqlBatch("insert into position_report values (?,?,?,?)", arr)
    })


  }

}
