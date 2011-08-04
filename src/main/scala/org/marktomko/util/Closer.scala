package org.marktomko.util

object Closer {
  /**
   * Calls <code>close()</code> each of the closeable resources provided, in
   * order.
   * @param resources The resources to close
   * @return A list of exceptions caught in the process
   */
  def tryClose(resources: List[Closeable]): List[(Closeable, Throwable)] = {
    resources match {
      case Nil => List()
      case r :: xr => {
        try {
          r.close()
          tryClose(xr)
        } catch {
          case e => (r, e) :: tryClose(xr)
        }
      }
    }
  }
}