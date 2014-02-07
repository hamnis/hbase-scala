package hbase

import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.{HBaseAdmin,HTable}

object Main {
  implicit val config = HBaseConfiguration.create()

  def main(args: Array[String]) {
    args match {
      case Array("list") => {
        borrow(new HBaseAdmin(config)) { admin =>
          val tables = admin.listTables()
          tables.foreach(t => println(t.getNameAsString))
        }
      }
      case Array("get", tableName, key) => {
        borrow(Table(tableName)) { table =>
          val result = table.get[String](key)
          println(result.flatMap(_.getValue[String]).getOrElse("Value for %s not found".format(key)))
        }
      }
      case _ => {
        println("Command missing or unknown")
        sys.exit(1)
      }
    }
  }

  private def borrow[A <: java.io.Closeable](obj: A)(f: A => Unit) {
    try {
      f(obj)
    } finally {
      obj.close()
    }
  }
  
}
