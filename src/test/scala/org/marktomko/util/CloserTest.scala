package org.marktomko.util

import org.junit.Test
import org.junit.Assert.assertEquals

class CloseableStub(val e: Exception) extends Closeable {
  override def close() {
    if (e != null) {
      throw e
    }
  }
}

@Test
class CloserTest {
  @Test
  def testTryCloseEmptyList() {
    assertEquals(List(), Closer.tryClose(List()))
  }
  
  @Test
  def testTryCloseNoThrow() {
    assertEquals(List(), Closer.tryClose (List(new CloseableStub(null))))
  }
  
  @Test
  def testTryCloseOneThrow() {
    val exception = new RuntimeException("foo")
    val resource = new CloseableStub(exception)
    val expected: List[(Closeable, Throwable)] = List((resource, exception))
    assertEquals(expected, Closer.tryClose(List(resource)))
  }
  
  @Test
  def testTryCloseMultipleThrow() {
    val exception1 = new RuntimeException("foo")
    val resource1 = new CloseableStub(exception1)
    
    val exception2 = new RuntimeException("foo2")
    val resource2 = new CloseableStub(exception2)
    
    val expected: List[(Closeable, Throwable)] = List((resource1, exception1), (resource2, exception2))
    assertEquals(expected, Closer.tryClose(List(resource1, resource2)))
  }
  
    @Test
  def testTryCloseMixedThrow() {
    val resource1 = new CloseableStub(null)
    
    val exception2 = new RuntimeException("foo2")
    val resource2 = new CloseableStub(exception2)
    
    val expected: List[(Closeable, Throwable)] = List((resource2, exception2))
    assertEquals(expected, Closer.tryClose(List(resource1, resource2)))
  }
}