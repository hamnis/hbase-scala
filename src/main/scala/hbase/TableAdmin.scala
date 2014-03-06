package hbase

import scala.util.Try
import org.apache.hadoop.hbase.client.HBaseAdmin
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.{HTableDescriptor, HColumnDescriptor}
import Bytes._

trait TableAdmin extends java.io.Closeable {
  def underlying: HBaseAdmin

  def close = underlying.close

  def list(): List[String] = 
    Try { underlying.getTableNames().toList }.getOrElse(Nil)
  
  def create(name: String, columnFamiles: String*)(implicit config: Configuration): Try[Unit] = 
    Try {
      if (columnFamiles.isEmpty) sys.error("You must specify at least one column family")
      val descriptor = new HTableDescriptor(name)
      columnFamiles.foreach{f => 
        descriptor.addFamily(new HColumnDescriptor(f))
      }
      descriptor
    }.map(create(_))
  

  def create(descriptor: HTableDescriptor)(implicit config: Configuration): Try[Unit] = 
    Try { underlying.createTable(descriptor) }

  def exists(name: String)(implicit config: Configuration):Boolean = 
    Try { underlying.tableExists(name) }.getOrElse(false)

  def compact(name: String): Try[Unit] = Try { underlying.compact(name) }
  
  def compact[F](name: String, family: F)(implicit familyC: Bytes[F]): Try[Unit] = 
    Try { underlying.compact(StringBytes.toBytes(name), familyC.toBytes(family)) }
  
  def majorCompact(name: String): Try[Unit] = Try { underlying.majorCompact(name) }

  def majorCompact[F](name: String, family: F)(implicit familyC: Bytes[F]): Try[Unit] = 
    Try { underlying.majorCompact(StringBytes.toBytes(name), familyC.toBytes(family)) }
}

object TableAdmin {
  def apply(implicit config: Configuration): TableAdmin = apply(new HBaseAdmin(config))
  
  def apply(admin: HBaseAdmin): TableAdmin = new TableAdmin {
    val underlying = admin
  }

  def execute[A](admin: TableAdmin)(block: TableAdmin => A): A = borrow(admin)(block)
  def execute[A](block: TableAdmin => A)(implicit config: Configuration): A = borrow(apply(config))(block)
}
