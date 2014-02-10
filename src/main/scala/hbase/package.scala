package object hbase {
  def borrow[A <: java.io.Closeable, B](obj: A)(f: A => B): B = {
    try {
      f(obj)
    } finally {
      obj.close()
    }
  }
}
