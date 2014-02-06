package hbase

case class QualifiedValue private[hbase] (_family: Array[Byte], _column: Array[Byte], _value: Array[Byte])

object QualifiedValue {
  def apply[F, C, V](family: F, column: C, value: V)(implicit familyC: Bytes[F], columnC: Bytes[C], valueC: Bytes[V]): QualifiedValue = new QualifiedValue(familyC.toBytes(family), columnC.toBytes(column), valueC.toBytes(value))
  
}

case class Increment private[hbase] (_family: Array[Byte], _column: Array[Byte], _amount: Long)

object Increment {
  def apply[F, C](family: F, column: C)(implicit familyC: Bytes[F], columnC: Bytes[C]): Increment = 
    apply(family, column, 1L)

  def apply[F, C](family: F, column: C, amount: Long)(implicit familyC: Bytes[F], columnC: Bytes[C]): Increment = 
    new Increment(familyC.toBytes(family), columnC.toBytes(column), amount)
  
}
