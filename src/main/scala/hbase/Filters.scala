package hbase

import org.apache.hadoop.hbase.filter._
import org.apache.hadoop.hbase.util.Pair
import CompareFilter.CompareOp
import collection.JavaConverters._


object Filters {
  object Row {
    def prefix[A](value: A)(implicit conv: Bytes[A]) = new PrefixFilter(conv.toBytes(value))
    def firstKeyOnly = new FirstKeyOnlyFilter()
    def page(size: Long) = new PageFilter(size)
    def fuzzy(pairs: (Array[Byte], Array[Byte])*) = {
      new FuzzyRowFilter(new java.util.ArrayList(pairs.map{case (k,v) => new Pair(k, v)}.asJava))
    }
    def random(chance: Float) = new RandomRowFilter(chance)
    def compare(op: CompareOp, comp: WritableByteArrayComparable) = new RowFilter(op, comp)
  }

  object Column {
    def paginate(start: Int, offset: Int) = new ColumnPaginationFilter(start, offset)
    def prefix[A](value: A)(implicit conv: Bytes[A]) = new ColumnPrefixFilter(conv.toBytes(value))
    def mulitiplePrefix[A](value: List[A])(implicit conv: Bytes[A]) = new MultipleColumnPrefixFilter(value.map(conv.toBytes(_)).toArray)
    def single(input: QualifiedValue, op: CompareOp) = {
      val Coordinates(family, Some(column)) = input.coords
      new SingleColumnValueFilter(family, column, op, input.value._bytes)
    }
    def single(coords: Coordinates, op: CompareOp, comp: WritableByteArrayComparable) = {
      val Coordinates(family, Some(column)) = coords
      new SingleColumnValueFilter(family, column, op, comp)
    }
    def range[A, B](min: A, max: B, minInclusive: Boolean, maxInclusive: Boolean)(implicit convA: Bytes[A], convB: Bytes[B]) = new ColumnRangeFilter(convA.toBytes(min), minInclusive, convB.toBytes(max), maxInclusive)

    def limit(n: Int) = new ColumnCountGetFilter(n)

    def value(op: CompareOp, comp: WritableByteArrayComparable) = new ValueFilter(op, comp)

  }
  def key = new KeyOnlyFilter()
  def list(filters: Filter*) = new FilterList(filters : _*)
  def list(op: FilterList.Operator, filters: Filter*) = new FilterList(op, filters : _*)
  def skip(filter: Filter) = new SkipFilter(filter)
  def whileMatch(filter: Filter) = new WhileMatchFilter(filter)

  
  
}
