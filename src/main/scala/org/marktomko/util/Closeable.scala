package org.marktomko.util

/**
 * Defines a <code>close()</code> method, allowing implementing objects to
 * be closed by a common method.
 * 
 * @author Mark Tomko, (c) 2011
 */
trait Closeable {
  /**
   * Closes the object, releasing any resources allocated to it; may
   * throw exceptions, depending on the implementation.
   */
  def close()
}