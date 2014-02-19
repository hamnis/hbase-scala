package hbase

import Bytes._

class InputSpec extends UnitSpec {
  "an Input" should "create QualifiedValues from supported types" in {
    val expected = QualifiedValue(Coordinates(StringBytes.toBytes("family"), Some(StringBytes.toBytes("column"))), Value(IntBytes.toBytes(20)))

    expected === QualifiedValue(Coordinates("family", "column"), Value(20))
  }
  it should "create Increments from supported types" in {
    val expected = Increment(Coordinates(StringBytes.toBytes("family"), None), 1L)
    expected === Increment(Coordinates("family"), 1L)
    val expected2 = Increment(Coordinates(LongBytes.toBytes(378294L), None), 42L)
    expected2 === Increment(Coordinates(378294L), 42L)
  }

}
