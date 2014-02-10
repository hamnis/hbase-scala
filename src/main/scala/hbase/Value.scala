package hbase

import Bytes._

case class Value private[hbase] (_bytes: Array[Byte]) {
  def as[A](implicit valueC: Bytes[A]) = valueC.fromBytes(_bytes)
  override def toString = as[String](implicitly[Bytes[String]])
}

object Value {
  def apply[A](value: A)(implicit valueC: Bytes[A]): Value = Value(valueC.toBytes(value))
}
