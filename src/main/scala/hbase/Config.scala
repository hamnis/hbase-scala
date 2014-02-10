package hbase

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.HConstants._
import Config._

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
  object ZookeeperTimeout extends Key[Int](ZK_SESSION_TIMEOUT)
  object ZookeeperMaxConnection extends Key[Int](ZOOKEEPER_MAX_CLIENT_CNXNS)


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

    class ListValueConverter[A](conv: ValueConverter[A]) extends ValueConverter[List[A]] {
      def to(input: List[A]): String = input.map(conv.to(_)).mkString("",",","")
      def from(string: String) = string.split(",").map(conv.from(_)).toList
    }

    implicit object StringListValueConverter extends ListValueConverter[String](StringValueConverter)
  }
}


