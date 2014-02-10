package hbase

sealed trait Input

case class QualifiedValue private[hbase] (_family: Array[Byte], _column: Array[Byte], _value: Array[Byte]) extends Input {
  override def toString = {
    import Bytes.{ StringBytes => SB}
    "family: %s, column: %s, value: %s".format(SB.fromBytes(_family), SB.fromBytes(_column), SB.fromBytes(_value))
  }
}

object QualifiedValue {
  def apply[F, C, V](family: F, column: C, value: V)(implicit familyC: Bytes[F], columnC: Bytes[C], valueC: Bytes[V]): QualifiedValue = new QualifiedValue(familyC.toBytes(family), columnC.toBytes(column), valueC.toBytes(value))
  
}

case class Increment private[hbase] (_family: Array[Byte], _column: Array[Byte], _amount: Long) extends Input {
  override def toString = {
    import Bytes.{ StringBytes => SB}
    "family: %s, column: %s, amount: %s".format(SB.fromBytes(_family), SB.fromBytes(_column), _amount)
  }
}

object Increment {
  def apply[F, C](family: F, column: C)(implicit familyC: Bytes[F], columnC: Bytes[C]): Increment = 
    apply(family, column, 1L)

  def apply[F, C](family: F, column: C, amount: Long)(implicit familyC: Bytes[F], columnC: Bytes[C]): Increment = 
    new Increment(familyC.toBytes(family), columnC.toBytes(column), amount)
  
}
