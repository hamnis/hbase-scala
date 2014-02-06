package hbase

import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.{HBaseAdmin,HTable}

object Main {
  implicit val config = HBaseConfiguration.create()

  def main(args: Array[String]) {
    val admin = new HBaseAdmin(config)
    val tables = admin.listTables()
    tables.foreach(println)
  
    val table = Table("shorturl")
    val result = table.get[String]("http://atmlb.com/7NG4sm")
    println(result)
  }
  
}
