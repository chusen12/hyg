package it.chusen.hyg.tools

import scala.xml.XML

/**
  * @Auther: 楚森
  * @Description:
  * @Company: 枣庄学院
  * @Date: 2018/7/29 12:45
  * @Version: 1.0
  */
object ProcessProperty {
  val classBean = getClassBean
  val positionBean = getPosition
  val dbBean = getdbBean


  def getClassBean: Basebean = processFile("log-class")

  def getPosition: Basebean = processFile("log-position")

  /**
    * 解析 DB 配置
    *
    * @return
    */
  def getdbBean = {
    //    val jarFilePath = ProcessProperty.getClass.getProtectionDomain.getCodeSource.getLocation.getFile
    //    val DBConfFile = new File(jarFilePath, "../../jarFilePathconf/logconf.xml").getCanonicalPath
    //val xml = XML.load(ProcessProperty.getClass.getClassLoader.getResourceAsStream("logconf.xml"))
    val xml = XML.loadFile("E:\\scalaproject\\hyg\\src\\main\\resources\\logconf.xml")
    val db = (xml \ "dbSource" \ "db")
    val url = (db \ "url").text.toString.trim
    val driver = (db \ "driver").text.toString.trim
    val username = (db \ "username").text.toString.trim
    val password = (db \ "password").text.toString.trim
    val dbBean = new DbBean
    dbBean.setDriver(driver)
    dbBean.setUrl(url)
    dbBean.setUsername(username)
    dbBean.setPassword(password)
    dbBean
  }

  def processFile(logName: String) = {
//    logconf.xml E:\scalaproject\hyg\src\main\resources\logconf.xml
    //    val jarFilePath = ProcessProperty.getClass.getProtectionDomain.getCodeSource.getLocation.getFile
    //    val DBConfFile = new File(jarFilePath, "../../conf/logconf.xml").getCanonicalPath
//    val xml = XML.load(ProcessProperty.getClass.getClassLoader.getResourceAsStream("logconf.xml"))
    val xml = XML.loadFile("E:\\scalaproject\\hyg\\src\\main\\resources\\logconf.xml")

    //    val xml = XML.loadFile(DBConfFile)
    //加载配置文件 部署生成环境时 需更改 配置文件位置
    val log_class = (xml \ "logProperties" \ logName)
    val sparkAppName = (log_class \ "sparkAppName").text.toString.trim
    val source = (log_class \ "source").text.toString.trim
    val splitSeparator = (log_class \ "splitSeparator").text.toString.trim
    val items = (log_class \ "items").text.toString.trim
    val itemsDescribe = (log_class \ "itemsDescribe").text.toString
    val baseBean = new Basebean
    baseBean.setSparkAppName(sparkAppName)
    baseBean.setSource(source)
    baseBean.setSplitSeparator(splitSeparator)
    baseBean.setItems(items)
    baseBean.setItemsDescribe(itemsDescribe)
    baseBean
  }

  def print_property = {
    println("==============DB 配置一览表==================")
    println("MYSQL URL:" + dbBean.getUrl)
    println("MYSQL DRIVER:" + dbBean.getDriver)
    println("MYSQL USER:" + dbBean.getUsername)
  }
}
