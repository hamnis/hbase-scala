package hbase

sealed trait Input

case class QualifiedValue(coords: Coordinates, value: Value) extends Input {
  override def toString = {
    "%s, value: %s".format(coords.toString, value.toString)
  }
}

case class Increment(coords: Coordinates, amount: Long = 1L) extends Input {
  override def toString = {
    "%s, amount: %s".format(coords.toString, amount)
  }
}
