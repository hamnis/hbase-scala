package hbase

trait Bytes[A] {
  def toBytes(input: A): Array[Byte]
  def fromBytes(bytes: Array[Byte]): A
}

object Bytes {
  import org.apache.hadoop.hbase.util.{Bytes => JBytes}

  implicit object ArrayBytes extends Bytes[Array[Byte]] {
    def toBytes(input: Array[Byte]) = input
    def fromBytes(input: Array[Byte]) = input
  }

  implicit object StringBytes extends Bytes[String] {
    def toBytes(input: String) = JBytes.toBytes(input)
    def fromBytes(input: Array[Byte]) = JBytes.toString(input)
  }

  implicit object BooleanBytes extends Bytes[Boolean] {
    def toBytes(input: Boolean) = JBytes.toBytes(input)
    def fromBytes(input: Array[Byte]) = JBytes.toBoolean(input)
  }
  
  implicit object IntBytes extends Bytes[Int] {
    def toBytes(input: Int) = JBytes.toBytes(input)
    def fromBytes(input: Array[Byte]) = JBytes.toInt(input)
  }
  
  implicit object LongBytes extends Bytes[Long] {
    def toBytes(input: Long) = JBytes.toBytes(input)
    def fromBytes(input: Array[Byte]) = JBytes.toLong(input)
  }

  implicit object DoubleBytes extends Bytes[Double] {
    def toBytes(input: Double) = JBytes.toBytes(input)
    def fromBytes(input: Array[Byte]) = JBytes.toDouble(input)
  }
}
