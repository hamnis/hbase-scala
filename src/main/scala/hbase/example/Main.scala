package hbase

import org.apache.hadoop.hbase.client.{HBaseAdmin}

object Main {
  implicit val config = Config.create().toConfiguration()

  def main(args: Array[String]) {
    args match {
      case Array("list") => {
        hbase.borrow(new HBaseAdmin(config)) { admin =>
          val tables = admin.listTables()
          tables.foreach(t => println(t.getNameAsString))
        }
      }
      case Array("get", tableName, key) => {
        hbase.borrow(Table(tableName)) { table =>
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
}
