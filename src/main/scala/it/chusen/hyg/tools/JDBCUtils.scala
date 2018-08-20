package it.chusen.hyg.tools

import java.sql.{Connection, DriverManager}

import org.slf4j.LoggerFactory

import scala.collection.mutable.ArrayBuffer

/**
  * @Auther: 楚森
  * @Description:
  * @Company: 枣庄学院
  * @Date: 2018/7/29 16:30
  * @Version: 1.0
  */
object JDBCUtils {


  val dbBean = ProcessProperty.getdbBean
  val logger= LoggerFactory.getLogger(JDBCUtils.getClass)

  /**
    * 获得一个数据库连接
    * @return
    */
  def getConnection:Connection = {
    var conn :Connection = null
    try {
      Class.forName(dbBean.getDriver)
      conn = DriverManager.getConnection(dbBean.getUrl,dbBean.getUsername,dbBean.getPassword)
    } catch {
        case ex:Exception => {
          logger.error("没有获得到数据库连接 200毫秒后继续获取..")
          Thread.sleep(200)
          conn = DriverManager.getConnection(dbBean.getUrl,dbBean.getUsername,dbBean.getPassword)
          ex.printStackTrace()
        }
    }
    conn
  }

  def insertSqlBatch(sql:String,data:ArrayBuffer[Object]): Unit = {
    val conn = getConnection
    val pre = conn.prepareStatement(sql)
    try {
      conn.setAutoCommit(false)
      if (data != null && data.length >0) {
        data.foreach(objs => {
          val arr = objs.asInstanceOf[Array[Object]]
          for (i <- 0 until arr.length) {
            pre.setObject(i + 1,arr(i))
          }
          pre.addBatch()
        })
        pre.executeBatch()
      }
      conn.commit()
      pre.executeUpdate()
    } catch {
        case ex:Exception => ex.printStackTrace()
    } finally {
      if (conn != null) conn.close()
      if (pre != null) pre.close()
    }
  }
}
