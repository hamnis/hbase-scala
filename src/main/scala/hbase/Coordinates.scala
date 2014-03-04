package hbase

case class Coordinates private[hbase](_family: Array[Byte], _column: Option[Array[Byte]]) {
  def family[F](implicit conv: Bytes[F]) = conv.fromBytes(_family)
  def column[C](implicit conv: Bytes[C]) = _column.map(conv.fromBytes(_))

  override def toString = {
    "family: %s, column: %s".format(family[String], column[String])
  }
}

object Coordinates {
  def apply[F](family: F)(implicit familyC: Bytes[F]): Coordinates = {
    new Coordinates(familyC.toBytes(family), None)
  }
  def apply[F, C](family: F, column: C)(implicit familyC: Bytes[F], columnC: Bytes[C]): Coordinates = {
    new Coordinates(familyC.toBytes(family), Some(columnC.toBytes(column)))
  }
}
