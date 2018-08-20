package it.chusen.hyg.tools

import scala.beans.BeanProperty

/**
  * @Auther: 楚森
  * @Description:
  * @Company: 枣庄学院
  * @Date: 2018/7/29 12:45
  * @Version: 1.0
  */
class Basebean extends Serializable {

  @BeanProperty var sparkAppName = ""
  @BeanProperty var source = ""
  @BeanProperty var splitSeparator = ""
  @BeanProperty var items = ""
  @BeanProperty var loglength = 0
  @BeanProperty var itemsDescribe = ""
}
