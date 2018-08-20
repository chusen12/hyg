package it.chusen.hyg.tools

import scala.beans.BeanProperty

/**
  * @Auther: 楚森
  * @Description:
  * @Company: 枣庄学院
  * @Date: 2018/7/29 12:45
  * @Version: 1.0
  */
class DbBean extends Serializable {
  @BeanProperty var url = ""
  @BeanProperty var driver = ""
  @BeanProperty var username = ""
  @BeanProperty var password = ""

}
