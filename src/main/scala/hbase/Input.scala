package hbase

sealed trait Input

case class Coordinates private[hbase](_family: Array[Byte], _column: Option[Array[Byte]] = None) {
  def family[F](implicit conv: Bytes[F]) = conv.fromBytes(_family)
  def column[C](implicit conv: Bytes[C]) = _column.map(conv.fromBytes(_))

  override def toString = {
    "family: %s, column: %s".format(family[String], column[String])
  }
}

case class QualifiedValue(coords: Coordinates, value: Value) extends Input {
  override def toString = {
    "%s, value: %s".format(coords.toString, value.toString)
  }
}

case class Increment(coords: Coordinates, amount: Long) extends Input {
  override def toString = {
    "%s, amount: %s".format(coords.toString, amount)
  }
}
