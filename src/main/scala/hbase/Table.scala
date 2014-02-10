package hbase

import org.apache.hadoop.hbase.client.{HTable, Put, Get, Increment => HIncrement}
import Bytes._

trait Table extends java.io.Closeable {
  def underlying: HTable

  def close() = underlying.close()

  def name: String = StringBytes.fromBytes(underlying.getTableName)
  
  def get[K](key: K)(implicit keyC: Bytes[K]): Option[Result] = 
    Option(underlying.get(new Get(keyC.toBytes(key)))).map(r => Result(r))

  def put[K](key: K, input: QualifiedValue*)(implicit keyC: Bytes[K]): Unit = putSeq(key, input.toList)

  def putSeq[K](key: K, input: Seq[QualifiedValue])(implicit keyC: Bytes[K]): Unit = {
    val query = new Put(keyC.toBytes(key))
    input.foreach{ in =>
      val QualifiedValue(f, c, v) = in
      query.add(f, c, v)
    }
    underlying.put(query)
    ()
  }

  def inc[K](key: K, input: Increment*)(implicit keyC: Bytes[K]): Unit = incSeq(key, input.toList)

  def incSeq[K](key: K, input: Seq[Increment])(implicit keyC: Bytes[K]): Unit = {
    val query = new HIncrement(keyC.toBytes(key))
    input.foreach{ in =>
      val Increment(f, c, i) = in
      query.addColumn(f, c, i)
    }
    underlying.increment(query)
    ()
  }

  def flush() = underlying.flushCommits()
}

object Table {
  import org.apache.hadoop.conf.Configuration

  def apply(name: String)(implicit config: Configuration): Table = {
    apply(new HTable(config, name))
  }

  def apply(table: HTable): Table = new Table {
    val underlying = table
  }

  /**
  * Execute the block of code in context of the table.
  * The table will be closed after the block has run.
  **/
  def execute[A](table: Table)(block: Table => A): A = {
    try {
      block(table)
    } finally {
      table.close()
    }
  }
}
