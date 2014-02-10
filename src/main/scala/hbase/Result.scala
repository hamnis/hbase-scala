package hbase

import org.apache.hadoop.hbase.client.{Result => HResult, ResultScanner}

trait Result {
  def underlying: HResult
  
  def getValue[F, C, V](family: F, column: C)(implicit familyC: Bytes[F], columnC: Bytes[C], valueC: Bytes[V]): Option[V] = {
    val res = underlying.getValue(familyC.toBytes(family), columnC.toBytes(column))
    Option(res).map(valueC.fromBytes(_))
  }

  def getValue[V](implicit valueC: Bytes[V]): Option[V] = 
    Option(underlying.value()).map(valueC.fromBytes(_))

  override def toString = underlying.toString
}

object Result {
  def apply(result: HResult): Result = new Result {
    val underlying = result
  }
}

class ResultIterable(rs: ResultScanner) extends Iterable[Result] with java.io.Closeable {
  def iterator = new Iterator[Result]{
    val underlying = rs.iterator()
    def hasNext = underlying.hasNext
    def next = Result(underlying.next())
  }
  
  def close = rs.close()  
  
}
