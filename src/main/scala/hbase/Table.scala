package hbase

import util.Try
import collection.JavaConverters._
import org.apache.hadoop.hbase.client.{HTableInterface, HTable, Put, Get, Scan, Increment => HIncrement}
import org.apache.hadoop.hbase.filter.Filter
import Bytes._


trait Table extends java.io.Closeable {
  def underlying: HTableInterface

  def name: String = StringBytes.fromBytes(underlying.getTableName)

  def exists[K](key: K, coords: Coordinates)(implicit keyC: Bytes[K]): Boolean = {
    val query = createGet(key, Some(coords))
    Try{ underlying.exists(query) }.getOrElse(false)
  }
  
  def get[K](key: K)(implicit keyC: Bytes[K]): Option[Result] = {
    val query = createGet(key, None)
    Try{ underlying.get(query) }.map(Result).toOption
  }

  def get[K](key: K, coords: Coordinates)(implicit keyC: Bytes[K]): Option[Result] = {
    val query = createGet(key, Some(coords))
    Try{ underlying.get(query) }.map(Result).toOption
  }

  def getSeq[K](keys: Seq[K])(implicit keyC: Bytes[K]): IndexedSeq[Result] = 
    underlying.get(keys.map(key => new Get(keyC.toBytes(key))).asJava).map(Result).toVector

  def put[K](key: K, input: QualifiedValue*)(implicit keyC: Bytes[K]): Unit = putSeq(key, input.toList)

  def putSeq[K](key: K, input: Seq[QualifiedValue])(implicit keyC: Bytes[K]): Unit = {
    val query = new Put(keyC.toBytes(key))
    input.foreach{ in =>
      val QualifiedValue(Coordinates(f, Some(c)), Value(v)) = in
      query.add(f, c, v)
    }
    underlying.put(query)
    ()
  }

  def inc[K](key: K, input: Increment*)(implicit keyC: Bytes[K]): Unit = incSeq(key, input.toList)

  def incSeq[K](key: K, input: Seq[Increment])(implicit keyC: Bytes[K]): Unit = {
    val query = new HIncrement(keyC.toBytes(key))
    input.foreach{ in =>
      val Increment(Coordinates(f, Some(c)), i) = in
      query.addColumn(f, c, i)
    }
    underlying.increment(query)
    ()
  }

  def scan[A](coords: Coordinates, filter: Option[Filter] = None)(f: ResultIterable => IndexedSeq[A]): IndexedSeq[A] = {
    val res = scan{ s =>
      coords._column match {
        case Some(column) => s.addColumn(coords._family, column)
        case None => s.addFamily(coords._family)
      }
      s.setFilter(filter.orNull)
      s
    }
    borrow(res)(f)
  }

  def scan(scan: Scan => Scan): ResultIterable = {
    val s = scan(new Scan())
    val scanner = underlying.getScanner(s)
    new ResultIterable(scanner)
  }

  def flush() = underlying.flushCommits()

  def close() = underlying.close()

  private def createGet[K](key: K, coords: Option[Coordinates])(implicit keyC: Bytes[K]): Get = {
    val query = new Get(keyC.toBytes(key))
    coords.foreach { c =>
      c._column match {
        case Some(column) => query.addColumn(c._family, column)
        case None => query.addFamily(c._family)
      }          
    }
    query
  }


}

object Table {
  import org.apache.hadoop.conf.Configuration

  def apply(name: String)(implicit config: Configuration): Table = {
    apply(new HTable(config, name))
  }

  def apply(table: HTableInterface): Table = new Table {
    val underlying = table
  }

  /**
  * Execute the block of code in context of the table.
  * The table will be closed after the block has run.
  **/
  def execute[A](table: Table)(block: Table => A): A = borrow(table)(block)
  
}
