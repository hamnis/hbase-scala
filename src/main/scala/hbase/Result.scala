package hbase

import org.apache.hadoop.hbase.client.{Result => HResult, ResultScanner}
import Bytes._

trait Result {
  def underlying: HResult

  def getRow[K](implicit keyC: Bytes[K]) = keyC.fromBytes(underlying.getRow)
  
  def getValue[F, C](family: F, column: C)(implicit familyC: Bytes[F], columnC: Bytes[C]): Option[Value] = {
    val res = underlying.getValue(familyC.toBytes(family), columnC.toBytes(column))
    Option(res).map(Value(_))
  }

  def getValueAs[V](implicit valueC: Bytes[V]): Option[V] = getValue.map(_.as[V])

  def getValue: Option[Value] = Option(underlying.value()).map(Value(_))

  override def toString = underlying.toString
}

object Result extends (HResult => Result){
  def apply(result: HResult): Result = new Result {
    val underlying = result
  }
}

/**
* ResultIterables should be closed.
*/
class ResultIterable(rs: ResultScanner) extends Iterable[Result] with java.io.Closeable {
  def iterator = new Iterator[Result]{
    val underlying = rs.iterator()
    def hasNext = underlying.hasNext
    def next = Result(underlying.next())
  }
  
  def close = rs.close()  
  
}
