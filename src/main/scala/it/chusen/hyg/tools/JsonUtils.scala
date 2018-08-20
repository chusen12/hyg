package it.chusen.hyg.tools
import org.json4s.DefaultFormats
import org.json4s.JsonAST.{JString, JValue}
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
/**
  * @Auther: 楚森
  * @Description:
  * @Company: 枣庄学院
  * @Date: 2018/7/31 23:44
  * @Version: 1.0
  */
object JsonUtils {
  /**
    * jsonutil
    * @param args
    */
  def main(args: Array[String]): Unit = {
//    val jsonStr = ProvinceTools.processProvince("青岛市")
    val jsonstr = "{\"error_code\":0,\"data\":{\"province\":\"江苏省\",\"provinceShort\":\"江苏\",\"city\":\"南京市\"},\"reason\":\"success\"}"
    val provinceName = compact(parse(compact(parse(jsonstr) \ "data" )) \ "province")
    val province = provinceName.substring(1,provinceName.length-2)
    println (province)
  }

}
