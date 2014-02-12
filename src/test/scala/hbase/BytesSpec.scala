package hbase

import org.scalatest._

class BytesSpec extends UnitSpec {
  "A Bytes typeclass" should "serialize and deserialize strings correctly" in {
    val expected = "input string is this"
    val typeclass = implicitly[Bytes[String]]
    typeclass.fromBytes(typeclass.toBytes(expected)) should be (expected)
  }
  
  it should "serialize and deserialize byte arrays correctly" in {
    val expected = "input string is this".getBytes("UTF-8")
    val typeclass = implicitly[Bytes[Array[Byte]]]
    typeclass.fromBytes(typeclass.toBytes(expected)) should be (expected) 
  }
  
  it should "serialize and deserialize ints correctly" in {
    val expected = 23
    val typeclass = implicitly[Bytes[Int]]
    typeclass.fromBytes(typeclass.toBytes(expected)) should be (expected) 
  }
  
  it should "serialize and deserialize longs correctly" in {
    val expected = 23L
    val typeclass = implicitly[Bytes[Long]]
    typeclass.fromBytes(typeclass.toBytes(expected)) should be (expected) 
  }
  
  it should "serialize and deserialize double correctly" in {
    val expected = 23.5
    val typeclass = implicitly[Bytes[Double]]
    typeclass.fromBytes(typeclass.toBytes(expected)) should be (expected) 
  }

  it should "serialize and deserialize boolean correctly" in {
    val expected = true
    val typeclass = implicitly[Bytes[Boolean]]
    typeclass.fromBytes(typeclass.toBytes(expected)) should be (expected) 
  }
}
