package hbase

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.HConstants._
import Config._
import scala.concurrent.duration._

case class Config private[hbase](props: Map[String, String] = Map.empty) {
  def put[A](key: Key[A], value: A)(implicit conv: ValueConverter[A]) = copy(props.updated(key.name, conv.to(value)))
  def get[A](key: Key[A])(implicit conv: ValueConverter[A]): Option[A] = props.get(key.name).map(conv.from(_))

  def toConfiguration(): Configuration = {
    val conf = HBaseConfiguration.create()
    props.foreach{case (k, v) => conf.set(k, v)}
    conf
  }
}

object Config {
  def create() = apply()

  sealed abstract class Key[A](val name: String)
  
  object ZookeeperQuorum extends Key[List[String]](ZOOKEEPER_QUORUM)
  object ZookeeperClientPort extends Key[Int](ZOOKEEPER_CLIENT_PORT)
  object ZookeeperTimeout extends Key[FiniteDuration](ZK_SESSION_TIMEOUT)
  object ZookeeperMaxConnection extends Key[Int](ZOOKEEPER_MAX_CLIENT_CNXNS)
  object ClientInstanceId extends Key[Int](HBASE_CLIENT_INSTANCE_ID)
  object ClientIpcPoolSize extends Key[Int](HBASE_CLIENT_IPC_POOL_SIZE)
  object ClientIpcPoolType extends Key[String](HBASE_CLIENT_IPC_POOL_TYPE)

/*
  TODO: 0.96 and higher.. Use string constants instead?

  object ClientMaxTasksPerRegion extends Key[Int](HBASE_CLIENT_MAX_PERREGION_TASKS)
  object ClientMaxTasksPerServer extends Key[Int](HBASE_CLIENT_MAX_PERSERVER_TASKS)
  object ClientMaxTasks extends Key[Int](HBASE_CLIENT_MAX_TOTAL_TASKS)
  object ClientMetaOperationTimeout extends Key[FiniteDuration](HBASE_CLIENT_META_OPERATION_TIMEOUT)
  object ClientScannerCaching extends Key[Int](HBASE_CLIENT_SCANNER_CACHING)
  object ClientScannerTimeout extends Key[FiniteDuration](HBASE_CLIENT_SCANNER_TIMEOUT_PERIOD)
*/

  object ClientOperationTimeout extends Key[FiniteDuration](HBASE_CLIENT_OPERATION_TIMEOUT)
  object ClientPause extends Key[Int](HBASE_CLIENT_PAUSE)
  object ClientPrefetchLimit extends Key[Int](HBASE_CLIENT_PREFETCH_LIMIT)
  object ClientRetriesNumber extends Key[Int](HBASE_CLIENT_RETRIES_NUMBER)
  object ClientScannerMaxResultSize extends Key[Int](HBASE_CLIENT_SCANNER_MAX_RESULT_SIZE_KEY)


  trait ValueConverter[A] {
    def to(input: A): String
    def from(string: String): A
  }

  object ValueConverter {
    implicit object StringValueConverter extends ValueConverter[String] {
      def to(input: String) = input
      def from(string: String) = string
    }
    
    implicit object IntValueConverter extends ValueConverter[Int] {
      def to(input: Int) = input.toString
      def from(string: String) = string.toInt
    }

    implicit object FiniteDurationValueConverter extends ValueConverter[FiniteDuration] {
      def to(input: FiniteDuration) = input.toMillis.toString
      def from(string: String) = new FiniteDuration(string.toLong, java.util.concurrent.TimeUnit.MILLISECONDS)
    }

    class ListValueConverter[A](conv: ValueConverter[A]) extends ValueConverter[List[A]] {
      def to(input: List[A]): String = input.map(conv.to(_)).mkString("",",","")
      def from(string: String) = string.split(",").map(conv.from(_)).toList
    }

    implicit object StringListValueConverter extends ListValueConverter[String](StringValueConverter)
  }
}


