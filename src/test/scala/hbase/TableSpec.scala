package hbase

import org.scalatest.mock.MockitoSugar
import Bytes._
import org.apache.hadoop.hbase.KeyValue
import org.apache.hadoop.hbase.client.{HTableInterface, Get, Result => HResult}
import org.mockito.Mockito._
import org.mockito.Matchers._

class TableSpec extends UnitSpec with MockitoSugar{
  "A table" should "get from key 'Hello'" in {
    val htable = mock[HTableInterface]
    val table = Table(htable)

    val row = "Hello"
    val hresult = new HResult(Array(new KeyValue(StringBytes.toBytes(row), StringBytes.toBytes("Family"), StringBytes.toBytes("Column"), null)))

    when(htable.get(any[Get])).thenReturn(hresult)    
    val result = table.get(row)
    verify(htable, times(1)).get(any[Get])
    result should not be empty
    assert(result.get.getRow[String] === row)
    result.get.getValueAs[String] shouldBe empty
    result.get.getValue("Family", "Column") shouldBe empty
  }
}
